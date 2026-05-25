package com.polymarket.ui;

import com.polymarket.dao.betsDao;
import com.polymarket.dao.usersDao;
import com.polymarket.domain.dto.BetRequest;
import com.polymarket.domain.dto.OutcomeLabel;
import com.polymarket.model.bets;
import com.polymarket.model.events;
import com.polymarket.model.outcomes;
import com.polymarket.model.users;
import com.polymarket.ui.components.ChromeFactory;

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

    private Label sidebarBalance;
    private Label topbarBalance;
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
    private StackPane headerIcon;
    private Label headerIconLabel;

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
        buildLayout();
    }

    private void buildLayout() {
        VBox sidebar = ChromeFactory.sidebar(
            ChromeFactory.NavId.MARKETS,
            new ChromeFactory.NavCallbacks(
                () -> { if (onMarketsClick != null) onMarketsClick.run(); },
                () -> { if (onPortfolioClick != null) onPortfolioClick.run(); },
                null,
                () -> { if (onWalletClick != null) onWalletClick.run(); },
                () -> { if (onHistoryClick != null) onHistoryClick.run(); },
                null
            )
        );
        sidebarBalance = ChromeFactory.findSidebarBalance(sidebar);
        root.setLeft(sidebar);

        BorderPane main = new BorderPane();
        main.getStyleClass().add("main-content");
        HBox topbar = ChromeFactory.topbar("Market detail", "Search markets...", () -> {
            if (onWalletClick != null) onWalletClick.run();
        });
        topbarBalance = ChromeFactory.findTopbarBalance(topbar);
        main.setTop(topbar);
        main.setCenter(buildContent());
        root.setCenter(main);
    }

    private ScrollPane buildContent() {
        VBox content = new VBox(16);
        content.setPadding(new Insets(14, 20, 24, 20));

        HBox crumbRow = new HBox();
        crumbRow.setAlignment(Pos.CENTER_LEFT);
        crumbRow.getChildren().add(ChromeFactory.breadcrumb("Markets", "Detail",
            () -> { if (onMarketsClick != null) onMarketsClick.run(); }));
        content.getChildren().add(crumbRow);

        content.getChildren().add(buildHeader());
        content.getChildren().add(buildMiddle());
        content.getChildren().add(buildBottom());

        ScrollPane scroll = new ScrollPane(content);
        scroll.getStyleClass().add("detail-scroll");
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        return scroll;
    }

    private VBox buildHeader() {
        VBox wrap = new VBox(10);
        wrap.getStyleClass().add("info-card");
        wrap.setPadding(new Insets(20));

        HBox row = new HBox(16);
        row.setAlignment(Pos.TOP_LEFT);

        headerIcon = new StackPane();
        headerIcon.getStyleClass().add("market-icon-container");
        headerIcon.setPrefSize(56, 56);
        headerIcon.setMinSize(56, 56);
        headerIcon.setMaxSize(56, 56);
        headerIconLabel = new Label("✨");
        headerIconLabel.setFont(Font.font("Segoe UI Emoji", 26));
        headerIcon.getChildren().add(headerIconLabel);

        VBox info = new VBox(8);
        HBox.setHgrow(info, Priority.ALWAYS);

        Label category = new Label("MARKETS");
        category.getStyleClass().add("label-uppercase");

        headerQuestion = new Label("");
        headerQuestion.getStyleClass().add("detail-question");
        headerQuestion.setFont(Font.font("Inter", FontWeight.BOLD, 22));
        headerQuestion.setWrapText(true);

        HBox meta = new HBox(14);
        meta.setAlignment(Pos.CENTER_LEFT);
        volumeLabel = new Label("");
        volumeLabel.getStyleClass().add("meta-label");
        volumeLabel.setFont(Font.font("Inter", 12));
        endsLabel = new Label("");
        endsLabel.getStyleClass().add("meta-label");
        endsLabel.setFont(Font.font("Inter", 12));

        HBox live = new HBox(5);
        live.setAlignment(Pos.CENTER);
        live.getStyleClass().add("live-badge");
        Region dot = new Region();
        dot.getStyleClass().add("live-dot");
        dot.setPrefSize(5, 5);
        dot.setMinSize(5, 5);
        dot.setMaxSize(5, 5);
        liveText = new Label("LIVE");
        liveText.getStyleClass().add("live-text");
        liveText.setFont(Font.font("Inter", FontWeight.BOLD, 9));
        live.getChildren().addAll(dot, liveText);

        meta.getChildren().addAll(volumeLabel, endsLabel, live);
        info.getChildren().addAll(category, headerQuestion, meta);

        VBox probBox = new VBox(2);
        probBox.setAlignment(Pos.TOP_RIGHT);
        probLabel = new Label("YES PROBABILITY");
        probLabel.getStyleClass().add("label-uppercase");
        HBox probRow = new HBox(2);
        probRow.setAlignment(Pos.BASELINE_RIGHT);
        probValue = new Label("--");
        probValue.getStyleClass().add("prob-value");
        probValue.setFont(Font.font("JetBrains Mono", FontWeight.BOLD, 38));
        probPercent = new Label("%");
        probPercent.getStyleClass().add("prob-percent");
        probPercent.setFont(Font.font("JetBrains Mono", FontWeight.BOLD, 18));
        probRow.getChildren().addAll(probValue, probPercent);
        probBox.getChildren().addAll(probLabel, probRow);

        HBox actions = new HBox(8);
        actions.setAlignment(Pos.TOP_RIGHT);
        actions.setPadding(new Insets(0, 0, 0, 16));

        Button edit = new Button("Edit");
        edit.getStyleClass().add("action-btn-edit");
        edit.setFont(Font.font("Inter", FontWeight.MEDIUM, 12));
        edit.setOnAction(e -> { if (onEditMarket != null) onEditMarket.run(); });

        Button del = new Button("Delete");
        del.getStyleClass().add("action-btn-delete");
        del.setFont(Font.font("Inter", FontWeight.MEDIUM, 12));
        del.setOnAction(e -> { if (onDeleteMarket != null) onDeleteMarket.run(); });

        actions.getChildren().addAll(edit, del);

        row.getChildren().addAll(headerIcon, info, probBox, actions);
        wrap.getChildren().add(row);
        return wrap;
    }

    private HBox buildMiddle() {
        HBox row = new HBox(16);
        row.setAlignment(Pos.TOP_LEFT);

        VBox left = buildProbabilityCard();
        HBox.setHgrow(left, Priority.ALWAYS);

        VBox trade = buildTradePanel();
        trade.setMinWidth(320);
        trade.setPrefWidth(340);
        trade.setMaxWidth(360);

        row.getChildren().addAll(left, trade);
        return row;
    }

    private VBox buildProbabilityCard() {
        VBox card = new VBox(12);
        card.getStyleClass().add("info-card");
        card.setPadding(new Insets(20));

        Label title = new Label("PROBABILITY");
        title.getStyleClass().add("info-card-title");

        VBox barWrap = new VBox(8);
        barWrap.setPadding(new Insets(16, 0, 0, 0));

        HBox legend = new HBox();
        legend.setAlignment(Pos.CENTER_LEFT);
        HBox yesL = new HBox(6);
        yesL.setAlignment(Pos.CENTER_LEFT);
        Region yd = new Region();
        yd.setStyle("-fx-background-color: -yes; -fx-background-radius: 999;");
        yd.setPrefSize(8, 8);
        yd.setMinSize(8, 8);
        yd.setMaxSize(8, 8);
        Label yesLab = new Label("YES");
        yesLab.setStyle("-fx-text-fill: -yes; -fx-font-size: 11px; -fx-font-weight: bold;");
        yesL.getChildren().addAll(yd, yesLab);

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        HBox noL = new HBox(6);
        noL.setAlignment(Pos.CENTER_RIGHT);
        Label noLab = new Label("NO");
        noLab.setStyle("-fx-text-fill: -no; -fx-font-size: 11px; -fx-font-weight: bold;");
        Region nd = new Region();
        nd.setStyle("-fx-background-color: -no; -fx-background-radius: 999;");
        nd.setPrefSize(8, 8);
        nd.setMinSize(8, 8);
        nd.setMaxSize(8, 8);
        noL.getChildren().addAll(noLab, nd);

        legend.getChildren().addAll(yesL, sp, noL);
        barWrap.getChildren().add(legend);

        StackPane bigBar = new StackPane();
        bigBar.setStyle("-fx-background-color: -no-dim; -fx-background-radius: 4;");
        bigBar.setPrefHeight(10);
        bigBar.setMinHeight(10);
        bigBar.setMaxHeight(10);
        bigBar.setAlignment(Pos.CENTER_LEFT);
        Region yesFill = new Region();
        yesFill.setStyle("-fx-background-color: -yes; -fx-background-radius: 4 0 0 4;");
        yesFill.setPrefHeight(10);
        yesFill.setMinHeight(10);
        yesFill.setMaxHeight(10);
        bigBar.getChildren().add(yesFill);

        HBox legendVals = new HBox();
        legendVals.setAlignment(Pos.CENTER_LEFT);
        Label yv = new Label("--%");
        yv.setStyle("-fx-text-fill: -yes; -fx-font-family: 'JetBrains Mono'; -fx-font-size: 14px; -fx-font-weight: bold;");
        yv.setId("yesProbLabel");
        Region s2 = new Region();
        HBox.setHgrow(s2, Priority.ALWAYS);
        Label nv = new Label("--%");
        nv.setStyle("-fx-text-fill: -no; -fx-font-family: 'JetBrains Mono'; -fx-font-size: 14px; -fx-font-weight: bold;");
        nv.setId("noProbLabel");
        legendVals.getChildren().addAll(yv, s2, nv);

        barWrap.getChildren().addAll(bigBar, legendVals);

        bigBar.widthProperty().addListener((o, ov, nvw) -> updateProbBarFill(yesFill, nvw.doubleValue()));

        card.getChildren().addAll(title, barWrap);
        return card;
    }

    private void updateProbBarFill(Region fill, double totalWidth) {
        outcomes y = findOutcome(OutcomeLabel.YES);
        if (y == null) return;
        double pct = y.getOdds();
        fill.setPrefWidth(totalWidth * pct);
    }

    private VBox buildTradePanel() {
        VBox panel = new VBox(14);
        panel.getStyleClass().add("trade-panel");
        panel.setPadding(new Insets(16));

        Label title = new Label("PLACE A TRADE");
        title.getStyleClass().add("info-card-title");

        HBox yn = new HBox(0);
        yn.getStyleClass().add("yes-no-toggle");
        yesBtn = new Button("Yes --¢");
        yesBtn.getStyleClass().add("yes-toggle-active");
        yesBtn.setFont(Font.font("Inter", FontWeight.BOLD, 13));
        HBox.setHgrow(yesBtn, Priority.ALWAYS);
        yesBtn.setMaxWidth(Double.MAX_VALUE);
        yesBtn.setOnAction(e -> selectOutcome(OutcomeLabel.YES));
        noBtn = new Button("No --¢");
        noBtn.getStyleClass().add("no-toggle");
        noBtn.setFont(Font.font("Inter", FontWeight.MEDIUM, 13));
        HBox.setHgrow(noBtn, Priority.ALWAYS);
        noBtn.setMaxWidth(Double.MAX_VALUE);
        noBtn.setOnAction(e -> selectOutcome(OutcomeLabel.NO));
        yn.getChildren().addAll(yesBtn, noBtn);

        HBox bs = new HBox(6);
        buyToggle = new Button("Buy");
        buyToggle.getStyleClass().add("buy-sell-active");
        buyToggle.setFont(Font.font("Inter", FontWeight.BOLD, 12));
        buyToggle.setOnAction(e -> setTradeMode(true));
        sellToggle = new Button("Sell");
        sellToggle.getStyleClass().add("buy-sell-toggle");
        sellToggle.setFont(Font.font("Inter", FontWeight.MEDIUM, 12));
        sellToggle.setOnAction(e -> setTradeMode(false));
        bs.getChildren().addAll(buyToggle, sellToggle);

        Label amtLabel = new Label("AMOUNT");
        amtLabel.getStyleClass().add("amount-label");

        HBox amtBox = new HBox(0);
        amtBox.getStyleClass().add("amount-input-box");
        amountField = new TextField();
        amountField.setPromptText("0.00");
        amountField.getStyleClass().add("amount-field");
        amountField.setFont(Font.font("JetBrains Mono", FontWeight.BOLD, 16));
        HBox.setHgrow(amountField, Priority.ALWAYS);
        Label curr = new Label("$NVB");
        curr.getStyleClass().add("currency-label");
        curr.setFont(Font.font("Inter", FontWeight.MEDIUM, 12));
        amtBox.getChildren().addAll(amountField, curr);
        amountField.textProperty().addListener((o, ov, nv) -> updateSummary());

        HBox quick = new HBox(6);
        Button q25 = quickAmount("$25"); q25.setOnAction(e -> amountField.setText("25"));
        Button q100 = quickAmount("$100"); q100.setOnAction(e -> amountField.setText("100"));
        Button q500 = quickAmount("$500"); q500.setOnAction(e -> amountField.setText("500"));
        Button qmax = quickAmount("MAX"); qmax.setOnAction(e -> amountField.setText("10000"));
        HBox.setHgrow(q25, Priority.ALWAYS); q25.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(q100, Priority.ALWAYS); q100.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(q500, Priority.ALWAYS); q500.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(qmax, Priority.ALWAYS); qmax.setMaxWidth(Double.MAX_VALUE);
        quick.getChildren().addAll(q25, q100, q500, qmax);

        Region divider = new Region();
        divider.getStyleClass().add("divider");

        VBox summary = new VBox(8);
        summary.setPadding(new Insets(4, 0, 4, 0));
        summary.getChildren().addAll(
            summaryRow("Avg price", summaryAvgPrice = mono("--", false)),
            summaryRow("Shares",    summaryShares = mono("--", false)),
            summaryRow("Potential", summaryReturn = mono("--", true))
        );

        buyButton = new Button("Buy YES");
        buyButton.getStyleClass().add("buy-button");
        buyButton.setFont(Font.font("Inter", FontWeight.BOLD, 13));
        buyButton.setMaxWidth(Double.MAX_VALUE);
        buyButton.setOnAction(e -> executeTrade());

        panel.getChildren().addAll(title, yn, bs, amtLabel, amtBox, quick, divider, summary, buyButton);
        return panel;
    }

    private Label mono(String text, boolean highlight) {
        Label l = new Label(text);
        l.getStyleClass().add(highlight ? "summary-value-highlight" : "summary-value");
        l.setFont(Font.font("JetBrains Mono", highlight ? FontWeight.BOLD : FontWeight.MEDIUM, 12));
        return l;
    }

    private HBox summaryRow(String label, Label value) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        Label l = new Label(label);
        l.getStyleClass().add("summary-label");
        l.setFont(Font.font("Inter", 12));
        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);
        row.getChildren().addAll(l, sp, value);
        return row;
    }

    private Button quickAmount(String text) {
        Button b = new Button(text);
        b.getStyleClass().add("quick-amount-btn");
        b.setFont(Font.font("JetBrains Mono", FontWeight.MEDIUM, 11));
        return b;
    }

    private void selectOutcome(OutcomeLabel outcome) {
        selectedOutcome = outcome;
        if (outcome == OutcomeLabel.YES) {
            yesBtn.getStyleClass().setAll("yes-toggle-active");
            noBtn.getStyleClass().setAll("no-toggle");
        } else {
            noBtn.getStyleClass().setAll("yes-toggle-active");
            yesBtn.getStyleClass().setAll("no-toggle");
        }
        if (probLabel != null) probLabel.setText(outcome == OutcomeLabel.YES ? "YES PROBABILITY" : "NO PROBABILITY");
        if (currentOutcomes != null) {
            outcomes target = findOutcome(outcome);
            if (target != null && probValue != null) {
                int pct = (int) Math.round(target.getOdds() * 100);
                probValue.setText(String.valueOf(pct));
            }
        }
        updateSummary();
        updateBuyButtonText();
    }

    private void setTradeMode(boolean buy) {
        isBuyMode = buy;
        if (buy) {
            buyToggle.getStyleClass().setAll("buy-sell-active");
            sellToggle.getStyleClass().setAll("buy-sell-toggle");
        } else {
            sellToggle.getStyleClass().setAll("buy-sell-active");
            buyToggle.getStyleClass().setAll("buy-sell-toggle");
        }
        updateBuyButtonText();
    }

    private void updateBuyButtonText() {
        if (buyButton == null) return;
        String oc = selectedOutcome == OutcomeLabel.YES ? "YES" : "NO";
        String act = isBuyMode ? "Buy" : "Sell";
        String shares = summaryShares != null ? summaryShares.getText() : "--";
        if ("--".equals(shares)) {
            buyButton.setText(act + " " + oc);
        } else {
            buyButton.setText(act + " " + oc + " · " + shares + " sh");
        }
    }

    private void updateSummary() {
        if (currentOutcomes == null || currentOutcomes.isEmpty()) return;
        String amt = amountField.getText();
        if (amt == null || amt.isBlank()) {
            summaryAvgPrice.setText("--");
            summaryShares.setText("--");
            summaryReturn.setText("--");
            updateBuyButtonText();
            return;
        }
        try {
            BigDecimal amount = new BigDecimal(amt.trim());
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                summaryAvgPrice.setText("--");
                summaryShares.setText("--");
                summaryReturn.setText("--");
                updateBuyButtonText();
                return;
            }
            outcomes target = findOutcome(selectedOutcome);
            if (target == null) return;
            BigDecimal price = BigDecimal.valueOf(target.getOdds()).setScale(4, RoundingMode.HALF_UP);
            if (selectedOutcome == OutcomeLabel.NO) {
                price = BigDecimal.ONE.subtract(price).setScale(4, RoundingMode.HALF_UP);
            }
            int sc = amount.divide(price, RoundingMode.DOWN).intValue();
            BigDecimal cost = price.multiply(BigDecimal.valueOf(sc)).setScale(2, RoundingMode.HALF_UP);
            BigDecimal win = BigDecimal.valueOf(sc).subtract(cost).setScale(2, RoundingMode.HALF_UP);
            double pct = cost.compareTo(BigDecimal.ZERO) > 0
                ? win.divide(cost, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).doubleValue() : 0;
            summaryAvgPrice.setText(price.multiply(BigDecimal.valueOf(100)).setScale(1, RoundingMode.HALF_UP).toPlainString() + "¢");
            summaryShares.setText(String.format("%,d", sc));
            summaryReturn.setText("+" + win.toPlainString() + " (" + String.format("%.1f", pct) + "%)");
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
        String amt = amountField.getText();
        if (amt == null || amt.isBlank()) return;
        try {
            BigDecimal amount = new BigDecimal(amt.trim());
            if (amount.compareTo(BigDecimal.ZERO) > 0) {
                onPlaceBet.accept(new BetRequest(currentUserId, currentEventId, selectedOutcome, amount, true));
                amountField.clear();
                updateSummary();
            }
        } catch (NumberFormatException | ArithmeticException ex) {
            Alert al = new Alert(Alert.AlertType.ERROR);
            al.setTitle("Bet failed");
            al.setHeaderText(null);
            al.setContentText(ex.getMessage());
            al.showAndWait();
        }
    }

    private HBox buildBottom() {
        HBox row = new HBox(16);
        row.setAlignment(Pos.TOP_LEFT);

        VBox left = new VBox(16);
        HBox.setHgrow(left, Priority.ALWAYS);
        left.getChildren().addAll(buildAbout(), buildTopHolders());

        VBox right = buildOrderBook();
        right.setMinWidth(320);
        right.setPrefWidth(340);
        right.setMaxWidth(360);

        row.getChildren().addAll(left, right);
        return row;
    }

    private VBox buildAbout() {
        VBox card = new VBox(10);
        card.getStyleClass().add("info-card");
        card.setPadding(new Insets(16));
        Label t = new Label("ABOUT THIS MARKET");
        t.getStyleClass().add("info-card-title");
        aboutDescription = new Label("");
        aboutDescription.getStyleClass().add("info-card-text");
        aboutDescription.setFont(Font.font("Inter", 13));
        aboutDescription.setWrapText(true);
        card.getChildren().addAll(t, aboutDescription);
        return card;
    }

    private VBox buildTopHolders() {
        VBox card = new VBox(10);
        card.getStyleClass().add("info-card");
        card.setPadding(new Insets(16));
        Label t = new Label("TOP HOLDERS");
        t.getStyleClass().add("info-card-title");
        holdersList = new VBox(8);
        card.getChildren().addAll(t, holdersList);
        return card;
    }

    private void loadTopHolders() {
        if (holdersList == null || currentEventId == null) return;
        holdersList.getChildren().clear();
        try {
            betsDao bd = new betsDao();
            usersDao ud = new usersDao();
            List<bets> all = bd.getAll();
            Map<Integer, Map<String, BigDecimal>> agg = new HashMap<>();
            for (bets bet : all) {
                outcomes o = null;
                if (currentOutcomes != null) {
                    for (outcomes oc : currentOutcomes) {
                        if (oc.getId().intValue() == bet.getOutcome_id()) { o = oc; break; }
                    }
                }
                if (o == null) continue;
                agg.putIfAbsent(bet.getUser_id(), new HashMap<>());
                Map<String, BigDecimal> per = agg.get(bet.getUser_id());
                per.put(o.getLabel(), per.getOrDefault(o.getLabel(), BigDecimal.ZERO).add(BigDecimal.valueOf(bet.getAmount())));
            }
            List<HolderInfo> sorted = new ArrayList<>();
            for (Map.Entry<Integer, Map<String, BigDecimal>> e : agg.entrySet()) {
                for (Map.Entry<String, BigDecimal> h : e.getValue().entrySet()) {
                    sorted.add(new HolderInfo(e.getKey(), h.getKey(), h.getValue()));
                }
            }
            sorted.sort(Comparator.comparing(HolderInfo::amount).reversed());
            int max = Math.min(4, sorted.size());
            for (int i = 0; i < max; i++) {
                HolderInfo h = sorted.get(i);
                users u = ud.findById((long) h.userId());
                String name = u != null ? u.getUsername() : "user" + h.userId();
                if (name.length() > 14) name = name.substring(0, 12) + "…";
                holdersList.getChildren().add(holderRow(name, h.label(),
                    "$" + h.amount().setScale(0, RoundingMode.HALF_UP).toPlainString()));
            }
            if (max == 0) {
                Label empty = new Label("No holders yet");
                empty.getStyleClass().add("empty-state");
                holdersList.getChildren().add(empty);
            }
        } catch (SQLException ex) {
            Label err = new Label("Error loading holders");
            err.getStyleClass().add("empty-state");
            holdersList.getChildren().add(err);
        }
    }

    private record HolderInfo(int userId, String label, BigDecimal amount) {}

    private HBox holderRow(String name, String position, String amt) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        Region av = new Region();
        av.getStyleClass().add("holder-avatar");
        av.setPrefSize(24, 24);
        av.setMinSize(24, 24);
        av.setMaxSize(24, 24);
        Label addr = new Label(name);
        addr.getStyleClass().add("holder-address");
        addr.setFont(Font.font("Inter", FontWeight.MEDIUM, 13));
        Label pos = new Label(position);
        pos.getStyleClass().add("YES".equalsIgnoreCase(position) ? "holder-yes-badge" : "holder-no-badge");
        pos.setFont(Font.font("JetBrains Mono", FontWeight.BOLD, 10));
        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);
        Label am = new Label(amt);
        am.getStyleClass().add("holder-amount");
        am.setFont(Font.font("JetBrains Mono", FontWeight.MEDIUM, 12));
        row.getChildren().addAll(av, addr, pos, sp, am);
        return row;
    }

    private VBox buildOrderBook() {
        VBox ob = new VBox(10);
        ob.getStyleClass().add("orderbook-panel");
        ob.setPadding(new Insets(16));

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        Label t = new Label("ORDER BOOK");
        t.getStyleClass().add("orderbook-title");
        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);
        orderBookSpread = new Label("");
        orderBookSpread.getStyleClass().add("spread-label");
        orderBookSpread.setFont(Font.font("JetBrains Mono", 11));
        header.getChildren().addAll(t, sp, orderBookSpread);

        orderBookGrid = new GridPane();
        orderBookGrid.setVgap(4);

        Label ph = new Label("Price");
        ph.getStyleClass().add("ob-col-label");
        Label sh = new Label("Shares");
        sh.getStyleClass().add("ob-col-label");
        sh.setAlignment(Pos.CENTER_RIGHT);
        Label th = new Label("Total");
        th.getStyleClass().add("ob-col-label");
        th.setAlignment(Pos.CENTER_RIGHT);
        orderBookGrid.addRow(0, ph, sh, th);

        ColumnConstraints c1 = new ColumnConstraints();
        c1.setPercentWidth(34);
        ColumnConstraints c2 = new ColumnConstraints();
        c2.setPercentWidth(33);
        c2.setHalignment(HPos.RIGHT);
        ColumnConstraints c3 = new ColumnConstraints();
        c3.setPercentWidth(33);
        c3.setHalignment(HPos.RIGHT);
        orderBookGrid.getColumnConstraints().addAll(c1, c2, c3);

        orderBookMidPrice = new Label("");
        orderBookMidPrice.getStyleClass().add("mid-price");
        orderBookMidPrice.setFont(Font.font("JetBrains Mono", FontWeight.BOLD, 13));

        ob.getChildren().addAll(header, orderBookGrid);
        return ob;
    }

    private void loadOrderBook() {
        if (orderBookGrid == null || currentOutcomes == null) return;
        orderBookGrid.getChildren().removeIf(n -> GridPane.getRowIndex(n) != null && GridPane.getRowIndex(n) > 0);
        outcomes y = findOutcome(OutcomeLabel.YES);
        outcomes n = findOutcome(OutcomeLabel.NO);
        if (y == null || n == null) return;
        double yp = y.getOdds(), np = n.getOdds();
        double spread = Math.abs(yp + np - 1.0) * 100;
        orderBookSpread.setText("SPREAD " + String.format("%.1f", spread) + "¢");
        int r = 1;
        for (int i = 3; i >= 1; i--) {
            double price = np * 100 + i * 0.3;
            Label p = new Label(String.format("%.1f¢", price));
            p.getStyleClass().add("ob-ask-price");
            p.setFont(Font.font("JetBrains Mono", 11));
            Label s = new Label(String.format("%,d", 100 * i));
            s.getStyleClass().add("ob-ask-shares");
            s.setFont(Font.font("JetBrains Mono", 11));
            Label t = new Label(String.format("$%,d", (int)(price * i * 100 / 100)));
            t.getStyleClass().add("ob-ask-total");
            t.setFont(Font.font("JetBrains Mono", 11));
            orderBookGrid.addRow(r++, p, s, t);
        }
        orderBookMidPrice.setText(String.format("%.1f¢", yp * 100));
        GridPane.setHalignment(orderBookMidPrice, HPos.CENTER);
        orderBookGrid.add(orderBookMidPrice, 0, r, 3, 1);
        r++;
        for (int i = 1; i <= 3; i++) {
            double price = yp * 100 - i * 0.3;
            Label p = new Label(String.format("%.1f¢", price));
            p.getStyleClass().add("ob-bid-price");
            p.setFont(Font.font("JetBrains Mono", 11));
            Label s = new Label(String.format("%,d", 120 * i));
            s.getStyleClass().add("ob-bid-shares");
            s.setFont(Font.font("JetBrains Mono", 11));
            Label t = new Label(String.format("$%,d", (int)(price * i * 120 / 100)));
            t.getStyleClass().add("ob-bid-total");
            t.setFont(Font.font("JetBrains Mono", 11));
            orderBookGrid.addRow(r++, p, s, t);
        }
    }

    private outcomes findOutcome(OutcomeLabel label) {
        if (currentOutcomes == null) return null;
        for (outcomes o : currentOutcomes) {
            if (label.name().equalsIgnoreCase(o.getLabel())) return o;
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

        if (headerQuestion != null) headerQuestion.setText(event.getTitle());
        if (aboutDescription != null) {
            aboutDescription.setText(event.getDescription() != null && !event.getDescription().isBlank()
                ? event.getDescription() : "No description available.");
        }
        if (volumeLabel != null) volumeLabel.setText("Status: " + (event.getStatus() != null ? event.getStatus() : "OPEN"));
        if (endsLabel != null && event.getCreatedAt() != null) endsLabel.setText("Created " + event.getCreatedAt());
        if (liveText != null) {
            String s = event.getStatus();
            liveText.setText("OPEN".equalsIgnoreCase(s) ? "LIVE" : (s != null ? s : "LIVE"));
        }
        if (headerIconLabel != null) headerIconLabel.setText(headerEmoji(event));

        outcomes y = findOutcome(OutcomeLabel.YES);
        outcomes n = findOutcome(OutcomeLabel.NO);
        if (y != null) {
            int yp = (int) Math.round(y.getOdds() * 100);
            if (probValue != null && selectedOutcome == OutcomeLabel.YES) probValue.setText(String.valueOf(yp));
            if (yesBtn != null) yesBtn.setText("Yes " + yp + "¢");
            Label ypl = (Label) root.lookup("#yesProbLabel");
            if (ypl != null) ypl.setText(yp + "%");
        }
        if (n != null) {
            int np = (int) Math.round(n.getOdds() * 100);
            if (probValue != null && selectedOutcome == OutcomeLabel.NO) probValue.setText(String.valueOf(np));
            if (noBtn != null) noBtn.setText("No " + np + "¢");
            Label npl = (Label) root.lookup("#noProbLabel");
            if (npl != null) npl.setText(np + "%");
        }

        loadOrderBook();
        loadTopHolders();
        updateSummary();
        updateBuyButtonText();
    }

    private String headerEmoji(events e) {
        String t = e.getTitle() != null ? e.getTitle().toLowerCase() : "";
        if (t.contains("ai") || t.contains("gpt") || t.contains("claude")) return "🤖";
        if (t.contains("mars") || t.contains("rocket") || t.contains("spacex")) return "🚀";
        if (t.contains("psg") || t.contains("foot") || t.contains("champion")) return "⚽";
        if (t.contains("f1") || t.contains("grand prix")) return "🏎️";
        if (t.contains("crypto") || t.contains("bitcoin")) return "🪙";
        if (t.contains("alien") || t.contains("ufo")) return "👽";
        return "✨";
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
        String s = String.format("%.2f", balance);
        if (sidebarBalance != null) sidebarBalance.setText(s);
        if (topbarBalance != null) topbarBalance.setText(s);
    }

    public void setCurrentUserId(Long userId) {
        this.currentUserId = userId;
    }

    public void setOnPlaceBet(Consumer<BetRequest> onPlaceBet) {
        this.onPlaceBet = onPlaceBet;
    }
}
