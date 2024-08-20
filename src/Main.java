import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Main {

    private static final BlockingQueue<String> QUEUE_A = new ArrayBlockingQueue<>(100);
    private static final BlockingQueue<String> QUEUE_B = new ArrayBlockingQueue<>(100);
    private static final BlockingQueue<String> QUEUE_C = new ArrayBlockingQueue<>(100);
    private static final int WORD_COUNT = 10_000;
    private static final int WORD_LENGTH = 100_000;


    public static void main(String[] args) throws InterruptedException {
        List<Thread> threads = new ArrayList<>();

        Thread generator = new Thread(() -> {
            for (int i = 0; i < WORD_COUNT; i++) {
                String text = generateText("abc", WORD_LENGTH);
                try {
                    QUEUE_A.put(text);
                    QUEUE_B.put(text);
                    QUEUE_C.put(text);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        generator.start();

        Thread threadA = new Thread(() -> findWordWithMaxSymbolCount(QUEUE_A, 'a'));
        Thread threadB = new Thread(() -> findWordWithMaxSymbolCount(QUEUE_B, 'b'));
        Thread threadC = new Thread(() -> findWordWithMaxSymbolCount(QUEUE_C, 'c'));

        threadA.start();
        threadB.start();
        threadC.start();
        threads.add(threadA);
        threads.add(threadB);
        threads.add(threadC);

        for (Thread thread : threads) {
            thread.join(); // зависаем, ждём когда поток объект которого лежит в thread завершится
        }
    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }

    public static void findWordWithMaxSymbolCount(BlockingQueue<String> queue, char symbol) {
        int maxCount = 0;
        String maxCountWord = "";
        for (int i = 0; i < WORD_COUNT; i++) {
            try {
                String word = queue.take();
                int currentCount = calculateCharCount(word, symbol);
                if (maxCount < currentCount) {
                    maxCountWord = word;
                    maxCount = currentCount;
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }
        System.out.println("Слово с максимальным количеством символов '" + symbol + "' - " + maxCountWord);
    }

    public static int calculateCharCount(String text, char symbol) {
        int counter = 0;
        for (char textChar : text.toCharArray()) {
            if (textChar == symbol) {
                counter++;
            }
        }
        return counter;
    }
}