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

public class RegisterView {

    private BorderPane root;
    private Runnable onLogin;

    public RegisterView() {
        root = new BorderPane();
        root.getStyleClass().add("main-container");
        root.setLeft(createSidebar());
        root.setCenter(createRegisterForm());
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

    private StackPane createRegisterForm() {
        StackPane centerPane = new StackPane();
        centerPane.getStyleClass().add("center-pane");

        VBox formContainer = new VBox(28);
        formContainer.setAlignment(Pos.CENTER);
        formContainer.setMaxWidth(420);
        formContainer.setPadding(new Insets(50, 40, 50, 40));

        Label title = new Label("Create account");
        title.getStyleClass().add("form-title");
        title.setFont(Font.font("Inter", FontWeight.BOLD, 28));

        VBox formFields = new VBox(18);
        formFields.setPrefWidth(380);

        VBox nameBox = createInputField("Full Name", "John Doe", false);
        VBox emailBox = createInputField("Email", "you@example.com", false);
        VBox passwordBox = createInputField("Password", "••••••••", true);
        VBox confirmBox = createInputField("Confirm Password", "••••••••", true);

        formFields.getChildren().addAll(nameBox, emailBox, passwordBox, confirmBox);

        Button registerButton = new Button("Create account");
        registerButton.getStyleClass().add("btn-primary");
        registerButton.setPrefWidth(380);
        registerButton.setPrefHeight(48);
        registerButton.setFont(Font.font("Inter", FontWeight.SEMI_BOLD, 15));

        HBox switchBox = new HBox(8);
        switchBox.setAlignment(Pos.CENTER);
        Label switchLabel = new Label("Already have an account?");
        switchLabel.getStyleClass().add("switch-text");
        Button switchButton = new Button("Sign in");
        switchButton.getStyleClass().add("link-button");
        switchButton.setOnAction(e -> {
            if (onLogin != null) onLogin.run();
        });
        switchBox.getChildren().addAll(switchLabel, switchButton);

        formContainer.getChildren().addAll(title, formFields, registerButton, switchBox);
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

    public void setOnLogin(Runnable onLogin) {
        this.onLogin = onLogin;
    }
}
