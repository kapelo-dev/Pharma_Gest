package com.pharmajava.dao;

import com.pharmajava.model.Produit;
import com.pharmajava.utils.DatabaseConfig;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Classe d'accès aux données pour les produits
 */
public class ProduitDAO {
    private static final Logger LOGGER = Logger.getLogger(ProduitDAO.class.getName());

    /**
     * Ajoute un nouveau produit à la base de données
     * 
     * @param produit Le produit à ajouter
     * @return Le produit avec son ID généré
     */
    public Produit ajouter(Produit produit) {
        String sql = "INSERT INTO produits (nom, description, quantite_en_stock, " +
                     "prix_unitaire, sur_ordonnance, prix_achat, prix_vente, seuil_alerte) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, produit.getNom());
            pstmt.setString(2, produit.getDescription());
            pstmt.setInt(3, produit.getQuantiteEnStock());
            
            // Utiliser le prix de vente comme prix unitaire par défaut
            BigDecimal prixUnitaire = produit.getPrixVente() != null ? produit.getPrixVente() : BigDecimal.ZERO;
            pstmt.setBigDecimal(4, prixUnitaire);
            
            pstmt.setBoolean(5, produit.isSurOrdonnance());
            
            // Ajouter les nouveaux champs
            pstmt.setBigDecimal(6, produit.getPrixAchat() != null ? produit.getPrixAchat() : BigDecimal.ZERO);
            pstmt.setBigDecimal(7, produit.getPrixVente() != null ? produit.getPrixVente() : BigDecimal.ZERO);
            pstmt.setInt(8, produit.getSeuilAlerte());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("La création du produit a échoué, aucune ligne affectée.");
            }
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    produit.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("La création du produit a échoué, aucun ID obtenu.");
                }
            }
            
            return produit;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de l'ajout d'un produit", e);
            return null;
        }
    }
    
    /**
     * Met à jour un produit existant
     * 
     * @param produit Le produit à mettre à jour
     * @return true si la mise à jour a réussi, false sinon
     */
    public boolean metreAJour(Produit produit) {
        String sql = "UPDATE produits SET nom = ?, description = ?, quantite_en_stock = ?, " +
                     "prix_unitaire = ?, sur_ordonnance = ?, prix_achat = ?, prix_vente = ?, " +
                     "seuil_alerte = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, produit.getNom());
            pstmt.setString(2, produit.getDescription());
            pstmt.setInt(3, produit.getQuantiteEnStock());
            
            // Utiliser le prix de vente comme prix unitaire par défaut
            BigDecimal prixUnitaire = produit.getPrixVente() != null ? produit.getPrixVente() : BigDecimal.ZERO;
            pstmt.setBigDecimal(4, prixUnitaire);
            
            pstmt.setBoolean(5, produit.isSurOrdonnance());
            
            // Nouveaux champs
            pstmt.setBigDecimal(6, produit.getPrixAchat() != null ? produit.getPrixAchat() : BigDecimal.ZERO);
            pstmt.setBigDecimal(7, produit.getPrixVente() != null ? produit.getPrixVente() : BigDecimal.ZERO);
            pstmt.setInt(8, produit.getSeuilAlerte());
            
            pstmt.setInt(9, produit.getId());
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la mise à jour d'un produit", e);
            return false;
        }
    }
    
    /**
     * Supprime un produit par son ID
     * 
     * @param id L'ID du produit à supprimer
     * @return true si la suppression a réussi, false sinon
     */
    public boolean supprimer(int id) {
        String sql = "DELETE FROM produits WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la suppression d'un produit", e);
            return false;
        }
    }
    
    /**
     * Récupère un produit par son ID
     * 
     * @param id L'ID du produit à récupérer
     * @return Le produit correspondant ou null s'il n'existe pas
     */
    public Produit obtenirParId(int id) {
        String sql = "SELECT * FROM produits WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return convertirResultSet(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération d'un produit par ID", e);
        }
        
        return null;
    }
    
    /**
     * Récupère un produit par son ID (surcharge pour Integer)
     * 
     * @param id L'ID du produit à récupérer
     * @return Le produit correspondant ou null s'il n'existe pas
     */
    public Produit findById(Integer id) {
        if (id == null) {
            return null;
        }
        return obtenirParId(id.intValue());
    }
    
    /**
     * Récupère un produit par son nom
     * 
     * @param nom Le nom du produit à rechercher
     * @return Le produit correspondant ou null s'il n'existe pas
     */
    public Produit obtenirParNom(String nom) {
        String sql = "SELECT * FROM produits WHERE nom = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, nom);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return convertirResultSet(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération d'un produit par nom", e);
        }
        
        return null;
    }
    
    /**
     * Récupère tous les produits de la base de données
     * 
     * @return Une liste de tous les produits
     */
    public List<Produit> obtenirTous() {
        List<Produit> produits = new ArrayList<>();
        String sql = "SELECT * FROM produits ORDER BY nom";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                produits.add(convertirResultSet(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération de tous les produits", e);
        }
        
        return produits;
    }
    
    /**
     * Recherche des produits par mot-clé dans le nom ou la description
     * 
     * @param motCle Le mot-clé à rechercher
     * @return Une liste des produits correspondants
     */
    public List<Produit> rechercher(String motCle) {
        List<Produit> produits = new ArrayList<>();
        String sql = "SELECT * FROM produits WHERE nom LIKE ? OR description LIKE ? ORDER BY nom";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            String pattern = "%" + motCle + "%";
            pstmt.setString(1, pattern);
            pstmt.setString(2, pattern);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    produits.add(convertirResultSet(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la recherche de produits", e);
        }
        
        return produits;
    }
    
    /**
     * Met à jour la quantité en stock d'un produit
     * 
     * @param id L'ID du produit
     * @param nouvelleQuantite La nouvelle quantité en stock
     * @return true si la mise à jour a réussi, false sinon
     */
    public boolean mettreAJourStock(int id, int nouvelleQuantite) {
        String sql = "UPDATE produits SET quantite_en_stock = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, nouvelleQuantite);
            pstmt.setInt(2, id);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la mise à jour du stock d'un produit", e);
            return false;
        }
    }
    
    /**
     * Convertit un ResultSet en objet Produit
     * 
     * @param rs Le ResultSet à convertir
     * @return L'objet Produit créé
     * @throws SQLException En cas d'erreur SQL
     */
    private Produit convertirResultSet(ResultSet rs) throws SQLException {
        Produit produit = new Produit();
        
        produit.setId(rs.getInt("id"));
        produit.setNom(rs.getString("nom"));
        produit.setDescription(rs.getString("description"));
        produit.setQuantiteEnStock(rs.getInt("quantite_en_stock"));
        produit.setPrixUnitaire(rs.getBigDecimal("prix_unitaire"));
        produit.setSurOrdonnance(rs.getBoolean("sur_ordonnance"));
        
        // Traiter les nouveaux champs s'ils existent dans la BD
        try {
            BigDecimal prixAchat = rs.getBigDecimal("prix_achat");
            if (!rs.wasNull()) {
                produit.setPrixAchat(prixAchat);
            }
            
            BigDecimal prixVente = rs.getBigDecimal("prix_vente");
            if (!rs.wasNull()) {
                produit.setPrixVente(prixVente);
            }
            
            int seuilAlerte = rs.getInt("seuil_alerte");
            if (!rs.wasNull()) {
                produit.setSeuilAlerte(seuilAlerte);
            }
        } catch (SQLException e) {
            // Les colonnes n'existent peut-être pas dans la BD, on utilise les valeurs par défaut
            LOGGER.log(Level.WARNING, "Certaines colonnes de prix ou seuil n'existent pas dans la BD", e);
        }
        
        Timestamp dateCreation = rs.getTimestamp("date_creation");
        if (dateCreation != null) {
            produit.setDateCreation(dateCreation.toLocalDateTime());
        }
        
        Timestamp derniereModification = rs.getTimestamp("derniere_modification");
        if (derniereModification != null) {
            produit.setDerniereModification(derniereModification.toLocalDateTime());
        }
        
        return produit;
    }
} 