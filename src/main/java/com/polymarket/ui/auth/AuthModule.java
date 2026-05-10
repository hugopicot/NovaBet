package com.polymarket.ui.auth;

import com.polymarket.dao.usersDao;
import com.polymarket.dao.walletsDao;
import com.polymarket.model.users;
import com.polymarket.model.wallets;
import com.polymarket.ui.LoginView;
import com.polymarket.ui.RegisterView;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AuthModule {

    private final Stage primaryStage;
    private final String cssPath;
    private Runnable onLoginSuccess;

    private usersDao userDao;
    private walletsDao walletDao;

    private LoginView loginView;
    private RegisterView registerView;

    private Scene loginScene;
    private Scene registerScene;

    public AuthModule(Stage primaryStage, String cssPath) {
        this.primaryStage = primaryStage;
        this.cssPath = cssPath;
        try {
            this.userDao = new usersDao();
            this.walletDao = new walletsDao();
        } catch (SQLException e) {
            System.err.println("Failed to init auth DAOs: " + e.getMessage());
        }
    }

    public void setOnLoginSuccess(Runnable onLoginSuccess) {
        this.onLoginSuccess = onLoginSuccess;
    }

    public void start() {
        loadFonts();

        loginView = new LoginView();
        registerView = new RegisterView();

        loginScene = new Scene(loginView.getView(), 1100, 700);
        registerScene = new Scene(registerView.getView(), 1100, 700);

        loginScene.getStylesheets().add(cssPath);
        registerScene.getStylesheets().add(cssPath);

        loginView.setOnRegister(() -> primaryStage.setScene(registerScene));

        loginView.setOnLogin(() -> {
            String email = loginView.getEmail();
            String password = loginView.getPassword();
            if (authenticate(email, password)) {
                if (onLoginSuccess != null) onLoginSuccess.run();
            } else {
                showAlert(Alert.AlertType.ERROR, "Login failed", "Invalid email or password.");
            }
        });

        registerView.setOnLogin(() -> primaryStage.setScene(loginScene));

        registerView.setOnRegister(() -> {
            String name = registerView.getName();
            String email = registerView.getEmail();
            String password = registerView.getPassword();
            String confirmPassword = registerView.getConfirmPassword();

            if (name.isBlank() || email.isBlank() || password.isBlank()) {
                showAlert(Alert.AlertType.WARNING, "Missing fields", "Please fill in all fields.");
                return;
            }

            if (!email.contains("@") || !email.contains(".")) {
                showAlert(Alert.AlertType.WARNING, "Invalid email", "Please enter a valid email address.");
                return;
            }

            if (!password.equals(confirmPassword)) {
                showAlert(Alert.AlertType.WARNING, "Password mismatch", "The passwords do not match.");
                return;
            }

            if (password.length() < 6) {
                showAlert(Alert.AlertType.WARNING, "Weak password", "Password must be at least 6 characters.");
                return;
            }

            register(name, email, password);
        });

        primaryStage.setScene(loginScene);
    }

    private boolean authenticate(String email, String password) {
        if (userDao == null) return false;
        try {
            users user = userDao.findByEmail(email);
            if (user != null && BCrypt.checkpw(password, user.getPasswordHash())) {
                return true;
            }
        } catch (Exception e) {
            System.err.println("Auth error: " + e.getMessage());
        }
        return false;
    }

    private void register(String name, String email, String password) {
        if (userDao == null) return;
        try {
            users existing = userDao.findByEmail(email);
            if (existing != null) {
                showAlert(Alert.AlertType.WARNING, "Email taken", "An account with this email already exists.");
                return;
            }
            users newUser = new users();
            newUser.setUsername(name);
            newUser.setEmail(email);
            newUser.setPasswordHash(BCrypt.hashpw(password, BCrypt.gensalt()));
            newUser.setCreatedAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            userDao.add(newUser);

            users created = userDao.findByEmail(email);
            if (created != null) {
                wallets wallet = new wallets();
                wallet.setUserId(created.getId());
                wallet.setRealBalance(0.0);
                wallet.setVirtualBalance(10000.0);
                walletDao.add(wallet);
            }

            showAlert(Alert.AlertType.INFORMATION, "Account created", "Your account has been created successfully. You can now sign in.");
            primaryStage.setScene(loginScene);
        } catch (Exception e) {
            System.err.println("Register error: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Registration error", "An error occurred while creating your account. Please try again.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void loadFonts() {
        try {
            Font.loadFont(
                "https://cdn.jsdelivr.net/gh/google/fonts@main/ofl/instrumentserif/InstrumentSerif-Regular.ttf",
                12
            );
            Font.loadFont(
                "https://cdn.jsdelivr.net/gh/google/fonts@main/ofl/inter/Inter-VariableFont_opsz,wght.ttf",
                12
            );
        } catch (Exception e) {
            System.err.println("Failed to load fonts: " + e.getMessage());
        }
    }
}