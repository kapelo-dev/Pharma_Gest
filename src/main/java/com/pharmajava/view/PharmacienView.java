package com.pharmajava.view;

import com.pharmajava.model.Pharmacien;
import com.pharmajava.controller.PharmacienController;
import com.pharmajava.utils.SessionManager;
import com.pharmajava.utils.TableModelUtil;
import com.pharmajava.utils.DialogUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.logging.Logger;

/**
 * Interface de gestion des pharmaciens
 */
public class PharmacienView extends JPanel {
    private static final Logger LOGGER = Logger.getLogger(PharmacienView.class.getName());

    private final PharmacienController controller;

    // Composants de l'interface
    private JTable tablePharmaciens;
    private DefaultTableModel tableModel;

    private JTextField champNom;
    private JTextField champPrenom;
    private JTextField champIdentifiant;
    private JTextField champTelephone;
    private JPasswordField champMotDePasse;
    private JPasswordField champConfirmMotDePasse;
    private JComboBox<String> comboRole;

    private JButton boutonAjouter;
    private JButton boutonModifier;
    private JButton boutonSupprimer;
    private JButton boutonEffacer;

    private Pharmacien pharmacienSelectionne;

    /**
     * Constructeur de la vue de gestion des pharmaciens
     */
    public PharmacienView() {
        this.controller = new PharmacienController();
        this.pharmacienSelectionne = null;

        initialiserComposants();
        initialiserEvenements();
        chargerPharmaciens();
    }

    /**
     * Initialise les composants de l'interface
     */
    private void initialiserComposants() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Titre
        JLabel titreLabel = new JLabel("Gestion des Pharmaciens");
        titreLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titreLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        add(titreLabel, BorderLayout.NORTH);

        // Panneau principal divisé en deux
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(0.6);
        splitPane.setResizeWeight(0.6);

        // Panneau gauche: Liste des pharmaciens
        JPanel leftPanel = new JPanel(new BorderLayout());

        // Table des pharmaciens
        String[] entetes = { "ID", "Nom", "Prénom", "Identifiant", "Téléphone", "Rôle" };
        tableModel = new DefaultTableModel(entetes, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablePharmaciens = new JTable(tableModel);
        tablePharmaciens.getTableHeader().setReorderingAllowed(false);
        tablePharmaciens.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Ajustement des largeurs des colonnes
        tablePharmaciens.getColumnModel().getColumn(0).setPreferredWidth(40); // ID
        tablePharmaciens.getColumnModel().getColumn(1).setPreferredWidth(100); // Nom
        tablePharmaciens.getColumnModel().getColumn(2).setPreferredWidth(100); // Prénom
        tablePharmaciens.getColumnModel().getColumn(3).setPreferredWidth(100); // Identifiant
        tablePharmaciens.getColumnModel().getColumn(4).setPreferredWidth(100); // Téléphone
        tablePharmaciens.getColumnModel().getColumn(5).setPreferredWidth(80); // Rôle

        JScrollPane scrollPane = new JScrollPane(tablePharmaciens);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Liste des pharmaciens"));

        leftPanel.add(scrollPane, BorderLayout.CENTER);

        // Panneau droit: Formulaire
        JPanel rightPanel = new JPanel(new BorderLayout());

        // Formulaire
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Détails du pharmacien"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Nom
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Nom:"), gbc);

        champNom = new JTextField(20);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        formPanel.add(champNom, gbc);

        // Prénom
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Prénom:"), gbc);

        champPrenom = new JTextField(20);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        formPanel.add(champPrenom, gbc);

        // Identifiant
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Identifiant:"), gbc);

        champIdentifiant = new JTextField(20);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        formPanel.add(champIdentifiant, gbc);

        // Téléphone
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Téléphone:"), gbc);

        champTelephone = new JTextField(20);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        formPanel.add(champTelephone, gbc);

        // Mot de passe
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Mot de passe:"), gbc);

        champMotDePasse = new JPasswordField(20);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        formPanel.add(champMotDePasse, gbc);

        // Confirmation mot de passe
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Confirmer:"), gbc);

        champConfirmMotDePasse = new JPasswordField(20);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        formPanel.add(champConfirmMotDePasse, gbc);

        // Rôle
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Rôle:"), gbc);

        comboRole = new JComboBox<>(new String[] { "ADMIN", "PHARMACIEN" });
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        formPanel.add(comboRole, gbc);

        rightPanel.add(formPanel, BorderLayout.CENTER);

        // Boutons d'action
        JPanel buttonPanel = new JPanel(new GridLayout(1, 4, 10, 0));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        boutonAjouter = new JButton("Ajouter");
        boutonAjouter.setBackground(new Color(46, 204, 113));
        boutonAjouter.setForeground(Color.WHITE);

        boutonModifier = new JButton("Modifier");
        boutonModifier.setEnabled(false);

        boutonSupprimer = new JButton("Supprimer");
        boutonSupprimer.setBackground(new Color(231, 76, 60));
        boutonSupprimer.setForeground(Color.WHITE);
        boutonSupprimer.setEnabled(false);

        boutonEffacer = new JButton("Effacer");

        buttonPanel.add(boutonAjouter);
        buttonPanel.add(boutonModifier);
        buttonPanel.add(boutonSupprimer);
        buttonPanel.add(boutonEffacer);

        rightPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Ajouter les panneaux au split pane
        splitPane.setLeftComponent(leftPanel);
        splitPane.setRightComponent(rightPanel);

        // Ajouter le split pane au panneau principal
        add(splitPane, BorderLayout.CENTER);
    }

