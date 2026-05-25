package com.polymarket.ui;

import com.polymarket.model.bets;
import com.polymarket.model.events;
import com.polymarket.model.outcomes;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class PortfolioView {

    private BorderPane root;
    private Runnable onMarketsClick;
    private Runnable onCreateMarketClick;
    private Consumer<Long> onMarketClick;
    private Long currentUserId;

    private final ObservableList<bets> betList = FXCollections.observableArrayList();

    private Label totalBetsLabel;
    private Label totalInvestedLabel;
    private Label potentialWinLabel;
    private GridPane betsGrid;
    private Label balanceValue;

    public PortfolioView() {
        root = new BorderPane();
        root.getStyleClass().add("main-container");
        root.setLeft(createSidebar());
        root.setCenter(createMainContent());
    }

    public void setBets(List<bets> bets, Map<Long, events> eventsMap, Map<Long, outcomes> outcomesMap) {
        betList.setAll(bets);
        refreshGrid(eventsMap, outcomesMap);
    }

    private void refreshGrid(Map<Long, events> eventsMap, Map<Long, outcomes> outcomesMap) {
        betsGrid.getChildren().clear();
        int count = betList.size();
        totalBetsLabel.setText(String.format("%,d", count));

        double totalInvested = 0;
        double totalPotential = 0;
        for (bets bet : betList) {
            totalInvested += bet.getAmount();
            totalPotential += bet.getPotential_win();
        }
        totalInvestedLabel.setText(String.format("%.2f", totalInvested));
        potentialWinLabel.setText(String.format("%.2f", totalPotential));

        for (int i = 0; i < betList.size(); i++) {
            bets bet = betList.get(i);
            events event = eventsMap.get((long) bet.getOutcome_id());
            outcomes outcome = outcomesMap.get((long) bet.getOutcome_id());
            int col = i % 2;
            int row = i / 2;
            betsGrid.add(createBetCard(bet, event, outcome), col, row);
        }
    }

    private VBox createBetCard(bets bet, events event, outcomes outcome) {
        VBox card = new VBox(12);
        card.getStyleClass().add("market-card");
        card.setPadding(new Insets(16, 16, 14, 16));
        card.setCursor(javafx.scene.Cursor.HAND);

        String marketTitle = event != null ? event.getTitle() : "Unknown Market";
        String outcomeLabel = outcome != null ? outcome.getLabel() : "?";
        boolean isYes = "YES".equalsIgnoreCase(outcomeLabel);

        HBox headerRow = new HBox(12);
        headerRow.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(4);
        Label marketLabel = new Label(marketTitle);
        marketLabel.getStyleClass().add("market-question");
        marketLabel.setFont(Font.font("Inter", FontWeight.MEDIUM, 14));
        marketLabel.setWrapText(true);

        HBox outcomeRow = new HBox(6);
        Label outcomeBadge = new Label(outcomeLabel);
        outcomeBadge.getStyleClass().add(isYes ? "holder-yes-badge" : "holder-no-badge");
        outcomeBadge.setFont(Font.font("Inter", FontWeight.BOLD, 11));
        Label betAmountLabel = new Label(String.format("Bet: %d $NVB", bet.getAmount()));
        betAmountLabel.getStyleClass().add("footer-label");
        betAmountLabel.setFont(Font.font("Inter", 11));
        outcomeRow.getChildren().addAll(outcomeBadge, betAmountLabel);

        titleBox.getChildren().addAll(marketLabel, outcomeRow);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        VBox winBox = new VBox(4);
        winBox.setAlignment(Pos.TOP_RIGHT);
        Label potentialLabel = new Label("Potential Win");
        potentialLabel.getStyleClass().add("footer-label");
        potentialLabel.setFont(Font.font("Inter", 10));
        Label winValue = new Label(String.format("%d $NVB", bet.getPotential_win()));
        winValue.getStyleClass().add("prob-value");
        winValue.setFont(Font.font("Inter", FontWeight.BOLD, 18));
        winBox.getChildren().addAll(potentialLabel, winValue);

        headerRow.getChildren().addAll(titleBox, spacer, winBox);

        HBox footerRow = new HBox(0);
        Label statusTag = new Label(event != null && event.getStatus() != null ? event.getStatus() : "OPEN");
        statusTag.getStyleClass().add("footer-label");
        statusTag.setFont(Font.font("Inter", 11));
        Label dot = new Label("\u00B7");
        dot.getStyleClass().add("footer-label");
        String resolution = event != null && event.getResolution() != null ? event.getResolution() : "";
        Label endsLabel = new Label(resolution.isEmpty() ? "" : "Ends " + resolution);
        endsLabel.getStyleClass().add("footer-label");
        endsLabel.setFont(Font.font("Inter", 11));

        Region footerSpacer = new Region();
        HBox.setHgrow(footerSpacer, Priority.ALWAYS);

        footerRow.getChildren().addAll(statusTag, dot, endsLabel, footerSpacer);

        card.getChildren().addAll(headerRow, footerRow);

        if (event != null && onMarketClick != null) {
            card.setOnMouseClicked(e -> onMarketClick.accept(event.getId()));
        }

        return card;
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
        HBox portfolioNav = createNavItem("Portfolio", true);
        portfolioNav.setCursor(javafx.scene.Cursor.HAND);
        HBox marketsNav = createNavItem("Markets", false);
        marketsNav.setCursor(javafx.scene.Cursor.HAND);
        marketsNav.setOnMouseClicked(e -> {
            if (onMarketsClick != null) onMarketsClick.run();
        });
        HBox createMarketNav = createNavItem("Create market", false);
        createMarketNav.setCursor(javafx.scene.Cursor.HAND);
        createMarketNav.setOnMouseClicked(e -> {
            if (onCreateMarketClick != null) onCreateMarketClick.run();
        });
        navItems.getChildren().addAll(
            marketsNav,
            portfolioNav,
            createMarketNav,
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
        balanceValue = new Label("0.00");
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
        mainContent.setCenter(createBetsContent());

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
        Label breadcrumbPortfolio = new Label("Portfolio");
        breadcrumbPortfolio.getStyleClass().add("breadcrumb-text-active");
        breadcrumbPortfolio.setFont(Font.font("Inter", FontWeight.MEDIUM, 14));
        breadcrumb.getChildren().addAll(breadcrumbMarkets, breadcrumbSlash, breadcrumbPortfolio);

        headerRow.getChildren().addAll(breadcrumb);

        topBar.getChildren().add(headerRow);
        return topBar;
    }

    private VBox createBetsContent() {
        VBox betsContent = new VBox(0);
        betsContent.setPadding(new Insets(0, 24, 24, 24));

        HBox statsRow = new HBox(24);
        statsRow.setAlignment(Pos.CENTER_LEFT);
        statsRow.setPadding(new Insets(0, 0, 16, 0));

        VBox totalBetsBox = new VBox(4);
        Label totalBetsTitle = new Label("Total Bets");
        totalBetsTitle.getStyleClass().add("footer-label");
        totalBetsTitle.setFont(Font.font("Inter", 12));
        totalBetsLabel = new Label("0");
        totalBetsLabel.getStyleClass().add("stats-count");
        totalBetsLabel.setFont(Font.font("Inter", FontWeight.BOLD, 20));
        totalBetsBox.getChildren().addAll(totalBetsTitle, totalBetsLabel);

        VBox totalInvestedBox = new VBox(4);
        Label totalInvestedTitle = new Label("Total Invested");
        totalInvestedTitle.getStyleClass().add("footer-label");
        totalInvestedTitle.setFont(Font.font("Inter", 12));
        totalInvestedLabel = new Label("0.00");
        totalInvestedLabel.getStyleClass().add("stats-count");
        totalInvestedLabel.setFont(Font.font("Inter", FontWeight.BOLD, 20));
        totalInvestedBox.getChildren().addAll(totalInvestedTitle, totalInvestedLabel);

        VBox potentialWinBox = new VBox(4);
        Label potentialWinTitle = new Label("Potential Win");
        potentialWinTitle.getStyleClass().add("footer-label");
        potentialWinTitle.setFont(Font.font("Inter", 12));
        potentialWinLabel = new Label("0.00");
        potentialWinLabel.getStyleClass().add("prob-value");
        potentialWinLabel.setFont(Font.font("Inter", FontWeight.BOLD, 20));
        potentialWinBox.getChildren().addAll(potentialWinTitle, potentialWinLabel);

        statsRow.getChildren().addAll(totalBetsBox, totalInvestedBox, potentialWinBox);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.getStyleClass().add("markets-scroll");
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        betsGrid = new GridPane();
        betsGrid.setHgap(16);
        betsGrid.setVgap(16);
        betsGrid.setPadding(new Insets(0, 0, 20, 0));

        scrollPane.setContent(betsGrid);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        betsContent.getChildren().addAll(statsRow, scrollPane);
        return betsContent;
    }

    public BorderPane getView() {
        return root;
    }

    public void setBalance(double balance) {
        if (balanceValue != null) {
            balanceValue.setText(String.format("%.2f", balance));
        }
    }

    public void setCurrentUserId(Long userId) {
        this.currentUserId = userId;
    }

    public void setOnMarketsClick(Runnable onMarketsClick) {
        this.onMarketsClick = onMarketsClick;
    }

    public void setOnCreateMarketClick(Runnable onCreateMarketClick) {
        this.onCreateMarketClick = onCreateMarketClick;
    }

    public void setOnMarketClick(Consumer<Long> onMarketClick) {
        this.onMarketClick = onMarketClick;
    }
}
