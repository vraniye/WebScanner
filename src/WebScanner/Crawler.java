package WebScanner;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class Crawler {

    private URLPool pool;

    public Crawler (String site, int depth) throws MalformedURLException {
        pool = new URLPool(depth);
        URL url = new URL(site);
        pool.add(new URLDepthPair(url.toString(), 0));
    }

    public static void main(String[] args) throws InterruptedException, MalformedURLException {
        Scanner scanner = new Scanner(System.in);
        int depth = 0;
        int streams = 1;
        String url = null;

        try{
            System.out.println("Введите URL - адресс:");
            url = scanner.nextLine();
            System.out.println("Введите глубину поиска:");
            depth = scanner.nextInt();
            System.out.println("Введите количество потоков:");
            streams = scanner.nextInt();
        }catch (Exception e){
            System.err.println("Ошибка ввода данных: " + e.getMessage());
            System.exit(1);
        }
        Crawler crawler = new Crawler(url, depth);
        crawler.startThreads(streams);
        System.exit(0);
    }

    /** Метод для запуска потоков */
    public void startThreads (int streams) throws InterruptedException {
        for (int i = 0; i < streams; i++){
            CrawlerTask crawlerTask = new CrawlerTask(pool);
            Thread thread = new Thread(crawlerTask);
            thread.start();
        }
        while (pool.getStreamCounter() != streams){
            Thread.sleep(500);
        }
        pool.printAllLinks();
    }
}