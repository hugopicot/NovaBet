package com.polymarket.ui;

import com.polymarket.model.events;
import com.polymarket.model.outcomes;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Polyline;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.List;

public class MarketDetailView {

    private BorderPane root;
    private Runnable onBack;
    private Runnable onMarketsClick;
    private Runnable onEditMarket;
    private Runnable onDeleteMarket;

    private Label questionLabel;
    private Label volumeLabel;
    private Label resolutionLabel;
    private Label statusBadge;
    private Label probValueLabel;
    private Label probPercentLabel;
    private Label descriptionLabel;
    private Label yesToggleBtn;
    private Label noToggleBtn;

    public MarketDetailView() {
        root = new BorderPane();
        root.getStyleClass().add("main-container");
        root.setLeft(createSidebar());
        root.setCenter(createMainContent());
    }

    public void setEventData(events event, List<outcomes> outcomes) {
        if (event == null) return;

        questionLabel.setText(event.getTitle() != null ? event.getTitle() : "");

        String status = event.getStatus() != null ? event.getStatus() : "OPEN";
        volumeLabel.setText("Status: " + status);

        String resolution = event.getResolution() != null ? event.getResolution() : "";
        resolutionLabel.setText(resolution.isEmpty() ? "" : "Ends " + resolution);

        descriptionLabel.setText(event.getDescription() != null ? event.getDescription() : "");

        double yesProb = getYesProbability(outcomes);
        probValueLabel.setText(String.format("%.0f", yesProb));
        probPercentLabel.setText("%");

        double noProb = 100.0 - yesProb;
        String yesPrice = String.format("%.0f\u00A2", yesProb);
        String noPrice = String.format("%.0f\u00A2", noProb);

        if (statusBadge != null) {
            statusBadge.setText(status);
        }
    }

