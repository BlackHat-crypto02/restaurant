-- ============================================
-- SCRIPT DE CRÉATION DE LA BASE DE DONNÉES
-- Restaurant Management System
-- ============================================

-- 1. Création de la base de données (si elle n'existe pas)
CREATE DATABASE IF NOT EXISTS restaurant_db;
USE restaurant_db;

-- ============================================
-- 2. Création des tables
-- ============================================

-- Table: CATEGORIE
CREATE TABLE IF NOT EXISTS categorie (
    id INT PRIMARY KEY AUTO_INCREMENT,
    libelle VARCHAR(100) NOT NULL UNIQUE,
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table: UTILISATEUR
CREATE TABLE IF NOT EXISTS utilisateur (
    id INT PRIMARY KEY AUTO_INCREMENT,
    login VARCHAR(50) NOT NULL UNIQUE,
    mot_de_passe VARCHAR(255) NOT NULL,
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table: PRODUIT
CREATE TABLE IF NOT EXISTS produit (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nom VARCHAR(100) NOT NULL,
    categorie_id INT NOT NULL,
    prix_vente DECIMAL(10, 2) NOT NULL CHECK (prix_vente > 0),
    stock_actuel INT DEFAULT 0 CHECK (stock_actuel >= 0),
    seuil_alerte INT DEFAULT 5 CHECK (seuil_alerte >= 0),
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (categorie_id) REFERENCES categorie(id) ON DELETE RESTRICT
);

-- Table: COMMANDE
CREATE TABLE IF NOT EXISTS commande (
    id INT PRIMARY KEY AUTO_INCREMENT,
    date_commande TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    etat ENUM('EN_COURS', 'VALIDÉE', 'ANNULÉE') DEFAULT 'EN_COURS',
    total DECIMAL(10, 2) DEFAULT 0.00,
    utilisateur_id INT,
    FOREIGN KEY (utilisateur_id) REFERENCES utilisateur(id) ON DELETE SET NULL
);

-- Table: LIGNE_COMMANDE
CREATE TABLE IF NOT EXISTS ligne_commande (
    id INT PRIMARY KEY AUTO_INCREMENT,
    commande_id INT NOT NULL,
    produit_id INT NOT NULL,
    quantite INT NOT NULL CHECK (quantite > 0),
    prix_unitaire DECIMAL(10, 2) NOT NULL,
    montant_ligne DECIMAL(10, 2) AS (quantite * prix_unitaire) STORED,
    FOREIGN KEY (commande_id) REFERENCES commande(id) ON DELETE CASCADE,
    FOREIGN KEY (produit_id) REFERENCES produit(id) ON DELETE RESTRICT
);

-- Table: MOUVEMENT_STOCK
CREATE TABLE IF NOT EXISTS mouvement_stock (
    id INT PRIMARY KEY AUTO_INCREMENT,
    produit_id INT NOT NULL,
    type ENUM('ENTRÉE', 'SORTIE') NOT NULL,
    quantite INT NOT NULL CHECK (quantite > 0),
    date_mouvement TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    motif VARCHAR(200),
    FOREIGN KEY (produit_id) REFERENCES produit(id) ON DELETE CASCADE
);

-- ============================================
-- 3. Insertion des données de test
-- ============================================

-- Insertion des catégories
INSERT INTO categorie (libelle) VALUES 
('Boissons'),
('Plats'),
('Desserts'),
('Snacks');

-- Insertion d'un utilisateur par défaut (mot de passe = "admin123")
INSERT INTO utilisateur (login, mot_de_passe) VALUES 
('admin', '$2y$10$YourHashedPasswordHere'); -- À changer avec un vrai hash

-- Insertion de quelques produits
INSERT INTO produit (nom, categorie_id, prix_vente, stock_actuel, seuil_alerte) VALUES
('Coca-Cola', 1, 1000.00, 50, 10),
('Eau minérale', 1, 500.00, 100, 20),
('Poulet Braisé', 2, 2500.00, 30, 5),
('Poisson Grillé', 2, 3000.00, 25, 5),
('Glace Vanille', 3, 800.00, 40, 10),
('Beignet', 4, 200.00, 100, 30);

-- Insertion d'une commande test
INSERT INTO commande (date_commande, etat, total, utilisateur_id) VALUES
(NOW(), 'VALIDÉE', 3500.00, 1);

-- Insertion de lignes de commande
INSERT INTO ligne_commande (commande_id, produit_id, quantite, prix_unitaire) VALUES
(1, 1, 2, 1000.00), -- 2 Coca-Cola
(1, 3, 1, 2500.00); -- 1 Poulet Braisé

-- Insertion de mouvements de stock
INSERT INTO mouvement_stock (produit_id, type, quantite, motif) VALUES
(1, 'ENTRÉE', 100, 'Achat initial'),
(3, 'ENTRÉE', 50, 'Achat initial'),
(1, 'SORTIE', 2, 'Vente commande #1');

-- ============================================
-- 4. Création des index pour améliorer les performances
-- ============================================

CREATE INDEX idx_produit_categorie ON produit(categorie_id);
CREATE INDEX idx_produit_nom ON produit(nom);
CREATE INDEX idx_commande_date ON commande(date_commande);
CREATE INDEX idx_commande_etat ON commande(etat);
CREATE INDEX idx_mouvement_produit ON mouvement_stock(produit_id);
CREATE INDEX idx_mouvement_date ON mouvement_stock(date_mouvement);

-- ============================================
-- 5. Création d'une vue pour les produits en alerte
-- ============================================

CREATE OR REPLACE VIEW vue_produits_alerte AS
SELECT 
    p.id,
    p.nom,
    c.libelle AS categorie,
    p.stock_actuel,
    p.seuil_alerte,
    CASE 
        WHEN p.stock_actuel = 0 THEN 'RUPTURE'
        WHEN p.stock_actuel <= p.seuil_alerte THEN 'ALERTE'
        ELSE 'NORMAL'
    END AS statut_stock
FROM produit p
JOIN categorie c ON p.categorie_id = c.id
WHERE p.stock_actuel <= p.seuil_alerte;

-- ============================================
-- 6. Création d'un déclencheur (trigger) pour mettre à jour le stock
-- ============================================

DELIMITER $$

CREATE TRIGGER after_mouvement_stock_insert
AFTER INSERT ON mouvement_stock
FOR EACH ROW
BEGIN
    IF NEW.type = 'ENTRÉE' THEN
        UPDATE produit 
        SET stock_actuel = stock_actuel + NEW.quantite 
        WHERE id = NEW.produit_id;
    ELSEIF NEW.type = 'SORTIE' THEN
        UPDATE produit 
        SET stock_actuel = stock_actuel - NEW.quantite 
        WHERE id = NEW.produit_id;
    END IF;
END$$

DELIMITER ;

-- ============================================
-- 7. Message de confirmation
-- ============================================
SELECT '✅ Base de données créée avec succès !' AS Message;





-- Change le mot de passe hashé pour l'utilisateur admin plus tard

-- Les triggers mettent à jour automatiquement le stock quand tu ajoutes un mouvement

-- La vue vue_produits_alerte te montre directement les produits à réapprovisionner