package com.polymarket.ui;

import com.polymarket.model.events;

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
    private Runnable onMarketsClick;

    private Label previewQuestion;
    private Label previewMeta;
    private TextField confirmInput;

    private events currentEvent;

    public DeleteMarketView() {
        root = new BorderPane();
        root.getStyleClass().add("main-container");
        root.setLeft(createSidebar());
        root.setCenter(createDeleteContent());
    }

    public void setEventData(events event) {
        this.currentEvent = event;
        if (event != null) {
            previewQuestion.setText(event.getTitle() != null ? event.getTitle() : "");
            String resolution = event.getResolution() != null ? event.getResolution() : "";
            previewMeta.setText(resolution.isEmpty() ? "" : "Ends " + resolution);
        }
    }

    public events getEvent() {
        return currentEvent;
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
        Label logoVersion = new Label("v0.4.2 \u00B7 alpha");
        logoVersion.getStyleClass().add("logo-version");
        logoVersion.setFont(Font.font("Inter", 10));
        logoTextContainer.getChildren().addAll(logoText, logoVersion);
        logoBox.getChildren().addAll(logoIcon, logoTextContainer);

        VBox navItems = new VBox(4);
        HBox marketsNav = createNavItem("Markets", false);
        marketsNav.setCursor(javafx.scene.Cursor.HAND);
        marketsNav.setOnMouseClicked(e -> {
            if (onMarketsClick != null) onMarketsClick.run();
        });
        navItems.getChildren().addAll(
            marketsNav,
            createNavItem("Portfolio", false),
            createNavItem("Create market", false),
            createNavItem("History", false)
        );

        topSection.getChildren().addAll(logoBox, navItems);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        VBox bottomSection = new VBox(12);
        bottomSection.setPadding(new Insets(0, 16, 20, 16));

        VBox balanceBox = new VBox(4);
        balanceBox.getStyleClass().add("balance-box");
        balanceBox.setPadding(new Insets(12, 14, 12, 14));
        Label balanceLabel = new Label("BALANCE");
        balanceLabel.getStyleClass().add("balance-label");
        balanceLabel.setFont(Font.font("Inter", 10));
        HBox balanceValueBox = new HBox(6);
        balanceValueBox.setAlignment(Pos.CENTER_LEFT);
        Label balanceValue = new Label("0.00");
        balanceValue.getStyleClass().add("balance-value");
        balanceValue.setFont(Font.font("Inter", FontWeight.BOLD, 18));
        Label balanceCurrency = new Label("$NVB");
        balanceCurrency.getStyleClass().add("balance-currency");
        balanceCurrency.setFont(Font.font("Inter", FontWeight.BOLD, 12));
        balanceValueBox.getChildren().addAll(balanceValue, balanceCurrency);
        balanceBox.getChildren().addAll(balanceLabel, balanceValueBox);

        bottomSection.getChildren().addAll(balanceBox);

        sidebar.getChildren().addAll(topSection, spacer, bottomSection);
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

        previewQuestion = new Label("");
        previewQuestion.getStyleClass().add("delete-preview-question");
        previewQuestion.setFont(Font.font("Inter", FontWeight.MEDIUM, 13));

        previewMeta = new Label("");
        previewMeta.getStyleClass().add("delete-preview-meta");
        previewMeta.setFont(Font.font("Inter", 11));

        marketPreview.getChildren().addAll(previewQuestion, previewMeta);

        VBox confirmBox = new VBox(10);
        confirmBox.setAlignment(Pos.CENTER);

        Label confirmLabel = new Label("Type DELETE to confirm");
        confirmLabel.getStyleClass().add("delete-confirm-label");
        confirmLabel.setFont(Font.font("Inter", FontWeight.MEDIUM, 12));

        confirmInput = new TextField();
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
            if ("DELETE".equals(confirmInput.getText()) && onConfirmDelete != null) {
                onConfirmDelete.run();
            }
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

    public void setOnMarketsClick(Runnable onMarketsClick) {
        this.onMarketsClick = onMarketsClick;
    }

    public void setOnConfirmDelete(Runnable onConfirmDelete) {
        this.onConfirmDelete = onConfirmDelete;
    }
}