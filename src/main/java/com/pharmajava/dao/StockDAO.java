package com.pharmajava.dao;

import com.pharmajava.model.Produit;
import com.pharmajava.model.Stock;
import com.pharmajava.utils.DatabaseConfig;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Classe d'accès aux données pour le stock
 */
public class StockDAO {
    private static final Logger LOGGER = Logger.getLogger(StockDAO.class.getName());
    private final ProduitDAO produitDAO;

    /**
     * Constructeur de StockDAO
     */
    public StockDAO() {
        this.produitDAO = new ProduitDAO();
    }

    /**
     * Récupère tous les stocks de la base de données
     *
     * @return Liste de tous les stocks
     * @throws SQLException si une erreur SQL survient
     */
    public List<Stock> findAll() throws SQLException {
        List<Stock> stocks = new ArrayList<>();
        String query = "SELECT * FROM stock";
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                stocks.add(extractStockFromResultSet(rs));
            }
        }
        
        return stocks;
    }

    /**
     * Récupère un stock par son ID
     *
     * @param id l'ID du stock à récupérer
     * @return le stock correspondant ou null si non trouvé
     * @throws SQLException si une erreur SQL survient
     */
    public Stock findById(Integer id) throws SQLException {
        String query = "SELECT * FROM stock WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractStockFromResultSet(rs);
                }
            }
        }
        
        return null;
    }

    /**
     * Récupère tous les stocks pour un produit spécifique
     *
     * @param produitId l'ID du produit
     * @return Liste des stocks pour le produit
     * @throws SQLException si une erreur SQL survient
     */
    public List<Stock> findByProduitId(Integer produitId) throws SQLException {
        List<Stock> stocks = new ArrayList<>();
        String query = "SELECT * FROM stock WHERE produit_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, produitId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    stocks.add(extractStockFromResultSet(rs));
                }
            }
        }
        
        return stocks;
    }

    /**
     * Enregistre un nouveau stock dans la base de données
     *
     * @param stock le stock à enregistrer
     * @return true si l'opération a réussi, false sinon
     * @throws SQLException si une erreur SQL survient
     */
    public boolean save(Stock stock) throws SQLException {
        String query = "INSERT INTO stock (produit_id, lot_numero, quantite_disponible, date_expiration) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            
            // Log des valeurs avant insertion
            LOGGER.info("Tentative d'insertion de stock: " +
                      "produit_id=" + stock.getProduit().getId() + 
                      ", lot_numero=" + stock.getNumeroLot() + 
                      ", quantite_disponible=" + stock.getQuantite() + 
                      ", date_expiration=" + stock.getDateExpiration());
            
            pstmt.setInt(1, stock.getProduit().getId());
            pstmt.setString(2, stock.getNumeroLot());
            pstmt.setInt(3, stock.getQuantite());
            pstmt.setDate(4, Date.valueOf(stock.getDateExpiration()));
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                LOGGER.warning("Échec de l'insertion de stock: aucune ligne affectée");
                return false;
            }
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    stock.setId(generatedKeys.getInt(1));
                    // Mise à jour de la quantité du produit après l'ajout d'un nouveau stock
                    mettreAJourQuantiteProduit(stock.getProduit().getId());
                    LOGGER.info("Stock ajouté avec succès, ID=" + stock.getId());
                    return true;
                } else {
                    LOGGER.warning("Échec de l'insertion de stock: aucune clé générée");
                    return false;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur SQL lors de l'insertion de stock: " + e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur inattendue lors de l'insertion de stock: " + e.getMessage(), e);
            throw new SQLException("Erreur lors de l'insertion de stock", e);
        }
    }

    /**
     * Met à jour un stock existant
     *
     * @param stock le stock à mettre à jour
     * @return true si l'opération a réussi, false sinon
     * @throws SQLException si une erreur SQL survient
     */
    public boolean update(Stock stock) throws SQLException {
        String query = "UPDATE stock SET produit_id = ?, lot_numero = ?, quantite_disponible = ?, date_expiration = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, stock.getProduit().getId());
            pstmt.setString(2, stock.getNumeroLot());
            pstmt.setInt(3, stock.getQuantite());
            pstmt.setDate(4, Date.valueOf(stock.getDateExpiration()));
            pstmt.setInt(5, stock.getId());
            
            int affectedRows = pstmt.executeUpdate();
            
            // Mise à jour de la quantité du produit après la mise à jour d'un stock
            if (affectedRows > 0) {
                mettreAJourQuantiteProduit(stock.getProduit().getId());
            }
            
            return affectedRows > 0;
        }
    }

    /**
     * Supprime un stock par son ID
     *
     * @param id l'ID du stock à supprimer
     * @return true si l'opération a réussi, false sinon
     * @throws SQLException si une erreur SQL survient
     */
    public boolean delete(Integer id) throws SQLException {
        String query = "DELETE FROM stock WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, id);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    /**
     * Extrait un objet Stock à partir d'un ResultSet
     *
     * @param rs le ResultSet contenant les données du stock
     * @return l'objet Stock créé
     * @throws SQLException si une erreur SQL survient
     */
    private Stock extractStockFromResultSet(ResultSet rs) throws SQLException {
        Integer id = rs.getInt("id");
        Integer produitId = rs.getInt("produit_id");
        String numeroLot = rs.getString("lot_numero");
        Integer quantite = rs.getInt("quantite_disponible");
        LocalDate dateExpiration = rs.getDate("date_expiration").toLocalDate();
        
        Produit produit = produitDAO.findById(produitId);
        
        Stock stock = new Stock();
        stock.setId(id);
        stock.setProduit(produit);
        stock.setNumeroLot(numeroLot);
        stock.setQuantite(quantite);
        stock.setDateExpiration(dateExpiration);
        
        return stock;
    }

    /**
     * Diminue la quantité du stock d'un produit en commençant par les lots les plus proches de l'expiration
     * 
     * @param produitId L'ID du produit à mettre à jour
     * @param quantiteARetirer La quantité totale à retirer du stock
     * @return true si l'opération a réussi, false sinon
     * @throws SQLException si une erreur SQL survient
     */
    public boolean diminuerStockParLot(Integer produitId, int quantiteARetirer) throws SQLException {
        // Si aucune quantité à retirer, on retourne vrai immédiatement
        if (quantiteARetirer <= 0) {
            return true;
        }
        
        Connection conn = null;
        boolean lotsMisAJour = false;
        
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false); // Démarrer une transaction
            
            // Récupérer tous les lots non expirés triés par date d'expiration croissante
            String sql = "SELECT * FROM stock WHERE produit_id = ? AND quantite_disponible > 0 AND date_expiration >= CURRENT_DATE ORDER BY date_expiration ASC";
            
            List<Stock> lots = new ArrayList<>();
            
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, produitId);
                
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        lots.add(extractStockFromResultSet(rs));
                    }
                }
            }
            
            // Vérifier si la quantité totale disponible est suffisante
            int quantiteTotaleDisponible = 0;
            for (Stock lot : lots) {
                quantiteTotaleDisponible += lot.getQuantite();
            }
            
            LOGGER.info("Vérification du stock pour le produit ID=" + produitId + 
                      ". Demandé: " + quantiteARetirer + 
                      ", Disponible: " + quantiteTotaleDisponible);
            
            if (quantiteTotaleDisponible < quantiteARetirer) {
                throw new SQLException("Stock insuffisant pour le produit ID=" + produitId + 
                                      ". Demandé: " + quantiteARetirer + 
                                      ", Disponible: " + quantiteTotaleDisponible);
            }
            
            // Mise à jour des lots un par un, en commençant par ceux qui expirent le plus tôt
            int quantiteRestante = quantiteARetirer;
            String updateSql = "UPDATE stock SET quantite_disponible = ? WHERE id = ?";
            
            for (Stock lot : lots) {
                if (quantiteRestante <= 0) {
                    break;
                }
                
                int quantiteLot = lot.getQuantite();
                int quantiteARetirerDuLot = Math.min(quantiteRestante, quantiteLot);
                int nouvelleQuantiteLot = quantiteLot - quantiteARetirerDuLot;
                
                try (PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
                    pstmt.setInt(1, nouvelleQuantiteLot);
                    pstmt.setInt(2, lot.getId());
                    pstmt.executeUpdate();
                }
                
                quantiteRestante -= quantiteARetirerDuLot;
                LOGGER.info("Lot " + lot.getNumeroLot() + " : Retiré " + quantiteARetirerDuLot + 
                           " unités. Nouvelle quantité : " + nouvelleQuantiteLot);
            }
            
            lotsMisAJour = true;
            
            // Mettre à jour la quantité totale du produit
            boolean majReussie = mettreAJourQuantiteProduit(produitId);
            
            if (!majReussie) {
                LOGGER.warning("Mise à jour de la quantité totale du produit " + produitId + " échouée après modification des lots");
                throw new SQLException("Échec de la mise à jour de la quantité totale du produit");
            }
            
            conn.commit();
            LOGGER.info("Stock du produit " + produitId + " mis à jour avec succès. Retiré: " + quantiteARetirer);
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                    LOGGER.info("Transaction annulée suite à une erreur");
                } catch (SQLException ex) {
                    LOGGER.log(Level.SEVERE, "Erreur lors du rollback de la transaction", ex);
                }
            }
            LOGGER.log(Level.SEVERE, "Erreur lors de la diminution du stock par lot: " + e.getMessage(), e);
            throw e;
        } finally {
            // Si les lots ont été mis à jour mais qu'une erreur est survenue avant le commit,
            // on s'assure quand même de mettre à jour la quantité totale du produit
            if (lotsMisAJour && conn != null) {
                try {
                    boolean majReussie = mettreAJourQuantiteProduit(produitId);
                    if (!majReussie) {
                        LOGGER.warning("Tentative de mise à jour de secours de la quantité totale du produit " + produitId + " échouée");
                    } else {
                        LOGGER.info("Mise à jour de secours de la quantité totale du produit " + produitId + " réussie");
                    }
                } catch (Exception ex) {
                    LOGGER.log(Level.WARNING, "Erreur lors de la mise à jour de secours de la quantité du produit", ex);
                }
            }
            
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    LOGGER.log(Level.SEVERE, "Erreur lors de la fermeture de la connexion", e);
                }
            }
        }
    }

    /**
     * Met à jour la quantité totale en stock d'un produit
     * en calculant la somme des quantités de tous ses lots non expirés
     * 
     * @param produitId L'ID du produit à mettre à jour
     * @return true si la mise à jour a réussi, false sinon
     */
    private boolean mettreAJourQuantiteProduit(int produitId) {
        // Modifier la requête pour exclure les lots expirés
        String sql = "SELECT SUM(quantite_disponible) as total FROM stock WHERE produit_id = ? AND date_expiration >= CURRENT_DATE";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, produitId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int nouvelleQuantite = rs.getInt("total");
                    // Si le résultat est NULL (pas de lots non expirés), on met 0
                    if (rs.wasNull()) {
                        nouvelleQuantite = 0;
                    }
                    LOGGER.info("Mise à jour de la quantité du produit " + produitId + ": nouvelle quantité = " + nouvelleQuantite);
                    boolean success = produitDAO.mettreAJourStock(produitId, nouvelleQuantite);
                    if (!success) {
                        LOGGER.warning("Échec de la mise à jour de la quantité du produit " + produitId);
                    }
                    return success;
                }
            }
            
            LOGGER.warning("Aucun résultat trouvé lors du calcul de la quantité du produit " + produitId);
            return false;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la mise à jour de la quantité du produit: " + e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Récupère tous les stocks expirés
     * 
     * @return Liste des stocks expirés
     */
    public List<Stock> obtenirStocksExpires() {
        List<Stock> stocksExpires = new ArrayList<>();
        String sql = "SELECT * FROM stock WHERE date_expiration < CURRENT_DATE";
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                stocksExpires.add(extractStockFromResultSet(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération des stocks expirés", e);
        }
        
        return stocksExpires;
    }
    
    /**
     * Récupère tous les stocks qui expirent dans un certain nombre de jours
     * 
     * @param jours Nombre de jours avant expiration
     * @return Liste des stocks expirant bientôt
     */
    public List<Stock> obtenirStocksExpirantBientot(int jours) {
        List<Stock> stocksExpirantBientot = new ArrayList<>();
        // Requête pour sélectionner les stocks dont la date d'expiration est entre aujourd'hui et X jours
        String sql = "SELECT * FROM stock WHERE date_expiration >= CURRENT_DATE AND date_expiration <= DATE_ADD(CURRENT_DATE, INTERVAL ? DAY)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, jours);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    stocksExpirantBientot.add(extractStockFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération des stocks expirant bientôt", e);
        }
        
        return stocksExpirantBientot;
    }

    /**
     * Met à jour les quantités en stock de tous les produits
     * en recalculant les sommes des quantités de leurs lots non expirés
     * 
     * @return true si l'opération a réussi, false sinon
     */
    public boolean actualiserTousLesStocks() {
        boolean success = true;
        Connection conn = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);
            
            // 1. D'abord, récupérer tous les produits existants
            List<Integer> produitIds = new ArrayList<>();
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT id FROM produits")) {
                
                while (rs.next()) {
                    produitIds.add(rs.getInt("id"));
                }
            }
            
            // 2. Ensuite, mettre à jour chaque produit individuellement
            for (Integer produitId : produitIds) {
                try {
                    boolean updated = mettreAJourQuantiteProduit(produitId);
                    if (!updated) {
                        LOGGER.warning("Échec de la mise à jour du stock pour le produit " + produitId);
                        success = false;
                    }
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "Erreur lors de la mise à jour du stock pour le produit " + produitId, e);
                    success = false;
                }
            }
            
            // 3. Journaliser le résultat
            if (success) {
                LOGGER.info("Actualisation de tous les stocks terminée avec succès - " + produitIds.size() + " produits traités");
                conn.commit();
            } else {
                LOGGER.warning("Actualisation de tous les stocks terminée avec des erreurs");
                conn.rollback();
            }
            
            return success;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur SQL lors de l'actualisation de tous les stocks: " + e.getMessage(), e);
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    LOGGER.log(Level.SEVERE, "Erreur lors du rollback de la transaction", ex);
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    LOGGER.log(Level.SEVERE, "Erreur lors de la fermeture de la connexion", e);
                }
            }
        }
    }
} 