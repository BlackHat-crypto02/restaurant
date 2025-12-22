/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import model.Commande;
import model.LigneCommande;
import model.Produit;
import model.Utilisateur;
import utils.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommandeDAO {
    private Connection connection;
    private LigneCommandeDAO ligneCommandeDAO;
    private ProduitDAO produitDAO;

    public CommandeDAO() {
        this.connection = DatabaseConnection.getConnection();
        this.ligneCommandeDAO = new LigneCommandeDAO();
        this.produitDAO = new ProduitDAO();
    }

    // 1. Créer une nouvelle commande
    public boolean createCommande(Commande commande) {
        String sql = "INSERT INTO commande (etat, total, utilisateur_id) VALUES (?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, commande.getEtat());
            stmt.setDouble(2, commande.getTotal());
            stmt.setInt(3, 1); // ID de l'utilisateur par défaut (à adapter)
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int commandeId = generatedKeys.getInt(1);
                    commande.setId(commandeId);
                    
                    // Récupérer la date de la commande
                    String dateSql = "SELECT date_commande FROM commande WHERE id = ?";
                    try (PreparedStatement dateStmt = connection.prepareStatement(dateSql)) {
                        dateStmt.setInt(1, commandeId);
                        ResultSet rs = dateStmt.executeQuery();
                        if (rs.next()) {
                            commande.setDateCommande(rs.getTimestamp("date_commande"));
                        }
                    }
                    
                    // Ajouter les lignes de commande si elles existent
                    if (commande.getLignes() != null && !commande.getLignes().isEmpty()) {
                        for (LigneCommande ligne : commande.getLignes()) {
                            ligne.setCommande(commande);
                            ligneCommandeDAO.addLigneCommande(ligne);
                        }
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la création de la commande : " + e.getMessage());
        }
        return false;
    }

    // 2. Mettre à jour l'état d'une commande
    public boolean updateCommandeEtat(int commandeId, String nouvelEtat) {
        String sql = "UPDATE commande SET etat = ? WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, nouvelEtat);
            stmt.setInt(2, commandeId);
            
            // Si la commande est annulée, remettre les produits en stock
            if (Commande.ETAT_ANNULEE.equals(nouvelEtat)) {
                restituerStockCommande(commandeId);
            }
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour de la commande : " + e.getMessage());
        }
        return false;
    }

    // 3. Restituer le stock d'une commande annulée
    private void restituerStockCommande(int commandeId) {
        List<LigneCommande> lignes = ligneCommandeDAO.getLignesByCommande(commandeId);
        
        for (LigneCommande ligne : lignes) {
            Produit produit = produitDAO.getProduitById(ligne.getProduit().getId());
            int nouveauStock = produit.getStockActuel() + ligne.getQuantite();
            produitDAO.updateStock(produit.getId(), nouveauStock);
            
            // Enregistrer un mouvement de stock (réintégration)
            MouvementStockDAO mouvementDAO = new MouvementStockDAO();
            // (Tu dois créer une méthode pour ajouter ce mouvement si besoin)
        }
    }

    // 4. Récupérer une commande par ID (avec ses lignes)
    public Commande getCommandeById(int id) {
        String sql = "SELECT * FROM commande WHERE id = ?";
        Commande commande = null;
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                commande = new Commande();
                commande.setId(rs.getInt("id"));
                commande.setDateCommande(rs.getTimestamp("date_commande"));
                commande.setEtat(rs.getString("etat"));
                commande.setTotal(rs.getDouble("total"));
                
                // Récupérer les lignes de commande
                List<LigneCommande> lignes = ligneCommandeDAO.getLignesByCommande(id);
                commande.setLignes(lignes);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération de la commande : " + e.getMessage());
        }
        return commande;
    }

    // 5. Récupérer toutes les commandes
    public List<Commande> getAllCommandes() {
        List<Commande> commandes = new ArrayList<>();
        String sql = "SELECT * FROM commande ORDER BY date_commande DESC";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Commande commande = new Commande();
                commande.setId(rs.getInt("id"));
                commande.setDateCommande(rs.getTimestamp("date_commande"));
                commande.setEtat(rs.getString("etat"));
                commande.setTotal(rs.getDouble("total"));
                
                commandes.add(commande);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des commandes : " + e.getMessage());
        }
        return commandes;
    }

    // 6. Récupérer les commandes par état
    public List<Commande> getCommandesByEtat(String etat) {
        List<Commande> commandes = new ArrayList<>();
        String sql = "SELECT * FROM commande WHERE etat = ? ORDER BY date_commande DESC";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, etat);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Commande commande = new Commande();
                commande.setId(rs.getInt("id"));
                commande.setDateCommande(rs.getTimestamp("date_commande"));
                commande.setEtat(rs.getString("etat"));
                commande.setTotal(rs.getDouble("total"));
                
                commandes.add(commande);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des commandes par état : " + e.getMessage());
        }
        return commandes;
    }

    // 7. Calculer le chiffre d'affaires par jour
    public double getChiffreAffairesParJour(Date date) {
        String sql = "SELECT SUM(total) AS ca FROM commande WHERE DATE(date_commande) = ? AND etat = 'VALIDÉE'";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, new java.sql.Date(date.getTime()));
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getDouble("ca");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors du calcul du CA : " + e.getMessage());
        }
        return 0.0;
    }

    // 8. Calculer le chiffre d'affaires sur une période
    public double getChiffreAffairesPeriode(Date dateDebut, Date dateFin) {
        String sql = "SELECT SUM(total) AS ca FROM commande WHERE date_commande BETWEEN ? AND ? AND etat = 'VALIDÉE'";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, new java.sql.Date(dateDebut.getTime()));
            stmt.setDate(2, new java.sql.Date(dateFin.getTime()));
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getDouble("ca");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors du calcul du CA sur période : " + e.getMessage());
        }
        return 0.0;
    }

    // 9. Mettre à jour le total d'une commande
    public boolean updateCommandeTotal(int commandeId, double nouveauTotal) {
        String sql = "UPDATE commande SET total = ? WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDouble(1, nouveauTotal);
            stmt.setInt(2, commandeId);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour du total : " + e.getMessage());
        }
        return false;
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
