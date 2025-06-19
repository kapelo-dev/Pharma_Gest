package com.pharmajava.controller;

import com.pharmajava.dao.ProduitDAO;
import com.pharmajava.dao.VenteDAO;
import com.pharmajava.model.Produit;
import com.pharmajava.model.Vente;
import com.pharmajava.utils.PrintUtils;
import com.pharmajava.model.Pharmacien;
import com.pharmajava.model.ProduitVendu;
import com.pharmajava.model.Client;
import com.pharmajava.dao.ClientDAO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Contrôleur pour gérer les ventes
 */
public class VenteController {
    private static final Logger LOGGER = Logger.getLogger(VenteController.class.getName());

    private final VenteDAO venteDAO;
    private final ProduitDAO produitDAO;
    private final ClientDAO clientDAO;

    /**
     * Constructeur
     */
    public VenteController() {
        this.venteDAO = new VenteDAO();
        this.produitDAO = new ProduitDAO();
        this.clientDAO = new ClientDAO();
    }

    /**
     * Récupère tous les produits disponibles
     * 
     * @return Liste de tous les produits
     */
    public List<Produit> obtenirTousProduits() {
        try {
            return produitDAO.obtenirTous();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération des produits", e);
            return new ArrayList<>();
        }
    }

    /**
     * Recherche des produits par mot-clé
     * 
     * @param motCle Le mot-clé de recherche
     * @return Liste des produits correspondants
     */
    public List<Produit> rechercherProduits(String motCle) {
        System.out.println("VenteController.rechercherProduits: " + motCle);
        try {
            List<Produit> resultats = produitDAO.rechercher(motCle);
            System.out.println("Résultats trouvés: " + resultats.size());
            return resultats;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la recherche de produits", e);
            System.err.println("Erreur lors de la recherche: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Récupère un produit par son ID
     * 
     * @param id L'ID du produit à récupérer
     * @return Le produit correspondant ou null si non trouvé
     */
    public Produit obtenirProduitParId(int id) {
        try {
            return produitDAO.obtenirParId(id);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération du produit ID=" + id, e);
            return null;
        }
    }

    /**
     * Vérifie si un produit a suffisamment de stock disponible
     * 
     * @param produitId ID du produit
     * @param quantite  Quantité demandée
     * @return true si le stock est suffisant, false sinon
     */
    public boolean verifierStockSuffisant(int produitId, int quantite) {
        try {
            Produit produit = produitDAO.obtenirParId(produitId);
            return produit != null && produit.getQuantiteEnStock() >= quantite;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la vérification du stock", e);
            return false;
        }
    }

    /**
     * Enregistre une nouvelle vente
     * 
     * @param produitsVendus Liste des produits vendus
     * @param montantPercu   Montant perçu du client
     * @param pharmacien     Pharmacien qui a effectué la vente
     * @param client         Client associé à la vente (optionnel, peut être null)
     * @return La vente enregistrée avec son ID, ou null en cas d'erreur
     */
    public Vente enregistrerVente(List<ProduitVendu> produitsVendus, BigDecimal montantPercu,
            Pharmacien pharmacien, Client client) {
        if (produitsVendus == null || produitsVendus.isEmpty()) {
            LOGGER.log(Level.WARNING, "Tentative d'enregistrement d'une vente sans produits");
            return null;
        }

        try {
            // Créer une nouvelle vente
            Vente vente = new Vente();
            vente.setDateVente(LocalDateTime.now());
            vente.setPharmacien(pharmacien);
            vente.setMontantPercu(montantPercu);

            // Associer le client si présent
            if (client != null) {
                vente.setClient(client);
            }

            // Ajouter les produits vendus
            for (ProduitVendu pv : produitsVendus) {
                vente.ajouterProduitVendu(pv);
            }

            // Calculer le montant à rendre
            vente.calculerMontantRendu();

            // Enregistrer la vente
            return venteDAO.ajouter(vente);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de l'enregistrement d'une vente", e);
            return null;
        }
    }

    /**
     * Imprime le ticket d'une vente
     * 
     * @param venteId L'ID de la vente
     * @return true si l'impression a réussi, false sinon
     */
    public boolean imprimerTicketVente(int venteId) {
        try {
            Vente vente = venteDAO.obtenirParId(venteId);
            if (vente == null) {
                LOGGER.warning("Impossible d'imprimer le ticket: Vente non trouvée (ID=" + venteId + ")");
                return false;
            }

            return PrintUtils.imprimerTicketVente(vente);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de l'impression du ticket de vente", e);
            return false;
        }
    }

    /**
     * Génère un ticket de vente au format texte
     * 
     * @param venteId L'ID de la vente
     * @return Le contenu du ticket au format texte ou null en cas d'erreur
     */
    public String genererTicketTexte(int venteId) {
        try {
            Vente vente = venteDAO.obtenirParId(venteId);
            if (vente == null) {
                LOGGER.warning("Impossible de générer le ticket texte: Vente non trouvée (ID=" + venteId + ")");
                return null;
            }

            return PrintUtils.genererTicketTexte(vente);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la génération du ticket texte", e);
            return null;
        }
    }

    /**
     * Récupère toutes les ventes
     * 
     * @return Liste de toutes les ventes
     */
    public List<Vente> obtenirToutesVentes() {
        try {
            return venteDAO.obtenirToutes();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération des ventes", e);
            return new ArrayList<>();
        }
    }

    /**
     * Obtenir une vente par son ID
     * 
     * @param id L'ID de la vente
     * @return La vente avec tous ses détails, ou null si non trouvée
     */
    public Vente obtenirVenteParId(int id) {
        try {
            return venteDAO.obtenirParId(id);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération de la vente ID=" + id, e);
            return null;
        }
    }

    /**
     * Récupère les ventes effectuées aujourd'hui
     * 
     * @return Liste des ventes du jour
     */
    public List<Vente> obtenirVentesAujourdhui() {
        try {
            java.time.LocalDateTime debut = java.time.LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
            java.time.LocalDateTime fin = java.time.LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);

            return venteDAO.obtenirParPeriode(debut, fin);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération des ventes du jour", e);
            return new ArrayList<>();
        }
    }

    /**
     * Récupère les ventes effectuées sur une période donnée
     * 
     * @param debut Date de début de la période
     * @param fin   Date de fin de la période
     * @return Liste des ventes de la période
     */
    public List<Vente> obtenirVentesParPeriode(java.time.LocalDateTime debut, java.time.LocalDateTime fin) {
        try {
            return venteDAO.obtenirParPeriode(debut, fin);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération des ventes par période", e);
            return new ArrayList<>();
        }
    }

    /**
     * Recherche un produit par son nom exact
     * 
     * @param nom Le nom du produit à rechercher
     * @return Le produit trouvé ou null si aucun produit ne correspond
     */
    public Produit rechercherProduitParNom(String nom) {
        try {
            return produitDAO.obtenirParNom(nom);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la recherche du produit par nom: " + nom, e);
            return null;
        }
    }

    /**
     * Recherche des clients par terme de recherche
     * 
     * @param recherche Terme de recherche pour les clients (nom, prénom, téléphone)
     * @return Liste des clients correspondant à la recherche
     */
    public List<Client> rechercherClients(String recherche) {
        return clientDAO.rechercher(recherche);
    }

    /**
     * Obtenir tous les clients
     * 
     * @return Liste de tous les clients
     */
    public List<Client> obtenirTousLesClients() {
        return clientDAO.obtenirTous();
    }

    /**
     * Récupère les ventes d'un client spécifique
     * 
     * @param clientId L'ID du client
     * @return Liste des ventes du client
     */
    public List<Vente> obtenirVentesParClient(Integer clientId) {
        try {
            return venteDAO.obtenirParClient(clientId);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération des ventes du client ID=" + clientId, e);
            return new ArrayList<>();
        }
    }
}