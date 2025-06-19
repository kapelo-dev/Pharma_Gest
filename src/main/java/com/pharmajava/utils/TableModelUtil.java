package com.pharmajava.utils;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Vector;

/**
 * Classe utilitaire pour faciliter la manipulation des TableModel dans Swing
 */
public class TableModelUtil {
    
    /**
     * Efface toutes les lignes d'un TableModel
     * 
     * @param model Le TableModel à vider
     */
    public static void viderTable(DefaultTableModel model) {
        while (model.getRowCount() > 0) {
            model.removeRow(0);
        }
    }
    
    /**
     * Ajoute une ligne à un TableModel à partir d'un tableau d'objets
     * 
     * @param model Le TableModel auquel ajouter une ligne
     * @param data Les données à ajouter
     */
    public static void ajouterLigne(DefaultTableModel model, Object[] data) {
        model.addRow(data);
    }
    
    /**
     * Convertit un tableau d'objets en Vector (utilisé par DefaultTableModel)
     * 
     * @param data Les données à convertir
     * @return Un Vector contenant les données
     */
    public static Vector<Object> convertirEnVector(Object[] data) {
        Vector<Object> vector = new Vector<>();
        for (Object item : data) {
            vector.add(item);
        }
        return vector;
    }
    
    /**
     * Formate les valeurs numériques pour l'affichage dans un tableau
     * 
     * @param value La valeur à formater
     * @return La valeur formatée sous forme de chaîne
     */
    public static String formaterValeur(Object value) {
        if (value == null) {
            return "";
        }
        
        if (value instanceof Number) {
            return String.format("%,.2f", ((Number)value).doubleValue());
        }
        
        return value.toString();
    }
    
    /**
     * Met en évidence les lignes du tableau où le stock est inférieur au seuil d'alerte
     * 
     * @param table Le tableau à mettre en évidence
     * @param stockColumnIndex L'index de la colonne contenant la quantité en stock
     * @param thresholdColumnIndex L'index de la colonne contenant le seuil d'alerte
     */
    public static void highlightRowsWithLowStock(JTable table, int stockColumnIndex, int thresholdColumnIndex) {
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);
                
                // Vérifier si le stock est inférieur au seuil d'alerte
                try {
                    int stock = Integer.parseInt(table.getValueAt(row, stockColumnIndex).toString());
                    int threshold = Integer.parseInt(table.getValueAt(row, thresholdColumnIndex).toString());
                    
                    if (stock <= threshold) {
                        c.setBackground(isSelected ? new Color(255, 200, 200) : new Color(255, 230, 230));
                    } else {
                        c.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
                    }
                } catch (Exception e) {
                    c.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
                }
                
                return c;
            }
        });
    }
} 