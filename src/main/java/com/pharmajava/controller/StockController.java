package com.pharmajava.controller;

import com.pharmajava.model.Stock;
import com.pharmajava.model.Produit;
import com.pharmajava.dao.StockDAO;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Contrôleur pour gérer les opérations liées aux stocks de produits
 */
public class StockController {
    private StockDAO stockDAO;
    private static final Logger LOGGER = Logger.getLogger(StockController.class.getName());

    /**
     * Constructeur
     */
    public StockController() {
        this.stockDAO = new StockDAO();
    }

    /**
     * Récupère tous les stocks
     *
     * @return Liste de tous les stocks
     */
    public List<Stock> obtenirTous() {
        try {
            return stockDAO.findAll();
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération des stocks: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Récupère un stock par son ID
     *
     * @param id L'ID du stock
     * @return Le stock correspondant ou null si non trouvé
     */
    public Stock obtenirParId(Integer id) {
        try {
            return stockDAO.findById(id);
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération du stock par ID: " + e.getMessage());
            return null;
        }
    }

    /**
     * Récupère les stocks pour un produit spécifique
     *
     * @param produitId l'ID du produit
     * @return Liste des stocks pour le produit
     */
    public List<Stock> getStocksByProduit(Integer produitId) {
        try {
            return stockDAO.findByProduitId(produitId);
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération des stocks par produit: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Ajoute un nouveau stock
     *
     * @param stock le stock à ajouter
     * @return true si l'ajout a réussi, false sinon
     */
    public boolean ajouter(Stock stock) {
        try {
            // Validation côté contrôleur
            if (stock == null) {
                System.err.println("Erreur: Tentative d'ajout d'un stock null");
                return false;
            }
            
            if (stock.getProduit() == null || stock.getProduit().getId() == null) {
                System.err.println("Erreur: Le produit du stock est null ou sans ID");
                return false;
            }
            
            if (stock.getNumeroLot() == null || stock.getNumeroLot().isEmpty()) {
                System.err.println("Erreur: Le numéro de lot du stock est vide");
                return false;
            }
            
            if (stock.getDateExpiration() == null) {
                System.err.println("Erreur: La date d'expiration du stock est nulle");
                return false;
            }
            
            // Tenter d'ajouter le stock
            System.out.println("Ajout d'un stock: produit=" + stock.getProduit().getId() + 
                ", lot=" + stock.getNumeroLot() + 
                ", quantité=" + stock.getQuantite() + 
                ", expiration=" + stock.getDateExpiration());
                
            boolean result = stockDAO.save(stock);
            if (result) {
                System.out.println("Stock ajouté avec succès, ID=" + stock.getId());
            } else {
                System.err.println("Échec de l'ajout du stock en base de données");
            }
            return result;
        } catch (Exception e) {
            System.err.println("Erreur lors de l'ajout du stock: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Met à jour un stock existant
     *
     * @param stock le stock à mettre à jour
     * @return true si la mise à jour a réussi, false sinon
     */
    public boolean mettreAJour(Stock stock) {
        try {
            return stockDAO.update(stock);
        } catch (Exception e) {
            System.err.println("Erreur lors de la mise à jour du stock: " + e.getMessage());
            return false;
        }
    }

    /**
     * Supprime un stock
     *
     * @param stockId l'ID du stock à supprimer
     * @return true si la suppression a réussi, false sinon
     */
    public boolean supprimer(Integer stockId) {
        try {
            return stockDAO.delete(stockId);
        } catch (Exception e) {
            System.err.println("Erreur lors de la suppression du stock: " + e.getMessage());
            return false;
        }
    }

    /**
     * Récupère les stocks périmés
     *
     * @return Liste des stocks périmés
     */
    public List<Stock> obtenirStocksExpires() {
        try {
            return stockDAO.obtenirStocksExpires();
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération des stocks expirés: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Récupère les stocks qui vont bientôt expirer (dans les jours spécifiés)
     *
     * @param jours Nombre de jours à vérifier
     * @return Liste des stocks qui vont bientôt expirer
     */
    public List<Stock> obtenirStocksExpirantBientot(int jours) {
        try {
            return stockDAO.obtenirStocksExpirantBientot(jours);
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération des stocks proches de l'expiration: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Vérifie si un produit a du stock disponible
     *
     * @param produitId l'ID du produit
     * @return true si le produit a du stock disponible, false sinon
     */
    public boolean hasStockForProduct(Integer produitId) {
        try {
            List<Stock> stocks = getStocksByProduit(produitId);
            return stocks.stream()
                    .filter(stock -> !stock.isExpired())
                    .mapToInt(Stock::getQuantite)
                    .sum() > 0;
        } catch (Exception e) {
            System.err.println("Erreur lors de la vérification du stock disponible: " + e.getMessage());
            return false;
        }
    }

    /**
     * Calcule la quantité totale disponible pour un produit
     *
     * @param produitId l'ID du produit
     * @return la quantité totale disponible
     */
    public int getTotalQuantityForProduct(Integer produitId) {
        try {
            List<Stock> stocks = getStocksByProduit(produitId);
            return stocks.stream()
                    .filter(stock -> !stock.isExpired())
                    .mapToInt(Stock::getQuantite)
                    .sum();
        } catch (Exception e) {
            System.err.println("Erreur lors du calcul de la quantité totale: " + e.getMessage());
            return 0;
        }
    }

    /**
     * Diminue le stock d'un produit en commençant par les lots qui expirent le plus tôt
     * 
     * @param produitId L'ID du produit
     * @param quantite La quantité à retirer
     * @return true si l'opération a réussi, false sinon
     */
    public boolean diminuerStockParLot(Integer produitId, int quantite) {
        try {
            return stockDAO.diminuerStockParLot(produitId, quantite);
        } catch (Exception e) {
            System.err.println("Erreur lors de la diminution du stock par lot: " + e.getMessage());
            return false;
        }
    }

    /**
     * Force l'actualisation de toutes les quantités en stock des produits
     * en recalculant la somme des lots non expirés pour chaque produit
     * 
     * @return true si l'opération a réussi, false sinon
     */
    public boolean actualiserTousLesStocks() {
        try {
            boolean resultat = stockDAO.actualiserTousLesStocks();
            if (resultat) {
                LOGGER.info("Actualisation de tous les stocks effectuée avec succès");
            } else {
                LOGGER.warning("L'actualisation de tous les stocks a rencontré des problèmes");
            }
            return resultat;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de l'actualisation de tous les stocks", e);
            return false;
        }
    }
} 