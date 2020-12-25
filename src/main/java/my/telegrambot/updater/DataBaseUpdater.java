package my.telegrambot.updater;

import my.telegrambot.DBRealize;

import java.io.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class DataBaseUpdater {
    private static final int SIZE = 2000;
    private static final String CLEAR = "DELETE FROM %s";
    private static final String INSERT = "INSERT INTO %1$s (%2$s) VALUES (?)";
    private static final String LOAD = "SELECT * FROM %s";
    private static final String DATA_COLUMN = "data";
    private static final String QUOTES_TABLE = "quotes";
    private static final String STRIPS_TABLE = "strips_table";


    public static void saveQuotesCollection() {
        saveCollectionToDataBase(UpdaterService.getInstance().getQuotesCollection(), QUOTES_TABLE, DATA_COLUMN);
    }

    public static void saveStripsCollection() {
        saveCollectionToDataBase(UpdaterService.getInstance().getStripsCollection(), STRIPS_TABLE, DATA_COLUMN);
    }

    private static void saveCollectionToDataBase(Collection<Integer> collection, String table, String column) {
        try {
            DBRealize.getInstance().executeUpdate(String.format(CLEAR, table));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        Integer[] ints = collection.toArray(new Integer[0]);
        int count_min;
        int count_max = 0;
        while (count_max < ints.length) {
            count_min = count_max;
            count_max = Math.min(ints.length, SIZE);
            int array_size = count_max - count_min;
            Integer[] integersToDB = new Integer[array_size];
            System.arraycopy(ints, count_min, integersToDB, 0, array_size);
            saveArrayQuotesToDataBase(integersToDB, table, column);
        }
    }

    private static void saveArrayQuotesToDataBase(Integer[] ints, String table, String column) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            String query = String.format(INSERT, table, column);
            PreparedStatement statement = DBRealize.getInstance().getConnection().prepareStatement(query);
            oos.writeObject(ints);
            oos.flush();
            byte[] bytes = baos.toByteArray();
            statement.setBytes(1, bytes);
            statement.executeUpdate();
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    public static Collection<Integer> loadQuotesCollection() {
        return loadCollection(new HashSet<>(), QUOTES_TABLE, DATA_COLUMN);
    }

    public static Collection<Integer> loadStripsCollection() {
        return loadCollection(new ArrayList<>(), STRIPS_TABLE, DATA_COLUMN);
    }

    private static Collection<Integer> loadCollection(Collection<Integer> collection, String table, String column) {
        try {
            ResultSet resultSet = DBRealize.getInstance().executeQuery(String.format(LOAD, table));
            while (resultSet.next()) {
                byte[] bytes = resultSet.getBytes(column);
                try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
                     ObjectInputStream ois = new ObjectInputStream(bais)) {
                    Integer[] integers = (Integer[]) ois.readObject();
                    collection.addAll(Arrays.asList(integers));
                } catch (IOException | ClassNotFoundException throwables) {
                    throwables.printStackTrace();
                }
            }
            resultSet.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return collection;
    }

}
