package com.pharmajava.utils;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Champ texte avec fonctionnalité d'auto-complétion
 */
public class AutoCompletionTextField<T> extends JPanel {
    private JTextField textField;
    private JPopupMenu popupMenu;
    private List<T> allItems;
    private List<T> filteredItems;
    private Function<T, String> textExtractor;
    private Function<T, String> detailsExtractor;
    private Consumer<T> onItemSelected;
    private String placeholderText;
    private boolean showPopup = false;
    private Color backgroundColor = Color.WHITE;
    private Color textColor = new Color(44, 62, 80);
    private Color placeholderColor = new Color(180, 180, 180);
    private Color borderColor = new Color(220, 220, 220);
    private Color hoverColor = new Color(230, 240, 250);
    private Color selectionColor = new Color(41, 128, 185);
    private int maxResults = 8;

    /**
     * Crée un champ texte avec auto-complétion
     * 
     * @param placeholderText Texte d'indication
     * @param textExtractor Fonction pour extraire le texte à afficher
     * @param detailsExtractor Fonction pour extraire les détails à afficher
     * @param onItemSelected Consommateur appelé quand un élément est sélectionné
     */
    public AutoCompletionTextField(
            String placeholderText,
            Function<T, String> textExtractor,
            Function<T, String> detailsExtractor,
            Consumer<T> onItemSelected) {
        this.placeholderText = placeholderText;
        this.textExtractor = textExtractor;
        this.detailsExtractor = detailsExtractor;
        this.onItemSelected = onItemSelected;
        this.allItems = new ArrayList<>();
        this.filteredItems = new ArrayList<>();
        
        initializeComponents();
        setupListeners();
    }
    
