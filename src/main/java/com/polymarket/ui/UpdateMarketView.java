package com.polymarket.ui;

import com.polymarket.model.events;

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

public class UpdateMarketView {

    private BorderPane root;
    private Runnable onBack;
    private Runnable onMarketsClick;
    private Runnable onPortfolioClick;
    private OnMarketUpdatedCallback onMarketUpdated;

    private TextField questionField;
    private TextField categoryField;
    private TextField dateField;
    private TextField descriptionField;
    private Slider probabilitySlider;
    private Label yesPercentLabel;
    private Label noPercentLabel;

    private events currentEvent;

    public interface OnMarketUpdatedCallback {
        void onUpdated(events event, double yesProbability);
    }

    public UpdateMarketView() {
        root = new BorderPane();
        root.getStyleClass().add("main-container");
        root.setLeft(createSidebar());
        root.setCenter(createMainContent());
    }

    public void setEventData(events event) {
        this.currentEvent = event;
        if (event != null) {
            questionField.setText(event.getTitle() != null ? event.getTitle() : "");
            descriptionField.setText(event.getDescription() != null ? event.getDescription() : "");
            dateField.setText(event.getResolution() != null ? event.getResolution() : "");
            probabilitySlider.setValue(50);
        }
    }

    public events getMarketFromForm() {
        if (currentEvent == null) {
            currentEvent = new events();
        }
        currentEvent.setTitle(questionField.getText());
        currentEvent.setDescription(descriptionField.getText());
        currentEvent.setResolution(dateField.getText());
        return currentEvent;
    }

