package com.polymarket.ui;

import com.polymarket.model.transactions;
import com.polymarket.ui.components.ChromeFactory;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.List;
import java.util.function.Consumer;

public class WalletView {

    private BorderPane root;
    private Runnable onBack;
    private Runnable onPortfolioClick;
    private Runnable onHistoryClick;
    private Consumer<Double> onDeposit;
    private Consumer<Double> onWithdraw;
    private Runnable onCasinoClick;

    private Label sidebarBalance;
    private Label topbarBalance;
    private Label totalValue;
    private Label realValue;
    private Label virtualValue;
    private TextField depositField;
    private TextField withdrawField;
    private TableView<transactions> historyTable;
    private final ObservableList<transactions> transactionList = FXCollections.observableArrayList();

    public WalletView() {
        root = new BorderPane();
        root.getStyleClass().add("main-container");
        buildLayout();
    }

    private void buildLayout() {
        VBox sidebar = ChromeFactory.sidebar(
            ChromeFactory.NavId.WALLET,
            new ChromeFactory.NavCallbacks(
                () -> { if (onBack != null) onBack.run(); },
                () -> { if (onPortfolioClick != null) onPortfolioClick.run(); },
                null,
                () -> { if (onHistoryClick != null) onHistoryClick.run(); },
                () -> { if (onCasinoClick != null) onCasinoClick.run(); }
            )
        );
        sidebarBalance = ChromeFactory.findSidebarBalance(sidebar);
        root.setLeft(sidebar);

        BorderPane main = new BorderPane();
        main.getStyleClass().add("main-content");
        HBox topbar = ChromeFactory.topbar("Wallet", "Search transactions...", null);
        topbarBalance = ChromeFactory.findTopbarBalance(topbar);
        main.setTop(topbar);
        main.setCenter(buildContent());
        root.setCenter(main);
    }

