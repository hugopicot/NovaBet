package com.polymarket.ui;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Polyline;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class MarketDetailView {

    private BorderPane root;
    private Runnable onBack;
    private Runnable onMarketsClick;
    private Runnable onEditMarket;
    private Runnable onDeleteMarket;

    public MarketDetailView() {
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

        headerRow.getChildren().addAll(breadcrumb, searchBox, spacer, balanceBox, notifBtn, depositBtn);

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
            createMiddleSection(),
            createBottomSection()
        );

        scrollPane.setContent(content);
        return scrollPane;
    }

    private VBox createMarketHeader() {
        VBox headerWrapper = new VBox(8);
        headerWrapper.setPadding(new Insets(8, 0, 0, 0));

        Label categoryLabel = new Label("Markets · Tech / AI");
        categoryLabel.getStyleClass().add("detail-category");
        categoryLabel.setFont(Font.font("Inter", 12));

        HBox header = new HBox(16);
        header.setAlignment(Pos.TOP_LEFT);

        StackPane iconContainer = new StackPane();
        iconContainer.getStyleClass().add("market-icon-container");
        iconContainer.setPrefSize(56, 56);
        Label iconLabel = new Label("🤖");
        iconLabel.setFont(Font.font(28));
        iconContainer.getChildren().add(iconLabel);

        VBox infoBox = new VBox(6);
        Label question = new Label("Will OpenAI release GPT-6 before December 2026?");
        question.getStyleClass().add("detail-question");
        question.setFont(Font.font("Inter", FontWeight.BOLD, 20));

        HBox metaRow = new HBox(16);
        metaRow.setAlignment(Pos.CENTER_LEFT);
        Label volumeLabel = new Label("Volume $4.21M");
        volumeLabel.getStyleClass().add("meta-label");
        volumeLabel.setFont(Font.font("Inter", 13));
        Label liquidityLabel = new Label("Liquidity $890K");
        liquidityLabel.getStyleClass().add("meta-label");
        liquidityLabel.setFont(Font.font("Inter", 13));
        Label endsLabel = new Label("Ends Dec 31, 2026");
        endsLabel.getStyleClass().add("meta-label");
        endsLabel.setFont(Font.font("Inter", 13));

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

        metaRow.getChildren().addAll(volumeLabel, liquidityLabel, endsLabel, liveBadge);
        infoBox.getChildren().addAll(question, metaRow);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        VBox probabilityBox = new VBox(4);
        probabilityBox.setAlignment(Pos.TOP_RIGHT);
        Label probLabel = new Label("YES probability");
        probLabel.getStyleClass().add("prob-label");
        probLabel.setFont(Font.font("Inter", 12));
        Label probValue = new Label("67");
        probValue.getStyleClass().add("prob-value");
        probValue.setFont(Font.font("Inter", FontWeight.BOLD, 42));
        Label probPercent = new Label("%");
        probPercent.getStyleClass().add("prob-percent");
        probPercent.setFont(Font.font("Inter", FontWeight.BOLD, 20));
        HBox probRow = new HBox(2);
        probRow.setAlignment(Pos.TOP_RIGHT);
        probRow.getChildren().addAll(probValue, probPercent);
        Label probChange = new Label("+4.2 · 24h");
        probChange.getStyleClass().add("prob-change");
        probChange.setFont(Font.font("Inter", 12));
        probabilityBox.getChildren().addAll(probLabel, probRow, probChange);

        header.getChildren().addAll(iconContainer, infoBox, spacer, probabilityBox, createActionButtons());
        headerWrapper.getChildren().addAll(categoryLabel, header);
        return headerWrapper;
    }

    private HBox createActionButtons() {
        HBox actionBox = new HBox(8);
        actionBox.setAlignment(Pos.CENTER);
        actionBox.setPadding(new Insets(0, 0, 0, 16));

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

        actionBox.getChildren().addAll(editBtn, deleteBtn);
        return actionBox;
    }

    private HBox createMiddleSection() {
        HBox middleSection = new HBox(16);

        VBox chartSection = createChartSection();
        chartSection.getStyleClass().add("left-column");
        HBox.setHgrow(chartSection, Priority.ALWAYS);

        VBox tradePanel = createTradePanel();
        tradePanel.getStyleClass().add("right-column");

        middleSection.getChildren().addAll(chartSection, tradePanel);
        return middleSection;
    }

    private VBox createChartSection() {
        VBox chartSection = new VBox(12);
        chartSection.getStyleClass().add("chart-section");
        chartSection.setPadding(new Insets(16, 16, 16, 16));

        HBox chartHeader = new HBox(0);
        Label chartTitle = new Label("Price history · YES");
        chartTitle.getStyleClass().add("chart-title");
        chartTitle.setFont(Font.font("Inter", FontWeight.MEDIUM, 13));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox timeButtons = new HBox(6);
        timeButtons.getChildren().addAll(
            createTimeButton("1H"),
            createTimeButton("24H"),
            createTimeButton("7D", true),
            createTimeButton("1M"),
            createTimeButton("ALL")
        );

        chartHeader.getChildren().addAll(chartTitle, spacer, timeButtons);

        StackPane chartArea = new StackPane();
        chartArea.getStyleClass().add("chart-area");
        chartArea.setPrefHeight(220);

        double[] points = {
            0, 200,
            50, 180,
            100, 190,
            150, 150,
            200, 160,
            250, 120,
            300, 130,
            350, 90,
            400, 100,
            450, 70,
            500, 80,
            550, 50,
            600, 60,
            650, 30,
            700, 20
        };

        Polygon fillPolygon = new Polygon();
        fillPolygon.getStyleClass().add("chart-fill");
        for (int i = 0; i < points.length; i += 2) {
            fillPolygon.getPoints().addAll(points[i], points[i + 1]);
        }
        fillPolygon.getPoints().addAll(points[points.length - 2], 220.0);
        fillPolygon.getPoints().addAll(points[0], 220.0);

        Polyline chartLine = createChartLine();
        chartArea.getChildren().addAll(fillPolygon, chartLine);

        chartSection.getChildren().addAll(chartHeader, chartArea);
        return chartSection;
    }

    private Polyline createChartLine() {
        Polyline line = new Polyline();
        line.getStyleClass().add("chart-polyline");

        double[] points = {
            0, 200,
            50, 180,
            100, 190,
            150, 150,
            200, 160,
            250, 120,
            300, 130,
            350, 90,
            400, 100,
            450, 70,
            500, 80,
            550, 50,
            600, 60,
            650, 30,
            700, 20
        };

        for (double p : points) {
            line.getPoints().add(p);
        }
        return line;
    }

    private Button createTimeButton(String text) {
        return createTimeButton(text, false);
    }

    private Button createTimeButton(String text, boolean active) {
        Button btn = new Button(text);
        if (active) {
            btn.getStyleClass().add("time-btn-active");
        } else {
            btn.getStyleClass().add("time-btn");
        }
        btn.setFont(Font.font("Inter", 11));
        return btn;
    }

    private HBox createBottomSection() {
        HBox bottomSection = new HBox(16);

        HBox leftBottom = new HBox(16);
        leftBottom.getStyleClass().add("left-column");
        HBox.setHgrow(leftBottom, Priority.ALWAYS);

        VBox aboutSection = createAboutSection();
        HBox.setHgrow(aboutSection, Priority.ALWAYS);

        VBox topHoldersSection = createTopHoldersSection();
        HBox.setHgrow(topHoldersSection, Priority.ALWAYS);

        leftBottom.getChildren().addAll(aboutSection, topHoldersSection);

        VBox orderBook = createOrderBook();
        orderBook.getStyleClass().add("right-column");

        bottomSection.getChildren().addAll(leftBottom, orderBook);
        return bottomSection;
    }

    private VBox createAboutSection() {
        VBox aboutBox = new VBox(12);
        aboutBox.getStyleClass().add("info-card");
        aboutBox.setPadding(new Insets(16, 16, 16, 16));

        Label title = new Label("ABOUT THIS MARKET");
        title.getStyleClass().add("info-card-title");
        title.setFont(Font.font("Inter", FontWeight.MEDIUM, 12));

        Label description = new Label("Resolves YES if OpenAI publicly releases a model branded as GPT-6 (general-availability or paid tier) before 2026-12-31 23:59 UTC. Resolves based on official OpenAI announcement.");
        description.getStyleClass().add("info-card-text");
        description.setFont(Font.font("Inter", 13));
        description.setWrapText(true);

        aboutBox.getChildren().addAll(title, description);
        return aboutBox;
    }

    private VBox createTopHoldersSection() {
        VBox holdersBox = new VBox(12);
        holdersBox.getStyleClass().add("info-card");
        holdersBox.setPadding(new Insets(16, 16, 16, 16));

        Label title = new Label("TOP HOLDERS");
        title.getStyleClass().add("info-card-title");
        title.setFont(Font.font("Inter", FontWeight.MEDIUM, 12));

        VBox holdersList = new VBox(10);
        holdersList.getChildren().addAll(
            createHolderRow("0xf3a...21c", "YES", "$142K"),
            createHolderRow("0x88e...092", "YES", "$98K"),
            createHolderRow("whale.eth", "NO", "$67K")
        );

        holdersBox.getChildren().addAll(title, holdersList);
        return holdersBox;
    }

    private HBox createHolderRow(String address, String position, String amount) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);

        Region avatar = new Region();
        avatar.getStyleClass().add("holder-avatar");
        avatar.setPrefSize(24, 24);

        Label addrLabel = new Label(address);
        addrLabel.getStyleClass().add("holder-address");
        addrLabel.setFont(Font.font("Inter", 13));

        Label posLabel = new Label(position);
        if (position.equals("YES")) {
            posLabel.getStyleClass().add("holder-yes-badge");
        } else {
            posLabel.getStyleClass().add("holder-no-badge");
        }
        posLabel.setFont(Font.font("Inter", FontWeight.BOLD, 10));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label amountLabel = new Label(amount);
        amountLabel.getStyleClass().add("holder-amount");
        amountLabel.setFont(Font.font("Inter", 13));

        row.getChildren().addAll(avatar, addrLabel, posLabel, spacer, amountLabel);
        return row;
    }

    private VBox createTradePanel() {
        VBox panel = new VBox(14);
        panel.getStyleClass().add("trade-panel");
        panel.setPadding(new Insets(16, 16, 16, 16));

        HBox yesNoToggle = new HBox(0);
        yesNoToggle.getStyleClass().add("yes-no-toggle");
        Button yesBtn = new Button("Yes 67¢");
        yesBtn.getStyleClass().add("yes-toggle-active");
        yesBtn.setFont(Font.font("Inter", FontWeight.MEDIUM, 13));
        Button noBtn = new Button("No 33¢");
        noBtn.getStyleClass().add("no-toggle");
        noBtn.setFont(Font.font("Inter", FontWeight.MEDIUM, 13));
        yesNoToggle.getChildren().addAll(yesBtn, noBtn);

        HBox buySellRow = new HBox(8);
        buySellRow.setAlignment(Pos.CENTER_LEFT);
        Button buyToggle = new Button("Buy");
        buyToggle.getStyleClass().add("buy-sell-active");
        buyToggle.setFont(Font.font("Inter", FontWeight.MEDIUM, 12));
        Button sellToggle = new Button("Sell");
        sellToggle.getStyleClass().add("buy-sell-toggle");
        sellToggle.setFont(Font.font("Inter", 12));
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Button limitBtn = new Button("Limit");
        limitBtn.getStyleClass().add("limit-btn");
        limitBtn.setFont(Font.font("Inter", 11));
        buySellRow.getChildren().addAll(buyToggle, sellToggle, spacer, limitBtn);

        Label amountLabel = new Label("AMOUNT ($PMC)");
        amountLabel.getStyleClass().add("amount-label");
        amountLabel.setFont(Font.font("Inter", 11));

        HBox amountInputBox = new HBox(0);
        amountInputBox.getStyleClass().add("amount-input-box");
        TextField amountField = new TextField("500.00");
        amountField.getStyleClass().add("amount-field");
        amountField.setFont(Font.font("Inter", 14));
        Label currencyLabel = new Label("$PMC");
        currencyLabel.getStyleClass().add("currency-label");
        currencyLabel.setFont(Font.font("Inter", 12));
        amountInputBox.getChildren().addAll(amountField, currencyLabel);

        HBox quickAmounts = new HBox(6);
        quickAmounts.getChildren().addAll(
            createQuickAmount("$25"),
            createQuickAmount("$100"),
            createQuickAmount("$500"),
            createQuickAmount("MAX")
        );

        VBox summaryBox = new VBox(8);
        summaryBox.setPadding(new Insets(4, 0, 0, 0));
        summaryBox.getChildren().addAll(
            createSummaryRow("Avg price", "67.0¢"),
            createSummaryRow("Shares", "746.27"),
            createSummaryRow("Potential return", "+$246.27 (49.2%)", true)
        );

        Button buyButton = new Button("Buy YES · 746.27 shares");
        buyButton.getStyleClass().add("buy-button");
        buyButton.setFont(Font.font("Inter", FontWeight.BOLD, 14));

        panel.getChildren().addAll(yesNoToggle, buySellRow, amountLabel, amountInputBox, quickAmounts, summaryBox, buyButton);
        return panel;
    }

    private Button createQuickAmount(String text) {
        Button btn = new Button(text);
        btn.getStyleClass().add("quick-amount-btn");
        btn.setFont(Font.font("Inter", 11));
        return btn;
    }

    private HBox createSummaryRow(String label, String value) {
        return createSummaryRow(label, value, false);
    }

    private HBox createSummaryRow(String label, String value, boolean highlight) {
        HBox row = new HBox(0);
        Label lbl = new Label(label);
        lbl.getStyleClass().add("summary-label");
        lbl.setFont(Font.font("Inter", 12));
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Label val = new Label(value);
        if (highlight) {
            val.getStyleClass().add("summary-value-highlight");
        } else {
            val.getStyleClass().add("summary-value");
        }
        val.setFont(Font.font("Inter", 12));
        row.getChildren().addAll(lbl, spacer, val);
        return row;
    }

    private VBox createOrderBook() {
        VBox orderBook = new VBox(8);
        orderBook.getStyleClass().add("orderbook-panel");
        orderBook.setPadding(new Insets(16, 16, 16, 16));

        HBox obHeader = new HBox(0);
        Label obTitle = new Label("ORDER BOOK");
        obTitle.getStyleClass().add("orderbook-title");
        obTitle.setFont(Font.font("Inter", FontWeight.MEDIUM, 12));
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Label spreadLabel = new Label("SPREAD 1.0¢");
        spreadLabel.getStyleClass().add("spread-label");
        spreadLabel.setFont(Font.font("Inter", 11));
        obHeader.getChildren().addAll(obTitle, spacer, spreadLabel);

        GridPane grid = new GridPane();
        grid.getStyleClass().add("orderbook-grid");
        grid.setVgap(4);

        Label priceHeader = new Label("Price");
        priceHeader.getStyleClass().add("ob-col-label");
        Label sharesHeader = new Label("Shares");
        sharesHeader.getStyleClass().add("ob-col-label");
        sharesHeader.setAlignment(Pos.CENTER_RIGHT);
        Label totalHeader = new Label("Total");
        totalHeader.getStyleClass().add("ob-col-label");
        totalHeader.setAlignment(Pos.CENTER_RIGHT);

        grid.addRow(0, priceHeader, sharesHeader, totalHeader);

        String[][] asks = {
            {"69.0¢", "420", "$290"},
            {"68.5¢", "1.2K", "$822"},
            {"68.0¢", "3.4K", "$2.3K"},
            {"67.5¢", "850", "$574"}
        };

        for (int i = 0; i < asks.length; i++) {
            HBox row = new HBox();
            row.getStyleClass().add("ob-ask-row");
            Label p = new Label(asks[i][0]);
            p.getStyleClass().add("ob-ask-price");
            Label s = new Label(asks[i][1]);
            s.getStyleClass().add("ob-ask-shares");
            s.setAlignment(Pos.CENTER_RIGHT);
            Label t = new Label(asks[i][2]);
            t.getStyleClass().add("ob-ask-total");
            t.setAlignment(Pos.CENTER_RIGHT);
            grid.addRow(i + 1, p, s, t);
        }

        Label midLabel = new Label("67.0¢");
        midLabel.getStyleClass().add("mid-price");
        midLabel.setFont(Font.font("Inter", FontWeight.BOLD, 13));
        GridPane.setHalignment(midLabel, HPos.CENTER);
        grid.add(midLabel, 0, asks.length + 1, 3, 1);

        String[][] bids = {
            {"66.5¢", "2.1K", "$1.4K"},
            {"66.0¢", "4.5K", "$2.9K"}
        };

        for (int i = 0; i < bids.length; i++) {
            HBox row = new HBox();
            row.getStyleClass().add("ob-bid-row");
            Label p = new Label(bids[i][0]);
            p.getStyleClass().add("ob-bid-price");
            Label s = new Label(bids[i][1]);
            s.getStyleClass().add("ob-bid-shares");
            s.setAlignment(Pos.CENTER_RIGHT);
            Label t = new Label(bids[i][2]);
            t.getStyleClass().add("ob-bid-total");
            t.setAlignment(Pos.CENTER_RIGHT);
            grid.addRow(i + asks.length + 2, p, s, t);
        }

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(33.33);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(33.33);
        ColumnConstraints col3 = new ColumnConstraints();
        col3.setPercentWidth(33.33);
        grid.getColumnConstraints().addAll(col1, col2, col3);

        orderBook.getChildren().addAll(obHeader, grid);
        return orderBook;
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
