package com.polymarket.ui.auth;

import com.polymarket.app.controllers.OnboardingController;
import com.polymarket.dao.usersDao;
import com.polymarket.model.users;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;

public class AuthModule {

    private final Stage primaryStage;
    private final String cssPath;
    private Runnable onLoginSuccess;

    private usersDao userDao;
    private users currentUser;

    private OnboardingController onboardingController;
    private Scene onboardingScene;

    public AuthModule(Stage primaryStage, String cssPath) {
        this.primaryStage = primaryStage;
        this.cssPath = cssPath;
        try {
            this.userDao = new usersDao();
        } catch (Exception e) {
            System.err.println("Failed to init auth DAOs: " + e.getMessage());
        }
    }

    public void setOnLoginSuccess(Runnable onLoginSuccess) {
        this.onLoginSuccess = onLoginSuccess;
    }

    public void start() {
        loadFonts();

        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/polymarket/views/OnboardingView.fxml")
            );
            Parent root = loader.load();
            onboardingController = loader.getController();

            onboardingController.setOnOnboardingComplete(() -> {
                long userId = onboardingController.getCurrentUserId();
                if (userId != -1) {
                    this.currentUser = userDao.findById(userId);
                    if (onLoginSuccess != null) onLoginSuccess.run();
                }
            });

            onboardingScene = new Scene(root, 1920, 1080);
            onboardingScene.getStylesheets().add(cssPath);
            primaryStage.setScene(onboardingScene);
        } catch (IOException e) {
            System.err.println("Failed to load onboarding: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public users getCurrentUser() {
        return currentUser;
    }

    public void showLogin() {
        if (onboardingController != null) {
            onboardingController.showLoginOnly();
        }
        if (onboardingScene != null) {
            primaryStage.setScene(onboardingScene);
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
                getClass().getResourceAsStream("/com/polymarket/fonts/InstrumentSerif-Regular.ttf"),
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
