package com.pharmajava.view;

import com.pharmajava.controller.LoginController;
import com.pharmajava.model.Pharmacien;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.geom.RoundRectangle2D;

/**
 * Interface de connexion à l'application
 */
public class LoginView extends JFrame {
    private final LoginController controller;

    // Couleurs thème
    private final Color PRIMARY_COLOR = new Color(41, 128, 185); // Bleu professionnel
    private final Color SECONDARY_COLOR = new Color(52, 152, 219); // Bleu plus clair
    private final Color ACCENT_COLOR = new Color(46, 204, 113); // Vert pour bouton
    private final Color TEXT_COLOR = new Color(44, 62, 80); // Texte foncé
    private final Color LIGHT_COLOR = new Color(236, 240, 241); // Fond clair
    private final Color ERROR_COLOR = new Color(231, 76, 60); // Rouge pour erreurs

    private JTextField identifiantField;
    private JPasswordField motDePasseField;
    private JButton connecterButton;
    private JLabel statusLabel;

    /**
     * Constructeur de la vue de connexion
     */
    public LoginView() {
        controller = new LoginController();

        initialiserComposants();
        initialiserEvenements();

        // Configuration de la fenêtre
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Connexion - PharmaGest");
        // Utiliser la pleine taille de l'écran
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        // Garder ces options pour assurer la compatibilité
        setMinimumSize(new Dimension(800, 600));
        setLocationRelativeTo(null);
        setUndecorated(false); // Garder les bordures Windows standard

        // Fond global
        getContentPane().setBackground(LIGHT_COLOR);
    }

