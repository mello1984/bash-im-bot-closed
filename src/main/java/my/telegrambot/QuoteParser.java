package my.telegrambot;

import org.jsoup.nodes.Document;

import java.util.*;

public class QuoteParser {
    static Collection<Integer> quotesCollection = UpdaterService.getInstance().getQuotesCollection();
    private final Random random = new Random();

    public String getRandomQuote() {
        String out = "";
        while (out.equals("")) {
            int i = getRandomNumber();
            Optional<Document> doc = DocumentDownloader.getInstance().download("https://bash.im/quote/" + i);
            if (doc.isPresent()) {
                if (doc.get().title().startsWith("Цитата #")) {
                    Quote quote = new Quote(doc.get().select("article").first());
                    out = quote.toString();
                } else quotesCollection.add(i);
            }
        }
        return out;
    }

    private int getRandomNumber() {
        int i;
        while (quotesCollection.contains(i = random.nextInt(UpdaterService.getInstance().getMaxNumberQuote()) + 1)) {
        }
        return i;
    }
}
 