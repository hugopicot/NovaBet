package com.polymarket.ui.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public final class ChromeFactory {

    public enum NavId { MARKETS, PORTFOLIO, CREATE, WALLET, HISTORY, CASINO }

    private ChromeFactory() {}

    public static VBox sidebar(NavId active, NavCallbacks cb) {
        VBox sidebar = new VBox(0);
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPrefWidth(220);
        sidebar.setMinWidth(220);
        sidebar.setMaxWidth(220);

        VBox top = new VBox(0);
        top.setPadding(new Insets(20, 14, 0, 14));

        HBox brand = new HBox(10);
        brand.setAlignment(Pos.CENTER_LEFT);
        brand.setPadding(new Insets(0, 0, 22, 0));

        StackPane logoIcon = new StackPane();
        logoIcon.getStyleClass().add("logo-icon");
        logoIcon.setPrefSize(24, 24);
        logoIcon.setMinSize(24, 24);
        logoIcon.setMaxSize(24, 24);
        Label logoLetter = new Label("N");
        logoLetter.setTextFill(javafx.scene.paint.Color.web("#2A1F0C"));
        logoLetter.setFont(Font.font("Inter", FontWeight.EXTRA_BOLD, 13));
        logoIcon.getChildren().add(logoLetter);

        VBox brandText = new VBox(0);
        HBox brandLine = new HBox(0);
        Label brandWhite = new Label("Nova");
        brandWhite.getStyleClass().add("logo-text");
        brandWhite.setFont(Font.font("Inter", FontWeight.BOLD, 15));
        Label brandGold = new Label("Bet");
        brandGold.getStyleClass().add("logo-text-gold");
        brandGold.setFont(Font.font("Inter", FontWeight.BOLD, 15));
        brandLine.getChildren().addAll(brandWhite, brandGold);
        Label version = new Label("v0.4.2 · alpha");
        version.getStyleClass().add("logo-version");
        brandText.getChildren().addAll(brandLine, version);

        brand.getChildren().addAll(logoIcon, brandText);

        Label navSection = new Label("NAVIGATION");
        navSection.getStyleClass().add("nav-section-label");
        navSection.setPadding(new Insets(0, 0, 6, 6));

        VBox navItems = new VBox(2);
        navItems.getChildren().addAll(
            navItem("Markets",       "▦", active == NavId.MARKETS,   cb.markets, false),
            navItem("Portfolio",     "◧", active == NavId.PORTFOLIO, cb.portfolio, false),
            navItem("Create market", "+", active == NavId.CREATE,    cb.create, false),
            navItem("Wallet",        "⬢", active == NavId.WALLET,    cb.wallet, false),
            navItem("History",       "≡", active == NavId.HISTORY,   cb.history, false)
        );

        Label casinoSection = new Label("ENTERTAINMENT");
        casinoSection.getStyleClass().add("nav-section-label");
        casinoSection.setPadding(new Insets(14, 0, 6, 6));

        HBox casinoItem = navItem("★ Casino", "★", active == NavId.CASINO, cb.casino, true);
        VBox casinoItems = new VBox(2);
        casinoItems.getChildren().add(casinoItem);

        top.getChildren().addAll(brand, navSection, navItems, casinoSection, casinoItems);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        VBox bottom = new VBox(10);
        bottom.setPadding(new Insets(0, 14, 18, 14));

        VBox balance = new VBox(6);
        balance.getStyleClass().add("balance-box");
        balance.setPadding(new Insets(12, 14, 12, 14));

        Label bLabel = new Label("BALANCE");
        bLabel.getStyleClass().add("balance-label");

        HBox bRow = new HBox(4);
        bRow.setAlignment(Pos.BASELINE_LEFT);
        Label bValue = new Label("0.00");
        bValue.getStyleClass().add("balance-value");
        bValue.setFont(Font.font("JetBrains Mono", FontWeight.BOLD, 20));
        bValue.setId("sidebarBalanceValue");
        Label bCurr = new Label("$NVB");
        bCurr.getStyleClass().add("balance-currency");
        bCurr.setFont(Font.font("Inter", FontWeight.BOLD, 11));
        bRow.getChildren().addAll(bValue, bCurr);

        balance.getChildren().addAll(bLabel, bRow);
        bottom.getChildren().add(balance);

        sidebar.getChildren().addAll(top, spacer, bottom);
        return sidebar;
    }

    public static Label findSidebarBalance(VBox sidebar) {
        return (Label) sidebar.lookup("#sidebarBalanceValue");
    }

    private static HBox navItem(String text, String iconChar, boolean active, Runnable onClick, boolean casino) {
        HBox item = new HBox(10);
        item.setAlignment(Pos.CENTER_LEFT);
        item.setPadding(new Insets(8, 10, 8, 10));
        item.setCursor(Cursor.HAND);
        if (casino) {
            item.getStyleClass().add("nav-item-casino");
        } else if (active) {
            item.getStyleClass().add("nav-item-active");
        } else {
            item.getStyleClass().add("nav-item");
        }

        Label icon = new Label(iconChar);
        icon.setFont(Font.font("Inter", FontWeight.BOLD, 12));
        if (casino) {
            icon.setTextFill(javafx.scene.paint.Color.web("#E5C84A"));
        } else {
            icon.setTextFill(active
                ? javafx.scene.paint.Color.web("#F5F6F7")
                : javafx.scene.paint.Color.web("#9B9DA3"));
        }
        icon.setMinWidth(16);
        icon.setAlignment(Pos.CENTER);

        Label label = new Label(text);
        label.getStyleClass().add(active ? "nav-text-active" : "nav-text");
        label.setFont(Font.font("Inter", FontWeight.MEDIUM, 13));

        item.getChildren().addAll(icon, label);

        if (onClick != null) {
            item.setOnMouseClicked(e -> onClick.run());
        }
        return item;
    }

    public static HBox topbar(String pageTitle, String searchPrompt, Runnable onDeposit) {
        return topbar(pageTitle, searchPrompt, onDeposit, null);
    }

    public static HBox topbar(String pageTitle, String searchPrompt, Runnable onDeposit, Runnable onSearch) {
        HBox bar = new HBox(16);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setPadding(new Insets(0, 20, 0, 20));
        bar.setMinHeight(52);
        bar.setPrefHeight(52);
        bar.setStyle("-fx-background-color: -bg-1; -fx-border-color: transparent transparent -line-soft transparent; -fx-border-width: 0 0 1 0;");

        Label title = new Label(pageTitle);
        title.getStyleClass().add("markets-title");
        title.setFont(Font.font("Inter", FontWeight.BOLD, 14));

        HBox search = new HBox(0);
        search.getStyleClass().add("search-box");
        search.setAlignment(Pos.CENTER_LEFT);
        search.setMaxWidth(420);
        search.setPrefWidth(380);
        search.setPrefHeight(32);
        Label searchIcon = new Label("⌕");
        searchIcon.setTextFill(javafx.scene.paint.Color.web("#71747A"));
        searchIcon.setFont(Font.font("Inter", 13));
        searchIcon.setMinWidth(16);
        TextField sf = new TextField();
        sf.setPromptText(searchPrompt);
        sf.getStyleClass().add("search-field");
        sf.setFont(Font.font("Inter", 12));
        HBox.setHgrow(sf, Priority.ALWAYS);
        search.getChildren().addAll(searchIcon, sf);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox pill = new HBox(8);
        pill.getStyleClass().add("balance-pill");
        pill.setAlignment(Pos.CENTER_LEFT);
        StackPane coin = new StackPane();
        coin.getStyleClass().add("coin-icon");
        Label cl = new Label("N");
        cl.getStyleClass().add("coin-icon-label");
        coin.getChildren().add(cl);
        Label balValue = new Label("0.00");
        balValue.getStyleClass().add("header-balance-value");
        balValue.setFont(Font.font("JetBrains Mono", FontWeight.BOLD, 12));
        balValue.setId("topbarBalanceValue");
        Label balCurr = new Label("$NVB");
        balCurr.getStyleClass().add("header-balance-currency");
        balCurr.setFont(Font.font("Inter", 11));
        pill.getChildren().addAll(coin, balValue, balCurr);

        Button bell = new Button("⌾");
        bell.getStyleClass().add("notification-btn");
        bell.setPrefSize(32, 32);
        bell.setMinSize(32, 32);
        bell.setFont(Font.font("Inter", 12));
        bell.setTextFill(javafx.scene.paint.Color.web("#9B9DA3"));

        Button deposit = new Button("+ Deposit");
        deposit.getStyleClass().add("deposit-btn");
        deposit.setFont(Font.font("Inter", FontWeight.BOLD, 11));
        if (onDeposit != null) deposit.setOnAction(e -> onDeposit.run());

        bar.getChildren().addAll(title, search, spacer, pill, bell, deposit);
        return bar;
    }

    public static Label findTopbarBalance(HBox topbar) {
        return (Label) topbar.lookup("#topbarBalanceValue");
    }

    public static HBox breadcrumb(String left, String current, Runnable onLeft) {
        HBox b = new HBox(8);
        b.setAlignment(Pos.CENTER_LEFT);
        Label l1 = new Label(left);
        l1.getStyleClass().add("breadcrumb-text");
        l1.setFont(Font.font("Inter", FontWeight.MEDIUM, 14));
        l1.setCursor(Cursor.HAND);
        if (onLeft != null) l1.setOnMouseClicked(e -> onLeft.run());
        Label sep = new Label("/");
        sep.getStyleClass().add("breadcrumb-separator");
        sep.setFont(Font.font("Inter", 14));
        Label cur = new Label(current);
        cur.getStyleClass().add("breadcrumb-text-active");
        cur.setFont(Font.font("Inter", FontWeight.MEDIUM, 14));
        b.getChildren().addAll(l1, sep, cur);
        return b;
    }

    public static final class NavCallbacks {
        public final Runnable markets;
        public final Runnable portfolio;
        public final Runnable create;
        public final Runnable wallet;
        public final Runnable history;
        public final Runnable casino;

        public NavCallbacks(Runnable markets, Runnable portfolio, Runnable create,
                            Runnable wallet, Runnable history, Runnable casino) {
            this.markets = markets;
            this.portfolio = portfolio;
            this.create = create;
            this.wallet = wallet;
            this.history = history;
            this.casino = casino;
        }
    }
}
