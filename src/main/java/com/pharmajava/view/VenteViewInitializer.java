package com.pharmajava.view;

import com.pharmajava.model.Client;

/**
 * Classe utilitaire pour initialiser les nouvelles fonctionnalités
 * dans la vue des ventes, notamment le sélecteur de client.
 */
public class VenteViewInitializer {

    /**
     * Client sélectionné pour la vente actuelle
     */
    private static Client clientSelectionne = null;

    /**
     * Initialise les nouvelles fonctionnalités dans la vue des ventes
     * 
     * @param venteView La vue des ventes à initialiser
     */
    public static void initialize(VenteView venteView) {
        // Intégrer le sélecteur de client dans la vue des ventes
        ModificationVenteView.integrerSelecteurClient(venteView, client -> {
            // Stocker le client sélectionné
            clientSelectionne = client;
        });
    }

    /**
     * Récupère le client sélectionné pour la vente actuelle
     * 
     * @return Le client sélectionné ou null si aucun client n'est sélectionné
     */
    public static Client getClientSelectionne() {
        return clientSelectionne;
    }

    /**
     * Réinitialise le client sélectionné
     */
    public static void resetClientSelectionne() {
        clientSelectionne = null;
    }
}