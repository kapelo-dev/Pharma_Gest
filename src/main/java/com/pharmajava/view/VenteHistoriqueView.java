package com.pharmajava.view;

import com.pharmajava.controller.VenteController;
import com.pharmajava.model.Produit;
import com.pharmajava.model.ProduitVendu;
import com.pharmajava.model.Vente;
import com.pharmajava.utils.DateUtil;
import com.pharmajava.utils.TableModelUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * Interface d'affichage de l'historique des ventes
 */
public class VenteHistoriqueView extends JPanel {
    private final VenteController controller;

    // Composants de l'interface
    private JTable tableVentes;
    private DefaultTableModel modelVentes;
    
    private JTable tableDetailsVente;
    private DefaultTableModel modelDetailsVente;
    
    private JComboBox<String> comboPeriode;
    private JDatePicker dateDebut;
    private JDatePicker dateFin;
    private JButton boutonRechercher;
    
    private JLabel labelTotalVentes;
    private JLabel labelMontantTotal;
    
    /**
     * Constructeur
     */
    public VenteHistoriqueView() {
        this.controller = new VenteController();
        
        initialiserComposants();
        initialiserEvenements();
        chargerVentesAujourdhui();
    }
    
    /**
     * Initialise les composants de l'interface
     */
    private void initialiserComposants() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Titre
        JLabel titreLabel = new JLabel("Historique des Ventes");
        titreLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titreLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        add(titreLabel, BorderLayout.NORTH);
        
        // Panneau principal
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        
        // Panneau de filtres
        JPanel filtresPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filtresPanel.setBorder(BorderFactory.createTitledBorder("Filtres"));
        
        JLabel periodeLabel = new JLabel("Période:");
        comboPeriode = new JComboBox<>(new String[]{"Aujourd'hui", "Hier", "Cette semaine", "Ce mois", "Personnalisée"});
        
        JLabel dateDebutLabel = new JLabel("Du:");
        dateDebut = new JDatePicker(LocalDate.now());
        dateDebut.setEnabled(false);
        
        JLabel dateFinLabel = new JLabel("Au:");
        dateFin = new JDatePicker(LocalDate.now());
        dateFin.setEnabled(false);
        
        boutonRechercher = new JButton("Rechercher");
        
        filtresPanel.add(periodeLabel);
        filtresPanel.add(comboPeriode);
        filtresPanel.add(dateDebutLabel);
        filtresPanel.add(dateDebut);
        filtresPanel.add(dateFinLabel);
        filtresPanel.add(dateFin);
        filtresPanel.add(boutonRechercher);
        
        mainPanel.add(filtresPanel, BorderLayout.NORTH);
        
