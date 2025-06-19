package com.pharmajava.model;

import java.time.LocalDateTime;

/**
 * Classe représentant les informations de la pharmacie
 */
public class PharmacieInfo {
    private Integer id;
    private String nom;
    private String adresse;
    private String telephone1;
    private String telephone2;
    private String email;
    private String logoPath;
    private LocalDateTime dateModification;

    /**
     * Constructeur par défaut
     */
    public PharmacieInfo() {
    }

    /**
     * Constructeur avec les champs essentiels
     * 
     * @param nom Le nom de la pharmacie
     * @param adresse L'adresse de la pharmacie
     * @param telephone1 Le numéro de téléphone principal
     */
    public PharmacieInfo(String nom, String adresse, String telephone1) {
        this.nom = nom;
        this.adresse = adresse;
        this.telephone1 = telephone1;
    }

    /**
     * Constructeur complet
     * 
     * @param id Identifiant unique
     * @param nom Le nom de la pharmacie
     * @param adresse L'adresse de la pharmacie
     * @param telephone1 Le numéro de téléphone principal
     * @param telephone2 Le numéro de téléphone secondaire (facultatif)
     * @param email L'adresse email de contact (facultatif)
     * @param logoPath Le chemin vers le logo de la pharmacie
     * @param dateModification La date de dernière modification
     */
    public PharmacieInfo(Integer id, String nom, String adresse, String telephone1,
                        String telephone2, String email, String logoPath, 
                        LocalDateTime dateModification) {
        this.id = id;
        this.nom = nom;
        this.adresse = adresse;
        this.telephone1 = telephone1;
        this.telephone2 = telephone2;
        this.email = email;
        this.logoPath = logoPath;
        this.dateModification = dateModification;
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

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getTelephone1() {
        return telephone1;
    }

    public void setTelephone1(String telephone1) {
        this.telephone1 = telephone1;
    }

    public String getTelephone2() {
        return telephone2;
    }

    public void setTelephone2(String telephone2) {
        this.telephone2 = telephone2;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLogoPath() {
        return logoPath;
    }

    public void setLogoPath(String logoPath) {
        this.logoPath = logoPath;
    }

    public LocalDateTime getDateModification() {
        return dateModification;
    }

    public void setDateModification(LocalDateTime dateModification) {
        this.dateModification = dateModification;
    }

    @Override
    public String toString() {
        return "PharmacieInfo{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", adresse='" + adresse + '\'' +
                ", telephone1='" + telephone1 + '\'' +
                '}';
    }
} 