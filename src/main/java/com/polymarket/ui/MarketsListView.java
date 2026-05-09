package com.polymarket.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class MarketsListView {

    private BorderPane root;
    private Runnable onLogout;
    private Runnable onMarketClick;
    private Runnable onCreateMarket;

    public MarketsListView() {
        root = new BorderPane();
        root.getStyleClass().add("main-container");
        root.setLeft(createSidebar());
        root.setCenter(createMainContent());
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
        HBox createMarketNav = createNavItem("Create market", false);
        createMarketNav.setCursor(javafx.scene.Cursor.HAND);
        createMarketNav.setOnMouseClicked(e -> {
            if (onCreateMarket != null) onCreateMarket.run();
        });
        navItems.getChildren().addAll(
            createNavItem("Markets", true),
            createNavItem("Portfolio", false),
            createMarketNav,
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
        searchField.setPromptText("Search markets, e.g. \"GPT-5 release\"");
        searchField.getStyleClass().add("search-field");
        searchField.setFont(Font.font("Inter", 13));
        searchBox.getChildren().addAll(searchIcon, searchField);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox balanceBox = new HBox(8);
        balanceBox.setAlignment(Pos.CENTER);
        balanceBox.getStyleClass().add("header-balance-box");
        balanceBox.setPadding(new Insets(6, 14, 6, 14));
        Region balanceIcon = new Region();
        balanceIcon.getStyleClass().add("balance-icon");
        balanceIcon.setPrefSize(20, 20);
        Label balanceValue = new Label("12,480.50");
        balanceValue.getStyleClass().add("header-balance-value");
        balanceValue.setFont(Font.font("Inter", 14));
        Label balanceCurrency = new Label("$NVB");
        balanceCurrency.getStyleClass().add("header-balance-currency");
        balanceCurrency.setFont(Font.font("Inter", 12));
        balanceBox.getChildren().addAll(balanceIcon, balanceValue, balanceCurrency);

        Region notifBtn = new Region();
        notifBtn.getStyleClass().add("notification-btn");
        notifBtn.setPrefSize(36, 36);

        Button depositBtn = new Button("+ DEPOSIT");
        depositBtn.getStyleClass().add("deposit-btn");
        depositBtn.setFont(Font.font("Inter", FontWeight.BOLD, 13));

        headerRow.getChildren().addAll(marketsTitle, searchBox, spacer, balanceBox, notifBtn, depositBtn);

        HBox filterRow = new HBox(12);
        filterRow.setAlignment(Pos.CENTER_LEFT);
        filterRow.setPadding(new Insets(8, 0, 12, 0));

        Button allFilter = createFilterButton("All", true);
        Button techFilter = createFilterButton("Tech / AI", false);
        Button sportFilter = createFilterButton("Sport", false);
        Button absurdFilter = createFilterButton("Absurd", false);

        Region filterSpacer = new Region();
        HBox.setHgrow(filterSpacer, Priority.ALWAYS);

        Button trendingBtn = createSortButton("Trending ↓");
        Button volumeBtn = createSortButton("24h volume");

        filterRow.getChildren().addAll(allFilter, techFilter, sportFilter, absurdFilter, filterSpacer, trendingBtn, volumeBtn);

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

    private Button createSortButton(String text) {
        Button btn = new Button(text);
        btn.getStyleClass().add("sort-btn");
        btn.setFont(Font.font("Inter", 12));
        return btn;
    }

    private VBox createMarketsContent() {
        VBox content = new VBox(0);
        content.setPadding(new Insets(0, 24, 24, 24));

        HBox statsRow = new HBox(8);
        statsRow.setAlignment(Pos.CENTER_LEFT);
        statsRow.setPadding(new Insets(0, 0, 16, 0));

        Label marketsCount = new Label("1,247");
        marketsCount.getStyleClass().add("stats-count");
        marketsCount.setFont(Font.font("Inter", FontWeight.BOLD, 14));
        Label marketsText = new Label("markets");
        marketsText.getStyleClass().add("stats-text-muted");
        marketsText.setFont(Font.font("Inter", 13));
        Label dot1 = new Label("·");
        dot1.getStyleClass().add("stats-text-muted");
        Label volumeText = new Label("$48.2M");
        volumeText.getStyleClass().add("stats-count");
        volumeText.setFont(Font.font("Inter", FontWeight.BOLD, 14));
        Label volumeLabel = new Label("24h volume");
        volumeLabel.getStyleClass().add("stats-text-muted");
        volumeLabel.setFont(Font.font("Inter", 13));

        Region liveSpacer = new Region();
        HBox.setHgrow(liveSpacer, Priority.ALWAYS);

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

        statsRow.getChildren().addAll(marketsCount, marketsText, dot1, volumeText, volumeLabel, liveSpacer, liveBadge);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.getStyleClass().add("markets-scroll");
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        GridPane marketsGrid = new GridPane();
        marketsGrid.setHgap(16);
        marketsGrid.setVgap(16);
        marketsGrid.setPadding(new Insets(0, 0, 20, 0));

        String[][] markets = {
            {"🤖", "Will OpenAI release GPT-6 before December 2026?", "67%", "33%", "67¢", "33¢", "$4.2M", "1284", "Dec 31, 2026"},
            {"🚀", "Will SpaceX land humans on Mars before 2030?", "23%", "77%", "23¢", "77¢", "$8.9M", "3902", "Jan 1, 2030"},
            {"", "Will PSG win the Champions League 2026?", "18%", "82%", "18¢", "82¢", "$2.1M", "945", "Jun 1, 2026"},
            {"👽", "Will the Pentagon publicly confirm UAP recovery before 2027?", "12%", "88%", "12¢", "88¢", "$780K", "412", "Jan 1, 2027"},
            {"⚡", "Will Anthropic raise at >$200B valuation in 2026?", "71%", "29%", "71¢", "29¢", "$1.8M", "622", "Dec 31, 2026"},
            {"🏎️", "Will Verstappen win F1 World Championship 2026?", "52%", "48%", "52¢", "48¢", "$3.4M", "1547", "Nov 30, 2026"},
            {"🎬", "Will Christopher Nolan win Best Director at the Oscars 2027?", "38%", "62%", "38¢", "62¢", "$1.2M", "834", "Mar 15, 2027"},
            {"📱", "Will Apple ship a foldable iPhone in 2026?", "14%", "86%", "14¢", "86¢", "$2.5M", "1123", "Dec 31, 2026"}
        };

        for (int i = 0; i < markets.length; i++) {
            int col = i % 2;
            int row = i / 2;
            marketsGrid.add(createMarketCard(markets[i]), col, row);
        }

        scrollPane.setContent(marketsGrid);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        content.getChildren().addAll(statsRow, scrollPane);
        return content;
    }

    private VBox createMarketCard(String[] data) {
        VBox card = new VBox(12);
        card.getStyleClass().add("market-card");
        card.setPadding(new Insets(16, 16, 14, 16));
        card.setCursor(javafx.scene.Cursor.HAND);
        card.setOnMouseClicked(e -> {
            if (onMarketClick != null) onMarketClick.run();
        });

        HBox headerRow = new HBox(12);
        headerRow.setAlignment(Pos.CENTER_LEFT);
        Region iconBox = new Region();
        iconBox.getStyleClass().add("market-icon-box");
        iconBox.setPrefSize(40, 40);
        Label iconLabel = new Label(data[0]);
        iconLabel.setFont(Font.font(20));
        StackPane iconContainer = new StackPane(iconLabel);
        iconContainer.getStyleClass().add("market-icon-container");
        iconContainer.setPrefSize(40, 40);

        Label question = new Label(data[1]);
        question.getStyleClass().add("market-question");
        question.setFont(Font.font("Inter", FontWeight.MEDIUM, 14));
        question.setWrapText(true);

        headerRow.getChildren().addAll(iconContainer, question);

        double yesPercent = Double.parseDouble(data[2].replace("%", ""));
        double noPercent = Double.parseDouble(data[3].replace("%", ""));

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
        Label yesPercentLabel = new Label(data[2]);
        yesPercentLabel.getStyleClass().add("percent-yes");
        yesPercentLabel.setFont(Font.font("Inter", FontWeight.BOLD, 12));
        Region spacer1 = new Region();
        HBox.setHgrow(spacer1, Priority.ALWAYS);
        Label noPercentLabel = new Label(data[3]);
        noPercentLabel.getStyleClass().add("percent-no");
        noPercentLabel.setFont(Font.font("Inter", FontWeight.BOLD, 12));
        percentRow.getChildren().addAll(yesPercentLabel, spacer1, noPercentLabel);

        HBox buttonsRow = new HBox(8);
        Button yesBtn = new Button("Yes · " + data[4]);
        yesBtn.getStyleClass().add("btn-yes");
        yesBtn.setFont(Font.font("Inter", FontWeight.MEDIUM, 12));
        Button noBtn = new Button("No · " + data[5]);
        noBtn.getStyleClass().add("btn-no");
        noBtn.setFont(Font.font("Inter", FontWeight.MEDIUM, 12));
        buttonsRow.getChildren().addAll(yesBtn, noBtn);

        HBox footerRow = new HBox(0);
        Label volLabel = new Label("Vol " + data[6]);
        volLabel.getStyleClass().add("footer-label");
        volLabel.setFont(Font.font("Inter", 11));
        Label dot2 = new Label("·");
        dot2.getStyleClass().add("footer-label");
        Label tradersLabel = new Label(data[7] + " traders");
        tradersLabel.getStyleClass().add("footer-label");
        tradersLabel.setFont(Font.font("Inter", 11));

        Region footerSpacer = new Region();
        HBox.setHgrow(footerSpacer, Priority.ALWAYS);

        Label endsLabel = new Label("Ends " + data[8]);
        endsLabel.getStyleClass().add("footer-label");
        endsLabel.setFont(Font.font("Inter", 11));

        footerRow.getChildren().addAll(volLabel, dot2, tradersLabel, footerSpacer, endsLabel);

        card.getChildren().addAll(headerRow, percentRow, progressBar, buttonsRow, footerRow);
        return card;
    }

    public BorderPane getView() {
        return root;
    }

    public void setOnLogout(Runnable onLogout) {
        this.onLogout = onLogout;
    }

    public void setOnMarketClick(Runnable onMarketClick) {
        this.onMarketClick = onMarketClick;
    }

    public void setOnCreateMarket(Runnable onCreateMarket) {
        this.onCreateMarket = onCreateMarket;
    }
}
