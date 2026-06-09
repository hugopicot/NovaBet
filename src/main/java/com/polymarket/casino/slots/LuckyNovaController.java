package com.polymarket.casino.slots;

import com.polymarket.service.SlotMachineService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LuckyNovaController {

    @FXML
    private Label reel1;

    @FXML
    private Label reel2;

    @FXML
    private Label reel3;

    @FXML
    private Label resultLabel;

    @FXML
    private Label balanceLabel;

    @FXML
    private TextField betField;

    private SlotMachineService slotService;
    private long userId;
    private Runnable onClose;

    public void setUserId(long userId) {
        this.userId = userId;
        refreshBalance();
    }

    public void setOnClose(Runnable onClose) {
        this.onClose = onClose;
    }

    @FXML
    private void initialize() {
        this.slotService = new SlotMachineService();
    }

    @FXML
    private void spin() {
        if (slotService == null) {
            resultLabel.setText("Service not ready");
            return;
        }

        double betAmount;
        try {
            betAmount = Double.parseDouble(betField.getText().trim());
            if (betAmount <= 0) {
                resultLabel.setText("Bet must be positive");
                return;
            }
        } catch (NumberFormatException e) {
            resultLabel.setText("Invalid bet amount");
            return;
        }

        SlotMachineService.SpinResult result = slotService.spin((int) userId, betAmount);
        if (result == null) {
            resultLabel.setText("Insufficient balance");
            refreshBalance();
            return;
        }

        reel1.setText(result.symbols[0]);
        reel2.setText(result.symbols[1]);
        reel3.setText(result.symbols[2]);

        if (result.isWin()) {
            resultLabel.setText("WIN! +" + String.format("%.2f", result.winAmount) + " credits");
            resultLabel.setStyle("-fx-text-fill: gold; -fx-font-size: 18; -fx-font-weight: bold;");
        } else {
            resultLabel.setText("LOSS -" + String.format("%.2f", betAmount) + " credits");
            resultLabel.setStyle("-fx-text-fill: #ff6b6b; -fx-font-size: 18;");
        }

        refreshBalance();
    }

    @FXML
    public void closeWindow() {
        if (onClose != null) {
            onClose.run();
        }
        Stage stage = (Stage) reel1.getScene().getWindow();
        if (stage != null) {
            stage.close();
        }
    }

    private void refreshBalance() {
        if (slotService == null) return;
        try {
            var wallet = slotService.getWallet(userId);
            if (wallet != null) {
                double virtual = wallet.getVirtualBalance();
                balanceLabel.setText(String.format("%.2f", virtual));
            }
        } catch (Exception e) {
            balanceLabel.setText("--");
        }
    }
}
