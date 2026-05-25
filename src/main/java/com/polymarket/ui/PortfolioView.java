package com.polymarket.ui;

import com.polymarket.model.bets;
import com.polymarket.model.events;
import com.polymarket.model.outcomes;
import com.polymarket.ui.components.ChromeFactory;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
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
    private Runnable onHistoryClick;
    private Runnable onWalletClick;
    private Consumer<Long> onMarketClick;
    private Long currentUserId;

    private final ObservableList<bets> betList = FXCollections.observableArrayList();

    private Label totalBetsLabel;
    private Label totalInvestedLabel;
    private Label potentialWinLabel;
    private Label avgPriceLabel;
    private GridPane betsGrid;
    private Label sidebarBalance;
    private Label topbarBalance;
    private VBox emptyState;
    private VBox content;

    public PortfolioView() {
        root = new BorderPane();
        root.getStyleClass().add("main-container");
        buildLayout();
    }

    private void buildLayout() {
        VBox sidebar = ChromeFactory.sidebar(
            ChromeFactory.NavId.PORTFOLIO,
            new ChromeFactory.NavCallbacks(
                () -> { if (onMarketsClick != null) onMarketsClick.run(); },
                null,
                () -> { if (onCreateMarketClick != null) onCreateMarketClick.run(); },
                () -> { if (onWalletClick != null) onWalletClick.run(); },
                () -> { if (onHistoryClick != null) onHistoryClick.run(); },
                null
            )
        );
        sidebarBalance = ChromeFactory.findSidebarBalance(sidebar);
        root.setLeft(sidebar);

        BorderPane main = new BorderPane();
        main.getStyleClass().add("main-content");
        HBox topbar = ChromeFactory.topbar("Portfolio", "Search positions...", () -> {
            if (onWalletClick != null) onWalletClick.run();
        });
        topbarBalance = ChromeFactory.findTopbarBalance(topbar);
        main.setTop(topbar);
        main.setCenter(buildContent());
        root.setCenter(main);
    }

    private ScrollPane buildContent() {
        content = new VBox(20);
        content.setPadding(new Insets(20, 20, 24, 20));

        content.getChildren().addAll(
            ChromeFactory.breadcrumb("Markets", "Portfolio",
                () -> { if (onMarketsClick != null) onMarketsClick.run(); }),
            buildStatsRow(),
            buildPositionsHeader(),
            buildGrid()
        );

        emptyState = buildEmptyState();
        emptyState.setVisible(false);
        emptyState.setManaged(false);
        content.getChildren().add(emptyState);

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
            statCard("OPEN POSITIONS", totalBetsLabel = mono("0", "stat-card-value"), null, null),
            statCard("TOTAL INVESTED", totalInvestedLabel = mono("0.00", "stat-card-value"), "$NVB", null),
            statCard("POTENTIAL WIN",  potentialWinLabel = mono("0.00", "stat-card-value-yes"), "$NVB", null),
            statCard("AVG ENTRY",      avgPriceLabel = mono("--", "stat-card-value-gold"), "¢", null)
        );

        for (javafx.scene.Node n : row.getChildren()) {
            HBox.setHgrow(n, Priority.ALWAYS);
        }
        return row;
    }

    private Label mono(String text, String style) {
        Label l = new Label(text);
        l.getStyleClass().addAll("stat-card-value", style);
        l.setFont(Font.font("JetBrains Mono", FontWeight.BOLD, 22));
        return l;
    }

    private VBox statCard(String label, Label value, String suffix, String sub) {
        VBox card = new VBox(8);
        card.getStyleClass().add("stat-card");
        Label l = new Label(label);
        l.getStyleClass().add("stat-card-label");

        HBox row = new HBox(4);
        row.setAlignment(Pos.BASELINE_LEFT);
        row.getChildren().add(value);
        if (suffix != null) {
            Label s = new Label(suffix);
            s.getStyleClass().add("stat-card-sub");
            s.setFont(Font.font("Inter", FontWeight.BOLD, 11));
            row.getChildren().add(s);
        }
        card.getChildren().addAll(l, row);
        if (sub != null) {
            Label sl = new Label(sub);
            sl.getStyleClass().addAll("stat-card-sub", "stat-card-sub-yes");
            sl.setFont(Font.font("Inter", 11));
            card.getChildren().add(sl);
        }
        return card;
    }

    private HBox buildPositionsHeader() {
        HBox row = new HBox(8);
        row.setAlignment(Pos.CENTER_LEFT);
        Label t = new Label("OPEN POSITIONS");
        t.getStyleClass().add("section-title");
        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);
        row.getChildren().addAll(t, sp);
        return row;
    }

    private GridPane buildGrid() {
        betsGrid = new GridPane();
        betsGrid.setHgap(12);
        betsGrid.setVgap(12);
        for (int c = 0; c < 2; c++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setPercentWidth(50);
            cc.setHgrow(Priority.ALWAYS);
            betsGrid.getColumnConstraints().add(cc);
        }
        return betsGrid;
    }

    private VBox buildEmptyState() {
        VBox box = new VBox(8);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(60, 20, 60, 20));
        box.getStyleClass().add("info-card");

        Label icon = new Label("◧");
        icon.setFont(Font.font("Inter", 40));
        icon.setTextFill(javafx.scene.paint.Color.web("#52555B"));

        Label title = new Label("No open positions yet");
        title.setFont(Font.font("Inter", FontWeight.BOLD, 16));
        title.setTextFill(javafx.scene.paint.Color.web("#D5D6D9"));

        Label sub = new Label("Pick a market and place your first trade.");
        sub.getStyleClass().add("empty-state");

        box.getChildren().addAll(icon, title, sub);
        return box;
    }

    public void setBets(List<bets> bets, Map<Long, events> eventsMap, Map<Long, outcomes> outcomesMap) {
        betList.setAll(bets);
        refreshGrid(eventsMap, outcomesMap);
    }

    private void refreshGrid(Map<Long, events> eventsMap, Map<Long, outcomes> outcomesMap) {
        betsGrid.getChildren().clear();
        totalBetsLabel.setText(String.format("%,d", betList.size()));

        double inv = 0, pot = 0, sumPrice = 0;
        int counted = 0;
        for (bets b : betList) {
            inv += b.getAmount();
            pot += b.getPotential_win();
            outcomes o = outcomesMap.get((long) b.getOutcome_id());
            if (o != null) {
                sumPrice += o.getOdds();
                counted++;
            }
        }
        totalInvestedLabel.setText(String.format("%,.2f", inv));
        potentialWinLabel.setText(String.format("%,.2f", pot));
        if (counted > 0) {
            avgPriceLabel.setText(String.format("%.0f", sumPrice / counted * 100));
        } else {
            avgPriceLabel.setText("--");
        }

        boolean empty = betList.isEmpty();
        emptyState.setVisible(empty);
        emptyState.setManaged(empty);
        betsGrid.setVisible(!empty);
        betsGrid.setManaged(!empty);

        for (int i = 0; i < betList.size(); i++) {
            bets b = betList.get(i);
            events e = eventsMap.get((long) b.getOutcome_id());
            outcomes o = outcomesMap.get((long) b.getOutcome_id());
            betsGrid.add(buildBetCard(b, e, o), i % 2, i / 2);
        }
    }

    private VBox buildBetCard(bets bet, events event, outcomes outcome) {
        VBox card = new VBox(12);
        card.getStyleClass().add("bet-card");
        card.setPadding(new Insets(16));
        card.setCursor(Cursor.HAND);
        if (event != null && onMarketClick != null) {
            card.setOnMouseClicked(e -> onMarketClick.accept(event.getId()));
        }

        String title = event != null ? event.getTitle() : "Unknown market";
        String label = outcome != null ? outcome.getLabel() : "?";
        boolean isYes = "YES".equalsIgnoreCase(label);

        HBox row = new HBox(12);
        row.setAlignment(Pos.TOP_LEFT);

        VBox titleBox = new VBox(6);
        HBox.setHgrow(titleBox, Priority.ALWAYS);

        Label q = new Label(title);
        q.getStyleClass().add("market-question");
        q.setFont(Font.font("Inter", FontWeight.MEDIUM, 14));
        q.setWrapText(true);

        HBox badges = new HBox(8);
        badges.setAlignment(Pos.CENTER_LEFT);
        Label badge = new Label(label);
        badge.getStyleClass().add(isYes ? "holder-yes-badge" : "holder-no-badge");
        badge.setFont(Font.font("JetBrains Mono", FontWeight.BOLD, 10));
        Label bet$ = new Label(String.format("Bet · %d $NVB", bet.getAmount()));
        bet$.getStyleClass().add("footer-label");
        bet$.setFont(Font.font("JetBrains Mono", 11));
        badges.getChildren().addAll(badge, bet$);

        titleBox.getChildren().addAll(q, badges);

        VBox winBox = new VBox(4);
        winBox.setAlignment(Pos.TOP_RIGHT);
        Label wl = new Label("POTENTIAL");
        wl.getStyleClass().add("label-uppercase");
        Label wv = new Label(String.format("%d", bet.getPotential_win()));
        wv.setStyle("-fx-text-fill: -yes;");
        wv.setFont(Font.font("JetBrains Mono", FontWeight.BOLD, 18));
        winBox.getChildren().addAll(wl, wv);

        row.getChildren().addAll(titleBox, winBox);

        HBox footer = new HBox(6);
        footer.setAlignment(Pos.CENTER_LEFT);
        Label status = new Label(event != null && event.getStatus() != null ? event.getStatus() : "OPEN");
        status.getStyleClass().add("footer-label");
        Label sep = new Label("·");
        sep.getStyleClass().add("footer-label");
        String res = event != null && event.getResolution() != null ? event.getResolution() : "";
        Label ends = new Label(res.isEmpty() ? "" : "Ends " + res);
        ends.getStyleClass().add("footer-label");
        footer.getChildren().addAll(status, sep, ends);

        card.getChildren().addAll(row, footer);
        return card;
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

    public void setOnCreateMarketClick(Runnable onCreateMarketClick) {
        this.onCreateMarketClick = onCreateMarketClick;
    }

    public void setOnMarketClick(Consumer<Long> onMarketClick) {
        this.onMarketClick = onMarketClick;
    }

    public void setOnHistoryClick(Runnable onHistoryClick) {
        this.onHistoryClick = onHistoryClick;
    }

    public void setOnWalletClick(Runnable onWalletClick) {
        this.onWalletClick = onWalletClick;
    }
}
