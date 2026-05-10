package com.novabet.app.controllers;

import com.novabet.app.services.UserService;
import com.novabet.app.services.TransactionService;
import java.sql.SQLException;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import javafx.scene.input.MouseEvent;

public class OnboardingController {

    @FXML
    private VBox step1Box;
    @FXML
    private VBox step2Box;
    @FXML
    private VBox step3Box;
    @FXML
    private VBox step4Box;
    @FXML
    private VBox loginBox;
    @FXML
    private VBox cguBox;

    @FXML
    private VBox card50;
    @FXML
    private VBox card250;
    @FXML
    private VBox card1000;
    @FXML
    private TextField customAmountField;
    @FXML
    private Button depositButton;

    @FXML
    private Button btnCard;
    @FXML
    private Button btnCrypto;
    @FXML
    private Button btnBank;
    @FXML
    private Button btnApplePay;

    @FXML
    private Button finishBtn;
    private int selectedCategoriesCount = 0;

    private final UserService userService = new UserService();
    private final TransactionService transactionService = new TransactionService();
    private long currentUserId = -1;

    private boolean isAmountSelected = false;
    private boolean isPaymentSelected = false;

    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField confirmPasswordField;
    
    @FXML
    private TextField loginEmailField;
    @FXML
    private PasswordField loginPasswordField;
    @FXML
    private Label loginError;
    @FXML
    private TextField referralField;
    @FXML
    private CheckBox termsCheckBox;
    @FXML
    private Button createAccountBtn;
    @FXML
    private Label emailError;
    @FXML
    private Label passwordError;
    @FXML
    private Label confirmPasswordError;

    @FXML
    public void loginWithGoogle() {
        // TODO : intégration login Google
    }

    @FXML
    public void loginWithApple() {
        // TODO : intégration login Apple
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
    public void selectAmount50() {
        setAmount("50", card50);
    }

    @FXML
    public void selectAmount250() {
        setAmount("250", card250);
    }

    @FXML
    public void selectAmount1000() {
        setAmount("1 000", card1000);
    }

    private void setAmount(String amount, VBox activeCard) {
        depositButton.setText("Déposer " + amount + "$ →");
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

    @FXML
    public void selectPaymentMethod(ActionEvent event) {
        btnCard.getStyleClass().remove("payment-btn-active");
        btnCrypto.getStyleClass().remove("payment-btn-active");
        btnBank.getStyleClass().remove("payment-btn-active");
        btnApplePay.getStyleClass().remove("payment-btn-active");

        Button clickedButton = (Button) event.getSource();

        clickedButton.getStyleClass().add("payment-btn-active");
        isPaymentSelected = true;
        validateDepositForm();
    }

    private void validateDepositForm() {
        if (depositButton != null) {
            boolean hasCustomAmount = customAmountField != null && !customAmountField.getText().trim().isEmpty();
            depositButton.setDisable(!(isAmountSelected || hasCustomAmount) || !isPaymentSelected);
        }
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
        step2Box.setVisible(false);
        step1Box.setVisible(true);
    }

    @FXML
    public void goToStep3() {
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
        step3Box.setVisible(false);
        step4Box.setVisible(true);
    }

    @FXML
    public void goToStep4() {
        String amountText = customAmountField.getText().trim();
        if (amountText.isEmpty()) {
            amountText = depositButton.getText()
                    .replace("Déposer ", "")
                    .replace("$ →", "")
                    .replace(" ", "");
        }

        try {
            double amount = Double.parseDouble(amountText);
            transactionService.createDeposit(currentUserId, amount);
            step3Box.setVisible(false);
            step4Box.setVisible(true);
        } catch (NumberFormatException e) {
            step3Box.setVisible(false);
            step4Box.setVisible(true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void goBackToStep3() {
        step4Box.setVisible(false);
        step3Box.setVisible(true);
    }

    @FXML
    public void finishOnboarding() {
        // TODO implementer transition vers la place des marchés
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
}
