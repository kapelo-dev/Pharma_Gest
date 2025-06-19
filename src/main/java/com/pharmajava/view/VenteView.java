package com.pharmajava.view;

import com.pharmajava.controller.VenteController;
import com.pharmajava.model.Pharmacien;
import com.pharmajava.model.Produit;
import com.pharmajava.model.ProduitSelection;
import com.pharmajava.model.ProduitVendu;
import com.pharmajava.model.Vente;
import com.pharmajava.model.Client;
import com.pharmajava.utils.AutoCompletionTextField;
import com.pharmajava.utils.IconUtils;
import com.pharmajava.utils.SessionManager;
import com.pharmajava.utils.TableModelUtil;
import com.pharmajava.utils.DialogUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.function.Consumer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Interface de gestion des ventes avec design professionnel
 */
public class VenteView extends JPanel {
    private static final Logger LOGGER = Logger.getLogger(VenteView.class.getName());

    // Couleurs de l'interface
    private final Color PRIMARY_COLOR = new Color(41, 128, 185); // Bleu principal
    private final Color SECONDARY_COLOR = new Color(52, 152, 219); // Bleu secondaire
    private final Color ACCENT_COLOR = new Color(46, 204, 113); // Vert accent
    private final Color WARNING_COLOR = new Color(241, 196, 15); // Jaune avertissement
    private final Color DANGER_COLOR = new Color(231, 76, 60); // Rouge danger
    private final Color TEAL_COLOR = new Color(22, 160, 133); // Vert-bleu (remplace le violet)
    private final Color TEXT_COLOR = new Color(44, 62, 80); // Texte foncé
    private final Color LIGHT_COLOR = new Color(236, 240, 241); // Fond clair
    private final Color WHITE_COLOR = new Color(255, 255, 255); // Blanc
    private final Color CARD_COLOR = new Color(255, 255, 255); // Cartes
    private final Color BORDER_COLOR = new Color(220, 220, 220); // Bordures

    private final VenteController controller;

    // Composants de l'interface
    private JTable tablePanier;
    private DefaultTableModel modelPanier;

    // Nouveau composant de recherche avec auto-complétion
    private AutoCompletionTextField<Produit> champRechercheProduit;
    // Informations sur le produit sélectionné
    private JPanel panelProduitInfo;
    private JLabel labelProduitNom;
    private JLabel labelProduitDescription;
    private JLabel labelProduitPrix;
    private JLabel labelProduitStock;

    private JTextField champQuantite;
    private JLabel labelMontantTotal;
    private JTextField champMontantPercu;
    private JLabel labelMontantRendu;

    private JButton boutonAjouter;
    private JButton boutonValider;
    private JButton boutonAnnuler;
    private JButton boutonImprimer;

    // Variable pour stocker le produit sélectionné
    private Produit produitSelectionne;

    // Variable pour stocker la dernière vente réalisée
    private Integer derniereVenteId;

    // Format pour les montants en devise
    private NumberFormat currencyFormat;

    /**
     * Constructeur de la vue des ventes
     */
    public VenteView() {
        System.out.println("Création de VenteView");
        this.controller = new VenteController();
        this.derniereVenteId = null;

        // Configuration du format monétaire personnalisé
        this.currencyFormat = NumberFormat.getNumberInstance(Locale.FRANCE);
        ((DecimalFormat) currencyFormat).applyPattern("#,##0 'FCFA'");

        // Configurer les symboles décimaux
        DecimalFormatSymbols symbols = ((DecimalFormat) currencyFormat).getDecimalFormatSymbols();
        symbols.setGroupingSeparator(' ');
        symbols.setDecimalSeparator(',');
        ((DecimalFormat) currencyFormat).setDecimalFormatSymbols(symbols);

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(LIGHT_COLOR);

        try {
            initialiserComposants();
            initialiserEvenements();

            // Initialiser les nouvelles fonctionnalités (comme le sélecteur de client)
            VenteViewInitializer.initialize(this);

            System.out.println("VenteView initialisée avec succès");
        } catch (Exception e) {
            System.err.println("Erreur lors de l'initialisation de VenteView: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Initialise les composants de l'interface
     */
    private void initialiserComposants() {
        // Titre avec style
        JPanel headerPanel = new JPanel() {
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
        headerPanel.setLayout(new BorderLayout(15, 0));
        headerPanel.setBorder(new EmptyBorder(12, 15, 12, 15));
        headerPanel.setPreferredSize(new Dimension(getWidth(), 60));

        JLabel titreLabel = new JLabel("Nouvelle Vente");
        titreLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titreLabel.setForeground(WHITE_COLOR);
        titreLabel.setIconTextGap(10);
        headerPanel.add(titreLabel, BorderLayout.WEST);

        // Afficher la date et l'utilisateur connecté
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        infoPanel.setOpaque(false);

        String utilisateur = "Utilisateur: " + (SessionManager.getInstance().getPharmacienConnecte() != null
                ? SessionManager.getInstance().getPharmacienConnecte().getNomComplet()
                : "Inconnu");
        JLabel userLabel = new JLabel(utilisateur);
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        userLabel.setForeground(WHITE_COLOR);
        infoPanel.add(userLabel);

        headerPanel.add(infoPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // Panneau principal avec un split pane horizontal
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setBorder(null);
        splitPane.setDividerLocation(0.5);
        splitPane.setResizeWeight(0.5);
        splitPane.setDividerSize(8);

        // Panneau gauche: Recherche et informations produit
        JPanel leftPanel = new JPanel(new BorderLayout(0, 10));
        leftPanel.setBackground(LIGHT_COLOR);
        leftPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 10));

        // Section de recherche de produits simplifiée
        JPanel searchPanel = createSearchPanel();
        leftPanel.add(searchPanel, BorderLayout.NORTH);

        // Panneau droit: Panier et actions simplifiés
        JPanel rightPanel = new JPanel(new BorderLayout(0, 10));
        rightPanel.setBackground(LIGHT_COLOR);
        rightPanel.setBorder(BorderFactory.createEmptyBorder(15, 10, 0, 0));

        // Section panier - simplifiée
        String[] entetesPanier = { "Produit", "P.U", "Qté", "S.Total", "" };
        modelPanier = new DefaultTableModel(entetesPanier, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablePanier = new JTable(modelPanier);
        tablePanier.getTableHeader().setReorderingAllowed(false);

        // Amélioration du style du tableau panier
        tablePanier.setRowHeight(35);
        tablePanier.setIntercellSpacing(new Dimension(5, 5));
        tablePanier.setShowGrid(false);
        tablePanier.setBackground(CARD_COLOR);
        tablePanier.setForeground(TEXT_COLOR);
        tablePanier.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tablePanier.setSelectionBackground(ACCENT_COLOR.brighter());
        tablePanier.setSelectionForeground(WHITE_COLOR);

        // Style de l'en-tête du tableau panier
        JTableHeader headerPanier = tablePanier.getTableHeader();
        headerPanier.setBackground(ACCENT_COLOR);
        headerPanier.setForeground(WHITE_COLOR);
        headerPanier.setFont(new Font("Segoe UI", Font.BOLD, 14));
        headerPanier.setPreferredSize(new Dimension(headerPanier.getWidth(), 40));

        // Style pour les cellules du panier
        DefaultTableCellRenderer panierRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (c instanceof JLabel) {
                    JLabel label = (JLabel) c;
                    label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                }

                if (!isSelected) {
                    c.setBackground(table.getBackground());
                }

                return c;
            }
        };
        panierRenderer.setHorizontalAlignment(JLabel.CENTER);

        // Appliquer le renderer aux colonnes
        for (int i = 0; i < tablePanier.getColumnCount() - 1; i++) {
            tablePanier.getColumnModel().getColumn(i).setCellRenderer(panierRenderer);
        }

        tablePanier.getColumnModel().getColumn(0).setPreferredWidth(150); // Produit
        tablePanier.getColumnModel().getColumn(1).setPreferredWidth(80); // Prix unit.
        tablePanier.getColumnModel().getColumn(2).setPreferredWidth(60); // Quantité
        tablePanier.getColumnModel().getColumn(3).setPreferredWidth(90); // Sous-total
        tablePanier.getColumnModel().getColumn(4).setPreferredWidth(40); // Action (-)
        tablePanier.getColumnModel().getColumn(4).setCellRenderer(new ButtonRenderer());

        // Gestion des clics sur les boutons de suppression dans le tableau
        tablePanier.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int column = tablePanier.getColumnModel().getColumnIndexAtX(e.getX());
                int row = e.getY() / tablePanier.getRowHeight();

                if (row < tablePanier.getRowCount() && row >= 0 && column == 4) {
                    // Retirer la ligne du panier
                    retirerDuPanierParIndex(row);
                }
            }
        });