    private double getYesProbability(List<outcomes> outcomes) {
        for (outcomes o : outcomes) {
            if ("YES".equalsIgnoreCase(o.getLabel())) {
                return o.getOdds() * 100;
            }
        }
        if (!outcomes.isEmpty()) {
            return outcomes.get(0).getOdds() * 100;
        }
        return 50.0;
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
        HBox marketsNav = createNavItem("Markets", true);
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

    private BorderPane createMainContent() {
        BorderPane mainContent = new BorderPane();
        mainContent.getStyleClass().add("main-content");

        mainContent.setTop(createTopBar());
        mainContent.setCenter(createDetailContent());

        return mainContent;
    }

    private VBox createTopBar() {
        VBox topBar = new VBox(0);
        topBar.setPadding(new Insets(0, 24, 0, 24));

        HBox headerRow = new HBox(16);
        headerRow.setAlignment(Pos.CENTER_LEFT);
        headerRow.setPadding(new Insets(16, 0, 12, 0));

        HBox breadcrumb = new HBox(6);
        breadcrumb.setAlignment(Pos.CENTER_LEFT);
        Label breadcrumbMarkets = new Label("Markets");
        breadcrumbMarkets.getStyleClass().add("breadcrumb-text");
        breadcrumbMarkets.setFont(Font.font("Inter", FontWeight.MEDIUM, 14));
        breadcrumbMarkets.setCursor(javafx.scene.Cursor.HAND);
        breadcrumbMarkets.setOnMouseClicked(e -> {
            if (onMarketsClick != null) onMarketsClick.run();
        });
        Label breadcrumbSlash = new Label("/");
        breadcrumbSlash.getStyleClass().add("breadcrumb-separator");
        breadcrumbSlash.setFont(Font.font("Inter", 14));
        Label breadcrumbDetail = new Label("Detail");
        breadcrumbDetail.getStyleClass().add("breadcrumb-text-active");
        breadcrumbDetail.setFont(Font.font("Inter", FontWeight.MEDIUM, 14));
        breadcrumb.getChildren().addAll(breadcrumbMarkets, breadcrumbSlash, breadcrumbDetail);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button editBtn = new Button("Edit");
        editBtn.getStyleClass().add("action-btn-edit");
        editBtn.setFont(Font.font("Inter", FontWeight.MEDIUM, 12));
        editBtn.setOnAction(e -> {
            if (onEditMarket != null) onEditMarket.run();
        });

        Button deleteBtn = new Button("Delete");
        deleteBtn.getStyleClass().add("action-btn-delete");
        deleteBtn.setFont(Font.font("Inter", FontWeight.MEDIUM, 12));
        deleteBtn.setOnAction(e -> {
            if (onDeleteMarket != null) onDeleteMarket.run();
        });

        headerRow.getChildren().addAll(breadcrumb, spacer, editBtn, deleteBtn);

        topBar.getChildren().add(headerRow);
        return topBar;
    }

    private ScrollPane createDetailContent() {
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.getStyleClass().add("detail-scroll");
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        VBox content = new VBox(20);
        content.setPadding(new Insets(0, 24, 24, 24));

        content.getChildren().addAll(
            createMarketHeader(),
            createAboutSection()
        );

        scrollPane.setContent(content);
        return scrollPane;
    }

    private HBox createMarketHeader() {
        HBox header = new HBox(16);
        header.setAlignment(Pos.TOP_LEFT);

        StackPane iconContainer = new StackPane();
        iconContainer.getStyleClass().add("market-icon-container");
        iconContainer.setPrefSize(56, 56);
        Label iconLabel = new Label("\u00A9");
        iconLabel.setFont(Font.font(28));
        iconContainer.getChildren().add(iconLabel);

        VBox infoBox = new VBox(6);
        questionLabel = new Label("Select a market");
        questionLabel.getStyleClass().add("detail-question");
        questionLabel.setFont(Font.font("Inter", FontWeight.BOLD, 20));

        HBox metaRow = new HBox(16);
        metaRow.setAlignment(Pos.CENTER_LEFT);
        volumeLabel = new Label("Status: OPEN");
        volumeLabel.getStyleClass().add("meta-label");
        volumeLabel.setFont(Font.font("Inter", 13));
        resolutionLabel = new Label("");
        resolutionLabel.getStyleClass().add("meta-label");
        resolutionLabel.setFont(Font.font("Inter", 13));

        HBox liveBadge = new HBox(6);
        liveBadge.setAlignment(Pos.CENTER);
        liveBadge.getStyleClass().add("live-badge");
        Region liveDot = new Region();
        liveDot.getStyleClass().add("live-dot");
        liveDot.setPrefSize(6, 6);
        statusBadge = new Label("LIVE");
        statusBadge.getStyleClass().add("live-text");
        statusBadge.setFont(Font.font("Inter", FontWeight.BOLD, 10));
        liveBadge.getChildren().addAll(liveDot, statusBadge);

        metaRow.getChildren().addAll(volumeLabel, resolutionLabel, liveBadge);
        infoBox.getChildren().addAll(questionLabel, metaRow);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        VBox probabilityBox = new VBox(4);
        probabilityBox.setAlignment(Pos.TOP_RIGHT);
        Label probLabel = new Label("YES probability");
        probLabel.getStyleClass().add("prob-label");
        probLabel.setFont(Font.font("Inter", 12));
        probValueLabel = new Label("50");
        probValueLabel.getStyleClass().add("prob-value");
        probValueLabel.setFont(Font.font("Inter", FontWeight.BOLD, 42));
        probPercentLabel = new Label("%");
        probPercentLabel.getStyleClass().add("prob-percent");
        probPercentLabel.setFont(Font.font("Inter", FontWeight.BOLD, 20));
        HBox probRow = new HBox(2);
        probRow.setAlignment(Pos.TOP_RIGHT);
        probRow.getChildren().addAll(probValueLabel, probPercentLabel);
        probabilityBox.getChildren().addAll(probLabel, probRow);

        header.getChildren().addAll(iconContainer, infoBox, spacer, probabilityBox);
        return header;
    }

    private VBox createAboutSection() {
        VBox aboutBox = new VBox(12);
        aboutBox.getStyleClass().add("info-card");
        aboutBox.setPadding(new Insets(16, 16, 16, 16));

        Label title = new Label("ABOUT THIS MARKET");
        title.getStyleClass().add("info-card-title");
        title.setFont(Font.font("Inter", FontWeight.MEDIUM, 12));

        descriptionLabel = new Label("");
        descriptionLabel.getStyleClass().add("info-card-text");
        descriptionLabel.setFont(Font.font("Inter", 13));
        descriptionLabel.setWrapText(true);

        aboutBox.getChildren().addAll(title, descriptionLabel);
        return aboutBox;
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

    public void setOnEditMarket(Runnable onEditMarket) {
        this.onEditMarket = onEditMarket;
    }

    public void setOnDeleteMarket(Runnable onDeleteMarket) {
        this.onDeleteMarket = onDeleteMarket;
    }
}