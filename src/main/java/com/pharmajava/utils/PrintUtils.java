package com.pharmajava.utils;

import com.pharmajava.model.ProduitVendu;
import com.pharmajava.model.Vente;
import com.pharmajava.dao.PharmacieInfoDAO;
import com.pharmajava.model.PharmacieInfo;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.view.JasperViewer;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Classe utilitaire pour l'impression de tickets et reçus
 */
public class PrintUtils {
    private static final Logger LOGGER = Logger.getLogger(PrintUtils.class.getName());
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    // Appeler cette méthode au démarrage de l'application pour vérifier les
    // ressources
    static {
        initResources();
    }

    /**
     * Initialise les ressources nécessaires pour l'impression
     */
    public static void initResources() {
        try {
            // Vérifier si le dossier des rapports existe
            Path resourcesPath = Paths.get("src/main/resources");
            Path reportsPath = Paths.get("src/main/resources/reports");

            if (!Files.exists(resourcesPath)) {
                LOGGER.info("Création du dossier resources");
                Files.createDirectories(resourcesPath);
            }

            if (!Files.exists(reportsPath)) {
                LOGGER.info("Création du dossier reports");
                Files.createDirectories(reportsPath);
            }

            // Vérifier si le template existe
            Path ticketTemplatePath = Paths.get("src/main/resources/reports/ticket_vente.jrxml");
            if (!Files.exists(ticketTemplatePath)) {
                LOGGER.warning("Le template du ticket n'existe pas: " + ticketTemplatePath.toAbsolutePath());
            } else {
                LOGGER.info("Template de ticket trouvé: " + ticketTemplatePath.toAbsolutePath());
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de l'initialisation des ressources", e);
        }
    }

    /**
     * Imprime un ticket de vente
     * 
     * @param vente La vente pour laquelle imprimer le ticket
     * @return true si l'impression a réussi, false sinon
     */
    public static boolean imprimerTicketVente(Vente vente) {
        try {
            // Récupérer les informations de la pharmacie
            PharmacieInfoDAO pharmacieInfoDAO = new PharmacieInfoDAO();
            PharmacieInfo pharmacieInfo = pharmacieInfoDAO.obtenirInfoPharmacieActuelle();

            // Préparer les paramètres du rapport
            Map<String, Object> parameters = new HashMap<>();

            // Informations sur la pharmacie
            parameters.put("nomPharmacie", pharmacieInfo.getNom());
            parameters.put("adressePharmacie", pharmacieInfo.getAdresse());
            parameters.put("telephonePharmacie", pharmacieInfo.getTelephone1());

            // Informations sur la vente
            parameters.put("numeroTicket", vente.getId().toString());
            parameters.put("dateVente", vente.getDateVente().format(DATE_TIME_FORMATTER));
            parameters.put("pharmacien", vente.getPharmacien().getNomComplet());
            parameters.put("montantTotal", vente.getMontantTotal().toString() + " FCFA");
            parameters.put("montantPercu", vente.getMontantPercu().toString() + " FCFA");
            parameters.put("montantRendu", vente.getMontantRendu().toString() + " FCFA");

            // Préparer les données pour le détail des produits
            List<Map<String, Object>> produitsData = new ArrayList<>();
            for (ProduitVendu produit : vente.getProduitsVendus()) {
                Map<String, Object> item = new HashMap<>();
                item.put("nom", produit.getProduit().getNom());
                item.put("quantite", produit.getQuantite());
                item.put("prixUnitaire", produit.getPrixUnitaire().toString() + " FCFA");
                item.put("prixTotal", produit.getPrixTotal().toString() + " FCFA");
                produitsData.add(item);
            }

            LOGGER.info("Nombre de produits pour le ticket: " + produitsData.size());

            // Vérifier les chemins des ressources
            File reportsDir = new File("src/main/resources/reports");
            File templateFile = new File(reportsDir, "ticket_vente.jrxml");
            LOGGER.info("Recherche du template à: " + templateFile.getAbsolutePath());
            LOGGER.info("Le template existe: " + templateFile.exists());

            // Charger le template du ticket
            InputStream ticketTemplate = PrintUtils.class.getResourceAsStream("/reports/ticket_vente.jrxml");
            if (ticketTemplate == null) {
                LOGGER.severe("Le template du ticket est introuvable");
                // Essayer avec un chemin absolu
                ticketTemplate = Files.newInputStream(templateFile.toPath());
                if (ticketTemplate == null) {
                    LOGGER.severe("Impossible de charger le template même avec chemin absolu");
                    return false;
                }
            }

            // Compiler le rapport
            JasperReport jasperReport = JasperCompileManager.compileReport(ticketTemplate);

            // Créer la source de données pour les produits
            JRBeanCollectionDataSource produitsDS = new JRBeanCollectionDataSource(produitsData);

            // Remplir le rapport avec les données des produits
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, produitsDS);

            // Afficher l'aperçu du rapport
            JasperViewer.viewReport(jasperPrint, false);

            // Imprimer directement sur l'imprimante par défaut
            JasperPrintManager.printReport(jasperPrint, true);

            return true;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de l'impression du ticket de vente", e);
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Génère un ticket au format texte simple pour les imprimantes de tickets
     * thermiques
     * 
     * @param vente La vente pour laquelle générer le ticket
     * @return Le contenu du ticket au format texte
     */
    public static String genererTicketTexte(Vente vente) {
        try {
            // Récupérer les informations de la pharmacie
            PharmacieInfoDAO pharmacieInfoDAO = new PharmacieInfoDAO();
            PharmacieInfo pharmacieInfo = pharmacieInfoDAO.obtenirInfoPharmacieActuelle();

            StringBuilder ticket = new StringBuilder();

            // En-tête du ticket
            ticket.append(centrerTexte(pharmacieInfo.getNom(), 42)).append("\n");
            ticket.append(centrerTexte(pharmacieInfo.getAdresse(), 42)).append("\n");
            ticket.append(centrerTexte("Tél: " + pharmacieInfo.getTelephone1(), 42)).append("\n");
            ticket.append(separateur()).append("\n");

            // Informations sur la vente
            ticket.append("Ticket N°: ").append(vente.getId()).append("\n");
            ticket.append("Date: ").append(vente.getDateVente().format(DATE_TIME_FORMATTER)).append("\n");
            ticket.append("Pharmacien: ").append(vente.getPharmacien().getNomComplet()).append("\n");
            ticket.append(separateur()).append("\n");

            // En-tête des produits
            ticket.append(String.format("%-20s %5s %7s %7s", "Produit", "Qté", "P.U.", "Total")).append("\n");
            ticket.append(separateur()).append("\n");

            // Détail des produits
            for (ProduitVendu produit : vente.getProduitsVendus()) {
                String nomProduit = produit.getProduit().getNom();
                if (nomProduit.length() > 20) {
                    nomProduit = nomProduit.substring(0, 17) + "...";
                }

                ticket.append(String.format("%-20s %5d %7s %7s",
                        nomProduit,
                        produit.getQuantite(),
                        produit.getPrixUnitaire().toString(),
                        produit.getPrixTotal().toString())).append("\n");
            }

            ticket.append(separateur()).append("\n");

            // Totaux
            ticket.append(String.format("%33s %7s", "TOTAL:", vente.getMontantTotal().toString())).append("\n");
            ticket.append(String.format("%33s %7s", "PERÇU:", vente.getMontantPercu().toString())).append("\n");
            ticket.append(String.format("%33s %7s", "RENDU:", vente.getMontantRendu().toString())).append("\n");

            ticket.append(separateur()).append("\n");
            ticket.append(centrerTexte("Merci de votre achat!", 42)).append("\n");
            ticket.append(centrerTexte("À bientôt!", 42)).append("\n");

            return ticket.toString();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la génération du ticket texte", e);
            return "ERREUR: Impossible de générer le ticket";
        }
    }

    /**
     * Centre un texte dans une largeur donnée
     * 
     * @param texte   Le texte à centrer
     * @param largeur La largeur totale
     * @return Le texte centré
     */
    private static String centrerTexte(String texte, int largeur) {
        if (texte.length() >= largeur) {
            return texte;
        }

        int espaces = (largeur - texte.length()) / 2;
        return " ".repeat(espaces) + texte;
    }

    /**
     * Génère un séparateur pour le ticket
     * 
     * @return Une ligne de séparation
     */
    private static String separateur() {
        return "-".repeat(42);
    }
}