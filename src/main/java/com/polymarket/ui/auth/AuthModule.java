package com.polymarket.ui.auth;

import com.polymarket.ui.LoginView;
import com.polymarket.ui.RegisterView;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class AuthModule {

    private final Stage primaryStage;
    private final String cssPath;

    public AuthModule(Stage primaryStage, String cssPath) {
        this.primaryStage = primaryStage;
        this.cssPath = cssPath;
    }

    public void start() {
        loadFonts();

        LoginView loginView = new LoginView();
        RegisterView registerView = new RegisterView();

        Scene loginScene = new Scene(loginView.getView(), 1100, 700);
        Scene registerScene = new Scene(registerView.getView(), 1100, 700);

        loginScene.getStylesheets().add(cssPath);
        registerScene.getStylesheets().add(cssPath);

        loginView.setOnRegister(() -> primaryStage.setScene(registerScene));
        registerView.setOnLogin(() -> primaryStage.setScene(loginScene));

        primaryStage.setScene(loginScene);
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
