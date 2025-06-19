package com.pharmajava.utils;

import com.pharmajava.model.Pharmacien;

/**
 * Gestionnaire de session pour l'application
 * Implémente le pattern Singleton
 */
public class SessionManager {
    private static SessionManager instance;
    private Pharmacien pharmacienConnecte;
    
    /**
     * Constructeur privé pour empêcher l'instanciation directe
     */
    private SessionManager() {
    }
    
    /**
     * Obtient l'instance unique de SessionManager
     * 
     * @return L'instance de SessionManager
     */
    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }
    
    /**
     * Obtient le pharmacien actuellement connecté
     * 
     * @return Le pharmacien connecté ou null si aucun
     */
    public Pharmacien getPharmacienConnecte() {
        return pharmacienConnecte;
    }
    
    /**
     * Définit le pharmacien connecté
     * 
     * @param pharmacien Le pharmacien à définir comme connecté
     */
    public void setPharmacienConnecte(Pharmacien pharmacien) {
        this.pharmacienConnecte = pharmacien;
    }
    
    /**
     * Vérifie si un utilisateur est connecté
     * 
     * @return true si un utilisateur est connecté, false sinon
     */
    public boolean estConnecte() {
        return pharmacienConnecte != null;
    }
    
    /**
     * Vérifie si l'utilisateur connecté a un rôle spécifique
     * 
     * @param role Le rôle à vérifier
     * @return true si l'utilisateur connecté a le rôle spécifié, false sinon
     */
    public boolean aRole(String role) {
        return estConnecte() && pharmacienConnecte.getRole().equals(role);
    }
} 