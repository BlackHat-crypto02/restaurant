/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import view.LoginForm;
import dao.UtilisateurDAO;
import javax.swing.JOptionPane;

public class LoginController {
    private LoginForm view;
    private UtilisateurDAO utilisateurDAO;

    public LoginController(LoginForm view) {
        this.view = view;
        this.utilisateurDAO = new UtilisateurDAO();
    }

    public void authenticate() {
        // Récupérer les données du formulaire
        String login = view.getLogin();
        String password = view.getPassword();
        boolean rememberMe = view.isRememberChecked();

        // Validation
        if (login.isEmpty()) {
            view.showMessage("Veuillez saisir votre login", true);
            return;
        }
        
        if (password.isEmpty()) {
            view.showMessage("Veuillez saisir votre mot de passe", true);
            return;
        }

        try {
            System.out.println("Tentative de connexion avec login: " + login);
            
            // Authentifier l'utilisateur
            // NOTE: Pour l'instant, on accepte n'importe quel mot de passe pour tester
            // Tu modifieras ça plus tard avec la base de données
            
            // Test temporaire: accepter admin/admin
            if (login.equals("admin") && password.equals("admin")) {
                // Connexion réussie (version test)
                view.showMessage("Connexion réussie ! (mode test)", false);
                
                // Gérer "Se souvenir de moi"
                if (rememberMe) {
                    System.out.println("Préférence sauvegardée pour: " + login);
                }
                
                // Attendre un peu pour montrer le message
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // Ignorer
                }
                
                // Fermer le formulaire de connexion
                view.closeForm();
                
                // Ici, normalement on ouvrirait le menu principal
                // Mais comme on ne l'a pas encore créé, on affiche un message
                JOptionPane.showMessageDialog(null, 
                    "Connexion réussie !\nLe menu principal s'ouvrirait ici.\n\nLogin: " + login, 
                    "Succès", 
                    JOptionPane.INFORMATION_MESSAGE);
                
                System.out.println("Connexion réussie pour: " + login);
                
            } else {
                // Échec de connexion
                view.showMessage("Login ou mot de passe incorrect (test: admin/admin)", true);
                view.clearFields();
            }
            
        } catch (Exception e) {
            view.showMessage("Erreur: " + e.getMessage(), true);
            System.err.println("Erreur lors de l'authentification: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void cancel() {
        int confirm = JOptionPane.showConfirmDialog(
            view, 
            "Voulez-vous vraiment quitter l'application ?",
            "Confirmation",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            System.out.println("Application fermée par l'utilisateur");
            System.exit(0);
        } else {
            System.out.println("Annulation confirmée, retour au formulaire");
        }
    }
}
