package com.pharmajava.model;

import java.math.BigDecimal;

/**
 * Classe pour encapsuler un produit et sa quantité sélectionnée
 * Utilisée pour l'interface de vente
 */
public class ProduitSelection {
    private Produit produit;
    private int quantiteSelectionnee;
    private BigDecimal prixUnitaire;
    
    /**
     * Crée une nouvelle instance de ProduitSelection
     * 
     * @param produit Le produit sélectionné
     */
    public ProduitSelection(Produit produit) {
        this.produit = produit;
        this.prixUnitaire = produit.getPrixVente();
        this.quantiteSelectionnee = 1; // Valeur par défaut
    }
    
    /**
     * Crée une nouvelle instance de ProduitSelection avec une quantité spécifiée
     * 
     * @param produit Le produit sélectionné
     * @param quantiteSelectionnee La quantité sélectionnée
     */
    public ProduitSelection(Produit produit, int quantiteSelectionnee) {
        this.produit = produit;
        this.prixUnitaire = produit.getPrixVente();
        this.quantiteSelectionnee = quantiteSelectionnee;
    }
    
    /**
     * Crée une nouvelle instance de ProduitSelection avec une quantité et un prix spécifiés
     * 
     * @param produit Le produit sélectionné
     * @param quantiteSelectionnee La quantité sélectionnée
     * @param prixUnitaire Le prix unitaire (peut être différent du prix standard)
     */
    public ProduitSelection(Produit produit, int quantiteSelectionnee, BigDecimal prixUnitaire) {
        this.produit = produit;
        this.quantiteSelectionnee = quantiteSelectionnee;
        this.prixUnitaire = prixUnitaire;
    }
    
    /**
     * Obtient le produit
     * 
     * @return Le produit
     */
    public Produit getProduit() {
        return produit;
    }
    
    /**
     * Définit le produit
     * 
     * @param produit Le produit à définir
     */
    public void setProduit(Produit produit) {
        this.produit = produit;
    }
    
    /**
     * Obtient la quantité sélectionnée
     * 
     * @return La quantité
     */
    public int getQuantiteSelectionnee() {
        return quantiteSelectionnee;
    }
    
    /**
     * Définit la quantité sélectionnée
     * 
     * @param quantiteSelectionnee La quantité à définir
     */
    public void setQuantiteSelectionnee(int quantiteSelectionnee) {
        this.quantiteSelectionnee = quantiteSelectionnee;
    }
    
    /**
     * Obtient le prix unitaire
     * 
     * @return Le prix unitaire
     */
    public BigDecimal getPrixUnitaire() {
        return prixUnitaire;
    }
    
    /**
     * Définit le prix unitaire
     * 
     * @param prixUnitaire Le prix unitaire à définir
     */
    public void setPrixUnitaire(BigDecimal prixUnitaire) {
        this.prixUnitaire = prixUnitaire;
    }
    
    /**
     * Calcule le sous-total pour ce produit (prix unitaire * quantité)
     * 
     * @return Le sous-total
     */
    public BigDecimal getSousTotal() {
        return prixUnitaire.multiply(BigDecimal.valueOf(quantiteSelectionnee));
    }
    
    /**
     * Vérifie si la quantité demandée est disponible en stock
     * 
     * @return true si le stock est suffisant, false sinon
     */
    public boolean estStockSuffisant() {
        return produit.getQuantiteEnStock() >= quantiteSelectionnee;
    }
    
    /**
     * Obtient le stock disponible pour ce produit
     * 
     * @return La quantité en stock
     */
    public int getStockDisponible() {
        return produit.getQuantiteEnStock();
    }
    
    /**
     * Obtient l'ID du produit
     * 
     * @return L'ID du produit
     */
    public Integer getProduitId() {
        return produit.getId();
    }
    
    /**
     * Obtient le nom du produit
     * 
     * @return Le nom du produit
     */
    public String getProduitNom() {
        return produit.getNom();
    }
    
    /**
     * Obtient la description du produit
     * 
     * @return La description du produit
     */
    public String getProduitDescription() {
        return produit.getDescription();
    }
    
    @Override
    public String toString() {
        return produit.getNom() + " - " + quantiteSelectionnee + " x " + prixUnitaire;
    }
} 