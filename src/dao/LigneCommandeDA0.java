/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import model.LigneCommande;
import model.Commande;
import model.Produit;
import utils.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

class LigneCommandeDAO {
    private Connection connection;
    private ProduitDAO produitDAO;

    public LigneCommandeDAO() {
        try {
            this.connection = DatabaseConnection.getConnection();
            this.produitDAO = new ProduitDAO();
            System.out.println("✅ LigneCommandeDAO initialisé");
        } catch (Exception e) {
            System.err.println("❌ Erreur d'initialisation LigneCommandeDAO: " + e.getMessage());
            e.printStackTrace();
            // Propager l'erreur comme RuntimeException
            throw new RuntimeException("Échec d'initialisation de LigneCommandeDAO", e);
        }
    }

    // 1. Ajouter une ligne de commande
    public boolean addLigneCommande(LigneCommande ligne) {
        String sql = "INSERT INTO ligne_commande (commande_id, produit_id, quantite, prix_unitaire) VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, ligne.getCommande().getId());
            stmt.setInt(2, ligne.getProduit().getId());
            stmt.setInt(3, ligne.getQuantite());
            stmt.setDouble(4, ligne.getPrixUnitaire());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    ligne.setId(generatedKeys.getInt(1));
                }
                
                // Mettre à jour le stock du produit
                Produit produit = produitDAO.getProduitById(ligne.getProduit().getId());
                if (produit != null) {
                    int nouveauStock = produit.getStockActuel() - ligne.getQuantite();
                    produitDAO.updateStock(produit.getId(), nouveauStock);
                } else {
                    System.err.println("Produit non trouvé pour ID: " + ligne.getProduit().getId());
                }
                
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout de la ligne de commande : " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // 2. Modifier une ligne de commande
    public boolean updateLigneCommande(LigneCommande ligne) {
        String sql = "UPDATE ligne_commande SET quantite = ?, prix_unitaire = ? WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, ligne.getQuantite());
            stmt.setDouble(2, ligne.getPrixUnitaire());
            stmt.setInt(3, ligne.getId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la modification de la ligne de commande : " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // 3. Supprimer une ligne de commande
    public boolean deleteLigneCommande(int id) {
        // Récupérer d'abord la ligne pour savoir combien restituer en stock
        LigneCommande ligne = getLigneCommandeById(id);
        if (ligne == null) {
            System.err.println("Ligne de commande non trouvée pour ID: " + id);
            return false;
        }
        
        String sql = "DELETE FROM ligne_commande WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            
            boolean success = stmt.executeUpdate() > 0;
            
            if (success) {
                // Restituer le stock
                Produit produit = produitDAO.getProduitById(ligne.getProduit().getId());
                if (produit != null) {
                    int nouveauStock = produit.getStockActuel() + ligne.getQuantite();
                    produitDAO.updateStock(produit.getId(), nouveauStock);
                }
            }
            
            return success;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de la ligne de commande : " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // 4. Récupérer une ligne par ID
    public LigneCommande getLigneCommandeById(int id) {
        String sql = "SELECT lc.*, p.nom AS produit_nom FROM ligne_commande lc " +
                     "JOIN produit p ON lc.produit_id = p.id WHERE lc.id = ?";
        LigneCommande ligne = null;
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                ligne = extractLigneFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération de la ligne de commande : " + e.getMessage());
            e.printStackTrace();
        }
        return ligne;
    }

    // 5. Récupérer toutes les lignes d'une commande
    public List<LigneCommande> getLignesByCommande(int commandeId) {
        List<LigneCommande> lignes = new ArrayList<>();
        String sql = "SELECT lc.*, p.nom AS produit_nom FROM ligne_commande lc " +
                     "JOIN produit p ON lc.produit_id = p.id WHERE lc.commande_id = ? ORDER BY lc.id";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, commandeId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                LigneCommande ligne = extractLigneFromResultSet(rs);
                lignes.add(ligne);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des lignes de commande : " + e.getMessage());
            e.printStackTrace();
        }
        return lignes;
    }

    // 6. Méthode utilitaire pour extraire une ligne d'un ResultSet
    private LigneCommande extractLigneFromResultSet(ResultSet rs) throws SQLException {
        LigneCommande ligne = new LigneCommande();
        ligne.setId(rs.getInt("id"));
        
        // Créer la commande (juste avec l'ID pour l'instant)
        Commande commande = new Commande();
        commande.setId(rs.getInt("commande_id"));
        ligne.setCommande(commande);
        
        // Créer le produit
        Produit produit = new Produit();
        produit.setId(rs.getInt("produit_id"));
        produit.setNom(rs.getString("produit_nom"));
        ligne.setProduit(produit);
        
        ligne.setQuantite(rs.getInt("quantite"));
        ligne.setPrixUnitaire(rs.getDouble("prix_unitaire"));
        ligne.setMontantLigne(rs.getDouble("montant_ligne"));
        
        return ligne;
    }

    // 7. Récupérer les produits les plus vendus
    public List<Object[]> getTopProduitsVendus(int limit) {
        List<Object[]> resultats = new ArrayList<>();
        String sql = "SELECT p.nom, SUM(lc.quantite) AS total_vendu, c.libelle AS categorie " +
                     "FROM ligne_commande lc " +
                     "JOIN produit p ON lc.produit_id = p.id " +
                     "JOIN categorie c ON p.categorie_id = c.id " +
                     "JOIN commande cmd ON lc.commande_id = cmd.id " +
                     "WHERE cmd.etat = 'VALIDÉE' " +
                     "GROUP BY p.id, p.nom, c.libelle " +
                     "ORDER BY total_vendu DESC " +
                     "LIMIT ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, limit);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Object[] ligne = new Object[3];
                ligne[0] = rs.getString("nom");
                ligne[1] = rs.getInt("total_vendu");
                ligne[2] = rs.getString("categorie");
                resultats.add(ligne);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des tops produits : " + e.getMessage());
            e.printStackTrace();
        }
        return resultats;
    }

    // 8. Fermer la connexion
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Connexion LigneCommandeDAO fermée");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la fermeture de la connexion : " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // 9. Méthode pour tester la connexion
    public boolean testConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                System.out.println("✅ Connexion LigneCommandeDAO active");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("❌ Test de connexion échoué: " + e.getMessage());
        }
        return false;
    }
}