package com.pharmajava.view;

import com.pharmajava.controller.VenteController;
import com.pharmajava.model.Client;
import com.pharmajava.model.Vente;
import com.pharmajava.utils.SessionManager;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

/**
 * Cette classe modifie la vue des ventes pour inclure le sélecteur de client.
 * Elle doit être appelée après l'initialisation de VenteView pour intégrer
 * le sélecteur de clients dans l'interface de vente.
 */
public class ModificationVenteView {
    private static final String NOM_METHODE_VALIDATION = "validerVente";

    /**
     * Intègre le sélecteur de client dans la vue des ventes
     * 
     * @param venteView      La vue des ventes à modifier
     * @param consumerClient Le consumer qui sera appelé lors de la sélection d'un
     *                       client
     */
    public static void integrerSelecteurClient(JPanel venteView, Consumer<Client> consumerClient) {
        try {
            // Obtenir le panneau des informations de la vente (en bas à droite
            // généralement)
            Component[] components = venteView.getComponents();
            JSplitPane splitPane = null;

            // Chercher le splitPane principal
            for (Component component : components) {
                if (component instanceof JSplitPane) {
                    splitPane = (JSplitPane) component;
                    break;
                }
            }

            if (splitPane != null) {
                // Obtenir le panneau droit (celui qui contient les informations de paiement)
                Component rightComponent = splitPane.getRightComponent();

                if (rightComponent instanceof JPanel) {
                    JPanel rightPanel = (JPanel) rightComponent;

                    // Chercher le panneau de paiement (il devrait contenir les champs de montant)
                    locateAndAddClientSelector(rightPanel, consumerClient);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erreur lors de l'intégration du sélecteur de client: " + e.getMessage());
        }
    }

    /**
     * Recherche et modifie le panneau de paiement pour ajouter le sélecteur de
     * client
     */
    private static void locateAndAddClientSelector(JPanel panel, Consumer<Client> consumerClient) {
        // Parcourir récursivement les composants pour trouver le panneau de paiement
        Component[] components = panel.getComponents();

        for (Component component : components) {
            if (component instanceof JPanel) {
                JPanel childPanel = (JPanel) component;

                // Vérifier si c'est le panneau de paiement (contient généralement des JLabels
                // "Montant")
                boolean isPanelPaiement = false;
                Component[] childComponents = childPanel.getComponents();

                for (Component childComponent : childComponents) {
                    if (childComponent instanceof JLabel) {
                        JLabel label = (JLabel) childComponent;
                        if (label.getText() != null &&
                                (label.getText().contains("Montant") || label.getText().contains("Total"))) {
                            isPanelPaiement = true;
                            break;
                        }
                    }
                }

                if (isPanelPaiement) {
                    // Créer et ajouter le sélecteur de client avant le panneau de paiement
                    ClientSelectorPanel clientSelector = new ClientSelectorPanel();
                    clientSelector.setOnClientSelectedCallback(consumerClient);

                    // Ajouter le sélecteur au panneau parent (pour qu'il soit au-dessus du panneau
                    // de paiement)
                    Container parent = childPanel.getParent();
                    if (parent instanceof JPanel) {
                        JPanel parentPanel = (JPanel) parent;
                        parentPanel.add(clientSelector, BorderLayout.NORTH);
                        parentPanel.revalidate();
                        parentPanel.repaint();
                    }
                    return;
                }

                // Continuer la recherche récursivement
                locateAndAddClientSelector(childPanel, consumerClient);
            }
        }
    }

    /**
     * Récupère le client sélectionné dans le panneau de vente
     */
    public static Client getClientFromVenteView(JPanel venteView) {
        try {
            Component[] components = venteView.getComponents();

            for (Component component : components) {
                if (component instanceof JSplitPane) {
                    JSplitPane splitPane = (JSplitPane) component;
                    Component rightComponent = splitPane.getRightComponent();

                    if (rightComponent instanceof JPanel) {
                        JPanel rightPanel = (JPanel) rightComponent;
                        return findClientSelector(rightPanel);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Recherche récursivement le sélecteur de client
     */
    private static Client findClientSelector(JPanel panel) {
        Component[] components = panel.getComponents();

        for (Component component : components) {
            if (component instanceof ClientSelectorPanel) {
                ClientSelectorPanel selector = (ClientSelectorPanel) component;
                return selector.getClientSelectionne();
            } else if (component instanceof JPanel) {
                Client client = findClientSelector((JPanel) component);
                if (client != null) {
                    return client;
                }
            }
        }

        return null;
    }

    /**
     * Modifie le contrôleur de vente pour inclure le client lors de
     * l'enregistrement
     * 
     * @param venteView  La vue des ventes à modifier
     * @param controller Le contrôleur de vente
     */
    public static void modifierEnregistrementVente(VenteController controller, JPanel venteView) {
        // Cette méthode serait implémentée si nécessaire pour intercepter
        // l'enregistrement
        // de vente et inclure le client. Cependant, elle n'est pas utilisée car nous
        // préférons modifier la méthode validerVente directement dans VenteView.
    }
}