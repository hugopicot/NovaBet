package com.polymarket.ui;

import com.polymarket.domain.dto.BetRequest;
import com.polymarket.domain.dto.OutcomeLabel;
import com.polymarket.model.events;
import com.polymarket.model.outcomes;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class MarketsListView {

    private BorderPane root;
    private Runnable onLogout;
    private Consumer<Long> onMarketClick;
    private Runnable onCreateMarket;
    private Runnable onPortfolioClick;
    private Consumer<BetRequest> onPlaceBet;
    private Long currentUserId;

    private final ObservableList<events> marketList = FXCollections.observableArrayList();

    private Label marketsCountLabel;
    private GridPane marketsGrid;
    private VBox marketsContent;
    private Label balanceValue;

    public MarketsListView() {
        root = new BorderPane();
        root.getStyleClass().add("main-container");
        root.setLeft(createSidebar());
        root.setCenter(createMainContent());
    }

    public void setMarkets(List<events> markets, java.util.Map<Long, List<outcomes>> outcomesMap) {
        marketList.setAll(markets);
        refreshGrid(outcomesMap);
    }

    private void refreshGrid(java.util.Map<Long, List<outcomes>> outcomesMap) {
        marketsGrid.getChildren().clear();
        int count = marketList.size();
        marketsCountLabel.setText(String.format("%,d", count));

        for (int i = 0; i < marketList.size(); i++) {
            events event = marketList.get(i);
            List<outcomes> outcomes = outcomesMap.getOrDefault(event.getId(), List.of());
            int col = i % 2;
            int row = i / 2;
            marketsGrid.add(createMarketCard(event, outcomes), col, row);
        }
    }

    private VBox createMarketCard(events event, List<outcomes> outcomes) {
        VBox card = new VBox(12);
        card.getStyleClass().add("market-card");
        card.setPadding(new Insets(16, 16, 14, 16));
        card.setCursor(javafx.scene.Cursor.HAND);
        card.setOnMouseClicked(e -> {
            if (onMarketClick != null) onMarketClick.accept(event.getId());
        });

        HBox headerRow = new HBox(12);
        headerRow.setAlignment(Pos.CENTER_LEFT);
        StackPane iconContainer = new StackPane();
        iconContainer.getStyleClass().add("market-icon-container");
        iconContainer.setPrefSize(40, 40);
        Label iconLabel = new Label(getCategoryIcon(event));
        iconLabel.setFont(Font.font(20));
        iconContainer.getChildren().add(iconLabel);

        Label question = new Label(event.getTitle());
        question.getStyleClass().add("market-question");
        question.setFont(Font.font("Inter", FontWeight.MEDIUM, 14));
        question.setWrapText(true);

        headerRow.getChildren().addAll(iconContainer, question);

        double yesProb = getYesProbability(outcomes);
        double noProb = 100.0 - yesProb;
        String yesStr = String.format("%.0f%%", yesProb);
        String noStr = String.format("%.0f%%", noProb);
        String yesPrice = String.format("%.0f\u00A2", yesProb);
        String noPrice = String.format("%.0f\u00A2", noProb);

        HBox progressBar = new HBox(0);
        progressBar.getStyleClass().add("progress-bar-bg");
        progressBar.setPrefHeight(4);

        Region yesBar = new Region();
        yesBar.getStyleClass().add("progress-bar-yes");
        yesBar.setPrefWidth(0);
        HBox.setHgrow(yesBar, Priority.ALWAYS);

        Region noBar = new Region();
        noBar.getStyleClass().add("progress-bar-no");
        noBar.setPrefWidth(0);
        HBox.setHgrow(noBar, Priority.ALWAYS);

        progressBar.getChildren().addAll(yesBar, noBar);

        HBox percentRow = new HBox(0);
        Label yesPercentLabel = new Label(yesStr);
        yesPercentLabel.getStyleClass().add("percent-yes");
        yesPercentLabel.setFont(Font.font("Inter", FontWeight.BOLD, 12));
        Region spacer1 = new Region();
        HBox.setHgrow(spacer1, Priority.ALWAYS);
        Label noPercentLabel = new Label(noStr);
        noPercentLabel.getStyleClass().add("percent-no");
        noPercentLabel.setFont(Font.font("Inter", FontWeight.BOLD, 12));
        percentRow.getChildren().addAll(yesPercentLabel, spacer1, noPercentLabel);

        HBox buttonsRow = new HBox(8);
        Button yesBtn = new Button("Yes \u00B7 " + yesPrice);
        yesBtn.getStyleClass().add("btn-yes");
        yesBtn.setFont(Font.font("Inter", FontWeight.MEDIUM, 12));
        yesBtn.setOnAction(e -> {
            e.consume();
            promptAndPlaceBet(event.getId(), OutcomeLabel.YES);
        });
        Button noBtn = new Button("No \u00B7 " + noPrice);
        noBtn.getStyleClass().add("btn-no");
        noBtn.setFont(Font.font("Inter", FontWeight.MEDIUM, 12));
        noBtn.setOnAction(e -> {
            e.consume();
            promptAndPlaceBet(event.getId(), OutcomeLabel.NO);
        });
        buttonsRow.getChildren().addAll(yesBtn, noBtn);

        HBox footerRow = new HBox(0);
        String statusLabel = event.getStatus() != null ? event.getStatus() : "OPEN";
        Label statusTag = new Label(statusLabel);
        statusTag.getStyleClass().add("footer-label");
        statusTag.setFont(Font.font("Inter", 11));
        Label dot2 = new Label("\u00B7");
        dot2.getStyleClass().add("footer-label");
        String endDate = event.getResolution() != null ? event.getResolution() : "";
        Label endsLabel = new Label(endDate.isEmpty() ? "" : "Ends " + endDate);
        endsLabel.getStyleClass().add("footer-label");
        endsLabel.setFont(Font.font("Inter", 11));

        Region footerSpacer = new Region();
        HBox.setHgrow(footerSpacer, Priority.ALWAYS);

        footerRow.getChildren().addAll(statusTag, dot2, endsLabel, footerSpacer);

        card.getChildren().addAll(headerRow, percentRow, progressBar, buttonsRow, footerRow);
        return card;
    }

    private String getCategoryIcon(events event) {
        String title = event.getTitle() != null ? event.getTitle().toLowerCase() : "";
        if (title.contains("ai") || title.contains("gpt") || title.contains("tech") || title.contains("openai") || title.contains("claude") || title.contains("anthropic")) {
            return "\u00A9";
        }
        if (title.contains("mars") || title.contains("spacex") || title.contains("rocket")) {
            return "\u00A9";
        }
        if (title.contains("champion") || title.contains("win") || title.contains("f1") || title.contains("psg") || title.contains("verspan")) {
            return "\u00A9";
        }
        return "\u00A9";
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
        HBox createMarketNav = createNavItem("Create market", false);
        createMarketNav.setCursor(javafx.scene.Cursor.HAND);
        createMarketNav.setOnMouseClicked(e -> {
            if (onCreateMarket != null) onCreateMarket.run();
        });
        HBox portfolioNav = createNavItem("Portfolio", false);
        portfolioNav.setCursor(javafx.scene.Cursor.HAND);
        portfolioNav.setOnMouseClicked(e -> {
            if (onPortfolioClick != null) onPortfolioClick.run();
        });
        navItems.getChildren().addAll(
            createNavItem("Markets", true),
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
        mainContent.setCenter(createMarketsContent());

        return mainContent;
    }

    private VBox createTopBar() {
        VBox topBar = new VBox(0);
        topBar.setPadding(new Insets(0, 24, 0, 24));

        HBox headerRow = new HBox(16);
        headerRow.setAlignment(Pos.CENTER_LEFT);
        headerRow.setPadding(new Insets(16, 0, 12, 0));

        Label marketsTitle = new Label("Markets");
        marketsTitle.getStyleClass().add("markets-title");
        marketsTitle.setFont(Font.font("Inter", FontWeight.BOLD, 16));

        HBox searchBox = new HBox(0);
        searchBox.getStyleClass().add("search-box");
        searchBox.setPrefWidth(320);
        searchBox.setPrefHeight(36);
        Region searchIcon = new Region();
        searchIcon.getStyleClass().add("search-icon");
        searchIcon.setPrefSize(16, 16);
        TextField searchField = new TextField();
        searchField.setPromptText("Search markets...");
        searchField.getStyleClass().add("search-field");
        searchField.setFont(Font.font("Inter", 13));
        searchBox.getChildren().addAll(searchIcon, searchField);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button createBtn = new Button("+ CREATE MARKET");
        createBtn.getStyleClass().add("deposit-btn");
        createBtn.setFont(Font.font("Inter", FontWeight.BOLD, 13));
        createBtn.setOnAction(e -> {
            if (onCreateMarket != null) onCreateMarket.run();
        });

        headerRow.getChildren().addAll(marketsTitle, searchBox, spacer, createBtn);

        HBox filterRow = new HBox(12);
        filterRow.setAlignment(Pos.CENTER_LEFT);
        filterRow.setPadding(new Insets(8, 0, 12, 0));

        Button allFilter = createFilterButton("All", true);
        Button techFilter = createFilterButton("Tech / AI", false);
        Button sportFilter = createFilterButton("Sport", false);
        Button absurdFilter = createFilterButton("Absurd", false);

        Region filterSpacer = new Region();
        HBox.setHgrow(filterSpacer, Priority.ALWAYS);

        filterRow.getChildren().addAll(allFilter, techFilter, sportFilter, absurdFilter, filterSpacer);

        topBar.getChildren().addAll(headerRow, filterRow);
        return topBar;
    }

    private Button createFilterButton(String text, boolean active) {
        Button btn = new Button(text);
        if (active) {
            btn.getStyleClass().add("filter-btn-active");
        } else {
            btn.getStyleClass().add("filter-btn");
        }
        btn.setFont(Font.font("Inter", FontWeight.MEDIUM, 13));
        return btn;
    }

    private VBox createMarketsContent() {
        marketsContent = new VBox(0);
        marketsContent.setPadding(new Insets(0, 24, 24, 24));

        HBox statsRow = new HBox(8);
        statsRow.setAlignment(Pos.CENTER_LEFT);
        statsRow.setPadding(new Insets(0, 0, 16, 0));

        marketsCountLabel = new Label("0");
        marketsCountLabel.getStyleClass().add("stats-count");
        marketsCountLabel.setFont(Font.font("Inter", FontWeight.BOLD, 14));
        Label marketsText = new Label("markets");
        marketsText.getStyleClass().add("stats-text-muted");
        marketsText.setFont(Font.font("Inter", 13));

        HBox liveBadge = new HBox(6);
        liveBadge.setAlignment(Pos.CENTER);
        liveBadge.getStyleClass().add("live-badge");
        Region liveDot = new Region();
        liveDot.getStyleClass().add("live-dot");
        liveDot.setPrefSize(6, 6);
        Label liveText = new Label("LIVE");
        liveText.getStyleClass().add("live-text");
        liveText.setFont(Font.font("Inter", FontWeight.BOLD, 10));
        liveBadge.getChildren().addAll(liveDot, liveText);

        Region liveSpacer = new Region();
        HBox.setHgrow(liveSpacer, Priority.ALWAYS);

        statsRow.getChildren().addAll(marketsCountLabel, marketsText, liveSpacer, liveBadge);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.getStyleClass().add("markets-scroll");
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        marketsGrid = new GridPane();
        marketsGrid.setHgap(16);
        marketsGrid.setVgap(16);
        marketsGrid.setPadding(new Insets(0, 0, 20, 0));

        scrollPane.setContent(marketsGrid);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        marketsContent.getChildren().addAll(statsRow, scrollPane);
        return marketsContent;
    }

    public BorderPane getView() {
        return root;
    }

    private void promptAndPlaceBet(long eventId, OutcomeLabel outcome) {
        if (currentUserId == null) return;
        TextInputDialog dialog = new TextInputDialog("10");
        dialog.setTitle("Place Bet");
        dialog.setHeaderText("Enter amount to bet on " + outcome);
        dialog.setContentText("Amount ($NVB):");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(amountStr -> {
            try {
                BigDecimal amount = new BigDecimal(amountStr.trim());
                if (amount.compareTo(BigDecimal.ZERO) > 0 && onPlaceBet != null) {
                    onPlaceBet.accept(new BetRequest(currentUserId, eventId, outcome, amount, false));
                }
            } catch (NumberFormatException | ArithmeticException ex) {
                System.err.println("Invalid bet amount: " + amountStr);
            }
        });
    }

    public void setBalance(double balance) {
        if (balanceValue != null) {
            balanceValue.setText(String.format("%.2f", balance));
        }
    }

    public void setCurrentUserId(Long userId) {
        this.currentUserId = userId;
    }

    public void setOnPlaceBet(Consumer<BetRequest> onPlaceBet) {
        this.onPlaceBet = onPlaceBet;
    }

    public void setOnLogout(Runnable onLogout) {
        this.onLogout = onLogout;
    }

    public void setOnMarketClick(Consumer<Long> onMarketClick) {
        this.onMarketClick = onMarketClick;
    }

    public void setOnCreateMarket(Runnable onCreateMarket) {
        this.onCreateMarket = onCreateMarket;
    }

    public void setOnPortfolioClick(Runnable onPortfolioClick) {
        this.onPortfolioClick = onPortfolioClick;
    }
}