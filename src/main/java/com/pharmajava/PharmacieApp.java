package com.pharmajava;

import com.formdev.flatlaf.FlatLightLaf;
import com.pharmajava.utils.DatabaseConfig;
import com.pharmajava.view.LoginView;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Classe principale de l'application de gestion de pharmacie
 */
public class PharmacieApp {
    
    /**
     * Point d'entrée principal de l'application
     * @param args arguments de ligne de commande (non utilisés)
     */
    public static void main(String[] args) {
        // Configuration du Look and Feel moderne
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Vérification de la connexion à la base de données
        try (Connection ignored = DatabaseConfig.getConnection()) {
            System.out.println("Connexion à la base de données réussie !");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, 
                "Impossible de se connecter à la base de données : " + e.getMessage(), 
                "Erreur de connexion", 
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            System.exit(1);
        }
        
        // Lancement de l'interface utilisateur
        EventQueue.invokeLater(() -> {
            try {
                LoginView loginView = new LoginView();
                loginView.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
} 