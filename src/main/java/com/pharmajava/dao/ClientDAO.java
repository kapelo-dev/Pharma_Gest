package com.pharmajava.dao;

import com.pharmajava.model.Client;
import com.pharmajava.utils.DatabaseConfig;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Classe d'accès aux données pour les clients
 */
public class ClientDAO {
    private static final Logger LOGGER = Logger.getLogger(ClientDAO.class.getName());

    /**
     * Ajoute un nouveau client
     * 
     * @param client Le client à ajouter
     * @return Le client avec son ID généré, ou null en cas d'erreur
     */
    public Client ajouter(Client client) {
        String sql = "INSERT INTO clients (nom, prenom, telephone, email, adresse) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, client.getNom());
            pstmt.setString(2, client.getPrenom());
            pstmt.setString(3, client.getTelephone());
            pstmt.setString(4, client.getEmail());
            pstmt.setString(5, client.getAdresse());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("La création du client a échoué, aucune ligne affectée.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    client.setId(generatedKeys.getInt(1));
                    return client;
                } else {
                    throw new SQLException("La création du client a échoué, aucun ID obtenu.");
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de l'ajout d'un client", e);
            return null;
        }
    }

    /**
     * Met à jour un client existant
     * 
     * @param client Le client à mettre à jour
     * @return true si la mise à jour a réussi, false sinon
     */
    public boolean mettreAJour(Client client) {
        String sql = "UPDATE clients SET nom = ?, prenom = ?, telephone = ?, email = ?, adresse = ? WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, client.getNom());
            pstmt.setString(2, client.getPrenom());
            pstmt.setString(3, client.getTelephone());
            pstmt.setString(4, client.getEmail());
            pstmt.setString(5, client.getAdresse());
            pstmt.setInt(6, client.getId());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la mise à jour d'un client", e);
            return false;
        }
    }

    /**
     * Supprime un client par son ID
     * 
     * @param id L'ID du client à supprimer
     * @return true si la suppression a réussi, false sinon
     */
    public boolean supprimer(int id) {
        String sql = "DELETE FROM clients WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la suppression d'un client", e);
            return false;
        }
    }

    /**
     * Récupère un client par son ID
     * 
     * @param id L'ID du client
     * @return Le client, ou null si non trouvé
     */
    public Client obtenirParId(int id) {
        String sql = "SELECT * FROM clients WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return convertirResultSet(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération d'un client par ID", e);
        }

        return null;
    }

    /**
     * Récupère tous les clients
     * 
     * @return Liste de tous les clients
     */
    public List<Client> obtenirTous() {
        List<Client> clients = new ArrayList<>();
        String sql = "SELECT * FROM clients ORDER BY nom, prenom";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                clients.add(convertirResultSet(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération de tous les clients", e);
        }

        return clients;
    }

    /**
     * Recherche des clients par nom, prénom ou téléphone
     * 
     * @param recherche La chaîne de recherche
     * @return Liste des clients correspondant à la recherche
     */
    public List<Client> rechercher(String recherche) {
        List<Client> clients = new ArrayList<>();

        // Si la recherche est vide, retourner une liste vide
        if (recherche == null || recherche.trim().isEmpty()) {
            return clients;
        }

        // Découper la recherche en termes pour permettre la recherche par prénom ET nom
        String[] termes = recherche.trim().split("\\s+");
        StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM clients WHERE ");

        // Construire la requête SQL avec OR entre chaque terme
        for (int i = 0; i < termes.length; i++) {
            if (i > 0) {
                sqlBuilder.append(" OR ");
            }
            sqlBuilder.append("(LOWER(nom) LIKE LOWER(?) OR LOWER(prenom) LIKE LOWER(?) OR telephone LIKE ?)");
        }

        sqlBuilder.append(" ORDER BY nom, prenom LIMIT 20"); // Limiter à 20 résultats pour des performances optimales

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sqlBuilder.toString())) {

            // Définir les paramètres pour chaque terme de recherche
            int paramIndex = 1;
            for (String terme : termes) {
                // Si le terme contient déjà un joker (%), l'utiliser tel quel
                // Sinon, ajouter les jokers avant et après (recherche contient)
                String termeRecherche = terme.contains("%") ? terme : "%" + terme + "%";

                pstmt.setString(paramIndex++, termeRecherche);
                pstmt.setString(paramIndex++, termeRecherche);
                pstmt.setString(paramIndex++, termeRecherche);
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    clients.add(convertirResultSet(rs));
                }
            }

            // Log pour débogage
            if (clients.isEmpty()) {
                LOGGER.info("Aucun client trouvé pour la recherche: " + recherche);
            } else {
                LOGGER.info("Nombre de clients trouvés pour '" + recherche + "': " + clients.size());
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la recherche de clients", e);
        }

        return clients;
    }

    /**
     * Convertit un ResultSet en objet Client
     * 
     * @param rs Le ResultSet à convertir
     * @return Un objet Client
     * @throws SQLException Si une erreur survient lors de l'accès aux données
     */
    private Client convertirResultSet(ResultSet rs) throws SQLException {
        return new Client(
                rs.getInt("id"),
                rs.getString("nom"),
                rs.getString("prenom"),
                rs.getString("telephone"),
                rs.getString("email"),
                rs.getString("adresse"),
                rs.getTimestamp("date_creation") != null ? rs.getTimestamp("date_creation").toLocalDateTime() : null,
                rs.getTimestamp("derniere_modification") != null
                        ? rs.getTimestamp("derniere_modification").toLocalDateTime()
                        : null);
    }
}