        // Table des ventes
        String[] entetesVentes = {"ID", "Date", "Nombre de produits", "Montant total", "Vendeur"};
        modelVentes = new DefaultTableModel(entetesVentes, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tableVentes = new JTable(modelVentes);
        tableVentes.getTableHeader().setReorderingAllowed(false);
        tableVentes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane scrollVentes = new JScrollPane(tableVentes);
        scrollVentes.setBorder(BorderFactory.createTitledBorder("Liste des ventes"));
        scrollVentes.setPreferredSize(new Dimension(600, 200));
        
        mainPanel.add(scrollVentes, BorderLayout.CENTER);
        
        // Panneau d'informations
        JPanel infoPanel = new JPanel(new BorderLayout());
        
        // Panel pour les statistiques
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statsPanel.setBorder(BorderFactory.createTitledBorder("Statistiques"));
        
        JLabel totalVentesLabel = new JLabel("Nombre de ventes:");
        labelTotalVentes = new JLabel("0");
        labelTotalVentes.setFont(new Font("Arial", Font.BOLD, 14));
        
        JLabel montantTotalLabel = new JLabel("Montant total:");
        labelMontantTotal = new JLabel("0 FCFA");
        labelMontantTotal.setFont(new Font("Arial", Font.BOLD, 14));
        
        statsPanel.add(totalVentesLabel);
        statsPanel.add(labelTotalVentes);
        statsPanel.add(Box.createHorizontalStrut(20));
        statsPanel.add(montantTotalLabel);
        statsPanel.add(labelMontantTotal);
        
        infoPanel.add(statsPanel, BorderLayout.NORTH);
        
        // Table des détails d'une vente
        String[] entetesDetails = {"Produit", "Prix unitaire", "Quantité", "Sous-total"};
        modelDetailsVente = new DefaultTableModel(entetesDetails, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tableDetailsVente = new JTable(modelDetailsVente);
        tableDetailsVente.getTableHeader().setReorderingAllowed(false);
        
        JScrollPane scrollDetails = new JScrollPane(tableDetailsVente);
        scrollDetails.setBorder(BorderFactory.createTitledBorder("Détails de la vente"));
        scrollDetails.setPreferredSize(new Dimension(600, 150));
        
        infoPanel.add(scrollDetails, BorderLayout.CENTER);
        
        mainPanel.add(infoPanel, BorderLayout.SOUTH);
        
        add(mainPanel, BorderLayout.CENTER);
    }
    
    /**
     * Initialise les événements
     */
    private void initialiserEvenements() {
        // Changer la période
        comboPeriode.addActionListener(e -> {
            String periode = (String) comboPeriode.getSelectedItem();
            if ("Personnalisée".equals(periode)) {
                dateDebut.setEnabled(true);
                dateFin.setEnabled(true);
            } else {
                dateDebut.setEnabled(false);
                dateFin.setEnabled(false);
            }
        });
        
        // Rechercher les ventes
        boutonRechercher.addActionListener(e -> rechercherVentes());
        
        // Sélectionner une vente pour voir les détails
        tableVentes.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = tableVentes.getSelectedRow();
                if (selectedRow >= 0) {
                    int venteId = (int) tableVentes.getValueAt(selectedRow, 0);
                    chargerDetailsVente(venteId);
                }
            }
        });
    }
    
    /**
     * Charge les ventes d'aujourd'hui
     */
    private void chargerVentesAujourdhui() {
        TableModelUtil.viderTable(modelVentes);
        TableModelUtil.viderTable(modelDetailsVente);
        
        List<Vente> ventes = controller.obtenirVentesAujourdhui();
        afficherVentes(ventes);
    }
    
    /**
     * Recherche les ventes selon les critères sélectionnés
     */
    private void rechercherVentes() {
        TableModelUtil.viderTable(modelVentes);
        TableModelUtil.viderTable(modelDetailsVente);
        
        String periode = (String) comboPeriode.getSelectedItem();
        LocalDateTime debut;
        LocalDateTime fin;
        
        switch (periode) {
            case "Aujourd'hui":
                debut = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
                fin = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
                break;
                
            case "Hier":
                debut = LocalDateTime.of(LocalDate.now().minusDays(1), LocalTime.MIN);
                fin = LocalDateTime.of(LocalDate.now().minusDays(1), LocalTime.MAX);
                break;
                
            case "Cette semaine":
                debut = LocalDateTime.of(LocalDate.now().minusDays(LocalDate.now().getDayOfWeek().getValue() - 1), LocalTime.MIN);
                fin = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
                break;
                
            case "Ce mois":
                debut = LocalDateTime.of(LocalDate.now().withDayOfMonth(1), LocalTime.MIN);
                fin = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
                break;
                
            case "Personnalisée":
                debut = LocalDateTime.of(dateDebut.getDate(), LocalTime.MIN);
                fin = LocalDateTime.of(dateFin.getDate(), LocalTime.MAX);
                break;
                
            default:
                debut = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
                fin = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
                break;
        }
        
        List<Vente> ventes = controller.obtenirVentesParPeriode(debut, fin);
        afficherVentes(ventes);
    }
    
    /**
     * Affiche les ventes dans la table
     * 
     * @param ventes Liste des ventes à afficher
     */
    private void afficherVentes(List<Vente> ventes) {
        TableModelUtil.viderTable(modelVentes);
        
        if (ventes.isEmpty()) {
            labelTotalVentes.setText("0");
            labelMontantTotal.setText("0 FCFA");
            return;
        }
        
        java.math.BigDecimal montantTotal = java.math.BigDecimal.ZERO;
        
        for (Vente vente : ventes) {
            Object[] ligne = {
                vente.getId(),
                DateUtil.formaterDateTime(vente.getDateVente()),
                vente.getNombreProduits(),
                vente.getMontantTotal() + " FCFA",
                vente.getPharmacien() != null ? vente.getPharmacien().getNomComplet() : "Inconnu"
            };
            modelVentes.addRow(ligne);
            
            montantTotal = montantTotal.add(vente.getMontantTotal());
        }
        
        labelTotalVentes.setText(String.valueOf(ventes.size()));
        labelMontantTotal.setText(montantTotal + " FCFA");
    }
    
    /**
     * Charge les détails d'une vente
     * 
     * @param venteId ID de la vente
     */
    private void chargerDetailsVente(int venteId) {
        TableModelUtil.viderTable(modelDetailsVente);
        
        Vente vente = controller.obtenirVenteParId(venteId);
        if (vente == null) {
            return;
        }
        
        List<ProduitVendu> produitsVendus = vente.getProduitsVendus();
        if (produitsVendus == null) {
            return;
        }
        
        for (ProduitVendu produitVendu : produitsVendus) {
            Produit produit = produitVendu.getProduit();
            if (produit == null) {
                continue;
            }
            
            Object[] ligne = {
                produit.getNom(),
                produitVendu.getPrixUnitaire() + " FCFA",
                produitVendu.getQuantite(),
                produitVendu.getPrixTotal() + " FCFA"
            };
            modelDetailsVente.addRow(ligne);
        }
    }
    
    /**
     * Classe interne pour simuler un sélecteur de date simple
     */
    private class JDatePicker extends JPanel {
        private final JTextField dateField;
        private LocalDate date;
        
        public JDatePicker(LocalDate initialDate) {
            setLayout(new BorderLayout());
            this.date = initialDate;
            
            dateField = new JTextField(DateUtil.formaterDate(date));
            dateField.setColumns(10);
            dateField.setEditable(false);
            
            JButton button = new JButton("...");
            button.addActionListener(e -> showDatePicker());
            
            add(dateField, BorderLayout.CENTER);
            add(button, BorderLayout.EAST);
        }
        
        public LocalDate getDate() {
            return date;
        }
        
        public void setDate(LocalDate date) {
            this.date = date;
            dateField.setText(DateUtil.formaterDate(date));
        }
        
        @Override
        public void setEnabled(boolean enabled) {
            super.setEnabled(enabled);
            dateField.setEnabled(enabled);
        }
        
        private void showDatePicker() {
            // Dans un vrai projet, on utiliserait un vrai sélecteur de date
            // Ici, on simule en demandant la date au format texte
            String input = JOptionPane.showInputDialog(this, 
                "Entrez la date (format: jj/mm/aaaa):", 
                DateUtil.formaterDate(date));
            
            if (input != null && !input.isEmpty()) {
                try {
                    LocalDate newDate = DateUtil.parseLocalDate(input);
                    if (newDate != null) {
                        setDate(newDate);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, 
                        "Format de date invalide. Utilisez le format jj/mm/aaaa.", 
                        "Erreur", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
} 