package com.pharmajava.controller;

import com.pharmajava.dao.ProduitDAO;
import com.pharmajava.dao.StockDAO;
import com.pharmajava.dao.VenteDAO;
import com.pharmajava.utils.DatabaseConfig;
import com.pharmajava.utils.SessionManager;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Contrôleur pour le tableau de bord
 */
public class DashboardController {
    private static final Logger LOGGER = Logger.getLogger(DashboardController.class.getName());
    
    private final ProduitDAO produitDAO;
    private final VenteDAO venteDAO;
    private final StockDAO stockDAO;
    
    /**
     * Constructeur du contrôleur de tableau de bord
     */
    public DashboardController() {
        this.produitDAO = new ProduitDAO();
        this.venteDAO = new VenteDAO();
        this.stockDAO = new StockDAO();
    }
    
    /**
     * Récupère le nombre total de produits en stock
     * 
     * @return Le nombre de produits en stock sous forme de chaîne
     */
    public String getNombreProduits() {
        try {
            int nombre = produitDAO.obtenirTous().size();
            return String.valueOf(nombre);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération du nombre de produits", e);
            return "0";
        }
    }
    
    /**
     * Récupère le nombre de ventes effectuées aujourd'hui
     * 
     * @return Le nombre de ventes du jour sous forme de chaîne
     */
    public String getNombreVentesAujourdhui() {
        try {
            LocalDateTime debutJour = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
            LocalDateTime finJour = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
            
            int nombre = venteDAO.obtenirParPeriode(debutJour, finJour).size();
            return String.valueOf(nombre);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération du nombre de ventes aujourd'hui", e);
            return "0";
        }
    }
    
    /**
     * Récupère le nombre de produits expirés
     * 
     * @return Le nombre de produits expirés sous forme de chaîne
     */
    public String getNombreProduitsExpires() {
        try {
            int nombre = stockDAO.obtenirStocksExpires().size();
            return String.valueOf(nombre);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération du nombre de produits expirés", e);
            return "0";
        }
    }
    
    /**
     * Récupère le montant total des ventes d'aujourd'hui
     * 
     * @return Le montant total des ventes du jour sous forme de chaîne
     */
    public String getMontantVentesAujourdhui() {
        try {
            LocalDateTime debutJour = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
            LocalDateTime finJour = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
            
            BigDecimal montantTotal = BigDecimal.ZERO;
            for (var vente : venteDAO.obtenirParPeriode(debutJour, finJour)) {
                if (vente.getMontantTotal() != null) {
                    montantTotal = montantTotal.add(vente.getMontantTotal());
                }
            }
            
            return String.valueOf(montantTotal.intValue());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération du montant des ventes aujourd'hui", e);
            return "0";
        }
    }
    
    /**
     * Déconnecte l'utilisateur actuel
     */
    public void deconnecter() {
        SessionManager.getInstance().setPharmacienConnecte(null);
    }
    
    /**
     * Nettoie les ressources à la fermeture de l'application
     */
    public void cleanup() {
        try {
            // Fermer la connexion à la base de données
            DatabaseConfig.closeDataSource();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors du nettoyage des ressources", e);
        }
    }
} 