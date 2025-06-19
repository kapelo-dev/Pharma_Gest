package com.pharmajava.controller;

import com.pharmajava.dao.ClientDAO;
import com.pharmajava.model.Client;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Contrôleur pour la gestion des clients
 */
public class ClientController {
    private static final Logger LOGGER = Logger.getLogger(ClientController.class.getName());
    private final ClientDAO clientDAO;

    /**
     * Constructeur par défaut
     */
    public ClientController() {
        this.clientDAO = new ClientDAO();
    }

    /**
     * Ajoute un nouveau client
     * 
     * @param client Le client à ajouter
     * @return Le client avec son ID généré, ou null en cas d'erreur
     */
    public Client ajouterClient(Client client) {
        try {
            return clientDAO.ajouter(client);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de l'ajout d'un client", e);
            return null;
        }
    }

    /**
     * Met à jour un client existant
     * 
     * @param client Le client à mettre à jour
     * @return true si la mise à jour a réussi, false sinon
     */
    public boolean mettreAJourClient(Client client) {
        try {
            return clientDAO.mettreAJour(client);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la mise à jour d'un client", e);
            return false;
        }
    }

    /**
     * Supprime un client par son ID
     * 
     * @param id L'ID du client à supprimer
     * @return true si la suppression a réussi, false sinon
     */
    public boolean supprimerClient(int id) {
        try {
            return clientDAO.supprimer(id);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la suppression d'un client", e);
            return false;
        }
    }

    /**
     * Récupère un client par son ID
     * 
     * @param id L'ID du client
     * @return Le client, ou null si non trouvé
     */
    public Client obtenirClientParId(int id) {
        try {
            return clientDAO.obtenirParId(id);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération d'un client par ID", e);
            return null;
        }
    }

    /**
     * Récupère tous les clients
     * 
     * @return Liste de tous les clients
     */
    public List<Client> obtenirTousLesClients() {
        try {
            return clientDAO.obtenirTous();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération de tous les clients", e);
            return List.of();
        }
    }

    /**
     * Recherche des clients par nom, prénom ou téléphone
     * 
     * @param recherche La chaîne de recherche
     * @return Liste des clients correspondant à la recherche
     */
    public List<Client> rechercherClients(String recherche) {
        try {
            return clientDAO.rechercher(recherche);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la recherche de clients", e);
            return List.of();
        }
    }

    /**
     * Recherche des clients spécifiquement pour l'auto-complétion
     * Gère des cas particuliers comme les recherches courtes
     * 
     * @param terme Le terme de recherche saisi
     * @return Liste des clients correspondant, avec gestion spéciale des cas
     *         limites
     */
    public List<Client> rechercherClientsAutoCompletion(String terme) {
        try {
            // Si le terme est vide, retourner les premiers clients
            if (terme == null || terme.trim().isEmpty()) {
                return clientDAO.obtenirTous().stream()
                        .limit(10)
                        .collect(java.util.stream.Collectors.toList());
            }

            // Si c'est un terme court (1 caractère), le rechercher comme préfixe
            if (terme.trim().length() == 1) {
                String prefixe = terme.trim() + "%";
                List<Client> resultatsPrefixe = clientDAO.rechercher(prefixe);

                if (!resultatsPrefixe.isEmpty()) {
                    return resultatsPrefixe;
                }

                // Si aucun résultat par préfixe, essayer une recherche contenant
                return clientDAO.rechercher("%" + terme.trim() + "%");
            }

            // Recherche normale
            List<Client> resultats = clientDAO.rechercher(terme);

            // Si pas de résultats, essayer avec des jokers
            if (resultats.isEmpty() && !terme.contains("%")) {
                resultats = clientDAO.rechercher("%" + terme.trim() + "%");
            }

            return resultats;

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la recherche de clients pour auto-complétion", e);
            return List.of();
        }
    }
}