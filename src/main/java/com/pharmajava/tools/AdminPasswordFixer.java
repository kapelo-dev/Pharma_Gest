package com.pharmajava.tools;

import com.pharmajava.utils.DatabaseConfig;
import com.pharmajava.utils.PasswordUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Outil pour corriger le mot de passe administrateur directement
 * Solution pour les personnes rencontrant des problèmes avec les scripts .bat
 */
public class AdminPasswordFixer {

    public static void main(String[] args) {
        System.out.println("=== Correction du mot de passe administrateur ===");
        
        // Générer un nouveau hash pour admin123
        String newPassword = "admin123";
        String newHash = PasswordUtils.hashPassword(newPassword);
        
        System.out.println("Nouveau hash généré pour 'admin123': " + newHash);
        
        try (Connection conn = DatabaseConfig.getConnection()) {
            // Vérifier si l'utilisateur admin existe
            boolean adminExists = false;
            
            try (PreparedStatement pstmt = conn.prepareStatement("SELECT id FROM pharmaciens WHERE identifiant = ?")) {
                pstmt.setString(1, "admin");
                try (ResultSet rs = pstmt.executeQuery()) {
                    adminExists = rs.next();
                }
            }
            
            if (adminExists) {
                // Mettre à jour le mot de passe admin existant
                try (PreparedStatement pstmt = conn.prepareStatement(
                        "UPDATE pharmaciens SET mot_de_passe = ? WHERE identifiant = ?")) {
                    pstmt.setString(1, newHash);
                    pstmt.setString(2, "admin");
                    int rows = pstmt.executeUpdate();
                    
                    System.out.println("\nMot de passe admin mis à jour avec succès!");
                    System.out.println("Lignes modifiées: " + rows);
                }
            } else {
                // Créer un nouvel utilisateur admin
                try (PreparedStatement pstmt = conn.prepareStatement(
                        "INSERT INTO pharmaciens (nom, prenom, identifiant, telephone, mot_de_passe, role) " +
                        "VALUES (?, ?, ?, ?, ?, ?)")) {
                    pstmt.setString(1, "Admin");
                    pstmt.setString(2, "System");
                    pstmt.setString(3, "admin");
                    pstmt.setString(4, "123456789");
                    pstmt.setString(5, newHash);
                    pstmt.setString(6, "ADMIN");
                    
                    int rows = pstmt.executeUpdate();
                    System.out.println("\nNouvel utilisateur admin créé avec succès!");
                    System.out.println("Lignes insérées: " + rows);
                }
            }
            
            // Afficher le message de succès
            System.out.println("\n=== Correction terminée ===");
            System.out.println("Vous pouvez maintenant vous connecter avec:");
            System.out.println("Identifiant: admin");
            System.out.println("Mot de passe: admin123");
            
        } catch (Exception e) {
            System.err.println("Erreur lors de la correction du mot de passe:");
            e.printStackTrace();
        }
    }
} 