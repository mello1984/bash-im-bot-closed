package my.telegrambot;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BaseUpdater implements Runnable {
    private static BaseUpdater constantsUpdater = null;
    private final Logger logger = Logger.getLogger(BaseUpdater.class.getName());
    private volatile int maxNumberQuote;


    private BaseUpdater() {
    }

    public static synchronized BaseUpdater getInstance() {
        if (constantsUpdater == null) constantsUpdater = new BaseUpdater();
        return constantsUpdater;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            updateMaxNumberOfQuote();
            sleep(15 * 60 * 1000);
        }
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            logger.log(Level.SEVERE, "Exception in baseUpdater", e);
        }
    }

    private void updateMaxNumberOfQuote() {
        int result = 0;
        Optional<Document> document = DocumentDownloader.getInstance().download("https://bash.im/");
        if (document.isEmpty()) {
            logger.log(Level.SEVERE, "Problem with updating MaxNumberOfQuote, document is empty");
            return;
        }
        Elements elements = document.get().select("article");
        result = elements.stream().mapToInt(e -> Integer.parseInt(e.attr("data-quote"))).max().orElse(result);
        result = Math.max(result, maxNumberQuote);
        maxNumberQuote = result;
        logger.log(Level.INFO, String.format("Update maxNumberQuote: %d->%d", maxNumberQuote, result));
    }

    public int getMaxNumberQuote() {
        return maxNumberQuote;
    }
}