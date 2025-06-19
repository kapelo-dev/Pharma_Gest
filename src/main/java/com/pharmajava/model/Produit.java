package com.pharmajava.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Classe représentant un produit pharmaceutique
 */
public class Produit {
    private Integer id;
    private String nom;
    private String description;
    private int quantiteEnStock;
    private BigDecimal prixUnitaire;
    private boolean surOrdonnance;
    private LocalDateTime dateCreation;
    private LocalDateTime derniereModification;
    
    // Nouveaux attributs pour les prix d'achat et de vente, et le seuil d'alerte
    private BigDecimal prixAchat;
    private BigDecimal prixVente;
    private int seuilAlerte;

    /**
     * Constructeur par défaut
     */
    public Produit() {
    }

    /**
     * Constructeur avec les champs essentiels
     * 
     * @param nom Nom du produit
     * @param description Description du produit
     * @param quantiteEnStock Quantité en stock
     * @param prixUnitaire Prix unitaire du produit
     * @param surOrdonnance Indique si le produit est vendu sur ordonnance
     */
    public Produit(String nom, String description, int quantiteEnStock, BigDecimal prixUnitaire, boolean surOrdonnance) {
        this.nom = nom;
        this.description = description;
        this.quantiteEnStock = quantiteEnStock;
        this.prixUnitaire = prixUnitaire;
        this.surOrdonnance = surOrdonnance;
        this.prixAchat = BigDecimal.ZERO;
        this.prixVente = prixUnitaire;
        this.seuilAlerte = 5; // Valeur par défaut
    }

    /**
     * Constructeur avec tous les champs
     */
    public Produit(Integer id, String nom, String description, int quantiteEnStock, 
                  BigDecimal prixUnitaire, boolean surOrdonnance, 
                  LocalDateTime dateCreation, LocalDateTime derniereModification) {
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.quantiteEnStock = quantiteEnStock;
        this.prixUnitaire = prixUnitaire;
        this.surOrdonnance = surOrdonnance;
        this.dateCreation = dateCreation;
        this.derniereModification = derniereModification;
        this.prixAchat = BigDecimal.ZERO;
        this.prixVente = prixUnitaire;
        this.seuilAlerte = 5; // Valeur par défaut
    }
    
    /**
     * Constructeur complet avec tous les champs y compris les nouveaux
     */
    public Produit(Integer id, String nom, String description, int quantiteEnStock, 
                  BigDecimal prixUnitaire, boolean surOrdonnance, 
                  BigDecimal prixAchat, BigDecimal prixVente, int seuilAlerte,
                  LocalDateTime dateCreation, LocalDateTime derniereModification) {
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.quantiteEnStock = quantiteEnStock;
        this.prixUnitaire = prixUnitaire;
        this.surOrdonnance = surOrdonnance;
        this.prixAchat = prixAchat;
        this.prixVente = prixVente;
        this.seuilAlerte = seuilAlerte;
        this.dateCreation = dateCreation;
        this.derniereModification = derniereModification;
    }

    // Getters et Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getQuantiteEnStock() {
        return quantiteEnStock;
    }

    public void setQuantiteEnStock(int quantiteEnStock) {
        this.quantiteEnStock = quantiteEnStock;
    }

    public BigDecimal getPrixUnitaire() {
        return prixUnitaire;
    }

    public void setPrixUnitaire(BigDecimal prixUnitaire) {
        this.prixUnitaire = prixUnitaire;
    }

    public boolean isSurOrdonnance() {
        return surOrdonnance;
    }

    public void setSurOrdonnance(boolean surOrdonnance) {
        this.surOrdonnance = surOrdonnance;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public LocalDateTime getDerniereModification() {
        return derniereModification;
    }

    public void setDerniereModification(LocalDateTime derniereModification) {
        this.derniereModification = derniereModification;
    }
    
    // Getters et Setters pour les nouveaux attributs
    public BigDecimal getPrixAchat() {
        return prixAchat != null ? prixAchat : BigDecimal.ZERO;
    }

    public void setPrixAchat(BigDecimal prixAchat) {
        this.prixAchat = prixAchat;
    }

    public BigDecimal getPrixVente() {
        return prixVente != null ? prixVente : prixUnitaire;
    }

    public void setPrixVente(BigDecimal prixVente) {
        this.prixVente = prixVente;
    }

    public int getSeuilAlerte() {
        return seuilAlerte;
    }

    public void setSeuilAlerte(int seuilAlerte) {
        this.seuilAlerte = seuilAlerte;
    }
    
    /**
     * Calcule la marge bénéficiaire du produit
     * 
     * @return La marge bénéficiaire
     */
    public BigDecimal calculerMargeBeneficiaire() {
        if (prixAchat == null || prixAchat.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal prixV = getPrixVente();
        return prixV.subtract(prixAchat).divide(prixAchat, 2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("100"));
    }
    
    /**
     * Vérifie si le stock est inférieur au seuil d'alerte
     * 
     * @return true si le stock est faible, false sinon
     */
    public boolean estEnRupture() {
        return quantiteEnStock <= seuilAlerte;
    }

    @Override
    public String toString() {
        return nom;
    }
} 