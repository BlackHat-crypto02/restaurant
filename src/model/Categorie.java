/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

public class Categorie {
    private int id;
    private String libelle;

    // Constructeur par défaut
    public Categorie() {
    }

    // Constructeur avec paramètres
    public Categorie(int id, String libelle) {
        this.id = id;
        this.libelle = libelle;
    }

    // Constructeur sans ID (pour l'insertion)
    public Categorie(String libelle) {
        this.libelle = libelle;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    // Méthode toString pour affichage
    @Override
    public String toString() {
        return libelle;
    }
}
