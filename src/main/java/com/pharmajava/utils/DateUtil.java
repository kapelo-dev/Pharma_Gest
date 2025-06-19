package com.pharmajava.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * Classe utilitaire pour faciliter la manipulation des dates
 */
public class DateUtil {
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    
    /**
     * Formater une date en chaîne de caractères (format: jj/mm/aaaa)
     * 
     * @param date La date à formater
     * @return La chaîne formatée
     */
    public static String formaterDate(LocalDate date) {
        if (date == null) {
            return "";
        }
        return DATE_FORMATTER.format(date);
    }
    
    /**
     * Formater une date et heure en chaîne de caractères (format: jj/mm/aaaa hh:mm)
     * 
     * @param dateTime La date et heure à formater
     * @return La chaîne formatée
     */
    public static String formaterDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        return DATE_TIME_FORMATTER.format(dateTime);
    }
    
    /**
     * Analyser une chaîne de caractères en date (format attendu: jj/mm/aaaa)
     * 
     * @param dateStr La chaîne à analyser
     * @return La date correspondante ou null si le format est invalide
     */
    public static LocalDate parseLocalDate(String dateStr) {
        try {
            return LocalDate.parse(dateStr, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            return null;
        }
    }
    
    /**
     * Analyser une chaîne de caractères en date et heure (format attendu: jj/mm/aaaa hh:mm)
     * 
     * @param dateTimeStr La chaîne à analyser
     * @return La date et heure correspondante ou null si le format est invalide
     */
    public static LocalDateTime parseLocalDateTime(String dateTimeStr) {
        try {
            return LocalDateTime.parse(dateTimeStr, DATE_TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            return null;
        }
    }
    
    /**
     * Convertit une Date en LocalDate
     * 
     * @param date La Date à convertir
     * @return La LocalDate correspondante
     */
    public static LocalDate convertirEnLocalDate(Date date) {
        if (date == null) {
            return null;
        }
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }
    
    /**
     * Convertit une LocalDate en Date
     * 
     * @param localDate La LocalDate à convertir
     * @return La Date correspondante
     */
    public static Date convertirEnDate(LocalDate localDate) {
        if (localDate == null) {
            return null;
        }
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
    
    /**
     * Calcule le nombre de jours entre aujourd'hui et une date d'expiration
     * 
     * @param dateExpiration La date d'expiration
     * @return Le nombre de jours jusqu'à expiration (négatif si déjà expiré)
     */
    public static long joursJusquaExpiration(LocalDate dateExpiration) {
        if (dateExpiration == null) {
            return 0;
        }
        return ChronoUnit.DAYS.between(LocalDate.now(), dateExpiration);
    }
    
    /**
     * Vérifier si une date est dans le passé
     * 
     * @param date La date à vérifier
     * @return true si la date est dans le passé, false sinon
     */
    public static boolean estDansLePasse(LocalDate date) {
        if (date == null) {
            return false;
        }
        return date.isBefore(LocalDate.now());
    }
    
    /**
     * Vérifier si une date est aujourd'hui
     * 
     * @param date La date à vérifier
     * @return true si la date est aujourd'hui, false sinon
     */
    public static boolean estAujourdhui(LocalDate date) {
        if (date == null) {
            return false;
        }
        return date.isEqual(LocalDate.now());
    }
    
    /**
     * Calculer le nombre de jours entre deux dates
     * 
     * @param debut La date de début
     * @param fin La date de fin
     * @return Le nombre de jours entre les deux dates
     */
    public static long nombreDeJoursEntre(LocalDate debut, LocalDate fin) {
        if (debut == null || fin == null) {
            return 0;
        }
        return java.time.temporal.ChronoUnit.DAYS.between(debut, fin);
    }
} 