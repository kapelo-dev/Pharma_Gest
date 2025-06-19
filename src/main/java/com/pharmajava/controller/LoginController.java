package com.pharmajava.controller;

import com.pharmajava.dao.PharmacienDAO;
import com.pharmajava.model.Pharmacien;
import com.pharmajava.utils.SessionManager;

/**
 * Contrôleur pour gérer l'authentification des utilisateurs
 */
public class LoginController {
    private final PharmacienDAO pharmacienDAO;
    
    /**
     * Constructeur du contrôleur de connexion
     */
    public LoginController() {
        this.pharmacienDAO = new PharmacienDAO();
    }
    
    /**
     * Authentifie un utilisateur avec son identifiant et mot de passe
     * 
     * @param identifiant L'identifiant de l'utilisateur
     * @param motDePasse Le mot de passe de l'utilisateur
     * @return Le pharmacien authentifié ou null si l'authentification échoue
     */
    public Pharmacien authentifier(String identifiant, String motDePasse) {
        Pharmacien pharmacien = pharmacienDAO.authentifier(identifiant, motDePasse);
        
        if (pharmacien != null) {
            // Stocker l'utilisateur connecté dans la session
            SessionManager.getInstance().setPharmacienConnecte(pharmacien);
            return pharmacien;
        }
        
        return null;
    }
    
    /**
     * Déconnecte l'utilisateur actuellement connecté
     */
    public void deconnecter() {
        SessionManager.getInstance().setPharmacienConnecte(null);
    }
} 