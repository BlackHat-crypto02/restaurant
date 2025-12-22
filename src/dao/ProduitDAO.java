/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import model.Produit;
import model.Categorie;
import utils.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProduitDAO {
    private Connection connection;
    private CategorieDAO categorieDAO;

    public ProduitDAO() {
        this.connection = DatabaseConnection.getConnection();
        this.categorieDAO = new CategorieDAO();
    }

    // 1. Ajouter un produit
    public boolean addProduit(Produit produit) {
        String sql = "INSERT INTO produit (nom, categorie_id, prix_vente, stock_actuel, seuil_alerte) VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, produit.getNom());
            stmt.setInt(2, produit.getCategorie().getId());
            stmt.setDouble(3, produit.getPrixVente());
            stmt.setInt(4, produit.getStockActuel());
            stmt.setInt(5, produit.getSeuilAlerte());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    produit.setId(generatedKeys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout du produit : " + e.getMessage());
        }
        return false;
    }

    // 2. Modifier un produit
    public boolean updateProduit(Produit produit) {
        String sql = "UPDATE produit SET nom = ?, categorie_id = ?, prix_vente = ?, stock_actuel = ?, seuil_alerte = ? WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, produit.getNom());
            stmt.setInt(2, produit.getCategorie().getId());
            stmt.setDouble(3, produit.getPrixVente());
            stmt.setInt(4, produit.getStockActuel());
            stmt.setInt(5, produit.getSeuilAlerte());
            stmt.setInt(6, produit.getId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la modification du produit : " + e.getMessage());
        }
        return false;
    }

    // 3. Supprimer un produit
    public boolean deleteProduit(int id) {
        String sql = "DELETE FROM produit WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression du produit : " + e.getMessage());
        }
        return false;
    }

    // 4. Récupérer un produit par son ID
    public Produit getProduitById(int id) {
        String sql = "SELECT p.*, c.libelle AS categorie_libelle FROM produit p " +
                     "JOIN categorie c ON p.categorie_id = c.id WHERE p.id = ?";
        Produit produit = null;
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                produit = new Produit();
                produit.setId(rs.getInt("id"));
                produit.setNom(rs.getString("nom"));
                
                Categorie categorie = new Categorie();
                categorie.setId(rs.getInt("categorie_id"));
                categorie.setLibelle(rs.getString("categorie_libelle"));
                produit.setCategorie(categorie);
                
                produit.setPrixVente(rs.getDouble("prix_vente"));
                produit.setStockActuel(rs.getInt("stock_actuel"));
                produit.setSeuilAlerte(rs.getInt("seuil_alerte"));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération du produit : " + e.getMessage());
        }
        return produit;
    }

    // 5. Récupérer tous les produits
    public List<Produit> getAllProduits() {
        List<Produit> produits = new ArrayList<>();
        String sql = "SELECT p.*, c.libelle AS categorie_libelle FROM produit p " +
                     "JOIN categorie c ON p.categorie_id = c.id ORDER BY p.nom";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Produit produit = new Produit();
                produit.setId(rs.getInt("id"));
                produit.setNom(rs.getString("nom"));
                
                Categorie categorie = new Categorie();
                categorie.setId(rs.getInt("categorie_id"));
                categorie.setLibelle(rs.getString("categorie_libelle"));
                produit.setCategorie(categorie);
                
                produit.setPrixVente(rs.getDouble("prix_vente"));
                produit.setStockActuel(rs.getInt("stock_actuel"));
                produit.setSeuilAlerte(rs.getInt("seuil_alerte"));
                
                produits.add(produit);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des produits : " + e.getMessage());
        }
        return produits;
    }

    // 6. Récupérer les produits par catégorie
    public List<Produit> getProduitsByCategorie(int categorieId) {
        List<Produit> produits = new ArrayList<>();
        String sql = "SELECT p.*, c.libelle AS categorie_libelle FROM produit p " +
                     "JOIN categorie c ON p.categorie_id = c.id " +
                     "WHERE p.categorie_id = ? ORDER BY p.nom";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, categorieId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Produit produit = new Produit();
                produit.setId(rs.getInt("id"));
                produit.setNom(rs.getString("nom"));
                
                Categorie categorie = new Categorie();
                categorie.setId(rs.getInt("categorie_id"));
                categorie.setLibelle(rs.getString("categorie_libelle"));
                produit.setCategorie(categorie);
                
                produit.setPrixVente(rs.getDouble("prix_vente"));
                produit.setStockActuel(rs.getInt("stock_actuel"));
                produit.setSeuilAlerte(rs.getInt("seuil_alerte"));
                
                produits.add(produit);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des produits par catégorie : " + e.getMessage());
        }
        return produits;
    }

    // 7. Récupérer les produits en alerte (stock ≤ seuil)
    public List<Produit> getProduitsEnAlerte() {
        List<Produit> produits = new ArrayList<>();
        String sql = "SELECT p.*, c.libelle AS categorie_libelle FROM produit p " +
                     "JOIN categorie c ON p.categorie_id = c.id " +
                     "WHERE p.stock_actuel <= p.seuil_alerte ORDER BY p.stock_actuel ASC";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Produit produit = new Produit();
                produit.setId(rs.getInt("id"));
                produit.setNom(rs.getString("nom"));
                
                Categorie categorie = new Categorie();
                categorie.setId(rs.getInt("categorie_id"));
                categorie.setLibelle(rs.getString("categorie_libelle"));
                produit.setCategorie(categorie);
                
                produit.setPrixVente(rs.getDouble("prix_vente"));
                produit.setStockActuel(rs.getInt("stock_actuel"));
                produit.setSeuilAlerte(rs.getInt("seuil_alerte"));
                
                produits.add(produit);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des produits en alerte : " + e.getMessage());
        }
        return produits;
    }

    // 8. Mettre à jour le stock d'un produit
    public boolean updateStock(int produitId, int nouvelleQuantite) {
        String sql = "UPDATE produit SET stock_actuel = ? WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, nouvelleQuantite);
            stmt.setInt(2, produitId);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour du stock : " + e.getMessage());
        }
        return false;
    }

    // 9. Rechercher des produits par nom
    public List<Produit> searchProduits(String keyword) {
        List<Produit> produits = new ArrayList<>();
        String sql = "SELECT p.*, c.libelle AS categorie_libelle FROM produit p " +
                     "JOIN categorie c ON p.categorie_id = c.id " +
                     "WHERE p.nom LIKE ? ORDER BY p.nom";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, "%" + keyword + "%");
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Produit produit = new Produit();
                produit.setId(rs.getInt("id"));
                produit.setNom(rs.getString("nom"));
                
                Categorie categorie = new Categorie();
                categorie.setId(rs.getInt("categorie_id"));
                categorie.setLibelle(rs.getString("categorie_libelle"));
                produit.setCategorie(categorie);
                
                produit.setPrixVente(rs.getDouble("prix_vente"));
                produit.setStockActuel(rs.getInt("stock_actuel"));
                produit.setSeuilAlerte(rs.getInt("seuil_alerte"));
                
                produits.add(produit);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche des produits : " + e.getMessage());
        }
        return produits;
    }

    // 10. Fermer la connexion
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
