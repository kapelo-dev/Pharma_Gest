-- Script pour ajouter la table clients et modifier la table ventes
-- pour lier les clients aux ventes en option
 -- Table des clients

CREATE TABLE IF NOT EXISTS clients
    ( id INT AUTO_INCREMENT PRIMARY KEY,
                                    nom VARCHAR(100) NOT NULL,
                                                     prenom VARCHAR(100) NOT NULL,
                                                                         telephone VARCHAR(20) NOT NULL,
                                                                                               email VARCHAR(100),
                                                                                                     adresse TEXT, date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                                                                                                                                   derniere_modification TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP);

-- Ajout d'une colonne client_id dans la table ventes (peut être NULL)

ALTER TABLE ventes ADD COLUMN client_id INT NULL,
                                            ADD CONSTRAINT fk_vente_client
FOREIGN KEY (client_id) REFERENCES clients(id) ON
DELETE
SET NULL;

-- Création d'un index sur client_id pour améliorer les performances

CREATE INDEX idx_ventes_client ON ventes(client_id);