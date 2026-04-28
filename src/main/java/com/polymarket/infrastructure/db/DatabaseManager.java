package com.polymarket.infrastructure.db;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseManager {

    private static final String CONFIG_FILE = "/config.properties";

    public static Connection getConnection() {
        Properties props = new Properties();
        try (InputStream in = DatabaseManager.class.getResourceAsStream(CONFIG_FILE)) {
            if (in == null) {
                System.err.println("Le fichier " + CONFIG_FILE + " est introuvable !");
                return null;
            }
            props.load(in);

            String url = props.getProperty("db.url");
            String user = props.getProperty("db.user");
            String pass = props.getProperty("db.password");

            // Assurer le chargement du driver MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            return DriverManager.getConnection(url, user, pass);

        } catch (SQLException e) {
            System.err.println("Erreur de connexion à la base de données : " + e.getMessage());
            // Retourne null au lieu de lever une exception
            return null;
        } catch (Exception e) {
            System.err.println("Erreur de lecture de la configuration : " + e.getMessage());
            return null;
        }
    }
}
