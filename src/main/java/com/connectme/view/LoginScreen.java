package com.connectme.view;

import javax.swing.*;
import net.miginfocom.swing.MigLayout;
import com.connectme.controller.UserController;

public class LoginScreen extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;

    private UserController controller;

    public LoginScreen() {
        super("ConnectMe - Login");

        controller = new UserController();

        setLayout(new MigLayout("wrap 1", "[300]", "[]20[]10[]10[]"));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(380, 260);
        setLocationRelativeTo(null); // centro da tela
        setResizable(false);

        initComponents();
    }

    private void initComponents() {

        JLabel lblTitle = new JLabel("ConnectMe - Acesso");
        lblTitle.setFont(lblTitle.getFont().deriveFont(20f));
        add(lblTitle, "align center");

        txtUsername = new JTextField();
        txtUsername.setBorder(BorderFactory.createTitledBorder("Usuário"));
        add(txtUsername, "growx");

        txtPassword = new JPasswordField();
        txtPassword.setBorder(BorderFactory.createTitledBorder("Senha"));
        add(txtPassword, "growx");

        btnLogin = new JButton("Entrar");
        add(btnLogin, "growx");

        btnLogin.addActionListener(e -> login());
    }

    private void login() {

        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Preencha todos os campos.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean success = controller.login(username, password);

        if (success) {
            JOptionPane.showMessageDialog(this, "Login bem-sucedido!");

            // Abrir Dashboard (vamos criar depois)
            DashboardScreen dash = new DashboardScreen(controller);
            dash.setVisible(true);
            this.dispose();

        } else {
            JOptionPane.showMessageDialog(this, 
                "Usuário ou senha incorretos.\nOu conta bloqueada.", 
                "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
