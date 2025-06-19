package com.pharmajava.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe représentant une vente
 */
public class Vente {
    private Integer id;
    private LocalDateTime dateVente;
    private BigDecimal montantTotal;
    private BigDecimal montantPercu;
    private BigDecimal montantRendu;
    private Pharmacien pharmacien;
    private Client client;
    private List<ProduitVendu> produitsVendus;

    /**
     * Constructeur par défaut
     */
    public Vente() {
        this.produitsVendus = new ArrayList<>();
        this.dateVente = LocalDateTime.now();
    }

    /**
     * Constructeur avec les champs essentiels
     * 
     * @param dateVente    Date de la vente
     * @param montantPercu Montant perçu du client
     * @param pharmacien   Pharmacien qui a effectué la vente
     */
    public Vente(LocalDateTime dateVente, BigDecimal montantPercu, Pharmacien pharmacien) {
        this.dateVente = dateVente;
        this.montantPercu = montantPercu;
        this.pharmacien = pharmacien;
        this.produitsVendus = new ArrayList<>();
        this.montantTotal = BigDecimal.ZERO;
    }

    /**
     * Constructeur complet
     */
    public Vente(Integer id, LocalDateTime dateVente, BigDecimal montantTotal,
            BigDecimal montantPercu, BigDecimal montantRendu, Pharmacien pharmacien, Client client) {
        this.id = id;
        this.dateVente = dateVente;
        this.montantTotal = montantTotal;
        this.montantPercu = montantPercu;
        this.montantRendu = montantRendu;
        this.pharmacien = pharmacien;
        this.client = client;
        this.produitsVendus = new ArrayList<>();
    }

    // Getters et Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDateTime getDateVente() {
        return dateVente;
    }

    public void setDateVente(LocalDateTime dateVente) {
        this.dateVente = dateVente;
    }

    public BigDecimal getMontantTotal() {
        return montantTotal;
    }

    public void setMontantTotal(BigDecimal montantTotal) {
        this.montantTotal = montantTotal;
    }

    public BigDecimal getMontantPercu() {
        return montantPercu;
    }

    public void setMontantPercu(BigDecimal montantPercu) {
        this.montantPercu = montantPercu;
    }

    public BigDecimal getMontantRendu() {
        return montantRendu;
    }

    public void setMontantRendu(BigDecimal montantRendu) {
        this.montantRendu = montantRendu;
    }

    public Pharmacien getPharmacien() {
        return pharmacien;
    }

    public void setPharmacien(Pharmacien pharmacien) {
        this.pharmacien = pharmacien;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public List<ProduitVendu> getProduitsVendus() {
        return produitsVendus;
    }

    public void setProduitsVendus(List<ProduitVendu> produitsVendus) {
        this.produitsVendus = produitsVendus;
    }

    /**
     * Ajoute un produit vendu à la vente
     * 
     * @param produitVendu Le produit vendu à ajouter
     */
    public void ajouterProduitVendu(ProduitVendu produitVendu) {
        if (this.produitsVendus == null) {
            this.produitsVendus = new ArrayList<>();
        }
        this.produitsVendus.add(produitVendu);

        // Recalculer le montant total
        calculerMontantTotal();
    }

    /**
     * Calcule le montant total de la vente
     */
    public void calculerMontantTotal() {
        this.montantTotal = BigDecimal.ZERO;
        for (ProduitVendu produitVendu : produitsVendus) {
            this.montantTotal = this.montantTotal.add(produitVendu.getPrixTotal());
        }
    }

    /**
     * Calcule le montant à rendre au client
     */
    public void calculerMontantRendu() {
        if (this.montantPercu != null && this.montantTotal != null) {
            this.montantRendu = this.montantPercu.subtract(this.montantTotal);
            if (this.montantRendu.compareTo(BigDecimal.ZERO) < 0) {
                this.montantRendu = BigDecimal.ZERO;
            }
        }
    }

    /**
     * Retourne le nombre de produits dans la vente
     * 
     * @return Le nombre de produits vendus
     */
    public int getNombreProduits() {
        return produitsVendus != null ? produitsVendus.size() : 0;
    }
}