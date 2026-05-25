package com.polymarket.app.controllers;

import com.polymarket.app.services.StripeIdentityService;
import com.polymarket.app.services.StripePaymentService;
import com.polymarket.app.services.TransactionService;
import com.polymarket.app.services.UserService;
import com.stripe.exception.StripeException;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.awt.Desktop;
import java.net.URI;
import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class OnboardingController {

    @FXML private VBox step1Box;
    @FXML private VBox step2Box;
    @FXML private VBox step3Box;
    @FXML private VBox step4Box;
    @FXML private VBox loginBox;
    @FXML private VBox cguBox;

    @FXML private VBox card50;
    @FXML private VBox card250;
    @FXML private VBox card1000;
    @FXML private TextField customAmountField;
    @FXML private Button depositButton;
    @FXML private Label depositStatusLabel;

    @FXML private Button finishBtn;
    private int selectedCategoriesCount = 0;

    private final UserService userService = new UserService();
    private final TransactionService transactionService = new TransactionService();
    private final StripeIdentityService identityService = new StripeIdentityService();
    private final StripePaymentService paymentService = new StripePaymentService();

    private long currentUserId = -1;
    private boolean isAmountSelected = false;

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r, "stripe-poller");
        t.setDaemon(true);
        return t;
    });
    private ScheduledFuture<?> currentPoll;

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;

    @FXML private TextField loginEmailField;
    @FXML private PasswordField loginPasswordField;
    @FXML private Label loginError;
    @FXML private TextField referralField;
    @FXML private CheckBox termsCheckBox;
    @FXML private Button createAccountBtn;
    @FXML private Label emailError;
    @FXML private Label passwordError;
    @FXML private Label confirmPasswordError;

    @FXML private Label kycStatusLabel;
    @FXML private Button kycStartBtn;
    @FXML private Button kycContinueBtn;

    @FXML public void loginWithGoogle() {}
    @FXML public void loginWithApple() {}

    private Runnable onOnboardingComplete;

    public void setOnOnboardingComplete(Runnable callback) {
        this.onOnboardingComplete = callback;
    }

    public long getCurrentUserId() {
        return currentUserId;
    }

    @FXML
    public void initialize() {
        if (customAmountField != null) {
            customAmountField.textProperty().addListener((obs, oldVal, newVal) -> {
                if (!newVal.isEmpty()) {
                    depositButton.setText("Déposer " + newVal + "$ →");
                    resetCards();
                    isAmountSelected = false;
                } else {
                    depositButton.setText("Déposer →");
                }
                validateDepositForm();
            });
        }

        if (emailField != null) {
            emailField.textProperty().addListener((obs, o, n) -> validateForm());
            passwordField.textProperty().addListener((obs, o, n) -> validateForm());
            confirmPasswordField.textProperty().addListener((obs, o, n) -> validateForm());
            termsCheckBox.selectedProperty().addListener((obs, o, n) -> validateForm());
        }
    }

    private void validateForm() {
        boolean valid = true;

        String email = emailField.getText().trim();
        if (email.isEmpty()) {
            emailError.setText("");
            emailField.getStyleClass().remove("input-field-error");
            valid = false;
        } else if (!email.matches("^[\\w.+-]+@[\\w-]+\\.[\\w.]+$")) {
            emailError.setText("Adresse email invalide");
            if (!emailField.getStyleClass().contains("input-field-error"))
                emailField.getStyleClass().add("input-field-error");
            valid = false;
        } else {
            emailError.setText("");
            emailField.getStyleClass().remove("input-field-error");
        }

        String pwd = passwordField.getText();
        if (pwd.isEmpty()) {
            passwordError.setText("");
            passwordField.getStyleClass().remove("input-field-error");
            valid = false;
        } else if (pwd.length() < 8) {
            passwordError.setText("Le mot de passe doit contenir au moins 8 caractères");
            if (!passwordField.getStyleClass().contains("input-field-error"))
                passwordField.getStyleClass().add("input-field-error");
            valid = false;
        } else {
            passwordError.setText("");
            passwordField.getStyleClass().remove("input-field-error");
        }

        String confirm = confirmPasswordField.getText();
        if (confirm.isEmpty()) {
            confirmPasswordError.setText("");
            confirmPasswordField.getStyleClass().remove("input-field-error");
            valid = false;
        } else if (!confirm.equals(pwd)) {
            confirmPasswordError.setText("Les mots de passe ne correspondent pas");
            if (!confirmPasswordField.getStyleClass().contains("input-field-error"))
                confirmPasswordField.getStyleClass().add("input-field-error");
            valid = false;
        } else {
            confirmPasswordError.setText("");
            confirmPasswordField.getStyleClass().remove("input-field-error");
        }

        if (!termsCheckBox.isSelected())
            valid = false;

        createAccountBtn.setDisable(!valid);
    }

    @FXML
    public void selectAmount50()   { setAmount(50,   card50); }
    @FXML
    public void selectAmount250()  { setAmount(250,  card250); }
    @FXML
    public void selectAmount1000() { setAmount(1000, card1000); }

    private void setAmount(double amount, VBox activeCard) {
        depositButton.setText("Déposer " + ((long) amount) + "$ →");
        customAmountField.clear();
        resetCards();
        activeCard.getStyleClass().add("deposit-card-active");
        isAmountSelected = true;
        validateDepositForm();
    }

    private void resetCards() {
        card50.getStyleClass().remove("deposit-card-active");
        card250.getStyleClass().remove("deposit-card-active");
        card1000.getStyleClass().remove("deposit-card-active");
    }

    private void validateDepositForm() {
        if (depositButton == null) return;
        boolean hasCustomAmount = customAmountField != null && !customAmountField.getText().trim().isEmpty();
        depositButton.setDisable(!(isAmountSelected || hasCustomAmount));
    }

    @FXML
    public void openTerms(MouseEvent e) {
        step1Box.setVisible(false);
        cguBox.setVisible(true);
    }

    @FXML
    public void closeTerms(ActionEvent e) {
        cguBox.setVisible(false);
        step1Box.setVisible(true);
    }

    @FXML
    public void openLogin(MouseEvent e) {
        step1Box.setVisible(false);
        loginBox.setVisible(true);
    }

    @FXML
    public void goBackToStep1FromLogin(MouseEvent e) {
        loginBox.setVisible(false);
        step1Box.setVisible(true);
    }

    @FXML
    public void loginUser() {
        String email = loginEmailField.getText().trim();
        String password = loginPasswordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            loginError.setText("Veuillez remplir tous les champs.");
            return;
        }

        try {
            long userId = userService.loginUser(email, password);
            if (userId != -1) {
                this.currentUserId = userId;
                loginError.setText("");
                finishOnboarding();
            } else {
                loginError.setText("Email ou mot de passe incorrect.");
            }
        } catch (SQLException e) {
            loginError.setText("Erreur de connexion au serveur.");
            e.printStackTrace();
        }
    }

    @FXML
    public void goToStep2() {
        String email = emailField.getText().trim();
        String password = passwordField.getText();

        try {
            long userId = userService.registerUser(email, password);
            if (userId == -1) {
                emailError.setText("Cet email est déjà utilisé.");
                if (!emailField.getStyleClass().contains("input-field-error"))
                    emailField.getStyleClass().add("input-field-error");
                return;
            }
            currentUserId = userId;
            step1Box.setVisible(false);
            step2Box.setVisible(true);
        } catch (SQLException e) {
            emailError.setText("Erreur serveur, réessaie.");
        }
    }

    @FXML
    public void goBackToStep1() {
        cancelCurrentPoll();
        step2Box.setVisible(false);
        step1Box.setVisible(true);
    }

    @FXML
    public void startKyc() {
        if (currentUserId == -1) {
            kycStatusLabel.setText("Erreur : utilisateur non identifié.");
            return;
        }
        kycStartBtn.setDisable(true);
        kycStatusLabel.setText("Ouverture de la vérification dans votre navigateur…");

        new Thread(() -> {
            try {
                StripeIdentityService.VerificationHandle h = identityService.createVerification(currentUserId);
                userService.setKycSession(currentUserId, h.sessionId());
                openInBrowser(h.hostedUrl());
                Platform.runLater(() -> kycStatusLabel.setText(
                        "Finalise la vérification dans le navigateur — on attend la confirmation…"));
                pollKycStatus(h.sessionId());
            } catch (StripeException | SQLException ex) {
                Platform.runLater(() -> {
                    kycStatusLabel.setText("Erreur Stripe : " + ex.getMessage());
                    kycStartBtn.setDisable(false);
                });
            }
        }, "kyc-start").start();
    }

    private void pollKycStatus(String sessionId) {
        cancelCurrentPoll();
        currentPoll = scheduler.scheduleAtFixedRate(() -> {
            try {
                String status = identityService.fetchStatus(sessionId);
                Platform.runLater(() -> kycStatusLabel.setText("Statut : " + humanKycStatus(status)));
                if ("verified".equals(status)) {
                    userService.setKycStatus(currentUserId, "verified");
                    cancelCurrentPoll();
                    Platform.runLater(() -> {
                        kycStatusLabel.setText("Identité vérifiée ✓");
                        kycStartBtn.setVisible(false);
                        kycStartBtn.setManaged(false);
                        kycContinueBtn.setVisible(true);
                        kycContinueBtn.setManaged(true);
                    });
                } else if ("canceled".equals(status)) {
                    userService.setKycStatus(currentUserId, "rejected");
                    cancelCurrentPoll();
                    Platform.runLater(() -> {
                        kycStatusLabel.setText("Vérification annulée. Réessaie.");
                        kycStartBtn.setDisable(false);
                    });
                }
            } catch (StripeException | SQLException ex) {
                Platform.runLater(() -> kycStatusLabel.setText("Erreur de polling : " + ex.getMessage()));
            }
        }, 0, 3, TimeUnit.SECONDS);
    }

    private String humanKycStatus(String s) {
        return switch (s) {
            case "requires_input" -> "en attente de tes informations";
            case "processing"     -> "vérification en cours par Stripe…";
            case "verified"       -> "vérifié ✓";
            case "canceled"       -> "annulé";
            default               -> s;
        };
    }

    @FXML
    public void goToStep3() {
        cancelCurrentPoll();
        step2Box.setVisible(false);
        step3Box.setVisible(true);
    }

    @FXML
    public void goBackToStep2() {
        step3Box.setVisible(false);
        step2Box.setVisible(true);
    }

    @FXML
    public void skipToStep4() {
        cancelCurrentPoll();
        step3Box.setVisible(false);
        step4Box.setVisible(true);
    }

    @FXML
    public void goToStep4() {
        double amount = parseDepositAmount();
        if (amount <= 0) {
            depositStatusLabel.setText("Montant invalide.");
            return;
        }
        if (currentUserId == -1) {
            depositStatusLabel.setText("Erreur : utilisateur non identifié.");
            return;
        }

        depositButton.setDisable(true);
        depositStatusLabel.setText("Création de la session de paiement…");

        new Thread(() -> {
            try {
                StripePaymentService.CheckoutHandle h = paymentService.createCheckout(currentUserId, amount);
                long txId = transactionService.createPendingDeposit(currentUserId, amount, h.sessionId());
                openInBrowser(h.hostedUrl());
                Platform.runLater(() -> depositStatusLabel.setText(
                        "Finalise le paiement dans le navigateur — on attend la confirmation…"));
                pollPaymentStatus(h.sessionId(), txId, amount);
            } catch (StripeException | SQLException ex) {
                Platform.runLater(() -> {
                    depositStatusLabel.setText("Erreur Stripe : " + ex.getMessage());
                    depositButton.setDisable(false);
                });
            }
        }, "checkout-start").start();
    }

    private double parseDepositAmount() {
        String custom = customAmountField.getText().trim();
        if (!custom.isEmpty()) {
            try {
                return Double.parseDouble(custom.replace(",", "."));
            } catch (NumberFormatException e) {
                return -1;
            }
        }
        String label = depositButton.getText()
                .replace("Déposer ", "")
                .replace("$ →", "")
                .replace(" ", "")
                .replace(",", ".");
        try {
            return Double.parseDouble(label);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private void pollPaymentStatus(String sessionId, long txId, double amount) {
        cancelCurrentPoll();
        currentPoll = scheduler.scheduleAtFixedRate(() -> {
            try {
                StripePaymentService.CheckoutResult res = paymentService.fetchStatus(sessionId);
                if ("paid".equals(res.status())) {
                    transactionService.confirmDeposit(txId, currentUserId, amount, res.paymentMethodType());
                    cancelCurrentPoll();
                    Platform.runLater(() -> {
                        depositStatusLabel.setText("Paiement confirmé ✓ — passage à l'étape suivante.");
                        step3Box.setVisible(false);
                        step4Box.setVisible(true);
                    });
                }
            } catch (StripeException | SQLException ex) {
                Platform.runLater(() -> depositStatusLabel.setText("Erreur de polling : " + ex.getMessage()));
            }
        }, 0, 2, TimeUnit.SECONDS);
    }

    @FXML
    public void goBackToStep3() {
        step4Box.setVisible(false);
        step3Box.setVisible(true);
    }

    @FXML
    public void finishOnboarding() {
        cancelCurrentPoll();
        if (onOnboardingComplete != null) {
            onOnboardingComplete.run();
        }
    }

    @FXML
    public void toggleCategory(MouseEvent event) {
        VBox clickedCard = (VBox) event.getSource();

        if (clickedCard.getStyleClass().contains("category-card-active")) {
            clickedCard.getStyleClass().remove("category-card-active");
            selectedCategoriesCount--;
        } else {
            clickedCard.getStyleClass().add("category-card-active");
            selectedCategoriesCount++;
        }

        if (finishBtn != null) {
            finishBtn.setDisable(selectedCategoriesCount < 2);
        }
    }

    private void openInBrowser(String url) {
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(URI.create(url));
            }
        } catch (Exception e) {
            System.err.println("Impossible d'ouvrir le navigateur : " + e.getMessage());
        }
    }

    private void cancelCurrentPoll() {
        if (currentPoll != null && !currentPoll.isDone()) {
            currentPoll.cancel(false);
        }
    }
}
