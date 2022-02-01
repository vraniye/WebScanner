package WebScanner;



import java.util.HashSet;
import java.util.LinkedList;

/** Класс с именем URLPool, который будет хранить список
 всех URL-адресов для поиска, а также относительный "уровень" каждого из
 этих URL-адресов (также известный как "глубина поиска") */
public class URLPool {
    //Необработанные URL`s
    private LinkedList<URLDepthPair> untreatedURL;
    //Отсканированные URL`s
    private LinkedList<URLDepthPair> scannedURL;

    //поле типа int, которое будет увеличиваться непосредственно перед вызовом wait() и уменьшаться сразу после выхода из режима ожидания
    private int streamCounter = 0;

    private int depth;

    //Для отслеживания одинаковых ссылок
    private HashSet<String> unRepeatSet;

    //Конструктор
    public URLPool(int depth) {
        untreatedURL = new LinkedList<>();
        scannedURL = new LinkedList<>();
        unRepeatSet = new HashSet<>();
        this.depth = depth;
    }

    //Гетер
    public int getStreamCounter() {
        return streamCounter;
    }

    /**
     * Метод, для получения пары из пула и удаления этой пары из списка одновременно
     */
    public synchronized URLDepthPair get() {
        while (untreatedURL.size() == 0) {
            streamCounter++;
            try {
                //Поток в ожидание
                wait();
            } catch (InterruptedException e) {
                System.err.println("InterruptedException: " + e.getMessage());
            }
            streamCounter--;
        }
        return untreatedURL.remove(0);
    }

    /**
     * Метод, для добавления пары URL-глубина к пулу.
     */
    public synchronized void add(URLDepthPair pair) {
        String URL = pair.getURLAddress();
        if (!unRepeatSet.contains(URL)) {
            unRepeatSet.add(URL);
        } else {
            return;
        }
        if (pair.getSearchDepth() < depth) {
            untreatedURL.add(pair);
            //Поток из ожидания
            notify();
        }
        scannedURL.add(pair);
    }

    /**
     * Метод, для вывода всех найденных ссылок
     */
    public synchronized  void printAllLinks(){
        Object[] output = scannedURL.toArray();
        for (Object o : output) {
            System.out.println(o);
        }
    }

}