    private ScrollPane buildContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(20, 20, 24, 20));

        content.getChildren().addAll(
            ChromeFactory.breadcrumb("Markets", "Wallet",
                () -> { if (onBack != null) onBack.run(); }),
            buildBalances(),
            buildActions(),
            buildHistory()
        );

        ScrollPane scroll = new ScrollPane(content);
        scroll.getStyleClass().add("detail-scroll");
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        return scroll;
    }

    private HBox buildBalances() {
        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);

        VBox total = balanceCard("TOTAL BALANCE", totalValue = bigMono("0.00"), true);
        VBox real  = balanceCard("REAL",          realValue  = bigMono("0.00"), false);
        VBox virt  = balanceCard("VIRTUAL",       virtualValue = bigMono("0.00"), false);

        HBox.setHgrow(total, Priority.ALWAYS);
        HBox.setHgrow(real, Priority.ALWAYS);
        HBox.setHgrow(virt, Priority.ALWAYS);

        row.getChildren().addAll(total, real, virt);
        return row;
    }

    private Label bigMono(String text) {
        Label l = new Label(text);
        l.setFont(Font.font("JetBrains Mono", FontWeight.BOLD, 28));
        l.setStyle("-fx-text-fill: -fg-0;");
        return l;
    }

    private VBox balanceCard(String title, Label value, boolean primary) {
        VBox card = new VBox(10);
        card.getStyleClass().add("stat-card");
        card.setPadding(new Insets(20));

        Label l = new Label(title);
        l.getStyleClass().add("stat-card-label");

        if (primary) {
            value.setStyle("-fx-text-fill: -gold; -fx-effect: dropshadow(gaussian, rgba(229,200,74,0.4), 14, 0, 0, 0);");
        }

        HBox row = new HBox(6);
        row.setAlignment(Pos.BASELINE_LEFT);
        Label curr = new Label("$NVB");
        curr.setStyle("-fx-text-fill: -gold; -fx-font-weight: bold; -fx-font-size: 12px;");
        row.getChildren().addAll(value, curr);

        card.getChildren().addAll(l, row);
        return card;
    }

    private HBox buildActions() {
        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);

        VBox deposit = actionCard(
            "DEPOSIT",
            "Add $NVB to your wallet to start trading.",
            "deposit",
            "+ Deposit",
            true,
            field -> depositField = field,
            this::handleDeposit
        );
        VBox withdraw = actionCard(
            "WITHDRAW",
            "Take your $NVB out. A special offer may apply.",
            "withdraw",
            "Withdraw →",
            false,
            field -> withdrawField = field,
            this::handleWithdraw
        );

        HBox.setHgrow(deposit, Priority.ALWAYS);
        HBox.setHgrow(withdraw, Priority.ALWAYS);
        row.getChildren().addAll(deposit, withdraw);
        return row;
    }

    private VBox actionCard(String title, String subtitle, String prompt, String btnText,
                            boolean gold, Consumer<TextField> consumer, Runnable onAction) {
        VBox card = new VBox(16);
        card.getStyleClass().add("wallet-action-card");

        Label t = new Label(title);
        t.getStyleClass().add("section-title");

        Label s = new Label(subtitle);
        s.setStyle("-fx-text-fill: -fg-2; -fx-font-size: 13px;");
        s.setWrapText(true);

        Label amtLabel = new Label("AMOUNT ($NVB)");
        amtLabel.getStyleClass().add("amount-label");

        TextField field = new TextField();
        field.setPromptText(prompt);
        field.getStyleClass().add(gold ? "wallet-amount-input-gold" : "wallet-amount-input");
        if (gold) field.getStyleClass().add("wallet-amount-input");
        consumer.accept(field);
        field.setOnAction(e -> onAction.run());

        HBox quick = new HBox(6);
        for (String preset : new String[]{"100", "500", "1000", "5000"}) {
            Button q = new Button("$" + preset);
            q.getStyleClass().add("quick-amount-btn");
            q.setFont(Font.font("JetBrains Mono", FontWeight.MEDIUM, 11));
            HBox.setHgrow(q, Priority.ALWAYS);
            q.setMaxWidth(Double.MAX_VALUE);
            q.setOnAction(e -> field.setText(preset));
            quick.getChildren().add(q);
        }

        Button btn = new Button(btnText);
        btn.getStyleClass().add(gold ? "btn-gold-cta" : "buy-button");
        btn.setFont(Font.font("Inter", FontWeight.BOLD, 13));
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setOnAction(e -> onAction.run());

        card.getChildren().addAll(t, s, amtLabel, field, quick, btn);
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
        } catch (NumberFormatException ignored) {}
    }

    private void handleWithdraw() {
        if (onWithdraw == null || withdrawField == null) return;
        try {
            double amount = Double.parseDouble(withdrawField.getText().trim());
            if (amount > 0) {
                onWithdraw.accept(amount);
                withdrawField.clear();
            }
        } catch (NumberFormatException ignored) {}
    }

    private VBox buildHistory() {
        VBox section = new VBox(10);

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        Label t = new Label("TRANSACTION HISTORY");
        t.getStyleClass().add("section-title");
        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);
        Label hint = new Label("Most recent first");
        hint.getStyleClass().add("footer-label");
        header.getChildren().addAll(t, sp, hint);

        VBox wrap = new VBox(0);
        wrap.getStyleClass().add("table-card");

        historyTable = new TableView<>();
        historyTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        historyTable.setPrefHeight(320);
        historyTable.setItems(transactionList);
        historyTable.setPlaceholder(new Label("No transactions yet"));

        TableColumn<transactions, String> dateCol = new TableColumn<>("DATE");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        dateCol.setCellFactory(c -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); }
                else {
                    setText(item);
                    setStyle("-fx-text-fill: -fg-2; -fx-font-family: 'JetBrains Mono'; -fx-font-size: 11px;");
                }
            }
        });

        TableColumn<transactions, String> marketCol = new TableColumn<>("MARKET");
        marketCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        marketCol.setCellFactory(c -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); }
                else {
                    setText(item);
                    setStyle("-fx-text-fill: -fg-0; -fx-font-family: 'Inter'; -fx-font-size: 12px;");
                }
            }
        });

        TableColumn<transactions, String> typeCol = new TableColumn<>("TYPE");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        typeCol.setCellFactory(c -> new TableCell<>() {
            @Override
            protected void updateItem(String type, boolean empty) {
                super.updateItem(type, empty);
                if (empty || type == null) { setText(null); setGraphic(null); }
                else {
                    Label badge = new Label(type);
                    badge.setFont(Font.font("JetBrains Mono", FontWeight.BOLD, 10));
                    boolean inflow = "DEPOSIT".equalsIgnoreCase(type) || "WIN".equalsIgnoreCase(type);
                    badge.getStyleClass().add(inflow ? "holder-yes-badge" : "holder-no-badge");
                    setGraphic(badge);
                    setText(null);
                }
            }
        });

        TableColumn<transactions, Double> amtCol = new TableColumn<>("AMOUNT");
        amtCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
        amtCol.setCellFactory(c -> new TableCell<>() {
            @Override
            protected void updateItem(Double amount, boolean empty) {
                super.updateItem(amount, empty);
                if (empty || amount == null) { setText(null); setStyle(""); }
                else {
                    String sign = amount >= 0 ? "+" : "";
                    setText(String.format("%s%,.2f $NVB", sign, amount));
                    setStyle(amount >= 0
                        ? "-fx-text-fill: -yes; -fx-font-family: 'JetBrains Mono'; -fx-font-weight: bold; -fx-font-size: 12px;"
                        : "-fx-text-fill: -no; -fx-font-family: 'JetBrains Mono'; -fx-font-weight: bold; -fx-font-size: 12px;");
                }
            }
        });

        historyTable.getColumns().addAll(dateCol, marketCol, typeCol, amtCol);
        wrap.getChildren().add(historyTable);
        VBox.setVgrow(wrap, Priority.ALWAYS);

        section.getChildren().addAll(header, wrap);
        return section;
    }

    public void setBalance(double balance) {
        String s = String.format("%.2f", balance);
        if (sidebarBalance != null) sidebarBalance.setText(s);
        if (topbarBalance != null) topbarBalance.setText(s);
    }

    public void setBalances(double total, double real, double virtual) {
        String tt = String.format("%,.2f", total);
        setBalance(total);
        if (totalValue != null) totalValue.setText(tt);
        if (realValue != null) realValue.setText(String.format("%,.2f", real));
        if (virtualValue != null) virtualValue.setText(String.format("%,.2f", virtual));
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

    public void setOnPortfolioClick(Runnable onPortfolioClick) {
        this.onPortfolioClick = onPortfolioClick;
    }

    public void setOnHistoryClick(Runnable onHistoryClick) {
        this.onHistoryClick = onHistoryClick;
    }

    public void setOnCasinoClick(Runnable onCasinoClick) {
        this.onCasinoClick = onCasinoClick;
    }

    public BorderPane getView() {
        return root;
    }
}
