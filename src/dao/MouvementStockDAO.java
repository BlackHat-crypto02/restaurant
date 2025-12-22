/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import model.MouvementStock;
import model.Produit;
import utils.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MouvementStockDAO {
    private Connection connection;
    private ProduitDAO produitDAO;

    public MouvementStockDAO() {
        this.connection = DatabaseConnection.getConnection();
        this.produitDAO = new ProduitDAO();
    }

    // 1. Ajouter un mouvement de stock
    public boolean addMouvement(MouvementStock mouvement) {
        String sql = "INSERT INTO mouvement_stock (produit_id, type, quantite, motif) VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, mouvement.getProduit().getId());
            stmt.setString(2, mouvement.getType());
            stmt.setInt(3, mouvement.getQuantite());
            stmt.setString(4, mouvement.getMotif());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    mouvement.setId(generatedKeys.getInt(1));
                    
                    // Récupérer la date du mouvement depuis la base
                    String dateSql = "SELECT date_mouvement FROM mouvement_stock WHERE id = ?";
                    try (PreparedStatement dateStmt = connection.prepareStatement(dateSql)) {
                        dateStmt.setInt(1, mouvement.getId());
                        ResultSet rs = dateStmt.executeQuery();
                        if (rs.next()) {
                            mouvement.setDateMouvement(rs.getTimestamp("date_mouvement"));
                        }
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout du mouvement : " + e.getMessage());
        }
        return false;
    }

    // 2. Récupérer tous les mouvements
    public List<MouvementStock> getAllMouvements() {
        List<MouvementStock> mouvements = new ArrayList<>();
        String sql = "SELECT m.*, p.nom AS produit_nom FROM mouvement_stock m " +
                     "JOIN produit p ON m.produit_id = p.id ORDER BY m.date_mouvement DESC";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                MouvementStock mouvement = extractMouvementFromResultSet(rs);
                mouvements.add(mouvement);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des mouvements : " + e.getMessage());
        }
        return mouvements;
    }

    // 3. Récupérer les mouvements d'un produit spécifique
    public List<MouvementStock> getMouvementsByProduit(int produitId) {
        List<MouvementStock> mouvements = new ArrayList<>();
        String sql = "SELECT m.*, p.nom AS produit_nom FROM mouvement_stock m " +
                     "JOIN produit p ON m.produit_id = p.id " +
                     "WHERE m.produit_id = ? ORDER BY m.date_mouvement DESC";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, produitId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                MouvementStock mouvement = extractMouvementFromResultSet(rs);
                mouvements.add(mouvement);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des mouvements par produit : " + e.getMessage());
        }
        return mouvements;
    }

    // 4. Récupérer les mouvements sur une période
    public List<MouvementStock> getMouvementsByPeriode(Date dateDebut, Date dateFin) {
        List<MouvementStock> mouvements = new ArrayList<>();
        String sql = "SELECT m.*, p.nom AS produit_nom FROM mouvement_stock m " +
                     "JOIN produit p ON m.produit_id = p.id " +
                     "WHERE m.date_mouvement BETWEEN ? AND ? ORDER BY m.date_mouvement DESC";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, new java.sql.Date(dateDebut.getTime()));
            stmt.setDate(2, new java.sql.Date(dateFin.getTime()));
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                MouvementStock mouvement = extractMouvementFromResultSet(rs);
                mouvements.add(mouvement);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des mouvements par période : " + e.getMessage());
        }
        return mouvements;
    }

    // 5. Récupérer un mouvement par ID
    public MouvementStock getMouvementById(int id) {
        String sql = "SELECT m.*, p.nom AS produit_nom FROM mouvement_stock m " +
                     "JOIN produit p ON m.produit_id = p.id WHERE m.id = ?";
        MouvementStock mouvement = null;
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                mouvement = extractMouvementFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération du mouvement : " + e.getMessage());
        }
        return mouvement;
    }

    // 6. Méthode utilitaire pour extraire un mouvement d'un ResultSet
    private MouvementStock extractMouvementFromResultSet(ResultSet rs) throws SQLException {
        MouvementStock mouvement = new MouvementStock();
        mouvement.setId(rs.getInt("id"));
        
        Produit produit = new Produit();
        produit.setId(rs.getInt("produit_id"));
        produit.setNom(rs.getString("produit_nom"));
        mouvement.setProduit(produit);
        
        mouvement.setType(rs.getString("type"));
        mouvement.setQuantite(rs.getInt("quantite"));
        mouvement.setDateMouvement(rs.getTimestamp("date_mouvement"));
        mouvement.setMotif(rs.getString("motif"));
        
        return mouvement;
    }

    // 7. Vérifier si une sortie est possible (stock suffisant)
    public boolean checkStockDisponible(int produitId, int quantiteDemandee) {
        String sql = "SELECT stock_actuel FROM produit WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, produitId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                int stockActuel = rs.getInt("stock_actuel");
                return stockActuel >= quantiteDemandee;
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la vérification du stock : " + e.getMessage());
        }
        return false;
    }

    // 8. Fermer la connexion
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la fermeture de la connexion : " + e.getMessage());
        }
    }
}
