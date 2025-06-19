package com.pharmajava.model;

import java.time.LocalDateTime;

/**
 * Classe représentant un client de la pharmacie
 */
public class Client {
    private Integer id;
    private String nom;
    private String prenom;
    private String telephone;
    private String email;
    private String adresse;
    private LocalDateTime dateCreation;
    private LocalDateTime derniereModification;

    /**
     * Constructeur par défaut
     */
    public Client() {
    }

    /**
     * Constructeur avec les champs essentiels
     * 
     * @param nom       Nom du client
     * @param prenom    Prénom du client
     * @param telephone Numéro de téléphone du client
     */
    public Client(String nom, String prenom, String telephone) {
        this.nom = nom;
        this.prenom = prenom;
        this.telephone = telephone;
    }

    /**
     * Constructeur complet
     */
    public Client(Integer id, String nom, String prenom, String telephone,
            String email, String adresse, LocalDateTime dateCreation,
            LocalDateTime derniereModification) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.telephone = telephone;
        this.email = email;
        this.adresse = adresse;
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

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
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

    /**
     * Retourne le nom complet du client
     * 
     * @return Le nom et le prénom du client
     */
    public String getNomComplet() {
        return this.prenom + " " + this.nom;
    }

    /**
     * Retourne une représentation textuelle du client avec son ID et son nom
     * complet
     * 
     * @return Une chaîne au format "[ID] Prénom Nom"
     */
    @Override
    public String toString() {
        return "[" + (id != null ? id : "Nouveau") + "] " + getNomComplet();
    }
}