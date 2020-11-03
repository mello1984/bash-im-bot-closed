package my.telegrambot.updater;

import my.telegrambot.DBRealize;
import my.telegrambot.DocumentDownloader;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class StripsUpdater {
    private final static Logger logger = Logger.getLogger(StripsUpdater.class.getName());

    static Map<Integer, Integer> loadMapStrips() {
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
        logger.log(Level.INFO, "loadMapStrips loaded from DataBase successful");
        return map;
    }

    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            logger.log(Level.SEVERE, "Exception in StripsUpdater", e);
        }
    }

    static Map<Integer, Integer> mapWithMonthStrips(Calendar period) {
        int urlPeriod = period.get(Calendar.YEAR) * 100 + period.get(Calendar.MONTH) + 1;
        String url = "https://bash.im/strips/" + urlPeriod;
        Optional<Document> document = DocumentDownloader.getInstance().download(url);
        Map<Integer, Integer> map = new HashMap<>();
        if (document.isPresent()) {
            Elements elements = document.get().body().getElementsByClass("quote__author");
            elements.forEach(e -> {
                int key = Integer.parseInt(e.child(0).text());
                int value = Integer.parseInt(e.child(0).attr("href").replaceAll("/strip/", ""));
                map.put(key, value);
            });
        }
        return map;
    }

    static boolean updateDataBase() {
        Calendar calendar = Calendar.getInstance();
        Map<Integer, Integer> map = mapWithMonthStrips(calendar);
        calendar.add(Calendar.MONTH, -1);
        map.putAll(mapWithMonthStrips(calendar));

        map = map.entrySet().stream()
                .filter(e -> !UpdaterService.getInstance().getMapStrips().containsKey(e.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        boolean needUpdate = !map.isEmpty();
        if (needUpdate) {
            DBRealize dbRealize = DBRealize.getInstance();
            for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
                try {
                    dbRealize.executeUpdate("INSERT INTO strips (key, value) VALUES(" + entry.getKey() + "," + entry.getValue() + ");");
                } catch (SQLException throwable) {
                    logger.log(Level.SEVERE, "SQLException in updateDataBase", throwable);
                }
            }
            dbRealize.closeConnection();
        }
        return needUpdate;
    }

}
