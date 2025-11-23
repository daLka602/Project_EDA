package com.connectme.view;

import javax.swing.*;

import com.connectme.view.componet.*;
import net.miginfocom.swing.MigLayout;
import com.connectme.controller.UserController;

import java.awt.*;

public class LoginScreen extends JFrame {

    private MyTextField txtUsername;
    private MyPasswordField txtPassword;
    private MyButton btnLogin;
    private JLabel lblUsernameError, lblPasswordError;

    private UserController controller;

    public LoginScreen() {
        super("ConnectMe - Login");
        controller = new UserController();

        setLayout(new MigLayout("wrap 1", "[400]", "[]20[]10[]10[]20[]"));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 420);
        setLocationRelativeTo(null);
        setResizable(false);

        initComponents();
        setupValidation();
    }

    private void initComponents() {
        JLabel lblTitle = new JLabel("ConnectMe - Acesso");
        lblTitle.setFont(lblTitle.getFont().deriveFont(20f));
        add(lblTitle, "align center, gapbottom 20");

        // Campo username com validação
        txtUsername = new MyTextField();
        txtUsername.setBorder(BorderFactory.createTitledBorder("Usuário"));
        add(txtUsername, "growx");
        
        lblUsernameError = createErrorLabel();
        add(lblUsernameError, "growx");

        // Campo password com validação
        txtPassword = new MyPasswordField();
        txtPassword.setBorder(BorderFactory.createTitledBorder("Senha"));
        add(txtPassword, "growx");
        
        lblPasswordError = createErrorLabel();
        add(lblPasswordError, "growx");

        // Botões
        btnLogin = new MyButton();
        btnLogin.setText("Entrar");
        btnLogin.setBackground(new Color(35, 60, 121));
        btnLogin.setForeground(new Color(250, 250, 250));
        
        JPanel buttonPanel = new JPanel(new MigLayout("insets 0", "[grow][grow]"));
        buttonPanel.add(btnLogin, "growx");
        
        add(buttonPanel, "w 40%, h 40");

        // Ações
        btnLogin.addActionListener(e -> login());
        
        // Enter para login
        getRootPane().setDefaultButton(btnLogin);
    }

    private JLabel createErrorLabel() {
        JLabel label = new JLabel();
        label.setForeground(java.awt.Color.RED);
        label.setFont(label.getFont().deriveFont(10f));
        return label;
    }

    private void setupValidation() {
        // Validação em tempo real do username
        txtUsername.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { validateUsername(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { validateUsername(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { validateUsername(); }
        });

        // Validação em tempo real da senha
        txtPassword.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { validatePassword(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { validatePassword(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { validatePassword(); }
        });
    }

    private void validateUsername() {
        String username = txtUsername.getText().trim();
        if (username.isEmpty()) {
            lblUsernameError.setText("Usuário é obrigatório");
        } else if (username.length() < 3) {
            lblUsernameError.setText("Mínimo 3 caracteres");
        } else if (!username.matches("^[a-zA-Z0-9_]+$")) {
            lblUsernameError.setText("Apenas letras, números e _");
        } else {
            lblUsernameError.setText("");
        }
    }

    private void validatePassword() {
        String password = new String(txtPassword.getPassword());
        if (password.isEmpty()) {
            lblPasswordError.setText("Senha é obrigatória");
        } else if (password.length() < 4) {
            lblPasswordError.setText("Mínimo 4 caracteres");
        } else {
            lblPasswordError.setText("");
        }
    }

    private boolean isFormValid() {
        validateUsername();
        validatePassword();
        return lblUsernameError.getText().isEmpty() && lblPasswordError.getText().isEmpty();
    }

    private void login() {
        if (!isFormValid()) {
            JOptionPane.showMessageDialog(this, 
                "Por favor, corrija os erros no formulário.", 
                "Erro de Validação", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        boolean success = controller.login(username, password);

        if (success) {
            HomepageView dash = new HomepageView(controller);
            dash.setVisible(true);
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                "Usuário ou senha incorretos.\nOu conta bloqueada.",
                "Erro", JOptionPane.ERROR_MESSAGE);
                
            // Limpar senha
            txtPassword.setText("");
            txtPassword.requestFocus();
        }
    }
}