package com.pharmajava.utils;

import javax.swing.*;
import java.awt.*;

/**
 * Classe utilitaire pour la gestion des boîtes de dialogue
 * avec des textes adaptés à la langue française
 */
public class DialogUtils {

    /**
     * Affiche une boîte de dialogue de confirmation avec des boutons en français
     * 
     * @param parent  Composant parent (pour le centrage)
     * @param message Message à afficher
     * @param titre   Titre de la boîte de dialogue
     * @return JOptionPane.YES_OPTION si l'utilisateur a cliqué sur Oui,
     *         JOptionPane.NO_OPTION sinon
     */
    public static int afficherConfirmation(Component parent, String message, String titre) {
        // Création de boutons personnalisés en français
        Object[] options = { "Oui", "Non" };

        return JOptionPane.showOptionDialog(
                parent,
                message,
                titre,
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[1] // Option "Non" par défaut
        );
    }

    /**
     * Affiche une boîte de dialogue de confirmation avec trois boutons en français
     * (Oui/Non/Annuler)
     * 
     * @param parent    Composant parent (pour le centrage)
     * @param message   Message à afficher
     * @param titre     Titre de la boîte de dialogue
     * @param typeIcone Type d'icône à afficher (utiliser les constantes
     *                  JOptionPane.WARNING_MESSAGE, etc.)
     * @return JOptionPane.YES_OPTION, JOptionPane.NO_OPTION ou
     *         JOptionPane.CANCEL_OPTION
     */
    public static int afficherConfirmationOuiNonAnnuler(Component parent, String message, String titre, int typeIcone) {
        // Création de boutons personnalisés en français
        Object[] options = { "Oui", "Non", "Annuler" };

        return JOptionPane.showOptionDialog(
                parent,
                message,
                titre,
                JOptionPane.YES_NO_CANCEL_OPTION,
                typeIcone,
                null,
                options,
                options[2] // Option "Annuler" par défaut
        );
    }

    /**
     * Affiche une boîte de dialogue d'information avec un bouton OK en français
     * 
     * @param parent  Composant parent (pour le centrage)
     * @param message Message à afficher
     * @param titre   Titre de la boîte de dialogue
     */
    public static void afficherInformation(Component parent, String message, String titre) {
        JOptionPane.showMessageDialog(
                parent,
                message,
                titre,
                JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Affiche une boîte de dialogue d'erreur avec un bouton OK en français
     * 
     * @param parent  Composant parent (pour le centrage)
     * @param message Message à afficher
     * @param titre   Titre de la boîte de dialogue
     */
    public static void afficherErreur(Component parent, String message, String titre) {
        JOptionPane.showMessageDialog(
                parent,
                message,
                titre,
                JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Affiche une boîte de dialogue d'avertissement avec un bouton OK en français
     * 
     * @param parent  Composant parent (pour le centrage)
     * @param message Message à afficher
     * @param titre   Titre de la boîte de dialogue
     */
    public static void afficherAvertissement(Component parent, String message, String titre) {
        JOptionPane.showMessageDialog(
                parent,
                message,
                titre,
                JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Affiche une boîte de dialogue de confirmation.
     * 
     * @param parent      Le composant parent (pour le positionnement de la boîte de
     *                    dialogue)
     * @param message     Le message à afficher
     * @param titre       Le titre de la boîte de dialogue
     * @param typeOption  Le type d'options (par exemple, JOptionPane.YES_NO_OPTION)
     * @param typeMessage Le type de message (par exemple,
     *                    JOptionPane.WARNING_MESSAGE)
     * @return La réponse de l'utilisateur (par exemple, JOptionPane.YES_OPTION)
     */
    public static int afficherConfirmation(JComponent parent, String message, String titre,
            int typeOption, int typeMessage) {
        // Création de boutons personnalisés en français
        Object[] options = (typeOption == JOptionPane.YES_NO_CANCEL_OPTION) ? new Object[] { "Oui", "Non", "Annuler" }
                : new Object[] { "Oui", "Non" };

        return JOptionPane.showOptionDialog(
                parent,
                message,
                titre,
                typeOption,
                typeMessage,
                null,
                options,
                options[0] // Option "Oui" par défaut
        );
    }

    /**
     * Version simplifiée qui utilise YES_NO_OPTION et WARNING_MESSAGE par défaut.
     * 
     * @param parent  Le composant parent (pour le positionnement de la boîte de
     *                dialogue)
     * @param message Le message à afficher
     * @param titre   Le titre de la boîte de dialogue
     * @return La réponse de l'utilisateur (par exemple, JOptionPane.YES_OPTION)
     */
    public static int afficherConfirmation(JComponent parent, String message, String titre) {
        return afficherConfirmation(
                parent,
                message,
                titre,
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
    }
}