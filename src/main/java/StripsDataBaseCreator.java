import my.telegrambot.DBRealize;
import my.telegrambot.DocumentDownloader;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StripsDataBaseCreator {
    private static final Logger logger = Logger.getLogger(StripsDataBaseCreator.class.getName());
    private static final Map<Integer, Integer> mapStrips = new HashMap<>();

    public static void main(String[] args) throws InterruptedException, SQLException {
        List<Integer> periods = Helper.generatePeriodsList(200708, 202010);
        for (Integer period : periods) {
            updateMaxNumberStrip(period);
            Thread.sleep(1000);
        }
        logger.log(Level.INFO, String.format("map created, size: %d", mapStrips.size()));

        DBRealize dbRealize = DBRealize.getInstance();
        dbRealize.execute("DROP TABLE IF EXISTS strips;");
        dbRealize.execute("CREATE TABLE strips (key INTEGER PRIMARY KEY, value INTEGER NOT NULL);");
        for (Map.Entry<Integer, Integer> entry : mapStrips.entrySet()) {
            dbRealize.executeUpdate("INSERT INTO strips (key, value) VALUES(" + entry.getKey() + "," + entry.getValue() + ");");
        }
        dbRealize.closeConnection();
    }


    public static void updateMaxNumberStrip(Integer period) {
        String url = "https://bash.im/strips/" + period;
        boolean update = false;
        Optional<Document> document = DocumentDownloader.getInstance().download(url);
        if (document.isPresent()) {
            if (!document.get().body().text().equals("HTTP/1.0 404 Not Found"))
                update = parseStripsToMapStrips(document.get());
            if (update) {
                logger.log(Level.INFO, String.format("Update map of strips, period %d, actual size: %d", period, mapStrips.size()));
            }
        }

    }

    private static boolean parseStripsToMapStrips(Document doc) {
        Elements elements = doc.body().getElementsByClass("quote__author");
        Map<Integer, Integer> map = new HashMap<>();
        elements.forEach(e -> {
            int key = Integer.parseInt(e.child(0).text());
            int value = Integer.parseInt(e.child(0).attr("href").replaceAll("/strip/", ""));
            map.put(key, value);
        });
        boolean needUpdate = !map.keySet().stream().allMatch(mapStrips::containsKey);
        if (needUpdate) mapStrips.putAll(map);
        return needUpdate;
    }
}
