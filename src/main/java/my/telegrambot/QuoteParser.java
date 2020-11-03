package my.telegrambot;

import my.telegrambot.updater.UpdaterService;
import org.jsoup.nodes.Document;

import java.util.HashSet;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

public class QuoteParser {
    static Set<Integer> set = new HashSet<>();
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
                } else set.add(i);
            }
        }
        return out;
    }

    private int getRandomNumber() {
        int i;
        while (set.contains(i = random.nextInt(UpdaterService.getInstance().getMaxNumberQuote()) + 1)) {
        }
        return i;
    }
}
 