package io.github.lo3ba.DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ConnexionBD {
    private static Connection connexion = null;
    private static final String URL = "jdbc:mysql://localhost:3306/jet_game?useSSL=false&serverTimezone=UTC";
    private static final String USER = "jihane";
    private static final String PASSWORD = "jihane123!";

    public static Statement seConnecter() throws SQLException {
        try {
            // 1. Charger le driver (méthode moderne pour JDBC 4.0+)
            Class.forName("com.mysql.cj.jdbc.Driver"); // Notez le "cj" pour MySQL Connector/J

            // 2. Établir la connexion si elle n'existe pas ou est fermée
            if (connexion == null || connexion.isClosed()) {
                connexion = DriverManager.getConnection(URL, USER, PASSWORD);
            }

            // 3. Créer un espace d'exécution
            return connexion.createStatement();
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver MySQL non trouvé", e);
        }
    }

    public static void seDeconnecter() {
        try {
            if (connexion != null && !connexion.isClosed()) {
                connexion.close();
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la déconnexion : ");
            e.printStackTrace();
        }
    }

    // Méthode supplémentaire pour obtenir directement une Connection
    public static Connection getConnection() throws SQLException {
        if (connexion == null || connexion.isClosed()) {
            connexion = DriverManager.getConnection(URL, USER, PASSWORD);
        }
        return connexion;
    }
}
