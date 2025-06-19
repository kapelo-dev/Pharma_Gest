package com.pharmajava.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Classe représentant un stock de produit pharmaceutique
 */
public class Stock {
    private Integer id;
    private Produit produit;
    private String numeroLot;
    private Integer quantite;
    private LocalDate dateExpiration;
    private LocalDateTime dateCreation;

    /**
     * Constructeur par défaut
     */
    public Stock() {
        this.dateCreation = LocalDateTime.now();
    }

    /**
     * Constructeur avec paramètres
     *
     * @param produit Le produit concerné
     * @param numeroLot Le numéro de lot
     * @param quantite La quantité en stock
     * @param dateExpiration La date d'expiration
     */
    public Stock(Produit produit, String numeroLot, Integer quantite, LocalDate dateExpiration) {
        this.produit = produit;
        this.numeroLot = numeroLot;
        this.quantite = quantite;
        this.dateExpiration = dateExpiration;
        this.dateCreation = LocalDateTime.now();
    }

    /**
     * Constructeur complet avec tous les champs
     * 
     * @param id L'identifiant unique du stock
     * @param produit Le produit concerné par ce stock
     * @param numeroLot Le numéro de lot du stock
     * @param quantite La quantité disponible
     * @param dateExpiration La date d'expiration du lot
     */
    public Stock(Integer id, Produit produit, String numeroLot, Integer quantite, LocalDate dateExpiration) {
        this.id = id;
        this.produit = produit;
        this.numeroLot = numeroLot;
        this.quantite = quantite;
        this.dateExpiration = dateExpiration;
        this.dateCreation = LocalDateTime.now();
    }

    /**
     * Vérifie si le stock est expiré
     *
     * @return true si le stock est expiré, false sinon
     */
    public boolean isExpired() {
        return dateExpiration != null && dateExpiration.isBefore(LocalDate.now());
    }

    /**
     * Vérifie si le stock expire bientôt (dans les prochains jours)
     *
     * @param days Nombre de jours à vérifier
     * @return true si le stock expire bientôt, false sinon
     */
    public boolean isExpiringInNextDays(int days) {
        if (dateExpiration == null) {
            return false;
        }
        
        LocalDate today = LocalDate.now();
        LocalDate futureDate = today.plusDays(days);
        
        return dateExpiration.isAfter(today) && dateExpiration.isBefore(futureDate.plusDays(1));
    }
    
    /**
     * Vérifie si le stock expire dans une période spécifique
     *
     * @param startDays Début de la période en jours (à partir d'aujourd'hui)
     * @param endDays Fin de la période en jours (à partir d'aujourd'hui)
     * @return true si le stock expire dans la période donnée, false sinon
     */
    public boolean isExpiringBetween(int startDays, int endDays) {
        if (dateExpiration == null) {
            return false;
        }
        
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.plusDays(startDays);
        LocalDate endDate = today.plusDays(endDays);
        
        return dateExpiration.isAfter(startDate.minusDays(1)) && 
               dateExpiration.isBefore(endDate.plusDays(1));
    }

    // Getters et Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Produit getProduit() {
        return produit;
    }

    public void setProduit(Produit produit) {
        this.produit = produit;
    }

    public String getNumeroLot() {
        return numeroLot;
    }

    public void setNumeroLot(String numeroLot) {
        this.numeroLot = numeroLot;
    }

    public Integer getQuantite() {
        return quantite;
    }

    public void setQuantite(Integer quantite) {
        this.quantite = quantite;
    }

    public LocalDate getDateExpiration() {
        return dateExpiration;
    }

    public void setDateExpiration(LocalDate dateExpiration) {
        this.dateExpiration = dateExpiration;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    /**
     * Calcule le nombre de jours avant expiration
     * 
     * @return Le nombre de jours avant expiration (négatif si déjà expiré)
     */
    public long joursAvantExpiration() {
        return java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), this.dateExpiration);
    }

    @Override
    public String toString() {
        return "Stock{" +
                "id=" + id +
                ", produit=" + (produit != null ? produit.getNom() : "null") +
                ", numeroLot='" + numeroLot + '\'' +
                ", quantite=" + quantite +
                ", dateExpiration=" + dateExpiration +
                '}';
    }
} 