        JPanel panierPanel = createStyledPanel("Panier");
        panierPanel.setLayout(new BorderLayout());

        JScrollPane scrollPanier = new JScrollPane(tablePanier);
        scrollPanier.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1, true));
        scrollPanier.setBackground(CARD_COLOR);
        scrollPanier.getViewport().setBackground(CARD_COLOR);

        // Panneau avec bouton pour supprimer des éléments
        JPanel actionButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionButtonPanel.setOpaque(false);

        // Suppression du bouton Retirer général
        // boutonRetirer = createIconButton("Retirer", "✖", DANGER_COLOR);
        // boutonRetirer.setToolTipText("Retirer le produit sélectionné du panier");
        // actionButtonPanel.add(boutonRetirer);

        panierPanel.add(scrollPanier, BorderLayout.CENTER);
        // Comme le panneau actionButtonPanel est maintenant vide, on peut le supprimer
        // panierPanel.add(actionButtonPanel, BorderLayout.NORTH);

        rightPanel.add(panierPanel, BorderLayout.CENTER);

        // Section paiement - simplifiée
        JPanel paiementPanel = createStyledPanel("Paiement");
        paiementPanel.setLayout(new GridBagLayout());
        paiementPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(5, 5, 5, 5),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                        BorderFactory.createEmptyBorder(15, 15, 15, 15))));
        GridBagConstraints paiementGbc = new GridBagConstraints();
        paiementGbc.insets = new Insets(5, 8, 5, 8);
        paiementGbc.fill = GridBagConstraints.HORIZONTAL;

        Font labelFont = new Font("Segoe UI", Font.BOLD, 14);

        JLabel montantTotalLabel = new JLabel("Montant total:");
        montantTotalLabel.setFont(labelFont);
        montantTotalLabel.setForeground(TEXT_COLOR);

        labelMontantTotal = new JLabel(currencyFormat.format(0));
        labelMontantTotal.setFont(new Font("Segoe UI", Font.BOLD, 16));
        labelMontantTotal.setForeground(ACCENT_COLOR);

        JLabel montantPercuLabel = new JLabel("Montant perçu:");
        montantPercuLabel.setFont(labelFont);
        montantPercuLabel.setForeground(TEXT_COLOR);

        champMontantPercu = createStyledTextField("0");
        champMontantPercu.setHorizontalAlignment(JTextField.RIGHT);
        champMontantPercu.setForeground(Color.BLACK);
        champMontantPercu.setBackground(Color.WHITE);
        champMontantPercu.setOpaque(true);
        champMontantPercu.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180), 1),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)));

        // Redéfinir paintComponent pour champMontantPercu pour éviter le gradient de
        // fond
        champMontantPercu = new JTextField(champMontantPercu.getText()) {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(Color.WHITE);
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        champMontantPercu.setHorizontalAlignment(JTextField.RIGHT);
        champMontantPercu.setForeground(Color.BLACK);
        champMontantPercu.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        champMontantPercu.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180), 1),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)));

        JLabel montantRenduLabel = new JLabel("Montant à rendre:");
        montantRenduLabel.setFont(labelFont);
        montantRenduLabel.setForeground(TEXT_COLOR);

        labelMontantRendu = new JLabel(currencyFormat.format(0));
        labelMontantRendu.setFont(new Font("Segoe UI", Font.BOLD, 16));
        labelMontantRendu.setForeground(TEAL_COLOR);

        paiementGbc.gridx = 0;
        paiementGbc.gridy = 0;
        paiementGbc.anchor = GridBagConstraints.WEST;
        paiementGbc.weightx = 0.3;
        paiementPanel.add(montantTotalLabel, paiementGbc);

        paiementGbc.gridx = 1;
        paiementGbc.gridy = 0;
        paiementGbc.weightx = 0.7;
        paiementPanel.add(labelMontantTotal, paiementGbc);

        paiementGbc.gridx = 0;
        paiementGbc.gridy = 1;
        paiementGbc.weightx = 0.3;
        paiementPanel.add(montantPercuLabel, paiementGbc);

        paiementGbc.gridx = 1;
        paiementGbc.gridy = 1;
        paiementGbc.weightx = 0.7;
        paiementPanel.add(champMontantPercu, paiementGbc);

        paiementGbc.gridx = 0;
        paiementGbc.gridy = 2;
        paiementGbc.weightx = 0.3;
        paiementPanel.add(montantRenduLabel, paiementGbc);

        paiementGbc.gridx = 1;
        paiementGbc.gridy = 2;
        paiementGbc.weightx = 0.7;
        paiementPanel.add(labelMontantRendu, paiementGbc);

        // Boutons de validation/annulation simplifiés
        JPanel boutonsPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        boutonsPanel.setOpaque(false);

        boutonValider = createIconButton("Valider", "✓", ACCENT_COLOR);
        boutonAnnuler = createIconButton("Annuler", "✗", DANGER_COLOR);

        // Bouton d'impression de ticket caché par défaut
        boutonImprimer = createIconButton("Imprimer", "🖨", TEAL_COLOR);
        boutonImprimer.setVisible(false);

        boutonsPanel.add(boutonValider);
        boutonsPanel.add(boutonAnnuler);
        boutonsPanel.add(boutonImprimer);

        paiementGbc.gridx = 0;
        paiementGbc.gridy = 3;
        paiementGbc.gridwidth = 2;
        paiementGbc.insets = new Insets(15, 8, 5, 8);
        paiementPanel.add(boutonsPanel, paiementGbc);

        rightPanel.add(paiementPanel, BorderLayout.SOUTH);

        // Ajouter les panneaux au split pane
        splitPane.setLeftComponent(leftPanel);
        splitPane.setRightComponent(rightPanel);

        // Ajouter le split pane au panneau principal
        add(splitPane, BorderLayout.CENTER);
    }

    /**
     * Initialise les écouteurs d'événements
     */
    private void initialiserEvenements() {
        // Configuration du champ de recherche de produit avec auto-complétion
        champRechercheProduit.onItemSelected(produit -> {
            System.out.println("Produit sélectionné: " + produit.getNom());
            produitSelectionne = produit;

            // Afficher les informations du produit sélectionné
            afficherInformationsProduit(produitSelectionne);

            // Mettre à jour automatiquement le montant total de la ligne
            mettreAJourMontantLigne();

            // Donner le focus au champ quantité pour faciliter la saisie
            champQuantite.requestFocusInWindow();
            champQuantite.selectAll(); // Sélectionner tout le texte pour faciliter la modification
        });

        // Mise à jour du montant total quand la quantité change
        champQuantite.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                mettreAJourMontantLigne();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                mettreAJourMontantLigne();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                mettreAJourMontantLigne();
            }
        });

        // Ajouter au panier quand on appuie sur Entrée dans le champ quantité
        champQuantite.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    ajouterAuPanier();
                    // Après ajout, remettre le focus sur le champ de recherche
                    champRechercheProduit.getTextField().requestFocusInWindow();
                }
            }
        });

        // Ajouter au panier
        boutonAjouter.addActionListener(e -> {
            ajouterAuPanier();
            // Après ajout, remettre le focus sur le champ de recherche
            champRechercheProduit.getTextField().requestFocusInWindow();
        });

        // Double-clic sur le panier pour retirer un produit
        tablePanier.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2 && tablePanier.getSelectedRow() != -1) {
                    retirerDuPanier();
                }
            }
        });

        // Calculer le montant à rendre quand le montant perçu change
        champMontantPercu.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                calculerMontantRendu();
            }
        });

        // Valider la vente
        boutonValider.addActionListener(e -> validerVente());

        // Annuler/réinitialiser la vente
        boutonAnnuler.addActionListener(e -> annulerVente());

        // Imprimer le ticket
        boutonImprimer.addActionListener(e -> reimprimerTicket());
    }

    /**
     * Met à jour le montant total de la ligne en fonction de la quantité saisie
     */
    private void mettreAJourMontantLigne() {
        if (produitSelectionne == null) {
            return;
        }

        try {
            // Récupérer la quantité saisie
            int quantite = 1; // Valeur par défaut
            String quantiteStr = champQuantite.getText().trim();
            if (!quantiteStr.isEmpty()) {
                quantite = Integer.parseInt(quantiteStr);
            }

            // Vérifier que la quantité est positive
            if (quantite <= 0) {
                quantite = 1;
                champQuantite.setText("1");
            }

            // Vérifier que la quantité ne dépasse pas le stock disponible
            int stockDisponible = produitSelectionne.getQuantiteEnStock();
            if (quantite > stockDisponible) {
                // Limiter à la quantité disponible en stock
                quantite = stockDisponible;
                champQuantite.setText(String.valueOf(quantite));

                // Afficher un message d'avertissement subtil
                JOptionPane.showMessageDialog(this,
                        "La quantité a été limitée au stock disponible : " + stockDisponible,
                        "Stock limité",
                        JOptionPane.INFORMATION_MESSAGE);
            }

            // Calculer le montant total de la ligne
            BigDecimal prixUnitaire = produitSelectionne.getPrixVente();
            BigDecimal montantTotal = prixUnitaire.multiply(BigDecimal.valueOf(quantite));

            // Mettre à jour les champs d'affichage
            labelProduitPrix.setText(
                    "Prix: " + formatCurrency(prixUnitaire) + " × " + quantite + " = " + formatCurrency(montantTotal));

        } catch (NumberFormatException e) {
            // Si la saisie n'est pas un nombre, remettre à 1
            champQuantite.setText("1");
        }
    }

    /**
     * Affiche les informations du produit sélectionné
     */
    private void afficherInformationsProduit(Produit produit) {
        if (produit == null) {
            labelProduitNom.setText("Sélectionnez un produit");
            if (labelProduitDescription != null) {
                labelProduitDescription.setText("");
            }
            labelProduitPrix.setText("");
            labelProduitStock.setText("");
            return;
        }

        // Mettre à jour les informations du produit
        labelProduitNom.setText(produit.getNom());

        if (labelProduitDescription != null && produit.getDescription() != null) {
            labelProduitDescription.setText(produit.getDescription());
        }

        labelProduitPrix.setText("Prix: " + formatCurrency(produit.getPrixVente()));

        // Afficher le stock avec une couleur appropriée selon la disponibilité
        int stock = produit.getQuantiteEnStock();
        labelProduitStock.setText("Stock: " + stock + " unités");

        if (stock == 0) {
            labelProduitStock.setForeground(DANGER_COLOR);
        } else if (stock < 5) {
            labelProduitStock.setForeground(WARNING_COLOR);
        } else {
            labelProduitStock.setForeground(ACCENT_COLOR);
        }

        // Mise à jour automatique du montant de la ligne
        mettreAJourMontantLigne();
    }

    /**
     * Réimprime le ticket de la dernière vente
     */
    private void reimprimerTicket() {
        if (derniereVenteId == null) {
            JOptionPane.showMessageDialog(this,
                    "Aucune vente récente à imprimer.",
                    "Impression impossible",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean succes = controller.imprimerTicketVente(derniereVenteId);
        if (succes) {
            JOptionPane.showMessageDialog(this,
                    "Ticket réimprimé avec succès.",
                    "Impression terminée",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Erreur lors de l'impression du ticket.",
                    "Erreur d'impression",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Ajoute le produit sélectionné au panier
     */
    private void ajouterAuPanier() {
        if (produitSelectionne == null) {
            JOptionPane.showMessageDialog(this,
                    "Veuillez sélectionner un produit à ajouter au panier.",
                    "Aucune sélection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Récupérer les données du produit sélectionné
        String produitNom = produitSelectionne.getNom();
        int stockDisponible = produitSelectionne.getQuantiteEnStock();

        // Vérifier la quantité
        int quantite;
        try {
            quantite = Integer.parseInt(champQuantite.getText().trim());
            if (quantite <= 0) {
                JOptionPane.showMessageDialog(this,
                        "La quantité doit être supérieure à zéro.",
                        "Quantité invalide",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Veuillez entrer une quantité valide.",
                    "Quantité invalide",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Vérifier le stock
        if (quantite > stockDisponible) {
            JOptionPane.showMessageDialog(this,
                    "Stock insuffisant. Disponible: " + stockDisponible,
                    "Stock insuffisant",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Vérifier si le produit est déjà dans le panier
        boolean produitTrouve = false;
        for (int i = 0; i < modelPanier.getRowCount(); i++) {
            String nomProduitPanier = (String) modelPanier.getValueAt(i, 0);
            if (nomProduitPanier.equals(produitNom)) {
                // Mettre à jour la quantité et le sous-total
                int quantiteActuelle = (int) modelPanier.getValueAt(i, 2);
                int nouvelleQuantite = quantiteActuelle + quantite;

                if (nouvelleQuantite > stockDisponible) {
                    JOptionPane.showMessageDialog(this,
                            "Stock insuffisant. Disponible: " + stockDisponible,
                            "Stock insuffisant",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                BigDecimal prixUnitaire = produitSelectionne.getPrixVente();
                BigDecimal sousTotal = prixUnitaire.multiply(BigDecimal.valueOf(nouvelleQuantite));

                modelPanier.setValueAt(nouvelleQuantite, i, 2);
                modelPanier.setValueAt(formatCurrency(sousTotal), i, 3);

                // Mettre à jour le montant total
                calculerMontantTotal();
                calculerMontantRendu();

                // Effet visuel pour indiquer la mise à jour
                highlightTableRow(i);

                // Réinitialiser après l'ajout
                resetAfterAdd();

                produitTrouve = true;
                break;
            }
        }

        // Si le produit n'est pas encore dans le panier, l'ajouter
        if (!produitTrouve) {
            // Ajouter le produit au panier
            BigDecimal prixUnitaire = produitSelectionne.getPrixVente();
            BigDecimal sousTotal = prixUnitaire.multiply(BigDecimal.valueOf(quantite));

            Object[] ligne = {
                    produitNom,
                    formatCurrency(prixUnitaire),
                    quantite,
                    formatCurrency(sousTotal),
                    "✕" // Symbole pour le bouton de suppression
            };
            modelPanier.addRow(ligne);

            // Mettre à jour le montant total
            calculerMontantTotal();
            calculerMontantRendu();

            // Effet visuel pour indiquer l'ajout
            highlightTableRow(modelPanier.getRowCount() - 1);
        }

        // Réinitialiser après l'ajout
        resetAfterAdd();
    }

    /**
     * Réinitialise l'interface après un ajout au panier
     */
    private void resetAfterAdd() {
        // Réinitialiser la quantité à 1
        champQuantite.setText("1");

        // Effacer la sélection et le champ de recherche
        produitSelectionne = null;
        champRechercheProduit.setText("");

        // Effacer les informations du produit
        labelProduitNom.setText("Sélectionnez un produit");
        if (labelProduitDescription != null) {
            labelProduitDescription.setText("");
        }
        labelProduitPrix.setText("");
        labelProduitStock.setText("");

        // Redonner le focus au champ de recherche
        champRechercheProduit.getTextField().requestFocusInWindow();
    }

    /**
     * Met en évidence une ligne du tableau pour indiquer une action
     */
    private void highlightTableRow(int row) {
        // Animation de mise en évidence de la ligne
        new Thread(() -> {
            try {
                // Sauvegarder la sélection actuelle
                int selectedRow = tablePanier.getSelectedRow();

                // Sélectionner temporairement la ligne mise à jour
                tablePanier.setRowSelectionInterval(row, row);
                tablePanier.scrollRectToVisible(tablePanier.getCellRect(row, 0, true));

                // Attendre un court instant
                Thread.sleep(500);

                // Restaurer la sélection précédente ou désélectionner
                if (selectedRow >= 0 && selectedRow < tablePanier.getRowCount()) {
                    tablePanier.setRowSelectionInterval(selectedRow, selectedRow);
                } else {
                    tablePanier.clearSelection();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    /**
     * Retire le produit sélectionné du panier
     */
    private void retirerDuPanier() {
        int selectedRow = tablePanier.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Veuillez sélectionner un produit à retirer du panier.",
                    "Aucune sélection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        retirerDuPanierParIndex(selectedRow);
    }

    /**
     * Retrait d'un produit du panier par son index
     */
    private void retirerDuPanierParIndex(int index) {
        if (index < 0 || index >= modelPanier.getRowCount()) {
            return;
        }

        modelPanier.removeRow(index);

        // Mettre à jour le montant total
        calculerMontantTotal();
        calculerMontantRendu();
    }

    /**
     * Calcule le montant total des articles dans le panier
     */
    private void calculerMontantTotal() {
        BigDecimal montantTotal = BigDecimal.ZERO;

        for (int i = 0; i < modelPanier.getRowCount(); i++) {
            try {
                String sousTotalStr = ((String) modelPanier.getValueAt(i, 3));
                // Supprimer toutes les lettres et caractères non numériques, sauf la virgule et
                // le point
                sousTotalStr = sousTotalStr.replaceAll("[^0-9.,]", "").trim();
                // Remplacer la virgule par un point pour le parseur décimal
                sousTotalStr = sousTotalStr.replace(',', '.');

                BigDecimal sousTotal = new BigDecimal(sousTotalStr);
                montantTotal = montantTotal.add(sousTotal);
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Erreur de format dans le calcul du montant total", e);
            }
        }

        labelMontantTotal.setText(formatCurrency(montantTotal));

        // Recalculer le montant à rendre si un montant perçu est déjà saisi
        calculerMontantRendu();
    }

    /**
     * Calcule le montant à rendre au client
     */
    private void calculerMontantRendu() {
        try {
            // Récupérer le montant total en nettoyant le format monétaire
            String montantTotalStr = labelMontantTotal.getText();
            montantTotalStr = montantTotalStr.replaceAll("[^0-9.,]", "").trim();
            montantTotalStr = montantTotalStr.replace(',', '.');

            BigDecimal montantTotal = new BigDecimal(montantTotalStr);

            // Récupérer le montant perçu
            String montantPercuStr = champMontantPercu.getText().trim();
            if (montantPercuStr.isEmpty()) {
                labelMontantRendu.setText("0 FCFA");
                labelMontantRendu.setForeground(TEXT_COLOR);
                return;
            }

            // Nettoyer également le montant perçu
            montantPercuStr = montantPercuStr.replaceAll("[^0-9.,]", "").trim();
            montantPercuStr = montantPercuStr.replace(',', '.');

            BigDecimal montantPercu = new BigDecimal(montantPercuStr);

            // Vérifier si le montant perçu est suffisant
            if (montantPercu.compareTo(montantTotal) < 0) {
                labelMontantRendu.setText("Montant insuffisant");
                labelMontantRendu.setForeground(DANGER_COLOR);
            } else {
                // Calculer le montant à rendre
                BigDecimal montantRendu = montantPercu.subtract(montantTotal);
                // Formater avec le format de devise configuré
                labelMontantRendu.setText(formatCurrency(montantRendu));
                labelMontantRendu.setForeground(TEAL_COLOR);
            }
        } catch (NumberFormatException e) {
            labelMontantRendu.setText("Format invalide");
            labelMontantRendu.setForeground(DANGER_COLOR);
            LOGGER.log(Level.WARNING, "Erreur de format dans le calcul du montant rendu", e);
        }
    }

    /**
     * Valide la vente actuelle
     */
    private void validerVente() {
        // Vérifier qu'il y a des produits dans le panier
        if (modelPanier.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this,
                    "Le panier est vide. Veuillez ajouter des produits avant de valider.",
                    "Panier vide",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Récupérer le montant perçu
        String montantPercuStr = champMontantPercu.getText().trim();
        if (montantPercuStr.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Veuillez saisir le montant perçu.",
                    "Montant perçu manquant",
                    JOptionPane.WARNING_MESSAGE);
            champMontantPercu.requestFocusInWindow();
            return;
        }

        try {
            // Calculer le montant total de la vente
            String montantTotalStr = labelMontantTotal.getText();
            montantTotalStr = montantTotalStr.replaceAll("[^0-9.,]", "").trim();
            montantTotalStr = montantTotalStr.replace(',', '.');
            BigDecimal montantTotal = new BigDecimal(montantTotalStr);

            // Vérifier que le montant perçu est suffisant
            // Nettoyer le format du montant perçu
            montantPercuStr = montantPercuStr.replaceAll("[^0-9.,]", "").trim();
            montantPercuStr = montantPercuStr.replace(',', '.');
            BigDecimal montantPercu = new BigDecimal(montantPercuStr);

            if (montantPercu.compareTo(montantTotal) < 0) {
                JOptionPane.showMessageDialog(this,
                        "Le montant perçu est insuffisant.",
                        "Montant insuffisant",
                        JOptionPane.WARNING_MESSAGE);
                champMontantPercu.requestFocusInWindow();
                return;
            }

            // Calculer le montant rendu
            BigDecimal montantRendu = montantPercu.subtract(montantTotal);

            // Demander confirmation avant de finaliser la vente
            Client client = VenteViewInitializer.getClientSelectionne();
            String messageConfirmation = "Voulez-vous confirmer cette vente ?\n\n" +
                    "Montant total: " + formatCurrency(montantTotal) + "\n" +
                    "Montant perçu: " + formatCurrency(montantPercu) + "\n" +
                    "Montant à rendre: " + formatCurrency(montantRendu);

            if (client != null) {
                messageConfirmation += "\n\nClient: " + client.getNomComplet();
            }

            int confirmation = DialogUtils.afficherConfirmation(
                    this,
                    messageConfirmation,
                    "Confirmation de vente");

            if (confirmation != JOptionPane.YES_OPTION) {
                return; // L'utilisateur a annulé la vente
            }

            // Récupérer les produits vendus
            List<ProduitVendu> produitsVendus = new ArrayList<>();

            for (int i = 0; i < modelPanier.getRowCount(); i++) {
                String nomProduit = (String) modelPanier.getValueAt(i, 0);
                String prixUnitaireStr = ((String) modelPanier.getValueAt(i, 1));
                int quantite = (int) modelPanier.getValueAt(i, 2);

                // Trouver le produit correspondant
                Produit produit = controller.rechercherProduitParNom(nomProduit);
                if (produit == null) {
                    JOptionPane.showMessageDialog(this,
                            "Produit introuvable: " + nomProduit,
                            "Erreur",
                            JOptionPane.ERROR_MESSAGE);
                    continue;
                }

                // Nettoyer et extraire le prix unitaire
                try {
                    prixUnitaireStr = prixUnitaireStr.replaceAll("[^0-9.,]", "").trim();
                    prixUnitaireStr = prixUnitaireStr.replace(',', '.');
                    BigDecimal prixUnitaire = new BigDecimal(prixUnitaireStr);

                    ProduitVendu produitVendu = new ProduitVendu(produit, quantite, prixUnitaire);
                    produitVendu.calculerPrixTotal();
                    produitsVendus.add(produitVendu);
                } catch (NumberFormatException e) {
                    LOGGER.log(Level.WARNING, "Erreur de format dans le prix unitaire pour " + nomProduit, e);
                    // Utiliser le prix du produit en cas d'erreur
                    BigDecimal prixUnitaire = produit.getPrixVente();
                    ProduitVendu produitVendu = new ProduitVendu(produit, quantite, prixUnitaire);
                    produitVendu.calculerPrixTotal();
                    produitsVendus.add(produitVendu);
                }
            }

            // Récupérer le pharmacien connecté
            Pharmacien pharmacien = SessionManager.getInstance().getPharmacienConnecte();

            // Enregistrer la vente avec la nouvelle méthode qui prend en compte le client
            Vente vente = controller.enregistrerVente(produitsVendus, montantPercu, pharmacien, client);

            if (vente != null && vente.getId() != null) {
                // Stocker l'ID de la vente pour permettre la réimpression
                derniereVenteId = vente.getId();
                boutonImprimer.setVisible(true);

                // Imprimer le ticket
                boolean ticketImprime = controller.imprimerTicketVente(vente.getId());

                // Afficher un message de confirmation
                if (ticketImprime) {
                    JOptionPane.showMessageDialog(this,
                            "Vente enregistrée avec succès!\n" +
                                    "Montant total: " + formatCurrency(montantTotal) + "\n" +
                                    "Montant rendu: " + formatCurrency(montantRendu) + "\n" +
                                    "Ticket imprimé.",
                            "Vente validée",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Vente enregistrée avec succès!\n" +
                                    "Montant total: " + formatCurrency(montantTotal) + "\n" +
                                    "Montant rendu: " + formatCurrency(montantRendu) + "\n" +
                                    "Note: Problème lors de l'impression du ticket.",
                            "Vente validée",
                            JOptionPane.INFORMATION_MESSAGE);
                }

                // Réinitialiser le formulaire pour une nouvelle vente
                reinitialiserVente();

                // Réinitialiser également le client sélectionné
                VenteViewInitializer.resetClientSelectionne();

                // Si un produit était sélectionné et est toujours affiché après
                // réinitialisation, actualiser ses informations
                if (produitSelectionne != null) {
                    for (ProduitVendu pv : produitsVendus) {
                        if (pv.getProduit() != null && pv.getProduit().getId().equals(produitSelectionne.getId())) {
                            // Recharger le produit pour avoir les informations à jour
                            Produit produitActualise = controller.obtenirProduitParId(produitSelectionne.getId());
                            if (produitActualise != null) {
                                produitSelectionne = produitActualise;
                                afficherInformationsProduit(produitSelectionne);
                            }
                            break;
                        }
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this,
                        "Erreur lors de l'enregistrement de la vente.",
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la validation de la vente", e);
            JOptionPane.showMessageDialog(this,
                    "Une erreur s'est produite: " + e.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Réinitialise la vente courante
     */
    private void reinitialiserVente() {
        // Vider le panier
        while (modelPanier.getRowCount() > 0) {
            modelPanier.removeRow(0);
        }

        // Réinitialiser les champs
        champRechercheProduit.setText("");
        champQuantite.setText("1");
        champMontantPercu.setText("");
        labelMontantTotal.setText("0 FCFA");
        labelMontantRendu.setText("0 FCFA");

        // Masquer les informations du produit
        labelProduitNom.setText("Aucun produit sélectionné");
        if (labelProduitDescription != null) {
            labelProduitDescription.setText("");
        }
        labelProduitPrix.setText("");
        labelProduitStock.setText("");

        // Réinitialiser les variables
        produitSelectionne = null;

        // Réinitialiser également le client sélectionné
        VenteViewInitializer.resetClientSelectionne();

        // Donner le focus au champ de recherche
        champRechercheProduit.requestFocusInWindow();
    }

    /**
     * Annule la vente en cours après confirmation
     */
    private void annulerVente() {
        // Vérifier s'il y a des produits dans le panier
        if (modelPanier.getRowCount() == 0) {
            // Si le panier est vide, simplement réinitialiser sans confirmation
            reinitialiserVente();
            return;
        }

        // Demander confirmation avant d'annuler
        int confirmation = DialogUtils.afficherConfirmation(
                this,
                "Êtes-vous sûr de vouloir annuler cette vente en cours?\nToutes les informations seront perdues.",
                "Confirmation d'annulation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirmation == JOptionPane.YES_OPTION) {
            reinitialiserVente();
        }
    }

    /**
     * Crée un panneau stylisé avec un titre
     */
    private JPanel createStyledPanel(String title) {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Créer un dégradé subtil pour le fond
                GradientPaint gp = new GradientPaint(
                        0, 0, CARD_COLOR,
                        0, getHeight(), new Color(250, 250, 250));
                g2d.setPaint(gp);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

                // Ajouter une ombre légère pour donner de la profondeur
                drawShadow(g2d, 0, 0, getWidth(), getHeight(), 15, 15, 3);

                g2d.dispose();
            }

            /**
             * Dessine une ombre douce autour d'un rectangle arrondi
             */
            private void drawShadow(Graphics2D g2d, int x, int y, int width, int height, int arcWidth, int arcHeight,
                    int shadowSize) {
                // Sauvegarder le composite original
                Composite originalComposite = g2d.getComposite();

                // Dessiner l'ombre avec un léger décalage
                for (int i = 1; i <= shadowSize; i++) {
                    // Réduire l'opacité pour chaque couche d'ombre
                    float opacity = 0.1f - (i * 0.02f);
                    if (opacity < 0.01f)
                        opacity = 0.01f;

                    // Composite pour gérer la transparence
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
                    g2d.setColor(Color.BLACK);
                    g2d.drawRoundRect(x + i, y + i, width - (i * 2), height - (i * 2), arcWidth, arcHeight);
                }

                // Restaurer le composite original
                g2d.setComposite(originalComposite);
            }
        };
        panel.setOpaque(false);

        // Ajouter le titre s'il est spécifié
        if (title != null && !title.isEmpty()) {
            // Créer une bordure avec un style titre élégant
            panel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createTitledBorder(
                            BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                            title,
                            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                            javax.swing.border.TitledBorder.DEFAULT_POSITION,
                            new Font("Segoe UI", Font.BOLD, 14),
                            PRIMARY_COLOR),
                    BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        }

        return panel;
    }

    /**
     * Crée un champ de texte stylisé avec un placeholder
     */
    private JTextField createStyledTextField(String placeholder) {
        JTextField textField = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                // Dessiner le placeholder si le champ est vide et non focalisé
                if (getText().isEmpty() && !isFocusOwner()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                    g2.setColor(new Color(150, 150, 150));
                    g2.setFont(getFont().deriveFont(Font.ITALIC));
                    int padding = (getHeight() - g2.getFontMetrics().getHeight()) / 2;
                    g2.drawString(placeholder, 8, getHeight() - padding - g2.getFontMetrics().getDescent());
                    g2.dispose();
                }

                // Ajouter un effet de gradient subtil au fond
                if (isFocusOwner()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    // Créer un dégradé de blanc vers une teinte très légère de la couleur d'accent
                    Color fadeColor = new Color(
                            Math.min(255, ACCENT_COLOR.getRed() + 200),
                            Math.min(255, ACCENT_COLOR.getGreen() + 200),
                            Math.min(255, ACCENT_COLOR.getBlue() + 200));
                    GradientPaint gp = new GradientPaint(
                            0, 0, Color.WHITE,
                            0, getHeight(), fadeColor);
                    g2.setPaint(gp);
                    g2.fillRect(0, 0, getWidth(), getHeight());
                    g2.dispose();

                    // Redessiner le texte pour s'assurer qu'il est visible sur le gradient
                    paintChildren(g);
                }
            }
        };

        // Style de base
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textField.setForeground(TEXT_COLOR);

        // Bordure personnalisée avec effet de focus
        final Color defaultBorderColor = new Color(200, 200, 200);
        final Color focusBorderColor = ACCENT_COLOR;

        textField.setBorder(BorderFactory.createCompoundBorder(
                new javax.swing.border.AbstractBorder() {
                    @Override
                    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                        // Choisir la couleur de bordure selon l'état de focus
                        g2.setColor(c.hasFocus() ? focusBorderColor : defaultBorderColor);

                        // Dessiner une bordure arrondie
                        g2.setStroke(new BasicStroke(1f));
                        g2.drawRoundRect(x, y, width - 1, height - 1, 8, 8);
                        g2.dispose();
                    }

                    @Override
                    public Insets getBorderInsets(Component c) {
                        return new Insets(6, 8, 6, 8);
                    }

                    @Override
                    public boolean isBorderOpaque() {
                        return false;
                    }
                },
                BorderFactory.createEmptyBorder(6, 8, 6, 8)));

        // Ajouter une animation de focus
        textField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                // Animation de focus - le champ se repeint avec la nouvelle bordure
                textField.repaint();
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                // Animation de perte de focus - le champ se repeint avec la bordure normale
                textField.repaint();
            }
        });

        return textField;
    }

    /**
     * Crée un bouton stylisé avec la couleur spécifiée
     */
    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(WHITE_COLOR);
        button.setBackground(color);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
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

    /**
     * Formate un montant en devise
     * 
     * @param amount Le montant à formater
     * @return Le montant formaté
     */
    private String formatCurrency(BigDecimal amount) {
        try {
            if (amount == null) {
                amount = BigDecimal.ZERO;
            }
            // Toujours arrondir à l'entier
            amount = amount.setScale(0, java.math.RoundingMode.HALF_UP);
            return currencyFormat.format(amount);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Erreur lors du formatage du montant", e);
            return "0 FCFA";
        }
    }

    /**
     * Crée un bouton avec icône intégrée
     * 
     * @param text     Texte du bouton
     * @param iconText Texte de l'icône
     * @param color    Couleur de fond du bouton
     * @return Le bouton créé
     */
    private JButton createIconButton(String text, String iconText, Color color) {
        JButton button = new JButton();
        button.setLayout(new BorderLayout());
        button.setBackground(color);
        button.setForeground(WHITE_COLOR);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Utiliser un panneau pour le contenu avec une mise en page soignée
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 8, 0));
        contentPanel.setOpaque(false);

        // Icône avec une police un peu plus grande
        JLabel icon = new JLabel(iconText);
        icon.setFont(new Font("Dialog", Font.BOLD, 16));
        icon.setForeground(WHITE_COLOR);

        // Texte du bouton
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(WHITE_COLOR);

        contentPanel.add(icon);
        contentPanel.add(label);
        button.add(contentPanel, BorderLayout.CENTER);

        // Effets avancés avec MouseAdapter
        button.addMouseListener(new MouseAdapter() {
            private final Color originalColor = color;
            private final Color hoverColor = new Color(
                    Math.min(color.getRed() + 20, 255),
                    Math.min(color.getGreen() + 20, 255),
                    Math.min(color.getBlue() + 20, 255));
            private final Color pressedColor = new Color(
                    Math.max(color.getRed() - 20, 0),
                    Math.max(color.getGreen() - 20, 0),
                    Math.max(color.getBlue() - 20, 0));

            @Override
            public void mouseEntered(MouseEvent e) {
                if (button.isEnabled()) {
                    // Animation d'entrée
                    animateButtonColor(button, originalColor, hoverColor, 150);

                    // Agrandir légèrement les composants pour un effet de zoom
                    icon.setFont(icon.getFont().deriveFont(icon.getFont().getSize() + 1.0f));
                    contentPanel.revalidate();
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (button.isEnabled()) {
                    // Animation de sortie
                    animateButtonColor(button, button.getBackground(), originalColor, 150);

                    // Restaurer la taille d'origine
                    icon.setFont(icon.getFont().deriveFont(icon.getFont().getSize() - 1.0f));
                    contentPanel.revalidate();
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (button.isEnabled()) {
                    button.setBackground(pressedColor);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (button.isEnabled()) {
                    button.setBackground(originalColor);
                }
            }
        });

        return button;
    }

    /**
     * Anime la transition de couleur d'un composant
     * 
     * @param component Le composant à animer
     * @param fromColor La couleur de départ
     * @param toColor   La couleur d'arrivée
     * @param duration  La durée de l'animation en millisecondes
     */
    private void animateButtonColor(JComponent component, Color fromColor, Color toColor, int duration) {
        if (fromColor == null || toColor == null)
            return;

        new Thread(() -> {
            try {
                // Nombre d'étapes pour l'animation
                int steps = 10;
                int delay = duration / steps;

                for (int i = 0; i <= steps; i++) {
                    float ratio = (float) i / steps;

                    int r = (int) (fromColor.getRed() + ratio * (toColor.getRed() - fromColor.getRed()));
                    int g = (int) (fromColor.getGreen() + ratio * (toColor.getGreen() - fromColor.getGreen()));
                    int b = (int) (fromColor.getBlue() + ratio * (toColor.getBlue() - fromColor.getBlue()));

                    Color stepColor = new Color(r, g, b);

                    SwingUtilities.invokeLater(() -> component.setBackground(stepColor));
                    Thread.sleep(delay);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    /**
     * Classe pour rendre un bouton dans une cellule de tableau
     */
    private class ButtonRenderer extends DefaultTableCellRenderer {
        private final JButton button;

        public ButtonRenderer() {
            button = new JButton("−");
            button.setFont(new Font("Segoe UI", Font.BOLD, 14));
            button.setBackground(DANGER_COLOR);
            button.setForeground(WHITE_COLOR);
            button.setFocusPainted(false);
            button.setBorderPainted(false);
            button.setOpaque(true);
            button.setToolTipText("Retirer ce produit du panier");
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                int row, int column) {
            return button;
        }
    }

    /**
     * Configure l'action à exécuter lorsqu'un produit est sélectionné
     * 
     * @param onSelectAction L'action à exécuter
     */
    public void onItemSelected(Consumer<Produit> onSelectAction) {
        if (champRechercheProduit != null) {
            champRechercheProduit.onItemSelected(produit -> {
                if (onSelectAction != null) {
                    onSelectAction.accept(produit);
                }
            });
        }
    }

    /**
     * Crée le panneau de recherche et d'ajout de produits simplifié
     */
    private JPanel createSearchPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Fond simple avec légère bordure
                g2d.setColor(CARD_COLOR);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2d.setColor(BORDER_COLOR);
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);

                g2d.dispose();
            }
        };
        panel.setLayout(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.setOpaque(false);

        // En-tête du panneau
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Recherche de produits");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(PRIMARY_COLOR);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        panel.add(headerPanel, BorderLayout.NORTH);

        // Panneau central
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout(10, 10));
        contentPanel.setOpaque(false);

        // Ligne 1: Recherche de produit
        JPanel searchRow = new JPanel(new BorderLayout(10, 0));
        searchRow.setOpaque(false);

        champRechercheProduit = createAutoCompletionField(30);
        champRechercheProduit.onItemSelected(produit -> {
            produitSelectionne = produit;
            afficherInformationsProduit(produit);
            champQuantite.requestFocusInWindow();
        });

        searchRow.add(champRechercheProduit, BorderLayout.CENTER);

        // Ligne 2: Informations produit
        panelProduitInfo = new JPanel(new BorderLayout(10, 5));
        panelProduitInfo.setOpaque(false);
        panelProduitInfo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR),
                BorderFactory.createEmptyBorder(10, 0, 0, 0)));

        // Informations principales (nom, prix)
        JPanel leftInfoPanel = new JPanel();
        leftInfoPanel.setLayout(new BoxLayout(leftInfoPanel, BoxLayout.Y_AXIS));
        leftInfoPanel.setOpaque(false);

        labelProduitNom = new JLabel("Sélectionnez un produit");
        labelProduitNom.setFont(new Font("Segoe UI", Font.BOLD, 14));
        labelProduitNom.setForeground(TEXT_COLOR);
        labelProduitNom.setAlignmentX(Component.LEFT_ALIGNMENT);

        labelProduitPrix = new JLabel("");
        labelProduitPrix.setFont(new Font("Segoe UI", Font.BOLD, 14));
        labelProduitPrix.setForeground(ACCENT_COLOR);
        labelProduitPrix.setAlignmentX(Component.LEFT_ALIGNMENT);

        labelProduitStock = new JLabel("");
        labelProduitStock.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        labelProduitStock.setForeground(TEXT_COLOR);
        labelProduitStock.setAlignmentX(Component.LEFT_ALIGNMENT);

        leftInfoPanel.add(labelProduitNom);
        leftInfoPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        leftInfoPanel.add(labelProduitPrix);
        leftInfoPanel.add(Box.createRigidArea(new Dimension(0, 3)));
        leftInfoPanel.add(labelProduitStock);

        // Panneau droit avec quantité et ajout
        JPanel rightInfoPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightInfoPanel.setOpaque(false);

        JLabel quantiteLabel = new JLabel("Qté:");
        quantiteLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        quantiteLabel.setForeground(TEXT_COLOR);

        champQuantite = new JTextField("1");
        champQuantite.setFont(new Font("Segoe UI", Font.BOLD, 14));
        champQuantite.setForeground(TEXT_COLOR);
        champQuantite.setBackground(WHITE_COLOR);
        champQuantite.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        champQuantite.setColumns(3);
        champQuantite.setHorizontalAlignment(JTextField.CENTER);

        boutonAjouter = createIconButton("Ajouter", "+", ACCENT_COLOR);

        rightInfoPanel.add(quantiteLabel);
        rightInfoPanel.add(champQuantite);
        rightInfoPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        rightInfoPanel.add(boutonAjouter);

        panelProduitInfo.add(leftInfoPanel, BorderLayout.CENTER);
        panelProduitInfo.add(rightInfoPanel, BorderLayout.EAST);

        // Ajouter tous les éléments
        contentPanel.add(searchRow, BorderLayout.NORTH);
        contentPanel.add(panelProduitInfo, BorderLayout.CENTER);

        panel.add(contentPanel, BorderLayout.CENTER);

        return panel;
    }

    private AutoCompletionTextField<Produit> createAutoCompletionField(int columns) {
        List<Produit> produits = new ArrayList<>();
        try {
            // Utilisation du controller existant pour obtenir les produits
            produits = controller.obtenirTousProduits();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors du chargement des produits", e);
        }

        // Liste finale de produits (pour la capture dans lambda)
        final List<Produit> finalProduits = produits;

        AutoCompletionTextField<Produit> field = new AutoCompletionTextField<>(
                "Rechercher un produit par nom...",
                produit -> produit.getNom(),
                produit -> {
                    // Format riche pour les détails du produit
                    String details = String.format("Prix: %s | Stock: %d",
                            currencyFormat.format(produit.getPrixVente()),
                            produit.getQuantiteEnStock());

                    // Ajouter indication des produits sur ordonnance
                    if (produit.isSurOrdonnance()) {
                        details += " | Sur ordonnance";
                    }

                    return details;
                },
                produit -> {
                    produitSelectionne = produit;
                    afficherInformationsProduit(produitSelectionne);
                });

        // Définir la liste des items
        field.setItems(finalProduits);

        // Configurer le champ pour afficher plus de résultats
        field.setMaxResults(10);

        // Configurer l'apparence du champ
        field.setShowPopupOnFocus(true);
        field.setColors(
                WHITE_COLOR, // Fond
                TEXT_COLOR, // Texte
                new Color(150, 150, 150), // Placeholder
                BORDER_COLOR, // Bordure
                new Color(240, 248, 255), // Survol
                SECONDARY_COLOR // Sélection
        );

        return field;
    }
}