package WebScanner;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Класс, для сканирования в нескольких потоках*/
public class CrawlerTask implements Runnable{
    /** Может понадобятся, а может уберу */
    public static final String LINK_REGEX = "href\\s*=\\s*\"([^$^\"]*)\"";
    public static final Pattern LINK_PATTERN = Pattern.compile(LINK_REGEX, Pattern.CASE_INSENSITIVE);

    private URLPool pool;
    private PrintWriter out;

    public CrawlerTask(URLPool pool){
        this.pool = pool;
    }

    /** Метод, для всего и сразу: отправка запроса на сайт, обработка полученного URL, добавление в пул */
    public void connectTo(URLDepthPair pair) throws IOException {
        //Создание сокета, отправка запроса на сервер
        Socket socket;
        try {
            socket = new Socket(pair.getDomainName(), 80);
            socket.setSoTimeout(50000);
            out = new PrintWriter(socket.getOutputStream(), true);
            out.println("GET " + pair.getDocPath() + " HTTP/1.1");
            out.println("Host: " + pair.getDomainName());
            out.println("Connection: close");
            out.println();
        }
        catch (SocketException e){
            System.err.println("SocketException: " + e.getMessage());
            return;

        }
        catch (IOException e){
            System.err.println("IOException: " + e.getMessage());
            return;
        }

        BufferedReader inputData = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String str;

        while ((str = inputData.readLine()) != null) {
            Matcher LinkFinder = LINK_PATTERN.matcher(str);
            while (LinkFinder.find()) {
                //Предыдущее совпадение
                String newURL = LinkFinder.group(1);
                URL newSite;
                try {
                    if (newURL.contains("href=\"")) {
                        newSite = new URL(newURL);
                    }
                    else {
                        URL test = new URL (pair.getURLAddress());
                        newSite = new URL(test, newURL);
                    }
                    pool.add(new URLDepthPair(newSite.toString(), pair.getSearchDepth() + 1));
                }
                catch (MalformedURLException e) {
                    System.err.println("Ошибка URL - " + e.getMessage());
                }
            }
        }

        //Закртие сокета и потоков
        try{
            socket.close();
            out.close();
            inputData.close();
        } catch (IOException e) {
            System.err.println("IOException: " + "не удалось закрыть сокет или поток");
        }
    }

    @Override
    public void run() {
        URLDepthPair firstPair;
        while (true) {
            firstPair = pool.get();
            try {
                connectTo(firstPair);
            } catch (IOException e) {
                System.err.println("IOException: " + e.getMessage());
            }
        }
    }
}