    /**
     * Initialise les événements
     */
    private void initialiserEvenements() {
        // Sélection d'un pharmacien dans la table
        tablePharmaciens.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = tablePharmaciens.getSelectedRow();
                if (selectedRow != -1) {
                    int pharmacienId = (int) tableModel.getValueAt(selectedRow, 0);
                    pharmacienSelectionne = controller.obtenirParId(pharmacienId);
                    remplirFormulaire(pharmacienSelectionne);

                    // Activer les boutons de modification et suppression
                    boutonModifier.setEnabled(true);

                    // Ne pas permettre à un utilisateur de se supprimer lui-même
                    boolean estPharmacienConnecte = SessionManager.getInstance().getPharmacienConnecte().getId()
                            .equals(pharmacienSelectionne.getId());
                    boutonSupprimer.setEnabled(!estPharmacienConnecte);
                }
            }
        });

        // Ajout d'un pharmacien
        boutonAjouter.addActionListener(e -> {
            if (validerFormulaire()) {
                Pharmacien pharmacien = extraireFormulaire();

                // Vérifier si l'identifiant existe déjà
                if (controller.identifiantExiste(pharmacien.getIdentifiant())) {
                    JOptionPane.showMessageDialog(this,
                            "Cet identifiant existe déjà. Veuillez en choisir un autre.",
                            "Identifiant existant",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                boolean succes = controller.ajouter(pharmacien);
                if (succes) {
                    JOptionPane.showMessageDialog(this,
                            "Pharmacien ajouté avec succès.",
                            "Succès",
                            JOptionPane.INFORMATION_MESSAGE);

                    effacerFormulaire();
                    chargerPharmaciens();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Erreur lors de l'ajout du pharmacien.",
                            "Erreur",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Modification d'un pharmacien
        boutonModifier.addActionListener(e -> {
            if (pharmacienSelectionne == null) {
                JOptionPane.showMessageDialog(this,
                        "Veuillez sélectionner un pharmacien à modifier.",
                        "Aucune sélection",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (validerFormulaire()) {
                Pharmacien pharmacien = extraireFormulaire();
                pharmacien.setId(pharmacienSelectionne.getId());

                // Vérifier si l'identifiant existe déjà (pour un autre pharmacien)
                if (!pharmacien.getIdentifiant().equals(pharmacienSelectionne.getIdentifiant()) &&
                        controller.identifiantExiste(pharmacien.getIdentifiant())) {
                    JOptionPane.showMessageDialog(this,
                            "Cet identifiant existe déjà. Veuillez en choisir un autre.",
                            "Identifiant existant",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                boolean succes = controller.mettreAJour(pharmacien, champMotDePasse.getPassword().length > 0);
                if (succes) {
                    JOptionPane.showMessageDialog(this,
                            "Pharmacien modifié avec succès.",
                            "Succès",
                            JOptionPane.INFORMATION_MESSAGE);

                    effacerFormulaire();
                    boutonModifier.setEnabled(false);
                    boutonSupprimer.setEnabled(false);
                    pharmacienSelectionne = null;
                    chargerPharmaciens();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Erreur lors de la modification du pharmacien.",
                            "Erreur",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Suppression d'un pharmacien
        boutonSupprimer.addActionListener(e -> {
            if (pharmacienSelectionne == null) {
                JOptionPane.showMessageDialog(this,
                        "Veuillez sélectionner un pharmacien à supprimer.",
                        "Aucune sélection",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Confirmation de suppression
            int choix = DialogUtils.afficherConfirmation(this,
                    "Êtes-vous sûr de vouloir supprimer ce pharmacien ?",
                    "Confirmation de suppression",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (choix == JOptionPane.YES_OPTION) {
                boolean succes = controller.supprimer(pharmacienSelectionne.getId());
                if (succes) {
                    JOptionPane.showMessageDialog(this,
                            "Pharmacien supprimé avec succès.",
                            "Succès",
                            JOptionPane.INFORMATION_MESSAGE);

                    effacerFormulaire();
                    boutonModifier.setEnabled(false);
                    boutonSupprimer.setEnabled(false);
                    pharmacienSelectionne = null;
                    chargerPharmaciens();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Erreur lors de la suppression du pharmacien.",
                            "Erreur",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Effacement du formulaire
        boutonEffacer.addActionListener(e -> {
            effacerFormulaire();
            boutonModifier.setEnabled(false);
            boutonSupprimer.setEnabled(false);
            pharmacienSelectionne = null;
            tablePharmaciens.clearSelection();
        });
    }

    /**
     * Remplit le formulaire avec les données d'un pharmacien
     */
    private void remplirFormulaire(Pharmacien pharmacien) {
        champNom.setText(pharmacien.getNom());
        champPrenom.setText(pharmacien.getPrenom());
        champIdentifiant.setText(pharmacien.getIdentifiant());
        champTelephone.setText(pharmacien.getTelephone());
        champMotDePasse.setText("");
        champConfirmMotDePasse.setText("");
        comboRole.setSelectedItem(pharmacien.getRole());
    }

    /**
     * Efface tous les champs du formulaire
     */
    private void effacerFormulaire() {
        champNom.setText("");
        champPrenom.setText("");
        champIdentifiant.setText("");
        champTelephone.setText("");
        champMotDePasse.setText("");
        champConfirmMotDePasse.setText("");
        comboRole.setSelectedIndex(0);
    }

    /**
     * Extrait les données du formulaire pour créer un objet Pharmacien
     */
    private Pharmacien extraireFormulaire() {
        String nom = champNom.getText().trim();
        String prenom = champPrenom.getText().trim();
        String identifiant = champIdentifiant.getText().trim();
        String telephone = champTelephone.getText().trim();
        String motDePasse = new String(champMotDePasse.getPassword());
        String role = (String) comboRole.getSelectedItem();

        Pharmacien pharmacien = new Pharmacien();
        pharmacien.setNom(nom);
        pharmacien.setPrenom(prenom);
        pharmacien.setIdentifiant(identifiant);
        pharmacien.setTelephone(telephone);

        // Ne définir le mot de passe que s'il a été saisi
        if (motDePasse.length() > 0) {
            pharmacien.setMotDePasse(motDePasse);
        }

        pharmacien.setRole(role);

        return pharmacien;
    }

    /**
     * Valide les données du formulaire
     */
    private boolean validerFormulaire() {
        String nom = champNom.getText().trim();
        if (nom.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Le nom du pharmacien est obligatoire.",
                    "Erreur de validation",
                    JOptionPane.WARNING_MESSAGE);
            champNom.requestFocus();
            return false;
        }

        String prenom = champPrenom.getText().trim();
        if (prenom.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Le prénom du pharmacien est obligatoire.",
                    "Erreur de validation",
                    JOptionPane.WARNING_MESSAGE);
            champPrenom.requestFocus();
            return false;
        }

        String identifiant = champIdentifiant.getText().trim();
        if (identifiant.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "L'identifiant du pharmacien est obligatoire.",
                    "Erreur de validation",
                    JOptionPane.WARNING_MESSAGE);
            champIdentifiant.requestFocus();
            return false;
        }

        String telephone = champTelephone.getText().trim();
        if (telephone.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Le numéro de téléphone est obligatoire.",
                    "Erreur de validation",
                    JOptionPane.WARNING_MESSAGE);
            champTelephone.requestFocus();
            return false;
        }

        // Vérifier le mot de passe uniquement pour un nouvel utilisateur ou si modifié
        if (pharmacienSelectionne == null || champMotDePasse.getPassword().length > 0) {
            String motDePasse = new String(champMotDePasse.getPassword());
            String confirmMotDePasse = new String(champConfirmMotDePasse.getPassword());

            if (motDePasse.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Le mot de passe est obligatoire.",
                        "Erreur de validation",
                        JOptionPane.WARNING_MESSAGE);
                champMotDePasse.requestFocus();
                return false;
            }

            if (motDePasse.length() < 6) {
                JOptionPane.showMessageDialog(this,
                        "Le mot de passe doit contenir au moins 6 caractères.",
                        "Erreur de validation",
                        JOptionPane.WARNING_MESSAGE);
                champMotDePasse.requestFocus();
                return false;
            }

            if (!motDePasse.equals(confirmMotDePasse)) {
                JOptionPane.showMessageDialog(this,
                        "Les mots de passe ne correspondent pas.",
                        "Erreur de validation",
                        JOptionPane.WARNING_MESSAGE);
                champConfirmMotDePasse.requestFocus();
                return false;
            }
        }

        return true;
    }

    /**
     * Charge la liste des pharmaciens
     */
    private void chargerPharmaciens() {
        TableModelUtil.viderTable(tableModel);

        List<Pharmacien> pharmaciens = controller.obtenirTous();
        for (Pharmacien pharmacien : pharmaciens) {
            Object[] ligne = {
                    pharmacien.getId(),
                    pharmacien.getNom(),
                    pharmacien.getPrenom(),
                    pharmacien.getIdentifiant(),
                    pharmacien.getTelephone(),
                    pharmacien.getRole()
            };
            tableModel.addRow(ligne);
        }
    }
}