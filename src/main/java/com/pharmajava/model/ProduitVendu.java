package com.pharmajava.model;

import java.math.BigDecimal;

/**
 * Classe représentant un produit vendu dans une vente
 */
public class ProduitVendu {
    private Integer id;
    private Integer venteId;
    private Produit produit;
    private int quantite;
    private BigDecimal prixUnitaire;
    private BigDecimal prixTotal;

    /**
     * Constructeur par défaut
     */
    public ProduitVendu() {
    }

    /**
     * Constructeur avec les champs essentiels
     * 
     * @param produit Le produit vendu
     * @param quantite La quantité vendue
     * @param prixUnitaire Le prix unitaire à la vente
     */
    public ProduitVendu(Produit produit, int quantite, BigDecimal prixUnitaire) {
        this.produit = produit;
        this.quantite = quantite;
        this.prixUnitaire = prixUnitaire;
        calculerPrixTotal();
    }

    /**
     * Constructeur complet
     */
    public ProduitVendu(Integer id, Integer venteId, Produit produit, int quantite, 
                        BigDecimal prixUnitaire, BigDecimal prixTotal) {
        this.id = id;
        this.venteId = venteId;
        this.produit = produit;
        this.quantite = quantite;
        this.prixUnitaire = prixUnitaire;
        this.prixTotal = prixTotal;
    }

    // Getters et Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getVenteId() {
        return venteId;
    }

    public void setVenteId(Integer venteId) {
        this.venteId = venteId;
    }

    public Produit getProduit() {
        return produit;
    }

    public void setProduit(Produit produit) {
        this.produit = produit;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
        calculerPrixTotal();
    }

    public BigDecimal getPrixUnitaire() {
        return prixUnitaire;
    }

    public void setPrixUnitaire(BigDecimal prixUnitaire) {
        this.prixUnitaire = prixUnitaire;
        calculerPrixTotal();
    }

    public BigDecimal getPrixTotal() {
        return prixTotal;
    }

    public void setPrixTotal(BigDecimal prixTotal) {
        this.prixTotal = prixTotal;
    }

    /**
     * Calcule le prix total du produit vendu (quantité * prix unitaire)
     */
    public void calculerPrixTotal() {
        if (this.prixUnitaire != null) {
            this.prixTotal = this.prixUnitaire.multiply(BigDecimal.valueOf(this.quantite));
        }
    }

    @Override
    public String toString() {
        return produit != null ? produit.getNom() + " x " + quantite : "Produit inconnu";
    }
} 