package my.telegrambot.updater;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;

import java.util.*;

@Log4j2
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UpdaterService implements Runnable {
    volatile int maxNumberQuote = 450000;
    final Collection<Integer> quotesCollection = DataBaseUpdater.loadQuotesCollection();
    final Collection<Integer> stripsCollection = DataBaseUpdater.loadStripsCollection();

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            updateMaxNumberOfQuote();
            updateMapStrips();
            DataBaseUpdater.saveQuotesCollection();
            sleep(6 * 60 * 60 * 1000);
        }
    }

    private void updateMaxNumberOfQuote() {
        int max = Math.max(maxNumberQuote, MaxNumberQuoteUpdater.updateMaxNumberOfQuote());
        maxNumberQuote = max;
        log.info(String.format("Update maxNumberQuote: %d->%d", maxNumberQuote, max));
    }

    private void updateMapStrips() {
        boolean needMapStripsUpdate = StripsUpdater.updateDataBase();
        if (needMapStripsUpdate) {
            DataBaseUpdater.saveStripsCollection();
            log.info(String.format("Update list of strips, current size: %d", stripsCollection.size()));
        }
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
        public static final UpdaterService HOLDER_INSTANCE = new UpdaterService();
    }
}