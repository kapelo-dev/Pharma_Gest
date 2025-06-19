package com.pharmajava.model;

import java.time.LocalDateTime;

/**
 * Classe représentant un pharmacien/utilisateur du système
 */
public class Pharmacien {
    private Integer id;
    private String nom;
    private String prenom;
    private String identifiant;
    private String telephone;
    private String motDePasse;
    private String role;
    private LocalDateTime dateCreation;
    private LocalDateTime derniereModification;

    /**
     * Constructeur par défaut
     */
    public Pharmacien() {
    }

    /**
     * Constructeur avec les champs essentiels
     * 
     * @param nom Nom du pharmacien
     * @param prenom Prénom du pharmacien
     * @param identifiant Identifiant de connexion
     * @param telephone Numéro de téléphone
     * @param motDePasse Mot de passe (non hashé)
     * @param role Rôle dans le système (ADMIN, PHARMACIEN, etc.)
     */
    public Pharmacien(String nom, String prenom, String identifiant, String telephone, 
                     String motDePasse, String role) {
        this.nom = nom;
        this.prenom = prenom;
        this.identifiant = identifiant;
        this.telephone = telephone;
        this.motDePasse = motDePasse;
        this.role = role;
    }

    /**
     * Constructeur complet avec tous les champs
     */
    public Pharmacien(Integer id, String nom, String prenom, String identifiant, 
                     String telephone, String motDePasse, String role, 
                     LocalDateTime dateCreation, LocalDateTime derniereModification) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.identifiant = identifiant;
        this.telephone = telephone;
        this.motDePasse = motDePasse;
        this.role = role;
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

    public String getIdentifiant() {
        return identifiant;
    }

    public void setIdentifiant(String identifiant) {
        this.identifiant = identifiant;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
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
     * Retourne le nom complet du pharmacien
     * 
     * @return Nom et prénom
     */
    public String getNomComplet() {
        return prenom + " " + nom;
    }

    @Override
    public String toString() {
        return getNomComplet();
    }
} 