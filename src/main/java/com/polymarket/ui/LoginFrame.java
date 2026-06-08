package com.polymarket.ui;

import com.polymarket.service.AuthService;

import javax.swing.*;

import java.awt.*;

import java.awt.event.*;

public class LoginFrame extends JFrame {

    private AuthService authService;

    private JTextField usernameField;

    private JPasswordField passwordField;

    public LoginFrame(AuthService authService) {

        this.authService = authService;

        initUI();

    }

    private void initUI() {

        setTitle("💎 Diamond Slots - Connexion");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setSize(450, 550);

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

        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 50, 40, 50));

        JLabel titleLabel = new JLabel("💎 DIAMOND SLOTS");

        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));

        titleLabel.setForeground(new Color(255, 215, 0));

        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Connectez-vous pour jouer");

        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        subtitleLabel.setForeground(Color.WHITE);

        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel formPanel = new JPanel();

        formPanel.setOpaque(false);

        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));

        formPanel.setMaximumSize(new Dimension(300, 200));

        JLabel userLabel = new JLabel("Nom d'utilisateur");

        userLabel.setForeground(Color.WHITE);

        usernameField = createStyledTextField();

        JLabel passLabel = new JLabel("Mot de passe");

        passLabel.setForeground(Color.WHITE);

        passwordField = createStyledPasswordField();

        formPanel.add(userLabel);

        formPanel.add(Box.createVerticalStrut(5));

        formPanel.add(usernameField);

        formPanel.add(Box.createVerticalStrut(15));

        formPanel.add(passLabel);

        formPanel.add(Box.createVerticalStrut(5));

        formPanel.add(passwordField);

        JButton loginButton = createStyledButton("SE CONNECTER", new Color(255, 215, 0), Color.BLACK);

        loginButton.addActionListener(e -> handleLogin());

        JButton registerButton = createStyledButton("Créer un compte", new Color(100, 100, 150), Color.WHITE);

        registerButton.addActionListener(e -> openRegister());

        mainPanel.add(titleLabel);

        mainPanel.add(Box.createVerticalStrut(10));

        mainPanel.add(subtitleLabel);

        mainPanel.add(Box.createVerticalStrut(50));

        mainPanel.add(formPanel);

        mainPanel.add(Box.createVerticalStrut(30));

        mainPanel.add(loginButton);

        mainPanel.add(Box.createVerticalStrut(15));

        mainPanel.add(registerButton);

        add(mainPanel);

    }

    private JTextField createStyledTextField() {

        JTextField field = new JTextField();

        field.setMaximumSize(new Dimension(300, 40));

        field.setBackground(new Color(50, 50, 80));

        field.setForeground(Color.WHITE);

        return field;

    }

    private JPasswordField createStyledPasswordField() {

        JPasswordField field = new JPasswordField();

        field.setMaximumSize(new Dimension(300, 40));

        field.setBackground(new Color(50, 50, 80));

        field.setForeground(Color.WHITE);

        return field;

    }

    private JButton createStyledButton(String text, Color bg, Color fg) {

        JButton button = new JButton(text);

        button.setMaximumSize(new Dimension(300, 45));

        button.setAlignmentX(Component.CENTER_ALIGNMENT);

        button.setBackground(bg);

        button.setForeground(fg);

        button.setFocusPainted(false);

        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return button;

    }

    private void handleLogin() {

        String username = usernameField.getText().trim();

        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {

            JOptionPane.showMessageDialog(this,

                    "Veuillez remplir tous les champs");

            return;

        }

        try {

            boolean ok = authService.login(username, password);

            if (ok) {

                dispose();

                // ⚠️ à créer si pas encore fait

                new SlotMachineFrame(authService).setVisible(true);

            } else {

                JOptionPane.showMessageDialog(this,

                        "Identifiants incorrects");

            }

        } catch (Exception e) {

            JOptionPane.showMessageDialog(this,

                    "Erreur serveur: " + e.getMessage());

        }

    }

    private void openRegister() {

        dispose();

        // ⚠️ à créer si pas encore fait

        new RegisterFrame(authService).setVisible(true);

    }

    // 🔥 START APP

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            AuthService authService = new AuthService();

            new LoginFrame(authService).setVisible(true);

        });

    }

}