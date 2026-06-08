package com.polymarket.service;


import com.polymarket.model.events;
import com.polymarket.dao.usersDao;
import com.polymarket.dao.walletsDao;
import com.polymarket.model.users;
import com.polymarket.model.wallets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
public class AuthService {
    private usersDao userDAO;
    private walletsDao walletDAO;
    private users currentUser;

    public AuthService() {
        this.userDAO = new usersDao();
        this.walletDAO = new walletsDao();
    }

    public boolean register(String username, String email, String password) {
        try {
            // Vérifier si l'utilisateur existe déjà
            if (userDAO.findByUsername(username) != null) {
                return false;
            }
            if (userDAO.findByEmail(email) != null) {
                return false;
            }

            // Créer l'utilisateur
            User user = new User(username, email, hashPassword(password));
            long userId = userDAO.create(user);

            if (userId > 0) {
                // Créer le portefeuille avec bonus de départ
                Wallet wallet = new Wallet((int) userId);
                walletDAO.create(wallet);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean login(String username, String password) {
        try {
            User user = userDAO.findByUsername(username);
            if (user != null && user.getPasswordHash().equals(hashPassword(password))) {
                this.currentUser = user;
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void logout() {
        this.currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}