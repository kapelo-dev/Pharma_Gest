package com.pharmajava.view;

import com.pharmajava.controller.ProduitController;
import com.pharmajava.controller.StockController;
import com.pharmajava.model.Produit;
import com.pharmajava.model.Stock;
import com.pharmajava.utils.DateUtil;
import com.pharmajava.utils.DialogUtils;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.SwingWorker;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Timer;

/**
 * Vue pour la gestion des stocks
 */
public class StockView extends JPanel {
    private static final Logger LOGGER = Logger.getLogger(StockView.class.getName());

    private final StockController stockController;
    private final ProduitController produitController;

    // Composants de l'interface
    private JTable tableStocks;
    private DefaultTableModel tableModel;
    private JTextField champRechercheProduit;
    private JPopupMenu popupSuggestions;
    private JLabel labelProduitSelectionne;
    private JTextField champQuantite;
    private JTextField champLot;
    private JSpinner champDateExpiration;
    private JButton boutonAjouter;
    private JButton boutonSupprimer;
    private JButton boutonEffacer;
    private JButton boutonRechercher;
    private JComboBox<String> comboFiltre;
    private boolean popupClicEnCours = false;

    private Stock stockSelectionne;
    private List<Produit> produits;
    private Produit produitSelectionne;

    /**
     * Constructeur de la vue stock
     */
    public StockView() {
        this.stockController = new StockController();
        this.produitController = new ProduitController();
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        initialiserComposants();
        initialiserEvenements();
        chargerDonnees();
    }

