package com.pharmajava.view;

import com.pharmajava.controller.ProduitController;
import com.pharmajava.model.Produit;
import com.pharmajava.utils.TableModelUtil;
import com.pharmajava.utils.DialogUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Vue pour la gestion des produits dans l'application
 */
public class ProduitView extends JPanel {
    private static final Logger LOGGER = Logger.getLogger(ProduitView.class.getName());

    // Couleurs de l'interface
    private final Color PRIMARY_COLOR = new Color(41, 128, 185); // Bleu principal
    private final Color SECONDARY_COLOR = new Color(52, 152, 219); // Bleu secondaire
    private final Color ACCENT_COLOR = new Color(46, 204, 113); // Vert accent
    private final Color WARNING_COLOR = new Color(241, 196, 15); // Jaune avertissement
    private final Color DANGER_COLOR = new Color(231, 76, 60); // Rouge danger
    private final Color TEXT_COLOR = new Color(44, 62, 80); // Texte foncé
    private final Color LIGHT_COLOR = new Color(236, 240, 241); // Fond clair
    private final Color WHITE_COLOR = new Color(255, 255, 255); // Blanc
    private final Color CARD_COLOR = new Color(255, 255, 255); // Cartes

    private final ProduitController controller;

    // Composants de l'interface
    private JTable tableProduits;
    private DefaultTableModel tableModel;
    private JTextField champNom;
    private JTextField champDescription;
    private JTextField champPrixAchat;
    private JTextField champPrixVente;
    private JTextField champStock;
    private JTextField champSeuilAlerte;
    private JTextField champRecherche;
    private JButton boutonAjouter;
    private JButton boutonModifier;
    private JButton boutonSupprimer;
    private JButton boutonEffacer;
    private JButton boutonRechercher;

    private Produit produitSelectionne;

    /**
     * Constructeur de la vue produit
     */
    public ProduitView() {
        this.controller = new ProduitController();
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(LIGHT_COLOR);

        initialiserComposants();
        initialiserEvenements();
        chargerTableProduits();
    }

