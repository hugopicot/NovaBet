package com.polymarket.ui;

import com.polymarket.model.events;
import com.polymarket.ui.components.ChromeFactory;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
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
    private Runnable onMarketsClick;
    private Runnable onPortfolioClick;
    private Runnable onWalletClick;
    private Runnable onHistoryClick;
    private OnMarketCreatedCallback onMarketCreated;

    private TextField questionField;
    private TextField categoryField;
    private TextField dateField;
    private TextField descriptionField;
    private Slider probabilitySlider;
    private Label yesPercentLabel;
    private Label noPercentLabel;
    private Label sidebarBalance;
    private Label topbarBalance;

    public interface OnMarketCreatedCallback {
        void onCreated(events event, double yesProbability);
    }

    public CreateMarketView() {
        root = new BorderPane();
        root.getStyleClass().add("main-container");
        buildLayout();
    }

    private void buildLayout() {
        VBox sidebar = ChromeFactory.sidebar(
            ChromeFactory.NavId.CREATE,
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
        HBox topbar = ChromeFactory.topbar("Create market", "Search markets...", () -> {
            if (onWalletClick != null) onWalletClick.run();
        });
        topbarBalance = ChromeFactory.findTopbarBalance(topbar);
        main.setTop(topbar);
        main.setCenter(buildForm());
        root.setCenter(main);
    }

    private ScrollPane buildForm() {
        VBox center = new VBox(0);
        center.setAlignment(Pos.TOP_CENTER);
        center.setPadding(new Insets(32, 24, 32, 24));

        VBox container = new VBox(20);
        container.setMaxWidth(720);

        Label step = new Label("STEP 2 OF 3 · DEFINE");
        step.getStyleClass().add("step-label");

        Label title = new Label("Define your market");
        title.getStyleClass().add("form-page-title");
        title.setFont(Font.font("Inter", FontWeight.BOLD, 26));

        Label subtitle = new Label("A clear, binary, time-bounded question. Other traders will provide liquidity.");
        subtitle.getStyleClass().add("form-page-subtitle");
        subtitle.setFont(Font.font("Inter", 14));
        subtitle.setWrapText(true);

        VBox card = new VBox(18);
        card.getStyleClass().add("form-card");
        card.setPadding(new Insets(24));

        card.getChildren().addAll(
            buildQuestion(),
            buildCategoryAndDate(),
            buildDescription(),
            buildProbability()
        );

        HBox buttons = new HBox(10);
        buttons.setAlignment(Pos.CENTER_RIGHT);

        Button back = new Button("← Back");
        back.getStyleClass().add("btn-back");
        back.setFont(Font.font("Inter", FontWeight.MEDIUM, 13));
        back.setOnAction(e -> { if (onBack != null) onBack.run(); });

        Button cont = new Button("Continue → Review");
        cont.getStyleClass().add("btn-continue");
        cont.setFont(Font.font("Inter", FontWeight.BOLD, 13));
        cont.setOnAction(e -> {
            if (onMarketCreated != null) {
                onMarketCreated.onCreated(getMarketFromForm(), getYesProbability());
            }
        });

        buttons.getChildren().addAll(back, cont);

        container.getChildren().addAll(step, title, subtitle, card, buttons);
        center.getChildren().add(container);

        ScrollPane scroll = new ScrollPane(center);
        scroll.getStyleClass().add("detail-scroll");
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        return scroll;
    }

    private VBox buildQuestion() {
        VBox box = new VBox(8);
        Label l = new Label("QUESTION");
        l.getStyleClass().add("form-field-label");
        questionField = new TextField();
        questionField.setPromptText("e.g. Will GPT-5 be released before December 2026?");
        questionField.getStyleClass().add("form-input");
        questionField.setFont(Font.font("Inter", 14));
        Label hint = new Label("Must resolve to a clear YES or NO based on a public source.");
        hint.getStyleClass().add("form-hint");
        box.getChildren().addAll(l, questionField, hint);
        return box;
    }

    private HBox buildCategoryAndDate() {
        HBox row = new HBox(14);

        VBox catBox = new VBox(8);
        HBox.setHgrow(catBox, Priority.ALWAYS);
        Label cl = new Label("CATEGORY");
        cl.getStyleClass().add("form-field-label");
        categoryField = new TextField();
        categoryField.setPromptText("e.g. Tech / AI");
        categoryField.getStyleClass().add("form-input");
        categoryField.setFont(Font.font("Inter", 14));
        catBox.getChildren().addAll(cl, categoryField);

        VBox dateBox = new VBox(8);
        HBox.setHgrow(dateBox, Priority.ALWAYS);
        Label dl = new Label("RESOLUTION DATE");
        dl.getStyleClass().add("form-field-label");
        dateField = new TextField();
        dateField.setPromptText("e.g. 2026-12-31");
        dateField.getStyleClass().add("form-input");
        dateField.setFont(Font.font("Inter", 14));
        dateBox.getChildren().addAll(dl, dateField);

        row.getChildren().addAll(catBox, dateBox);
        return row;
    }

    private VBox buildDescription() {
        VBox box = new VBox(8);
        Label l = new Label("DESCRIPTION");
        l.getStyleClass().add("form-field-label");
        descriptionField = new TextField();
        descriptionField.setPromptText("Detailed resolution criteria...");
        descriptionField.getStyleClass().add("form-input");
        descriptionField.setFont(Font.font("Inter", 14));
        box.getChildren().addAll(l, descriptionField);
        return box;
    }

    private VBox buildProbability() {
        VBox box = new VBox(14);
        box.getStyleClass().add("probability-box");
        box.setPadding(new Insets(18, 20, 18, 20));

        Label l = new Label("INITIAL PROBABILITY");
        l.getStyleClass().add("form-field-label");

        HBox row = new HBox(18);
        row.setAlignment(Pos.CENTER);

        yesPercentLabel = new Label("50%");
        yesPercentLabel.getStyleClass().add("prob-yes-label");
        yesPercentLabel.setFont(Font.font("JetBrains Mono", FontWeight.BOLD, 22));
        yesPercentLabel.setMinWidth(60);
        yesPercentLabel.setAlignment(Pos.CENTER);

        probabilitySlider = new Slider(0, 100, 50);
        probabilitySlider.getStyleClass().add("prob-slider");
        HBox.setHgrow(probabilitySlider, Priority.ALWAYS);
        probabilitySlider.setShowTickLabels(false);
        probabilitySlider.setShowTickMarks(false);
        probabilitySlider.valueProperty().addListener((o, ov, nv) -> {
            int yes = nv.intValue();
            yesPercentLabel.setText(yes + "%");
            noPercentLabel.setText((100 - yes) + "%");
        });

        noPercentLabel = new Label("50%");
        noPercentLabel.getStyleClass().add("prob-no-label");
        noPercentLabel.setFont(Font.font("JetBrains Mono", FontWeight.BOLD, 22));
        noPercentLabel.setMinWidth(60);
        noPercentLabel.setAlignment(Pos.CENTER);

        row.getChildren().addAll(yesPercentLabel, probabilitySlider, noPercentLabel);

        HBox legend = new HBox();
        legend.setAlignment(Pos.CENTER_LEFT);
        Label yesLeg = new Label("YES");
        yesLeg.setStyle("-fx-text-fill: -yes; -fx-font-size: 10px; -fx-font-weight: bold;");
        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);
        Label noLeg = new Label("NO");
        noLeg.setStyle("-fx-text-fill: -no; -fx-font-size: 10px; -fx-font-weight: bold;");
        legend.getChildren().addAll(yesLeg, sp, noLeg);

        box.getChildren().addAll(l, row, legend);
        return box;
    }

    public events getMarketFromForm() {
        events e = new events();
        e.setTitle(questionField.getText());
        e.setDescription(descriptionField.getText());
        e.setStatus("OPEN");
        e.setResolution(dateField.getText());
        return e;
    }

    public double getYesProbability() {
        return probabilitySlider.getValue() / 100.0;
    }

    public void clearForm() {
        questionField.clear();
        categoryField.clear();
        dateField.clear();
        descriptionField.clear();
        probabilitySlider.setValue(50);
        yesPercentLabel.setText("50%");
        noPercentLabel.setText("50%");
    }

    public void setBalance(double balance) {
        String s = String.format("%.2f", balance);
        if (sidebarBalance != null) sidebarBalance.setText(s);
        if (topbarBalance != null) topbarBalance.setText(s);
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

    public void setOnWalletClick(Runnable onWalletClick) {
        this.onWalletClick = onWalletClick;
    }

    public void setOnHistoryClick(Runnable onHistoryClick) {
        this.onHistoryClick = onHistoryClick;
    }

    public void setOnMarketCreated(OnMarketCreatedCallback onMarketCreated) {
        this.onMarketCreated = onMarketCreated;
    }
}
