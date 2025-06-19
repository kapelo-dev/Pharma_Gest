-- Script pour corriger le mot de passe de l'administrateur
-- Exécuter ce script pour réinitialiser le mot de passe admin à "admin123"

USE pharmacie_db;

-- Mise à jour du mot de passe admin avec un nouveau hash valide
-- Ce hash a été généré par le test d'authentification et fonctionne correctement
UPDATE pharmaciens 
SET mot_de_passe = 'L8C18xtPNjgG0Il19MWDjg==:l1EV1lgQv3syt7Kuo5DAunWJOcEtb7x5T8dytHjRhRw=' 
WHERE identifiant = 'admin';

-- Vérifier que la mise à jour a réussi
SELECT id, identifiant, nom, prenom, role FROM pharmaciens WHERE identifiant = 'admin';

-- Si aucune ligne n'est mise à jour (l'utilisateur admin n'existe pas), on le crée
INSERT INTO pharmaciens (nom, prenom, identifiant, telephone, mot_de_passe, role)
SELECT 'Admin', 'System', 'admin', '123456789', 'L8C18xtPNjgG0Il19MWDjg==:l1EV1lgQv3syt7Kuo5DAunWJOcEtb7x5T8dytHjRhRw=', 'ADMIN'
WHERE NOT EXISTS (SELECT 1 FROM pharmaciens WHERE identifiant = 'admin'); 