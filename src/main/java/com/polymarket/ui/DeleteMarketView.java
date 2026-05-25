package com.polymarket.ui;

import com.polymarket.model.events;
import com.polymarket.ui.components.ChromeFactory;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

public class DeleteMarketView {

    private BorderPane root;
    private Runnable onBack;
    private Runnable onConfirmDelete;
    private Runnable onMarketsClick;
    private Runnable onPortfolioClick;
    private Runnable onWalletClick;
    private Runnable onHistoryClick;

    private Label previewQuestion;
    private Label previewMeta;
    private TextField confirmInput;
    private Button deleteBtn;
    private Label sidebarBalance;
    private Label topbarBalance;

    private events currentEvent;

    public DeleteMarketView() {
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
        HBox topbar = ChromeFactory.topbar("Delete market", "Search markets...", () -> {
            if (onWalletClick != null) onWalletClick.run();
        });
        topbarBalance = ChromeFactory.findTopbarBalance(topbar);
        main.setTop(topbar);

        StackPane center = new StackPane();
        center.setPadding(new Insets(40, 24, 40, 24));
        center.setAlignment(Pos.TOP_CENTER);
        center.setStyle("-fx-background-color: -bg-0;");
        center.getChildren().add(buildDialog());
        main.setCenter(center);

        root.setCenter(main);
    }

    private VBox buildDialog() {
        VBox dialog = new VBox(20);
        dialog.setAlignment(Pos.TOP_CENTER);
        dialog.setMaxWidth(460);
        dialog.getStyleClass().add("delete-dialog");
        dialog.setPadding(new Insets(32, 32, 28, 32));

        StackPane iconWrap = new StackPane();
        iconWrap.getStyleClass().add("warning-icon");
        iconWrap.setPrefSize(56, 56);
        iconWrap.setMinSize(56, 56);
        iconWrap.setMaxSize(56, 56);
        Label warn = new Label("!");
        warn.setFont(Font.font("Inter", FontWeight.EXTRA_BOLD, 26));
        warn.setStyle("-fx-text-fill: -no;");
        iconWrap.getChildren().add(warn);

        Label title = new Label("Delete this market?");
        title.getStyleClass().add("delete-dialog-title");
        title.setFont(Font.font("Inter", FontWeight.BOLD, 22));

        Label subtitle = new Label("This action cannot be undone. All liquidity and positions will be permanently lost.");
        subtitle.getStyleClass().add("delete-dialog-subtitle");
        subtitle.setFont(Font.font("Inter", 13));
        subtitle.setWrapText(true);
        subtitle.setTextAlignment(TextAlignment.CENTER);

        VBox preview = new VBox(8);
        preview.getStyleClass().add("delete-market-preview");
        preview.setPadding(new Insets(14, 16, 14, 16));
        preview.setMaxWidth(Double.MAX_VALUE);

        previewQuestion = new Label("");
        previewQuestion.getStyleClass().add("delete-preview-question");
        previewQuestion.setFont(Font.font("Inter", FontWeight.MEDIUM, 14));
        previewQuestion.setWrapText(true);

        previewMeta = new Label("");
        previewMeta.getStyleClass().add("delete-preview-meta");
        previewMeta.setFont(Font.font("Inter", 11));

        preview.getChildren().addAll(previewQuestion, previewMeta);

        VBox confirmBox = new VBox(8);
        confirmBox.setAlignment(Pos.CENTER);
        Label cl = new Label("TYPE DELETE TO CONFIRM");
        cl.getStyleClass().add("delete-confirm-label");

        confirmInput = new TextField();
        confirmInput.setPromptText("DELETE");
        confirmInput.getStyleClass().add("delete-confirm-input");
        confirmInput.setPrefWidth(280);
        confirmInput.setAlignment(Pos.CENTER);
        confirmInput.textProperty().addListener((o, ov, nv) -> {
            if (deleteBtn != null) deleteBtn.setDisable(!"DELETE".equals(nv));
        });

        confirmBox.getChildren().addAll(cl, confirmInput);

        HBox buttons = new HBox(10);
        buttons.setAlignment(Pos.CENTER);

        Button cancel = new Button("Cancel");
        cancel.getStyleClass().add("btn-back");
        cancel.setFont(Font.font("Inter", FontWeight.MEDIUM, 13));
        cancel.setPrefWidth(130);
        cancel.setOnAction(e -> { if (onBack != null) onBack.run(); });

        deleteBtn = new Button("Delete market");
        deleteBtn.getStyleClass().add("btn-delete");
        deleteBtn.setFont(Font.font("Inter", FontWeight.BOLD, 13));
        deleteBtn.setPrefWidth(160);
        deleteBtn.setDisable(true);
        deleteBtn.setOnAction(e -> {
            if ("DELETE".equals(confirmInput.getText()) && onConfirmDelete != null) {
                onConfirmDelete.run();
                confirmInput.clear();
            }
        });

        buttons.getChildren().addAll(cancel, deleteBtn);

        dialog.getChildren().addAll(iconWrap, title, subtitle, preview, confirmBox, buttons);
        return dialog;
    }

    public void setEventData(events event) {
        this.currentEvent = event;
        if (event != null) {
            previewQuestion.setText(event.getTitle() != null ? event.getTitle() : "");
            String r = event.getResolution() != null ? event.getResolution() : "";
            previewMeta.setText(r.isEmpty() ? "No resolution date" : "Ends " + r);
        }
        if (confirmInput != null) confirmInput.clear();
        if (deleteBtn != null) deleteBtn.setDisable(true);
    }

    public events getEvent() {
        return currentEvent;
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

    public void setOnConfirmDelete(Runnable onConfirmDelete) {
        this.onConfirmDelete = onConfirmDelete;
    }
}
