package com.pharmajava.view;

import com.pharmajava.controller.ClientController;
import com.pharmajava.model.Client;
import com.pharmajava.utils.DialogUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Vue pour la gestion des clients
 */
public class ClientView extends JPanel {
    private final ClientController controller;

    // Composants
    private JTextField champNom;
    private JTextField champPrenom;
    private JTextField champTelephone;
    private JTextField champEmail;
    private JTextArea champAdresse;
    private JTable tableClients;
    private DefaultTableModel modelTable;
    private JButton boutonAjouter;
    private JButton boutonModifier;
    private JButton boutonSupprimer;
    private JTextField champRecherche;
    private JLabel statusLabel;

    // Client sélectionné
    private Client clientSelectionne;

    /**
     * Constructeur
     */
    public ClientView() {
        controller = new ClientController();

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
        setLayout(new BorderLayout());

        // En-tête
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Panneau principal avec split
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(400);
        splitPane.setDividerSize(5);

        // Panneau gauche : formulaire
        JPanel panneauFormulaire = createFormPanel();
        splitPane.setLeftComponent(panneauFormulaire);

        // Panneau droit : table des clients
        JPanel panneauTable = createTablePanel();
        splitPane.setRightComponent(panneauTable);

        add(splitPane, BorderLayout.CENTER);

        // Panneau de statut en bas
        statusLabel = new JLabel(" ");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        statusLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        add(statusLabel, BorderLayout.SOUTH);
    }

    /**
     * Crée le panneau d'en-tête
     */
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(new Color(41, 128, 185));
        panel.setBorder(new EmptyBorder(10, 15, 10, 15));

