package com.polymarket.ui;

import com.polymarket.model.transactions;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.List;
import java.util.function.Consumer;

public class WalletView {

    private BorderPane root;
    private Runnable onBack;
    private Consumer<Double> onDeposit;
    private Consumer<Double> onWithdraw;

    private Label sidebarBalanceValue;
    private Label mainTotalValue;
    private Label mainRealValue;
    private Label mainVirtualValue;
    private TextField depositField;
    private TextField withdrawField;
    private TableView<transactions> historyTable;
    private ObservableList<transactions> transactionList = FXCollections.observableArrayList();

    public WalletView() {
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
        Label logoVersion = new Label("v0.4.2 \u00B7 alpha");
        logoVersion.getStyleClass().add("logo-version");
        logoVersion.setFont(Font.font("Inter", 10));
        logoTextContainer.getChildren().addAll(logoText, logoVersion);
        logoBox.getChildren().addAll(logoIcon, logoTextContainer);

        VBox navItems = new VBox(4);
        HBox marketsNav = createNavItem("Markets", false);
        marketsNav.setCursor(javafx.scene.Cursor.HAND);
        marketsNav.setOnMouseClicked(e -> {
            if (onBack != null) onBack.run();
        });
        navItems.getChildren().addAll(
            marketsNav,
            createNavItem("Portfolio", false),
            createNavItem("Create market", false),
            createNavItem("Wallet", true),
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
        sidebarBalanceValue = new Label("0.00");
        sidebarBalanceValue.getStyleClass().add("balance-value");
        sidebarBalanceValue.setFont(Font.font("Inter", FontWeight.BOLD, 18));
        Label balanceCurrency = new Label("$NVB");
        balanceCurrency.getStyleClass().add("balance-currency");
        balanceCurrency.setFont(Font.font("Inter", FontWeight.BOLD, 12));
        balanceValueBox.getChildren().addAll(sidebarBalanceValue, balanceCurrency);
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
        mainContent.setCenter(createContentBody());
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
            if (onBack != null) onBack.run();
        });
        Label breadcrumbSlash = new Label("/");
        breadcrumbSlash.getStyleClass().add("breadcrumb-separator");
        breadcrumbSlash.setFont(Font.font("Inter", 14));
        Label breadcrumbWallet = new Label("Wallet");
        breadcrumbWallet.getStyleClass().add("breadcrumb-text-active");
        breadcrumbWallet.setFont(Font.font("Inter", FontWeight.MEDIUM, 14));
        breadcrumb.getChildren().addAll(breadcrumbMarkets, breadcrumbSlash, breadcrumbWallet);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        headerRow.getChildren().addAll(breadcrumb, spacer);
        topBar.getChildren().addAll(headerRow);
        return topBar;
    }

    private ScrollPane createContentBody() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(0, 24, 24, 24));

        content.getChildren().addAll(
            createBalanceSection(),
            createActionsSection(),
            createHistorySection()
        );

        ScrollPane scroll = new ScrollPane(content);
        scroll.getStyleClass().add("detail-scroll");
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        return scroll;
    }

    private HBox createBalanceSection() {
        HBox section = new HBox(16);
        section.setAlignment(Pos.TOP_LEFT);

        mainTotalValue = new Label("0.00");
        mainRealValue = new Label("0.00");
        mainVirtualValue = new Label("0.00");

        VBox totalCard = createInfoCard("TOTAL BALANCE", mainTotalValue, "balance-value");
        VBox realCard = createInfoCard("REAL BALANCE", mainRealValue, "info-card-text");
        VBox virtualCard = createInfoCard("VIRTUAL BALANCE", mainVirtualValue, "info-card-text");

        section.getChildren().addAll(totalCard, realCard, virtualCard);
        return section;
    }

    private VBox createInfoCard(String title, Label valueLabel, String valueStyleClass) {
        VBox card = new VBox(8);
        card.getStyleClass().add("info-card");
        card.setPadding(new Insets(16, 16, 16, 16));
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPrefWidth(220);

        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("info-card-title");
        titleLabel.setFont(Font.font("Inter", FontWeight.MEDIUM, 12));

        valueLabel.getStyleClass().add(valueStyleClass);
        valueLabel.setFont(Font.font("Inter", FontWeight.BOLD, 28));

        Label currency = new Label("$NVB");
        currency.getStyleClass().add("balance-currency");
        currency.setFont(Font.font("Inter", FontWeight.BOLD, 12));

        HBox valueBox = new HBox(6);
        valueBox.setAlignment(Pos.CENTER_LEFT);
        valueBox.getChildren().addAll(valueLabel, currency);

        card.getChildren().addAll(titleLabel, valueBox);
        return card;
    }

    private HBox createActionsSection() {
        HBox section = new HBox(16);
        section.setAlignment(Pos.TOP_LEFT);

        VBox depositCard = createActionCard(
            "Deposit",
            "Add virtual funds to your wallet",
            "Amount",
            "Deposit",
            field -> depositField = field,
            () -> handleDeposit()
        );

        VBox withdrawCard = createActionCard(
            "Withdraw",
            "Withdraw virtual funds from your wallet",
            "Amount",
            "Withdraw",
            field -> withdrawField = field,
            () -> handleWithdraw()
        );

        section.getChildren().addAll(depositCard, withdrawCard);
        return section;
    }

    private VBox createActionCard(String title, String subtitle, String prompt, String btnText,
                                   Consumer<TextField> fieldConsumer, Runnable onAction) {
        VBox card = new VBox(16);
        card.getStyleClass().add("info-card");
        card.setPadding(new Insets(16, 16, 16, 16));
        card.setPrefWidth(360);

        Label titleLabel = new Label(title.toUpperCase());
        titleLabel.getStyleClass().add("info-card-title");
        titleLabel.setFont(Font.font("Inter", FontWeight.MEDIUM, 12));

        Label subtitleLabel = new Label(subtitle);
        subtitleLabel.getStyleClass().add("info-card-text");
        subtitleLabel.setFont(Font.font("Inter", 13));

        HBox inputRow = new HBox(10);
        inputRow.setAlignment(Pos.CENTER_LEFT);
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.getStyleClass().add("search-field");
        field.setFont(Font.font("Inter", 13));
        field.setPrefWidth(200);
        fieldConsumer.accept(field);
        inputRow.getChildren().addAll(field);

        Button btn = new Button(btnText);
        if ("Deposit".equals(btnText)) {
            btn.getStyleClass().add("deposit-btn");
        } else {
            btn.getStyleClass().add("btn-back");
        }
        btn.setFont(Font.font("Inter", FontWeight.BOLD, 13));
        btn.setOnAction(e -> onAction.run());

        card.getChildren().addAll(titleLabel, subtitleLabel, inputRow, btn);
        return card;
    }

    private void handleDeposit() {
        if (onDeposit == null || depositField == null) return;
        try {
            double amount = Double.parseDouble(depositField.getText().trim());
            if (amount > 0) {
                onDeposit.accept(amount);
                depositField.clear();
            }
        } catch (NumberFormatException ex) {
            // ignore invalid input
        }
    }

    private void handleWithdraw() {
        if (onWithdraw == null || withdrawField == null) return;
        try {
            double amount = Double.parseDouble(withdrawField.getText().trim());
            if (amount > 0) {
                onWithdraw.accept(amount);
                withdrawField.clear();
            }
        } catch (NumberFormatException ex) {
            // ignore invalid input
        }
    }

    private VBox createHistorySection() {
        VBox section = new VBox(12);

        Label title = new Label("TRANSACTION HISTORY");
        title.getStyleClass().add("info-card-title");
        title.setFont(Font.font("Inter", FontWeight.MEDIUM, 12));

        VBox tableCard = new VBox(0);
        tableCard.getStyleClass().add("info-card");
        tableCard.setPadding(new Insets(16, 16, 16, 16));

        historyTable = new TableView<>();
        historyTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        historyTable.setPrefHeight(320);

        TableColumn<transactions, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        dateCol.setMinWidth(140);

        TableColumn<transactions, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        typeCol.setMinWidth(120);

        TableColumn<transactions, Double> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
        amountCol.setMinWidth(120);

        historyTable.getColumns().addAll(dateCol, typeCol, amountCol);
        historyTable.setItems(transactionList);

        tableCard.getChildren().addAll(historyTable);
        section.getChildren().addAll(title, tableCard);
        return section;
    }

    public void setBalances(double total, double real, double virtual) {
        sidebarBalanceValue.setText(String.format("%.2f", total));
        mainTotalValue.setText(String.format("%.2f", total));
        mainRealValue.setText(String.format("%.2f", real));
        mainVirtualValue.setText(String.format("%.2f", virtual));
    }

    public void setTransactions(List<transactions> transactions) {
        transactionList.setAll(transactions);
    }

    public void setOnBack(Runnable onBack) {
        this.onBack = onBack;
    }

    public void setOnDeposit(Consumer<Double> onDeposit) {
        this.onDeposit = onDeposit;
    }

    public void setOnWithdraw(Consumer<Double> onWithdraw) {
        this.onWithdraw = onWithdraw;
    }

    public BorderPane getView() {
        return root;
    }
}
