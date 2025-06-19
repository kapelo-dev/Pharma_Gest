package com.pharmajava.view;

import com.pharmajava.controller.DashboardController;
import com.pharmajava.model.Pharmacien;
import com.pharmajava.utils.DialogUtils;
import com.pharmajava.utils.IconUtils;
import com.pharmajava.utils.SessionManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.logging.Logger;

/**
 * MODIFICATIONS APPORTÉES POUR RÉSOUDRE LE PROBLÈME DE CONTRASTE DES MENUS:
 * 
 * 1. Dans la méthode createMenuButton:
 * - Changé setContentAreaFilled(false) à setContentAreaFilled(true) pour
 * assurer
 * que le fond des boutons est correctement rempli
 * 
 * 2. Dans la méthode activateButton:
 * - Ajouté une vérification supplémentaire pour s'assurer que tous les
 * composants
 * des boutons (surtout les labels de texte) gardent la bonne couleur de texte
 * avec un contraste suffisant
 * 
 * 3. Dans la méthode createQuickLinkButton:
 * - Modifié la couleur de fond pour améliorer le contraste (245,245,245)
 * - Changé également setContentAreaFilled(false) à setContentAreaFilled(true)
 * - Remplacé ACCENT_COLOR.brighter() par ACCENT_COLOR pour un meilleur
 * contraste
 * 
 * 4. Dans la méthode createAlertItem:
 * - Amélioré le contraste des bordures (230,230,230 au lieu de 240,240,240)
 * 
 * Interface principale du tableau de bord de l'application
 */
public class DashboardView extends JFrame {
    private static final Logger logger = Logger.getLogger(DashboardView.class.getName());
    private final DashboardController controller;
    private final Pharmacien pharmacienConnecte;

    // Définition des couleurs du thème
    private final Color PRIMARY_DARK = new Color(30, 41, 59); // Bleu foncé
    private final Color PRIMARY_MEDIUM = new Color(51, 65, 85); // Bleu moyen
    private final Color PRIMARY_LIGHT = new Color(71, 85, 105); // Bleu clair
    private final Color ACCENT_COLOR = new Color(14, 165, 233); // Bleu vif
    private final Color SUCCESS_COLOR = new Color(34, 197, 94); // Vert
    private final Color WARNING_COLOR = new Color(234, 179, 8); // Jaune
    private final Color DANGER_COLOR = new Color(239, 68, 68); // Rouge
    private final Color TEXT_LIGHT = new Color(241, 245, 249); // Texte clair
    private final Color TEXT_DARK = new Color(30, 41, 59); // Texte foncé
    private final Color SURFACE_COLOR = new Color(248, 250, 252); // Fond clair
    private final Color CARD_COLOR = Color.WHITE; // Couleur des cartes

    private JPanel mainPanel;
    private JPanel menuPanel;
    private JPanel contentPanel;

    private JButton produitsButton;
    private JButton ventesButton;
    private JButton stockButton;
    private JButton rapportsButton;
    private JButton parametresButton;
    private JButton pharmaciensButton;
    private JButton deconnexionButton;
    private JButton dashboardButton;
    private JButton clientsButton;

    // Couleur pour le menu actif
    private JButton activeButton = null;

    private JLabel welcomeLabel;
    private JLabel statusLabel;

    private JTabbedPane tabbedPane;

    /**
     * Constructeur du tableau de bord
     * 
     * @param pharmacien Le pharmacien connecté
     */
    public DashboardView(Pharmacien pharmacien) {
        this.pharmacienConnecte = pharmacien;
        this.controller = new DashboardController();

        initialiserComposants();
        initialiserEvenements();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("PharmaGest - Tableau de bord");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setSize(1280, 720);
        setMinimumSize(new Dimension(1024, 600));
        setLocationRelativeTo(null);

        // Afficher le panel de bienvenue par défaut
        activateButton(dashboardButton);
        afficherBienvenue();
    }

