package io.github.lo3ba.DAO;

import java.sql.*;
import java.util.concurrent.Executors;

public class ConnexionBD {
    private static final String URL = "jdbc:mysql://localhost:3306/jet_game";
    private static final String USER = "root";
    private static final String PASSWORD = "";
    private static Connection connection;

    public static synchronized Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                connection.setNetworkTimeout(
                    Executors.newFixedThreadPool(5),
                    3000 // 3 second timeout
                );
                connection.setAutoCommit(true);
            } catch (ClassNotFoundException e) {
                throw new SQLException("MySQL Driver not found", e);
            }
        }
        return connection;
    }

    public static synchronized void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }

    public static void executeUpdate(String sql, Object... params) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }
            stmt.executeUpdate();
        }
    }

    public static ResultSet executeQuery(String sql, Object... params) throws SQLException {
        Connection conn = getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);

        for (int i = 0; i < params.length; i++) {
            stmt.setObject(i + 1, params[i]);
        }
        return stmt.executeQuery();
    }
}
