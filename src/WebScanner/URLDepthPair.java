package WebScanner;
import java.net.MalformedURLException;
import java.net.URL;

/** Класс, для объединения в пару */
public class URLDepthPair {
    //Поле типа String, представляющее URL-адрес
    private String URLAddress;
    //Поле типа int, представляющее глубину поиска
    private int searchDepth;

    //Констурктор класса
    public URLDepthPair(String URL, int depth){
        URLAddress = URL;
        searchDepth = depth;
    }

    //Get`ы для полей
    public String getURLAddress() {
        return URLAddress;
    }

    public int getSearchDepth() {
        return searchDepth;
    }

    //Метод toString для возврата строкового значения URL-адреса и глубины
    @Override
    public String toString() {
        return "Глубина поиска: " + searchDepth  +"\t" +"URL: " + URLAddress;
    }

    /** Итак, если вы пишете какие-либо функции для того, чтобы разбить URL-адрес, или для проверки на то,
     является ли URL-адрес допустимым, поместите их в этот классе */

    //Метод, для возврата только основной части ссылки
    public String getDomainName (){
        try {
            URL url = new URL(URLAddress);
            return url.getHost();
        }catch (MalformedURLException e){
            System.err.println("MalformedURLException" + " : " + e.getMessage());
            return null;
        }
    }

    //Метод, для возврата остальной части ссылки
    public String getDocPath (){
        try {
            URL url = new URL(URLAddress);
            return url.getPath();
        }catch (MalformedURLException e){
            System.err.println("MalformedURLException" + " : " + e.getMessage());
            return null;
        }
    }
}