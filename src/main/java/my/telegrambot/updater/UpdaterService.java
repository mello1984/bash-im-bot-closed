package my.telegrambot.updater;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UpdaterService implements Runnable {
    private static UpdaterService constantsUpdater = null;
    private final static Logger logger = Logger.getLogger(UpdaterService.class.getName());
    private volatile int maxNumberQuote = 450000;
    private volatile Map<Integer, Integer> mapStrips;
    private volatile List<Integer> keyStrips;


    private UpdaterService() {
        mapStrips = StripsUpdater.loadMapStrips();
        keyStrips = new ArrayList<>(mapStrips.keySet());
    }

    public static synchronized UpdaterService getInstance() {
        if (constantsUpdater == null) constantsUpdater = new UpdaterService();
        return constantsUpdater;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            updateMaxNumberOfQuote();
            updateMapStrips();
            sleep(60 * 60 * 1000);
        }
    }

    private void updateMaxNumberOfQuote() {
        int max = Math.max(maxNumberQuote, MaxNumberQuoteUpdater.updateMaxNumberOfQuote());
        maxNumberQuote = max;
        logger.log(Level.INFO, String.format("Update maxNumberQuote: %d->%d", maxNumberQuote, max));
    }

    private void updateMapStrips() {
        boolean needMapStripsUpdate = StripsUpdater.updateDataBase();
        if (needMapStripsUpdate) {
            mapStrips = StripsUpdater.loadMapStrips();
            keyStrips = new ArrayList<>(mapStrips.keySet());
            logger.log(Level.INFO, String.format("Update map of strips, current size: %d", mapStrips.size()));
        }
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            logger.log(Level.SEVERE, "Exception in UpdaterService", e);
        }
    }

    public Map<Integer, Integer> getMapStrips() {
        return Collections.unmodifiableMap(mapStrips);
    }

    public List<Integer> getKeyStrips() {
        return Collections.unmodifiableList(keyStrips);
    }

    public Integer getMaxNumberQuote() {
        return maxNumberQuote;
    }
}