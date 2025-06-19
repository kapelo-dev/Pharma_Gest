package com.pharmajava.controller;

import com.pharmajava.model.Produit;
import com.pharmajava.dao.ProduitDAO;

import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;

/**
 * Contrôleur pour gérer les opérations liées aux produits pharmaceutiques
 */
public class ProduitController {
    private ProduitDAO produitDAO;

    /**
     * Constructeur
     */
    public ProduitController() {
        this.produitDAO = new ProduitDAO();
    }

    /**
     * Récupère tous les produits
     *
     * @return Liste de tous les produits
     */
    public List<Produit> obtenirTous() {
        try {
            return produitDAO.obtenirTous();
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération des produits: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Récupère tous les produits (alias pour obtenirTous)
     *
     * @return Liste de tous les produits
     */
    public List<Produit> obtenirTousProduits() {
        return obtenirTous();
    }

    /**
     * Recherche des produits par mot-clé
     *
     * @param motCle Le mot-clé à rechercher
     * @return Liste des produits correspondants
     */
    public List<Produit> rechercher(String motCle) {
        try {
            return produitDAO.rechercher(motCle);
        } catch (Exception e) {
            System.err.println("Erreur lors de la recherche de produits: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Recherche des produits par mot-clé (alias pour rechercher)
     *
     * @param motCle Le mot-clé à rechercher
     * @return Liste des produits correspondants
     */
    public List<Produit> rechercherProduits(String motCle) {
        return rechercher(motCle);
    }

    /**
     * Récupère un produit par son ID
     *
     * @param id L'ID du produit
     * @return Le produit ou null si non trouvé
     */
    public Produit obtenirParId(int id) {
        try {
            return produitDAO.obtenirParId(id);
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération du produit par ID: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Récupère un produit par son ID (Integer)
     *
     * @param id L'ID du produit
     * @return Le produit ou null si non trouvé
     */
    public Produit obtenirProduitParId(Integer id) {
        if (id == null) return null;
        return obtenirParId(id);
    }

    /**
     * Récupère un produit par son nom
     *
     * @param nom Le nom du produit
     * @return Le produit ou null si non trouvé
     */
    public Produit obtenirParNom(String nom) {
        try {
            return produitDAO.obtenirParNom(nom);
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération du produit par nom: " + e.getMessage());
            return null;
        }
    }

    /**
     * Ajoute un nouveau produit
     *
     * @param produit Le produit à ajouter
     * @return Le produit ajouté avec son ID ou null si échec
     */
    public Produit ajouter(Produit produit) {
        try {
            return produitDAO.ajouter(produit);
        } catch (Exception e) {
            System.err.println("Erreur lors de l'ajout du produit: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Ajoute un nouveau produit (alias pour ajouter)
     *
     * @param produit Le produit à ajouter
     * @return true si l'ajout a réussi, false sinon
     */
    public boolean ajouterProduit(Produit produit) {
        return ajouter(produit) != null;
    }

    /**
     * Met à jour un produit existant
     *
     * @param produit Le produit à mettre à jour
     * @return true si la mise à jour a réussi, false sinon
     */
    public boolean mettreAJour(Produit produit) {
        try {
            return produitDAO.metreAJour(produit);
        } catch (Exception e) {
            System.err.println("Erreur lors de la mise à jour du produit: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Met à jour un produit existant (alias pour mettreAJour)
     *
     * @param produit Le produit à mettre à jour
     * @return true si la mise à jour a réussi, false sinon
     */
    public boolean modifierProduit(Produit produit) {
        return mettreAJour(produit);
    }

    /**
     * Supprime un produit
     *
     * @param id L'ID du produit à supprimer
     * @return true si la suppression a réussi, false sinon
     */
    public boolean supprimer(int id) {
        try {
            return produitDAO.supprimer(id);
        } catch (Exception e) {
            System.err.println("Erreur lors de la suppression du produit: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Supprime un produit (alias pour supprimer)
     *
     * @param id L'ID du produit à supprimer
     * @return true si la suppression a réussi, false sinon
     */
    public boolean supprimerProduit(Integer id) {
        if (id == null) return false;
        return supprimer(id);
    }

    /**
     * Met à jour la quantité en stock d'un produit
     *
     * @param id L'ID du produit
     * @param nouvelleQuantite La nouvelle quantité en stock
     * @return true si la mise à jour a réussi, false sinon
     */
    public boolean mettreAJourStock(int id, int nouvelleQuantite) {
        try {
            return produitDAO.mettreAJourStock(id, nouvelleQuantite);
        } catch (Exception e) {
            System.err.println("Erreur lors de la mise à jour du stock: " + e.getMessage());
            return false;
        }
    }
} 