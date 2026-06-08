package com.polymarket.service;


import com.polymarket.dao.usersDao;
import com.polymarket.dao.walletsDao;
import com.polymarket.model.users;
import com.polymarket.model.wallets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AuthService {
    private usersDao userDAO;
    private walletsDao walletDAO;
    private users currentUser;

    public AuthService() {
        try {
            this.userDAO = new usersDao();
            this.walletDAO = new walletsDao();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean register(String username, String email, String password) {
        if (userDAO.findByUsername(username) != null) {
            return false;
        }
        if (userDAO.findByEmail(email) != null) {
            return false;
        }

        String hashedPassword = hashPassword(password);
        String createdAt = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        users user = new users(username, email, hashedPassword, createdAt);
        userDAO.add(user);

        users createdUser = userDAO.findByUsername(username);
        if (createdUser != null) {
            wallets wallet = new wallets(createdUser.getId(), 0, 1000);
            walletDAO.add(wallet);
            return true;
        }
        return false;
    }

    public boolean login(String username, String password) {
        users user = userDAO.findByUsername(username);
        if (user != null && user.getPasswordHash().equals(hashPassword(password))) {
            this.currentUser = user;
            return true;
        }
        return false;
    }

    public void logout() {
        this.currentUser = null;
    }

    public users getCurrentUser() {
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