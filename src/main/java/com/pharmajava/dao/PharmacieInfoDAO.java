package com.pharmajava.dao;

import com.pharmajava.model.PharmacieInfo;
import com.pharmajava.utils.DatabaseConfig;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Classe d'accès aux données pour les informations de la pharmacie
 */
public class PharmacieInfoDAO {
    private static final Logger LOGGER = Logger.getLogger(PharmacieInfoDAO.class.getName());
    
    /**
     * Récupère les informations actuelles de la pharmacie
     * 
     * @return Les informations de la pharmacie
     */
    public PharmacieInfo obtenirInfoPharmacieActuelle() {
        String query = "SELECT * FROM pharmacie_info WHERE id = 1";
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            if (rs.next()) {
                return extractPharmacieInfoFromResultSet(rs);
            } else {
                // Si aucune information n'est trouvée, retourner des informations par défaut
                LOGGER.warning("Aucune information de pharmacie trouvée dans la base de données");
                return new PharmacieInfo("Ma Pharmacie", "Adresse non définie", "Téléphone non défini");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération des informations de la pharmacie", e);
            return new PharmacieInfo("Ma Pharmacie", "Adresse non définie", "Téléphone non défini");
        }
    }
    
    /**
     * Met à jour les informations de la pharmacie
     * 
     * @param pharmacieInfo Les nouvelles informations de la pharmacie
     * @return true si la mise à jour a réussi, false sinon
     */
    public boolean mettreAJour(PharmacieInfo pharmacieInfo) {
        String query = "UPDATE pharmacie_info SET nom = ?, adresse = ?, telephone1 = ?, " +
                      "telephone2 = ?, email = ?, logo_path = ? WHERE id = 1";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, pharmacieInfo.getNom());
            pstmt.setString(2, pharmacieInfo.getAdresse());
            pstmt.setString(3, pharmacieInfo.getTelephone1());
            pstmt.setString(4, pharmacieInfo.getTelephone2());
            pstmt.setString(5, pharmacieInfo.getEmail());
            pstmt.setString(6, pharmacieInfo.getLogoPath());
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected == 0) {
                // Si aucune ligne n'a été mise à jour, c'est peut-être parce que l'entrée n'existe pas encore
                return insererNouvelleInfoPharmacieInfo(pharmacieInfo);
            }
            
            return true;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la mise à jour des informations de la pharmacie", e);
            return false;
        }
    }
    
    /**
     * Insère de nouvelles informations de pharmacie (utilisé si aucune entrée n'existe)
     * 
     * @param pharmacieInfo Les informations de la pharmacie à insérer
     * @return true si l'insertion a réussi, false sinon
     */
    private boolean insererNouvelleInfoPharmacieInfo(PharmacieInfo pharmacieInfo) {
        String query = "INSERT INTO pharmacie_info (id, nom, adresse, telephone1, telephone2, email, logo_path) " +
                      "VALUES (1, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, pharmacieInfo.getNom());
            pstmt.setString(2, pharmacieInfo.getAdresse());
            pstmt.setString(3, pharmacieInfo.getTelephone1());
            pstmt.setString(4, pharmacieInfo.getTelephone2());
            pstmt.setString(5, pharmacieInfo.getEmail());
            pstmt.setString(6, pharmacieInfo.getLogoPath());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de l'insertion des informations de la pharmacie", e);
            return false;
        }
    }
    
    /**
     * Extrait les informations de la pharmacie depuis un ResultSet
     * 
     * @param rs Le ResultSet contenant les données
     * @return L'objet PharmacieInfo correspondant
     * @throws SQLException si une erreur SQL survient
     */
    private PharmacieInfo extractPharmacieInfoFromResultSet(ResultSet rs) throws SQLException {
        Integer id = rs.getInt("id");
        String nom = rs.getString("nom");
        String adresse = rs.getString("adresse");
        String telephone1 = rs.getString("telephone1");
        String telephone2 = rs.getString("telephone2");
        String email = rs.getString("email");
        String logoPath = rs.getString("logo_path");
        
        Timestamp timestamp = rs.getTimestamp("date_modification");
        LocalDateTime dateModification = timestamp != null ? timestamp.toLocalDateTime() : null;
        
        return new PharmacieInfo(id, nom, adresse, telephone1, telephone2, email, logoPath, dateModification);
    }
} 