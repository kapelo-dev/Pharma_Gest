package com.pharmajava.view;

import com.pharmajava.model.PharmacieInfo;
import com.pharmajava.controller.ParametresController;
import com.pharmajava.utils.SessionManager;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Interface de gestion des paramètres de la pharmacie
 */
public class ParametresView extends JPanel {
    private static final Logger LOGGER = Logger.getLogger(ParametresView.class.getName());
    
    private final ParametresController controller;
    
    // Composants de l'interface
    private JTextField champNom;
    private JTextArea champAdresse;
    private JTextField champTelephone1;
    private JTextField champTelephone2;
    private JTextField champEmail;
    private JTextField champLogo;
    private JButton boutonParcourir;
    private JButton boutonEnregistrer;
    private JButton boutonAnnuler;
    
    private File logoFile;
    private PharmacieInfo pharmacieInfo;
    
    /**
     * Constructeur de la vue des paramètres
     */
    public ParametresView() {
        this.controller = new ParametresController();
        
        initialiserComposants();
        initialiserEvenements();
        chargerParametres();
    }
    
    /**
     * Initialise les composants de l'interface
     */
    private void initialiserComposants() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Titre
        JLabel titreLabel = new JLabel("Paramètres de la Pharmacie");
        titreLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titreLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        add(titreLabel, BorderLayout.NORTH);
        
        // Panneau principal
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        
        // Formulaire
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Informations de la pharmacie"),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        
        // Nom de la pharmacie
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Nom de la pharmacie:"), gbc);
        
        champNom = new JTextField(30);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.gridwidth = 2;
        formPanel.add(champNom, gbc);
        
        // Adresse
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        gbc.gridwidth = 1;
        formPanel.add(new JLabel("Adresse:"), gbc);
        
        champAdresse = new JTextArea(4, 30);
        champAdresse.setLineWrap(true);
        champAdresse.setWrapStyleWord(true);
        JScrollPane scrollAdresse = new JScrollPane(champAdresse);
        
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.gridwidth = 2;
        gbc.gridheight = 2;
        formPanel.add(scrollAdresse, gbc);
        
        // Téléphone 1
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        formPanel.add(new JLabel("Téléphone principal:"), gbc);
        
        champTelephone1 = new JTextField(15);
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        formPanel.add(champTelephone1, gbc);
        
        // Téléphone 2
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Téléphone secondaire:"), gbc);
        
        champTelephone2 = new JTextField(15);
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.weightx = 1.0;
        formPanel.add(champTelephone2, gbc);
        
        // Email
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Email:"), gbc);
        
        champEmail = new JTextField(25);
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.weightx = 1.0;
        formPanel.add(champEmail, gbc);
        
        // Logo
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Logo:"), gbc);
        
        JPanel logoPanel = new JPanel(new BorderLayout(5, 0));
        champLogo = new JTextField(25);
        champLogo.setEditable(false);
        boutonParcourir = new JButton("Parcourir...");
        
        logoPanel.add(champLogo, BorderLayout.CENTER);
        logoPanel.add(boutonParcourir, BorderLayout.EAST);
        
        gbc.gridx = 1;
        gbc.gridy = 6;
        gbc.weightx = 1.0;
        formPanel.add(logoPanel, gbc);
        
        mainPanel.add(formPanel, BorderLayout.CENTER);
        
        // Panneau des boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        boutonEnregistrer = new JButton("Enregistrer");
        boutonEnregistrer.setBackground(new Color(46, 204, 113));
        boutonEnregistrer.setForeground(Color.WHITE);
        
        boutonAnnuler = new JButton("Annuler");
        