        JLabel labelTitre = new JLabel("Gestion des Clients");
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
     * Crée le panneau du formulaire
     */
    private JPanel createFormPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(0, 10));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Informations du client"),
                new EmptyBorder(10, 10, 10, 10)));

        // Ajout d'un panneau pour le texte d'aide
        JPanel helpPanel = new JPanel(new BorderLayout());
        helpPanel.setBackground(new Color(245, 245, 245));
        helpPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 10, 5));

        JLabel helpLabel = new JLabel(
                "<html><body style='width: 300px'>" +
                        "<b>Pour modifier un client :</b><br>" +
                        "1. Cliquez sur une ligne du tableau ou sur le bouton \"Modifier\" correspondant<br>" +
                        "2. Modifiez les informations dans ce formulaire<br>" +
                        "3. Cliquez sur le bouton \"Modifier\" ci-dessous pour enregistrer" +
                        "</body></html>");
        helpLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        helpLabel.setForeground(new Color(80, 80, 80));

        helpPanel.add(helpLabel, BorderLayout.CENTER);
        panel.add(helpPanel, BorderLayout.NORTH);

        JPanel formFields = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Champs du formulaire
        gbc.gridx = 0;
        gbc.gridy = 0;
        formFields.add(new JLabel("Nom:"), gbc);

        gbc.gridx = 1;
        champNom = new JTextField(20);
        formFields.add(champNom, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formFields.add(new JLabel("Prénom:"), gbc);

        gbc.gridx = 1;
        champPrenom = new JTextField(20);
        formFields.add(champPrenom, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        formFields.add(new JLabel("Téléphone:"), gbc);

        gbc.gridx = 1;
        champTelephone = new JTextField(20);
        formFields.add(champTelephone, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        formFields.add(new JLabel("Email:"), gbc);

        gbc.gridx = 1;
        champEmail = new JTextField(20);
        formFields.add(champEmail, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        formFields.add(new JLabel("Adresse:"), gbc);

        gbc.gridx = 1;
        champAdresse = new JTextArea(3, 20);
        champAdresse.setLineWrap(true);
        JScrollPane scrollAdresse = new JScrollPane(champAdresse);
        formFields.add(scrollAdresse, gbc);

        panel.add(formFields, BorderLayout.CENTER);

        // Panneau des boutons
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        boutonAjouter = new JButton("Ajouter");
        boutonAjouter.setBackground(new Color(46, 204, 113)); // Vert pour ajouter
        boutonAjouter.setForeground(Color.WHITE);
        boutonAjouter.addActionListener(this::ajouterClient);

        boutonModifier = new JButton("Modifier");
        boutonModifier.setBackground(new Color(52, 152, 219)); // Bleu pour modifier
        boutonModifier.setForeground(Color.WHITE);
        boutonModifier.addActionListener(this::modifierClient);
        boutonModifier.setEnabled(false);

        boutonSupprimer = new JButton("Supprimer");
        boutonSupprimer.setBackground(new Color(231, 76, 60)); // Rouge pour supprimer
        boutonSupprimer.setForeground(Color.WHITE);
        boutonSupprimer.addActionListener(this::supprimerClient);
        boutonSupprimer.setEnabled(false);

        JButton boutonNouveau = new JButton("Nouveau");
        boutonNouveau.setBackground(new Color(241, 196, 15)); // Jaune pour nouveau
        boutonNouveau.setForeground(Color.WHITE);
        boutonNouveau.addActionListener(e -> reinitialiserFormulaire());

        // Ajouter une marge extérieure aux boutons pour une meilleure visibilité
        boutonAjouter.setMargin(new Insets(5, 15, 5, 15));
        boutonModifier.setMargin(new Insets(5, 15, 5, 15));
        boutonSupprimer.setMargin(new Insets(5, 15, 5, 15));
        boutonNouveau.setMargin(new Insets(5, 15, 5, 15));

        buttonsPanel.add(boutonAjouter);
        buttonsPanel.add(boutonModifier);
        buttonsPanel.add(boutonSupprimer);
        buttonsPanel.add(boutonNouveau);

        panel.add(buttonsPanel, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Crée le panneau de la table des clients
     */
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Liste des clients"),
                new EmptyBorder(10, 10, 10, 10)));

        // Modèle de la table
        String[] colonnes = { "ID", "Nom", "Prénom", "Téléphone", "Email", "Actions" };
        modelTable = new DefaultTableModel(colonnes, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableClients = new JTable(modelTable);
        tableClients.getTableHeader().setReorderingAllowed(false);
        tableClients.getTableHeader().setResizingAllowed(true);

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

        JScrollPane scrollPane = new JScrollPane(tableClients);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Charge la liste des clients dans la table
     */
    private void chargerClients() {
        List<Client> clients = controller.obtenirTousLesClients();
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
            List<Client> clients = controller.rechercherClients(terme);
            afficherClients(clients);
        }
    }

    /**
     * Affiche la liste des clients dans la table
     */
    private void afficherClients(List<Client> clients) {
        modelTable.setRowCount(0);

        for (Client client : clients) {
            modelTable.addRow(new Object[] {
                    client.getId(),
                    client.getNom(),
                    client.getPrenom(),
                    client.getTelephone(),
                    client.getEmail(),
                    "Modifier" // Le texte du bouton
            });
        }

        // Ajouter le renderer pour le bouton d'édition avec style amélioré
        ButtonRenderer buttonRenderer = new ButtonRenderer();

        tableClients.getColumnModel().getColumn(5).setCellRenderer(buttonRenderer);
        tableClients.getColumnModel().getColumn(5).setCellEditor(new ButtonEditor(new JCheckBox()));

        // Ajuster la largeur de la colonne des boutons
        tableClients.getColumnModel().getColumn(5).setPreferredWidth(100);
    }

    /**
     * Sélectionne un client et remplit le formulaire
     */
    private void selectionnerClient(int id) {
        clientSelectionne = controller.obtenirClientParId(id);
        if (clientSelectionne != null) {
            // Remplir les champs avec les informations du client
            champNom.setText(clientSelectionne.getNom());
            champPrenom.setText(clientSelectionne.getPrenom());
            champTelephone.setText(clientSelectionne.getTelephone());
            champEmail.setText(clientSelectionne.getEmail() != null ? clientSelectionne.getEmail() : "");
            champAdresse.setText(clientSelectionne.getAdresse() != null ? clientSelectionne.getAdresse() : "");

            // Activer les boutons appropriés
            boutonModifier.setEnabled(true);
            boutonSupprimer.setEnabled(true);
            boutonAjouter.setEnabled(false);

            // Mettre en évidence le formulaire pour indiquer qu'un client est sélectionné
            champNom.setBackground(new Color(240, 248, 255)); // Couleur de fond légère pour indiquer la sélection
            champPrenom.setBackground(new Color(240, 248, 255));
            champTelephone.setBackground(new Color(240, 248, 255));
            champEmail.setBackground(new Color(240, 248, 255));
            champAdresse.setBackground(new Color(240, 248, 255));

            // Afficher un message de statut indiquant que le client est sélectionné
            statusLabel.setText("✓ Client sélectionné : " + clientSelectionne.getNomComplet()
                    + " — Vous pouvez maintenant modifier ses informations");
            statusLabel.setForeground(new Color(46, 204, 113)); // Vert pour le succès
        }
    }

    /**
     * Réinitialise le formulaire
     */
    private void reinitialiserFormulaire() {
        clientSelectionne = null;
        champNom.setText("");
        champPrenom.setText("");
        champTelephone.setText("");
        champEmail.setText("");
        champAdresse.setText("");

        // Réinitialiser la couleur de fond des champs
        champNom.setBackground(Color.WHITE);
        champPrenom.setBackground(Color.WHITE);
        champTelephone.setBackground(Color.WHITE);
        champEmail.setBackground(Color.WHITE);
        champAdresse.setBackground(Color.WHITE);

        boutonModifier.setEnabled(false);
        boutonSupprimer.setEnabled(false);
        boutonAjouter.setEnabled(true);

        tableClients.clearSelection();

        // Effacer le message de statut
        statusLabel.setText(" ");
    }

    /**
     * Ajoute un nouveau client
     */
    private void ajouterClient(ActionEvent e) {
        if (validateForm()) {
            Client nouveauClient = new Client();
            nouveauClient.setNom(champNom.getText().trim());
            nouveauClient.setPrenom(champPrenom.getText().trim());
            nouveauClient.setTelephone(champTelephone.getText().trim());
            nouveauClient.setEmail(champEmail.getText().trim());
            nouveauClient.setAdresse(champAdresse.getText().trim());

            Client clientAjoute = controller.ajouterClient(nouveauClient);
            if (clientAjoute != null) {
                // Utiliser la barre de statut pour informer l'utilisateur
                statusLabel.setText("✓ Client ajouté avec succès : " + clientAjoute.getNomComplet());
                statusLabel.setForeground(new Color(46, 204, 113)); // Vert pour le succès

                // Afficher également un dialogue de confirmation
                DialogUtils.afficherInformation(this,
                        "Client ajouté avec succès",
                        "Succès");

                reinitialiserFormulaire();
                chargerClients();
            } else {
                // Message d'erreur dans la barre de statut
                statusLabel.setText("⚠ Erreur lors de l'ajout du client. Veuillez réessayer.");
                statusLabel.setForeground(new Color(231, 76, 60)); // Rouge pour l'erreur

                DialogUtils.afficherErreur(this,
                        "Erreur lors de l'ajout du client",
                        "Erreur");
            }
        }
    }

    /**
     * Modifie un client existant
     */
    private void modifierClient(ActionEvent e) {
        if (clientSelectionne != null && validateForm()) {
            clientSelectionne.setNom(champNom.getText().trim());
            clientSelectionne.setPrenom(champPrenom.getText().trim());
            clientSelectionne.setTelephone(champTelephone.getText().trim());
            clientSelectionne.setEmail(champEmail.getText().trim());
            clientSelectionne.setAdresse(champAdresse.getText().trim());

            boolean success = controller.mettreAJourClient(clientSelectionne);
            if (success) {
                // Utiliser la barre de statut pour informer l'utilisateur
                statusLabel.setText("✓ Client modifié avec succès : " + clientSelectionne.getNomComplet());
                statusLabel.setForeground(new Color(46, 204, 113)); // Vert pour le succès

                DialogUtils.afficherInformation(this,
                        "Client modifié avec succès",
                        "Succès");

                reinitialiserFormulaire();
                chargerClients();
            } else {
                // Message d'erreur dans la barre de statut
                statusLabel.setText("⚠ Erreur lors de la modification du client. Veuillez réessayer.");
                statusLabel.setForeground(new Color(231, 76, 60)); // Rouge pour l'erreur

                DialogUtils.afficherErreur(this,
                        "Erreur lors de la modification du client",
                        "Erreur");
            }
        }
    }

    /**
     * Supprime un client
     */
    private void supprimerClient(ActionEvent e) {
        if (clientSelectionne != null) {
            int confirmation = DialogUtils.afficherConfirmation(this,
                    "Êtes-vous sûr de vouloir supprimer ce client ?",
                    "Confirmation de suppression");

            if (confirmation == JOptionPane.YES_OPTION) {
                boolean success = controller.supprimerClient(clientSelectionne.getId());
                if (success) {
                    // Utiliser la barre de statut pour informer l'utilisateur
                    statusLabel.setText("✓ Client supprimé avec succès : " + clientSelectionne.getNomComplet());
                    statusLabel.setForeground(new Color(46, 204, 113)); // Vert pour le succès

                    DialogUtils.afficherInformation(this,
                            "Client supprimé avec succès",
                            "Succès");

                    reinitialiserFormulaire();
                    chargerClients();
                } else {
                    // Message d'erreur dans la barre de statut
                    statusLabel.setText("⚠ Erreur lors de la suppression du client. Veuillez réessayer.");
                    statusLabel.setForeground(new Color(231, 76, 60)); // Rouge pour l'erreur

                    DialogUtils.afficherErreur(this,
                            "Erreur lors de la suppression du client",
                            "Erreur");
                }
            }
        }
    }

    /**
     * Valide le formulaire
     */
    private boolean validateForm() {
        if (champNom.getText().trim().isEmpty()) {
            DialogUtils.afficherErreur(this,
                    "Le nom est obligatoire",
                    "Erreur de validation");
            return false;
        }

        if (champPrenom.getText().trim().isEmpty()) {
            DialogUtils.afficherErreur(this,
                    "Le prénom est obligatoire",
                    "Erreur de validation");
            return false;
        }

        if (champTelephone.getText().trim().isEmpty()) {
            DialogUtils.afficherErreur(this,
                    "Le téléphone est obligatoire",
                    "Erreur de validation");
            return false;
        }

        return true;
    }

    /**
     * Méthode pour obtenir un client à partir de la liste
     * Utile pour la sélection d'un client lors d'une vente
     */
    public Client obtenirClientSelectionne() {
        return clientSelectionne;
    }

    /**
     * Classe pour rendre les boutons dans la table
     */
    class ButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {

        public ButtonRenderer() {
            setOpaque(true);
            setBackground(new Color(41, 128, 185)); // Bleu clair
            setForeground(Color.WHITE);
            setBorderPainted(true);
            setFocusPainted(false);

            // Bord arrondi pour un look moderne
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(31, 97, 141), 1),
                    BorderFactory.createEmptyBorder(6, 10, 6, 10)));

            setFont(new Font("SansSerif", Font.BOLD, 12));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {

            setText(value != null ? value.toString() : "");

            // Améliorer l'apparence lors de la sélection
            if (isSelected) {
                setBackground(new Color(52, 152, 219)); // Bleu plus clair pour la sélection
            } else {
                setBackground(new Color(41, 128, 185)); // Bleu normal
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
            button.setBackground(new Color(41, 128, 185));
            button.setForeground(Color.WHITE);
            button.setBorderPainted(true);
            button.setFocusPainted(false);

            // Bord arrondi pour un look moderne
            button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(31, 97, 141), 1),
                    BorderFactory.createEmptyBorder(6, 10, 6, 10)));

            button.setFont(new Font("SansSerif", Font.BOLD, 12));

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
                // Sélectionner le client et charger ses données dans le formulaire
                selectionnerClient(id);
                // Focus sur le champ nom pour faciliter la modification
                champNom.requestFocus();
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
}