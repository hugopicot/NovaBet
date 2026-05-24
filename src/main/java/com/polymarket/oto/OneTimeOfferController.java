package com.polymarket.oto;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class OneTimeOfferController {

    private static final int TIMER_DURATION_SECONDS = 5 * 60;
    private static final DecimalFormat AMOUNT_FORMAT;

    static {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.FRANCE);
        symbols.setGroupingSeparator(' ');
        AMOUNT_FORMAT = new DecimalFormat("#,##0", symbols);
    }

    // ── Page Retrait ────────────────────────────────────────────────
    @FXML private VBox withdrawBox;
    @FXML private TextField amountField;
    @FXML private Label withdrawError;
    @FXML private Button withdrawBtn;

    // ── Modale OTO ──────────────────────────────────────────────────
    @FXML private StackPane otoBox;
    @FXML private Label timerLabel;
    @FXML private Label declineAmountLabel;
    @FXML private Label maxAmountLabel;
    @FXML private Label card2xValue;
    @FXML private Label card5xValue;
    @FXML private Label card10xValue;
    @FXML private Button spinBtn;
    @FXML private Label declineLink;

    private Timeline countdown;
    private int secondsLeft;
    private double currentAmount;

    @FXML
    public void initialize() {
        // Limite l'input aux chiffres et au point décimal
        amountField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.matches("\\d*([\\.,]\\d{0,2})?")) {
                amountField.setText(oldVal);
            }
            withdrawError.setText(" ");
        });
    }

    @FXML
    public void onWithdrawClicked() {
        String raw = amountField.getText().trim().replace(",", ".");
        if (raw.isEmpty()) {
            withdrawError.setText("Entre un montant");
            return;
        }
        double amount;
        try {
            amount = Double.parseDouble(raw);
        } catch (NumberFormatException e) {
            withdrawError.setText("Montant invalide");
            return;
        }
        if (amount <= 0) {
            withdrawError.setText("Le montant doit être positif");
            return;
        }

        this.currentAmount = amount;
        showOtoModal();
    }

    private void showOtoModal() {
        // Remplir les libellés dynamiques
        declineAmountLabel.setText(formatAmount(currentAmount) + " $.");
        maxAmountLabel.setText(formatAmount(currentAmount * 10) + " $.");
        card2xValue.setText(formatAmount(currentAmount * 2)  + " $");
        card5xValue.setText(formatAmount(currentAmount * 5)  + " $");
        card10xValue.setText(formatAmount(currentAmount * 10) + " $");
        spinBtn.setText("TOURNER LA ROUE · " + formatAmount(currentAmount) + " $ → JUSQU'À "
                + formatAmount(currentAmount * 10) + " $");

        // Afficher l'overlay, cacher la page retrait
        withdrawBox.setVisible(false);
        withdrawBox.setManaged(false);
        otoBox.setVisible(true);
        otoBox.setManaged(true);

        startCountdown();
    }

    private void hideOtoModal() {
        stopCountdown();
        otoBox.setVisible(false);
        otoBox.setManaged(false);
        withdrawBox.setVisible(true);
        withdrawBox.setManaged(true);
        amountField.clear();
    }

    @FXML
    public void onSpinClicked() {
        stopCountdown();
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/polymarket/oto/WheelView.fxml")
            );
            Parent wheelRoot = loader.load();
            WheelController wheelCtrl = loader.getController();
            wheelCtrl.setStake(currentAmount);

            Scene scene = spinBtn.getScene();
            scene.setRoot(wheelRoot);
        } catch (IOException e) {
            e.printStackTrace();
            withdrawError.setText("Erreur d'ouverture de la roue : " + e.getMessage());
        }
    }

    @FXML
    public void onDeclineClicked(MouseEvent event) {
        hideOtoModal();
        withdrawError.setText("Retrait effectué : " + formatAmount(currentAmount) + " $");
    }

    // ── Timer 5 minutes ─────────────────────────────────────────────
    private void startCountdown() {
        stopCountdown();
        secondsLeft = TIMER_DURATION_SECONDS;
        updateTimerLabel();

        countdown = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            secondsLeft--;
            updateTimerLabel();
            if (secondsLeft <= 0) {
                Platform.runLater(() -> {
                    hideOtoModal();
                    withdrawError.setText("Offre expirée — retrait simple effectué.");
                });
            }
        }));
        countdown.setCycleCount(TIMER_DURATION_SECONDS);
        countdown.play();
    }

    private void stopCountdown() {
        if (countdown != null) {
            countdown.stop();
            countdown = null;
        }
    }

    private void updateTimerLabel() {
        int m = secondsLeft / 60;
        int s = secondsLeft % 60;
        timerLabel.setText(String.format("%d:%02d", m, s));
    }

    private static String formatAmount(double v) {
        return AMOUNT_FORMAT.format(v);
    }
}
