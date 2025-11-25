// src/main/java/com/connectme/view/LoginScreen.java
package com.connectme.view;

import com.connectme.controller.AuthController;
import com.connectme.model.entities.User;
import com.connectme.model.util.HashUtil;
import com.connectme.view.componet.MyButton;
import com.connectme.view.componet.MyPasswordField;
import com.connectme.view.componet.MyTextField;
import com.connectme.view.componet.RoundedBorder;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.util.logging.Logger;

public class LoginScreen extends JFrame {
    private static final Logger logger = Logger.getLogger(LoginScreen.class.getName());

    private MyTextField usernameField;
    private MyPasswordField senhaField;
    private MyButton loginBtn;
    private JLabel errorLabel;
    private AuthController authController;
    private Runnable onLoginSuccess;
    private User authenticatedUser;

    public LoginScreen() {
        setTitle("ConnectMe - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 650);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        //setResizable(false);

        this.authController = new AuthController();
        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new MigLayout("fill, insets 40", "[center]", "[center]"));
        mainPanel.setBackground(new Color(25, 118, 210));

        JPanel cardPanel = new JPanel(new MigLayout("wrap, insets 40", "[fill,450]", "[]15[]"));
        cardPanel.setBackground(Color.WHITE);
        cardPanel.setBorder(new RoundedBorder(20, new Color(220, 220, 225)));
        cardPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        // Título
        JLabel titleLabel = new JLabel("Bem-vindo ao ConnectMe");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        cardPanel.add(titleLabel, "center, wrap 3");

        JLabel subtitleLabel = new JLabel("Sistema de Gestão de Contactos");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        subtitleLabel.setForeground(new Color(120, 120, 120));
        cardPanel.add(subtitleLabel, "center, wrap 25");

        // Username
        cardPanel.add(new JLabel("Username"), "");
        usernameField = new MyTextField();
        usernameField.setFont(new Font("Arial", Font.PLAIN, 12));
        usernameField.setText("admin");
        cardPanel.add(usernameField, "grow, wrap 15");

        // Senha
        cardPanel.add(new JLabel("Senha"), "");
        senhaField = new MyPasswordField();
        senhaField.setFont(new Font("Arial", Font.PLAIN, 12));
        cardPanel.add(senhaField, "grow, wrap 15");

        // Erro
        errorLabel = new JLabel();
        errorLabel.setForeground(new Color(244, 67, 54));
        errorLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        cardPanel.add(errorLabel, "grow, wrap 15");

        // Botão Login
        loginBtn = new MyButton();
        loginBtn.setText("Entrar");
        loginBtn.setPreferredSize(new Dimension(120, 45));
        loginBtn.setFocusPainted(false);
        loginBtn.setBorder(BorderFactory.createEmptyBorder());
        cardPanel.add(loginBtn, "grow, wrap 20");

        mainPanel.add(cardPanel);
        add(mainPanel);

        setupListeners();
    }

    private void setupListeners() {
        loginBtn.addActionListener(e -> handleLogin());
        senhaField.addActionListener(e -> handleLogin());
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(senhaField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Username e senha são obrigatórios");
            logger.warning("Tentativa de login com campos vazios");
            return;
        }

        logger.info("Tentando login com username: " + username);

        // Usar o AuthController para fazer login
        if (authController.login(username, password)) {
            this.authenticatedUser = authController.getLoggedInUser();
            logger.info("Login bem-sucedido para: " + username);

            if (onLoginSuccess != null) {
                onLoginSuccess.run();
            }
            dispose();
        } else {
            errorLabel.setText("Username ou senha incorretos");
            senhaField.setText("");
            logger.warning("Falha no login para: " + username);
        }
    }

    public User getAuthenticatedUser() {
        return authenticatedUser;
    }

    public void setOnLoginSuccess(Runnable callback) {
        this.onLoginSuccess = callback;
    }
}