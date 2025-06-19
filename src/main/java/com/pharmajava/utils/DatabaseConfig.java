package com.pharmajava.utils;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Configuration et gestion des connexions à la base de données
 */
public class DatabaseConfig {
    private static HikariDataSource dataSource;

    static {
        try {
            initDataSource();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Initialise le pool de connexions HikariCP
     */
    private static void initDataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://mysql-kapelo.alwaysdata.net:3306/kapelo_pharmacie_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC");
        config.setUsername("kapelo");
        config.setPassword("Juwela99237614");
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        
        
        // Configuration du pool
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setIdleTimeout(30000);
        config.setConnectionTimeout(30000);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        dataSource = new HikariDataSource(config);
    }

    /**
     * Obtient une connexion depuis le pool
     * 
     * @return une connexion à la base de données
     * @throws SQLException si une erreur de connexion survient
     */
    public static Connection getConnection() throws SQLException {
        if (dataSource == null) {
            initDataSource();
        }
        return dataSource.getConnection();
    }

    /**
     * Ferme le pool de connexions
     */
    public static void closeDataSource() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
} 