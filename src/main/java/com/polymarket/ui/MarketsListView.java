package com.polymarket.ui;

import com.polymarket.domain.dto.BetRequest;
import com.polymarket.domain.dto.OutcomeLabel;
import com.polymarket.model.events;
import com.polymarket.model.outcomes;
import com.polymarket.ui.components.ChromeFactory;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public class MarketsListView {

    private BorderPane root;
    private Runnable onLogout;
    private Consumer<Long> onMarketClick;
    private Runnable onPortfolioClick;
    private Runnable onWalletClick;
    private Runnable onHistoryClick;
    private Runnable onCasino;
    private Consumer<BetRequest> onPlaceBet;
    private Long currentUserId;

    private final ObservableList<events> marketList = FXCollections.observableArrayList();

    private Label marketsCountLabel;
    private GridPane marketsGrid;
    private Label sidebarBalance;
    private Label topbarBalance;
    private String activeFilter = "All";
    private List<events> allMarkets = new ArrayList<>();
    private Map<Long, List<outcomes>> currentOutcomesMap;

    public MarketsListView() {
        root = new BorderPane();
        root.getStyleClass().add("main-container");
        buildLayout();
    }

    private void buildLayout() {
        VBox sidebar = ChromeFactory.sidebar(
            ChromeFactory.NavId.MARKETS,
            new ChromeFactory.NavCallbacks(
                null,
                () -> { if (onPortfolioClick != null) onPortfolioClick.run(); },
                () -> { if (onWalletClick != null) onWalletClick.run(); },
                () -> { if (onHistoryClick != null) onHistoryClick.run(); },
                () -> { if (onCasino != null) onCasino.run(); }
            )
        );
        sidebarBalance = ChromeFactory.findSidebarBalance(sidebar);
        root.setLeft(sidebar);

        BorderPane main = new BorderPane();
        main.getStyleClass().add("main-content");

        HBox topbar = ChromeFactory.topbar("Markets", "Search markets, e.g. \"GPT-5 release\"", () -> {
            if (onWalletClick != null) onWalletClick.run();
        });
        topbarBalance = ChromeFactory.findTopbarBalance(topbar);
        main.setTop(topbar);
        main.setCenter(buildBody());

        root.setCenter(main);
    }

    private VBox buildBody() {
        VBox body = new VBox(0);
        body.setPadding(new Insets(0, 0, 0, 0));

        body.getChildren().addAll(buildCategoryRow(), buildStatsRow(), buildScrollGrid());
        return body;
    }

    private HBox buildCategoryRow() {
        HBox row = new HBox(8);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(14, 20, 12, 20));

        row.getChildren().addAll(
            catPill("All", true),
            catPill("Tech / AI", false),
            catPill("Sport", false),
            catPill("Absurd", false)
        );

        return row;
    }

    private Button catPill(String label, boolean active) {
        Button b = new Button(label);
        b.getStyleClass().add(active ? "filter-btn-active" : "filter-btn");
        b.setFont(Font.font("Inter", active ? FontWeight.BOLD : FontWeight.MEDIUM, 12));
        b.setOnAction(e -> {
            activeFilter = label;
            applyFilter();
        });
        return b;
    }

    private HBox buildStatsRow() {
        HBox row = new HBox(8);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(6, 20, 12, 20));

        marketsCountLabel = new Label("0");
        marketsCountLabel.getStyleClass().add("stats-count");
        marketsCountLabel.setFont(Font.font("JetBrains Mono", FontWeight.BOLD, 13));
        Label suffix = new Label("markets");
        suffix.getStyleClass().add("stats-text-muted");
        suffix.setFont(Font.font("Inter", 12));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox live = new HBox(5);
        live.setAlignment(Pos.CENTER);
        live.getStyleClass().add("live-badge");
        Region dot = new Region();
        dot.getStyleClass().add("live-dot");
        dot.setPrefSize(5, 5);
        dot.setMinSize(5, 5);
        dot.setMaxSize(5, 5);
        Label lt = new Label("LIVE");
        lt.getStyleClass().add("live-text");
        lt.setFont(Font.font("Inter", FontWeight.BOLD, 9));
        live.getChildren().addAll(dot, lt);

        row.getChildren().addAll(marketsCountLabel, suffix, spacer, live);
        return row;
    }

    private ScrollPane buildScrollGrid() {
        marketsGrid = new GridPane();
        marketsGrid.setHgap(12);
        marketsGrid.setVgap(12);
        marketsGrid.setPadding(new Insets(0, 20, 24, 20));

        for (int c = 0; c < 2; c++) {
            javafx.scene.layout.ColumnConstraints cc = new javafx.scene.layout.ColumnConstraints();
            cc.setPercentWidth(50);
            cc.setHgrow(Priority.ALWAYS);
            marketsGrid.getColumnConstraints().add(cc);
        }

        ScrollPane scroll = new ScrollPane(marketsGrid);
        scroll.getStyleClass().add("markets-scroll");
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        VBox.setVgrow(scroll, Priority.ALWAYS);
        return scroll;
    }

    public void setMarkets(List<events> markets, Map<Long, List<outcomes>> outcomesMap) {
        this.allMarkets = new ArrayList<>(markets);
        this.currentOutcomesMap = outcomesMap;
        applyFilter();
    }

    private void applyFilter() {
        List<events> filtered = new ArrayList<>();
        for (events ev : allMarkets) {
            if ("All".equals(activeFilter)) {
                filtered.add(ev);
            } else if ("Tech / AI".equals(activeFilter)) {
                String t = ev.getTitle() != null ? ev.getTitle().toLowerCase() : "";
                if (t.contains("ai") || t.contains("gpt") || t.contains("tech") || t.contains("claude") || t.contains("openai")) filtered.add(ev);
            } else if ("Sport".equals(activeFilter)) {
                String t = ev.getTitle() != null ? ev.getTitle().toLowerCase() : "";
                if (t.contains("sport") || t.contains("foot") || t.contains("match") || t.contains("champion") || t.contains("league") || t.contains("f1") || t.contains("grand prix") || t.contains("psg") || t.contains("basket") || t.contains("tennis")) filtered.add(ev);
            } else if ("Absurd".equals(activeFilter)) {
                String t = ev.getTitle() != null ? ev.getTitle().toLowerCase() : "";
                if (t.contains("alien") || t.contains("ufo") || t.contains("ovni") || t.contains("absurd") || t.contains("conspiracy")) filtered.add(ev);
            }
        }
        marketList.setAll(filtered);
        refreshGrid(currentOutcomesMap);
    }

    private void refreshGrid(Map<Long, List<outcomes>> outcomesMap) {
        marketsGrid.getChildren().clear();
        marketsCountLabel.setText(String.format("%,d", marketList.size()));

        for (int i = 0; i < marketList.size(); i++) {
            events ev = marketList.get(i);
            List<outcomes> os = outcomesMap.getOrDefault(ev.getId(), List.of());
            int col = i % 2;
            int row = i / 2;
            marketsGrid.add(buildMarketCard(ev, os), col, row);
        }
    }

    private VBox buildMarketCard(events event, List<outcomes> os) {
        VBox card = new VBox(10);
        card.getStyleClass().add("market-card");
        card.setPadding(new Insets(14));
        card.setCursor(Cursor.HAND);
        card.setOnMouseClicked(e -> {
            if (onMarketClick != null) onMarketClick.accept(event.getId());
        });

        HBox top = new HBox(12);
        top.setAlignment(Pos.TOP_LEFT);

        StackPane icon = new StackPane();
        icon.getStyleClass().add("market-icon-container");
        icon.setPrefSize(40, 40);
        icon.setMinSize(40, 40);
        icon.setMaxSize(40, 40);
        Label emoji = new Label(getCategoryIcon(event));
        emoji.setFont(Font.font("Segoe UI Emoji", 18));
        icon.getChildren().add(emoji);

        VBox titleBox = new VBox(2);
        HBox.setHgrow(titleBox, Priority.ALWAYS);

        Label question = new Label(event.getTitle() != null ? event.getTitle() : "");
        question.getStyleClass().add("market-question");
        question.setFont(Font.font("Inter", FontWeight.MEDIUM, 13));
        question.setWrapText(true);

        if ("POLYMARKET".equals(event.getSource())) {
            HBox sourceRow = new HBox(4);
            sourceRow.setAlignment(Pos.CENTER_LEFT);
            Label badge = new Label("POLYMARKET");
            badge.getStyleClass().add("filter-btn-active");
            badge.setFont(Font.font("Inter", FontWeight.BOLD, 8));
            badge.setPadding(new Insets(1, 4, 1, 4));
            sourceRow.getChildren().add(badge);
            titleBox.getChildren().addAll(question, sourceRow);
        } else {
            titleBox.getChildren().add(question);
        }

        top.getChildren().addAll(icon, titleBox);

        double yesProb = getYesProbability(os);
        double noProb = 100.0 - yesProb;

        HBox probRow = new HBox();
        probRow.setAlignment(Pos.CENTER_LEFT);
        Label yesPct = new Label(String.format("%.0f%%", yesProb));
        yesPct.getStyleClass().add("percent-yes");
        yesPct.setFont(Font.font("JetBrains Mono", FontWeight.BOLD, 11));

        StackPane bar = new StackPane();
        bar.getStyleClass().add("progress-bar-bg");
        HBox.setHgrow(bar, Priority.ALWAYS);
        bar.setMinHeight(4);
        bar.setMaxHeight(4);
        bar.setPrefHeight(4);
        HBox fillWrap = new HBox();
        fillWrap.setAlignment(Pos.CENTER_LEFT);
        fillWrap.setMaxHeight(4);
        Region fill = new Region();
        fill.getStyleClass().add("progress-bar-yes");
        fill.setMinHeight(4);
        fill.setPrefHeight(4);
        fill.setMaxHeight(4);
        bar.widthProperty().addListener((o, ov, nv) -> fill.setPrefWidth(nv.doubleValue() * (yesProb / 100.0)));
        fillWrap.getChildren().add(fill);
        bar.getChildren().add(fillWrap);
        bar.setPadding(new Insets(0, 10, 0, 10));

        Label noPct = new Label(String.format("%.0f%%", noProb));
        noPct.getStyleClass().add("percent-no");
        noPct.setFont(Font.font("JetBrains Mono", FontWeight.BOLD, 11));

        probRow.getChildren().addAll(yesPct, bar, noPct);

        HBox actions = new HBox(8);
        Button yesBtn = new Button(String.format("Yes · %.0f¢", yesProb));
        yesBtn.getStyleClass().add("btn-yes");
        yesBtn.setFont(Font.font("Inter", FontWeight.BOLD, 11));
        yesBtn.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(yesBtn, Priority.ALWAYS);
        yesBtn.setOnAction(e -> {
            e.consume();
            promptAndPlaceBet(event.getId(), OutcomeLabel.YES);
        });
        Button noBtn = new Button(String.format("No · %.0f¢", noProb));
        noBtn.getStyleClass().add("btn-no");
        noBtn.setFont(Font.font("Inter", FontWeight.BOLD, 11));
        noBtn.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(noBtn, Priority.ALWAYS);
        noBtn.setOnAction(e -> {
            e.consume();
            promptAndPlaceBet(event.getId(), OutcomeLabel.NO);
        });
        actions.getChildren().addAll(yesBtn, noBtn);

        HBox meta = new HBox(6);
        meta.setAlignment(Pos.CENTER_LEFT);
        Label status = new Label(event.getStatus() != null ? event.getStatus() : "OPEN");
        status.getStyleClass().add("footer-label");
        Label sep = new Label("·");
        sep.getStyleClass().add("footer-label");
        String resolution = event.getResolution() != null ? event.getResolution() : "";
        Label ends = new Label(resolution.isEmpty() ? "" : "Ends " + resolution);
        ends.getStyleClass().add("footer-label");
        meta.getChildren().addAll(status, sep, ends);

        card.getChildren().addAll(top, probRow, actions, meta);
        return card;
    }

    private String getCategoryIcon(events event) {
        String title = event.getTitle() != null ? event.getTitle().toLowerCase() : "";
        if (title.contains("ai") || title.contains("gpt") || title.contains("claude") || title.contains("openai") || title.contains("anthropic")) {
            return "🤖";
        }
        if (title.contains("mars") || title.contains("spacex") || title.contains("rocket") || title.contains("space")) {
            return "🚀";
        }
        if (title.contains("psg") || title.contains("foot") || title.contains("match") || title.contains("champion") || title.contains("league")) {
            return "⚽";
        }
        if (title.contains("f1") || title.contains("grand prix") || title.contains("verstap")) {
            return "🏎️";
        }
        if (title.contains("bitcoin") || title.contains("crypto") || title.contains("eth")) {
            return "🪙";
        }
        if (title.contains("alien") || title.contains("ufo") || title.contains("ovni")) {
            return "👽";
        }
        return "✨";
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

    private void promptAndPlaceBet(long eventId, OutcomeLabel outcome) {
        if (currentUserId == null) return;
        TextInputDialog dialog = new TextInputDialog("10");
        dialog.setTitle("Place Bet");
        dialog.setHeaderText("Enter amount to bet on " + outcome);
        dialog.setContentText("Amount ($NVB):");
        if (dialog.getDialogPane().getStylesheets().isEmpty()) {
            try {
                dialog.getDialogPane().getStylesheets().add(
                    getClass().getResource("/styles.css").toExternalForm()
                );
            } catch (Exception ignored) {}
        }
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

    public BorderPane getView() {
        return root;
    }

    public void setBalance(double balance) {
        String formatted = String.format("%.2f", balance);
        if (sidebarBalance != null) sidebarBalance.setText(formatted);
        if (topbarBalance != null) topbarBalance.setText(formatted);
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

    public void setOnPortfolioClick(Runnable onPortfolioClick) {
        this.onPortfolioClick = onPortfolioClick;
    }

    public void setOnWalletClick(Runnable onWalletClick) {
        this.onWalletClick = onWalletClick;
    }

    public void setOnCasino(Runnable onCasino) {
        this.onCasino = onCasino;
    }

    public void setOnHistoryClick(Runnable onHistoryClick) {
        this.onHistoryClick = onHistoryClick;
    }
}