        buttonPanel.add(boutonAnnuler);
        buttonPanel.add(boutonEnregistrer);
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel, BorderLayout.CENTER);
    }
    
    /**
     * Initialise les événements
     */
    private void initialiserEvenements() {
        // Sélection du logo
        boutonParcourir.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Sélectionner un logo");
            fileChooser.setFileFilter(new FileNameExtensionFilter("Images", "jpg", "jpeg", "png", "gif"));
            
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                logoFile = fileChooser.getSelectedFile();
                champLogo.setText(logoFile.getAbsolutePath());
            }
        });
        
        // Enregistrement des paramètres
        boutonEnregistrer.addActionListener(e -> {
            if (validerFormulaire()) {
                PharmacieInfo info = extraireFormulaire();
                
                boolean succes = controller.enregistrerParametres(info, logoFile);
                if (succes) {
                    JOptionPane.showMessageDialog(this,
                        "Paramètres enregistrés avec succès.",
                        "Succès",
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    // Recharger les paramètres pour afficher les changements
                    chargerParametres();
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Erreur lors de l'enregistrement des paramètres.",
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        // Annulation des modifications
        boutonAnnuler.addActionListener(e -> chargerParametres());
    }
    
    /**
     * Charge les paramètres de la pharmacie
     */
    private void chargerParametres() {
        try {
            pharmacieInfo = controller.obtenirParametres();
            
            if (pharmacieInfo != null) {
                champNom.setText(pharmacieInfo.getNom());
                champAdresse.setText(pharmacieInfo.getAdresse());
                champTelephone1.setText(pharmacieInfo.getTelephone1());
                champTelephone2.setText(pharmacieInfo.getTelephone2() != null ? pharmacieInfo.getTelephone2() : "");
                champEmail.setText(pharmacieInfo.getEmail() != null ? pharmacieInfo.getEmail() : "");
                champLogo.setText(pharmacieInfo.getLogoPath() != null ? pharmacieInfo.getLogoPath() : "");
                
                logoFile = null;
            } else {
                // Paramètres par défaut si aucun n'est trouvé
                champNom.setText("Ma Pharmacie");
                champAdresse.setText("");
                champTelephone1.setText("");
                champTelephone2.setText("");
                champEmail.setText("");
                champLogo.setText("");
                
                logoFile = null;
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors du chargement des paramètres", e);
            JOptionPane.showMessageDialog(this,
                "Erreur lors du chargement des paramètres.",
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Extrait les données du formulaire
     */
    private PharmacieInfo extraireFormulaire() {
        String nom = champNom.getText().trim();
        String adresse = champAdresse.getText().trim();
        String telephone1 = champTelephone1.getText().trim();
        String telephone2 = champTelephone2.getText().trim();
        String email = champEmail.getText().trim();
        
        PharmacieInfo info = new PharmacieInfo();
        
        if (pharmacieInfo != null && pharmacieInfo.getId() != null) {
            info.setId(pharmacieInfo.getId());
        }
        
        info.setNom(nom);
        info.setAdresse(adresse);
        info.setTelephone1(telephone1);
        
        // Champs optionnels
        if (!telephone2.isEmpty()) {
            info.setTelephone2(telephone2);
        }
        
        if (!email.isEmpty()) {
            info.setEmail(email);
        }
        
        // Conserver le chemin du logo existant si aucun nouveau logo n'est sélectionné
        if (logoFile == null && pharmacieInfo != null) {
            info.setLogoPath(pharmacieInfo.getLogoPath());
        }
        
        return info;
    }
    
    /**
     * Valide les données du formulaire
     */
    private boolean validerFormulaire() {
        String nom = champNom.getText().trim();
        if (nom.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Le nom de la pharmacie est obligatoire.",
                "Erreur de validation",
                JOptionPane.WARNING_MESSAGE);
            champNom.requestFocus();
            return false;
        }
        
        String adresse = champAdresse.getText().trim();
        if (adresse.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "L'adresse de la pharmacie est obligatoire.",
                "Erreur de validation",
                JOptionPane.WARNING_MESSAGE);
            champAdresse.requestFocus();
            return false;
        }
        
        String telephone1 = champTelephone1.getText().trim();
        if (telephone1.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Le numéro de téléphone principal est obligatoire.",
                "Erreur de validation",
                JOptionPane.WARNING_MESSAGE);
            champTelephone1.requestFocus();
            return false;
        }
        
        return true;
    }
} 