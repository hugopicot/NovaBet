package com.polymarket.casino.crash;

import com.polymarket.dao.WalletRepository;
import com.polymarket.dao.WalletSnapshot;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.sql.SQLException;
import java.text.DecimalFormat;

public class CrashGameView {

    private enum State { WAITING, FLYING, CRASHED }

    private static final DecimalFormat MULT_FMT = new DecimalFormat("0.00");
    private static final DecimalFormat AMT_FMT  = new DecimalFormat("#,##0.00");

    private final long userId;
    private final WalletRepository walletRepo;
    private final Runnable onBack;

    private BorderPane root;
    private Label multiplierLabel;
    private Label rocketLabel;
    private Label statusLabel;
    private Label balanceLabel;
    private TextField betField;
    private Button actionButton;
    private Label resultLabel;

    private State state = State.WAITING;
    private CrashGameLogic logic;
    private double currentBet = 0;
    private Timeline ticker;

    public CrashGameView(long userId, Runnable onBack) throws SQLException {
        this.userId = userId;
        this.onBack = onBack;
        this.walletRepo = new WalletRepository();
        buildUI();
    }

    private void buildUI() {
        root = new BorderPane();
        root.setStyle("-fx-background-color: #0d0d0d;");

        // --- TOP BAR ---
        HBox topBar = new HBox(12);
        topBar.setPadding(new Insets(14, 20, 14, 20));
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setStyle("-fx-background-color: #1a1a1a; -fx-border-color: #2a2a2a; -fx-border-width: 0 0 1 0;");

        Button backBtn = new Button("← Lobby");
        backBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #888; -fx-cursor: hand; -fx-font-size: 13;");
        backBtn.setOnAction(e -> { stopTicker(); if (onBack != null) onBack.run(); });

        Label title = new Label("🚀 Crash Roquette");
        title.setStyle("-fx-text-fill: #f0c040; -fx-font-size: 18; -fx-font-weight: bold;");

        balanceLabel = new Label();
        balanceLabel.setStyle("-fx-text-fill: #aaa; -fx-font-size: 13;");
        HBox.setHgrow(balanceLabel, Priority.ALWAYS);
        balanceLabel.setMaxWidth(Double.MAX_VALUE);
        balanceLabel.setAlignment(Pos.CENTER_RIGHT);

        topBar.getChildren().addAll(backBtn, title, balanceLabel);
        root.setTop(topBar);

        // --- CENTER : MULTIPLIER DISPLAY ---
        VBox center = new VBox(16);
        center.setAlignment(Pos.CENTER);
        center.setPadding(new Insets(40));

        rocketLabel = new Label("🚀");
        rocketLabel.setStyle("-fx-font-size: 64;");

        multiplierLabel = new Label("1.00×");
        multiplierLabel.setStyle("-fx-text-fill: #4cff91; -fx-font-size: 80; -fx-font-weight: bold;");

        statusLabel = new Label("Mise en attente...");
        statusLabel.setStyle("-fx-text-fill: #888; -fx-font-size: 15;");

        resultLabel = new Label("");
        resultLabel.setStyle("-fx-text-fill: #f0c040; -fx-font-size: 16; -fx-font-weight: bold;");

        center.getChildren().addAll(rocketLabel, multiplierLabel, statusLabel, resultLabel);
        root.setCenter(center);

        // --- BOTTOM : BET PANEL ---
        VBox bottom = new VBox(12);
        bottom.setPadding(new Insets(20, 40, 30, 40));
        bottom.setAlignment(Pos.CENTER);
        bottom.setStyle("-fx-background-color: #1a1a1a; -fx-border-color: #2a2a2a; -fx-border-width: 1 0 0 0;");

        HBox betRow = new HBox(10);
        betRow.setAlignment(Pos.CENTER);
        Label betLbl = new Label("Mise (crédits casino) :");
        betLbl.setStyle("-fx-text-fill: #ccc; -fx-font-size: 14;");
        betField = new TextField("50");
        betField.setStyle("-fx-background-color: #2a2a2a; -fx-text-fill: #fff; -fx-font-size: 14; "
                + "-fx-border-color: #444; -fx-border-radius: 4; -fx-background-radius: 4; -fx-pref-width: 100;");
        betRow.getChildren().addAll(betLbl, betField);

        actionButton = new Button("🚀  LANCER");
        actionButton.setStyle("-fx-background-color: #f0c040; -fx-text-fill: #000; -fx-font-size: 16; "
                + "-fx-font-weight: bold; -fx-padding: 12 40; -fx-background-radius: 8; -fx-cursor: hand;");
        actionButton.setOnAction(e -> handleAction());

        bottom.getChildren().addAll(betRow, actionButton);
        root.setBottom(bottom);

        refreshBalance();
    }

    private void handleAction() {
        switch (state) {
            case WAITING -> startRound();
            case FLYING  -> cashout();
            case CRASHED -> resetRound();
        }
    }

