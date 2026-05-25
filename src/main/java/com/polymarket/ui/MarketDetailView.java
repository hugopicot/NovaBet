package com.polymarket.ui;

import com.polymarket.dao.betsDao;
import com.polymarket.dao.usersDao;
import com.polymarket.domain.dto.BetRequest;
import com.polymarket.domain.dto.OutcomeLabel;
import com.polymarket.model.bets;
import com.polymarket.model.events;
import com.polymarket.model.outcomes;
import com.polymarket.model.users;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
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
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class MarketDetailView {

    private BorderPane root;
    private Runnable onBack;
    private Runnable onMarketsClick;
    private Runnable onPortfolioClick;
    private Runnable onWalletClick;
    private Runnable onHistoryClick;
    private Runnable onEditMarket;
    private Runnable onDeleteMarket;
    private Consumer<BetRequest> onPlaceBet;
    private Long currentUserId;
    private Long currentEventId;
    private List<outcomes> currentOutcomes;
    private events currentEvent;
    private Label balanceValue;
    private TextField amountField;

    private Label headerQuestion;
    private Label probValue;
    private Label probPercent;
    private Label probLabel;
    private Label volumeLabel;
    private Label endsLabel;
    private Label liveText;
    private Label aboutDescription;
    private VBox holdersList;
    private GridPane orderBookGrid;
    private Label orderBookMidPrice;
    private Label orderBookSpread;
    private VBox topHoldersSection;
    private VBox orderBookPanel;

    private Button yesBtn;
    private Button noBtn;
    private Button buyToggle;
    private Button sellToggle;
    private Label summaryAvgPrice;
    private Label summaryShares;
    private Label summaryReturn;
    private Button buyButton;

    private OutcomeLabel selectedOutcome = OutcomeLabel.YES;
    private boolean isBuyMode = true;

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
        HBox portfolioNav = createNavItem("Portfolio", false);
        portfolioNav.setCursor(javafx.scene.Cursor.HAND);
        portfolioNav.setOnMouseClicked(e -> {
            if (onPortfolioClick != null) onPortfolioClick.run();
        });
        HBox walletNav = createNavItem("Wallet", false);
        walletNav.setCursor(javafx.scene.Cursor.HAND);
        walletNav.setOnMouseClicked(e -> {
            if (onWalletClick != null) onWalletClick.run();
        });
        HBox historyNav = createNavItem("History", false);
        historyNav.setCursor(javafx.scene.Cursor.HAND);
        historyNav.setOnMouseClicked(e -> {
            if (onHistoryClick != null) onHistoryClick.run();
        });
        navItems.getChildren().addAll(
            marketsNav,
            portfolioNav,
            createNavItem("Create market", false),
            walletNav,
            historyNav
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
        balanceValue = new Label("0.00");
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
        Label balanceValue = new Label("0.00");
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

        Label categoryLabel = new Label("Markets");
        categoryLabel.getStyleClass().add("detail-category");
        categoryLabel.setFont(Font.font("Inter", 12));

        HBox header = new HBox(16);
        header.setAlignment(Pos.TOP_LEFT);

        StackPane iconContainer = new StackPane();
        iconContainer.getStyleClass().add("market-icon-container");
        iconContainer.setPrefSize(56, 56);
        Label iconLabel = new Label("\uD83C\uDFB2");
        iconLabel.setFont(Font.font(28));
        iconContainer.getChildren().add(iconLabel);

        VBox infoBox = new VBox(6);
        headerQuestion = new Label("");
        headerQuestion.getStyleClass().add("detail-question");
        headerQuestion.setFont(Font.font("Inter", FontWeight.BOLD, 20));
        headerQuestion.setWrapText(true);

        HBox metaRow = new HBox(16);
        metaRow.setAlignment(Pos.CENTER_LEFT);
        volumeLabel = new Label("");
        volumeLabel.getStyleClass().add("meta-label");
        volumeLabel.setFont(Font.font("Inter", 13));
        endsLabel = new Label("");
        endsLabel.getStyleClass().add("meta-label");
        endsLabel.setFont(Font.font("Inter", 13));

        HBox liveBadge = new HBox(6);
        liveBadge.setAlignment(Pos.CENTER);
        liveBadge.getStyleClass().add("live-badge");
        Region liveDot = new Region();
        liveDot.getStyleClass().add("live-dot");
        liveDot.setPrefSize(6, 6);
        liveText = new Label("LIVE");
        liveText.getStyleClass().add("live-text");
        liveText.setFont(Font.font("Inter", FontWeight.BOLD, 10));
        liveBadge.getChildren().addAll(liveDot, liveText);

        metaRow.getChildren().addAll(volumeLabel, endsLabel, liveBadge);
        infoBox.getChildren().addAll(headerQuestion, metaRow);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        VBox probabilityBox = new VBox(4);
        probabilityBox.setAlignment(Pos.TOP_RIGHT);
        probLabel = new Label("YES probability");
        probLabel.getStyleClass().add("prob-label");
        probLabel.setFont(Font.font("Inter", 12));
        probValue = new Label("--");
        probValue.getStyleClass().add("prob-value");
        probValue.setFont(Font.font("Inter", FontWeight.BOLD, 42));
        probPercent = new Label("%");
        probPercent.getStyleClass().add("prob-percent");
        probPercent.setFont(Font.font("Inter", FontWeight.BOLD, 20));
        HBox probRow = new HBox(2);
        probRow.setAlignment(Pos.TOP_RIGHT);
        probRow.getChildren().addAll(probValue, probPercent);
        probabilityBox.getChildren().addAll(probLabel, probRow);

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

        VBox tradePanel = createTradePanel();
        tradePanel.getStyleClass().add("right-column");

        middleSection.getChildren().addAll(tradePanel);
        return middleSection;
    }

    private VBox createTradePanel() {
        VBox panel = new VBox(14);
        panel.getStyleClass().add("trade-panel");
        panel.setPadding(new Insets(16, 16, 16, 16));

        HBox yesNoToggle = new HBox(0);
        yesNoToggle.getStyleClass().add("yes-no-toggle");
        yesBtn = new Button("Yes --¢");
        yesBtn.getStyleClass().add("yes-toggle-active");
        yesBtn.setFont(Font.font("Inter", FontWeight.MEDIUM, 13));
        yesBtn.setOnAction(e -> selectOutcome(OutcomeLabel.YES));
        noBtn = new Button("No --¢");
        noBtn.getStyleClass().add("no-toggle");
        noBtn.setFont(Font.font("Inter", FontWeight.MEDIUM, 13));
        noBtn.setOnAction(e -> selectOutcome(OutcomeLabel.NO));
        yesNoToggle.getChildren().addAll(yesBtn, noBtn);

        HBox buySellRow = new HBox(8);
        buySellRow.setAlignment(Pos.CENTER_LEFT);
        buyToggle = new Button("Buy");
        buyToggle.getStyleClass().add("buy-sell-active");
        buyToggle.setFont(Font.font("Inter", FontWeight.MEDIUM, 12));
        buyToggle.setOnAction(e -> setTradeMode(true));
        sellToggle = new Button("Sell");
        sellToggle.getStyleClass().add("buy-sell-toggle");
        sellToggle.setFont(Font.font("Inter", 12));
        sellToggle.setOnAction(e -> setTradeMode(false));
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        buySellRow.getChildren().addAll(buyToggle, sellToggle, spacer);

        Label amountLabel = new Label("AMOUNT ($NVB)");
        amountLabel.getStyleClass().add("amount-label");
        amountLabel.setFont(Font.font("Inter", 11));

        HBox amountInputBox = new HBox(0);
        amountInputBox.getStyleClass().add("amount-input-box");
        amountField = new TextField("");
        amountField.setPromptText("0.00");
        amountField.getStyleClass().add("amount-field");
        amountField.setFont(Font.font("Inter", 14));
        Label currencyLabel = new Label("$NVB");
        currencyLabel.getStyleClass().add("currency-label");
        currencyLabel.setFont(Font.font("Inter", 12));
        amountInputBox.getChildren().addAll(amountField, currencyLabel);

        amountField.textProperty().addListener((obs, old, newVal) -> updateSummary());

        HBox quickAmounts = new HBox(6);
        Button q25 = createQuickAmount("$25");
        q25.setOnAction(e -> amountField.setText("25"));
        Button q100 = createQuickAmount("$100");
        q100.setOnAction(e -> amountField.setText("100"));
        Button q500 = createQuickAmount("$500");
        q500.setOnAction(e -> amountField.setText("500"));
        Button qMax = createQuickAmount("MAX");
        qMax.setOnAction(e -> amountField.setText("10000"));
        quickAmounts.getChildren().addAll(q25, q100, q500, qMax);

        VBox summaryBox = new VBox(8);
        summaryBox.setPadding(new Insets(4, 0, 0, 0));
        summaryBox.getChildren().addAll(
            createSummaryRow("Avg price", summaryAvgPrice = new Label("--")),
            createSummaryRow("Shares", summaryShares = new Label("--")),
            createSummaryRow("Potential return", summaryReturn = new Label("--"), true)
        );

        buyButton = new Button("Buy YES");
        buyButton.getStyleClass().add("buy-button");
        buyButton.setFont(Font.font("Inter", FontWeight.BOLD, 14));
        buyButton.setOnAction(e -> executeTrade());

        panel.getChildren().addAll(yesNoToggle, buySellRow, amountLabel, amountInputBox, quickAmounts, summaryBox, buyButton);
        return panel;
    }

    private void selectOutcome(OutcomeLabel outcome) {
        selectedOutcome = outcome;
        if (outcome == OutcomeLabel.YES) {
            yesBtn.getStyleClass().clear();
            yesBtn.getStyleClass().add("yes-toggle-active");
            noBtn.getStyleClass().clear();
            noBtn.getStyleClass().add("no-toggle");
        } else {
            noBtn.getStyleClass().clear();
            noBtn.getStyleClass().add("yes-toggle-active");
            yesBtn.getStyleClass().clear();
            yesBtn.getStyleClass().add("no-toggle");
        }
        updateSummary();
        updateBuyButtonText();
    }

    private void setTradeMode(boolean buy) {
        isBuyMode = buy;
        if (buy) {
            buyToggle.getStyleClass().clear();
            buyToggle.getStyleClass().add("buy-sell-active");
            sellToggle.getStyleClass().clear();
            sellToggle.getStyleClass().add("buy-sell-toggle");
        } else {
            sellToggle.getStyleClass().clear();
            sellToggle.getStyleClass().add("buy-sell-active");
            buyToggle.getStyleClass().clear();
            buyToggle.getStyleClass().add("buy-sell-toggle");
        }
        updateBuyButtonText();
    }

    private void updateBuyButtonText() {
        if (buyButton == null) return;
        String outcomeStr = selectedOutcome == OutcomeLabel.YES ? "YES" : "NO";
        String actionStr = isBuyMode ? "Buy" : "Sell";
        String shares = summaryShares.getText();
        if ("--".equals(shares)) {
            buyButton.setText(actionStr + " " + outcomeStr);
        } else {
            buyButton.setText(actionStr + " " + outcomeStr + " · " + shares + " shares");
        }
    }

    private void updateSummary() {
        if (currentOutcomes == null || currentOutcomes.isEmpty()) return;

        String amountStr = amountField.getText();
        if (amountStr == null || amountStr.isBlank()) {
            summaryAvgPrice.setText("--");
            summaryShares.setText("--");
            summaryReturn.setText("--");
            updateBuyButtonText();
            return;
        }

        try {
            BigDecimal amount = new BigDecimal(amountStr.trim());
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                summaryAvgPrice.setText("--");
                summaryShares.setText("--");
                summaryReturn.setText("--");
                updateBuyButtonText();
                return;
            }

            outcomes targetOutcome = findOutcome(selectedOutcome);
            if (targetOutcome == null) return;

            BigDecimal sharePrice = BigDecimal.valueOf(targetOutcome.getOdds()).setScale(4, RoundingMode.HALF_UP);
            if (selectedOutcome == OutcomeLabel.NO) {
                sharePrice = BigDecimal.ONE.subtract(sharePrice).setScale(4, RoundingMode.HALF_UP);
            }

            int shareCount = amount.divide(sharePrice, RoundingMode.DOWN).intValue();
            BigDecimal totalCost = sharePrice.multiply(BigDecimal.valueOf(shareCount)).setScale(2, RoundingMode.HALF_UP);
            BigDecimal potentialWin = BigDecimal.valueOf(shareCount).subtract(totalCost).setScale(2, RoundingMode.HALF_UP);
            double returnPct = totalCost.compareTo(BigDecimal.ZERO) > 0
                ? potentialWin.divide(totalCost, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).doubleValue()
                : 0;

            summaryAvgPrice.setText(sharePrice.multiply(BigDecimal.valueOf(100)).setScale(1, RoundingMode.HALF_UP).toPlainString() + "¢");
            summaryShares.setText(String.format("%,.2f", (double) shareCount));
            summaryReturn.setText("+" + potentialWin.toPlainString() + " (" + String.format("%.1f", returnPct) + "%)");
            updateBuyButtonText();
        } catch (NumberFormatException e) {
            summaryAvgPrice.setText("--");
            summaryShares.setText("--");
            summaryReturn.setText("--");
            updateBuyButtonText();
        }
    }

    private void executeTrade() {
        if (currentUserId == null || currentEventId == null || onPlaceBet == null) return;
        String amountStr = amountField.getText();
        if (amountStr == null || amountStr.isBlank()) return;
        try {
            BigDecimal amount = new BigDecimal(amountStr.trim());
            if (amount.compareTo(BigDecimal.ZERO) > 0) {
                onPlaceBet.accept(new BetRequest(currentUserId, currentEventId, selectedOutcome, amount, true));
                amountField.clear();
                updateSummary();
            }
        } catch (NumberFormatException | ArithmeticException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Bet failed");
            alert.setHeaderText(null);
            alert.setContentText(ex.getMessage());
            alert.showAndWait();
        }
    }

    private Button createQuickAmount(String text) {
        Button btn = new Button(text);
        btn.getStyleClass().add("quick-amount-btn");
        btn.setFont(Font.font("Inter", 11));
        return btn;
    }

    private HBox createSummaryRow(String label, Label value) {
        return createSummaryRow(label, value, false);
    }

    private HBox createSummaryRow(String label, Label value, boolean highlight) {
        HBox row = new HBox(0);
        Label lbl = new Label(label);
        lbl.getStyleClass().add("summary-label");
        lbl.setFont(Font.font("Inter", 12));
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        if (highlight) {
            value.getStyleClass().add("summary-value-highlight");
        } else {
            value.getStyleClass().add("summary-value");
        }
        value.setFont(Font.font("Inter", 12));
        row.getChildren().addAll(lbl, spacer, value);
        return row;
    }

    private HBox createBottomSection() {
        HBox bottomSection = new HBox(16);

        HBox leftBottom = new HBox(16);
        leftBottom.getStyleClass().add("left-column");
        HBox.setHgrow(leftBottom, Priority.ALWAYS);

        VBox aboutSection = createAboutSection();
        HBox.setHgrow(aboutSection, Priority.ALWAYS);

        topHoldersSection = createTopHoldersSection();
        HBox.setHgrow(topHoldersSection, Priority.ALWAYS);

        leftBottom.getChildren().addAll(aboutSection, topHoldersSection);

        orderBookPanel = createOrderBook();
        orderBookPanel.getStyleClass().add("right-column");

        bottomSection.getChildren().addAll(leftBottom, orderBookPanel);
        return bottomSection;
    }

    private VBox createAboutSection() {
        VBox aboutBox = new VBox(12);
        aboutBox.getStyleClass().add("info-card");
        aboutBox.setPadding(new Insets(16, 16, 16, 16));

        Label title = new Label("ABOUT THIS MARKET");
        title.getStyleClass().add("info-card-title");
        title.setFont(Font.font("Inter", FontWeight.MEDIUM, 12));

        aboutDescription = new Label("");
        aboutDescription.getStyleClass().add("info-card-text");
        aboutDescription.setFont(Font.font("Inter", 13));
        aboutDescription.setWrapText(true);

        aboutBox.getChildren().addAll(title, aboutDescription);
        return aboutBox;
    }

    private VBox createTopHoldersSection() {
        VBox holdersBox = new VBox(12);
        holdersBox.getStyleClass().add("info-card");
        holdersBox.setPadding(new Insets(16, 16, 16, 16));

        Label title = new Label("TOP HOLDERS");
        title.getStyleClass().add("info-card-title");
        title.setFont(Font.font("Inter", FontWeight.MEDIUM, 12));

        holdersList = new VBox(10);

        holdersBox.getChildren().addAll(title, holdersList);
        return holdersBox;
    }

    private void loadTopHolders() {
        if (holdersList == null || currentEventId == null) return;
        holdersList.getChildren().clear();

        try {
            betsDao betsDao = new betsDao();
            usersDao usersDao = new usersDao();
            List<bets> allBets = betsDao.getAll();

            Map<Integer, Map<String, BigDecimal>> userHoldings = new HashMap<>();

            for (bets bet : allBets) {
                outcomes outcome = null;
                if (currentOutcomes != null) {
                    for (outcomes o : currentOutcomes) {
                        if (o.getId().intValue() == bet.getOutcome_id()) {
                            outcome = o;
                            break;
                        }
                    }
                }
                if (outcome == null) continue;

                String label = outcome.getLabel();
                userHoldings.putIfAbsent(bet.getUser_id(), new HashMap<>());
                Map<String, BigDecimal> holdings = userHoldings.get(bet.getUser_id());
                holdings.put(label, holdings.getOrDefault(label, BigDecimal.ZERO).add(BigDecimal.valueOf(bet.getAmount())));
            }

            List<HolderInfo> topHolders = new ArrayList<>();
            for (Map.Entry<Integer, Map<String, BigDecimal>> entry : userHoldings.entrySet()) {
                int userId = entry.getKey();
                for (Map.Entry<String, BigDecimal> holding : entry.getValue().entrySet()) {
                    topHolders.add(new HolderInfo(userId, holding.getKey(), holding.getValue()));
                }
            }

            topHolders.sort(Comparator.comparing(HolderInfo::amount).reversed());

            int count = Math.min(3, topHolders.size());
            for (int i = 0; i < count; i++) {
                HolderInfo h = topHolders.get(i);
                users user = usersDao.findById((long) h.userId());
                String displayName = user != null ? user.getUsername() : "User " + h.userId();
                if (displayName.length() > 12) {
                    displayName = displayName.substring(0, 8) + "...";
                }
                holdersList.getChildren().add(createHolderRow(displayName, h.label(), "$" + h.amount().setScale(0, RoundingMode.HALF_UP).toPlainString()));
            }

            if (count == 0) {
                Label noHolders = new Label("No holders yet");
                noHolders.getStyleClass().add("holder-amount");
                noHolders.setFont(Font.font("Inter", 12));
                holdersList.getChildren().add(noHolders);
            }
        } catch (SQLException e) {
            Label errorLabel = new Label("Error loading holders");
            errorLabel.getStyleClass().add("holder-amount");
            errorLabel.setFont(Font.font("Inter", 12));
            holdersList.getChildren().add(errorLabel);
        }
    }

    private record HolderInfo(int userId, String label, BigDecimal amount) {}

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
        orderBookSpread = new Label("");
        orderBookSpread.getStyleClass().add("spread-label");
        orderBookSpread.setFont(Font.font("Inter", 11));
        obHeader.getChildren().addAll(obTitle, spacer, orderBookSpread);

        orderBookGrid = new GridPane();
        orderBookGrid.getStyleClass().add("orderbook-grid");
        orderBookGrid.setVgap(4);

        Label priceHeader = new Label("Price");
        priceHeader.getStyleClass().add("ob-col-label");
        Label sharesHeader = new Label("Shares");
        sharesHeader.getStyleClass().add("ob-col-label");
        sharesHeader.setAlignment(Pos.CENTER_RIGHT);
        Label totalHeader = new Label("Total");
        totalHeader.getStyleClass().add("ob-col-label");
        totalHeader.setAlignment(Pos.CENTER_RIGHT);

        orderBookGrid.addRow(0, priceHeader, sharesHeader, totalHeader);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(33.33);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(33.33);
        ColumnConstraints col3 = new ColumnConstraints();
        col3.setPercentWidth(33.33);
        orderBookGrid.getColumnConstraints().addAll(col1, col2, col3);

        orderBookMidPrice = new Label("");
        orderBookMidPrice.getStyleClass().add("mid-price");
        orderBookMidPrice.setFont(Font.font("Inter", FontWeight.BOLD, 13));

        orderBook.getChildren().addAll(obHeader, orderBookGrid);
        return orderBook;
    }

    private void loadOrderBook() {
        if (orderBookGrid == null || currentOutcomes == null) return;

        orderBookGrid.getChildren().removeIf(node -> GridPane.getRowIndex(node) != null && GridPane.getRowIndex(node) > 0);

        outcomes yesOutcome = findOutcome(OutcomeLabel.YES);
        outcomes noOutcome = findOutcome(OutcomeLabel.NO);

        if (yesOutcome == null || noOutcome == null) return;

        double yesOdds = yesOutcome.getOdds();
        double noOdds = noOutcome.getOdds();

        double yesPrice = yesOdds;
        double noPrice = noOdds;

        double spread = Math.abs(yesPrice + noPrice - 1.0) * 100;

        orderBookSpread.setText("SPREAD " + String.format("%.1f", spread) + "¢");

        int row = 1;

        Label p1 = new Label(String.format("%.1f¢", noPrice * 100));
        p1.getStyleClass().add("ob-ask-price");
        Label s1 = new Label("--");
        s1.getStyleClass().add("ob-ask-shares");
        s1.setAlignment(Pos.CENTER_RIGHT);
        Label t1 = new Label("--");
        t1.getStyleClass().add("ob-ask-total");
        t1.setAlignment(Pos.CENTER_RIGHT);
        orderBookGrid.addRow(row++, p1, s1, t1);

        orderBookMidPrice.setText(String.format("%.1f¢", yesPrice * 100));
        GridPane.setHalignment(orderBookMidPrice, HPos.CENTER);
        orderBookGrid.add(orderBookMidPrice, 0, row, 3, 1);
        row++;

        Label p2 = new Label(String.format("%.1f¢", yesPrice * 100));
        p2.getStyleClass().add("ob-bid-price");
        Label s2 = new Label("--");
        s2.getStyleClass().add("ob-bid-shares");
        s2.setAlignment(Pos.CENTER_RIGHT);
        Label t2 = new Label("--");
        t2.getStyleClass().add("ob-bid-total");
        t2.setAlignment(Pos.CENTER_RIGHT);
        orderBookGrid.addRow(row++, p2, s2, t2);
    }

    private outcomes findOutcome(OutcomeLabel label) {
        if (currentOutcomes == null) return null;
        for (outcomes o : currentOutcomes) {
            if (label.name().equalsIgnoreCase(o.getLabel())) {
                return o;
            }
        }
        return null;
    }

    public BorderPane getView() {
        return root;
    }

    public void setEventData(events event, List<outcomes> outcomes) {
        if (event == null) return;
        this.currentEvent = event;
        this.currentEventId = event.getId();
        this.currentOutcomes = outcomes;

        if (headerQuestion != null) {
            headerQuestion.setText(event.getTitle());
        }

        if (aboutDescription != null) {
            aboutDescription.setText(event.getDescription() != null ? event.getDescription() : "No description available.");
        }

        if (volumeLabel != null) {
            volumeLabel.setText("Status: " + event.getStatus());
        }

        if (endsLabel != null && event.getCreatedAt() != null) {
            endsLabel.setText("Created: " + event.getCreatedAt());
        }

        if (liveText != null) {
            liveText.setText("OPEN".equals(event.getStatus()) ? "LIVE" : event.getStatus());
        }

        outcomes yesOutcome = findOutcome(OutcomeLabel.YES);
        outcomes noOutcome = findOutcome(OutcomeLabel.NO);

        if (yesOutcome != null) {
            int yesPct = (int) Math.round(yesOutcome.getOdds() * 100);
            if (probValue != null) probValue.setText(String.valueOf(yesPct));
            if (yesBtn != null) yesBtn.setText("Yes " + yesPct + "¢");
        }

        if (noOutcome != null) {
            int noPct = (int) Math.round(noOutcome.getOdds() * 100);
            if (noBtn != null) noBtn.setText("No " + noPct + "¢");
        }

        if (probLabel != null) {
            probLabel.setText(selectedOutcome == OutcomeLabel.YES ? "YES probability" : "NO probability");
        }

        loadOrderBook();
        loadTopHolders();
        updateSummary();
        updateBuyButtonText();
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

    public void setOnPortfolioClick(Runnable onPortfolioClick) {
        this.onPortfolioClick = onPortfolioClick;
    }

    public void setOnWalletClick(Runnable onWalletClick) {
        this.onWalletClick = onWalletClick;
    }

    public void setOnHistoryClick(Runnable onHistoryClick) {
        this.onHistoryClick = onHistoryClick;
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
}
