package com.pharmajava.tools;

import com.pharmajava.model.Pharmacien;
import com.pharmajava.dao.PharmacienDAO;
import com.pharmajava.utils.PasswordUtils;
import com.pharmajava.utils.DatabaseConfig;

import java.sql.Connection;

/**
 * Programme de test pour l'authentification dans PharmacieGestion
 * Permet de vérifier si le système d'authentification fonctionne correctement
 */
public class AuthenticationTest {

    public static void main(String[] args) {
        System.out.println("=== Test d'authentification PharmacieGestion ===");
        
        // 1. Vérifier la connexion à la base de données
        testDatabaseConnection();
        
        // 2. Tester l'algorithme de vérification des mots de passe
        testPasswordVerification();
        
        // 3. Tester l'authentification de l'utilisateur admin
        testAdminAuthentication();
    }
    
    private static void testDatabaseConnection() {
        System.out.println("\n1. Test de connexion à la base de données:");
        try {
            Connection connection = DatabaseConfig.getConnection();
            System.out.println("✅ Connexion à la base de données réussie!");
            
            // Afficher les informations de connexion
            System.out.println("   URL: " + connection.getMetaData().getURL());
            System.out.println("   Utilisateur: " + connection.getMetaData().getUserName());
            
            connection.close();
        } catch (Exception e) {
            System.out.println("❌ Échec de connexion à la base de données!");
            System.out.println("   Erreur: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void testPasswordVerification() {
        System.out.println("\n2. Test de l'algorithme de vérification des mots de passe:");
        
        // Mot de passe en clair et son hash connu
        String plainPassword = "admin123";
        String hashedPassword = "k2fzMJgfrPmsYdW/2Sfirw==:u0A3hQy5JXUjCVKTf2/mKADYjJiRlwNs+R5KQI1YtLQ=";
        
        // Tester la vérification du mot de passe
        boolean matches = PasswordUtils.verifyPassword(plainPassword, hashedPassword);
        
        if (matches) {
            System.out.println("✅ Vérification du mot de passe réussie!");
        } else {
            System.out.println("❌ Échec de vérification du mot de passe!");
            System.out.println("   Le mot de passe 'admin123' ne correspond pas au hash stocké.");
            
            // Tester le hashage d'un nouveau mot de passe
            String newHash = PasswordUtils.hashPassword(plainPassword);
            System.out.println("   Nouveau hash généré: " + newHash);
            System.out.println("   Vérification du nouveau hash: " + 
                    PasswordUtils.verifyPassword(plainPassword, newHash));
        }
    }
    
    private static void testAdminAuthentication() {
        System.out.println("\n3. Test d'authentification de l'utilisateur admin:");
        
        try {
            PharmacienDAO pharmacienDAO = new PharmacienDAO();
            
            // Vérifier si l'utilisateur admin existe
            Pharmacien admin = pharmacienDAO.obtenirParIdentifiant("admin");
            
            if (admin != null) {
                System.out.println("✅ Utilisateur admin trouvé dans la base de données!");
                System.out.println("   ID: " + admin.getId());
                System.out.println("   Nom complet: " + admin.getNomComplet());
                System.out.println("   Rôle: " + admin.getRole());
                
                // Tester l'authentification avec le mot de passe par défaut
                Pharmacien authenticated = pharmacienDAO.authentifier("admin", "admin123");
                
                if (authenticated != null) {
                    System.out.println("✅ Authentification réussie avec admin/admin123!");
                } else {
                    System.out.println("❌ Échec d'authentification avec admin/admin123!");
                    
                    // Tester la vérification directe du mot de passe
                    boolean passwordMatches = PasswordUtils.verifyPassword(
                            "admin123", admin.getMotDePasse());
                    
                    System.out.println("   Vérification directe du mot de passe: " + 
                            (passwordMatches ? "réussie" : "échouée"));
                    System.out.println("   Hash stocké: " + admin.getMotDePasse());
                }
            } else {
                System.out.println("❌ Utilisateur admin non trouvé dans la base de données!");
                
                // Vérifier le nombre de pharmaciens dans la base
                System.out.println("   Recherche d'autres utilisateurs...");
                java.util.List<Pharmacien> pharmaciens = pharmacienDAO.obtenirTous();
                
                if (pharmaciens.isEmpty()) {
                    System.out.println("   Aucun utilisateur trouvé dans la base de données!");
                } else {
                    System.out.println("   " + pharmaciens.size() + " utilisateur(s) trouvé(s):");
                    for (Pharmacien p : pharmaciens) {
                        System.out.println("     - " + p.getIdentifiant() + " (" + p.getNomComplet() + ")");
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("❌ Erreur lors du test d'authentification!");
            System.out.println("   Erreur: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 