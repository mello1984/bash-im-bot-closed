package my.telegrambot;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DataUpdater implements Runnable {
    private static DataUpdater constantsUpdater = null;
    private final static Logger logger = Logger.getLogger(DataUpdater.class.getName());
    private volatile Integer maxNumberQuote=450000;
    private volatile Map<Integer, Integer> mapStrips = new HashMap<>();
    private volatile List<Integer> keyStrips = new ArrayList<>();


    private DataUpdater() {
        loadMapStrips();
        loadListStrips();
    }

    public static synchronized DataUpdater getInstance() {
        if (constantsUpdater == null) constantsUpdater = new DataUpdater();
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

    public Integer getMaxNumberQuote() {
        return maxNumberQuote;
    }

    private void loadMapStrips() {
        DBRealize db = DBRealize.getInstance();
        Map<Integer, Integer> map = new HashMap<>();

        boolean load = false;
        while (!load) {
            try {
                ResultSet resSet = db.executeQuery("SELECT * FROM strips");
                while (resSet.next()) {
                    map.put(resSet.getInt("key"), resSet.getInt("value"));
                }
                resSet.close();
                load = true;
            } catch (SQLException throwable) {
                logger.log(Level.SEVERE, "SQLException in loadMapStrips from DataBase", throwable);
                sleep(100);
            }
        }
        mapStrips = map;
        logger.log(Level.INFO, "loadMapStrips loaded from DataBase successful");
    }

    private void loadListStrips() {
        keyStrips = new ArrayList<>(mapStrips.keySet());
    }

    public Map<Integer, Integer> getMapStrips() {
        return Collections.unmodifiableMap(mapStrips);
    }

    public List<Integer> getKeyStrips() {
        return Collections.unmodifiableList(keyStrips);
    }

}