    /**
     * Initialise les composants de l'interface
     */
    private void initialiserComposants() {
        // Création du panneau principal avec BorderLayout pour le centrage
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(LIGHT_COLOR);

        // Panneau central avec BoxLayout vertical
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(LIGHT_COLOR);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));
        centerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.setMaximumSize(new Dimension(550, 680));

        // Panel pour le logo et le titre
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(LIGHT_COLOR);
        headerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Logo
        JLabel logoLabel;
        try {
            java.net.URL logoUrl = getClass().getResource("/images/logo_pharmacie.png");
            if (logoUrl != null) {
                ImageIcon originalIcon = new ImageIcon(logoUrl);
                Image scaledImage = originalIcon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
                logoLabel = new JLabel(new ImageIcon(scaledImage));
            } else {
                // Logo textuel stylisé
                logoLabel = createTextLogo();
            }
        } catch (Exception e) {
            // Logo textuel stylisé
            logoLabel = createTextLogo();
        }
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerPanel.add(logoLabel);
        headerPanel.add(Box.createVerticalStrut(20));

        // Titre principal
        JLabel titleLabel = new JLabel("PharmaGest");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerPanel.add(titleLabel);

        // Sous-titre
        JLabel subtitleLabel = new JLabel("Système de Gestion de Pharmacie");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitleLabel.setForeground(TEXT_COLOR);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerPanel.add(subtitleLabel);

        headerPanel.add(Box.createVerticalStrut(60));
        centerPanel.add(headerPanel);

        // Panneau de formulaire
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(LIGHT_COLOR);
        formPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Label de bienvenue
        JLabel welcomeLabel = new JLabel("Bienvenue");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        welcomeLabel.setForeground(TEXT_COLOR);
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.add(welcomeLabel);

        JLabel instructionLabel = new JLabel("Veuillez vous connecter pour continuer");
        instructionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        instructionLabel.setForeground(TEXT_COLOR.brighter());
        instructionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.add(instructionLabel);
        formPanel.add(Box.createVerticalStrut(40));

        // Champ d'identifiant
        JLabel identifiantLabel = new JLabel("Identifiant");
        identifiantLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        identifiantLabel.setForeground(TEXT_COLOR);
        identifiantLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.add(identifiantLabel);
        formPanel.add(Box.createVerticalStrut(5));

        identifiantField = createStyledTextField("Entrez votre identifiant");
        identifiantField.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.add(identifiantField);
        formPanel.add(Box.createVerticalStrut(25));

        // Champ de mot de passe
        JLabel passwordLabel = new JLabel("Mot de passe");
        passwordLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        passwordLabel.setForeground(TEXT_COLOR);
        passwordLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.add(passwordLabel);
        formPanel.add(Box.createVerticalStrut(5));

        motDePasseField = createStyledPasswordField();
        motDePasseField.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.add(motDePasseField);
        formPanel.add(Box.createVerticalStrut(5));

        // Label d'erreur
        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        statusLabel.setForeground(ERROR_COLOR);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.add(statusLabel);
        formPanel.add(Box.createVerticalStrut(15));

        // Bouton de connexion
        connecterButton = createStyledButton("Se connecter");
        connecterButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Créer un panel pour contenir le bouton pour garantir qu'il s'affiche
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(LIGHT_COLOR);
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttonPanel.setPreferredSize(new Dimension(430, 50));
        buttonPanel.setMaximumSize(new Dimension(430, 50));
        buttonPanel.add(connecterButton);

        formPanel.add(buttonPanel);

        centerPanel.add(formPanel);
        centerPanel.add(Box.createVerticalGlue());

        // Panneau de pied de page
        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(LIGHT_COLOR);
        footerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel versionLabel = new JLabel("Version 1.0 • © 2025 PharmaGest");
        versionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        versionLabel.setForeground(TEXT_COLOR.brighter());
        footerPanel.add(versionLabel);

        centerPanel.add(footerPanel);

        // Ajouter le panneau central au panneau principal
        JPanel wrapperPanel = new JPanel();
        wrapperPanel.setLayout(new BoxLayout(wrapperPanel, BoxLayout.Y_AXIS));
        wrapperPanel.setBackground(LIGHT_COLOR);
        wrapperPanel.add(Box.createVerticalGlue());
        wrapperPanel.add(centerPanel);
        wrapperPanel.add(Box.createVerticalGlue());

        mainPanel.add(wrapperPanel, BorderLayout.CENTER);

        // Ajout du panneau principal à la fenêtre
        setContentPane(mainPanel);
    }

    /**
     * Crée un logo textuel stylisé
     */
    private JLabel createTextLogo() {
        JLabel logo = new JLabel("P");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 72));
        logo.setForeground(Color.WHITE);
        logo.setBackground(PRIMARY_COLOR);
        logo.setOpaque(true);
        logo.setHorizontalAlignment(SwingConstants.CENTER);
        logo.setPreferredSize(new Dimension(120, 120));
        logo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        return logo;
    }

    /**
     * Crée un champ de texte stylisé
     */
    private JTextField createStyledTextField(String placeholder) {
        JTextField field = new JTextField(20);
        field.setPreferredSize(new Dimension(300, 40));
        field.setMaximumSize(new Dimension(300, 40));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        field.setForeground(TEXT_COLOR);
        field.setBorder(new CompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1, true),
                new EmptyBorder(6, 10, 6, 10)));
        field.setText(placeholder);
        field.setForeground(Color.GRAY);

        field.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(TEXT_COLOR);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(Color.GRAY);
                }
            }
        });

        return field;
    }

    /**
     * Crée un champ de mot de passe stylisé
     */
    private JPasswordField createStyledPasswordField() {
        JPasswordField field = new JPasswordField(20);
        field.setPreferredSize(new Dimension(300, 40));
        field.setMaximumSize(new Dimension(300, 40));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        field.setForeground(TEXT_COLOR);
        field.setBorder(new CompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1, true),
                new EmptyBorder(6, 10, 6, 10)));
        return field;
    }

    /**
     * Crée un bouton stylisé
     */
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setForeground(Color.WHITE);
        button.setBackground(ACCENT_COLOR);
        button.setPreferredSize(new Dimension(250, 40));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return button;
    }

    /**
     * Initialise les événements
     */
    private void initialiserEvenements() {
        connecterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final String identifiantOriginal = identifiantField.getText();
                final String identifiant;
                if (identifiantOriginal.equals("Entrez votre identifiant")) {
                    identifiant = "";
                } else {
                    identifiant = identifiantOriginal;
                }

                final String motDePasse = new String(motDePasseField.getPassword());

                if (identifiant.isEmpty() || motDePasse.isEmpty()) {
                    statusLabel.setText("Veuillez remplir tous les champs");
                    return;
                }

                // Désactiver le bouton et montrer le chargement
                connecterButton.setEnabled(false);
                connecterButton.setText("Connexion en cours...");

                // Simuler le délai de connexion pour une meilleure UX
                Timer timer = new Timer(800, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        authentifierUtilisateur(identifiant, motDePasse);
                    }
                });
                timer.setRepeats(false);
                timer.start();
            }
        });

        // Ajouter l'action "Enter" sur le champ de mot de passe
        motDePasseField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                connecterButton.doClick();
            }
        });
    }

    /**
     * Authentifie l'utilisateur et gère les résultats
     */
    private void authentifierUtilisateur(String identifiant, String motDePasse) {
        Pharmacien pharmacien = controller.authentifier(identifiant, motDePasse);

        if (pharmacien != null) {
            // Authentification réussie
            ouvrirDashboard(pharmacien);
        } else {
            // Échec d'authentification
            statusLabel.setText("Identifiant ou mot de passe incorrect");
            motDePasseField.setText("");

            // Réactiver le bouton
            connecterButton.setEnabled(true);
            connecterButton.setText("Se connecter");

            // Animer label d'erreur
            animerLabelErreur();
        }
    }

    /**
     * Anime le label d'erreur pour attirer l'attention
     */
    private void animerLabelErreur() {
        final int[] position = { 0 };
        final int amplitude = 5;
        final int steps = 10;
        final int delay = 30;

        Timer timer = new Timer(delay, null);
        timer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (position[0] >= steps) {
                    timer.stop();
                    statusLabel.setBounds(statusLabel.getX(), statusLabel.getY(),
                            statusLabel.getWidth(), statusLabel.getHeight());
                    return;
                }

                double offset = amplitude * Math.sin(Math.PI * position[0] / (steps / 2));
                statusLabel.setBounds((int) (statusLabel.getX() + offset), statusLabel.getY(),
                        statusLabel.getWidth(), statusLabel.getHeight());
                position[0]++;
            }
        });
        timer.start();
    }

    /**
     * Ouvre le tableau de bord après une authentification réussie
     * 
     * @param pharmacien Le pharmacien authentifié
     */
    private void ouvrirDashboard(Pharmacien pharmacien) {
        dispose(); // Fermer la fenêtre de connexion

        DashboardView dashboard = new DashboardView(pharmacien);
        dashboard.setVisible(true);
    }
}