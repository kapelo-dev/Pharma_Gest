package com.pharmajava.view;

import com.pharmajava.controller.ClientController;
import com.pharmajava.controller.VenteController;
import com.pharmajava.model.Client;
import com.pharmajava.model.Vente;
import com.pharmajava.model.ProduitVendu;
import com.pharmajava.utils.DialogUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.awt.event.ActionEvent;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.JDialog;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

/**
 * Vue pour afficher les clients et l'historique de leurs achats
 */
public class ClientAchatsView extends JPanel {
    private final ClientController clientController;
    private final VenteController venteController;

    // Format pour les montants
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.FRANCE);

    // Format pour les dates
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // Composants
    private JTable tableClients;
    private DefaultTableModel modelTableClients;

    private JTable tableVentes;
    private DefaultTableModel modelTableVentes;

    private JTable tableProduits;
    private DefaultTableModel modelTableProduits;

    private JLabel labelClient;
    private JLabel labelTotalAchats;
    private JTextField champRecherche;

    // Sélection
    private Client clientSelectionne;
    private Vente venteSelectionnee;

    /**
     * Constructeur
     */
    public ClientAchatsView() {
        clientController = new ClientController();
        venteController = new VenteController();

        // Modifier le format monétaire pour utiliser FCFA au lieu de l'euro
        currencyFormat.setCurrency(java.util.Currency.getInstance("XOF"));

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(Color.WHITE);

        initialiserComposants();
        chargerClients();
    }

    /**
     * Initialise les composants de l'interface
     */
    private void initialiserComposants() {
        // En-tête
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Panneau principal
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));

        // Split pane horizontal entre la liste des clients et les détails
        JSplitPane splitPaneHorizontal = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPaneHorizontal.setDividerLocation(300);
        splitPaneHorizontal.setDividerSize(5);

        // Panneau gauche : liste des clients
        JPanel panneauClients = createClientsPanel();
        splitPaneHorizontal.setLeftComponent(panneauClients);

        // Panneau droit : split vertical entre ventes et détails de vente
        JSplitPane splitPaneVertical = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPaneVertical.setDividerLocation(250);
        splitPaneVertical.setDividerSize(5);

        // Panneau des ventes du client
        JPanel panneauVentes = createVentesPanel();
        splitPaneVertical.setTopComponent(panneauVentes);

        // Panneau des produits de la vente
        JPanel panneauProduits = createProduitsPanel();
        splitPaneVertical.setBottomComponent(panneauProduits);

        splitPaneHorizontal.setRightComponent(splitPaneVertical);

        mainPanel.add(splitPaneHorizontal, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);
    }

    /**
     * Crée le panneau d'en-tête
     */
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(new Color(41, 128, 185));
        panel.setBorder(new EmptyBorder(10, 15, 10, 15));

        JLabel labelTitre = new JLabel("Clients et leurs achats");
        labelTitre.setForeground(Color.WHITE);
        labelTitre.setFont(new Font("Segoe UI", Font.BOLD, 18));
        panel.add(labelTitre, BorderLayout.WEST);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setOpaque(false);

        champRecherche = new JTextField(15);
        champRecherche.setToolTipText("Rechercher un client");
        champRecherche.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                    rechercherClients();
                }
            }
        });

        JButton boutonRecherche = new JButton("Rechercher");
        boutonRecherche.addActionListener(e -> rechercherClients());

        searchPanel.add(champRecherche);
        searchPanel.add(boutonRecherche);

        panel.add(searchPanel, BorderLayout.EAST);

        return panel;
    }

    /**
     * Crée le panneau de la liste des clients
     */
    private JPanel createClientsPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Liste des clients"),
                new EmptyBorder(10, 10, 10, 10)));

        // Modèle de la table
        String[] colonnes = { "ID", "Nom", "Prénom", "Téléphone", "Actions" };
        modelTableClients = new DefaultTableModel(colonnes, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4;
            }
        };

        tableClients = new JTable(modelTableClients);
        tableClients.getTableHeader().setReorderingAllowed(false);
        tableClients.getTableHeader().setResizingAllowed(true);

        // Ajuster les largeurs des colonnes
        tableClients.getColumnModel().getColumn(0).setPreferredWidth(40);
        tableClients.getColumnModel().getColumn(1).setPreferredWidth(100);
        tableClients.getColumnModel().getColumn(2).setPreferredWidth(100);
        tableClients.getColumnModel().getColumn(3).setPreferredWidth(120);
        tableClients.getColumnModel().getColumn(4).setPreferredWidth(100);

        // Gestion des événements de la table
        tableClients.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int ligne = tableClients.getSelectedRow();
                if (ligne >= 0) {
                    int id = Integer.parseInt(tableClients.getValueAt(ligne, 0).toString());
                    selectionnerClient(id);
                }
            }
        });

        // Configuration du renderer et de l'editor pour la colonne des boutons
        tableClients.getColumnModel().getColumn(4).setCellRenderer(new ButtonRenderer());
        tableClients.getColumnModel().getColumn(4).setCellEditor(new ButtonEditor(new JCheckBox()));

        JScrollPane scrollPane = new JScrollPane(tableClients);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Ajouter un bouton pour créer un nouveau client
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton boutonAjouter = new JButton("Ajouter un client");
        boutonAjouter.setBackground(new Color(46, 204, 113)); // Vert
        boutonAjouter.setForeground(Color.WHITE);
        boutonAjouter.setFont(new Font("Segoe UI", Font.BOLD, 12));
        boutonAjouter.addActionListener(e -> ouvrirDialogueAjout());
        buttonPanel.add(boutonAjouter);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Crée le panneau des ventes du client
     */
    private JPanel createVentesPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));

        // En-tête du panneau des ventes
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(new EmptyBorder(0, 0, 10, 0));

        labelClient = new JLabel("Aucun client sélectionné");
        labelClient.setFont(new Font("Segoe UI", Font.BOLD, 16));
        headerPanel.add(labelClient, BorderLayout.WEST);

        labelTotalAchats = new JLabel("Total des achats: 0 FCFA");
        headerPanel.add(labelTotalAchats, BorderLayout.EAST);

        panel.add(headerPanel, BorderLayout.NORTH);

        // Table des ventes
        String[] colonnes = { "ID", "Date", "Montant", "Pharmacien" };
        modelTableVentes = new DefaultTableModel(colonnes, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableVentes = new JTable(modelTableVentes);
        tableVentes.getTableHeader().setReorderingAllowed(false);

        // Ajuster les largeurs des colonnes
        tableVentes.getColumnModel().getColumn(0).setPreferredWidth(40);
        tableVentes.getColumnModel().getColumn(1).setPreferredWidth(150);
        tableVentes.getColumnModel().getColumn(2).setPreferredWidth(100);
        tableVentes.getColumnModel().getColumn(3).setPreferredWidth(150);

        // Gestion des événements de la table
        tableVentes.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int ligne = tableVentes.getSelectedRow();
                if (ligne >= 0) {
                    int id = Integer.parseInt(tableVentes.getValueAt(ligne, 0).toString());
                    selectionnerVente(id);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(tableVentes);
        panel.add(scrollPane, BorderLayout.CENTER);

        panel.setBorder(BorderFactory.createTitledBorder("Historique des achats"));

        return panel;
    }

    /**
     * Crée le panneau des produits d'une vente
     */
    private JPanel createProduitsPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Détails de la vente"),
                new EmptyBorder(10, 10, 10, 10)));

        // Modèle de la table
        String[] colonnes = { "Produit", "Quantité", "Prix unitaire", "Prix total" };
        modelTableProduits = new DefaultTableModel(colonnes, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableProduits = new JTable(modelTableProduits);
        tableProduits.getTableHeader().setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(tableProduits);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Charge la liste des clients dans la table
     */
    private void chargerClients() {
        List<Client> clients = clientController.obtenirTousLesClients();
        afficherClients(clients);
    }

    /**
     * Recherche les clients selon le terme de recherche
     */
    private void rechercherClients() {
        String terme = champRecherche.getText().trim();
        if (terme.isEmpty()) {
            chargerClients();
        } else {
            List<Client> clients = clientController.rechercherClients(terme);
            afficherClients(clients);
        }
    }

    /**
     * Affiche la liste des clients dans la table
     */
    private void afficherClients(List<Client> clients) {
        modelTableClients.setRowCount(0);

        for (Client client : clients) {
            modelTableClients.addRow(new Object[] {
                    client.getId(),
                    client.getNom(),
                    client.getPrenom(),
                    client.getTelephone(),
                    "✏️ Modifier"
            });
        }
    }

    /**
     * Sélectionne un client et affiche ses ventes
     */
    private void selectionnerClient(int id) {
        clientSelectionne = clientController.obtenirClientParId(id);
        if (clientSelectionne != null) {
            // Mettre à jour le label avec le nom du client
            labelClient.setText("Client: " + clientSelectionne.getNomComplet());

            // Charger les ventes du client
            List<Vente> ventes = venteController.obtenirVentesParClient(clientSelectionne.getId());
            afficherVentes(ventes);

            // Calculer le total des achats
            double totalAchats = 0;
            for (Vente vente : ventes) {
                totalAchats += vente.getMontantTotal().doubleValue();
            }

            labelTotalAchats.setText("Total des achats: " + currencyFormat.format(totalAchats));
        }
    }

    /**
     * Affiche les ventes d'un client
     */
    private void afficherVentes(List<Vente> ventes) {
        modelTableVentes.setRowCount(0);
        modelTableProduits.setRowCount(0); // Effacer les détails de vente

        for (Vente vente : ventes) {
            String nomPharmacien = vente.getPharmacien() != null
                    ? vente.getPharmacien().getPrenom() + " " + vente.getPharmacien().getNom()
                    : "Non spécifié";

            modelTableVentes.addRow(new Object[] {
                    vente.getId(),
                    vente.getDateVente().format(dateTimeFormatter),
                    currencyFormat.format(vente.getMontantTotal()),
                    nomPharmacien
            });
        }
    }

    /**
     * Sélectionne une vente et affiche ses produits
     */
    private void selectionnerVente(int id) {
        venteSelectionnee = venteController.obtenirVenteParId(id);
        if (venteSelectionnee != null) {
            afficherProduits(venteSelectionnee.getProduitsVendus());
        } else {
            DialogUtils.afficherErreur(this,
                    "Impossible de charger les détails de la vente",
                    "Erreur de chargement");
        }
    }

    /**
     * Affiche les produits d'une vente
     */
    private void afficherProduits(List<ProduitVendu> produits) {
        modelTableProduits.setRowCount(0);

        for (ProduitVendu produit : produits) {
            modelTableProduits.addRow(new Object[] {
                    produit.getProduit().getNom(),
                    produit.getQuantite(),
                    currencyFormat.format(produit.getPrixUnitaire()),
                    currencyFormat.format(produit.getPrixTotal())
            });
        }
    }

    /**
     * Classe pour rendre les boutons dans la table
     */
    class ButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
            setBackground(new Color(240, 240, 240));
            setBorderPainted(true);
            setFocusPainted(false);
            setForeground(new Color(41, 128, 185)); // Bleu pour meilleure visibilité
            setFont(new Font("Segoe UI", Font.BOLD, 12));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "" : value.toString());

            // Améliorer l'apparence lors de la sélection
            if (isSelected) {
                setBackground(new Color(220, 230, 240));
            } else {
                setBackground(new Color(240, 240, 240));
            }

            return this;
        }
    }

    /**
     * Classe pour gérer les clics sur les boutons dans la table
     */
    class ButtonEditor extends DefaultCellEditor {
        protected JButton button;
        private String label;
        private boolean isPushed;
        private int selectedRow;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> fireEditingStopped());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            label = (value == null) ? "" : value.toString();
            button.setText(label);
            selectedRow = row;
            isPushed = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                // Récupérer l'ID du client à modifier
                int id = Integer.parseInt(tableClients.getValueAt(selectedRow, 0).toString());
                // Ouvrir la boîte de dialogue de modification
                ouvrirDialogueModification(id);
            }
            isPushed = false;
            return label;
        }

        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }
    }

    /**
     * Ouvre une boîte de dialogue pour modifier un client
     */
    private void ouvrirDialogueModification(int clientId) {
        // Récupérer le client à modifier
        Client client = clientController.obtenirClientParId(clientId);
        if (client == null) {
            DialogUtils.afficherErreur(this, "Client introuvable", "Erreur");
            return;
        }

        // Créer la boîte de dialogue
        JDialog dialogModification = new JDialog();
        dialogModification.setTitle("Modifier le client");
        dialogModification.setModal(true);
        dialogModification.setSize(400, 350);
        dialogModification.setLocationRelativeTo(this);

        // Créer le panneau principal
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Créer le formulaire
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Champs du formulaire
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Nom:"), gbc);

        gbc.gridx = 1;
        JTextField champNom = new JTextField(20);
        champNom.setText(client.getNom());
        formPanel.add(champNom, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Prénom:"), gbc);

        gbc.gridx = 1;
        JTextField champPrenom = new JTextField(20);
        champPrenom.setText(client.getPrenom());
        formPanel.add(champPrenom, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Téléphone:"), gbc);

        gbc.gridx = 1;
        JTextField champTelephone = new JTextField(20);
        champTelephone.setText(client.getTelephone());
        formPanel.add(champTelephone, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Email:"), gbc);

        gbc.gridx = 1;
        JTextField champEmail = new JTextField(20);
        champEmail.setText(client.getEmail() != null ? client.getEmail() : "");
        formPanel.add(champEmail, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("Adresse:"), gbc);

        gbc.gridx = 1;
        JTextArea champAdresse = new JTextArea(3, 20);
        champAdresse.setLineWrap(true);
        champAdresse.setText(client.getAdresse() != null ? client.getAdresse() : "");
        JScrollPane scrollAdresse = new JScrollPane(champAdresse);
        formPanel.add(scrollAdresse, gbc);

        panel.add(formPanel, BorderLayout.CENTER);

        // Panneau des boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton boutonEnregistrer = new JButton("Enregistrer");
        boutonEnregistrer.setBackground(new Color(52, 152, 219)); // Bleu
        boutonEnregistrer.setForeground(Color.WHITE);
        boutonEnregistrer.addActionListener(e -> {
            // Validation des champs
            if (champNom.getText().trim().isEmpty() ||
                    champPrenom.getText().trim().isEmpty() ||
                    champTelephone.getText().trim().isEmpty()) {
                DialogUtils.afficherErreur(dialogModification,
                        "Les champs Nom, Prénom et Téléphone sont obligatoires",
                        "Erreur de validation");
                return;
            }

            // Mettre à jour le client
            client.setNom(champNom.getText().trim());
            client.setPrenom(champPrenom.getText().trim());
            client.setTelephone(champTelephone.getText().trim());
            client.setEmail(champEmail.getText().trim());
            client.setAdresse(champAdresse.getText().trim());

            // Enregistrer les modifications
            boolean success = clientController.mettreAJourClient(client);
            if (success) {
                DialogUtils.afficherInformation(dialogModification,
                        "Client modifié avec succès",
                        "Succès");
                dialogModification.dispose();

                // Rafraîchir l'affichage
                if (clientSelectionne != null && clientSelectionne.getId() == client.getId()) {
                    selectionnerClient(client.getId());
                }
                chargerClients();
            } else {
                DialogUtils.afficherErreur(dialogModification,
                        "Erreur lors de la modification du client",
                        "Erreur");
            }
        });

        JButton boutonAnnuler = new JButton("Annuler");
        boutonAnnuler.addActionListener(e -> dialogModification.dispose());

        buttonPanel.add(boutonEnregistrer);
        buttonPanel.add(boutonAnnuler);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Ajouter le panneau à la boîte de dialogue
        dialogModification.add(panel);
        dialogModification.setVisible(true);
    }

    /**
     * Ouvre une boîte de dialogue pour ajouter un nouveau client
     */
    private void ouvrirDialogueAjout() {
        // Créer la boîte de dialogue
        JDialog dialogAjout = new JDialog();
        dialogAjout.setTitle("Ajouter un client");
        dialogAjout.setModal(true);
        dialogAjout.setSize(400, 350);
        dialogAjout.setLocationRelativeTo(this);

        // Créer le panneau principal
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Créer le formulaire
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Champs du formulaire
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Nom:"), gbc);

        gbc.gridx = 1;
        JTextField champNom = new JTextField(20);
        formPanel.add(champNom, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Prénom:"), gbc);

        gbc.gridx = 1;
        JTextField champPrenom = new JTextField(20);
        formPanel.add(champPrenom, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Téléphone:"), gbc);

        gbc.gridx = 1;
        JTextField champTelephone = new JTextField(20);
        formPanel.add(champTelephone, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Email:"), gbc);

        gbc.gridx = 1;
        JTextField champEmail = new JTextField(20);
        formPanel.add(champEmail, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("Adresse:"), gbc);

        gbc.gridx = 1;
        JTextArea champAdresse = new JTextArea(3, 20);
        champAdresse.setLineWrap(true);
        JScrollPane scrollAdresse = new JScrollPane(champAdresse);
        formPanel.add(scrollAdresse, gbc);

        panel.add(formPanel, BorderLayout.CENTER);

        // Panneau des boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton boutonEnregistrer = new JButton("Enregistrer");
        boutonEnregistrer.setBackground(new Color(46, 204, 113)); // Vert
        boutonEnregistrer.setForeground(Color.WHITE);
        boutonEnregistrer.addActionListener(e -> {
            // Validation des champs
            if (champNom.getText().trim().isEmpty() ||
                    champPrenom.getText().trim().isEmpty() ||
                    champTelephone.getText().trim().isEmpty()) {
                DialogUtils.afficherErreur(dialogAjout,
                        "Les champs Nom, Prénom et Téléphone sont obligatoires",
                        "Erreur de validation");
                return;
            }

            // Créer un nouveau client
            Client nouveauClient = new Client();
            nouveauClient.setNom(champNom.getText().trim());
            nouveauClient.setPrenom(champPrenom.getText().trim());
            nouveauClient.setTelephone(champTelephone.getText().trim());
            nouveauClient.setEmail(champEmail.getText().trim());
            nouveauClient.setAdresse(champAdresse.getText().trim());

            // Enregistrer le client
            Client clientAjoute = clientController.ajouterClient(nouveauClient);
            if (clientAjoute != null) {
                DialogUtils.afficherInformation(dialogAjout,
                        "Client ajouté avec succès",
                        "Succès");
                dialogAjout.dispose();

                // Rafraîchir l'affichage
                chargerClients();

                // Sélectionner le nouveau client
                selectionnerClient(clientAjoute.getId());
            } else {
                DialogUtils.afficherErreur(dialogAjout,
                        "Erreur lors de l'ajout du client",
                        "Erreur");
            }
        });

        JButton boutonAnnuler = new JButton("Annuler");
        boutonAnnuler.addActionListener(e -> dialogAjout.dispose());

        buttonPanel.add(boutonEnregistrer);
        buttonPanel.add(boutonAnnuler);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Ajouter le panneau à la boîte de dialogue
        dialogAjout.add(panel);
        dialogAjout.setVisible(true);
    }
}