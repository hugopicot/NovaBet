package com.polymarket.casino.lobby;

import com.polymarket.dao.GameDAO;
import com.polymarket.dao.WalletRepository;
import com.polymarket.dao.WalletSnapshot;
import com.polymarket.domain.exception.CasinoCashoutException;
import com.polymarket.domain.service.CasinoCashoutService;
import com.polymarket.domain.service.WageringService;
import com.polymarket.model.Game;
import com.polymarket.model.GameType;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;

public class CasinoLobbyController {

    private static final DecimalFormat AMOUNT_FORMAT;
    static {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.FRANCE);
        symbols.setGroupingSeparator(' ');
        AMOUNT_FORMAT = new DecimalFormat("#,##0", symbols);
    }

    private long currentUserId = 1L;
    private Runnable onBack;

    @FXML private Label balanceLabel;
    @FXML private Button cashoutBtn;
    @FXML private Button filterAll;
    @FXML private Button filterSlots;
    @FXML private Button filterCrash;
    @FXML private GridPane gamesGrid;

    private WalletRepository walletRepo;
    private GameDAO gameDAO;
    private WageringService wageringService;
    private CasinoCashoutService cashoutService;

    private List<Game> allGames;
    private GameType activeFilter = null;

    @FXML
    public void initialize() {
        try {
            this.walletRepo = new WalletRepository();
            this.gameDAO = new GameDAO();
            this.wageringService = new WageringService();
            this.cashoutService = new CasinoCashoutService();
        } catch (SQLException e) {
            showError("Connexion BDD impossible", e.getMessage());
            return;
        }

        allGames = gameDAO.findAll();
        refreshGrid();
        refreshBalance();
    }

    public void setCurrentUserId(long userId) {
        this.currentUserId = userId;
        refreshBalance();
    }

    public void setOnBack(Runnable onBack) {
        this.onBack = onBack;
    }

    private void refreshBalance() {
        WalletSnapshot wallet = walletRepo.findByUserId(currentUserId);
        double virtual = wallet == null ? 0 : wallet.virtualBalance();

        balanceLabel.setText(formatAmount(virtual));

        boolean canCashout = wageringService.canCashout(currentUserId);
        cashoutBtn.setDisable(!canCashout);
    }

    @FXML
    private void onCashoutClicked() {
        try {
            double cashed = cashoutService.cashoutAll(currentUserId);
            Alert ok = new Alert(Alert.AlertType.INFORMATION);
            ok.setHeaderText("Cashout réussi");
            ok.setContentText(formatAmount(cashed) + " $ transférés vers votre solde réel.");
            ok.showAndWait();
        } catch (CasinoCashoutException e) {
            showError("Cashout impossible", e.getMessage());
        }
        refreshBalance();
    }

    @FXML private void onFilterAll()   { activeFilter = null;            updateFilterUI(filterAll); }
    @FXML private void onFilterSlots() { activeFilter = GameType.SLOT;   updateFilterUI(filterSlots); }
    @FXML private void onFilterCrash() { activeFilter = GameType.CRASH;  updateFilterUI(filterCrash); }

    private void updateFilterUI(Button active) {
        for (Button b : List.of(filterAll, filterSlots, filterCrash)) {
            b.getStyleClass().remove("active");
        }
        if (!active.getStyleClass().contains("active")) {
            active.getStyleClass().add("active");
        }
        refreshGrid();
    }

    private void refreshGrid() {
        gamesGrid.getChildren().clear();
        gamesGrid.getColumnConstraints().clear();

        int col = 0;
        for (Game g : allGames) {
            if (activeFilter != null && g.getType() != activeFilter) continue;
            gamesGrid.add(buildCard(g), col, 0);
            col++;
        }
    }

    private VBox buildCard(Game game) {
        VBox card = new VBox();
        card.getStyleClass().add("casino-card");

        Label emoji = new Label(emojiFor(game));
        emoji.getStyleClass().add("card-emoji");

        Label name = new Label(game.getName());
        name.getStyleClass().add("card-name");

        card.getChildren().addAll(emoji, name);
        card.setOnMouseClicked(e -> onGameClicked(game));
        return card;
    }

    private void onGameClicked(Game game) {
        Alert info = new Alert(Alert.AlertType.INFORMATION);
        info.setHeaderText(game.getName());
        info.setContentText(
                game.getType() == GameType.SLOT
                        ? "Slot machine en cours de développement."
                        : "Crash game en cours de développement."
        );
        info.showAndWait();
    }

    @FXML
    private void onBackClicked() {
        if (onBack != null) onBack.run();
    }

    private String emojiFor(Game game) {
        return switch (game.getType()) {
            case SLOT  -> "💎";
            case CRASH -> "🚀";
        };
    }

    private static String formatAmount(double v) {
        return AMOUNT_FORMAT.format(v);
    }

    private void showError(String header, String content) {
        Alert err = new Alert(Alert.AlertType.ERROR);
        err.setHeaderText(header);
        err.setContentText(content);
        err.showAndWait();
    }
}
