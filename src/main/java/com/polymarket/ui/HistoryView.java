package com.polymarket.ui;

import com.polymarket.model.bets;
import com.polymarket.model.events;
import com.polymarket.model.outcomes;
import com.polymarket.model.transactions;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.List;
import java.util.Map;

public class HistoryView {

    private BorderPane root;
    private Runnable onMarketsClick;
    private Runnable onPortfolioClick;
    private Runnable onCreateMarketClick;
    private Runnable onWalletClick;
    private Long currentUserId;

    private TableView<transactions> transactionTable;
    private final ObservableList<transactions> transactionList = FXCollections.observableArrayList();
    private Label totalBetsLabel;
    private Label totalTransactionsLabel;
    private Label balanceValue;

    public HistoryView() {
        root = new BorderPane();
        root.getStyleClass().add("main-container");
        root.setLeft(createSidebar());
        root.setCenter(createMainContent());
    }

    public void setTransactions(List<transactions> transactions) {
        transactionList.setAll(transactions);
        totalTransactionsLabel.setText(String.format("%,d", transactions.size()));
    }

    public void setBetStats(int totalBets) {
        totalBetsLabel.setText(String.format("%,d", totalBets));
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
        HBox portfolioNav = createNavItem("Portfolio", false);
        portfolioNav.setCursor(javafx.scene.Cursor.HAND);
        portfolioNav.setOnMouseClicked(e -> {
            if (onPortfolioClick != null) onPortfolioClick.run();
        });
        HBox createMarketNav = createNavItem("Create market", false);
        createMarketNav.setCursor(javafx.scene.Cursor.HAND);
        createMarketNav.setOnMouseClicked(e -> {
            if (onCreateMarketClick != null) onCreateMarketClick.run();
        });
        HBox walletNav = createNavItem("Wallet", false);
        walletNav.setCursor(javafx.scene.Cursor.HAND);
        walletNav.setOnMouseClicked(e -> {
            if (onWalletClick != null) onWalletClick.run();
        });
        navItems.getChildren().addAll(
            marketsNav,
            portfolioNav,
            createMarketNav,
            walletNav,
            createNavItem("History", true)
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
        mainContent.setCenter(createHistoryContent());

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
        Label breadcrumbHistory = new Label("History");
        breadcrumbHistory.getStyleClass().add("breadcrumb-text-active");
        breadcrumbHistory.setFont(Font.font("Inter", FontWeight.MEDIUM, 14));
        breadcrumb.getChildren().addAll(breadcrumbMarkets, breadcrumbSlash, breadcrumbHistory);

        headerRow.getChildren().addAll(breadcrumb);

        topBar.getChildren().add(headerRow);
        return topBar;
    }

    private VBox createHistoryContent() {
        VBox historyContent = new VBox(0);
        historyContent.setPadding(new Insets(0, 24, 24, 24));

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

        VBox totalTxBox = new VBox(4);
        Label totalTxTitle = new Label("Transactions");
        totalTxTitle.getStyleClass().add("footer-label");
        totalTxTitle.setFont(Font.font("Inter", 12));
        totalTransactionsLabel = new Label("0");
        totalTransactionsLabel.getStyleClass().add("stats-count");
        totalTransactionsLabel.setFont(Font.font("Inter", FontWeight.BOLD, 20));
        totalTxBox.getChildren().addAll(totalTxTitle, totalTransactionsLabel);

        statsRow.getChildren().addAll(totalBetsBox, totalTxBox);

        VBox tableSection = new VBox(8);
        Label tableTitle = new Label("TRANSACTION HISTORY");
        tableTitle.getStyleClass().add("footer-label");
        tableTitle.setFont(Font.font("Inter", FontWeight.BOLD, 12));

        transactionTable = new TableView<>();
        transactionTable.setItems(transactionList);
        transactionTable.getStyleClass().add("market-card");
        transactionTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<transactions, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        dateCol.setPrefWidth(200);

        TableColumn<transactions, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        typeCol.setPrefWidth(120);
        typeCol.setCellFactory(col -> new TableCell<transactions, String>() {
            @Override
            protected void updateItem(String type, boolean empty) {
                super.updateItem(type, empty);
                if (empty || type == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Label badge = new Label(type);
                    badge.setFont(Font.font("Inter", FontWeight.BOLD, 11));
                    if ("DEPOSIT".equalsIgnoreCase(type)) {
                        badge.getStyleClass().add("holder-yes-badge");
                    } else {
                        badge.getStyleClass().add("holder-no-badge");
                    }
                    setGraphic(badge);
                    setText(null);
                }
            }
        });

        TableColumn<transactions, Double> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
        amountCol.setPrefWidth(150);
        amountCol.setCellFactory(col -> new TableCell<transactions, Double>() {
            @Override
            protected void updateItem(Double amount, boolean empty) {
                super.updateItem(amount, empty);
                if (empty || amount == null) {
                    setText(null);
                } else {
                    String sign = amount >= 0 ? "+" : "";
                    setText(String.format("%s%.2f $NVB", sign, amount));
                    if (amount >= 0) {
                        setStyle("-fx-text-fill: #22c55e;");
                    } else {
                        setStyle("-fx-text-fill: #ef4444;");
                    }
                    setFont(Font.font("Inter", FontWeight.BOLD, 12));
                }
            }
        });

        transactionTable.getColumns().addAll(dateCol, typeCol, amountCol);

        VBox.setVgrow(transactionTable, Priority.ALWAYS);

        tableSection.getChildren().addAll(tableTitle, transactionTable);
        VBox.setVgrow(tableSection, Priority.ALWAYS);

        historyContent.getChildren().addAll(statsRow, tableSection);
        return historyContent;
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

    public void setOnPortfolioClick(Runnable onPortfolioClick) {
        this.onPortfolioClick = onPortfolioClick;
    }

    public void setOnCreateMarketClick(Runnable onCreateMarketClick) {
        this.onCreateMarketClick = onCreateMarketClick;
    }

    public void setOnWalletClick(Runnable onWalletClick) {
        this.onWalletClick = onWalletClick;
    }
}
