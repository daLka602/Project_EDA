package com.connectme.view;

import javax.swing.*;
import net.miginfocom.swing.MigLayout;
import com.connectme.controller.UserController;
import com.connectme.model.service.AuthService;

public class LoginScreen extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin, btnRegister;
    private JLabel lblUsernameError, lblPasswordError;

    private UserController controller;

    public LoginScreen() {
        super("ConnectMe - Login");
        controller = new UserController();

        setLayout(new MigLayout("wrap 1", "[300]", "[]20[]10[]10[]10[]"));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 320);
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
        txtUsername = new JTextField();
        txtUsername.setBorder(BorderFactory.createTitledBorder("Usuário"));
        add(txtUsername, "growx");
        
        lblUsernameError = createErrorLabel();
        add(lblUsernameError, "growx");

        // Campo password com validação
        txtPassword = new JPasswordField();
        txtPassword.setBorder(BorderFactory.createTitledBorder("Senha"));
        add(txtPassword, "growx");
        
        lblPasswordError = createErrorLabel();
        add(lblPasswordError, "growx");

        // Botões
        btnLogin = new JButton("Entrar");
        btnRegister = new JButton("Registrar");
        
        JPanel buttonPanel = new JPanel(new MigLayout("insets 0", "[grow][grow]"));
        buttonPanel.add(btnLogin, "growx");
        buttonPanel.add(btnRegister, "growx");
        
        add(buttonPanel, "growx");

        // Ações
        btnLogin.addActionListener(e -> login());
        btnRegister.addActionListener(e -> register());
        
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
            DashboardScreen dash = new DashboardScreen(controller);
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

    private void register() {
        if (!isFormValid()) {
            JOptionPane.showMessageDialog(this, 
                "Por favor, corrija os erros no formulário.", 
                "Erro de Validação", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        // Verificar força da senha
        AuthService.PasswordStrength strength = controller.getAuthService().checkPasswordStrength(password);
        if (strength == AuthService.PasswordStrength.WEAK) {
            int confirm = JOptionPane.showConfirmDialog(this,
                "Esta senha é considerada fraca. Deseja continuar?",
                "Senha Fraca",
                JOptionPane.YES_NO_OPTION);
                
            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }
        }

        boolean success = controller.getAuthService().register(username, password);

        if (success) {
            JOptionPane.showMessageDialog(this,
                "Registro realizado com sucesso!\nAgora faça login.",
                "Sucesso",
                JOptionPane.INFORMATION_MESSAGE);
                
            // Limpar campos
            txtUsername.setText("");
            txtPassword.setText("");
            txtUsername.requestFocus();
        } else {
            JOptionPane.showMessageDialog(this,
                "Erro ao registrar. Usuário já existe ou dados inválidos.",
                "Erro",
                JOptionPane.ERROR_MESSAGE);
        }
    }
}