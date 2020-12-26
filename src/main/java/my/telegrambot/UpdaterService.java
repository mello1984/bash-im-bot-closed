package my.telegrambot;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.*;

@Log4j2
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UpdaterService implements Runnable {
    volatile int maxNumberQuote = 464000;
    final Collection<Integer> quotesCollection = DBUpdater.loadQuotesCollection();
    final Collection<Integer> stripsCollection = DBUpdater.loadStripsCollection();

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            updateMaxQuoteNumber();
            updateStripsAndSaveToDataBase();
            DBUpdater.saveQuotesCollection();
            sleep(6 * 60 * 60 * 1000);
        }
    }

    private void updateMaxQuoteNumber() {
        int result = 0;
        Optional<Document> document = DocumentDownloader.getInstance().download("https://bash.im/");
        if (document.isPresent()) {
            Elements elements = document.get().select("article");
            result = elements.stream().mapToInt(e -> Integer.parseInt(e.attr("data-quote"))).max().orElse(result);
        } else log.warn("Problem with updating MaxNumberOfQuote, document is empty");
        maxNumberQuote = Math.max(result, UpdaterService.getInstance().getMaxNumberQuote());
        log.info(String.format("Update maxNumberQuote, current value: %d", result));
    }


    private void updateStripsAndSaveToDataBase() {
        Calendar calendar = Calendar.getInstance();
        Collection<Integer> stripsTwoLastMonth = mapWithMonthStrips(calendar);
        calendar.add(Calendar.MONTH, -1);
        stripsTwoLastMonth.addAll(mapWithMonthStrips(calendar));

        stripsTwoLastMonth.removeAll(stripsCollection);
        boolean needUpdate = !stripsTwoLastMonth.isEmpty();
        if (needUpdate) {
            stripsCollection.addAll(stripsTwoLastMonth);
            DBUpdater.saveStripsCollection();
        }
    }

    private Collection<Integer> mapWithMonthStrips(Calendar period) {
        int urlPeriod = period.get(Calendar.YEAR) * 100 + period.get(Calendar.MONTH) + 1;
        String url = String.format("https://bash.im/strips/%d", urlPeriod);
        Optional<Document> document = DocumentDownloader.getInstance().download(url);
        Collection<Integer> collection = new ArrayList<>();
        if (document.isPresent()) {
            Elements elements = document.get().body().getElementsByClass("quote__author");
            elements.forEach(e -> {
                int value = Integer.parseInt(e.child(0).attr("href").replaceAll("/strip/", ""));
                collection.add(value);
            });
        }
        return collection;
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            log.warn(e);
        }
    }

    public static UpdaterService getInstance() {
        return UpdaterServiceHolder.HOLDER_INSTANCE;
    }

    private static class UpdaterServiceHolder {
        private static final UpdaterService HOLDER_INSTANCE = new UpdaterService();
    }
}