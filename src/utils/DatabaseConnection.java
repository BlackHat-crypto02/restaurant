package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/restaurant_db";
    private static final String USER = "root";
    private static final String PASSWORD = "ton_mot_de_passe";

    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✅ Connexion MySQL établie");
            return conn;
        } catch (ClassNotFoundException e) {
            System.err.println("❌ Driver MySQL introuvable !");
            throw new RuntimeException("Driver MySQL non trouvé", e);
        } catch (SQLException e) {
            System.err.println("❌ Erreur SQL: " + e.getMessage());
            throw new RuntimeException("Erreur de connexion à la base de données", e);
        }
    }
    
    // Méthode pour tester la connexion
    public static void testConnection() {
        try (Connection conn = getConnection()) {
            if (conn != null && !conn.isClosed()) {
                System.out.println("✅ Test de connexion réussi !");
            }
        } catch (SQLException e) {
            System.err.println("❌ Test de connexion échoué: " + e.getMessage());
        }
    }
}