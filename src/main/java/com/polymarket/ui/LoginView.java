package com.polymarket.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class LoginView {

    private BorderPane root;
    private Runnable onRegister;
    private Runnable onLogin;

    private TextField emailField;
    private PasswordField passwordField;

    public LoginView() {
        root = new BorderPane();
        root.getStyleClass().add("main-container");
        root.setLeft(createSidebar());
        root.setCenter(createLoginForm());
    }

    private VBox createSidebar() {
        VBox sidebar = new VBox(30);
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPadding(new Insets(50, 40, 50, 40));
        sidebar.setPrefWidth(320);

        HBox logoBox = new HBox(12);
        logoBox.setAlignment(Pos.CENTER_LEFT);
        Region logoIcon = new Region();
        logoIcon.getStyleClass().add("logo-icon");
        logoIcon.setPrefSize(32, 32);
        Label logoText = new Label("NovaBet");
        logoText.getStyleClass().add("logo-text");
        logoText.setFont(Font.font("Inter", FontWeight.BOLD, 18));
        logoBox.getChildren().addAll(logoIcon, logoText);

        VBox taglineBox = new VBox(16);
        Label headline = new Label("Trade what\nothers won't.");
        headline.getStyleClass().add("sidebar-headline");
        Label subtitle = new Label("Predict the future on AI, sport, and the absurd. Or\nbet against the consensus.");
        subtitle.getStyleClass().add("sidebar-subtitle");
        taglineBox.getChildren().addAll(headline, subtitle);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        HBox statsBox = new HBox(20);
        statsBox.getStyleClass().add("stats-box");
        Label markets = new Label("1,247 markets");
        markets.getStyleClass().add("stats-text");
        Label volume = new Label("$48.2M today");
        volume.getStyleClass().add("stats-text");
        statsBox.getChildren().addAll(markets, volume);

        sidebar.getChildren().addAll(logoBox, taglineBox, spacer, statsBox);
        return sidebar;
    }

    private StackPane createLoginForm() {
        StackPane centerPane = new StackPane();
        centerPane.getStyleClass().add("center-pane");

        VBox formContainer = new VBox(32);
        formContainer.setAlignment(Pos.CENTER);
        formContainer.setMaxWidth(420);
        formContainer.setPadding(new Insets(60, 40, 60, 40));

        Label title = new Label("Welcome back");
        title.getStyleClass().add("form-title");
        title.setFont(Font.font("Inter", FontWeight.BOLD, 28));

        VBox formFields = new VBox(20);
        formFields.setPrefWidth(380);

        VBox emailBox = createInputField("Email", "you@example.com", false);
        emailField = (TextField) emailBox.getChildren().get(1);

        VBox passwordBox = createInputField("Password", "••••••••", true);
        passwordField = (PasswordField) passwordBox.getChildren().get(1);

        formFields.getChildren().addAll(emailBox, passwordBox);

        Button loginButton = new Button("Sign in");
        loginButton.getStyleClass().add("btn-primary");
        loginButton.setPrefWidth(380);
        loginButton.setPrefHeight(48);
        loginButton.setFont(Font.font("Inter", FontWeight.SEMI_BOLD, 15));
        loginButton.setOnAction(e -> {
            if (onLogin != null) onLogin.run();
        });

        HBox switchBox = new HBox(8);
        switchBox.setAlignment(Pos.CENTER);
        Label switchLabel = new Label("Don't have an account?");
        switchLabel.getStyleClass().add("switch-text");
        Button switchButton = new Button("Sign up");
        switchButton.getStyleClass().add("link-button");
        switchButton.setOnAction(e -> {
            if (onRegister != null) onRegister.run();
        });
        switchBox.getChildren().addAll(switchLabel, switchButton);

        formContainer.getChildren().addAll(title, formFields, loginButton, switchBox);
        centerPane.getChildren().add(formContainer);
        return centerPane;
    }

    private VBox createInputField(String labelText, String placeholder, boolean isPassword) {
        VBox container = new VBox(8);
        Label label = new Label(labelText);
        label.getStyleClass().add("input-label");
        label.setFont(Font.font("Inter", FontWeight.MEDIUM, 13));

        if (isPassword) {
            PasswordField field = new PasswordField();
            field.setPromptText(placeholder);
            field.getStyleClass().add("input-field");
            field.setPrefHeight(44);
            container.getChildren().addAll(label, field);
        } else {
            TextField field = new TextField();
            field.setPromptText(placeholder);
            field.getStyleClass().add("input-field");
            field.setPrefHeight(44);
            container.getChildren().addAll(label, field);
        }

        return container;
    }

    public BorderPane getView() {
        return root;
    }

    public void setOnRegister(Runnable onRegister) {
        this.onRegister = onRegister;
    }

    public void setOnLogin(Runnable onLogin) {
        this.onLogin = onLogin;
    }

    public String getEmail() {
        return emailField.getText();
    }

    public String getPassword() {
        return passwordField.getText();
    }
}