    /**
     * Initialise les composants de l'interface
     */
    private void initialiserComposants() {
        // Panel principal avec BorderLayout
        mainPanel = new JPanel(new BorderLayout());

        // Panel du menu à gauche avec dégradé de couleur
        menuPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                // Créer un dégradé vertical
                GradientPaint gradient = new GradientPaint(
                        0, 0, PRIMARY_DARK,
                        0, getHeight(), PRIMARY_DARK.darker());

                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
            }
        };

        menuPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        menuPanel.setPreferredSize(new Dimension(260, getHeight()));

        // Panel pour contenir les boutons du menu (avec BoxLayout)
        JPanel menuButtonsPanel = new JPanel();
        menuButtonsPanel.setLayout(new BoxLayout(menuButtonsPanel, BoxLayout.Y_AXIS));
        menuButtonsPanel.setOpaque(false);

        // Logo et titre en haut du menu
        JPanel logoPanel = new JPanel(new BorderLayout());
        logoPanel.setOpaque(false);
        logoPanel.setPreferredSize(new Dimension(260, 100));
        logoPanel.setBorder(BorderFactory.createEmptyBorder(20, 15, 20, 15));

        JLabel logoLabel = new JLabel("PharmaGest");
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        logoLabel.setForeground(TEXT_LIGHT);

        JLabel subTitleLabel = new JLabel("Gestion de Pharmacie");
        subTitleLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        subTitleLabel.setForeground(new Color(255, 255, 255, 180));

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);
        titlePanel.add(logoLabel);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 5)));
        titlePanel.add(subTitleLabel);

        logoPanel.add(titlePanel, BorderLayout.CENTER);
        menuPanel.add(logoPanel, BorderLayout.NORTH);

        // Panel utilisateur
        JPanel userPanel = new JPanel();
        userPanel.setLayout(new BoxLayout(userPanel, BoxLayout.Y_AXIS));
        userPanel.setOpaque(false);
        userPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        userPanel.setMaximumSize(new Dimension(260, 80));

        // Avatar utilisateur (cercle avec initiales)
        JPanel avatarPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int size = Math.min(getWidth(), getHeight()) - 4;
                int x = (getWidth() - size) / 2;
                int y = (getHeight() - size) / 2;

                // Dessiner le cercle de fond
                g2d.setColor(ACCENT_COLOR);
                g2d.fillOval(x, y, size, size);

                // Dessiner les initiales
                g2d.setColor(TEXT_LIGHT);
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 16));

                String initials = String.valueOf(pharmacienConnecte.getPrenom().charAt(0)) +
                        String.valueOf(pharmacienConnecte.getNom().charAt(0));

                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(initials);
                int textHeight = fm.getHeight();

                g2d.drawString(initials,
                        x + (size - textWidth) / 2,
                        y + ((size - textHeight) / 2) + fm.getAscent());

                g2d.dispose();
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(40, 40);
            }
        };

        avatarPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel userNameLabel = new JLabel(pharmacienConnecte.getNomComplet());
        userNameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        userNameLabel.setForeground(TEXT_LIGHT);
        userNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel userRoleLabel = new JLabel(pharmacienConnecte.getRole());
        userRoleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        userRoleLabel.setForeground(new Color(255, 255, 255, 180));
        userRoleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        userPanel.add(avatarPanel);
        userPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        userPanel.add(userNameLabel);
        userPanel.add(Box.createRigidArea(new Dimension(0, 3)));
        userPanel.add(userRoleLabel);

        // Menu principal - Titre
        JLabel menuTitleLabel = new JLabel("MENU PRINCIPAL");
        menuTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        menuTitleLabel.setForeground(new Color(180, 180, 180));
        menuTitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        menuTitleLabel.setBorder(BorderFactory.createEmptyBorder(15, 15, 5, 15));

        // Création des boutons de menu avec icônes
        dashboardButton = createMenuButton("Tableau de bord", IconUtils.ICON_DASHBOARD);
        produitsButton = createMenuButton("Produits", IconUtils.ICON_PRODUCTS);
        ventesButton = createMenuButton("Ventes", IconUtils.ICON_SALES);
        stockButton = createMenuButton("Stock", IconUtils.ICON_STOCK);
        rapportsButton = createMenuButton("Rapports", IconUtils.ICON_REPORTS);
        clientsButton = createMenuButton("Clients", IconUtils.ICON_CLIENTS);

        // Ajouter les composants au panel des boutons de menu
        menuButtonsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        menuButtonsPanel.add(userPanel);
        menuButtonsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        menuButtonsPanel.add(menuTitleLabel);
        menuButtonsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        menuButtonsPanel.add(dashboardButton);
        menuButtonsPanel.add(produitsButton);
        menuButtonsPanel.add(ventesButton);
        menuButtonsPanel.add(stockButton);
        menuButtonsPanel.add(rapportsButton);
        menuButtonsPanel.add(clientsButton);

        // N'afficher les boutons d'administration que pour les utilisateurs avec le
        // rôle ADMIN
        if (pharmacienConnecte.getRole().equals("ADMIN")) {
            // Titre de la section d'administration
            JLabel adminTitleLabel = new JLabel("ADMINISTRATION");
            adminTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
            adminTitleLabel.setForeground(new Color(180, 180, 180));
            adminTitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            adminTitleLabel.setBorder(BorderFactory.createEmptyBorder(15, 15, 5, 15));

            pharmaciensButton = createMenuButton("Pharmaciens", IconUtils.ICON_USERS);
            parametresButton = createMenuButton("Paramètres", IconUtils.ICON_SETTINGS);

            menuButtonsPanel.add(Box.createRigidArea(new Dimension(0, 15)));
            menuButtonsPanel.add(adminTitleLabel);
            menuButtonsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            menuButtonsPanel.add(pharmaciensButton);
            menuButtonsPanel.add(parametresButton);
        }

        // Ajouter espace flexible pour pousser le bouton déconnexion vers le bas
        menuButtonsPanel.add(Box.createVerticalGlue());

        // Bouton de déconnexion en bas
        JSeparator separator = new JSeparator();
        separator.setMaximumSize(new Dimension(260, 1));
        separator.setForeground(new Color(80, 80, 80));
        separator.setBackground(new Color(60, 60, 60));

        deconnexionButton = createMenuButton("Déconnexion", IconUtils.ICON_LOGOUT);

        menuButtonsPanel.add(separator);
        menuButtonsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        menuButtonsPanel.add(deconnexionButton);
        menuButtonsPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Ajouter le panel des boutons au menu
        menuPanel.add(menuButtonsPanel, BorderLayout.CENTER);

        // Panel de contenu principal
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(SURFACE_COLOR);

        // Barre de statut en bas
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBackground(PRIMARY_DARK);
        statusPanel.setPreferredSize(new Dimension(getWidth(), 30));

        statusLabel = new JLabel(" ");
        statusLabel.setForeground(TEXT_LIGHT);
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));

        // Ajouter l'heure et la date à droite de la barre de statut
        JLabel timeLabel = new JLabel();
        timeLabel.setForeground(TEXT_LIGHT);
        timeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        timeLabel.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));

        // Mise à jour de l'heure toutes les secondes
        Timer timer = new Timer(1000, e -> {
            LocalDate today = LocalDate.now();
            String dayName = today.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.FRANCE);
            String datePart = dayName + " " + today.format(DateTimeFormatter.ofPattern("dd MMMM yyyy"));
            String timePart = java.time.LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            timeLabel.setText(datePart + " | " + timePart);
        });
        timer.start();

        statusPanel.add(statusLabel, BorderLayout.WEST);
        statusPanel.add(timeLabel, BorderLayout.EAST);

        // Ajouter les panels au panel principal
        mainPanel.add(menuPanel, BorderLayout.WEST);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        mainPanel.add(statusPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    /**
     * Crée un bouton de menu stylisé
     * 
     * @param text     Le texte du bouton
     * @param iconText L'emoji ou icône à afficher
     * @return Le bouton créé
     */
    private JButton createMenuButton(String text, String iconText) {
        // Création d'un panel pour contenir l'icône et le texte
        JPanel buttonPanel = new JPanel(new BorderLayout(10, 0));
        buttonPanel.setOpaque(false);

        // Créer un panel pour l'icône avec une taille fixe
        JPanel iconContainer = new JPanel(new BorderLayout());
        iconContainer.setOpaque(false);
        iconContainer.setPreferredSize(new Dimension(30, 30));

        // Créer un label pour l'icône
        JLabel iconLabel = new JLabel(iconText);
        iconLabel.setFont(new Font("Dialog", Font.BOLD, 22));
        iconLabel.setForeground(TEXT_LIGHT);
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        iconContainer.add(iconLabel, BorderLayout.CENTER);

        // Créer un label pour le texte
        JLabel textLabel = new JLabel(text);
        textLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textLabel.setForeground(TEXT_LIGHT);

        // Ajouter l'icône et le texte au panel
        buttonPanel.add(iconContainer, BorderLayout.WEST);
        buttonPanel.add(textLabel, BorderLayout.CENTER);

        // Créer le bouton avec le panel comme contenu
        JButton button = new JButton();
        button.setLayout(new BorderLayout());
        button.add(buttonPanel, BorderLayout.CENTER);

        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(true);
        button.setOpaque(true);
        button.setBackground(PRIMARY_DARK);
        button.setMaximumSize(new Dimension(260, 45));
        button.setPreferredSize(new Dimension(260, 45));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));

        // Créer un indicateur de menu actif (barre verticale à gauche)
        JPanel indicator = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (button == activeButton) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2d.setColor(ACCENT_COLOR);
                    g2d.fillRect(0, 0, 4, getHeight());
                    g2d.dispose();
                }
            }
        };
        indicator.setOpaque(false);
        indicator.setPreferredSize(new Dimension(4, 0));
        button.add(indicator, BorderLayout.WEST);

        // Effet de survol avec transitions plus fluides
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (button != activeButton) {
                    button.setBackground(PRIMARY_MEDIUM);
                    // Animation subtile d'agrandissement
                    buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 3, 0, 0));
                    iconLabel.setForeground(ACCENT_COLOR);
                }
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (button != activeButton) {
                    button.setBackground(PRIMARY_DARK);
                    buttonPanel.setBorder(null);
                    iconLabel.setForeground(TEXT_LIGHT);
                }
            }

            public void mousePressed(java.awt.event.MouseEvent evt) {
                if (button != activeButton) {
                    button.setBackground(PRIMARY_LIGHT);
                }
            }

            public void mouseReleased(java.awt.event.MouseEvent evt) {
                if (button != activeButton) {
                    if (button.contains(evt.getPoint())) {
                        button.setBackground(PRIMARY_MEDIUM);
                    } else {
                        button.setBackground(PRIMARY_DARK);
                        buttonPanel.setBorder(null);
                        iconLabel.setForeground(TEXT_LIGHT);
                    }
                }
            }
        });

        return button;
    }

    /**
     * Active un bouton du menu en le mettant en évidence
     * 
     * @param button Le bouton à activer
     */
    private void activateButton(JButton button) {
        // Désactive l'ancien bouton actif s'il existe
        if (activeButton != null) {
            activeButton.setBackground(PRIMARY_DARK);

            // Réinitialiser les composants internes du bouton précédemment actif
            for (Component comp : activeButton.getComponents()) {
                if (comp instanceof JPanel && comp.getName() == null) {
                    ((JPanel) comp).setBorder(null);
                }
            }

            // S'assurer que tous les composants sont visibles avec les couleurs par défaut
            for (Component comp : activeButton.getComponents()) {
                if (comp instanceof JPanel) {
                    JPanel panel = (JPanel) comp;
                    for (Component c : panel.getComponents()) {
                        if (c instanceof JLabel) {
                            ((JLabel) c).setForeground(TEXT_LIGHT);
                        } else if (c instanceof JPanel) {
                            // Rechercher l'icône à l'intérieur des sous-panels
                            for (Component subC : ((JPanel) c).getComponents()) {
                                if (subC instanceof JLabel) {
                                    ((JLabel) subC).setForeground(TEXT_LIGHT);
                                }
                            }
                        }
                    }
                }
            }

            activeButton.repaint();
        }

        // Active le nouveau bouton
        activeButton = button;
        activeButton.setBackground(ACCENT_COLOR.darker());

        // Ajouter un effet visuel pour le bouton actif et s'assurer que le texte est
        // visible
        for (Component comp : activeButton.getComponents()) {
            if (comp instanceof JPanel && !(comp instanceof JButton) && comp.getName() == null) {
                JPanel panel = (JPanel) comp;
                ((JPanel) comp).setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));

                // S'assurer que tous les labels dans le panel sont bien contrastés
                for (Component c : panel.getComponents()) {
                    if (c instanceof JLabel) {
                        ((JLabel) c).setForeground(TEXT_LIGHT);
                    } else if (c instanceof JPanel) {
                        // Rechercher l'icône à l'intérieur des sous-panels
                        for (Component subC : ((JPanel) c).getComponents()) {
                            if (subC instanceof JLabel) {
                                ((JLabel) subC).setForeground(ACCENT_COLOR.brighter());
                            }
                        }
                    }
                }
            }
        }

        activeButton.repaint();
    }

    /**
     * Initialise les événements (listeners) des composants
     */
    private void initialiserEvenements() {
        // Bouton Tableau de bord
        dashboardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                activateButton(dashboardButton);
                afficherBienvenue();
            }
        });

        // Bouton Produits
        produitsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                activateButton(produitsButton);
                afficherGestionProduits();
            }
        });

        // Bouton Ventes
        ventesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                activateButton(ventesButton);
                afficherGestionVentes();
            }
        });

        // Bouton Stock
        stockButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                activateButton(stockButton);
                afficherGestionStock();
            }
        });

        // Bouton Rapports
        rapportsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                activateButton(rapportsButton);
                afficherRapports();
            }
        });

        // Bouton Clients
        clientsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                activateButton(clientsButton);
                afficherGestionClients();
            }
        });

        // Bouton Paramètres (si l'utilisateur est admin)
        if (parametresButton != null) {
            parametresButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    activateButton(parametresButton);
                    afficherParametres();
                }
            });
        }

        // Bouton Pharmaciens (si l'utilisateur est admin)
        if (pharmaciensButton != null) {
            pharmaciensButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    activateButton(pharmaciensButton);
                    afficherGestionPharmaciens();
                }
            });
        }

        // Bouton Déconnexion
        deconnexionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deconnecter();
            }
        });

        // Détection de fermeture de la fenêtre
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // Nettoyer les ressources à la fermeture
                controller.cleanup();
            }
        });
    }

    /**
     * Affiche le panneau de bienvenue
     */
    private void afficherBienvenue() {
        contentPanel.removeAll();

        // Panel principal avec effet de grille
        JPanel welcomePanel = new JPanel();
        welcomePanel.setLayout(new BorderLayout(0, 20));
        welcomePanel.setBackground(SURFACE_COLOR);
        welcomePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Panel d'en-tête avec dégradé de couleur
        JPanel headerPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                // Créer un dégradé horizontal
                GradientPaint gradient = new GradientPaint(
                        0, 0, ACCENT_COLOR,
                        getWidth(), 0, ACCENT_COLOR.darker());

                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

                // Dessiner un motif subtil en arrière-plan
                g2d.setColor(new Color(255, 255, 255, 30));
                for (int x = 0; x < getWidth(); x += 20) {
                    for (int y = 0; y < getHeight(); y += 20) {
                        g2d.fillOval(x, y, 2, 2);
                    }
                }

                g2d.dispose();
            }
        };
        headerPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        headerPanel.setPreferredSize(new Dimension(getWidth(), 130));

        // Conteneur pour les informations de l'en-tête
        JPanel headerContentPanel = new JPanel(new BorderLayout(20, 0));
        headerContentPanel.setOpaque(false);

        // Avatar plus grand
        JPanel headerAvatarPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int size = Math.min(getWidth(), getHeight()) - 4;
                int x = (getWidth() - size) / 2;
                int y = (getHeight() - size) / 2;

                // Dessiner le cercle de fond
                g2d.setColor(TEXT_LIGHT);
                g2d.fillOval(x, y, size, size);

                // Dessiner les initiales
                g2d.setColor(ACCENT_COLOR.darker());
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 30));

                String initials = String.valueOf(pharmacienConnecte.getPrenom().charAt(0)) +
                        String.valueOf(pharmacienConnecte.getNom().charAt(0));

                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(initials);
                int textHeight = fm.getHeight();

                g2d.drawString(initials,
                        x + (size - textWidth) / 2,
                        y + ((size - textHeight) / 2) + fm.getAscent());

                g2d.dispose();
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(80, 80);
            }
        };

        JPanel headerInfoPanel = new JPanel();
        headerInfoPanel.setLayout(new BoxLayout(headerInfoPanel, BoxLayout.Y_AXIS));
        headerInfoPanel.setOpaque(false);

        welcomeLabel = new JLabel("Bienvenue, " + pharmacienConnecte.getPrenom() + " !");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 35));
        welcomeLabel.setForeground(TEXT_LIGHT);

        JLabel subtitleLabel = new JLabel("Voici un aperçu de votre pharmacie aujourd'hui");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitleLabel.setForeground(new Color(255, 255, 255, 200));

        LocalDate today = LocalDate.now();
        String dayName = today.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.FRANCE);
        String dateStr = dayName + " " + today.format(DateTimeFormatter.ofPattern("dd MMMM yyyy"));

        JLabel dateLabel = new JLabel(dateStr);
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dateLabel.setForeground(new Color(255, 255, 255, 180));

        headerInfoPanel.add(welcomeLabel);
        headerInfoPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        headerInfoPanel.add(subtitleLabel);
        headerInfoPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        headerInfoPanel.add(dateLabel);

        headerContentPanel.add(headerAvatarPanel, BorderLayout.WEST);
        headerContentPanel.add(headerInfoPanel, BorderLayout.CENTER);

        headerPanel.add(headerContentPanel, BorderLayout.CENTER);

        // Panel pour les statistiques
        JPanel statsContainer = new JPanel(new BorderLayout(0, 15));
        statsContainer.setOpaque(false);

        // Titre des statistiques
        JLabel statsTitle = new JLabel("Aperçu des statistiques");
        statsTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        statsTitle.setForeground(TEXT_DARK);

        // Grille de statistiques (2x2)
        JPanel statsGridPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        statsGridPanel.setBackground(SURFACE_COLOR);

        // Récupérer les statistiques actuelles
        String produits = controller.getNombreProduits();
        String ventesAujourdhui = controller.getNombreVentesAujourdhui();
        String produitsExpires = controller.getNombreProduitsExpires();
        String montantAujourdhui = controller.getMontantVentesAujourdhui() + " FCFA";

        // Créer les cartes de statistiques
        statsGridPanel.add(createStatCard("Produits en stock", produits, IconUtils.ICON_PRODUCT_COUNT, ACCENT_COLOR));
        statsGridPanel
                .add(createStatCard("Ventes aujourd'hui", ventesAujourdhui, IconUtils.ICON_SALES_COUNT, SUCCESS_COLOR));
        statsGridPanel.add(createStatCard("Produits expirés", produitsExpires, IconUtils.ICON_EXPIRED, DANGER_COLOR));
        statsGridPanel
                .add(createStatCard("Montant aujourd'hui", montantAujourdhui, IconUtils.ICON_MONEY, WARNING_COLOR));

        statsContainer.add(statsTitle, BorderLayout.NORTH);
        statsContainer.add(statsGridPanel, BorderLayout.CENTER);

        // Panel pour les liens rapides et activités récentes
        JPanel quickPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        quickPanel.setOpaque(false);

        // Panel des liens rapides
        JPanel quickLinksPanel = createQuickLinksPanel();

        // Panel des activités récentes
        JPanel recentActivitiesPanel = createRecentActivitiesPanel();

        quickPanel.add(quickLinksPanel);
        quickPanel.add(recentActivitiesPanel);

        JPanel dashboardContent = new JPanel();
        dashboardContent.setLayout(new BoxLayout(dashboardContent, BoxLayout.Y_AXIS));
        dashboardContent.setOpaque(false);
        dashboardContent.add(statsContainer);
        dashboardContent.add(Box.createRigidArea(new Dimension(0, 20)));
        dashboardContent.add(quickPanel);

        welcomePanel.add(headerPanel, BorderLayout.NORTH);
        welcomePanel.add(dashboardContent, BorderLayout.CENTER);

        // Ajouter un panel de marge pour créer un effet de largeur maximale
        JPanel marginPanel = new JPanel(new BorderLayout());
        marginPanel.setOpaque(false);
        marginPanel.add(welcomePanel, BorderLayout.CENTER);
        marginPanel.add(Box.createRigidArea(new Dimension(50, 0)), BorderLayout.WEST);
        marginPanel.add(Box.createRigidArea(new Dimension(50, 0)), BorderLayout.EAST);

        contentPanel.add(marginPanel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();

        statusLabel.setText(IconUtils.ICON_USER + " Connecté en tant que " + pharmacienConnecte.getRole());
    }

    /**
     * Crée une carte de statistique
     * 
     * @param title Le titre de la statistique
     * @param value La valeur à afficher
     * @param icon  L'icône à afficher
     * @param color La couleur d'accent
     * @return Le panneau créé
     */
    private JPanel createStatCard(String title, String value, String icon, Color color) {
        JPanel panel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Fond blanc arrondi
                g2d.setColor(CARD_COLOR);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

                // Barre d'accent en haut
                g2d.setColor(color);
                g2d.fillRoundRect(0, 0, getWidth(), 5, 5, 5);

                g2d.dispose();
            }
        };
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setOpaque(false);

        JPanel contentPanel = new JPanel(new BorderLayout(15, 10));
        contentPanel.setOpaque(false);

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Dialog", Font.BOLD, 48));
        iconLabel.setForeground(color);
        iconLabel.setVerticalAlignment(SwingConstants.TOP);

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(TEXT_DARK);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        valueLabel.setForeground(color);
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        textPanel.add(titleLabel);
        textPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        textPanel.add(valueLabel);

        contentPanel.add(iconLabel, BorderLayout.WEST);
        contentPanel.add(textPanel, BorderLayout.CENTER);

        panel.add(contentPanel, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Crée un panneau de liens rapides
     * 
     * @return Le panneau créé
     */
    private JPanel createQuickLinksPanel() {
        JPanel panel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Fond blanc arrondi
                g2d.setColor(CARD_COLOR);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

                g2d.dispose();
            }
        };
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Accès rapide");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(TEXT_DARK);

        JPanel linksPanel = new JPanel(new GridLayout(3, 1, 0, 10));
        linksPanel.setOpaque(false);

        linksPanel.add(createQuickLinkButton("Nouvelle vente", IconUtils.ICON_SALES, e -> {
            activateButton(ventesButton);
            afficherGestionVentes();
        }));

        linksPanel.add(createQuickLinkButton("Gérer le stock", IconUtils.ICON_STOCK, e -> {
            activateButton(stockButton);
            afficherGestionStock();
        }));

        linksPanel.add(createQuickLinkButton("Voir les rapports", IconUtils.ICON_REPORTS, e -> {
            activateButton(rapportsButton);
            afficherRapports();
        }));

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(Box.createRigidArea(new Dimension(0, 10)), BorderLayout.CENTER);
        panel.add(linksPanel, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Crée un bouton de lien rapide
     * 
     * @param text   Le texte du bouton
     * @param icon   L'icône à afficher
     * @param action L'action à exécuter
     * @return Le bouton créé
     */
    private JButton createQuickLinkButton(String text, String icon, ActionListener action) {
        JButton button = new JButton();
        button.setLayout(new BorderLayout(10, 0));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        // Créer un fond avec un contour léger pour le bouton
        button.setOpaque(true);
        button.setBackground(new Color(245, 245, 245));

        // Créer un panel pour l'icône avec une taille fixe
        JPanel iconPanel = new JPanel(new BorderLayout());
        iconPanel.setOpaque(false);
        iconPanel.setPreferredSize(new Dimension(30, 30));

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Dialog", Font.BOLD, 24));
        iconLabel.setForeground(ACCENT_COLOR);
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        iconPanel.add(iconLabel, BorderLayout.CENTER);

        JLabel textLabel = new JLabel(text);
        textLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        textLabel.setForeground(TEXT_DARK);

        button.add(iconPanel, BorderLayout.WEST);
        button.add(textLabel, BorderLayout.CENTER);

        // Effet visuel au survol
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(ACCENT_COLOR);
                textLabel.setForeground(TEXT_LIGHT);
                iconLabel.setForeground(TEXT_LIGHT);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(245, 245, 245));
                textLabel.setForeground(TEXT_DARK);
                iconLabel.setForeground(ACCENT_COLOR);
            }
        });

        button.addActionListener(action);

        return button;
    }

    /**
     * Crée un panneau d'activités récentes
     * 
     * @return Le panneau créé
     */
    private JPanel createRecentActivitiesPanel() {
        JPanel panel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Fond blanc arrondi
                g2d.setColor(CARD_COLOR);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

                g2d.dispose();
            }
        };
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Alertes importantes");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(TEXT_DARK);

        JPanel alertsPanel = new JPanel();
        alertsPanel.setLayout(new BoxLayout(alertsPanel, BoxLayout.Y_AXIS));
        alertsPanel.setOpaque(false);

        // Nombre de produits expirés
        int produitsExpires = Integer.parseInt(controller.getNombreProduitsExpires());
        if (produitsExpires > 0) {
            alertsPanel.add(createAlertItem(IconUtils.ICON_WARNING,
                    produitsExpires + " produits expirés à retirer du stock", DANGER_COLOR));
        } else {
            alertsPanel.add(createAlertItem(IconUtils.ICON_SUCCESS, "Aucun produit expiré", SUCCESS_COLOR));
        }

        // Simuler d'autres alertes (à remplacer par des alertes réelles)
        alertsPanel.add(createAlertItem(IconUtils.ICON_WARNING, "5 produits sous le seuil d'alerte", WARNING_COLOR));
        alertsPanel
                .add(createAlertItem(IconUtils.ICON_MEDICINE, "Les ordonnances doivent être archivées", ACCENT_COLOR));

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(Box.createRigidArea(new Dimension(0, 10)), BorderLayout.CENTER);
        panel.add(alertsPanel, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Crée un élément d'alerte
     * 
     * @param icon  L'icône à afficher
     * @param text  Le texte de l'alerte
     * @param color La couleur de l'alerte
     * @return Le panneau créé
     */
    private JPanel createAlertItem(String icon, String text, Color color) {
        JPanel panel = new JPanel(new BorderLayout(15, 0));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));

        // Créer un panel pour l'icône avec une taille fixe
        JPanel iconPanel = new JPanel(new BorderLayout());
        iconPanel.setOpaque(false);
        iconPanel.setPreferredSize(new Dimension(30, 30));

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Dialog", Font.BOLD, 24));
        iconLabel.setForeground(color);
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        iconPanel.add(iconLabel, BorderLayout.CENTER);

        JLabel textLabel = new JLabel(text);
        textLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textLabel.setForeground(TEXT_DARK);

        panel.add(iconPanel, BorderLayout.WEST);
        panel.add(textLabel, BorderLayout.CENTER);

        // Ajouter une ligne de séparation et un effet au survol
        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setOpaque(false);
        wrapperPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
                BorderFactory.createEmptyBorder(2, 2, 2, 2)));
        wrapperPanel.add(panel, BorderLayout.CENTER);

        // Effet de survol
        wrapperPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                wrapperPanel.setOpaque(true);
                wrapperPanel.setBackground(new Color(245, 247, 250));
                wrapperPanel.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 0, color),
                        BorderFactory.createEmptyBorder(2, 2, 2, 2)));
                iconLabel.setForeground(color.darker());
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                wrapperPanel.setOpaque(false);
                wrapperPanel.setBackground(null);
                wrapperPanel.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
                        BorderFactory.createEmptyBorder(2, 2, 2, 2)));
                iconLabel.setForeground(color);
            }
        });

        return wrapperPanel;
    }

    /**
     * Affiche le panneau de gestion des produits
     */
    private void afficherGestionProduits() {
        contentPanel.removeAll();

        // Utiliser la vue ProduitView pour afficher la gestion des produits
        ProduitView produitView = new ProduitView();
        contentPanel.add(produitView, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();

        statusLabel.setText("Gestion des produits");
    }

    /**
     * Affiche le panneau de gestion des ventes
     */
    private void afficherGestionVentes() {
        System.out.println("Affichage de la vue des ventes");
        contentPanel.removeAll();

        // Utiliser la vue VenteView pour afficher la gestion des ventes
        VenteView venteView = new VenteView();
        contentPanel.add(venteView, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
        System.out.println("Vue des ventes ajoutée au contentPanel");

        statusLabel.setText("Gestion des ventes");
    }

    /**
     * Affiche le panneau de gestion du stock
     */
    private void afficherGestionStock() {
        contentPanel.removeAll();

        // Utiliser la vue StockView pour afficher la gestion du stock
        StockView stockView = new StockView();
        contentPanel.add(stockView, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();

        statusLabel.setText("Gestion du stock");
    }

    /**
     * Affiche le panneau des rapports
     */
    private void afficherRapports() {
        contentPanel.removeAll();

        // Utiliser la vue VenteHistoriqueView pour afficher les rapports de vente
        VenteHistoriqueView rapportView = new VenteHistoriqueView();
        contentPanel.add(rapportView, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();

        statusLabel.setText("Rapports");
    }

    /**
     * Affiche le panneau de gestion des pharmaciens
     */
    private void afficherGestionPharmaciens() {
        contentPanel.removeAll();

        // Utiliser la nouvelle vue PharmacienView
        PharmacienView pharmacienView = new PharmacienView();
        contentPanel.add(pharmacienView, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();

        statusLabel.setText("Gestion des pharmaciens");
    }

    /**
     * Affiche le panneau des paramètres
     */
    private void afficherParametres() {
        contentPanel.removeAll();

        // Utiliser la nouvelle vue ParametresView
        ParametresView parametresView = new ParametresView();
        contentPanel.add(parametresView, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();

        statusLabel.setText("Paramètres");
    }

    /**
     * Affiche le panneau de gestion des clients et leurs achats
     */
    private void afficherGestionClients() {
        contentPanel.removeAll();

        // Utiliser la nouvelle vue ClientAchatsView
        ClientAchatsView clientAchatsView = new ClientAchatsView();
        contentPanel.add(clientAchatsView, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();

        statusLabel.setText("Gestion des clients et de leurs achats");
    }

    /**
     * Méthode pour déconnecter l'utilisateur et revenir à l'écran de connexion
     */
    private void deconnecter() {
        // Ajouter la boîte de dialogue de confirmation
        int reponse = DialogUtils.afficherConfirmation(
                this,
                "Êtes-vous sûr de vouloir vous déconnecter ?",
                "Confirmation de déconnexion");

        // Procéder à la déconnexion si l'utilisateur confirme
        if (reponse == JOptionPane.YES_OPTION) {
            logger.info("Déconnexion de l'utilisateur");
            controller.deconnecter();
            this.dispose();
            new LoginView().setVisible(true);
        }
    }

    // Modifions la méthode initialiserOnglets pour ajouter un onglet Clients
    private void initialiserOnglets() {
        // Créer le JTabbedPane
        tabbedPane = new JTabbedPane();
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        // Onglet Produits
        ProduitView produitView = new ProduitView();
        tabbedPane.addTab("Produits", null, produitView, "Gérer les produits pharmaceutiques");

        // Onglet Ventes
        VenteView venteView = new VenteView();
        tabbedPane.addTab("Ventes", null, venteView, "Enregistrer des ventes");

        // Onglet Historique des ventes
        VenteHistoriqueView historiqueView = new VenteHistoriqueView();
        tabbedPane.addTab("Historique", null, historiqueView, "Consulter l'historique des ventes");

        // Onglet Stocks
        StockView stockView = new StockView();
        tabbedPane.addTab("Stocks", null, stockView, "Gérer les stocks");

        // Onglet Clients
        ClientView clientView = new ClientView();
        tabbedPane.addTab("Clients", null, clientView, "Gérer les clients");

        // Onglet Paramètres (uniquement pour les administrateurs)
        if (pharmacienConnecte.getRole().equals("ADMIN")) {
            ParametresView parametresView = new ParametresView();
            tabbedPane.addTab("Paramètres", null, parametresView, "Configurer les paramètres de l'application");

            // Onglet Gestion des utilisateurs
            PharmacienView pharmacienView = new PharmacienView();
            tabbedPane.addTab("Utilisateurs", null, pharmacienView, "Gérer les utilisateurs du système");
        }

        contentPanel.add(tabbedPane, BorderLayout.CENTER);
    }
}