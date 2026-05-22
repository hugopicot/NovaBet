package com.novabet.infrastructure.db;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseManager {

    private static final String CONFIG_FILE = "/config.properties";

    public static Connection getConnection() throws SQLException {
        Properties props = new Properties();
        try (InputStream in = DatabaseManager.class.getResourceAsStream(CONFIG_FILE)) {
            if (in == null) {
                throw new SQLException("Fichier de configuration " + CONFIG_FILE + " introuvable dans le classpath.");
            }
            props.load(in);
        } catch (java.io.IOException e) {
            throw new SQLException("Impossible de lire " + CONFIG_FILE, e);
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver MySQL introuvable (mysql-connector-j manquant ?)", e);
        }

        String url = props.getProperty("db.url");
        String user = props.getProperty("db.user");
        String pass = props.getProperty("db.password");

        return DriverManager.getConnection(url, user, pass);
    }
}
