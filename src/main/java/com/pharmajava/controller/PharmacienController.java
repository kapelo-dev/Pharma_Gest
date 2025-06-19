package com.pharmajava.controller;

import com.pharmajava.dao.PharmacienDAO;
import com.pharmajava.model.Pharmacien;
import com.pharmajava.utils.PasswordUtils;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Contrôleur pour gérer les opérations sur les pharmaciens
 */
public class PharmacienController {
    private static final Logger LOGGER = Logger.getLogger(PharmacienController.class.getName());
    
    private final PharmacienDAO pharmacienDAO;
    
    /**
     * Constructeur du contrôleur de pharmaciens
     */
    public PharmacienController() {
        this.pharmacienDAO = new PharmacienDAO();
    }
    
    /**
     * Récupère tous les pharmaciens
     * 
     * @return Liste de tous les pharmaciens
     */
    public List<Pharmacien> obtenirTous() {
        try {
            return pharmacienDAO.obtenirTous();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération des pharmaciens", e);
            return java.util.Collections.emptyList();
        }
    }
    
    /**
     * Récupère un pharmacien par son ID
     * 
     * @param id L'ID du pharmacien à récupérer
     * @return Le pharmacien trouvé ou null si non trouvé
     */
    public Pharmacien obtenirParId(int id) {
        try {
            return pharmacienDAO.obtenirParId(id);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération du pharmacien avec ID " + id, e);
            return null;
        }
    }
    
    /**
     * Ajoute un nouveau pharmacien
     * 
     * @param pharmacien Le pharmacien à ajouter
     * @return true si l'ajout a réussi, false sinon
     */
    public boolean ajouter(Pharmacien pharmacien) {
        try {
            LOGGER.log(Level.INFO, "Tentative d'ajout d'un nouveau pharmacien: " + pharmacien.getIdentifiant());
            
            // Vérifier que le mot de passe n'est pas vide
            if (pharmacien.getMotDePasse() == null || pharmacien.getMotDePasse().isEmpty()) {
                LOGGER.log(Level.WARNING, "Tentative d'ajout d'un pharmacien avec un mot de passe vide");
                return false;
            }
            
            // Stockage du mot de passe en clair (sans hashage)
            LOGGER.log(Level.INFO, "Stockage du mot de passe en clair pour: " + pharmacien.getIdentifiant());
            
            Pharmacien result = pharmacienDAO.ajouter(pharmacien);
            boolean success = result != null && result.getId() != null;
            
            if (success) {
                LOGGER.log(Level.INFO, "Pharmacien ajouté avec succès, ID: " + result.getId());
            } else {
                LOGGER.log(Level.WARNING, "Échec de l'ajout du pharmacien dans la base de données");
            }
            
            return success;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de l'ajout du pharmacien", e);
            return false;
        }
    }
    
    /**
     * Met à jour un pharmacien existant
     * 
     * @param pharmacien Le pharmacien à mettre à jour
     * @param modifierMotDePasse Indique si le mot de passe doit être modifié
     * @return true si la mise à jour a réussi, false sinon
     */
    public boolean mettreAJour(Pharmacien pharmacien, boolean modifierMotDePasse) {
        try {
            // Si on doit modifier le mot de passe, on le stocke en clair
            if (modifierMotDePasse) {
                LOGGER.log(Level.INFO, "Mise à jour du mot de passe en clair pour le pharmacien ID: " + pharmacien.getId());
                // Le mot de passe est déjà en clair dans pharmacien.getMotDePasse()
                return pharmacienDAO.mettreAJour(pharmacien);
            } else {
                // Récupérer le pharmacien existant pour conserver son mot de passe
                Pharmacien existant = pharmacienDAO.obtenirParId(pharmacien.getId());
                if (existant != null) {
                    pharmacien.setMotDePasse(existant.getMotDePasse());
                    return pharmacienDAO.mettreAJour(pharmacien);
                }
                LOGGER.log(Level.WARNING, "Pharmacien existant non trouvé pour l'ID: " + pharmacien.getId());
                return false;
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la mise à jour du pharmacien", e);
            return false;
        }
    }
    
    /**
     * Supprime un pharmacien
     * 
     * @param id L'ID du pharmacien à supprimer
     * @return true si la suppression a réussi, false sinon
     */
    public boolean supprimer(int id) {
        try {
            return pharmacienDAO.supprimer(id);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la suppression du pharmacien avec ID " + id, e);
            return false;
        }
    }
    
    /**
     * Vérifie si un identifiant de pharmacien existe déjà
     * 
     * @param identifiant L'identifiant à vérifier
     * @return true si l'identifiant existe déjà, false sinon
     */
    public boolean identifiantExiste(String identifiant) {
        try {
            Pharmacien pharmacien = pharmacienDAO.obtenirParIdentifiant(identifiant);
            return pharmacien != null;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la vérification de l'identifiant", e);
            return false;
        }
    }
} 