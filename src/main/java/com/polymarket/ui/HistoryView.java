package com.polymarket.ui;

import com.polymarket.model.transactions;
import com.polymarket.ui.components.ChromeFactory;

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

public class HistoryView {

    private BorderPane root;
    private Runnable onMarketsClick;
    private Runnable onPortfolioClick;
    private Runnable onCreateMarketClick;
    private Runnable onWalletClick;
    private Runnable onCasinoClick;
    private Long currentUserId;

    private TableView<transactions> transactionTable;
    private final ObservableList<transactions> transactionList = FXCollections.observableArrayList();
    private Label totalBetsLabel;
    private Label totalTransactionsLabel;
    private Label netFlowLabel;
    private Label sidebarBalance;
    private Label topbarBalance;

    public HistoryView() {
        root = new BorderPane();
        root.getStyleClass().add("main-container");
        buildLayout();
    }

    private void buildLayout() {
        VBox sidebar = ChromeFactory.sidebar(
            ChromeFactory.NavId.HISTORY,
            new ChromeFactory.NavCallbacks(
                () -> { if (onMarketsClick != null) onMarketsClick.run(); },
                () -> { if (onPortfolioClick != null) onPortfolioClick.run(); },
                () -> { if (onCreateMarketClick != null) onCreateMarketClick.run(); },
                () -> { if (onWalletClick != null) onWalletClick.run(); },
                null,
                () -> { if (onCasinoClick != null) onCasinoClick.run(); }
            )
        );
        sidebarBalance = ChromeFactory.findSidebarBalance(sidebar);
        root.setLeft(sidebar);

        BorderPane main = new BorderPane();
        main.getStyleClass().add("main-content");
        HBox topbar = ChromeFactory.topbar("History", "Search history...", () -> {
            if (onWalletClick != null) onWalletClick.run();
        });
        topbarBalance = ChromeFactory.findTopbarBalance(topbar);
        main.setTop(topbar);
        main.setCenter(buildContent());
        root.setCenter(main);
    }

    private ScrollPane buildContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(20, 20, 24, 20));

        content.getChildren().addAll(
            ChromeFactory.breadcrumb("Markets", "History",
                () -> { if (onMarketsClick != null) onMarketsClick.run(); }),
            buildStatsRow(),
            buildTableSection()
        );

        ScrollPane scroll = new ScrollPane(content);
        scroll.getStyleClass().add("detail-scroll");
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        return scroll;
    }

    private HBox buildStatsRow() {
        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);

        row.getChildren().addAll(
            statCard("TOTAL BETS",     totalBetsLabel = bigMono("0", "stat-card-value")),
            statCard("TRANSACTIONS",   totalTransactionsLabel = bigMono("0", "stat-card-value")),
            statCard("NET FLOW",       netFlowLabel = bigMono("0.00", "stat-card-value-gold"))
        );
        for (javafx.scene.Node n : row.getChildren()) {
            HBox.setHgrow(n, Priority.ALWAYS);
        }
        return row;
    }

    private Label bigMono(String text, String style) {
        Label l = new Label(text);
        l.getStyleClass().addAll("stat-card-value", style);
        l.setFont(Font.font("JetBrains Mono", FontWeight.BOLD, 22));
        return l;
    }

    private VBox statCard(String label, Label value) {
        VBox card = new VBox(8);
        card.getStyleClass().add("stat-card");
        Label l = new Label(label);
        l.getStyleClass().add("stat-card-label");
        card.getChildren().addAll(l, value);
        return card;
    }

    private VBox buildTableSection() {
        VBox section = new VBox(10);
        VBox.setVgrow(section, Priority.ALWAYS);

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("TRANSACTION HISTORY");
        title.getStyleClass().add("section-title");
        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);
        Label hint = new Label("Most recent first");
        hint.getStyleClass().add("footer-label");
        header.getChildren().addAll(title, sp, hint);

        VBox wrap = new VBox(0);
        wrap.getStyleClass().add("table-card");

        transactionTable = new TableView<>();
        transactionTable.setItems(transactionList);
        transactionTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        transactionTable.setPrefHeight(440);
        transactionTable.setPlaceholder(new Label("No transactions yet"));

        TableColumn<transactions, String> dateCol = new TableColumn<>("DATE");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        dateCol.setCellFactory(c -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    setStyle("-fx-text-fill: -fg-2; -fx-font-family: 'JetBrains Mono'; -fx-font-size: 11px;");
                }
            }
        });

        TableColumn<transactions, String> typeCol = new TableColumn<>("TYPE");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        typeCol.setCellFactory(c -> new TableCell<>() {
            @Override
            protected void updateItem(String type, boolean empty) {
                super.updateItem(type, empty);
                if (empty || type == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Label badge = new Label(type);
                    badge.setFont(Font.font("JetBrains Mono", FontWeight.BOLD, 10));
                    boolean isDeposit = "DEPOSIT".equalsIgnoreCase(type) || "WIN".equalsIgnoreCase(type) || "BET_REFUND".equalsIgnoreCase(type);
                    badge.getStyleClass().add(isDeposit ? "holder-yes-badge" : "holder-no-badge");
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
                if (empty || amount == null) {
                    setText(null);
                    setStyle("");
                } else {
                    String sign = amount >= 0 ? "+" : "";
                    setText(String.format("%s%,.2f $NVB", sign, amount));
                    setStyle(amount >= 0
                        ? "-fx-text-fill: -yes; -fx-font-family: 'JetBrains Mono'; -fx-font-weight: bold; -fx-font-size: 12px;"
                        : "-fx-text-fill: -no; -fx-font-family: 'JetBrains Mono'; -fx-font-weight: bold; -fx-font-size: 12px;");
                }
            }
        });

        transactionTable.getColumns().addAll(dateCol, typeCol, amtCol);

        wrap.getChildren().add(transactionTable);
        VBox.setVgrow(wrap, Priority.ALWAYS);

        section.getChildren().addAll(header, wrap);
        return section;
    }

    public void setTransactions(List<transactions> transactions) {
        transactionList.setAll(transactions);
        totalTransactionsLabel.setText(String.format("%,d", transactions.size()));

        double net = 0;
        for (transactions tx : transactions) {
            net += tx.getAmount();
        }
        netFlowLabel.setText(String.format("%s%,.2f", net >= 0 ? "+" : "", net));
    }

    public void setBetStats(int totalBets) {
        totalBetsLabel.setText(String.format("%,d", totalBets));
    }

    public BorderPane getView() {
        return root;
    }

    public void setBalance(double balance) {
        String s = String.format("%.2f", balance);
        if (sidebarBalance != null) sidebarBalance.setText(s);
        if (topbarBalance != null) topbarBalance.setText(s);
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

    public void setOnCasinoClick(Runnable onCasinoClick) {
        this.onCasinoClick = onCasinoClick;
    }
}
