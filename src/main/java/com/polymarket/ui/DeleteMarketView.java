package com.polymarket.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class DeleteMarketView {

    private BorderPane root;
    private Runnable onBack;
    private Runnable onConfirmDelete;

    public DeleteMarketView() {
        root = new BorderPane();
        root.getStyleClass().add("main-container");
        root.setLeft(createSidebar());
        root.setCenter(createDeleteContent());
    }

    private VBox createSidebar() {
        VBox sidebar = new VBox(0);
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPrefWidth(220);

        VBox topSection = new VBox(0);
        topSection.setPadding(new Insets(20, 16, 0, 16));

        HBox logoBox = new HBox(10);
        logoBox.setAlignment(Pos.CENTER_LEFT);
        logoBox.setPadding(new Insets(0, 0, 24, 0));
        Region logoIcon = new Region();
        logoIcon.getStyleClass().add("logo-icon");
        logoIcon.setPrefSize(28, 28);
        VBox logoTextContainer = new VBox(0);
        Label logoText = new Label("NovaBet");
        logoText.getStyleClass().add("logo-text");
        logoText.setFont(Font.font("Inter", FontWeight.BOLD, 16));
        Label logoVersion = new Label("v0.4.2 · alpha");
        logoVersion.getStyleClass().add("logo-version");
        logoVersion.setFont(Font.font("Inter", 10));
        logoTextContainer.getChildren().addAll(logoText, logoVersion);
        logoBox.getChildren().addAll(logoIcon, logoTextContainer);

        VBox navItems = new VBox(4);
        navItems.getChildren().addAll(
            createNavItem("Markets", false),
            createNavItem("Portfolio", false),
            createNavItem("Create market", false),
            createNavItem("History", false)
        );

        topSection.getChildren().addAll(logoBox, navItems);

        VBox categoriesSection = new VBox(8);
        categoriesSection.setPadding(new Insets(16, 16, 0, 16));
        Label categoriesTitle = new Label("CATEGORIES");
        categoriesTitle.getStyleClass().add("categories-title");
        categoriesTitle.setFont(Font.font("Inter", FontWeight.MEDIUM, 11));
        categoriesSection.getChildren().add(categoriesTitle);

        VBox categoryItems = new VBox(4);
        categoryItems.getChildren().addAll(
            createCategoryItem("Tech / AI", "248"),
            createCategoryItem("Sport", "412"),
            createCategoryItem("Absurd", "87")
        );
        categoriesSection.getChildren().add(categoryItems);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        VBox bottomSection = new VBox(12);
        bottomSection.setPadding(new Insets(0, 16, 20, 16));

        HBox casinoBox = new HBox(10);
        casinoBox.setAlignment(Pos.CENTER_LEFT);
        casinoBox.setPadding(new Insets(8, 0, 8, 0));
        Label casinoIcon = new Label("");
        casinoIcon.getStyleClass().add("casino-icon");
        Label casinoText = new Label("Casino");
        casinoText.getStyleClass().add("casino-text");
        Region hotBadge = new Region();
        hotBadge.getStyleClass().add("hot-badge");
        Region spacer2 = new Region();
        HBox.setHgrow(spacer2, Priority.ALWAYS);
        casinoBox.getChildren().addAll(casinoIcon, casinoText, spacer2, hotBadge);

        VBox balanceBox = new VBox(4);
        balanceBox.getStyleClass().add("balance-box");
        balanceBox.setPadding(new Insets(12, 14, 12, 14));
        Label balanceLabel = new Label("BALANCE");
        balanceLabel.getStyleClass().add("balance-label");
        balanceLabel.setFont(Font.font("Inter", 10));
        HBox balanceValueBox = new HBox(6);
        balanceValueBox.setAlignment(Pos.CENTER_LEFT);
        Label balanceValue = new Label("12,480.50");
        balanceValue.getStyleClass().add("balance-value");
        balanceValue.setFont(Font.font("Inter", FontWeight.BOLD, 18));
        Label balanceCurrency = new Label("$NVB");
        balanceCurrency.getStyleClass().add("balance-currency");
        balanceCurrency.setFont(Font.font("Inter", FontWeight.BOLD, 12));
        balanceValueBox.getChildren().addAll(balanceValue, balanceCurrency);
        Label balanceChange = new Label("+240 · 24h");
        balanceChange.getStyleClass().add("balance-change");
        balanceChange.setFont(Font.font("Inter", 11));
        balanceBox.getChildren().addAll(balanceLabel, balanceValueBox, balanceChange);

        bottomSection.getChildren().addAll(casinoBox, balanceBox);

        sidebar.getChildren().addAll(topSection, categoriesSection, spacer, bottomSection);
        return sidebar;
    }

    private HBox createNavItem(String text, boolean active) {
        HBox item = new HBox(10);
        item.setAlignment(Pos.CENTER_LEFT);
        item.setPadding(new Insets(8, 12, 8, 12));
        if (active) {
            item.getStyleClass().add("nav-item-active");
        } else {
            item.getStyleClass().add("nav-item");
        }

        Region icon = new Region();
        icon.setPrefSize(16, 16);
        if (active) {
            icon.getStyleClass().add("nav-icon-active");
        } else {
            icon.getStyleClass().add("nav-icon");
        }

        Label label = new Label(text);
        label.getStyleClass().add(active ? "nav-text-active" : "nav-text");
        label.setFont(Font.font("Inter", FontWeight.MEDIUM, 13));

        item.getChildren().addAll(icon, label);
        return item;
    }

    private HBox createCategoryItem(String text, String count) {
        HBox item = new HBox(10);
        item.setAlignment(Pos.CENTER_LEFT);
        item.setPadding(new Insets(6, 12, 6, 12));
        item.getStyleClass().add("category-item");

        Region icon = new Region();
        icon.setPrefSize(16, 16);
        icon.getStyleClass().add("category-icon");

        Label label = new Label(text);
        label.getStyleClass().add("category-text");
        label.setFont(Font.font("Inter", 13));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label countLabel = new Label(count);
        countLabel.getStyleClass().add("category-count");
        countLabel.setFont(Font.font("Inter", 12));

        item.getChildren().addAll(icon, label, spacer, countLabel);
        return item;
    }

    private VBox createDeleteContent() {
        VBox content = new VBox(0);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(32, 24, 24, 24));

        VBox dialogBox = new VBox(24);
        dialogBox.setAlignment(Pos.CENTER);
        dialogBox.setMaxWidth(440);
        dialogBox.getStyleClass().add("delete-dialog");
        dialogBox.setPadding(new Insets(32, 32, 28, 32));

        Region warningIcon = new Region();
        warningIcon.getStyleClass().add("warning-icon");
        warningIcon.setPrefSize(48, 48);

        Label title = new Label("Delete this market?");
        title.getStyleClass().add("delete-dialog-title");
        title.setFont(Font.font("Inter", FontWeight.BOLD, 20));

        Label subtitle = new Label("This action cannot be undone. All liquidity and positions will be permanently lost.");
        subtitle.getStyleClass().add("delete-dialog-subtitle");
        subtitle.setFont(Font.font("Inter", 13));
        subtitle.setWrapText(true);
        subtitle.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        VBox marketPreview = new VBox(10);
        marketPreview.getStyleClass().add("delete-market-preview");
        marketPreview.setPadding(new Insets(12, 14, 12, 14));

        Label previewQuestion = new Label("Will Anthropic release Claude 5 before September 2026?");
        previewQuestion.getStyleClass().add("delete-preview-question");
        previewQuestion.setFont(Font.font("Inter", FontWeight.MEDIUM, 13));

        HBox previewMeta = new HBox(12);
        Label metaCat = new Label("Tech / AI");
        metaCat.getStyleClass().add("delete-preview-meta");
        metaCat.setFont(Font.font("Inter", 11));
        Label metaDot = new Label("·");
        metaDot.getStyleClass().add("delete-preview-meta");
        Label metaDate = new Label("Sep 30, 2026");
        metaDate.getStyleClass().add("delete-preview-meta");
        metaDate.setFont(Font.font("Inter", 11));
        previewMeta.getChildren().addAll(metaCat, metaDot, metaDate);

        marketPreview.getChildren().addAll(previewQuestion, previewMeta);

        VBox confirmBox = new VBox(10);
        confirmBox.setAlignment(Pos.CENTER);

        Label confirmLabel = new Label("Type DELETE to confirm");
        confirmLabel.getStyleClass().add("delete-confirm-label");
        confirmLabel.setFont(Font.font("Inter", FontWeight.MEDIUM, 12));

        TextField confirmInput = new TextField();
        confirmInput.setPromptText("DELETE");
        confirmInput.getStyleClass().add("delete-confirm-input");
        confirmInput.setFont(Font.font("Inter", 14));
        confirmInput.setPrefWidth(280);
        confirmInput.setAlignment(Pos.CENTER);

        confirmBox.getChildren().addAll(confirmLabel, confirmInput);

        HBox buttonRow = new HBox(10);
        buttonRow.setAlignment(Pos.CENTER);

        Button cancelBtn = new Button("Cancel");
        cancelBtn.getStyleClass().add("btn-back");
        cancelBtn.setFont(Font.font("Inter", FontWeight.MEDIUM, 13));
        cancelBtn.setPrefWidth(120);
        cancelBtn.setOnAction(e -> {
            if (onBack != null) onBack.run();
        });

        Button deleteBtn = new Button("Delete market");
        deleteBtn.getStyleClass().add("btn-delete");
        deleteBtn.setFont(Font.font("Inter", FontWeight.BOLD, 13));
        deleteBtn.setPrefWidth(140);
        deleteBtn.setOnAction(e -> {
            if (onConfirmDelete != null) onConfirmDelete.run();
        });

        buttonRow.getChildren().addAll(cancelBtn, deleteBtn);

        dialogBox.getChildren().addAll(warningIcon, title, subtitle, marketPreview, confirmBox, buttonRow);
        content.getChildren().add(dialogBox);

        return content;
    }

    public BorderPane getView() {
        return root;
    }

    public void setOnBack(Runnable onBack) {
        this.onBack = onBack;
    }

    public void setOnConfirmDelete(Runnable onConfirmDelete) {
        this.onConfirmDelete = onConfirmDelete;
    }
}
