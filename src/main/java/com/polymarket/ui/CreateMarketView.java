package com.polymarket.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class CreateMarketView {

    private BorderPane root;
    private Runnable onBack;
    private int currentStep = 2;

    public CreateMarketView() {
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
        navItems.getChildren().addAll(
            createNavItem("Markets", false),
            createNavItem("Portfolio", false),
            createNavItem("Create market", true),
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
        mainContent.setCenter(createFormContent());

        return mainContent;
    }

    private VBox createTopBar() {
        VBox topBar = new VBox(0);
        topBar.setPadding(new Insets(0, 24, 0, 24));

        HBox headerRow = new HBox(16);
        headerRow.setAlignment(Pos.CENTER_LEFT);
        headerRow.setPadding(new Insets(16, 0, 12, 0));

        Label pageTitle = new Label("Create market");
        pageTitle.getStyleClass().add("markets-title");
        pageTitle.setFont(Font.font("Inter", FontWeight.BOLD, 16));

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

        headerRow.getChildren().addAll(pageTitle, searchBox, spacer, balanceBox, notifBtn, depositBtn);

        topBar.getChildren().add(headerRow);
        return topBar;
    }

    private VBox createFormContent() {
        VBox content = new VBox(0);
        content.setAlignment(Pos.TOP_CENTER);
        content.setPadding(new Insets(32, 24, 24, 24));

        VBox formContainer = new VBox(24);
        formContainer.setMaxWidth(680);

        Label stepLabel = new Label("STEP 2 OF 3");
        stepLabel.getStyleClass().add("step-label");
        stepLabel.setFont(Font.font("Inter", FontWeight.MEDIUM, 12));

        Label formTitle = new Label("Define your market");
        formTitle.getStyleClass().add("form-page-title");
        formTitle.setFont(Font.font("Inter", FontWeight.BOLD, 24));

        Label formSubtitle = new Label("A clear, binary, time-bounded question. Other traders will provide liquidity.");
        formSubtitle.getStyleClass().add("form-page-subtitle");
        formSubtitle.setFont(Font.font("Inter", 14));
        formSubtitle.setWrapText(true);

        VBox formCard = new VBox(20);
        formCard.getStyleClass().add("form-card");
        formCard.setPadding(new Insets(24, 24, 24, 24));

        formCard.getChildren().addAll(
            createQuestionField(),
            createCategoryAndDateRow(),
            createResolutionSourceField(),
            createLiquidityField(),
            createProbabilitySlider()
        );

        HBox buttonRow = new HBox(12);
        buttonRow.setAlignment(Pos.CENTER_RIGHT);

        Button backBtn = new Button("← Back");
        backBtn.getStyleClass().add("btn-back");
        backBtn.setFont(Font.font("Inter", FontWeight.MEDIUM, 13));
        backBtn.setOnAction(e -> {
            if (onBack != null) onBack.run();
        });

        Button continueBtn = new Button("Continue → Review");
        continueBtn.getStyleClass().add("btn-continue");
        continueBtn.setFont(Font.font("Inter", FontWeight.BOLD, 14));

        buttonRow.getChildren().addAll(backBtn, continueBtn);

        formContainer.getChildren().addAll(stepLabel, formTitle, formSubtitle, formCard, buttonRow);
        content.getChildren().add(formContainer);

        return content;
    }

    private VBox createQuestionField() {
        VBox field = new VBox(8);

        Label label = new Label("QUESTION");
        label.getStyleClass().add("form-field-label");
        label.setFont(Font.font("Inter", FontWeight.MEDIUM, 12));

        TextField input = new TextField("Will Anthropic release Claude 5 before September 2026?");
        input.getStyleClass().add("form-input");
        input.setFont(Font.font("Inter", 14));

        Label hint = new Label("Must resolve to a clear YES or NO based on a public source.");
        hint.getStyleClass().add("form-hint");
        hint.setFont(Font.font("Inter", 12));

        field.getChildren().addAll(label, input, hint);
        return field;
    }

    private HBox createCategoryAndDateRow() {
        HBox row = new HBox(16);

        VBox categoryBox = new VBox(8);
        HBox.setHgrow(categoryBox, Priority.ALWAYS);
        Label catLabel = new Label("CATEGORY");
        catLabel.getStyleClass().add("form-field-label");
        catLabel.setFont(Font.font("Inter", FontWeight.MEDIUM, 12));
        TextField catInput = new TextField("Tech / AI");
        catInput.getStyleClass().add("form-input");
        catInput.setFont(Font.font("Inter", 14));
        categoryBox.getChildren().addAll(catLabel, catInput);

        VBox dateBox = new VBox(8);
        HBox.setHgrow(dateBox, Priority.ALWAYS);
        Label dateLabel = new Label("RESOLUTION DATE");
        dateLabel.getStyleClass().add("form-field-label");
        dateLabel.setFont(Font.font("Inter", FontWeight.MEDIUM, 12));
        TextField dateInput = new TextField("2026-09-30");
        dateInput.getStyleClass().add("form-input");
        dateInput.setFont(Font.font("Inter", 14));
        dateBox.getChildren().addAll(dateLabel, dateInput);

        row.getChildren().addAll(categoryBox, dateBox);
        return row;
    }

    private VBox createResolutionSourceField() {
        VBox field = new VBox(8);

        Label label = new Label("RESOLUTION SOURCE");
        label.getStyleClass().add("form-field-label");
        label.setFont(Font.font("Inter", FontWeight.MEDIUM, 12));

        TextField input = new TextField("https://anthropic.com/news");
        input.getStyleClass().add("form-input");
        input.setFont(Font.font("Inter", 14));

        field.getChildren().addAll(label, input);
        return field;
    }

    private VBox createLiquidityField() {
        VBox field = new VBox(8);

        Label label = new Label("INITIAL LIQUIDITY ($PMC)");
        label.getStyleClass().add("form-field-label");
        label.setFont(Font.font("Inter", FontWeight.MEDIUM, 12));

        HBox inputRow = new HBox(12);
        inputRow.setAlignment(Pos.CENTER_LEFT);

        TextField input = new TextField("500.00");
        input.getStyleClass().add("form-input");
        input.setFont(Font.font("Inter", 14));
        input.setPrefWidth(400);

        Label rangeHint = new Label("min 100 · max 50,000");
        rangeHint.getStyleClass().add("form-hint");
        rangeHint.setFont(Font.font("Inter", 12));

        inputRow.getChildren().addAll(input, rangeHint);
        field.getChildren().addAll(label, inputRow);
        return field;
    }

    private VBox createProbabilitySlider() {
        VBox field = new VBox(12);
        field.getStyleClass().add("probability-box");
        field.setPadding(new Insets(16, 16, 16, 16));

        Label label = new Label("INITIAL PROBABILITY");
        label.getStyleClass().add("form-field-label");
        label.setFont(Font.font("Inter", FontWeight.MEDIUM, 12));

        HBox sliderRow = new HBox(16);
        sliderRow.setAlignment(Pos.CENTER);

        Label yesLabel = new Label("50%");
        yesLabel.getStyleClass().add("prob-yes-label");
        yesLabel.setFont(Font.font("Inter", FontWeight.BOLD, 20));

        Slider slider = new Slider(0, 100, 50);
        slider.getStyleClass().add("prob-slider");
        slider.setPrefWidth(400);
        slider.setShowTickLabels(false);
        slider.setShowTickMarks(false);

        Label noLabel = new Label("50%");
        noLabel.getStyleClass().add("prob-no-label");
        noLabel.setFont(Font.font("Inter", FontWeight.BOLD, 20));

        sliderRow.getChildren().addAll(yesLabel, slider, noLabel);
        field.getChildren().addAll(label, sliderRow);
        return field;
    }

    public BorderPane getView() {
        return root;
    }

    public void setOnBack(Runnable onBack) {
        this.onBack = onBack;
    }
}
