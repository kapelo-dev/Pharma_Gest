package com.pharmajava.controller;

import com.pharmajava.dao.PharmacieInfoDAO;
import com.pharmajava.model.PharmacieInfo;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Contrôleur pour gérer les paramètres de la pharmacie
 */
public class ParametresController {
    private static final Logger LOGGER = Logger.getLogger(ParametresController.class.getName());
    private static final String LOGOS_DIR = "resources/logos";
    
    private final PharmacieInfoDAO pharmacieInfoDAO;
    
    /**
     * Constructeur du contrôleur de paramètres
     */
    public ParametresController() {
        this.pharmacieInfoDAO = new PharmacieInfoDAO();
        
        // Assurer que le répertoire de logos existe
        creerRepertoireSiNecessaire();
    }
    
    /**
     * Obtient les paramètres actuels de la pharmacie
     * 
     * @return Les informations de la pharmacie
     */
    public PharmacieInfo obtenirParametres() {
        try {
            return pharmacieInfoDAO.obtenirInfoPharmacieActuelle();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération des paramètres de la pharmacie", e);
            return null;
        }
    }
    
    /**
     * Enregistre les paramètres de la pharmacie
     * 
     * @param pharmacieInfo Les nouvelles informations de la pharmacie
     * @param logoFile Le fichier logo (peut être null si pas de changement)
     * @return true si l'enregistrement a réussi, false sinon
     */
    public boolean enregistrerParametres(PharmacieInfo pharmacieInfo, File logoFile) {
        try {
            // Gérer le logo s'il a été fourni
            if (logoFile != null) {
                String logoPath = enregistrerLogo(logoFile);
                if (logoPath != null) {
                    pharmacieInfo.setLogoPath(logoPath);
                }
            }
            
            // Enregistrer les informations de la pharmacie
            return pharmacieInfoDAO.mettreAJour(pharmacieInfo);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de l'enregistrement des paramètres de la pharmacie", e);
            return false;
        }
    }
    
    /**
     * Enregistre un nouveau logo
     * 
     * @param logoFile Le fichier logo
     * @return Le chemin du logo enregistré ou null en cas d'erreur
     */
    private String enregistrerLogo(File logoFile) {
        try {
            // Vérifier si c'est bien une image
            try {
                BufferedImage image = ImageIO.read(logoFile);
                if (image == null) {
                    LOGGER.warning("Le fichier sélectionné n'est pas une image valide");
                    return null;
                }
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, "Erreur lors de la lecture de l'image", e);
                return null;
            }
            
            // Générer un nom unique pour le fichier
            String originalFileName = logoFile.getName();
            String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
            String newFileName = UUID.randomUUID().toString() + extension;
            
            // Copier le fichier dans le répertoire des logos
            Path sourcePath = logoFile.toPath();
            Path targetPath = Paths.get(LOGOS_DIR, newFileName);
            
            Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
            
            return targetPath.toString();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de l'enregistrement du logo", e);
            return null;
        }
    }
    
    /**
     * Crée le répertoire des logos s'il n'existe pas
     */
    private void creerRepertoireSiNecessaire() {
        try {
            Path logosPath = Paths.get(LOGOS_DIR);
            if (!Files.exists(logosPath)) {
                Files.createDirectories(logosPath);
                LOGGER.info("Répertoire des logos créé: " + logosPath.toAbsolutePath());
            }
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Impossible de créer le répertoire des logos", e);
        }
    }
} 