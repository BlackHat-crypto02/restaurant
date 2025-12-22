/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.util.Date;
import java.util.List;

public class Commande {
    private int id;
    private Date dateCommande;
    private String etat; // "EN_COURS", "VALIDÉE", "ANNULÉE"
    private double total;
    private List<LigneCommande> lignes;

    // Constantes pour les états
    public static final String ETAT_EN_COURS = "EN_COURS";
    public static final String ETAT_VALIDEE = "VALIDÉE";
    public static final String ETAT_ANNULEE = "ANNULÉE";

    // Constructeurs
    public Commande() {
        this.dateCommande = new Date();
        this.etat = ETAT_EN_COURS;
        this.total = 0.0;
    }

    public Commande(int id, Date dateCommande, String etat, double total) {
        this.id = id;
        this.dateCommande = dateCommande;
        this.etat = etat;
        this.total = total;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDateCommande() {
        return dateCommande;
    }

    public void setDateCommande(Date dateCommande) {
        this.dateCommande = dateCommande;
    }

    public String getEtat() {
        return etat;
    }

    public void setEtat(String etat) {
        this.etat = etat;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public List<LigneCommande> getLignes() {
        return lignes;
    }

    public void setLignes(List<LigneCommande> lignes) {
        this.lignes = lignes;
    }

    // Méthode pour ajouter une ligne et recalculer le total
    public void ajouterLigne(LigneCommande ligne) {
        this.lignes.add(ligne);
        recalculerTotal();
    }

    // Méthode pour recalculer le total
    public void recalculerTotal() {
        this.total = 0.0;
        if (lignes != null) {
            for (LigneCommande ligne : lignes) {
                this.total += ligne.getMontantLigne();
            }
        }
    }

    @Override
    public String toString() {
        return "Commande #" + id + " - " + dateCommande + " - " + etat + " - Total: " + total + " FCFA";
    }
}
