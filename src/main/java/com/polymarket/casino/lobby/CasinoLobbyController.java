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
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

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

    /** TODO : injecter via SessionManager quand l'auth sera branchée. */
    private long currentUserId = 1L;

    @FXML private Label balanceLabel;
    @FXML private Label heroTitle;
    @FXML private Label heroSubtitle;
    @FXML private ProgressBar wagerProgress;
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
    private GameType activeFilter = null; // null = All

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
        refreshHero();
    }

    public void setCurrentUserId(long userId) {
        this.currentUserId = userId;
        refreshHero();
    }

    // ─── Hero ribbon + balance pill ────────────────────────────────

    private void refreshHero() {
        WalletSnapshot wallet = walletRepo.findByUserId(currentUserId);
        double virtual  = wallet == null ? 0 : wallet.virtualBalance();
        double wagered  = wallet == null ? 0 : wallet.wageredAmount();

        balanceLabel.setText(formatAmount(virtual));
        heroTitle.setText("Your $" + formatAmount(virtual) + " prize is hot.");
        heroSubtitle.setText("Wager 1x to unlock withdrawal · "
                + formatAmount(wagered) + " / " + formatAmount(virtual) + " wagered");

        double progress = virtual <= 0 ? 0 : Math.min(1.0, wagered / virtual);
        wagerProgress.setProgress(progress);

        boolean canCashout = wageringService.canCashout(currentUserId);
        cashoutBtn.setDisable(!canCashout);
        cashoutBtn.setText(canCashout
                ? "Cashout " + formatAmount(virtual) + " $"
                : "Cashout locked");
    }

    @FXML
    private void onCashoutClicked() {
        try {
            double cashed = cashoutService.cashoutAll(currentUserId);
            Alert ok = new Alert(Alert.AlertType.INFORMATION);
            ok.setHeaderText("Cashout reussi");
            ok.setContentText(formatAmount(cashed) + " $ transferes vers votre solde reel.");
            ok.showAndWait();
        } catch (CasinoCashoutException e) {
            showError("Cashout impossible", e.getMessage());
        }
        refreshHero();
    }

    // ─── Filtres (cosmetic + filtre la grille) ─────────────────────

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

    // ─── Grille des jeux ───────────────────────────────────────────

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
        if (game.getType() == GameType.SLOT) {
            card.getStyleClass().add("featured");
        }

        // Art zone (emoji + tag)
        StackPane art = new StackPane();
        art.getStyleClass().add("card-art");
        Label emoji = new Label(emojiFor(game));
        emoji.getStyleClass().add("card-emoji");
        art.getChildren().add(emoji);

        Label tag = new Label(tagFor(game));
        tag.getStyleClass().add("card-tag");
        if (game.getType() == GameType.SLOT) tag.getStyleClass().add("featured");
        StackPane.setAlignment(tag, javafx.geometry.Pos.TOP_LEFT);
        StackPane.setMargin(tag, new javafx.geometry.Insets(10, 0, 0, 10));
        art.getChildren().add(tag);

        // Footer (name + meta)
        VBox footer = new VBox(3);
        footer.getStyleClass().add("card-footer");
        Label name = new Label(game.getName());
        name.getStyleClass().add("card-name");
        HBox meta = new HBox(8);
        Label rtpLbl = new Label("RTP");
        rtpLbl.getStyleClass().add("card-meta");
        Label rtpVal = new Label(rtpFor(game) + "%");
        rtpVal.getStyleClass().add("card-meta-gold");
        meta.getChildren().addAll(rtpLbl, rtpVal);
        footer.getChildren().addAll(name, meta);

        Region grow = new Region();
        VBox.setVgrow(grow, javafx.scene.layout.Priority.ALWAYS);

        card.getChildren().addAll(art, grow, footer);
        card.setOnMouseClicked(e -> onGameClicked(game));
        return card;
    }

    private void onGameClicked(Game game) {
        Alert info = new Alert(Alert.AlertType.INFORMATION);
        info.setHeaderText(game.getName());
        info.setContentText(
                game.getType() == GameType.SLOT
                        ? "Slot machine en cours de developpement (feature/casino-slot-machine)."
                        : "Crash game en cours de developpement (feature/casino-crash-game)."
        );
        info.showAndWait();
    }

    @FXML
    private void onBackClicked() {
        Stage stage = (Stage) ((Node) cashoutBtn).getScene().getWindow();
        stage.close();
    }

    // ─── Catalogue cosmetic (emoji / tag / RTP affichés) ───────────

    private String emojiFor(Game game) {
        return switch (game.getType()) {
            case SLOT  -> "💎";
            case CRASH -> "🚀";
        };
    }

    private String tagFor(Game game) {
        return switch (game.getType()) {
            case SLOT  -> "Featured";
            case CRASH -> "Hot";
        };
    }

    private String rtpFor(Game game) {
        return switch (game.getType()) {
            case SLOT  -> "96.4";
            case CRASH -> "97.0";
        };
    }

    // ─── Utilities ─────────────────────────────────────────────────

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
