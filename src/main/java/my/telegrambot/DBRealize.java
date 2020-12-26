package my.telegrambot;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.*;
import java.util.Optional;

@Log4j2
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DBRealize {
    private Connection connection = setConnection();

    private Connection setConnection() {
        Optional<Connection> optional = Optional.empty();
        while (optional.isEmpty()) {
            try {
                URI dbUri = new URI(System.getenv("telegrambotJDBC_DATABASE_URL"));
                String username = dbUri.getUserInfo().split(":")[0];
                String password = dbUri.getUserInfo().split(":")[1];
                String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath() + "?sslmode=require";
                optional = Optional.of(DriverManager.getConnection(dbUrl, username, password));
                log.info("Database Connection Initialized");
            } catch (URISyntaxException | SQLException e) {
                log.warn("Exception with connection to database", e);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
            }
        }
        return optional.get();
    }

    public void closeConnection() {
        if (connection == null) return;
        try {
            connection.close();
            connection = null;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public boolean execute(String sql) throws SQLException {
        if (connection == null)
            throw new SQLException("Connection null!");
        Statement statement = connection.createStatement();
        boolean res = statement.execute(sql);
        statement.close();
        return res;
    }

    public int executeUpdate(String sql) throws SQLException {
        if (connection == null)
            throw new SQLException("Connection null!");
        Statement statement = connection.createStatement();
        int res = statement.executeUpdate(sql);
        statement.close();
        return res;
    }

    public ResultSet executeQuery(String sql) throws SQLException {
        if (connection == null)
            throw new SQLException("Connection null!");
        Statement statement = connection.createStatement();
        ResultSet res = statement.executeQuery(sql);
        statement.close();
        return res;
    }

    public static DBRealize getInstance() {
        return DBHolder.HOLDER_INSTANCE;
    }

    private static class DBHolder {
        private static final DBRealize HOLDER_INSTANCE = new DBRealize();
    }
}
