package com.pharmajava.dao;

import com.pharmajava.dao.StockDAO;
import com.pharmajava.model.Pharmacien;
import com.pharmajava.model.Produit;
import com.pharmajava.model.ProduitVendu;
import com.pharmajava.model.Vente;
import com.pharmajava.model.Client;
import com.pharmajava.utils.DatabaseConfig;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Classe d'accès aux données pour les ventes
 */
public class VenteDAO {
    private static final Logger LOGGER = Logger.getLogger(VenteDAO.class.getName());
    private final PharmacienDAO pharmacienDAO = new PharmacienDAO();
    private final ProduitDAO produitDAO = new ProduitDAO();
    private final ClientDAO clientDAO = new ClientDAO();

    /**
     * Ajoute une nouvelle vente avec ses produits vendus
     * 
     * @param vente La vente à ajouter
     * @return La vente avec son ID généré, ou null en cas d'erreur
     */
    public Vente ajouter(Vente vente) {
        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false); // Démarrer une transaction

            // Insérer la vente
            String sqlVente = "INSERT INTO ventes (date_vente, montant_total, montant_percu, montant_rendu, pharmacien_id, client_id) "
                    +
                    "VALUES (?, ?, ?, ?, ?, ?)";

            try (PreparedStatement pstmt = conn.prepareStatement(sqlVente, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setTimestamp(1, Timestamp.valueOf(vente.getDateVente()));
                pstmt.setBigDecimal(2, vente.getMontantTotal());
                pstmt.setBigDecimal(3, vente.getMontantPercu());
                pstmt.setBigDecimal(4, vente.getMontantRendu());

                // Si le pharmacien est défini, utiliser son ID
                if (vente.getPharmacien() != null && vente.getPharmacien().getId() != null) {
                    pstmt.setInt(5, vente.getPharmacien().getId());
                } else {
                    pstmt.setNull(5, Types.INTEGER);
                }

                // Si le client est défini, utiliser son ID (optionnel)
                if (vente.getClient() != null && vente.getClient().getId() != null) {
                    pstmt.setInt(6, vente.getClient().getId());
                } else {
                    pstmt.setNull(6, Types.INTEGER);
                }

                int affectedRows = pstmt.executeUpdate();

                if (affectedRows == 0) {
                    throw new SQLException("La création de la vente a échoué, aucune ligne affectée.");
                }

                // Récupérer l'ID généré pour la vente
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        vente.setId(generatedKeys.getInt(1));
                    } else {
                        throw new SQLException("La création de la vente a échoué, aucun ID obtenu.");
                    }
                }
            }

            // Insérer les produits vendus
            if (vente.getProduitsVendus() != null && !vente.getProduitsVendus().isEmpty()) {
                String sqlProduitVendu = "INSERT INTO produits_vendus (vente_id, produit_id, quantite, " +
                        "prix_unitaire, prix_total) VALUES (?, ?, ?, ?, ?)";

                // Créer une instance de StockDAO pour diminuer les stocks par lot
                StockDAO stockDAO = new StockDAO();

                try (PreparedStatement pstmt = conn.prepareStatement(sqlProduitVendu,
                        Statement.RETURN_GENERATED_KEYS)) {
                    for (ProduitVendu produitVendu : vente.getProduitsVendus()) {
                        pstmt.setInt(1, vente.getId());

                        // Si le produit est défini, utiliser son ID
                        if (produitVendu.getProduit() != null && produitVendu.getProduit().getId() != null) {
                            Produit produit = produitVendu.getProduit();
                            int produitId = produit.getId();
                            int quantite = produitVendu.getQuantite();

                            pstmt.setInt(2, produitId);

                            // Diminuer le stock par lot (utilisant la nouvelle méthode)
                            boolean stockMisAJour = stockDAO.diminuerStockParLot(produitId, quantite);
                            if (!stockMisAJour) {
                                throw new SQLException(
                                        "Erreur lors de la mise à jour du stock pour le produit: " + produit.getNom());
                            }
                        } else {
                            throw new SQLException("Produit non défini dans la vente");
                        }

                        pstmt.setInt(3, produitVendu.getQuantite());
                        pstmt.setBigDecimal(4, produitVendu.getPrixUnitaire());
                        pstmt.setBigDecimal(5, produitVendu.getPrixTotal());

                        pstmt.executeUpdate();

                        // Récupérer l'ID généré pour le produit vendu
                        try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                            if (generatedKeys.next()) {
                                produitVendu.setId(generatedKeys.getInt(1));
                                produitVendu.setVenteId(vente.getId());
                            }
                        }
                    }
                }
            }

            // Valider la transaction
            conn.commit();
            return vente;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Annuler la transaction en cas d'erreur
                } catch (SQLException ex) {
                    LOGGER.log(Level.SEVERE, "Erreur lors du rollback de la transaction", ex);
                }
            }
            LOGGER.log(Level.SEVERE, "Erreur lors de l'ajout d'une vente", e);
            return null;
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

    /**
     * Récupère une vente par son ID, avec tous ses produits vendus
     * 
     * @param id L'ID de la vente
     * @return La vente avec ses produits vendus, ou null si non trouvée
     */
    public Vente obtenirParId(int id) {
        String sqlVente = "SELECT * FROM ventes WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sqlVente)) {

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Vente vente = convertirResultSet(rs);

                    // Récupérer les produits vendus pour cette vente
                    vente.setProduitsVendus(obtenirProduitsVendus(vente.getId()));

                    return vente;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération d'une vente par ID", e);
        }

        return null;
    }

    /**
     * Récupère toutes les ventes, avec leurs produits vendus
     * 
     * @return Une liste de toutes les ventes
     */
    public List<Vente> obtenirToutes() {
        List<Vente> ventes = new ArrayList<>();
        String sql = "SELECT * FROM ventes ORDER BY date_vente DESC";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Vente vente = convertirResultSet(rs);

                // Récupérer les produits vendus pour cette vente
                vente.setProduitsVendus(obtenirProduitsVendus(vente.getId()));

                ventes.add(vente);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération de toutes les ventes", e);
        }

        return ventes;
    }

    /**
     * Récupère les ventes dans une période donnée
     * 
     * @param debut Date de début
     * @param fin   Date de fin
     * @return Liste des ventes dans la période
     */
    public List<Vente> obtenirParPeriode(LocalDateTime debut, LocalDateTime fin) {
        List<Vente> ventes = new ArrayList<>();
        String sql = "SELECT * FROM ventes WHERE date_vente BETWEEN ? AND ? ORDER BY date_vente DESC";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setTimestamp(1, Timestamp.valueOf(debut));
            pstmt.setTimestamp(2, Timestamp.valueOf(fin));

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Vente vente = convertirResultSet(rs);

                    // Récupérer les produits vendus pour cette vente
                    vente.setProduitsVendus(obtenirProduitsVendus(vente.getId()));

                    ventes.add(vente);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération des ventes par période", e);
        }

        return ventes;
    }

    /**
     * Récupère les ventes pour un client spécifique
     * 
     * @param clientId L'ID du client
     * @return Liste des ventes du client, ou une liste vide si aucune vente n'est
     *         trouvée
     */
    public List<Vente> obtenirParClient(int clientId) {
        List<Vente> ventes = new ArrayList<>();
        String sql = "SELECT * FROM ventes WHERE client_id = ? ORDER BY date_vente DESC";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, clientId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Vente vente = convertirResultSet(rs);

                    // Récupérer les produits vendus pour cette vente
                    vente.setProduitsVendus(obtenirProduitsVendus(vente.getId()));

                    ventes.add(vente);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération des ventes par client", e);
        }

        return ventes;
    }

    /**
     * Récupère les produits vendus pour une vente donnée
     * 
     * @param venteId L'ID de la vente
     * @return Liste des produits vendus pour cette vente
     */
    private List<ProduitVendu> obtenirProduitsVendus(int venteId) {
        List<ProduitVendu> produitsVendus = new ArrayList<>();
        String sql = "SELECT * FROM produits_vendus WHERE vente_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, venteId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ProduitVendu produitVendu = new ProduitVendu();

                    produitVendu.setId(rs.getInt("id"));
                    produitVendu.setVenteId(rs.getInt("vente_id"));

                    int produitId = rs.getInt("produit_id");
                    Produit produit = produitDAO.obtenirParId(produitId);
                    produitVendu.setProduit(produit);

                    produitVendu.setQuantite(rs.getInt("quantite"));
                    produitVendu.setPrixUnitaire(rs.getBigDecimal("prix_unitaire"));
                    produitVendu.setPrixTotal(rs.getBigDecimal("prix_total"));

                    produitsVendus.add(produitVendu);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération des produits vendus", e);
        }

        return produitsVendus;
    }

    /**
     * Convertit un ResultSet en objet Vente
     * 
     * @param rs Le ResultSet à convertir
     * @return Un objet Vente
     * @throws SQLException Si une erreur survient lors de l'accès aux données
     */
    private Vente convertirResultSet(ResultSet rs) throws SQLException {
        Integer id = rs.getInt("id");
        LocalDateTime dateVente = rs.getTimestamp("date_vente").toLocalDateTime();
        BigDecimal montantTotal = rs.getBigDecimal("montant_total");
        BigDecimal montantPercu = rs.getBigDecimal("montant_percu");
        BigDecimal montantRendu = rs.getBigDecimal("montant_rendu");

        // Récupérer le pharmacien
        Integer pharmacienId = rs.getInt("pharmacien_id");
        Pharmacien pharmacien = null;
        if (!rs.wasNull()) {
            pharmacien = pharmacienDAO.obtenirParId(pharmacienId);
        }

        // Récupérer le client (s'il existe)
        Integer clientId = rs.getInt("client_id");
        Client client = null;
        if (!rs.wasNull()) {
            client = clientDAO.obtenirParId(clientId);
        }

        return new Vente(id, dateVente, montantTotal, montantPercu, montantRendu, pharmacien, client);
    }
}