    public double getYesProbability() {
        return probabilitySlider.getValue() / 100.0;
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
        navItems.getChildren().addAll(
            marketsNav,
            portfolioNav,
            createNavItem("Create market", true),
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
        Label balanceValue = new Label("0.00");
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
        mainContent.setCenter(createFormContent());

        return mainContent;
    }

    private VBox createTopBar() {
        VBox topBar = new VBox(0);
        topBar.setPadding(new Insets(0, 24, 0, 24));

        HBox headerRow = new HBox(16);
        headerRow.setAlignment(Pos.CENTER_LEFT);
        headerRow.setPadding(new Insets(16, 0, 12, 0));

        Label pageTitle = new Label("Update market");
        pageTitle.getStyleClass().add("markets-title");
        pageTitle.setFont(Font.font("Inter", FontWeight.BOLD, 16));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        headerRow.getChildren().addAll(pageTitle, spacer);

        topBar.getChildren().add(headerRow);
        return topBar;
    }

    private VBox createFormContent() {
        VBox content = new VBox(0);
        content.setAlignment(Pos.TOP_CENTER);
        content.setPadding(new Insets(32, 24, 24, 24));

        VBox formContainer = new VBox(24);
        formContainer.setMaxWidth(680);

        Label stepLabel = new Label("EDITING MARKET");
        stepLabel.getStyleClass().add("step-label");
        stepLabel.setFont(Font.font("Inter", FontWeight.MEDIUM, 12));

        Label formTitle = new Label("Update market details");
        formTitle.getStyleClass().add("form-page-title");
        formTitle.setFont(Font.font("Inter", FontWeight.BOLD, 24));

        Label formSubtitle = new Label("Modify the question, resolution date, or other market parameters.");
        formSubtitle.getStyleClass().add("form-page-subtitle");
        formSubtitle.setFont(Font.font("Inter", 14));
        formSubtitle.setWrapText(true);

        VBox formCard = new VBox(20);
        formCard.getStyleClass().add("form-card");
        formCard.setPadding(new Insets(24, 24, 24, 24));

        formCard.getChildren().addAll(
            createQuestionField(),
            createCategoryAndDateRow(),
            createDescriptionField(),
            createProbabilitySlider()
        );

        HBox buttonRow = new HBox(12);
        buttonRow.setAlignment(Pos.CENTER_RIGHT);

        Button cancelBtn = new Button("Cancel");
        cancelBtn.getStyleClass().add("btn-back");
        cancelBtn.setFont(Font.font("Inter", FontWeight.MEDIUM, 13));
        cancelBtn.setOnAction(e -> {
            if (onBack != null) onBack.run();
        });

        Button saveBtn = new Button("Save changes");
        saveBtn.getStyleClass().add("btn-save");
        saveBtn.setFont(Font.font("Inter", FontWeight.BOLD, 14));
        saveBtn.setOnAction(e -> {
            if (onMarketUpdated != null) {
                onMarketUpdated.onUpdated(getMarketFromForm(), getYesProbability());
            }
        });

        buttonRow.getChildren().addAll(cancelBtn, saveBtn);

        formContainer.getChildren().addAll(stepLabel, formTitle, formSubtitle, formCard, buttonRow);
        content.getChildren().add(formContainer);

        return content;
    }

    private VBox createQuestionField() {
        VBox field = new VBox(8);

        Label label = new Label("QUESTION");
        label.getStyleClass().add("form-field-label");
        label.setFont(Font.font("Inter", FontWeight.MEDIUM, 12));

        questionField = new TextField();
        questionField.setPromptText("e.g. Will X happen before Y date?");
        questionField.getStyleClass().add("form-input");
        questionField.setFont(Font.font("Inter", 14));

        field.getChildren().addAll(label, questionField);
        return field;
    }

    private HBox createCategoryAndDateRow() {
        HBox row = new HBox(16);

        VBox categoryBox = new VBox(8);
        HBox.setHgrow(categoryBox, Priority.ALWAYS);
        Label catLabel = new Label("CATEGORY");
        catLabel.getStyleClass().add("form-field-label");
        catLabel.setFont(Font.font("Inter", FontWeight.MEDIUM, 12));
        categoryField = new TextField();
        categoryField.setPromptText("e.g. Tech / AI");
        categoryField.getStyleClass().add("form-input");
        categoryField.setFont(Font.font("Inter", 14));
        categoryBox.getChildren().addAll(catLabel, categoryField);

        VBox dateBox = new VBox(8);
        HBox.setHgrow(dateBox, Priority.ALWAYS);
        Label dateLabel = new Label("RESOLUTION DATE");
        dateLabel.getStyleClass().add("form-field-label");
        dateLabel.setFont(Font.font("Inter", FontWeight.MEDIUM, 12));
        dateField = new TextField();
        dateField.setPromptText("e.g. 2026-12-31");
        dateField.getStyleClass().add("form-input");
        dateField.setFont(Font.font("Inter", 14));
        dateBox.getChildren().addAll(dateLabel, dateField);

        row.getChildren().addAll(categoryBox, dateBox);
        return row;
    }

    private VBox createDescriptionField() {
        VBox field = new VBox(8);

        Label label = new Label("DESCRIPTION");
        label.getStyleClass().add("form-field-label");
        label.setFont(Font.font("Inter", FontWeight.MEDIUM, 12));

        descriptionField = new TextField();
        descriptionField.setPromptText("Detailed resolution criteria...");
        descriptionField.getStyleClass().add("form-input");
        descriptionField.setFont(Font.font("Inter", 14));

        field.getChildren().addAll(label, descriptionField);
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

        yesPercentLabel = new Label("50%");
        yesPercentLabel.getStyleClass().add("prob-yes-label");
        yesPercentLabel.setFont(Font.font("Inter", FontWeight.BOLD, 20));

        probabilitySlider = new Slider(0, 100, 50);
        probabilitySlider.getStyleClass().add("prob-slider");
        probabilitySlider.setPrefWidth(400);
        probabilitySlider.setShowTickLabels(false);
        probabilitySlider.setShowTickMarks(false);
        probabilitySlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            int yes = newVal.intValue();
            yesPercentLabel.setText(yes + "%");
            noPercentLabel.setText((100 - yes) + "%");
        });

        noPercentLabel = new Label("50%");
        noPercentLabel.getStyleClass().add("prob-no-label");
        noPercentLabel.setFont(Font.font("Inter", FontWeight.BOLD, 20));

        sliderRow.getChildren().addAll(yesPercentLabel, probabilitySlider, noPercentLabel);
        field.getChildren().addAll(label, sliderRow);
        return field;
    }

    public BorderPane getView() {
        return root;
    }

    public void setOnBack(Runnable onBack) {
        this.onBack = onBack;
    }

    public void setOnMarketsClick(Runnable onMarketsClick) {
        this.onMarketsClick = onMarketsClick;
    }

    public void setOnPortfolioClick(Runnable onPortfolioClick) {
        this.onPortfolioClick = onPortfolioClick;
    }

    public void setOnMarketUpdated(OnMarketUpdatedCallback onMarketUpdated) {
        this.onMarketUpdated = onMarketUpdated;
    }
}