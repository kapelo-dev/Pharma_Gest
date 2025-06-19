-- Script de création de la base de données pour l'application de gestion de pharmacie
-- Ce script initialise toutes les tables nécessaires pour le système

CREATE DATABASE IF NOT EXISTS pharmacie_db;

USE pharmacie_db;

-- Table des utilisateurs (Pharmaciens)

CREATE TABLE IF NOT EXISTS pharmaciens
    (id INT AUTO_INCREMENT PRIMARY KEY,
                                   nom VARCHAR(100) NOT NULL,
                                                    prenom VARCHAR(100) NOT NULL,
                                                                        identifiant VARCHAR(50) NOT NULL UNIQUE,
                                                                                                         telephone VARCHAR(20) NOT NULL,
                                                                                                                               mot_de_passe VARCHAR(255) NOT NULL,
                                                                                                                                                         role VARCHAR(50) NOT NULL, -- 'ADMIN', 'PHARMACIEN', etc.
 date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                 derniere_modification TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP);

-- Table des clients

CREATE TABLE IF NOT EXISTS clients
    (id INT AUTO_INCREMENT PRIMARY KEY,
                                   nom VARCHAR(100) NOT NULL,
                                                    prenom VARCHAR(100) NOT NULL,
                                                                        telephone VARCHAR(20) NOT NULL,
                                                                                              email VARCHAR(100),
                                                                                                    adresse TEXT, date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                                                                                                                                  derniere_modification TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP);

-- Table des produits/médicaments

CREATE TABLE IF NOT EXISTS produits
    (id INT AUTO_INCREMENT PRIMARY KEY,
                                   nom VARCHAR(150) NOT NULL UNIQUE,
                                                             description TEXT NOT NULL,
                                                                              quantite_en_stock INT NOT NULL DEFAULT 0,
                                                                                                                     prix_unitaire DECIMAL(10,2) NOT NULL,
                                                                                                                                                 sur_ordonnance BOOLEAN NOT NULL DEFAULT FALSE,
                                                                                                                                                                                         date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                                                                                                                                                                                                         derniere_modification TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP);

-- Table des stocks (lots de produits avec dates d'expiration)

CREATE TABLE IF NOT EXISTS stock
    (id INT AUTO_INCREMENT PRIMARY KEY,
                                   produit_id INT NOT NULL,
                                                  lot_numero VARCHAR(100) NOT NULL,
                                                                          quantite_disponible INT NOT NULL DEFAULT 0,
                                                                                                                   date_expiration DATE NOT NULL,
                                                                                                                                        date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
     FOREIGN KEY (produit_id) REFERENCES produits(id) ON DELETE CASCADE);

-- Table des fournisseurs

CREATE TABLE IF NOT EXISTS fournisseurs (id INT AUTO_INCREMENT PRIMARY KEY,
                                                                       nom VARCHAR(150) NOT NULL,
                                                                                        adresse TEXT, telephone VARCHAR(20),
                                                                                                                email VARCHAR(100),
                                                                                                                      date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP);

-- Table des ravitaillements

CREATE TABLE IF NOT EXISTS ravitaillements
    (id INT AUTO_INCREMENT PRIMARY KEY,
                                   fournisseur_id INT, produit_id INT NOT NULL,
                                                                      lot_numero VARCHAR(100) NOT NULL,
                                                                                              quantite_ravitailler INT NOT NULL,
                                                                                                                       date_ravitaillement TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                                                                                                                                             date_expiration DATE NOT NULL,
     FOREIGN KEY (fournisseur_id) REFERENCES fournisseurs(id) ON DELETE
     SET NULL,
     FOREIGN KEY (produit_id) REFERENCES produits(id) ON DELETE CASCADE);

-- Table des ventes

CREATE TABLE IF NOT EXISTS ventes
    (id INT AUTO_INCREMENT PRIMARY KEY,
                                   date_vente TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                                                montant_total DECIMAL(10,2) NOT NULL,
                                                                                            montant_percu DECIMAL(10,2) NOT NULL,
                                                                                                                        montant_rendu DECIMAL(10,2) NOT NULL,
                                                                                                                                                    pharmacien_id INT, client_id INT, -- Ajout du champ client_id optionnel

     FOREIGN KEY (pharmacien_id) REFERENCES pharmaciens(id) ON DELETE
     SET NULL,
     FOREIGN KEY (client_id) REFERENCES clients(id) ON DELETE
     SET NULL);

-- Table des détails de vente (produits vendus)

CREATE TABLE IF NOT EXISTS produits_vendus
    (id INT AUTO_INCREMENT PRIMARY KEY,
                                   vente_id INT NOT NULL,
                                                produit_id INT NOT NULL,
                                                               quantite INT NOT NULL,
                                                                            prix_unitaire DECIMAL(10,2) NOT NULL,
                                                                                                        prix_total DECIMAL(10,2) NOT NULL,
     FOREIGN KEY (vente_id) REFERENCES ventes(id) ON DELETE CASCADE,
     FOREIGN KEY (produit_id) REFERENCES produits(id) ON DELETE RESTRICT);

-- Table des informations de la pharmacie

CREATE TABLE IF NOT EXISTS pharmacie_info
    (id INT PRIMARY KEY DEFAULT 1,
                                nom VARCHAR(150) NOT NULL,
                                                 adresse TEXT NOT NULL,
                                                              telephone1 VARCHAR(20) NOT NULL,
                                                                                     telephone2 VARCHAR(20),
                                                                                                email VARCHAR(100),
                                                                                                      logo_path VARCHAR(255),
                                                                                                                date_modification TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP);

-- Insertion des données initiales

INSERT INTO pharmacie_info (nom, adresse, telephone1, email)
VALUES ('Ma Pharmacie',
        'Adresse de la pharmacie',
        '123456789',
        'contact@pharmacie.com');

-- Créer un utilisateur admin par défaut avec un mot de passe hashé (admin123)
-- Le format est "sel:hash" généré par PasswordUtils.hashPassword

INSERT INTO pharmaciens (nom, prenom, identifiant, telephone, mot_de_passe, role)
VALUES ('Admin',
        'System',
        'admin',
        '123456789',
        'k2fzMJgfrPmsYdW/2Sfirw==:u0A3hQy5JXUjCVKTf2/mKADYjJiRlwNs+R5KQI1YtLQ=',
        'ADMIN');

-- Ajoutons quelques clients de test

INSERT INTO clients (nom, prenom, telephone, email, adresse)
VALUES ('Diallo',
        'Mamadou',
        '601234567',
        'mamadou.diallo@example.com',
        'Bamako, Mali'), ('Touré',
                          'Fatoumata',
                          '602345678',
                          'fatou.toure@example.com',
                          'Conakry, Guinée'), ('Koné',
                                               'Ibrahim',
                                               '603456789',
                                               'ibrahim.kone@example.com',
                                               'Abidjan, Côte d\'Ivoire');

-- Création d'un index sur client_id pour améliorer les performances des requêtes

CREATE INDEX idx_ventes_client ON ventes(client_id);


ALTER TABLE produits ADD COLUMN prix_achat DECIMAL(10,2) DEFAULT NULL,
                                                                 ADD COLUMN prix_vente DECIMAL(10,2) DEFAULT NULL,
                                                                                                             ADD COLUMN seuil_alerte INT DEFAULT 5;