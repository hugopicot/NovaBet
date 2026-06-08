package com.polymarket.ui;

import com.polymarket.service.AuthService;

import javax.swing.*;

import java.awt.*;

import java.awt.event.*;

public class RegisterFrame extends JFrame {

    private AuthService authService;

    private JTextField usernameField;

    private JTextField emailField;

    private JPasswordField passwordField;

    private JPasswordField confirmPasswordField;

    public RegisterFrame(AuthService authService) {

        this.authService = authService;

        initUI();

    }

    private void initUI() {

        setTitle("💎 Diamond Slots - Inscription");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setSize(450, 650);

        setLocationRelativeTo(null);

        setResizable(false);

        JPanel mainPanel = new JPanel() {

            @Override

            protected void paintComponent(Graphics g) {

                super.paintComponent(g);

                Graphics2D g2d = (Graphics2D) g;

                GradientPaint gradient = new GradientPaint(

                        0, 0, new Color(30, 30, 60),

                        0, getHeight(), new Color(60, 20, 80)

                );

                g2d.setPaint(gradient);

                g2d.fillRect(0, 0, getWidth(), getHeight());

            }

        };

        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        JLabel titleLabel = new JLabel("💎 CRÉER UN COMPTE");

        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));

        titleLabel.setForeground(new Color(255, 215, 0));

        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel bonusLabel = new JLabel("🎁 Bonus de bienvenue: 1000 crédits!");

        bonusLabel.setFont(new Font("Arial", Font.ITALIC, 14));

        bonusLabel.setForeground(new Color(100, 255, 100));

        bonusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel formPanel = new JPanel();

        formPanel.setOpaque(false);

        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));

        formPanel.setMaximumSize(new Dimension(300, 350));

        usernameField = createField("Nom d'utilisateur");

        emailField = createField("Email");

        passwordField = createPasswordField("Mot de passe");

        confirmPasswordField = createPasswordField("Confirmer mot de passe");

        JButton registerButton = createButton("S'INSCRIRE", new Color(255, 215, 0), Color.BLACK);

        registerButton.addActionListener(e -> handleRegister());

        JButton backButton = createButton("Retour à la connexion", new Color(100, 100, 150), Color.WHITE);

        backButton.addActionListener(e -> {

            dispose();

            new LoginFrame(authService).setVisible(true);

        });

        formPanel.add(usernameField);

        formPanel.add(Box.createVerticalStrut(10));

        formPanel.add(emailField);

        formPanel.add(Box.createVerticalStrut(10));

        formPanel.add(passwordField);

        formPanel.add(Box.createVerticalStrut(10));

        formPanel.add(confirmPasswordField);

        mainPanel.add(titleLabel);

        mainPanel.add(Box.createVerticalStrut(10));

        mainPanel.add(bonusLabel);

        mainPanel.add(Box.createVerticalStrut(30));

        mainPanel.add(formPanel);

        mainPanel.add(Box.createVerticalStrut(20));

        mainPanel.add(registerButton);

        mainPanel.add(Box.createVerticalStrut(10));

        mainPanel.add(backButton);

        add(mainPanel);

    }

    // 🔧 FIELDS

    private JTextField createField(String placeholder) {

        JTextField field = new JTextField();

        field.setMaximumSize(new Dimension(300, 40));

        return field;

    }

    private JPasswordField createPasswordField(String placeholder) {

        JPasswordField field = new JPasswordField();

        field.setMaximumSize(new Dimension(300, 40));

        return field;

    }

    // 🔧 BUTTON

    private JButton createButton(String text, Color bg, Color fg) {

        JButton button = new JButton(text);

        button.setMaximumSize(new Dimension(300, 45));

        button.setBackground(bg);

        button.setForeground(fg);

        button.setFocusPainted(false);

        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return button;

    }

    // 🔥 REGISTER LOGIC

    private void handleRegister() {

        String username = usernameField.getText().trim();

        String email = emailField.getText().trim();

        String password = new String(passwordField.getPassword());

        String confirm = new String(confirmPasswordField.getPassword());

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {

            JOptionPane.showMessageDialog(this, "Tous les champs sont obligatoires");

            return;

        }

        if (!password.equals(confirm)) {

            JOptionPane.showMessageDialog(this, "Les mots de passe ne correspondent pas");

            return;

        }

        boolean ok = authService.register(username, email, password);

        if (ok) {

            JOptionPane.showMessageDialog(this, "Compte créé !");

            dispose();

            new LoginFrame(authService).setVisible(true);

        } else {

            JOptionPane.showMessageDialog(this, "Erreur lors de l'inscription");

        }

    }

}