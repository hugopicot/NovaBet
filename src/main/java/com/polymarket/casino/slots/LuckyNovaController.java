package com.polymarket.casino.slots;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import java.util.Random;

public class LuckyNovaController {

    @FXML
    private Label reel1;

    @FXML
    private Label reel2;

    @FXML
    private Label reel3;

    @FXML
    private Label resultLabel;

    private final String[] symbols = {"🍒", "🍋", "⭐", "💎", "7️⃣"};
    private final Random random = new Random();

    @FXML
    private void spin() {
        String a = symbols[random.nextInt(symbols.length)];
        String b = symbols[random.nextInt(symbols.length)];
        String c = symbols[random.nextInt(symbols.length)];

        reel1.setText(a);
        reel2.setText(b);
        reel3.setText(c);

        if (a.equals(b) && b.equals(c)) {
            resultLabel.setText("JACKPOT!");
            resultLabel.setStyle("-fx-text-fill: gold; -fx-font-size: 18; -fx-font-weight: bold;");
        } else {
            resultLabel.setText("Perdu - Retry!");
            resultLabel.setStyle("-fx-text-fill: #cccccc; -fx-font-size: 18;");
        }
    }
}