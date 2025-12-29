/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package view;

import controller.*;
import model.Utilisateur;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import java.awt.Color;
import java.awt.Cursor;
import javax.swing.BorderFactory;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
/**
 *
 * @author logos
 */
public class MainMenuForm extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(MainMenuForm.class.getName());
    private Utilisateur utilisateur;
    private ProduitController produitController;
    private CategorieController categorieController;
    private StockController stockController;
    private CommandeController commandeController;
    private StatistiqueController statistiqueController;
    /**
     * Creates new form MainMenuForm
     */
    public MainMenuForm() {
        initComponents();
        customizeInterface();
        setupControllers();
    }
    
    public MainMenuForm(Utilisateur utilisateur) {
        this();
        if (utilisateur != null) {
            this.utilisateur = utilisateur;
            setWelcomeMessage(utilisateur.getLogin());
        }
    }
    
    private void setupControllers() {
        produitController = new ProduitController();
        categorieController = new CategorieController();
        stockController = new StockController();
        commandeController = new CommandeController();
        statistiqueController = new StatistiqueController();
    }
    
    private void customizeInterface() {
        // Personnaliser les boutons de la toolbar
        customizeToolbarButtons();
        
        // Personnaliser les boutons principaux
        customizeMainButtons();
        
        // Personnaliser le message de bienvenue
        if (utilisateur != null) {
            setWelcomeMessage(utilisateur.getLogin());
        } else {
            setWelcomeMessage("Administrateur");
        }
        
        // Personnaliser le label de statut avec date/heure
        updateStatusBar();
        
        // Ajouter un listener pour la fermeture de la fenêtre
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                cleanup();
            }
        });
    }
    
    private void customizeToolbarButtons() {
        javax.swing.JButton[] toolbarButtons = {jbproduit, jbstock, jbcommande, jbstats};
        String[] emojis = {"📦", "📊", "🛒", "📈"};
        String[] tooltips = {
            "Gestion des produits - Ajouter/modifier/supprimer des produits",
            "Gestion du stock - Voir et gérer les niveaux de stock",
            "Commandes - Créer et gérer les commandes clients",
            "Statistiques - Voir les rapports et analyses"
        };
        
        for (int i = 0; i < toolbarButtons.length; i++) {
            javax.swing.JButton btn = toolbarButtons[i];
            btn.setText(emojis[i] + " " + btn.getText());
            btn.setToolTipText(tooltips[i]);
            btn.setBackground(new Color(240, 248, 255)); // AliceBlue
            btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(70, 130, 180), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
            ));
            btn.setFocusPainted(false);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            // Effet hover
            btn.addMouseListener(new java.awt.event.MouseAdapter() {
                Color originalColor = btn.getBackground();
                @Override
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    btn.setBackground(new Color(173, 216, 230)); // LightBlue
                    btn.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(30, 144, 255), 2),
                        BorderFactory.createEmptyBorder(5, 10, 5, 10)
                    ));
                }
                @Override
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    btn.setBackground(originalColor);
                    btn.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(70, 130, 180), 1),
                        BorderFactory.createEmptyBorder(5, 10, 5, 10)
                    ));
                }
            });
        }
    }
    
    private void customizeMainButtons() {
        javax.swing.JButton[] mainButtons = {jbgestionproduit, jbgestionstock, jbprisecommande, jbstatistique};
        
        // Couleurs pour chaque bouton
        Color[] colors = {
            new Color(26, 82, 118),   // Bleu foncé pour produits
            new Color(39, 174, 96),   // Vert pour stock
            new Color(211, 84, 0),    // Orange pour commandes
            new Color(142, 68, 173)   // Violet pour stats
        };
        
        for (int i = 0; i < mainButtons.length; i++) {
            javax.swing.JButton btn = mainButtons[i];
            btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(colors[i], 2),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
            ));
            btn.setFocusPainted(false);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            // Effet hover
            final int index = i;
            btn.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    btn.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(colors[index].darker(), 3),
                        BorderFactory.createEmptyBorder(10, 10, 10, 10)
                    ));
                }
                @Override
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    btn.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(colors[index], 2),
                        BorderFactory.createEmptyBorder(10, 10, 10, 10)
                    ));
                }
            });
        }
    }
    
    public void setWelcomeMessage(String username) {
        jlbienvenue.setText("BIENVENUE, " + username.toUpperCase() + " !");
    }
    
    private void updateStatusBar() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String timestamp = sdf.format(new Date());
        String username = utilisateur != null ? utilisateur.getLogin() : "admin";
        String statusText = "Connecté en tant que: " + username + 
                           " | Restaurant Management System v1.0 | " + 
                           timestamp;
        jlabelstatut.setText(statusText);
        
        // Actualiser toutes les secondes
        javax.swing.Timer timer = new javax.swing.Timer(1000, e -> {
            String newTimestamp = sdf.format(new Date());
            String newStatusText = "Connecté en tant que: " + username + 
                                 " | Restaurant Management System v1.0 | " + 
                                 newTimestamp;
            jlabelstatut.setText(newStatusText);
        });
        timer.start();
    }
    
    private void openGestionProduits() {
        logger.info("Ouverture de la gestion des produits");
        try {
            // Créer et afficher le formulaire de gestion des produits
            GestionProduitsForm produitsForm = new GestionProduitsForm();
            produitsForm.setLocationRelativeTo(this);
            produitsForm.setVisible(true);
            logger.info("Formulaire GestionProduitsForm ouvert avec succès");
        } catch (Exception e) {
            logger.severe("Erreur lors de l'ouverture de GestionProduitsForm: " + e.getMessage());
            JOptionPane.showMessageDialog(this,
                "Erreur lors de l'ouverture de la gestion des produits:\n" + e.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void openGestionStock() {
        logger.info("Ouverture de la gestion du stock");
        try {
            GestionStockForm stockForm = new GestionStockForm();
            stockForm.setLocationRelativeTo(this);
            stockForm.setVisible(true);
            logger.info("Formulaire GestionStockForm ouvert avec succès");
        } catch (Exception e) {
            logger.severe("Erreur lors de l'ouverture de GestionStockForm: " + e.getMessage());
            JOptionPane.showMessageDialog(this,
                "Erreur lors de l'ouverture de la gestion du stock:\n" + e.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void openPriseCommandes() {
        logger.info("Ouverture de la prise de commandes");
        try {
            PriseCommandesForm commandesForm = new PriseCommandesForm(utilisateur);
            commandesForm.setLocationRelativeTo(this);
            commandesForm.setVisible(true);
            logger.info("Formulaire PriseCommandesForm ouvert avec succès");
        } catch (Exception e) {
            logger.severe("Erreur lors de l'ouverture de PriseCommandesForm: " + e.getMessage());
            JOptionPane.showMessageDialog(this,
                "Erreur lors de l'ouverture de la prise de commandes:\n" + e.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void openStatistiques() {
        logger.info("Ouverture des statistiques");
        try {
            StatistiquesForm statsForm = new StatistiquesForm();
            statsForm.setLocationRelativeTo(this);
            statsForm.setVisible(true);
            logger.info("Formulaire StatistiquesForm ouvert avec succès");
        } catch (Exception e) {
            logger.severe("Erreur lors de l'ouverture de StatistiquesForm: " + e.getMessage());
            JOptionPane.showMessageDialog(this,
                "Erreur lors de l'ouverture des statistiques:\n" + e.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    } 
    
     private void openGestionCategories() {
        logger.info("Ouverture de la gestion des catégories");
        try {
            GestionCategoriesForm categoriesForm = new GestionCategoriesForm();
            categoriesForm.setLocationRelativeTo(this);
            categoriesForm.setVisible(true);
            logger.info("Formulaire GestionCategoriesForm ouvert avec succès");
        } catch (Exception e) {
            logger.severe("Erreur lors de l'ouverture de GestionCategoriesForm: " + e.getMessage());
            JOptionPane.showMessageDialog(this,
                "Erreur lors de l'ouverture de la gestion des catégories:\n" + e.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }
     
    private void openChiffreAffaires() {
        logger.info("Affichage du chiffre d'affaires");
        try {
            // Ouvre le formulaire des statistiques avec l'onglet chiffre d'affaires sélectionné
            StatistiquesForm statsForm = new StatistiquesForm();
            statsForm.setLocationRelativeTo(this);
            statsForm.showChiffreAffairesTab();
            statsForm.setVisible(true);
            logger.info("Affichage du chiffre d'affaires avec succès");
        } catch (Exception e) {
            logger.severe("Erreur lors de l'affichage du chiffre d'affaires: " + e.getMessage());
            JOptionPane.showMessageDialog(this,
                "Erreur lors de l'affichage du chiffre d'affaires:\n" + e.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void openTopProduits() {
        logger.info("Affichage des top produits");
        try {
            // Ouvre le formulaire des statistiques avec l'onglet top produits sélectionné
            StatistiquesForm statsForm = new StatistiquesForm();
            statsForm.setLocationRelativeTo(this);
            statsForm.showTopProduitsTab();
            statsForm.setVisible(true);
            logger.info("Affichage des top produits avec succès");
        } catch (Exception e) {
            logger.severe("Erreur lors de l'affichage des top produits: " + e.getMessage());
            JOptionPane.showMessageDialog(this,
                "Erreur lors de l'affichage des top produits:\n" + e.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showFeatureMessage(String title, String message) {
        JOptionPane.showMessageDialog(this, 
            message,
            title,
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void deconnecter() {
        int response = JOptionPane.showConfirmDialog(this,
            "Êtes-vous sûr de vouloir vous déconnecter?\nTous les formulaires ouverts seront fermés.",
            "Déconnexion",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (response == JOptionPane.YES_OPTION) {
            logger.info("Déconnexion de l'utilisateur: " + 
                (utilisateur != null ? utilisateur.getLogin() : "admin"));
            
            // Nettoyer les ressources
            cleanup();
            
            // Fermer cette fenêtre
            dispose();
            
            // Retour à l'écran de connexion
            java.awt.EventQueue.invokeLater(() -> {
                new LoginForm().setVisible(true);
            });
        }
    }
    
    private void quitter() {
        int response = JOptionPane.showConfirmDialog(this,
            "Êtes-vous sûr de vouloir quitter l'application?\nToutes les données non sauvegardées seront perdues.",
            "Quitter",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (response == JOptionPane.YES_OPTION) {
            logger.info("Fermeture de l'application");
            
            // Nettoyer les ressources
            cleanup();
            
            // Fermer l'application
            System.exit(0);
        }
    }
    
    private void afficherAPropos() {
        String username = utilisateur != null ? utilisateur.getLogin() : "Administrateur";
        JOptionPane.showMessageDialog(this,
            "<html><div style='text-align: center;'>"
            + "<h2>Restaurant Management System</h2>"
            + "<p><b>Version :</b> 1.0</p>"
            + "<p><b>Développé par :</b> " + System.getProperty("user.name") + "</p>"
            + "<p><b>Pour :</b> TP POO Java - IAI Togo</p>"
            + "<p><b>Année académique :</b> 2025-2026</p>"
            + "<p><b>Utilisateur connecté :</b> " + username + "</p>"
            + "<hr>"
            + "<p>© 2025 - Tous droits réservés</p>"
            + "</div></html>",
            "À propos",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    
    private void cleanup() {
        logger.info("Nettoyage des ressources du menu principal");
        // Ici tu pourrais fermer les connexions à la base si nécessaire
        // ou sauvegarder des données temporaires
    }
    
    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
        if (utilisateur != null) {
            setWelcomeMessage(utilisateur.getLogin());
            updateStatusBar();
        }
    }
    
    public Utilisateur getUtilisateur() {
        return utilisateur;
    }
    
    public void refreshInterface() {
        customizeInterface();
        repaint();
        revalidate();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSeparator7 = new javax.swing.JSeparator();
        jtoolbar = new javax.swing.JToolBar();
        jbproduit = new javax.swing.JButton();
        jSeparator6 = new javax.swing.JToolBar.Separator();
        jbstock = new javax.swing.JButton();
        jSeparator8 = new javax.swing.JToolBar.Separator();
        jbcommande = new javax.swing.JButton();
        jSeparator9 = new javax.swing.JToolBar.Separator();
        jbstats = new javax.swing.JButton();
        jpcentre = new javax.swing.JPanel();
        jpbienvenue = new javax.swing.JPanel();
        jlbienvenue = new javax.swing.JLabel();
        jpbouton = new javax.swing.JPanel();
        jbgestionproduit = new javax.swing.JButton();
        jbgestionstock = new javax.swing.JButton();
        jbprisecommande = new javax.swing.JButton();
        jbstatistique = new javax.swing.JButton();
        jpstatus = new javax.swing.JPanel();
        jlabelstatut = new javax.swing.JLabel();
        jmbmenu = new javax.swing.JMenuBar();
        jmfichier = new javax.swing.JMenu();
        jmideconnexion = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        jmiquitter = new javax.swing.JMenuItem();
        jmgestion = new javax.swing.JMenu();
        jmigestionproduit = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        jmigestioncategorie = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        jmigestionstock = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JPopupMenu.Separator();
        jmiprisecommande = new javax.swing.JMenuItem();
        jmstatistiques = new javax.swing.JMenu();
        jmichiffresaffaires = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JPopupMenu.Separator();
        jmitopproduits = new javax.swing.JMenuItem();
        jmaide = new javax.swing.JMenu();
        jmiapropos = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Menu Principal - Restaurant Management");

        jtoolbar.setRollover(true);

        jbproduit.setText("Produits");
        jbproduit.setToolTipText("Gestion des produits");
        jbproduit.setFocusable(false);
        jbproduit.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jbproduit.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jbproduit.addActionListener(this::jbproduitActionPerformed);
        jtoolbar.add(jbproduit);
        jtoolbar.add(jSeparator6);

        jbstock.setText("Stock");
        jbstock.setFocusable(false);
        jbstock.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jbstock.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jbstock.addActionListener(this::jbstockActionPerformed);
        jtoolbar.add(jbstock);
        jtoolbar.add(jSeparator8);

        jbcommande.setText("Commandes");
        jbcommande.setFocusable(false);
        jbcommande.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jbcommande.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jbcommande.addActionListener(this::jbcommandeActionPerformed);
        jtoolbar.add(jbcommande);
        jtoolbar.add(jSeparator9);

        jbstats.setText("Stats");
        jbstats.setFocusable(false);
        jbstats.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jbstats.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jbstats.addActionListener(this::jbstatsActionPerformed);
        jtoolbar.add(jbstats);

        jpcentre.setBackground(new java.awt.Color(0, 153, 153));
        jpcentre.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jpcentre.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N

        jpbienvenue.setToolTipText("");

        jlbienvenue.setBackground(new java.awt.Color(51, 51, 255));
        jlbienvenue.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        jlbienvenue.setText("BIENVENUE");

        javax.swing.GroupLayout jpbienvenueLayout = new javax.swing.GroupLayout(jpbienvenue);
        jpbienvenue.setLayout(jpbienvenueLayout);
        jpbienvenueLayout.setHorizontalGroup(
            jpbienvenueLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpbienvenueLayout.createSequentialGroup()
                .addContainerGap(236, Short.MAX_VALUE)
                .addComponent(jlbienvenue, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(208, 208, 208))
        );
        jpbienvenueLayout.setVerticalGroup(
            jpbienvenueLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpbienvenueLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jlbienvenue)
                .addContainerGap())
        );

        jbgestionproduit.setText("<html> <center> <div style='color:#1a5276; font-size:28px;'>📦</div> <b style='color:#1a5276; font-size:12px;'>GESTION</b><br> <small style='color:#2e86c1; font-size:10px;'>PRODUITS</small> </center> </html>");
        jbgestionproduit.addActionListener(this::jbgestionproduitActionPerformed);

        jbgestionstock.setText("<html> <center> <div style='color:#27ae60; font-size:28px;'>📊</div> <b style='color:#27ae60; font-size:12px;'>GESTION</b><br> <small style='color:#2ecc71; font-size:10px;'>STOCK</small> </center> </html>");
        jbgestionstock.addActionListener(this::jbgestionstockActionPerformed);

        jbprisecommande.setText("<html> <center> <div style='color:#d35400; font-size:28px;'>🛒</div> <b style='color:#d35400; font-size:12px;'>PRISE DE</b><br> <small style='color:#e67e22; font-size:10px;'>COMMANDES</small> </center> </html>");
        jbprisecommande.addActionListener(this::jbprisecommandeActionPerformed);

        jbstatistique.setText("<html> <center> <div style='color:#8e44ad; font-size:28px;'>📈</div> <b style='color:#8e44ad; font-size:12px;'>STATS &</b><br> <small style='color:#9b59b6; font-size:10px;'>RAPPORTS</small> </center> </html>");
        jbstatistique.addActionListener(this::jbstatistiqueActionPerformed);

        javax.swing.GroupLayout jpboutonLayout = new javax.swing.GroupLayout(jpbouton);
        jpbouton.setLayout(jpboutonLayout);
        jpboutonLayout.setHorizontalGroup(
            jpboutonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpboutonLayout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addGroup(jpboutonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jbgestionproduit)
                    .addComponent(jbprisecommande, javax.swing.GroupLayout.DEFAULT_SIZE, 147, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jpboutonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jbgestionstock, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jbstatistique, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(43, 43, 43))
        );
        jpboutonLayout.setVerticalGroup(
            jpboutonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpboutonLayout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(jpboutonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jbgestionproduit)
                    .addComponent(jbgestionstock))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 46, Short.MAX_VALUE)
                .addGroup(jpboutonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jbprisecommande, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jbstatistique, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(17, 17, 17))
        );

        jpstatus.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jlabelstatut.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jlabelstatut.setText("Connecté en tant que: admin | Restaurant Management System v1.0");

        javax.swing.GroupLayout jpstatusLayout = new javax.swing.GroupLayout(jpstatus);
        jpstatus.setLayout(jpstatusLayout);
        jpstatusLayout.setHorizontalGroup(
            jpstatusLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpstatusLayout.createSequentialGroup()
                .addGap(120, 120, 120)
                .addComponent(jlabelstatut, javax.swing.GroupLayout.PREFERRED_SIZE, 376, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(128, Short.MAX_VALUE))
        );
        jpstatusLayout.setVerticalGroup(
            jpstatusLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpstatusLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jlabelstatut))
        );

        javax.swing.GroupLayout jpcentreLayout = new javax.swing.GroupLayout(jpcentre);
        jpcentre.setLayout(jpcentreLayout);
        jpcentreLayout.setHorizontalGroup(
            jpcentreLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpcentreLayout.createSequentialGroup()
                .addGap(0, 14, Short.MAX_VALUE)
                .addComponent(jpbienvenue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(38, 38, 38))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpcentreLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jpcentreLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jpstatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jpbouton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18))
        );
        jpcentreLayout.setVerticalGroup(
            jpcentreLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpcentreLayout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(jpbienvenue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(175, 175, 175)
                .addComponent(jpbouton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jpstatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jmfichier.setText("Fichier");

        jmideconnexion.setText("Deconnexion");
        jmideconnexion.addActionListener(this::jmideconnexionActionPerformed);
        jmfichier.add(jmideconnexion);
        jmfichier.add(jSeparator1);

        jmiquitter.setText("Quitter");
        jmiquitter.addActionListener(this::jmiquitterActionPerformed);
        jmfichier.add(jmiquitter);

        jmbmenu.add(jmfichier);

        jmgestion.setText("Gestion");

        jmigestionproduit.setText("Gestion des Produits");
        jmigestionproduit.addActionListener(this::jmigestionproduitActionPerformed);
        jmgestion.add(jmigestionproduit);
        jmgestion.add(jSeparator2);

        jmigestioncategorie.setText("Gestion des Catégories");
        jmigestioncategorie.addActionListener(this::jmigestioncategorieActionPerformed);
        jmgestion.add(jmigestioncategorie);
        jmgestion.add(jSeparator3);

        jmigestionstock.setText("Gestion du Stock");
        jmigestionstock.addActionListener(this::jmigestionstockActionPerformed);
        jmgestion.add(jmigestionstock);
        jmgestion.add(jSeparator4);

        jmiprisecommande.setText("Prise de Commandes");
        jmiprisecommande.addActionListener(this::jmiprisecommandeActionPerformed);
        jmgestion.add(jmiprisecommande);

        jmbmenu.add(jmgestion);

        jmstatistiques.setText("Statistiques");

        jmichiffresaffaires.setText("Chiffre d'Affaires");
        jmichiffresaffaires.addActionListener(this::jmichiffresaffairesActionPerformed);
        jmstatistiques.add(jmichiffresaffaires);
        jmstatistiques.add(jSeparator5);

        jmitopproduits.setText("Top Produits");
        jmitopproduits.addActionListener(this::jmitopproduitsActionPerformed);
        jmstatistiques.add(jmitopproduits);

        jmbmenu.add(jmstatistiques);

        jmaide.setText("Aide");

        jmiapropos.setText("À propos");
        jmiapropos.addActionListener(this::jmiaproposActionPerformed);
        jmaide.add(jmiapropos);

        jmbmenu.add(jmaide);

        setJMenuBar(jmbmenu);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jtoolbar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jpcentre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(20, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jtoolbar, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jpcentre, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jbstatistiqueActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbstatistiqueActionPerformed
        // TODO add your handling code here:
        openStatistiques();
    }//GEN-LAST:event_jbstatistiqueActionPerformed

    private void jbproduitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbproduitActionPerformed
        // TODO add your handling code here:
         openGestionProduits();
    }//GEN-LAST:event_jbproduitActionPerformed

    private void jbstockActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbstockActionPerformed
        // TODO add your handling code here:
         openGestionStock();
    }//GEN-LAST:event_jbstockActionPerformed

    private void jbcommandeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbcommandeActionPerformed
        // TODO add your handling code here:
         openPriseCommandes();
    }//GEN-LAST:event_jbcommandeActionPerformed

    private void jbstatsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbstatsActionPerformed
        openStatistiques();
    }//GEN-LAST:event_jbstatsActionPerformed

    private void jbgestionproduitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbgestionproduitActionPerformed
        openGestionProduits();
    }//GEN-LAST:event_jbgestionproduitActionPerformed

    private void jbgestionstockActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbgestionstockActionPerformed
        openGestionStock();
    }//GEN-LAST:event_jbgestionstockActionPerformed

    private void jbprisecommandeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbprisecommandeActionPerformed
        openPriseCommandes();
    }//GEN-LAST:event_jbprisecommandeActionPerformed

    private void jmiquitterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmiquitterActionPerformed
        quitter();
    }//GEN-LAST:event_jmiquitterActionPerformed

    private void jmideconnexionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmideconnexionActionPerformed
        deconnecter();
    }//GEN-LAST:event_jmideconnexionActionPerformed

    private void jmigestionproduitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmigestionproduitActionPerformed
         openGestionProduits();
    }//GEN-LAST:event_jmigestionproduitActionPerformed

    private void jmigestioncategorieActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmigestioncategorieActionPerformed
        openGestionCategories();
    }//GEN-LAST:event_jmigestioncategorieActionPerformed

    private void jmigestionstockActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmigestionstockActionPerformed
         openGestionStock();
    }//GEN-LAST:event_jmigestionstockActionPerformed

    private void jmiprisecommandeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmiprisecommandeActionPerformed
        openPriseCommandes();
    }//GEN-LAST:event_jmiprisecommandeActionPerformed

    private void jmichiffresaffairesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmichiffresaffairesActionPerformed
        openChiffreAffaires();
    }//GEN-LAST:event_jmichiffresaffairesActionPerformed

    private void jmitopproduitsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmitopproduitsActionPerformed
        openTopProduits();
    }//GEN-LAST:event_jmitopproduitsActionPerformed

    private void jmiaproposActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmiaproposActionPerformed
        afficherAPropos();
    }//GEN-LAST:event_jmiaproposActionPerformed

    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
         /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        
        // Utiliser le look and feel système
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            logger.log(java.util.logging.Level.WARNING, null, e);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            // Créer un utilisateur de test pour la prévisualisation
            model.Utilisateur testUser = new model.Utilisateur();
            testUser.setId(1);
            testUser.setLogin("admin");
            
            MainMenuForm form = new MainMenuForm(testUser);
            form.setLocationRelativeTo(null); // Centrer la fenêtre
            form.setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JPopupMenu.Separator jSeparator4;
    private javax.swing.JPopupMenu.Separator jSeparator5;
    private javax.swing.JToolBar.Separator jSeparator6;
    private javax.swing.JSeparator jSeparator7;
    private javax.swing.JToolBar.Separator jSeparator8;
    private javax.swing.JToolBar.Separator jSeparator9;
    private javax.swing.JButton jbcommande;
    private javax.swing.JButton jbgestionproduit;
    private javax.swing.JButton jbgestionstock;
    private javax.swing.JButton jbprisecommande;
    private javax.swing.JButton jbproduit;
    private javax.swing.JButton jbstatistique;
    private javax.swing.JButton jbstats;
    private javax.swing.JButton jbstock;
    private javax.swing.JLabel jlabelstatut;
    private javax.swing.JLabel jlbienvenue;
    private javax.swing.JMenu jmaide;
    private javax.swing.JMenuBar jmbmenu;
    private javax.swing.JMenu jmfichier;
    private javax.swing.JMenu jmgestion;
    private javax.swing.JMenuItem jmiapropos;
    private javax.swing.JMenuItem jmichiffresaffaires;
    private javax.swing.JMenuItem jmideconnexion;
    private javax.swing.JMenuItem jmigestioncategorie;
    private javax.swing.JMenuItem jmigestionproduit;
    private javax.swing.JMenuItem jmigestionstock;
    private javax.swing.JMenuItem jmiprisecommande;
    private javax.swing.JMenuItem jmiquitter;
    private javax.swing.JMenuItem jmitopproduits;
    private javax.swing.JMenu jmstatistiques;
    private javax.swing.JPanel jpbienvenue;
    private javax.swing.JPanel jpbouton;
    private javax.swing.JPanel jpcentre;
    private javax.swing.JPanel jpstatus;
    private javax.swing.JToolBar jtoolbar;
    // End of variables declaration//GEN-END:variables
}
