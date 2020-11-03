package my.telegrambot.updater;

import my.telegrambot.DBRealize;

import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

class StripsDataBaseCreator {
    private static final Logger logger = Logger.getLogger(StripsDataBaseCreator.class.getName());
    private static final Map<Integer, Integer> mapStrips = new HashMap<>();

    static void reloadAllStripsDataBase() throws InterruptedException, SQLException {
        Calendar currentCalendar = Calendar.getInstance();
        Calendar calendar = Calendar.getInstance();
        calendar.set(2007, Calendar.AUGUST, 1);
        while (calendar.get(Calendar.MONTH) <= currentCalendar.get(Calendar.MONTH)) {
            mapStrips.putAll(StripsUpdater.mapWithMonthStrips(calendar));
            calendar.add(Calendar.MONTH,1);
            Thread.sleep(1000);
        }
        logger.log(Level.INFO, String.format("StripsDataBaseCreator: actual map of strips created, size: %d", mapStrips.size()));

        DBRealize dbRealize = DBRealize.getInstance();
        dbRealize.execute("DROP TABLE IF EXISTS strips;");
        dbRealize.execute("CREATE TABLE strips (key INTEGER PRIMARY KEY, value INTEGER NOT NULL);");
        for (Map.Entry<Integer, Integer> entry : mapStrips.entrySet()) {
            dbRealize.executeUpdate("INSERT INTO strips (key, value) VALUES(" + entry.getKey() + "," + entry.getValue() + ");");
        }
        dbRealize.closeConnection();
    }
}
