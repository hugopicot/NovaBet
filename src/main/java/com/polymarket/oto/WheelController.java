package com.polymarket.oto;

import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class WheelController {

    /** Couleurs visuelles des 8 segments (alignées sur WheelLogic.SLICE_MULTIPLIERS). */
    private static final String[] SLICE_COLORS = {
            "#3b6db5",   // 2× — bleu
            "#d847a9",   // 10× — magenta
            "#3b6db5",   // 2× — bleu
            "#e0ac2e",   // 5× — gold
            "#3b6db5",   // 2× — bleu
            "#e0ac2e",   // 5× — gold
            "#3b6db5",   // 2× — bleu
            "#e0ac2e",   // 5× — gold
    };
    private static final double WHEEL_RADIUS = 140;
    private static final Duration SPIN_DURATION = Duration.seconds(4.2);

    private static final DecimalFormat AMOUNT_FORMAT;
    static {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.FRANCE);
        symbols.setGroupingSeparator(' ');
        AMOUNT_FORMAT = new DecimalFormat("#,##0", symbols);
    }

    @FXML private StackPane wheelContainer;
    @FXML private Label wheelSubtitle;
    @FXML private Label stakeLabel;
    @FXML private Label landedLabel;
    @FXML private Label creditsLabel;
    @FXML private Button playInCasinoBtn;
    @FXML private Label footerNote;

    private final WheelLogic logic = new WheelLogic();
    private Group wheelGroup;
    private StackPane hub;
    private double currentStake = 746;
    private boolean spinning = false;
    private boolean spun = false;
