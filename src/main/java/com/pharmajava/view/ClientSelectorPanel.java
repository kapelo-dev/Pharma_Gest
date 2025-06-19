package com.pharmajava.view;

import com.pharmajava.controller.ClientController;
import com.pharmajava.model.Client;
import com.pharmajava.utils.AutoCompletionTextField;
import com.pharmajava.utils.DialogUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Panneau de sélection de client réutilisable
 */
public class ClientSelectorPanel extends JPanel {
    private final ClientController controller;
    private Client clientSelectionne;
    private AutoCompletionTextField<Client> champRechercheClient;
    private JLabel labelClientSelectionne;
    private JButton boutonEffacer;
    private JButton boutonNouveauClient;
    private Consumer<Client> onClientSelectedCallback;
    private static final Logger LOGGER = Logger.getLogger(ClientSelectorPanel.class.getName());

    /**
     * Constructeur
     */
    public ClientSelectorPanel() {
        controller = new ClientController();
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Client (optionnel)"),
                new EmptyBorder(5, 5, 5, 5)));

        initialiserComposants();
    }

    /**
     * Initialise les composants
     */
    private void initialiserComposants() {
        // Panneau de recherche
        JPanel searchPanel = new JPanel(new BorderLayout(5, 0));

        // Configuration des couleurs
        Color primaryColor = new Color(41, 128, 185);
        Color accentColor = new Color(52, 152, 219);
        Color textColor = new Color(44, 62, 80);
        Color placeholderColor = new Color(149, 165, 166);
        Color borderColor = new Color(189, 195, 199);
        Color hoverColor = new Color(236, 240, 241);

        // Création du champ de recherche auto-complété avec tous les paramètres
        champRechercheClient = new AutoCompletionTextField<>(
                "Rechercher un client...", // Placeholder text
                client -> client.getNomComplet(), // Text extractor (ce qui s'affiche dans le champ)
                client -> "Tél: " + client.getTelephone(), // Details extractor (infos supplémentaires dans la liste)
                this::selectionnerClient // Action quand un élément est sélectionné
        );

        // Configurer la source de données (notre fonction de recherche)
        champRechercheClient.getTextField().getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            private javax.swing.Timer timer;

            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                resetTimer();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                resetTimer();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                resetTimer();
            }

            private void resetTimer() {
                if (timer != null && timer.isRunning()) {
                    timer.stop();
                }
                // Créer un nouveau timer qui attend 300ms avant d'exécuter la recherche
                // Cela évite les multiples recherches lors de la frappe rapide
                timer = new javax.swing.Timer(300, evt -> updateSuggestions());
                timer.setRepeats(false);
                timer.start();
            }

            private void updateSuggestions() {
                try {
                    String text = champRechercheClient.getText().trim();
                    List<Client> clients = rechercherClients(text);

                    // Si aucun résultat et texte non vide, essayer une recherche plus large
                    if (clients.isEmpty() && text.length() > 0) {
                        clients = controller.rechercherClients("%" + text + "%");
                    }

                    // Mettre à jour la liste des suggestions
                    champRechercheClient.setItems(clients);

                    // Forcer l'affichage du popup si on a des résultats
                    if (!clients.isEmpty() && champRechercheClient.getTextField().hasFocus()) {
                        // Cette partie dépend de l'implémentation exacte d'AutoCompletionTextField
                        // Si la classe a une méthode pour forcer l'affichage, l'utiliser ici
                    }
                } catch (Exception ex) {
                    LOGGER.log(Level.WARNING, "Erreur lors de la mise à jour des suggestions", ex);
                }
            }
        });

        // Configuration additionnelle
        champRechercheClient.setMaxResults(10); // Montrer plus de résultats
        champRechercheClient.setShowPopupOnFocus(true); // Afficher les suggestions au focus
        champRechercheClient.setColors(
                Color.WHITE, // fond
                textColor, // texte
                placeholderColor, // placeholder
                borderColor, // bordure
                hoverColor, // survol
                accentColor // sélection
        );

        champRechercheClient.getTextField().setColumns(20);
        champRechercheClient.getTextField().setToolTipText("Rechercher un client par nom, prénom ou téléphone");

        // Bouton pour créer un nouveau client
        boutonNouveauClient = new JButton("Nouveau");
        boutonNouveauClient.addActionListener(e -> montrerDialogueNouveauClient());

        searchPanel.add(champRechercheClient, BorderLayout.CENTER);
        searchPanel.add(boutonNouveauClient, BorderLayout.EAST);

        // Panneau d'affichage du client sélectionné
        JPanel selectedPanel = new JPanel(new BorderLayout(5, 0));

        labelClientSelectionne = new JLabel("Aucun client sélectionné");
        labelClientSelectionne.setForeground(Color.GRAY);

        boutonEffacer = new JButton("×");
        boutonEffacer.setToolTipText("Effacer le client sélectionné");
        boutonEffacer.setPreferredSize(new Dimension(25, 25));
        boutonEffacer.addActionListener(e -> effacerSelection());
        boutonEffacer.setVisible(false);

        selectedPanel.add(labelClientSelectionne, BorderLayout.CENTER);
        selectedPanel.add(boutonEffacer, BorderLayout.EAST);

        // Assembler le panneau
        add(searchPanel, BorderLayout.NORTH);
        add(selectedPanel, BorderLayout.CENTER);

        // Charger les clients initiaux pour le focus
        champRechercheClient.setItems(controller.obtenirTousLesClients().stream()
                .limit(10)
                .collect(java.util.stream.Collectors.toList()));
    }

    /**
     * Recherche des clients par terme
     * Cette méthode est utilisée par l'auto-complétion pour afficher les
     * suggestions
     * 
     * @param terme Le terme de recherche saisi par l'utilisateur
     * @return Une liste de clients correspondant à la recherche
     */
    private List<Client> rechercherClients(String terme) {
        // Utiliser la méthode spécifique du contrôleur pour l'auto-complétion
        List<Client> resultats = controller.rechercherClientsAutoCompletion(terme);

        // Assurer qu'on a toujours au moins quelques suggestions
        if (resultats.isEmpty() && (terme == null || terme.trim().isEmpty())) {
            return controller.obtenirTousLesClients().stream()
                    .limit(10)
                    .collect(java.util.stream.Collectors.toList());
        }

        return resultats;
    }

    /**
     * Sélectionne un client et met à jour l'interface
     */
    private void selectionnerClient(Client client) {
        this.clientSelectionne = client;

        if (client != null) {
            labelClientSelectionne.setText(client.getNomComplet() + " (" + client.getTelephone() + ")");
            labelClientSelectionne.setForeground(Color.BLACK);
            boutonEffacer.setVisible(true);
            champRechercheClient.setText("");

            // Notifier le callback si présent
            if (onClientSelectedCallback != null) {
                onClientSelectedCallback.accept(client);
            }
        } else {
            effacerSelection();
        }
    }

    /**
     * Efface la sélection actuelle
     */
    private void effacerSelection() {
        clientSelectionne = null;
        labelClientSelectionne.setText("Aucun client sélectionné");
        labelClientSelectionne.setForeground(Color.GRAY);
        boutonEffacer.setVisible(false);
        champRechercheClient.setText("");

        // Notifier le callback si présent
        if (onClientSelectedCallback != null) {
            onClientSelectedCallback.accept(null);
        }
    }

    /**
     * Affiche un dialogue pour créer un nouveau client
     */
    private void montrerDialogueNouveauClient() {
        // Créer un panneau de formulaire pour le nouveau client
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Nom:"), gbc);

        gbc.gridx = 1;
        JTextField champNom = new JTextField(15);
        panel.add(champNom, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Prénom:"), gbc);

        gbc.gridx = 1;
        JTextField champPrenom = new JTextField(15);
        panel.add(champPrenom, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Téléphone:"), gbc);

        gbc.gridx = 1;
        JTextField champTelephone = new JTextField(15);
        panel.add(champTelephone, gbc);

        // Montrer le dialogue avec options personnalisées en français
        Object[] options = { "Ajouter", "Annuler" };
        int result = JOptionPane.showOptionDialog(
                this,
                panel,
                "Ajouter un nouveau client",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                options[0]);

        // Traiter le résultat
        if (result == JOptionPane.OK_OPTION) {
            String nom = champNom.getText().trim();
            String prenom = champPrenom.getText().trim();
            String telephone = champTelephone.getText().trim();

            // Vérifier les champs obligatoires
            if (nom.isEmpty() || prenom.isEmpty() || telephone.isEmpty()) {
                DialogUtils.afficherErreur(this,
                        "Tous les champs sont obligatoires",
                        "Erreur");
                return;
            }

            // Créer et ajouter le client
            Client nouveauClient = new Client(nom, prenom, telephone);
            Client clientAjoute = controller.ajouterClient(nouveauClient);

            if (clientAjoute != null) {
                selectionnerClient(clientAjoute);
                DialogUtils.afficherInformation(this,
                        "Client ajouté avec succès",
                        "Succès");
            } else {
                DialogUtils.afficherErreur(this,
                        "Erreur lors de l'ajout du client",
                        "Erreur");
            }
        }
    }

    /**
     * Définit un callback pour la sélection de client
     */
    public void setOnClientSelectedCallback(Consumer<Client> callback) {
        this.onClientSelectedCallback = callback;
    }

    /**
     * Retourne le client actuellement sélectionné
     */
    public Client getClientSelectionne() {
        return clientSelectionne;
    }

    /**
     * Définit le client sélectionné programmatiquement
     */
    public void setClientSelectionne(Client client) {
        selectionnerClient(client);
    }
}