    private void startRound() {
        double bet;
        try {
            bet = Double.parseDouble(betField.getText().trim());
            if (bet <= 0) { showError("Mise invalide"); return; }
        } catch (NumberFormatException e) {
            showError("Montant invalide"); return;
        }

        WalletSnapshot w = walletRepo.findByUserId(userId);
        if (w == null || w.virtualBalance() < bet) {
            showError("Solde insuffisant"); return;
        }

        try { walletRepo.debitVirtual(userId, bet); } catch (SQLException ex) { showError("Erreur BDD"); return; }
        currentBet = bet;
        logic = new CrashGameLogic();
        state = State.FLYING;

        betField.setDisable(true);
        actionButton.setText("💰  CASHOUT");
        actionButton.setStyle("-fx-background-color: #ff4444; -fx-text-fill: #fff; -fx-font-size: 16; "
                + "-fx-font-weight: bold; -fx-padding: 12 40; -fx-background-radius: 8; -fx-cursor: hand;");
        resultLabel.setText("");
        statusLabel.setText("Vol en cours — appuie sur CASHOUT pour sécuriser tes gains !");
        statusLabel.setStyle("-fx-text-fill: #4cff91; -fx-font-size: 14;");

        startTicker();
        refreshBalance();
    }

    private void cashout() {
        if (state != State.FLYING || logic == null) return;
        if (logic.hasCrashed()) return; // trop tard

        stopTicker();
        double mult = logic.cashout();
        double winAmount = currentBet * mult;
        try { walletRepo.creditVirtual(userId, winAmount); } catch (SQLException ex) { /* log */ }

        state = State.CRASHED;
        multiplierLabel.setText(MULT_FMT.format(mult) + "×");
        multiplierLabel.setStyle("-fx-text-fill: #4cff91; -fx-font-size: 80; -fx-font-weight: bold;");
        rocketLabel.setText("🚀");
        statusLabel.setText("Cashout réussi !");
        resultLabel.setText("+ " + AMT_FMT.format(winAmount) + " crédits (×" + MULT_FMT.format(mult) + ")");
        actionButton.setText("▶  REJOUER");
        actionButton.setStyle("-fx-background-color: #f0c040; -fx-text-fill: #000; -fx-font-size: 16; "
                + "-fx-font-weight: bold; -fx-padding: 12 40; -fx-background-radius: 8; -fx-cursor: hand;");
        betField.setDisable(false);
        refreshBalance();
    }

    private void startTicker() {
        ticker = new Timeline(new KeyFrame(Duration.millis(80), e -> tick()));
        ticker.setCycleCount(Timeline.INDEFINITE);
        ticker.play();
    }

    private void tick() {
        if (logic == null) return;
        double mult = logic.currentMultiplier();

        if (logic.hasCrashed()) {
            stopTicker();
            state = State.CRASHED;
            double crashAt = logic.getCrashPoint();
            multiplierLabel.setText(MULT_FMT.format(crashAt) + "×");
            multiplierLabel.setStyle("-fx-text-fill: #ff4444; -fx-font-size: 80; -fx-font-weight: bold;");
            rocketLabel.setText("💥");
            statusLabel.setText("CRASHÉ à " + MULT_FMT.format(crashAt) + "× — tu as perdu ta mise.");
            statusLabel.setStyle("-fx-text-fill: #ff4444; -fx-font-size: 14;");
            resultLabel.setText("- " + AMT_FMT.format(currentBet) + " crédits");
            actionButton.setText("▶  REJOUER");
            actionButton.setStyle("-fx-background-color: #f0c040; -fx-text-fill: #000; -fx-font-size: 16; "
                    + "-fx-font-weight: bold; -fx-padding: 12 40; -fx-background-radius: 8; -fx-cursor: hand;");
            betField.setDisable(false);
            refreshBalance();
            return;
        }

        multiplierLabel.setText(MULT_FMT.format(mult) + "×");
        // Color shifts green → yellow → red as multiplier climbs
        if (mult < 2.0) {
            multiplierLabel.setStyle("-fx-text-fill: #4cff91; -fx-font-size: 80; -fx-font-weight: bold;");
        } else if (mult < 5.0) {
            multiplierLabel.setStyle("-fx-text-fill: #f0c040; -fx-font-size: 80; -fx-font-weight: bold;");
        } else {
            multiplierLabel.setStyle("-fx-text-fill: #ff8c00; -fx-font-size: 80; -fx-font-weight: bold;");
        }

        // Animate rocket position (simple vertical offset on the label)
        double offset = Math.min(mult * 6, 60);
        rocketLabel.setTranslateY(-offset);
    }

    private void resetRound() {
        state = State.WAITING;
        multiplierLabel.setText("1.00×");
        multiplierLabel.setStyle("-fx-text-fill: #4cff91; -fx-font-size: 80; -fx-font-weight: bold;");
        rocketLabel.setText("🚀");
        rocketLabel.setTranslateY(0);
        statusLabel.setText("Entre ta mise et lance la fusée !");
        statusLabel.setStyle("-fx-text-fill: #888; -fx-font-size: 14;");
        resultLabel.setText("");
        actionButton.setText("🚀  LANCER");
        actionButton.setStyle("-fx-background-color: #f0c040; -fx-text-fill: #000; -fx-font-size: 16; "
                + "-fx-font-weight: bold; -fx-padding: 12 40; -fx-background-radius: 8; -fx-cursor: hand;");
        logic = null;
        currentBet = 0;
    }

    private void stopTicker() {
        if (ticker != null) { ticker.stop(); ticker = null; }
    }

    private void refreshBalance() {
        WalletSnapshot w = walletRepo.findByUserId(userId);
        double v = (w == null) ? 0 : w.virtualBalance();
        balanceLabel.setText("Crédits casino : " + AMT_FMT.format(v));
    }

    private void showError(String msg) {
        statusLabel.setText("⚠ " + msg);
        statusLabel.setStyle("-fx-text-fill: #ff4444; -fx-font-size: 14;");
    }

    public BorderPane getView() { return root; }
}