    /**
     * Initialise les composants de l'interface utilisateur
     */
    private void initialiserComposants() {
        // En-tête avec titre et recherche
        JPanel panneauEntete = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Dégradé de couleur pour l'en-tête
                GradientPaint gradient = new GradientPaint(
                        0, 0, PRIMARY_COLOR,
                        getWidth(), 0, SECONDARY_COLOR);
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2d.dispose();
            }
        };
        panneauEntete.setLayout(new BorderLayout(15, 0));
        panneauEntete.setBorder(new EmptyBorder(10, 15, 10, 15));
        panneauEntete.setPreferredSize(new Dimension(getWidth(), 60));

        JLabel labelTitre = new JLabel("Gestion des Produits");
        labelTitre.setFont(new Font("Segoe UI", Font.BOLD, 22));
        labelTitre.setForeground(WHITE_COLOR);
        labelTitre.setIcon(null);
        labelTitre.setIconTextGap(10);
        panneauEntete.add(labelTitre, BorderLayout.WEST);

        // Panneau de recherche modernisé
        JPanel panneauRecherche = new JPanel();
        panneauRecherche.setOpaque(false);
        panneauRecherche.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 0));

        champRecherche = createStyledTextField("Rechercher un produit...");
        champRecherche.setPreferredSize(new Dimension(220, 32));

        boutonRechercher = createStyledButton("Rechercher");

        panneauRecherche.add(champRecherche);
        panneauRecherche.add(boutonRechercher);
        panneauEntete.add(panneauRecherche, BorderLayout.EAST);

        add(panneauEntete, BorderLayout.NORTH);

        // Tableau des produits modernisé
        String[] entetes = { "ID", "Nom", "Description", "Prix d'achat", "Prix de vente", "Stock", "Seuil d'alerte" };
        tableModel = new DefaultTableModel(entetes, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Rendre toutes les cellules non modifiables
            }
        };

        tableProduits = new JTable(tableModel);
        tableProduits.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableProduits.getTableHeader().setReorderingAllowed(false);

        // Amélioration du style du tableau
        tableProduits.setRowHeight(35);
        tableProduits.setIntercellSpacing(new Dimension(10, 5));
        tableProduits.setShowGrid(false);
        tableProduits.setBackground(CARD_COLOR);
        tableProduits.setForeground(TEXT_COLOR);
        tableProduits.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tableProduits.setSelectionBackground(PRIMARY_COLOR.brighter());
        tableProduits.setSelectionForeground(WHITE_COLOR);

        // Style de l'en-tête du tableau
        JTableHeader header = tableProduits.getTableHeader();
        header.setBackground(PRIMARY_COLOR);
        header.setForeground(WHITE_COLOR);
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setPreferredSize(new Dimension(header.getWidth(), 40));

        // Style par défaut pour toutes les cellules
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (c instanceof JLabel) {
                    JLabel label = (JLabel) c;
                    label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

                    // Vérifier si le stock est inférieur au seuil d'alerte
                    if (column == 5) { // Colonne Stock
                        try {
                            int stock = Integer.parseInt(value.toString());
                            int threshold = Integer.parseInt(table.getValueAt(row, 6).toString()); // Seuil d'alerte

                            if (stock <= threshold) {
                                if (stock == 0) {
                                    if (!isSelected)
                                        c.setBackground(DANGER_COLOR.brighter());
                                } else {
                                    if (!isSelected)
                                        c.setBackground(WARNING_COLOR.brighter());
                                }
                            } else {
                                if (!isSelected)
                                    c.setBackground(table.getBackground());
                            }
                        } catch (Exception e) {
                            if (!isSelected)
                                c.setBackground(table.getBackground());
                        }
                    } else {
                        if (!isSelected)
                            c.setBackground(table.getBackground());
                    }
                }

                return c;
            }
        };
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        // Appliquer le renderer aux colonnes
        for (int i = 0; i < tableProduits.getColumnCount(); i++) {
            tableProduits.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Définir des largeurs préférées pour les colonnes
        tableProduits.getColumnModel().getColumn(0).setPreferredWidth(50); // ID
        tableProduits.getColumnModel().getColumn(1).setPreferredWidth(150); // Nom
        tableProduits.getColumnModel().getColumn(2).setPreferredWidth(200); // Description

        // Panel pour le tableau avec style
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(LIGHT_COLOR);
        tablePanel.setBorder(BorderFactory.createEmptyBorder(20, 5, 20, 5));

        JScrollPane scrollPane = new JScrollPane(tableProduits);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true));
        scrollPane.setBackground(CARD_COLOR);
        scrollPane.getViewport().setBackground(CARD_COLOR);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        add(tablePanel, BorderLayout.CENTER);

        // Formulaire d'ajout/modification modernisé
        JPanel panneauFormulaire = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(CARD_COLOR);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2d.dispose();
            }
        };
        panneauFormulaire.setLayout(new GridBagLayout());
        panneauFormulaire.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(5, 5, 5, 5),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(230, 230, 230), 1, true),
                        BorderFactory.createEmptyBorder(15, 15, 15, 15))));

        JLabel labelFormTitle = new JLabel("Détails du produit");
        labelFormTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        labelFormTitle.setForeground(PRIMARY_COLOR);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Titre du formulaire
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 4;
        gbc.anchor = GridBagConstraints.CENTER;
        panneauFormulaire.add(labelFormTitle, gbc);

        // Séparateur
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 15, 0);
        JSeparator separator = new JSeparator();
        separator.setForeground(new Color(220, 220, 220));
        panneauFormulaire.add(separator, gbc);

        // Réinitialiser les insets
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.gridwidth = 1;

        // Style pour les labels
        Font labelFont = new Font("Segoe UI", Font.BOLD, 14);

        // Ligne 1
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        JLabel labelNom = new JLabel("Nom:");
        labelNom.setFont(labelFont);
        labelNom.setForeground(TEXT_COLOR);
        panneauFormulaire.add(labelNom, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        champNom = createStyledTextField("Nom du produit");
        panneauFormulaire.add(champNom, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0;
        JLabel labelDescription = new JLabel("Description:");
        labelDescription.setFont(labelFont);
        labelDescription.setForeground(TEXT_COLOR);
        panneauFormulaire.add(labelDescription, gbc);

        gbc.gridx = 3;
        gbc.weightx = 1.0;
        champDescription = createStyledTextField("Description du produit");
        panneauFormulaire.add(champDescription, gbc);

        // Ligne 2
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0;
        JLabel labelPrixAchat = new JLabel("Prix d'achat:");
        labelPrixAchat.setFont(labelFont);
        labelPrixAchat.setForeground(TEXT_COLOR);
        panneauFormulaire.add(labelPrixAchat, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        champPrixAchat = createStyledTextField("0.00");
        panneauFormulaire.add(champPrixAchat, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0;
        JLabel labelPrixVente = new JLabel("Prix de vente:");
        labelPrixVente.setFont(labelFont);
        labelPrixVente.setForeground(TEXT_COLOR);
        panneauFormulaire.add(labelPrixVente, gbc);

        gbc.gridx = 3;
        gbc.weightx = 1.0;
        champPrixVente = createStyledTextField("0.00");
        panneauFormulaire.add(champPrixVente, gbc);

        // Ligne 3
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0;
        JLabel labelStock = new JLabel("Stock actuel:");
        labelStock.setFont(labelFont);
        labelStock.setForeground(TEXT_COLOR);
        panneauFormulaire.add(labelStock, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        champStock = createStyledTextField("0");
        champStock.setEditable(false);
        champStock.setBackground(new Color(240, 240, 240));
        panneauFormulaire.add(champStock, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0;
        JLabel labelSeuilAlerte = new JLabel("Seuil d'alerte:");
        labelSeuilAlerte.setFont(labelFont);
        labelSeuilAlerte.setForeground(TEXT_COLOR);
        panneauFormulaire.add(labelSeuilAlerte, gbc);

        gbc.gridx = 3;
        gbc.weightx = 1.0;
        champSeuilAlerte = createStyledTextField("5");
        panneauFormulaire.add(champSeuilAlerte, gbc);

        // Boutons modernisés
        JPanel panneauBoutons = new JPanel();
        panneauBoutons.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panneauBoutons.setOpaque(false);

        boutonAjouter = createActionButton("Ajouter", ACCENT_COLOR);
        boutonModifier = createActionButton("Modifier", PRIMARY_COLOR);
        boutonSupprimer = createActionButton("Supprimer", DANGER_COLOR);
        boutonEffacer = createActionButton("Effacer", new Color(120, 120, 120));

        panneauBoutons.add(boutonAjouter);
        panneauBoutons.add(boutonModifier);
        panneauBoutons.add(boutonSupprimer);
        panneauBoutons.add(boutonEffacer);

        // Désactiver les boutons modifier et supprimer initialement
        boutonModifier.setEnabled(false);
        boutonSupprimer.setEnabled(false);

        // Ligne des boutons
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 4;
        gbc.insets = new Insets(15, 5, 5, 5);
        panneauFormulaire.add(panneauBoutons, gbc);

        JPanel panneauSud = new JPanel(new BorderLayout());
        panneauSud.setOpaque(false);
        panneauSud.add(panneauFormulaire, BorderLayout.CENTER);

        add(panneauSud, BorderLayout.SOUTH);
    }

    /**
     * Initialise les événements pour les composants de l'interface
     */
    private void initialiserEvenements() {
        // Sélection d'un produit dans le tableau
        tableProduits.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tableProduits.getSelectedRow();
                if (row >= 0) {
                    Integer id = (Integer) tableModel.getValueAt(row, 0);
                    produitSelectionne = controller.obtenirProduitParId(id);
                    if (produitSelectionne != null) {
                        remplirFormulaire(produitSelectionne);
                        boutonModifier.setEnabled(true);
                        boutonSupprimer.setEnabled(true);
                        boutonAjouter.setEnabled(false);
                    }
                }
            }
        });

        // Bouton Ajouter
        boutonAjouter.addActionListener((ActionEvent e) -> {
            if (validerFormulaire()) {
                Produit nouveauProduit = extraireFormulaire();
                boolean succes = controller.ajouterProduit(nouveauProduit);
                if (succes) {
                    showNotification("Produit ajouté avec succès", NotificationType.SUCCESS);
                    effacerFormulaire();
                    chargerTableProduits();
                } else {
                    showNotification("Erreur lors de l'ajout du produit", NotificationType.ERROR);
                }
            }
        });

        // Bouton Modifier
        boutonModifier.addActionListener((ActionEvent e) -> {
            if (produitSelectionne != null && validerFormulaire()) {
                Produit produitModifie = extraireFormulaire();
                produitModifie.setId(produitSelectionne.getId());
                produitModifie.setQuantiteEnStock(produitSelectionne.getQuantiteEnStock()); // Préserver le stock

                boolean succes = controller.modifierProduit(produitModifie);
                if (succes) {
                    showNotification("Produit modifié avec succès", NotificationType.SUCCESS);
                    effacerFormulaire();
                    chargerTableProduits();
                } else {
                    showNotification("Erreur lors de la modification du produit", NotificationType.ERROR);
                }
            }
        });

        // Bouton Supprimer
        boutonSupprimer.addActionListener((ActionEvent e) -> {
            if (produitSelectionne != null) {
                // Créer une boîte de dialogue de confirmation personnalisée
                int confirmation = com.pharmajava.utils.DialogUtils.afficherConfirmation(this,
                        "Êtes-vous sûr de vouloir supprimer ce produit ?\n" +
                                "Nom: " + produitSelectionne.getNom() + "\n" +
                                "Cette action est irréversible.",
                        "Confirmation de suppression",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);

                if (confirmation == JOptionPane.YES_OPTION) {
                    // Sauvegarder l'ID du produit à supprimer
                    Integer produitId = produitSelectionne.getId();

                    // Effacer le formulaire et désélectionner avant la suppression
                    effacerFormulaire();

                    // Supprimer le produit
                    boolean succes = controller.supprimerProduit(produitId);
                    if (succes) {
                        showNotification("Produit supprimé avec succès", NotificationType.INFO);

                        // Réinitialiser complètement le tableau et recharger les données
                        tableModel.setRowCount(0);
                        SwingUtilities.invokeLater(() -> {
                            chargerTableProduits();
                            // Force le rafraîchissement complet du tableau
                            tableProduits.invalidate();
                            tableProduits.revalidate();
                            tableProduits.repaint();
                        });
                    } else {
                        showNotification("Erreur lors de la suppression du produit", NotificationType.ERROR);
                    }
                }
            }
        });

        // Bouton Effacer
        boutonEffacer.addActionListener((ActionEvent e) -> {
            effacerFormulaire();
            showNotification("Formulaire effacé avec succès", NotificationType.SUCCESS);
        });

        // Bouton Rechercher
        boutonRechercher.addActionListener((ActionEvent e) -> {
            rechercherProduits();
        });

        // Recherche par appui sur Entrée
        champRecherche.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent evt) {
                if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                    rechercherProduits();
                }
            }
        });
    }

    /**
     * Fonction de recherche de produits
     */
    private void rechercherProduits() {
        String terme = champRecherche.getText().trim();
        if (!terme.isEmpty()) {
            List<Produit> resultats = controller.rechercherProduits(terme);
            actualiserTableAvecResultats(resultats);

            if (resultats.isEmpty()) {
                showNotification("Aucun produit trouvé pour: " + terme, NotificationType.INFO);
            } else {
                showNotification(resultats.size() + " produit(s) trouvé(s)", NotificationType.INFO);
            }
        } else {
            chargerTableProduits(); // Recharger tous les produits si recherche vide
        }
    }

    /**
     * Charge tous les produits dans le tableau et actualise l'affichage
     */
    private void chargerTableProduits() {
        try {
            List<Produit> produits = controller.obtenirTousProduits();
            actualiserTableAvecResultats(produits);

            // Assurer que le tableau est correctement rafraîchi à l'écran
            tableProduits.revalidate();
            tableProduits.repaint();

            // Notifier le modèle que les données ont changé
            tableModel.fireTableDataChanged();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors du chargement des produits", e);
            showNotification("Erreur lors du chargement des produits", NotificationType.ERROR);
        }
    }

    /**
     * Met à jour le tableau avec une liste de produits
     * 
     * @param produits La liste des produits à afficher
     */
    private void actualiserTableAvecResultats(List<Produit> produits) {
        // Vider le tableau
        tableModel.setRowCount(0);

        // Remplir avec les données
        for (Produit produit : produits) {
            Object[] row = {
                    produit.getId(),
                    produit.getNom(),
                    produit.getDescription(),
                    formatCurrency(produit.getPrixAchat()),
                    formatCurrency(produit.getPrixVente()),
                    produit.getQuantiteEnStock(),
                    produit.getSeuilAlerte()
            };
            tableModel.addRow(row);
        }
    }

    /**
     * Formate un nombre comme une devise
     */
    private String formatCurrency(BigDecimal amount) {
        if (amount == null)
            return "0.00";
        return String.format("%,.2f", amount);
    }

    /**
     * Types de notifications
     */
    private enum NotificationType {
        SUCCESS, ERROR, INFO, WARNING
    }

    /**
     * Affiche une notification stylisée
     */
    private void showNotification(String message, NotificationType type) {
        Color bgColor;
        Color textColor = WHITE_COLOR;
        String iconText;
        String title = "";

        switch (type) {
            case SUCCESS:
                bgColor = ACCENT_COLOR;
                iconText = "✓";
                title = "Succès";
                break;
            case ERROR:
                bgColor = DANGER_COLOR;
                iconText = "✗";
                title = "Erreur";
                break;
            case WARNING:
                bgColor = WARNING_COLOR;
                textColor = TEXT_COLOR;
                iconText = "!";
                title = "Attention";
                break;
            default: // INFO
                bgColor = PRIMARY_COLOR;
                iconText = "i";
                title = "Information";
                break;
        }

        // Pour les notifications de succès après l'ajout ou la modification d'un
        // produit, utiliser JOptionPane standard
        if (type == NotificationType.SUCCESS && (message.contains("ajouté") || message.contains("modifié"))) {
            JOptionPane.showMessageDialog(
                    this,
                    message,
                    title,
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Création du panneau de notification
        JPanel notificationPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(bgColor);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2d.dispose();
            }
        };
        notificationPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 10));
        notificationPanel.setOpaque(false);

        // Icône
        JLabel iconLabel = new JLabel(iconText);
        iconLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        iconLabel.setForeground(textColor);

        // Message
        JLabel messageLabel = new JLabel(message);
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        messageLabel.setForeground(textColor);

        notificationPanel.add(iconLabel);
        notificationPanel.add(messageLabel);

        JOptionPane optionPane = new JOptionPane(
                notificationPanel,
                JOptionPane.PLAIN_MESSAGE,
                JOptionPane.DEFAULT_OPTION,
                null,
                new Object[] {},
                null);

        JDialog dialog = optionPane.createDialog(this, title);
        dialog.setUndecorated(true);
        dialog.setBackground(new Color(0, 0, 0, 0));

        // Positionnement au centre de l'écran
        dialog.setLocationRelativeTo(this);

        // Timer pour fermer automatiquement la notification
        Timer timer = new Timer(4000, (e) -> dialog.dispose());
        timer.setRepeats(false);
        timer.start();

        dialog.setVisible(true);
    }

    /**
     * Remplit le formulaire avec les données d'un produit
     * 
     * @param produit Le produit dont les données seront affichées
     */
    private void remplirFormulaire(Produit produit) {
        champNom.setText(produit.getNom());
        champDescription.setText(produit.getDescription());
        champPrixAchat.setText(produit.getPrixAchat().toString());
        champPrixVente.setText(produit.getPrixVente().toString());
        champStock.setText(String.valueOf(produit.getQuantiteEnStock()));
        champSeuilAlerte.setText(String.valueOf(produit.getSeuilAlerte()));
    }

    /**
     * Efface le formulaire et réinitialise les boutons
     */
    private void effacerFormulaire() {
        champNom.setText("");
        champDescription.setText("");
        champPrixAchat.setText("");
        champPrixVente.setText("");
        champStock.setText("0");
        champSeuilAlerte.setText("");

        produitSelectionne = null;
        tableProduits.clearSelection();

        boutonAjouter.setEnabled(true);
        boutonModifier.setEnabled(false);
        boutonSupprimer.setEnabled(false);
    }

    /**
     * Extrait les données du formulaire pour créer un objet Produit
     * 
     * @return Le produit créé à partir des données du formulaire
     */
    private Produit extraireFormulaire() {
        Produit produit = new Produit();

        produit.setNom(champNom.getText().trim());
        produit.setDescription(champDescription.getText().trim());

        try {
            BigDecimal prixAchat = new BigDecimal(champPrixAchat.getText().trim());
            produit.setPrixAchat(prixAchat);
        } catch (NumberFormatException e) {
            produit.setPrixAchat(BigDecimal.ZERO);
        }

        try {
            BigDecimal prixVente = new BigDecimal(champPrixVente.getText().trim());
            produit.setPrixVente(prixVente);
            // Utiliser le prix de vente comme prix unitaire également
            produit.setPrixUnitaire(prixVente);
        } catch (NumberFormatException e) {
            produit.setPrixVente(BigDecimal.ZERO);
            produit.setPrixUnitaire(BigDecimal.ZERO);
        }

        try {
            int seuilAlerte = Integer.parseInt(champSeuilAlerte.getText().trim());
            produit.setSeuilAlerte(seuilAlerte);
        } catch (NumberFormatException e) {
            produit.setSeuilAlerte(0);
        }

        // Initialiser à 0 pour un nouveau produit
        // Les stocks seront gérés par la vue StockView
        produit.setQuantiteEnStock(0);

        // Par défaut, on considère que le produit n'est pas sur ordonnance
        produit.setSurOrdonnance(false);

        return produit;
    }

    /**
     * Valide les données du formulaire
     * 
     * @return true si les données sont valides, false sinon
     */
    private boolean validerFormulaire() {
        // Vérifier que le nom n'est pas vide
        if (champNom.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Le nom du produit est obligatoire",
                    "Erreur de validation",
                    JOptionPane.ERROR_MESSAGE);
            champNom.requestFocus();
            return false;
        }

        // Vérifier que les prix sont valides
        try {
            BigDecimal prixAchat = new BigDecimal(champPrixAchat.getText().trim());
            if (prixAchat.compareTo(BigDecimal.ZERO) < 0) {
                JOptionPane.showMessageDialog(this,
                        "Le prix d'achat doit être positif",
                        "Erreur de validation",
                        JOptionPane.ERROR_MESSAGE);
                champPrixAchat.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Le prix d'achat doit être un nombre valide",
                    "Erreur de validation",
                    JOptionPane.ERROR_MESSAGE);
            champPrixAchat.requestFocus();
            return false;
        }

        try {
            BigDecimal prixVente = new BigDecimal(champPrixVente.getText().trim());
            if (prixVente.compareTo(BigDecimal.ZERO) < 0) {
                JOptionPane.showMessageDialog(this,
                        "Le prix de vente doit être positif",
                        "Erreur de validation",
                        JOptionPane.ERROR_MESSAGE);
                champPrixVente.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Le prix de vente doit être un nombre valide",
                    "Erreur de validation",
                    JOptionPane.ERROR_MESSAGE);
            champPrixVente.requestFocus();
            return false;
        }

        // Vérifier que le seuil d'alerte est valide
        try {
            int seuilAlerte = Integer.parseInt(champSeuilAlerte.getText().trim());
            if (seuilAlerte < 0) {
                JOptionPane.showMessageDialog(this,
                        "Le seuil d'alerte doit être positif",
                        "Erreur de validation",
                        JOptionPane.ERROR_MESSAGE);
                champSeuilAlerte.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Le seuil d'alerte doit être un nombre entier valide",
                    "Erreur de validation",
                    JOptionPane.ERROR_MESSAGE);
            champSeuilAlerte.requestFocus();
            return false;
        }

        return true;
    }

    // Méthodes d'aide pour créer des composants stylisés

    /**
     * Crée un champ de texte stylisé avec un placeholder
     */
    private JTextField createStyledTextField(String placeholder) {
        JTextField textField = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty() && !isFocusOwner()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                    g2.setColor(new Color(150, 150, 150));
                    g2.setFont(getFont().deriveFont(Font.ITALIC));
                    int padding = (getHeight() - g2.getFontMetrics().getHeight()) / 2;
                    g2.drawString(placeholder, 8, getHeight() - padding - g2.getFontMetrics().getDescent());
                    g2.dispose();
                }
            }
        };
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)));
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textField.setForeground(TEXT_COLOR);
        return textField;
    }

    /**
     * Crée un bouton stylisé
     */
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(WHITE_COLOR);
        button.setBackground(ACCENT_COLOR);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Effet de survol
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(ACCENT_COLOR.brighter());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(ACCENT_COLOR);
            }
        });

        return button;
    }

    /**
     * Crée un bouton d'action stylisé avec la couleur spécifiée
     */
    private JButton createActionButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(WHITE_COLOR);
        button.setBackground(color);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Effet de survol
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (button.isEnabled()) {
                    button.setBackground(color.brighter());
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (button.isEnabled()) {
                    button.setBackground(color);
                }
            }
        });

        return button;
    }
}