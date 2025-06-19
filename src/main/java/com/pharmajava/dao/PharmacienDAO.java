package com.pharmajava.dao;

import com.pharmajava.model.Pharmacien;
import com.pharmajava.utils.DatabaseConfig;
import com.pharmajava.utils.PasswordUtils;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Classe d'accès aux données pour les pharmaciens (utilisateurs)
 */
public class PharmacienDAO {
    private static final Logger LOGGER = Logger.getLogger(PharmacienDAO.class.getName());

    /**
     * Ajoute un nouveau pharmacien à la base de données
     * 
     * @param pharmacien Le pharmacien à ajouter
     * @return Le pharmacien avec son ID généré
     */
    public Pharmacien ajouter(Pharmacien pharmacien) {
        String sql = "INSERT INTO pharmaciens (nom, prenom, identifiant, telephone, " +
                     "mot_de_passe, role) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, pharmacien.getNom());
            pstmt.setString(2, pharmacien.getPrenom());
            pstmt.setString(3, pharmacien.getIdentifiant());
            pstmt.setString(4, pharmacien.getTelephone());
            
            // Stocker le mot de passe en clair
            pstmt.setString(5, pharmacien.getMotDePasse());
            
            pstmt.setString(6, pharmacien.getRole());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("La création du pharmacien a échoué, aucune ligne affectée.");
            }
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    pharmacien.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("La création du pharmacien a échoué, aucun ID obtenu.");
                }
            }
            
            return pharmacien;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de l'ajout d'un pharmacien", e);
            return null;
        }
    }
    
    /**
     * Met à jour un pharmacien existant
     * 
     * @param pharmacien Le pharmacien à mettre à jour
     * @return true si la mise à jour a réussi, false sinon
     */
    public boolean mettreAJour(Pharmacien pharmacien) {
        String sql = "UPDATE pharmaciens SET nom = ?, prenom = ?, identifiant = ?, " +
                     "telephone = ?, mot_de_passe = ?, role = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, pharmacien.getNom());
            pstmt.setString(2, pharmacien.getPrenom());
            pstmt.setString(3, pharmacien.getIdentifiant());
            pstmt.setString(4, pharmacien.getTelephone());
            pstmt.setString(5, pharmacien.getMotDePasse());
            pstmt.setString(6, pharmacien.getRole());
            pstmt.setInt(7, pharmacien.getId());
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la mise à jour d'un pharmacien", e);
            return false;
        }
    }
    
    /**
     * Met à jour le mot de passe d'un pharmacien
     * 
     * @param id L'ID du pharmacien
     * @param nouveauMotDePasse Le nouveau mot de passe
     * @return true si la mise à jour a réussi, false sinon
     */
    public boolean mettreAJourMotDePasse(int id, String nouveauMotDePasse) {
        String sql = "UPDATE pharmaciens SET mot_de_passe = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            // Stocker le mot de passe en clair
            pstmt.setString(1, nouveauMotDePasse);
            pstmt.setInt(2, id);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la mise à jour du mot de passe", e);
            return false;
        }
    }
    
    /**
     * Supprime un pharmacien par son ID
     * 
     * @param id L'ID du pharmacien à supprimer
     * @return true si la suppression a réussi, false sinon
     */
    public boolean supprimer(int id) {
        String sql = "DELETE FROM pharmaciens WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la suppression d'un pharmacien", e);
            return false;
        }
    }
    
    /**
     * Récupère un pharmacien par son ID
     * 
     * @param id L'ID du pharmacien à récupérer
     * @return Le pharmacien correspondant ou null s'il n'existe pas
     */
    public Pharmacien obtenirParId(int id) {
        String sql = "SELECT * FROM pharmaciens WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return convertirResultSet(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération d'un pharmacien par ID", e);
        }
        
        return null;
    }
    
    /**
     * Récupère un pharmacien par son identifiant de connexion
     * 
     * @param identifiant L'identifiant du pharmacien
     * @return Le pharmacien correspondant ou null s'il n'existe pas
     */
    public Pharmacien obtenirParIdentifiant(String identifiant) {
        String sql = "SELECT * FROM pharmaciens WHERE identifiant = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, identifiant);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return convertirResultSet(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération d'un pharmacien par identifiant", e);
        }
        
        return null;
    }
    
    /**
     * Récupère tous les pharmaciens de la base de données
     * 
     * @return Une liste de tous les pharmaciens
     */
    public List<Pharmacien> obtenirTous() {
        List<Pharmacien> pharmaciens = new ArrayList<>();
        String sql = "SELECT * FROM pharmaciens ORDER BY nom, prenom";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                pharmaciens.add(convertirResultSet(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération de tous les pharmaciens", e);
        }
        
        return pharmaciens;
    }
    
    /**
     * Authentifie un utilisateur avec son identifiant et mot de passe
     * 
     * @param identifiant L'identifiant de l'utilisateur
     * @param motDePasse Le mot de passe non hashé
     * @return Le pharmacien authentifié ou null si l'authentification échoue
     */
    public Pharmacien authentifier(String identifiant, String motDePasse) {
        LOGGER.log(Level.INFO, "Tentative d'authentification pour l'identifiant: " + identifiant);
        
        Pharmacien pharmacien = obtenirParIdentifiant(identifiant);
        
        if (pharmacien == null) {
            LOGGER.log(Level.INFO, "Aucun pharmacien trouvé avec l'identifiant: " + identifiant);
            return null;
        }
        
        LOGGER.log(Level.INFO, "Pharmacien trouvé: " + pharmacien.getNom() + " " + pharmacien.getPrenom());
        
        // Vérifier si le mot de passe est vide ou nul dans la base
        if (pharmacien.getMotDePasse() == null || pharmacien.getMotDePasse().isEmpty()) {
            LOGGER.log(Level.WARNING, "Le mot de passe stocké est vide pour l'identifiant: " + identifiant);
            return null;
        }
        
        // Vérifier si le mot de passe en clair correspond
        if (pharmacien.getMotDePasse().equals(motDePasse)) {
            LOGGER.log(Level.INFO, "Authentification réussie pour: " + identifiant);
            return pharmacien;
        }
        
        LOGGER.log(Level.INFO, "Échec d'authentification pour: " + identifiant + " (mot de passe incorrect)");
        return null;
    }
    
    /**
     * Convertit un ResultSet en objet Pharmacien
     * 
     * @param rs Le ResultSet à convertir
     * @return L'objet Pharmacien créé
     * @throws SQLException En cas d'erreur SQL
     */
    private Pharmacien convertirResultSet(ResultSet rs) throws SQLException {
        Pharmacien pharmacien = new Pharmacien();
        
        pharmacien.setId(rs.getInt("id"));
        pharmacien.setNom(rs.getString("nom"));
        pharmacien.setPrenom(rs.getString("prenom"));
        pharmacien.setIdentifiant(rs.getString("identifiant"));
        pharmacien.setTelephone(rs.getString("telephone"));
        pharmacien.setMotDePasse(rs.getString("mot_de_passe"));
        pharmacien.setRole(rs.getString("role"));
        
        Timestamp dateCreation = rs.getTimestamp("date_creation");
        if (dateCreation != null) {
            pharmacien.setDateCreation(dateCreation.toLocalDateTime());
        }
        
        Timestamp derniereModification = rs.getTimestamp("derniere_modification");
        if (derniereModification != null) {
            pharmacien.setDerniereModification(derniereModification.toLocalDateTime());
        }
        
        return pharmacien;
    }
} 