    /**
     * Crée un champ texte avec auto-complétion qui utilise une fonction de recherche directe
     * 
     * @param searchFunction Fonction qui retourne les résultats de recherche à partir d'un texte
     */
    public AutoCompletionTextField(Function<String, List<T>> searchFunction) {
        this("Rechercher...", 
             item -> item.toString(), 
             item -> "", 
             null);
        
        // Nous allons simplement ajouter un nouveau listener sans supprimer l'ancien
        // car DocumentListeners est une interface et getDocumentListeners() n'existe pas
        
        // Ajouter un nouveau listener qui utilise la fonction de recherche
        textField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                searchWithFunction(searchFunction);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                searchWithFunction(searchFunction);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                searchWithFunction(searchFunction);
            }
        });
    }

    private void initializeComponents() {
        setLayout(new BorderLayout());
        
        // Créer le champ texte
        textField = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                
                // Afficher le placeholder si le champ est vide
                if (getText().isEmpty() && !hasFocus()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                    g2.setColor(placeholderColor);
                    g2.setFont(getFont());
                    g2.drawString(placeholderText, 8, getHeight() / 2 + getFont().getSize() / 2 - 2);
                    g2.dispose();
                }
            }
        };
        
        textField.setPreferredSize(new Dimension(250, 35));
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textField.setForeground(textColor);
        textField.setBackground(backgroundColor);
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor, 1, true),
                BorderFactory.createEmptyBorder(5, 8, 5, 30) // Espace à droite pour le bouton
        ));
        
        // Créer un panneau pour contenir le champ texte et le bouton
        JPanel textFieldPanel = new JPanel(new BorderLayout());
        textFieldPanel.setBackground(backgroundColor);
        textFieldPanel.setBorder(BorderFactory.createLineBorder(borderColor, 1, true));
        textFieldPanel.add(textField, BorderLayout.CENTER);
        
        // Créer un bouton pour afficher toutes les suggestions
        JButton dropdownButton = new JButton("▼");
        dropdownButton.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        dropdownButton.setForeground(textColor);
        dropdownButton.setBackground(backgroundColor);
        dropdownButton.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        dropdownButton.setFocusable(false);
        dropdownButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        dropdownButton.addActionListener(e -> {
            if (popupMenu.isVisible()) {
                popupMenu.setVisible(false);
            } else {
                // Afficher toutes les suggestions si le texte est vide
                String currentText = textField.getText();
                textField.setText("");
                // Affichage de toutes les suggestions disponibles
                showAllSuggestions();
                // Restaurer le texte d'origine
                textField.setText(currentText);
            }
        });
        
        // Panneau pour le bouton avec un layout personnalisé pour le positionnement
        JPanel buttonPanel = new JPanel() {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(25, textField.getPreferredSize().height);
            }
        };
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new BorderLayout());
        buttonPanel.add(dropdownButton, BorderLayout.CENTER);
        
        // Ajouter le panneau du bouton dans un panneau couche pour le superposer sur le champ texte
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(textField.getPreferredSize());
        
        textField.setBounds(0, 0, textField.getPreferredSize().width, textField.getPreferredSize().height);
        layeredPane.add(textField, JLayeredPane.DEFAULT_LAYER);
        
        buttonPanel.setBounds(textField.getPreferredSize().width - 25, 0, 25, textField.getPreferredSize().height);
        layeredPane.add(buttonPanel, JLayeredPane.PALETTE_LAYER);
        
        add(textField, BorderLayout.CENTER);
        
        // Initialiser le menu popup pour les suggestions
        popupMenu = new JPopupMenu();
        popupMenu.setBackground(Color.WHITE);
        popupMenu.setBorder(BorderFactory.createLineBorder(borderColor, 1, true));
    }

    private void setupListeners() {
        // Listener pour le champ texte
        textField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updatePopup();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updatePopup();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updatePopup();
            }
        });
        
        // Gérer les touches spéciales pour la navigation
        textField.addKeyListener(new KeyAdapter() {
            private int selectedIndex = -1;
            
            @Override
            public void keyPressed(KeyEvent e) {
                if (!popupMenu.isVisible()) {
                    if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_UP) {
                        // Ouvrir le popup si fermé mais flèches appuyées
                        updatePopup();
                        selectedIndex = -1;
                        e.consume();
                    }
                    return;
                }
                
                int itemCount = popupMenu.getComponentCount();
                if (itemCount == 0) return;
                
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_DOWN:
                        selectedIndex = (selectedIndex + 1) % itemCount;
                        highlightItem(selectedIndex);
                        e.consume();
                        break;
                    
                    case KeyEvent.VK_UP:
                        selectedIndex = (selectedIndex - 1 + itemCount) % itemCount;
                        highlightItem(selectedIndex);
                        e.consume();
                        break;
                    
                    case KeyEvent.VK_ENTER:
                        if (selectedIndex >= 0 && selectedIndex < filteredItems.size()) {
                            // Sélectionner l'élément surligné
                            T selectedItem = filteredItems.get(selectedIndex);
                            textField.setText(textExtractor.apply(selectedItem));
                            popupMenu.setVisible(false);
                            if (onItemSelected != null) {
                                onItemSelected.accept(selectedItem);
                            }
                            e.consume();
                        }
                        break;
                    
                    case KeyEvent.VK_ESCAPE:
                        popupMenu.setVisible(false);
                        selectedIndex = -1;
                        e.consume();
                        break;
                }
            }
            
            // Méthode pour mettre en évidence un élément du popup
            private void highlightItem(int index) {
                for (int i = 0; i < popupMenu.getComponentCount(); i++) {
                    Component comp = popupMenu.getComponent(i);
                    if (comp instanceof JMenuItem) {
                        JMenuItem menuItem = (JMenuItem) comp;
                        if (i == index) {
                            // Mettre en évidence l'élément sélectionné
                            menuItem.setBackground(selectionColor);
                            menuItem.setForeground(Color.WHITE);
                        } else {
                            // Restaurer l'apparence normale des autres éléments
                            menuItem.setBackground(Color.WHITE);
                            menuItem.setForeground(textColor);
                        }
                    }
                }
            }
        });
        
        // Remplacer la gestion de la perte de focus
        // Désactiver temporairement la fermeture automatique du popup à la perte de focus
        // pour permettre de cliquer sur un élément du popup
        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                // Ne pas fermer le popup tout de suite, laisser le temps de cliquer sur un élément
                // La fermeture se fera dans l'actionListener de chaque élément
                
                // Ne pas fermer le popup si la cible de l'événement est le popup ou un de ses composants
                Component opposite = e.getOppositeComponent();
                if (opposite != null) {
                    // Vérifier si l'élément qui a reçu le focus est lié au popup
                    boolean isPopupRelated = false;
                    
                    // Vérifier le popup lui-même
                    if (opposite == popupMenu) {
                        isPopupRelated = true;
                    }
                    
                    // Vérifier les composants du popup
                    if (!isPopupRelated) {
                        Component parent = opposite.getParent();
                        while (parent != null) {
                            if (parent == popupMenu) {
                                isPopupRelated = true;
                                break;
                            }
                            parent = parent.getParent();
                        }
                    }
                    
                    if (!isPopupRelated) {
                        // Utiliser un timer pour retarder la fermeture du popup
                        Timer timer = new Timer(500, event -> {
                            // Ne fermer que si le champ texte n'a pas repris le focus
                            if (!textField.isFocusOwner()) {
                                popupMenu.setVisible(false);
                            }
                        });
                        timer.setRepeats(false);
                        timer.start();
                    }
                }
                
                textField.repaint(); // Pour rafraîchir le placeholder
            }
            
            @Override
            public void focusGained(FocusEvent e) {
                textField.repaint(); // Pour rafraîchir le placeholder
                if (showPopup && !textField.getText().isEmpty()) {
                    updatePopup();
                }
            }
        });
        
        // Empêcher le popup de voler le focus
        popupMenu.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                e.consume(); // Empêcher l'événement de faire perdre le focus au champ
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                e.consume(); // Empêcher l'événement de faire perdre le focus au champ
            }
        });
        
        // Pour les éléments du popup, assurer qu'ils restent visibles
        popupMenu.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                // Garder le popup ouvert quand la souris est dessus
                if (!popupMenu.isVisible()) {
                    updatePopup();
                }
                e.consume();
            }
        });
    }

    /**
     * Affiche toutes les suggestions disponibles
     */
    private void showAllSuggestions() {
        // Mettre à jour le popup avec toutes les suggestions
        popupMenu.removeAll();
        
        if (allItems.isEmpty()) {
            return;
        }
        
        filteredItems.clear();
        filteredItems.addAll(allItems.subList(0, Math.min(allItems.size(), maxResults)));
        
        // Ajouter les éléments au popup
        for (T item : filteredItems) {
            JPanel panel = createSuggestionPanel(item);
            JMenuItem menuItem = new JMenuItem();
            menuItem.setLayout(new BorderLayout());
            menuItem.add(panel, BorderLayout.CENTER);
            menuItem.setBackground(Color.WHITE);
            menuItem.setBorder(BorderFactory.createEmptyBorder());
            
            // Améliorer la détection du clic
            menuItem.addActionListener(e -> {
                textField.setText(textExtractor.apply(item));
                if (onItemSelected != null) {
                    onItemSelected.accept(item);
                }
                popupMenu.setVisible(false);
            });
            
            // Empêcher la fermeture du popup lors du passage de la souris
            menuItem.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    menuItem.setBackground(hoverColor);
                }
                
                @Override
                public void mouseExited(MouseEvent e) {
                    menuItem.setBackground(Color.WHITE);
                }
            });
            
            popupMenu.add(menuItem);
        }
        
        // Configurer et afficher le popup avec une largeur appropriée
        int width = Math.max(textField.getWidth(), 300); // Au moins 300px pour la lisibilité
        int height = Math.min(filteredItems.size() * 45, 300); // Limiter la hauteur à 300px
        
        popupMenu.setPreferredSize(new Dimension(width, height));
        popupMenu.show(textField, 0, textField.getHeight());
        popupMenu.setFocusable(true);
        
        // S'assurer que le popup reste visible
        popupMenu.requestFocusInWindow();
    }

    private void updatePopup() {
        String text = textField.getText().toLowerCase().trim();
        
        // Pour débuguer
        System.out.println("Mise à jour du popup. Texte: '" + text + "'");
        
        if (text.isEmpty() || allItems.isEmpty()) {
            popupMenu.setVisible(false);
            return;
        }
        
        // Filtrer les éléments
        filteredItems.clear();
        for (T item : allItems) {
            String itemText = textExtractor.apply(item).toLowerCase();
            if (itemText.contains(text)) {
                filteredItems.add(item);
                if (filteredItems.size() >= maxResults) {
                    break;
                }
            }
        }
        
        // Pour débuguer
        System.out.println("Éléments filtrés trouvés: " + filteredItems.size());
        
        // Mettre à jour le popup
        popupMenu.removeAll();
        
        if (filteredItems.isEmpty()) {
            popupMenu.setVisible(false);
            return;
        }
        
        // Ajouter les éléments filtrés au popup
        for (T item : filteredItems) {
            JPanel panel = createSuggestionPanel(item);
            JMenuItem menuItem = new JMenuItem();
            menuItem.setLayout(new BorderLayout());
            menuItem.add(panel, BorderLayout.CENTER);
            menuItem.setBackground(Color.WHITE);
            menuItem.setBorder(BorderFactory.createEmptyBorder());
            
            // Améliorer la détection du clic
            final T finalItem = item; // Capture pour le lambda
            menuItem.addActionListener(e -> {
                System.out.println("Élément sélectionné via ActionListener");
                textField.setText(textExtractor.apply(finalItem));
                popupMenu.setVisible(false);
                
                // Important: ajouter un délai pour l'événement onItemSelected 
                // pour éviter les conflits de focus
                SwingUtilities.invokeLater(() -> {
                    if (onItemSelected != null) {
                        onItemSelected.accept(finalItem);
                    }
                });
            });
            
            popupMenu.add(menuItem);
        }
        
        // Configurer et afficher le popup avec une taille adéquate
        int width = Math.max(textField.getWidth(), 250);
        int height = Math.min(filteredItems.size() * 40, 300);
        
        popupMenu.setPreferredSize(new Dimension(width, height));
        
        // Afficher le popup sous le champ texte
        popupMenu.show(textField, 0, textField.getHeight());
        
        // Pour assurer la visibilité du popup, même en cas de perte de focus
        popupMenu.setFocusable(false); // Important: empêche le popup de prendre le focus
    }

    /**
     * Crée un panneau pour afficher une suggestion dans le popup
     */
    private JPanel createSuggestionPanel(T item) {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setBackground(backgroundColor);
        panel.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        
        String mainText = textExtractor.apply(item);
        String details = detailsExtractor != null ? detailsExtractor.apply(item) : null;
        
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        
        JLabel mainLabel = new JLabel(mainText);
        mainLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        mainLabel.setForeground(textColor);
        mainLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        textPanel.add(mainLabel);
        
        if (details != null && !details.isEmpty()) {
            JLabel detailsLabel = new JLabel(details);
            detailsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            detailsLabel.setForeground(textColor.darker());
            detailsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            textPanel.add(Box.createRigidArea(new Dimension(0, 3)));
            textPanel.add(detailsLabel);
            
            // Ajouter des icônes pour les informations importantes
            if (details.contains("Stock")) {
                String stockText = details.substring(details.indexOf("Stock:") + 7);
                int stock = 0;
                try {
                    stock = Integer.parseInt(stockText.trim().split(" ")[0]);
                } catch (Exception e) {
                    // Ignorer les erreurs de parsing
                }
                
                // Ajouter une indication visuelle pour les niveaux de stock
                if (stock == 0) {
                    detailsLabel.setForeground(new Color(231, 76, 60)); // Rouge
                } else if (stock < 5) {
                    detailsLabel.setForeground(new Color(243, 156, 18)); // Orange
                }
            }
        }
        
        panel.add(textPanel, BorderLayout.CENTER);
        
        // Ajouter un indicateur visuel ou une icône à droite si nécessaire
        if (details != null && details.contains("ordonnance")) {
            JLabel iconLabel = new JLabel("🔒");
            iconLabel.setFont(new Font("Dialog", Font.PLAIN, 14));
            iconLabel.setForeground(new Color(52, 152, 219));
            panel.add(iconLabel, BorderLayout.EAST);
        }
        
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                panel.setBackground(hoverColor);
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                panel.setBackground(backgroundColor);
            }
            
            @Override
            public void mouseClicked(MouseEvent e) {
                popupMenu.setVisible(false);
                if (onItemSelected != null) {
                    onItemSelected.accept(item);
                }
                
                // Effet visuel lors de la sélection
                panel.setBackground(selectionColor);
                setSelectedText(item);
                
                // Animer la transition
                new Thread(() -> {
                    try {
                        Thread.sleep(100);
                        SwingUtilities.invokeLater(() -> panel.setBackground(backgroundColor));
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    }
                }).start();
            }
        });
        
        return panel;
    }

    /**
     * Définit la liste complète des éléments pour l'auto-complétion
     */
    public void setItems(List<T> items) {
        this.allItems = new ArrayList<>(items);
    }

    /**
     * Récupère le champ texte interne
     */
    public JTextField getTextField() {
        return textField;
    }

    /**
     * Récupère le texte actuellement saisi
     */
    public String getText() {
        return textField.getText();
    }

    /**
     * Définit le texte du champ
     */
    public void setText(String text) {
        textField.setText(text);
    }

    /**
     * Définit si le popup doit s'afficher automatiquement au focus
     */
    public void setShowPopupOnFocus(boolean show) {
        this.showPopup = show;
    }
    
    /**
     * Définit le nombre maximum de résultats à afficher
     */
    public void setMaxResults(int maxResults) {
        this.maxResults = maxResults;
    }
    
    /**
     * Définit les couleurs du composant
     */
    public void setColors(Color background, Color text, Color placeholder, Color border, Color hover, Color selection) {
        this.backgroundColor = background;
        this.textColor = text;
        this.placeholderColor = placeholder;
        this.borderColor = border;
        this.hoverColor = hover;
        this.selectionColor = selection;
        
        textField.setForeground(textColor);
        textField.setBackground(backgroundColor);
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor, 1, true),
                BorderFactory.createEmptyBorder(5, 8, 5, 30) // Espace à droite pour le bouton
        ));
        
        repaint();
    }

    /**
     * Effectue une recherche avec la fonction fournie
     */
    private void searchWithFunction(Function<String, List<T>> searchFunction) {
        String text = textField.getText().trim();
        
        System.out.println("Recherche: '" + text + "'");
        
        if (text.isEmpty() || text.length() < 2) {
            popupMenu.setVisible(false);
            return;
        }
        
        // Exécuter la recherche
        List<T> results = searchFunction.apply(text);
        
        System.out.println("Résultats trouvés: " + (results != null ? results.size() : 0));
        
        // Mettre à jour le popup avec les résultats
        popupMenu.removeAll();
        
        if (results == null || results.isEmpty()) {
            popupMenu.setVisible(false);
            return;
        }
        
        // Ajouter les éléments au popup
        for (T item : results) {
            if (popupMenu.getComponentCount() >= maxResults) break;
            
            System.out.println("Ajout d'un élément au popup: " + item);
            
            JPanel panel = createSuggestionPanel(item);
            JMenuItem menuItem = new JMenuItem();
            menuItem.setLayout(new BorderLayout());
            menuItem.add(panel, BorderLayout.CENTER);
            menuItem.setBackground(Color.WHITE);
            menuItem.setBorder(BorderFactory.createEmptyBorder());
            
            menuItem.addActionListener(e -> {
                System.out.println("Élément sélectionné: " + item);
                textField.setText(textExtractor.apply(item));
                popupMenu.setVisible(false);
                if (onItemSelected != null) {
                    onItemSelected.accept(item);
                }
            });
            
            popupMenu.add(menuItem);
        }
        
        // Afficher le popup
        if (popupMenu.getComponentCount() > 0) {
            popupMenu.show(textField, 0, textField.getHeight());
            popupMenu.setFocusable(true);
            System.out.println("Popup affiché avec " + popupMenu.getComponentCount() + " éléments");
        }
    }

    /**
     * Définit l'action à exécuter lorsqu'un élément est sélectionné
     * 
     * @param onItemSelected Le consommateur à appeler avec l'élément sélectionné
     */
    public void onItemSelected(Consumer<T> onItemSelected) {
        this.onItemSelected = onItemSelected;
    }

    /**
     * Définit le texte du champ à partir d'un élément sélectionné
     * 
     * @param item L'élément sélectionné
     */
    private void setSelectedText(T item) {
        if (item != null && textExtractor != null) {
            textField.setText(textExtractor.apply(item));
        }
    }
} 