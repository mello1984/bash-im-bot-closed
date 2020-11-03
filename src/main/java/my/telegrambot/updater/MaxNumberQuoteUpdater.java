package my.telegrambot.updater;

import my.telegrambot.DocumentDownloader;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

class MaxNumberQuoteUpdater {
    private final static Logger logger = Logger.getLogger(MaxNumberQuoteUpdater.class.getName());

    static int updateMaxNumberOfQuote() {
        int result = 0;
        Optional<Document> document = DocumentDownloader.getInstance().download("https://bash.im/");
        if (document.isPresent()) {
            Elements elements = document.get().select("article");
            result = elements.stream().mapToInt(e -> Integer.parseInt(e.attr("data-quote"))).max().orElse(result);
        } else logger.log(Level.SEVERE, "Problem with updating MaxNumberOfQuote, document is empty");
        return result;
    }
}
