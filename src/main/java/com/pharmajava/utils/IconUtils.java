package com.pharmajava.utils;

import javax.swing.*;
import java.awt.*;

/**
 * Utilitaire pour gérer les icônes textuelles universelles
 * Fournit des constantes pour les symboles compatibles avec toutes les polices
 */
public class IconUtils {

    // Icônes pour le menu principal
    public static final String ICON_DASHBOARD = "⌂"; // Maison
    public static final String ICON_PRODUCTS = "□"; // Carré
    public static final String ICON_SALES = "₣"; // Franc CFA
    public static final String ICON_STOCK = "▤"; // Grille
    public static final String ICON_REPORTS = "≡"; // Trois lignes
    public static final String ICON_USERS = "♦"; // Losange
    public static final String ICON_SETTINGS = "⚙"; // Engrenage
    public static final String ICON_LOGOUT = "←"; // Flèche gauche
    public static final String ICON_CLIENTS = "👥"; // Deux personnes

    // Icônes pour les statistiques
    public static final String ICON_PRODUCT_COUNT = "□"; // Carré
    public static final String ICON_SALES_COUNT = "₣"; // Franc CFA
    public static final String ICON_EXPIRED = "!"; // Point d'exclamation
    public static final String ICON_MONEY = "₣"; // Franc CFA

    // Icônes pour les alertes
    public static final String ICON_WARNING = "!"; // Point d'exclamation
    public static final String ICON_SUCCESS = "✓"; // Coche
    public static final String ICON_INFO = "i"; // Information
    public static final String ICON_MEDICINE = "†"; // Croix

    // Icônes pour d'autres éléments
    public static final String ICON_USER = "♦"; // Losange
    public static final String ICON_CALENDAR = "▣"; // Calendrier
    public static final String ICON_TIME = "◷"; // Horloge

    /**
     * Crée un JLabel avec une icône textuelle
     * 
     * @param icon  L'icône à afficher
     * @param size  La taille de la police
     * @param color La couleur de l'icône
     * @return Un JLabel configuré avec l'icône
     */
    public static JLabel createIconLabel(String icon, int size, Color color) {
        JLabel label = new JLabel(icon);
        label.setFont(new Font("Dialog", Font.BOLD, size));
        label.setForeground(color);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        return label;
    }
}