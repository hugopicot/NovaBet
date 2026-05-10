package com.polymarket.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {
    private static final String CONFIG_FILE = "/config.properties";

    public static Connection getConnection() throws SQLException {
        Properties props = new Properties();
        try (InputStream in = DatabaseConnection.class.getResourceAsStream(CONFIG_FILE)) {
            if (in == null) {
                throw new SQLException("Le fichier " + CONFIG_FILE + " est introuvable !");
            }
            props.load(in);

            String url = props.getProperty("db.url");
            String user = props.getProperty("db.user");
            String pass = props.getProperty("db.password");

            Class.forName("com.mysql.cj.jdbc.Driver");

            return DriverManager.getConnection(url, user, pass);
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL driver not found", e);
        } catch (Exception e) {
            throw new SQLException("Erreur de lecture de la configuration : " + e.getMessage(), e);
        }
    }
}