private Runnable onPlayInCasino;

    public void setOnPlayInCasino(Runnable onPlayInCasino) {
        this.onPlayInCasino = onPlayInCasino;
    }

    @FXML
    public void initialize() {
        buildWheel();
    }

    /** Appelé par OneTimeOfferController juste après le chargement. */
    public void setStake(double stake) {
        this.currentStake = stake;
        wheelSubtitle.setText(
                "Un spin gratuit sur tes " + formatAmount(stake) +
                " $ · clique sur SPIN pour découvrir ton multiplicateur"
        );
        stakeLabel.setText(formatAmount(stake) + " $");
    }

    // ────────────────────────────────────────────────────────────────
    // Construction de la roue
    // ────────────────────────────────────────────────────────────────
    private void buildWheel() {
        // Group qui contient les segments (c'est lui qu'on rotate)
        wheelGroup = new Group();

        for (int i = 0; i < WheelLogic.SLICE_MULTIPLIERS.length; i++) {
            // Arc : startAngle est le bord trigo (sens antihoraire depuis 3h)
            double startAngle = i * WheelLogic.SEGMENT_DEG;
            Arc arc = new Arc(0, 0, WHEEL_RADIUS, WHEEL_RADIUS, startAngle, WheelLogic.SEGMENT_DEG);
            arc.setType(ArcType.ROUND);
            arc.setFill(Color.web(SLICE_COLORS[i]));
            arc.setStroke(Color.web("#1a1224"));
            arc.setStrokeWidth(2);
            wheelGroup.getChildren().add(arc);

            // Label : positionné sur le rayon du segment, pivoté pour être upright
            // quand le segment passe en haut (sous le pointeur).
            double centerAngleTrig = startAngle + WheelLogic.SEGMENT_DEG / 2;
            double centerAngleRad  = Math.toRadians(centerAngleTrig);
            double labelRadius     = WHEEL_RADIUS * 0.70;
            double x = Math.cos(centerAngleRad) * labelRadius;
            double y = -Math.sin(centerAngleRad) * labelRadius;

            Text label = new Text(WheelLogic.SLICE_MULTIPLIERS[i] + "×");
            label.setFill(Color.web("#1a1224"));
            label.setFont(Font.font("Consolas", FontWeight.EXTRA_BOLD, 22));

            // StackPane = boîte de taille fixe centrée → translation simple sur (x, y).
            StackPane labelHolder = new StackPane(label);
            labelHolder.setPrefSize(50, 30);
            labelHolder.setMaxSize(50, 30);
            labelHolder.setMinSize(50, 30);
            labelHolder.setTranslateX(x - 25);
            labelHolder.setTranslateY(y - 15);
            // Rotation = angle visuel horaire depuis le haut.
            // 90 - centerAngleTrig fait correspondre : segment au top → 0°, droite → 90°, bas → 180°…
            labelHolder.setRotate(90 - centerAngleTrig);
            wheelGroup.getChildren().add(labelHolder);
        }

        // Cercle de bordure extérieure (anneau gold)
        Circle outerRing = new Circle(0, 0, WHEEL_RADIUS + 4);
        outerRing.setFill(Color.TRANSPARENT);
        outerRing.setStroke(Color.web("#e0ac2e"));
        outerRing.setStrokeWidth(6);
        outerRing.setEffect(new DropShadow(40, Color.web("#e0ac2e", 0.5)));

        // Pointer (triangle gold) en haut de la roue — pointe vers le haut, base contre le bord
        double baseY = -WHEEL_RADIUS + 6;   // base juste à l'intérieur du bord supérieur
        double tipY  = -WHEEL_RADIUS - 24;  // pointe au-dessus de la roue
        Polygon pointer = new Polygon(
                -12, baseY,
                 12, baseY,
                  0, tipY
        );
        pointer.setFill(Color.web("#e0ac2e"));
        pointer.setStroke(Color.web("#2a2014"));
        pointer.setStrokeWidth(1);
        pointer.setEffect(new DropShadow(8, Color.color(0, 0, 0, 0.6)));

        // Hub central — cliquable pour lancer le spin
        hub = new StackPane();
        hub.setPrefSize(64, 64);
        hub.setMaxSize(64, 64);
        hub.getStyleClass().add("wheel-hub");
        Label spinLabel = new Label("SPIN");
        spinLabel.getStyleClass().add("wheel-hub-label");
        hub.getChildren().add(spinLabel);
        hub.setOnMouseClicked(e -> onHubClicked());

        wheelContainer.getChildren().addAll(wheelGroup, outerRing, pointer, hub);
    }

    // ────────────────────────────────────────────────────────────────
    // Spin
    // ────────────────────────────────────────────────────────────────
    private void onHubClicked() {
        if (spinning || spun) return;
        spinning = true;
        hub.setDisable(true);
        hub.setOpacity(0.6);

        int chosenMultiplier = logic.drawMultiplier();
        int chosenIndex = logic.pickSegmentIndex(chosenMultiplier);
        double targetRotation = logic.computeTargetRotation(chosenIndex);

        RotateTransition rt = new RotateTransition(SPIN_DURATION, wheelGroup);
        rt.setFromAngle(0);
        rt.setToAngle(targetRotation);
        rt.setInterpolator(Interpolator.SPLINE(0.18, 0.85, 0.2, 1.0));
        rt.setOnFinished(e -> showResult(chosenMultiplier));
        rt.play();
    }

    private void showResult(int multiplier) {
        spinning = false;
        spun = true;
        double credits = currentStake * multiplier;

        landedLabel.setText(multiplier + "×");
        creditsLabel.setText(formatAmount(credits) + " $");

        playInCasinoBtn.setText("Jouer avec " + formatAmount(credits) + " $ au casino →");
        playInCasinoBtn.setVisible(true);
        playInCasinoBtn.setManaged(true);
        footerNote.setVisible(true);
        footerNote.setManaged(true);

        // Le hub devient inactif visuellement
        hub.setVisible(false);
    }

    @FXML
    private void onPlayInCasinoClicked() {
if (onPlayInCasino != null) {
            onPlayInCasino.run();
            return;
        }
        playInCasinoBtn.setDisable(true);
        playInCasinoBtn.setText("Casino indisponible");
    }

    private static String formatAmount(double v) {
        return AMOUNT_FORMAT.format(v);
    }
}
