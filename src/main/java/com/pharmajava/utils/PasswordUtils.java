package com.pharmajava.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Classe utilitaire pour gérer le hachage et la vérification des mots de passe
 */
public class PasswordUtils {
    
    private static final Logger LOGGER = Logger.getLogger(PasswordUtils.class.getName());
    private static final String HASH_ALGORITHM = "SHA-256";
    private static final int SALT_LENGTH = 16;
    private static final String SEPARATOR = ":";

    /**
     * Hashe un mot de passe en utilisant SHA-256 avec un sel aléatoire
     * 
     * @param password Le mot de passe à hasher
     * @return Le mot de passe hashé au format "sel:hash"
     */
    public static String hashPassword(String password) {
        try {
            // Générer un sel aléatoire
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[SALT_LENGTH];
            random.nextBytes(salt);
            
            // Hasher le mot de passe avec le sel
            MessageDigest md = MessageDigest.getInstance(HASH_ALGORITHM);
            md.update(salt);
            byte[] hashedPassword = md.digest(password.getBytes(StandardCharsets.UTF_8));
            
            // Encoder le sel et le hash en Base64
            String saltString = Base64.getEncoder().encodeToString(salt);
            String hashString = Base64.getEncoder().encodeToString(hashedPassword);
            
            // Combiner le sel et le hash
            return saltString + SEPARATOR + hashString;
        } catch (NoSuchAlgorithmException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors du hachage du mot de passe", e);
            return null;
        }
    }
    
    /**
     * Vérifie si un mot de passe correspond à un hash
     * 
     * @param password Le mot de passe à vérifier
     * @param storedHash Le hash stocké au format "sel:hash"
     * @return true si le mot de passe correspond, false sinon
     */
    public static boolean verifyPassword(String password, String storedHash) {
        if (storedHash == null || !storedHash.contains(SEPARATOR)) {
            LOGGER.log(Level.WARNING, "Format de hash invalide: " + (storedHash == null ? "null" : storedHash));
            return false;
        }
        
        try {
            // Extraire le sel et le hash
            String[] parts = storedHash.split(SEPARATOR);
            if (parts.length != 2) {
                LOGGER.log(Level.WARNING, "Format de hash invalide (mauvais nombre de parties): " + storedHash);
                return false;
            }
            
            String saltString = parts[0];
            String hashString = parts[1];
            
            // Décoder le sel
            byte[] salt;
            try {
                salt = Base64.getDecoder().decode(saltString);
            } catch (IllegalArgumentException e) {
                LOGGER.log(Level.WARNING, "Impossible de décoder le sel: " + saltString, e);
                return false;
            }
            
            // Hasher le mot de passe fourni avec le même sel
            MessageDigest md = MessageDigest.getInstance(HASH_ALGORITHM);
            md.update(salt);
            byte[] hashedPassword = md.digest(password.getBytes(StandardCharsets.UTF_8));
            
            // Encoder le hash en Base64
            String newHashString = Base64.getEncoder().encodeToString(hashedPassword);
            
            // Comparer les hash
            boolean result = hashString.equals(newHashString);
            if (!result) {
                LOGGER.log(Level.INFO, "Échec de vérification du mot de passe. Hash attendu: " + hashString + ", Hash calculé: " + newHashString);
            }
            return result;
        } catch (NoSuchAlgorithmException | IllegalArgumentException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la vérification du mot de passe", e);
            return false;
        }
    }
} 