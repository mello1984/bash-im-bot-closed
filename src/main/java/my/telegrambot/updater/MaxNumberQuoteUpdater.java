package my.telegrambot.updater;

import lombok.extern.log4j.Log4j2;
import my.telegrambot.DocumentDownloader;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.Optional;

@Log4j2
class MaxNumberQuoteUpdater {
    static int updateMaxNumberOfQuote() {
        int result = 0;
        Optional<Document> document = DocumentDownloader.getInstance().download("https://bash.im/");
        if (document.isPresent()) {
            Elements elements = document.get().select("article");
            result = elements.stream().mapToInt(e -> Integer.parseInt(e.attr("data-quote"))).max().orElse(result);
        } else log.warn("Problem with updating MaxNumberOfQuote, document is empty");
        return result;
    }
}
