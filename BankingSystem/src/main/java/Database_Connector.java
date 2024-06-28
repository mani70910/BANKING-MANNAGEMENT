import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database_Connector {
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/bankingsystem";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "BankingSystem@12";

    public static class DatabaseConnectionException extends Exception {
        public DatabaseConnectionException(String message) {
            super(message);
        }
    }

    public static Connection connect() throws DatabaseConnectionException {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
            System.out.println("Connected to the database.");
        } catch (SQLException e) {
            throw new DatabaseConnectionException("Failed to connect to the database. Reason: " + e.getMessage());
        }
        return connection;
    }

    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Connection closed.");
            } catch (SQLException e) {
                System.out.println("Failed to close connection.");
                e.printStackTrace();
            }
        }
    }
}