    /**
     * Initialise les composants de l'interface utilisateur
     */
    private void initialiserComposants() {
        // En-tête avec titre et recherche
        JPanel panneauEntete = new JPanel(new BorderLayout(5, 0));
        panneauEntete.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        JLabel labelTitre = new JLabel("Gestion des Stocks");
        labelTitre.setFont(new Font("Arial", Font.BOLD, 24));
        labelTitre.setForeground(new Color(44, 62, 80));
        labelTitre.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 0));
        panneauEntete.add(labelTitre, BorderLayout.WEST);

        JPanel panneauRecherche = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        // Configuration du champ de recherche avec placeholder
        champRechercheProduit = createStyledTextField(20);
        champRechercheProduit.setForeground(Color.GRAY);
        champRechercheProduit.setText("Rechercher un produit...");
        champRechercheProduit.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (champRechercheProduit.getText().equals("Rechercher un produit...")) {
                    champRechercheProduit.setText("");
                    champRechercheProduit.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (champRechercheProduit.getText().isEmpty()) {
                    champRechercheProduit.setForeground(Color.GRAY);
                    champRechercheProduit.setText("Rechercher un produit...");
                }
            }
        });

        popupSuggestions = new JPopupMenu();
        labelProduitSelectionne = new JLabel("");
        labelProduitSelectionne.setFont(new Font("Arial", Font.BOLD, 12));
        labelProduitSelectionne.setForeground(new Color(44, 62, 80));

        // Créer le panneau de recherche de produit pour le formulaire
        JPanel panneauRechercheProduitForm = new JPanel(new BorderLayout(5, 5));
        panneauRechercheProduitForm.add(champRechercheProduit, BorderLayout.CENTER);
        panneauRechercheProduitForm.add(labelProduitSelectionne, BorderLayout.SOUTH);

        boutonRechercher = new JButton("Rechercher");
        styleButton(boutonRechercher, new Color(52, 152, 219));

        comboFiltre = new JComboBox<>(new String[] { "Tous", "Expiré", "Expire bientôt", "En stock" });
        comboFiltre.setPreferredSize(new Dimension(130, 30));
        comboFiltre.setFont(new Font("Arial", Font.PLAIN, 14));

        JLabel labelFiltre = new JLabel("Filtrer:");
        labelFiltre.setFont(new Font("Arial", Font.PLAIN, 14));
        panneauRecherche.add(labelFiltre);
        panneauRecherche.add(comboFiltre);

        JLabel labelRecherche = new JLabel("Rechercher:");
        labelRecherche.setFont(new Font("Arial", Font.PLAIN, 14));
        panneauRecherche.add(labelRecherche);
        panneauRecherche.add(boutonRechercher);
        panneauEntete.add(panneauRecherche, BorderLayout.EAST);

        add(panneauEntete, BorderLayout.NORTH);

        // Tableau des stocks
        String[] entetes = { "ID", "Produit", "Lot", "Quantité", "Date d'expiration", "Jours restants" };
        tableModel = new DefaultTableModel(entetes, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Rendre toutes les cellules non modifiables
            }
        };

        tableStocks = new JTable(tableModel);
        tableStocks.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableStocks.getTableHeader().setReorderingAllowed(false);

        // Amélioration du style de la table
        tableStocks.setRowHeight(30); // Hauteur des lignes augmentée
        tableStocks.setFont(new Font("Segoe UI", Font.PLAIN, 14)); // Police moderne
        tableStocks.setSelectionBackground(new Color(0, 120, 215)); // Couleur de sélection bleu moderne
        tableStocks.setSelectionForeground(Color.WHITE);
        tableStocks.setShowGrid(true);
        tableStocks.setGridColor(new Color(230, 230, 230)); // Grille subtile

        JTableHeader header = tableStocks.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(new Color(240, 240, 240));
        header.setForeground(new Color(60, 60, 60));
        header.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        header.setPreferredSize(new Dimension(header.getWidth(), 35));

        // Alternance de couleurs pour les lignes
        tableStocks.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 245, 250));
                }
                setBorder(noFocusBorder);
                return c;
            }
        });

        // Définir des largeurs préférées pour les colonnes
        tableStocks.getColumnModel().getColumn(0).setPreferredWidth(50); // ID
        tableStocks.getColumnModel().getColumn(1).setPreferredWidth(200); // Produit
        tableStocks.getColumnModel().getColumn(2).setPreferredWidth(100); // Lot
        tableStocks.getColumnModel().getColumn(3).setPreferredWidth(80); // Quantité
        tableStocks.getColumnModel().getColumn(4).setPreferredWidth(120); // Date d'expiration
        tableStocks.getColumnModel().getColumn(5).setPreferredWidth(100); // Jours restants

        JScrollPane scrollPane = new JScrollPane(tableStocks);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        scrollPane.getViewport().setBackground(Color.WHITE);

        // Formulaire d'ajout/modification
        JPanel panneauFormulaire = new JPanel(new GridBagLayout());
        panneauFormulaire.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(52, 152, 219), 1),
                        "Ajouter ou modifier un stock",
                        javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                        javax.swing.border.TitledBorder.DEFAULT_POSITION,
                        new Font("Arial", Font.BOLD, 14),
                        new Color(52, 152, 219)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        panneauFormulaire.setBackground(new Color(245, 247, 250));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Ligne 1
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel labelProduit = new JLabel("Produit:");
        labelProduit.setFont(new Font("Arial", Font.BOLD, 14));
        labelProduit.setForeground(new Color(44, 62, 80));
        panneauFormulaire.add(labelProduit, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        panneauFormulaire.add(panneauRechercheProduitForm, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0;
        JLabel labelLot = new JLabel("Lot:");
        labelLot.setFont(new Font("Arial", Font.BOLD, 14));
        labelLot.setForeground(new Color(44, 62, 80));
        panneauFormulaire.add(labelLot, gbc);

        gbc.gridx = 3;
        gbc.weightx = 1.0;
        champLot = createStyledTextField(10);
        panneauFormulaire.add(champLot, gbc);

        // Ligne 2
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        JLabel labelQuantite = new JLabel("Quantité:");
        labelQuantite.setFont(new Font("Arial", Font.BOLD, 14));
        labelQuantite.setForeground(new Color(44, 62, 80));
        panneauFormulaire.add(labelQuantite, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        champQuantite = createStyledTextField(10);
        panneauFormulaire.add(champQuantite, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0;
        JLabel labelDateExpiration = new JLabel("Date d'expiration:");
        labelDateExpiration.setFont(new Font("Arial", Font.BOLD, 14));
        labelDateExpiration.setForeground(new Color(44, 62, 80));
        panneauFormulaire.add(labelDateExpiration, gbc);

        gbc.gridx = 3;
        gbc.weightx = 1.0;
        // Utiliser un JSpinner avec modèle de date
        SpinnerDateModel dateModel = new SpinnerDateModel();
        champDateExpiration = new JSpinner(dateModel);
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(champDateExpiration, "dd/MM/yyyy");
        champDateExpiration.setEditor(dateEditor);
        champDateExpiration.setFont(new Font("Arial", Font.PLAIN, 14));
        champDateExpiration.setPreferredSize(new Dimension(150, 30));
        panneauFormulaire.add(champDateExpiration, gbc);

        // Boutons
        JPanel panneauBoutons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panneauBoutons.setBackground(new Color(245, 247, 250));

        boutonAjouter = new JButton("Ajouter");
        styleButton(boutonAjouter, new Color(46, 204, 113));

        boutonSupprimer = new JButton("Supprimer");
        styleButton(boutonSupprimer, new Color(231, 76, 60));

        boutonEffacer = new JButton("Effacer");
        styleButton(boutonEffacer, new Color(149, 165, 166));

        panneauBoutons.add(boutonAjouter);
        panneauBoutons.add(boutonSupprimer);
        panneauBoutons.add(boutonEffacer);

        // Désactiver le bouton supprimer initialement
        boutonSupprimer.setEnabled(false);

        JPanel panneauSud = new JPanel(new BorderLayout());
        panneauSud.add(panneauFormulaire, BorderLayout.CENTER);
        panneauSud.add(panneauBoutons, BorderLayout.SOUTH);

        add(scrollPane, BorderLayout.CENTER);
        add(panneauSud, BorderLayout.SOUTH);
    }

    /**
     * Initialise les événements pour les composants de l'interface
     */
    private void initialiserEvenements() {
        // Sélection d'un stock dans le tableau
        tableStocks.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tableStocks.getSelectedRow();
                if (row >= 0) {
                    Integer id = (Integer) tableModel.getValueAt(row, 0);
                    stockSelectionne = stockController.obtenirParId(id);
                    if (stockSelectionne != null) {
                        remplirFormulaire(stockSelectionne);
                        boutonSupprimer.setEnabled(true);
                    }
                }
            }
        });

        // Bouton Ajouter
        boutonAjouter.addActionListener((ActionEvent e) -> {
            try {
                if (validerFormulaire()) {
                    Stock nouveauStock = extraireFormulaire();

                    // Vérifier que le produit a un ID valide
                    if (nouveauStock.getProduit() == null || nouveauStock.getProduit().getId() == null) {
                        JOptionPane.showMessageDialog(this,
                                "Erreur: Produit invalide ou sans ID",
                                "Erreur de validation",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    // Vérifier que le lot n'est pas vide
                    if (nouveauStock.getNumeroLot() == null || nouveauStock.getNumeroLot().isEmpty()) {
                        JOptionPane.showMessageDialog(this,
                                "Erreur: Numéro de lot invalide",
                                "Erreur de validation",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    // Vérifier que la date n'est pas nulle
                    if (nouveauStock.getDateExpiration() == null) {
                        JOptionPane.showMessageDialog(this,
                                "Erreur: Date d'expiration invalide",
                                "Erreur de validation",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    boolean succes = stockController.ajouter(nouveauStock);
                    if (succes) {
                        JOptionPane.showMessageDialog(this,
                                "Stock ajouté avec succès",
                                "Succès",
                                JOptionPane.INFORMATION_MESSAGE);
                        effacerFormulaire();
                        chargerTousLesStocks();
                    } else {
                        JOptionPane.showMessageDialog(this,
                                "Erreur lors de l'ajout du stock",
                                "Erreur",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (Exception ex) {
                LOGGER.log(Level.SEVERE, "Erreur lors de l'ajout du stock", ex);
                JOptionPane.showMessageDialog(this,
                        "Erreur technique: " + ex.getMessage(),
                        "Erreur critique",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        // Bouton Supprimer
        boutonSupprimer.addActionListener((ActionEvent e) -> {
            if (stockSelectionne != null) {
                int confirmation = DialogUtils.afficherConfirmation(this,
                        "Êtes-vous sûr de vouloir supprimer ce stock ?",
                        "Confirmation de suppression",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);

                if (confirmation == JOptionPane.YES_OPTION) {
                    boolean succes = stockController.supprimer(stockSelectionne.getId());
                    if (succes) {
                        JOptionPane.showMessageDialog(this,
                                "Stock supprimé avec succès",
                                "Succès",
                                JOptionPane.INFORMATION_MESSAGE);
                        effacerFormulaire();
                        chargerTousLesStocks();
                    } else {
                        JOptionPane.showMessageDialog(this,
                                "Erreur lors de la suppression du stock",
                                "Erreur",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        // Bouton Effacer
        boutonEffacer.addActionListener((ActionEvent e) -> {
            effacerFormulaire();
        });

        // Bouton Rechercher
        boutonRechercher.addActionListener((ActionEvent e) -> {
            String terme = champRechercheProduit.getText().trim();
            chargerStocksFiltrés();
        });

        // ComboBox Filtre
        comboFiltre.addActionListener((ActionEvent e) -> {
            chargerStocksFiltrés();
        });

        // Ajouter la gestion de l'autocomplétion
        champRechercheProduit.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                SwingUtilities.invokeLater(() -> {
                    if (!popupClicEnCours) {
                        mettreAJourSuggestions();
                    }
                });
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                SwingUtilities.invokeLater(() -> {
                    if (!popupClicEnCours) {
                        mettreAJourSuggestions();
                    }
                });
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                SwingUtilities.invokeLater(() -> {
                    if (!popupClicEnCours) {
                        mettreAJourSuggestions();
                    }
                });
            }
        });

        // Gérer le focus du champ de recherche
        champRechercheProduit.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (champRechercheProduit.getText().equals("Rechercher un produit...")) {
                    champRechercheProduit.setText("");
                    champRechercheProduit.setForeground(Color.BLACK);
                }
                mettreAJourSuggestions();
            }

            @Override
            public void focusLost(FocusEvent e) {
                // Ne pas fermer le popup si on clique dessus
                Component opposite = e.getOppositeComponent();
                if (opposite != null &&
                        (SwingUtilities.isDescendingFrom(opposite, popupSuggestions) ||
                                opposite instanceof JMenuItem)) {
                    return;
                }

                // Attendre un peu avant de fermer le popup
                Timer timer = new Timer(1000, (ActionEvent ae) -> {
                    if (!popupClicEnCours) {
                        popupSuggestions.setVisible(false);
                        if (champRechercheProduit.getText().isEmpty()) {
                            champRechercheProduit.setForeground(Color.GRAY);
                            champRechercheProduit.setText("Rechercher un produit...");
                        }
                    }
                });
                timer.setRepeats(false);
                timer.start();
            }
        });

        // Gérer les interactions avec le popup
        popupSuggestions.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                popupClicEnCours = true;
            }

            @Override
            public void mouseExited(MouseEvent e) {
                // Vérifier si la souris est toujours sur un élément du popup
                Point p = e.getPoint();
                Component comp = SwingUtilities.getDeepestComponentAt(popupSuggestions, p.x, p.y);
                if (comp == null || !(comp instanceof JMenuItem)) {
                    popupClicEnCours = false;
                }
            }
        });

        // Ajouter la gestion du clavier pour la navigation dans les suggestions
        champRechercheProduit.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (popupSuggestions.isVisible()) {
                    int keyCode = e.getKeyCode();
                    switch (keyCode) {
                        case KeyEvent.VK_DOWN:
                            // Sélectionner le premier élément
                            if (popupSuggestions.getComponentCount() > 0) {
                                ((JMenuItem) popupSuggestions.getComponent(0)).requestFocus();
                            }
                            break;
                        case KeyEvent.VK_ESCAPE:
                            popupSuggestions.setVisible(false);
                            break;
                    }
                }
            }
        });
    }

    /**
     * Charge les données nécessaires pour la vue
     */
    private void chargerDonnees() {
        // Charger tous les produits
        produits = produitController.obtenirTousProduits();

        // Charger tous les stocks
        chargerTousLesStocks();
    }

    /**
     * Charge tous les stocks dans le tableau
     */
    private void chargerTousLesStocks() {
        List<Stock> stocks = stockController.obtenirTous();
        actualiserTableAvecResultats(stocks);
    }

    /**
     * Charge les stocks filtrés selon les critères sélectionnés
     */
    private void chargerStocksFiltrés() {
        String filtre = (String) comboFiltre.getSelectedItem();
        String recherche = champRechercheProduit.getText().trim();
        List<Stock> stocks;

        if ("Expiré".equals(filtre)) {
            stocks = stockController.obtenirStocksExpires();
        } else if ("Expire bientôt".equals(filtre)) {
            stocks = stockController.obtenirStocksExpirantBientot(30); // 30 jours
        } else {
            stocks = stockController.obtenirTous();
        }

        // Filtrer par recherche si nécessaire
        if (!recherche.isEmpty()) {
            stocks = stocks.stream()
                    .filter(s -> s.getProduit().getNom().toLowerCase().contains(recherche.toLowerCase()) ||
                            s.getNumeroLot().toLowerCase().contains(recherche.toLowerCase()))
                    .toList();
        }

        actualiserTableAvecResultats(stocks);
    }

    /**
     * Met à jour le tableau avec une liste de stocks
     *
     * @param stocks La liste des stocks à afficher
     */
    private void actualiserTableAvecResultats(List<Stock> stocks) {
        // Vider le tableau
        tableModel.setRowCount(0);

        // Remplir avec les données
        for (Stock stock : stocks) {
            long joursRestants = DateUtil.joursJusquaExpiration(stock.getDateExpiration());
            Object[] row = {
                    stock.getId(),
                    stock.getProduit().getNom(),
                    stock.getNumeroLot(),
                    stock.getQuantite(),
                    DateUtil.formaterDate(stock.getDateExpiration()),
                    joursRestants < 0 ? "Expiré" : joursRestants
            };
            tableModel.addRow(row);
        }

        // Mettre en évidence les stocks expirés ou expirant bientôt
        colorerLignesSelonExpiration();
    }

    /**
     * Colore les lignes du tableau selon la date d'expiration
     */
    private void colorerLignesSelonExpiration() {
        tableStocks.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                Object joursObj = table.getValueAt(row, 5);

                if ("Expiré".equals(joursObj)) {
                    c.setBackground(isSelected ? new Color(255, 150, 150) : new Color(255, 200, 200));
                } else {
                    long jours = joursObj instanceof Long ? (Long) joursObj : Long.parseLong(joursObj.toString());
                    if (jours <= 30) {
                        c.setBackground(isSelected ? new Color(255, 200, 150) : new Color(255, 235, 200));
                    } else {
                        c.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
                    }
                }

                return c;
            }
        });
    }

    /**
     * Remplit le formulaire avec les données d'un stock
     *
     * @param stock Le stock dont les données seront affichées
     */
    private void remplirFormulaire(Stock stock) {
        // Mettre à jour le champ de recherche et le produit sélectionné
        produitSelectionne = stock.getProduit();
        champRechercheProduit.setText(stock.getProduit().getNom());
        labelProduitSelectionne.setText("Produit sélectionné: " + stock.getProduit().getNom());

        champLot.setText(stock.getNumeroLot());
        champQuantite.setText(String.valueOf(stock.getQuantite()));
        champDateExpiration.setValue(java.sql.Date.valueOf(stock.getDateExpiration()));
    }

    /**
     * Efface le formulaire et réinitialise les boutons
     */
    private void effacerFormulaire() {
        try {
            if (comboFiltre.getItemCount() > 0) {
                comboFiltre.setSelectedIndex(0);
            }
            champRechercheProduit.setText("");
            labelProduitSelectionne.setText("");
            champLot.setText("");
            champQuantite.setText("");
            // Initialiser la date d'expiration à la date actuelle + 1 an
            champDateExpiration.setValue(java.sql.Date.valueOf(LocalDate.now().plusYears(1)));

            stockSelectionne = null;
            tableStocks.clearSelection();
            boutonSupprimer.setEnabled(false);

            // Recharger tous les produits pour le combobox (au cas où des produits ont été
            // ajoutés)
            chargerDonnees();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de l'effacement du formulaire", e);
        }
    }

    /**
     * Extrait les données du formulaire pour créer un objet Stock
     *
     * @return Le stock créé à partir des données du formulaire
     */
    private Stock extraireFormulaire() {
        Stock stock = new Stock();

        try {
            // Vérifier si un produit est sélectionné
            if (produitSelectionne == null) {
                LOGGER.warning("Aucun produit sélectionné lors de l'extraction du formulaire");
                throw new IllegalStateException("Aucun produit sélectionné");
            }
            stock.setProduit(produitSelectionne);

            // Récupérer le numéro de lot
            String numeroLot = champLot.getText().trim();
            if (numeroLot.isEmpty()) {
                LOGGER.warning("Numéro de lot vide lors de l'extraction du formulaire");
                throw new IllegalStateException("Numéro de lot vide");
            }
            stock.setNumeroLot(numeroLot);

            // Récupérer la quantité
            try {
                int quantite = Integer.parseInt(champQuantite.getText().trim());
                if (quantite <= 0) {
                    LOGGER.warning("Quantité invalide lors de l'extraction du formulaire: " + quantite);
                    throw new IllegalStateException("La quantité doit être positive");
                }
                stock.setQuantite(quantite);
            } catch (NumberFormatException e) {
                LOGGER.warning("Format de quantité invalide lors de l'extraction du formulaire");
                throw new IllegalStateException("Format de quantité invalide");
            }

            // Récupérer et convertir la date d'expiration
            try {
                // Convertir java.util.Date en LocalDate
                Object value = champDateExpiration.getValue();
                if (value == null) {
                    LOGGER.warning("Date d'expiration nulle lors de l'extraction du formulaire");
                    throw new IllegalStateException("Date d'expiration nulle");
                }

                java.util.Date dateUtil = (java.util.Date) value;
                LocalDate dateExpiration = dateUtil.toInstant()
                        .atZone(java.time.ZoneId.systemDefault())
                        .toLocalDate();

                // Vérifier que la date est dans le futur
                if (dateExpiration.isBefore(LocalDate.now())) {
                    LOGGER.warning("Date d'expiration dans le passé: " + dateExpiration);
                    throw new IllegalStateException("La date d'expiration doit être dans le futur");
                }

                stock.setDateExpiration(dateExpiration);
            } catch (ClassCastException e) {
                LOGGER.log(Level.SEVERE, "Erreur de cast lors de la conversion de la date", e);
                throw new IllegalStateException("Format de date invalide");
            }

            return stock;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de l'extraction du formulaire", e);
            JOptionPane.showMessageDialog(null,
                    "Erreur lors de la création du stock: " + e.getMessage(),
                    "Erreur de formulaire",
                    JOptionPane.ERROR_MESSAGE);
            throw new RuntimeException("Erreur lors de l'extraction du formulaire", e);
        }
    }

    /**
     * Valide les données du formulaire
     *
     * @return true si les données sont valides, false sinon
     */
    private boolean validerFormulaire() {
        // Vérifier qu'un produit est sélectionné
        if (produitSelectionne == null) {
            JOptionPane.showMessageDialog(this,
                    "Veuillez sélectionner un produit",
                    "Erreur de validation",
                    JOptionPane.ERROR_MESSAGE);
            champRechercheProduit.requestFocus();
            return false;
        }

        // Vérifier que le numéro de lot n'est pas vide
        if (champLot.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Le numéro de lot est obligatoire",
                    "Erreur de validation",
                    JOptionPane.ERROR_MESSAGE);
            champLot.requestFocus();
            return false;
        }

        // Vérifier que la quantité est valide
        try {
            int quantite = Integer.parseInt(champQuantite.getText().trim());
            if (quantite <= 0) {
                JOptionPane.showMessageDialog(this,
                        "La quantité doit être positive",
                        "Erreur de validation",
                        JOptionPane.ERROR_MESSAGE);
                champQuantite.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "La quantité doit être un nombre entier valide",
                    "Erreur de validation",
                    JOptionPane.ERROR_MESSAGE);
            champQuantite.requestFocus();
            return false;
        }

        // Vérifier que la date d'expiration est dans le futur
        java.util.Date dateUtil = (java.util.Date) champDateExpiration.getValue();
        java.sql.Date dateSql = new java.sql.Date(dateUtil.getTime());
        LocalDate dateExpiration = dateSql.toLocalDate();
        LocalDate aujourdhui = LocalDate.now();

        if (dateExpiration.isBefore(aujourdhui)) {
            JOptionPane.showMessageDialog(this,
                    "La date d'expiration doit être dans le futur",
                    "Erreur de validation",
                    JOptionPane.ERROR_MESSAGE);
            champDateExpiration.requestFocus();
            return false;
        }

        return true;
    }

    /**
     * Crée un champ de texte stylisé avec une apparence moderne
     * 
     * @param columns Nombre de colonnes du champ de texte
     * @return Le champ de texte stylisé
     */
    private JTextField createStyledTextField(int columns) {
        JTextField textField = new JTextField(columns);
        textField.setFont(new Font("Arial", Font.PLAIN, 14));
        textField.setPreferredSize(new Dimension(textField.getPreferredSize().width, 30));
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        // Ajouter un effet de focus
        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                textField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(52, 152, 219), 2),
                        BorderFactory.createEmptyBorder(5, 5, 5, 5)));
            }

            @Override
            public void focusLost(FocusEvent e) {
                textField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(189, 195, 199)),
                        BorderFactory.createEmptyBorder(5, 5, 5, 5)));
            }
        });

        return textField;
    }

    /**
     * Style un bouton avec des couleurs et effets visuels
     * 
     * @param button            Le bouton à styliser
     * @param couleurPrincipale La couleur principale du bouton
     */
    private void styleButton(JButton button, Color couleurPrincipale) {
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(couleurPrincipale);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(120, 36));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Ajouter des effets de survol
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(couleurPrincipale.darker());
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(couleurPrincipale);
            }
        });
    }

    private void mettreAJourSuggestions() {
        String recherche = champRechercheProduit.getText().toLowerCase().trim();
        if (recherche.isEmpty() || recherche.equals("rechercher un produit...")) {
            popupSuggestions.setVisible(false);
            return;
        }

        popupSuggestions.removeAll();
        boolean trouveCorrespondance = false;

        for (Produit produit : produits) {
            if (produit.getNom().toLowerCase().contains(recherche)) {
                trouveCorrespondance = true;
                JMenuItem item = new JMenuItem(produit.getNom());
                item.setFont(new Font("Arial", Font.PLAIN, 14));

                // Améliorer l'apparence des éléments du menu
                item.setPreferredSize(new Dimension(champRechercheProduit.getWidth(), 30));
                item.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

                item.addActionListener(e -> {
                    produitSelectionne = produit;
                    champRechercheProduit.setText(produit.getNom());
                    labelProduitSelectionne.setText("Produit sélectionné: " + produit.getNom());
                    popupSuggestions.setVisible(false);
                    popupClicEnCours = false;
                    champRechercheProduit.requestFocus();
                });

                // Ajouter un effet de survol
                item.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        item.setBackground(new Color(240, 240, 240));
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        item.setBackground(UIManager.getColor("MenuItem.background"));
                    }
                });

                popupSuggestions.add(item);
            }
        }

        if (trouveCorrespondance) {
            if (!popupSuggestions.isVisible()) {
                popupSuggestions.show(champRechercheProduit, 0, champRechercheProduit.getHeight());
                // Ajuster la largeur du popup pour correspondre au champ de recherche
                popupSuggestions.setPopupSize(champRechercheProduit.getWidth(),
                        Math.min(200, popupSuggestions.getComponentCount() * 30));
            }
            popupSuggestions.revalidate();
            popupSuggestions.repaint();
        } else {
            popupSuggestions.setVisible(false);
        }
    }
}