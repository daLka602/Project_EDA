package com.connectme.view;

import com.connectme.controller.AdminController;
import com.connectme.model.entities.User;
import com.connectme.model.enums.AccessLevel;
import com.connectme.model.enums.UserStatus;
import com.connectme.view.componet.MyButton;
import com.connectme.view.componet.MyPasswordField;
import com.connectme.view.componet.MyTextField;
import com.connectme.view.componet.RoundedFormBorder;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;

public class UserFormDialog extends JDialog {
    private User user;
    private AdminController adminController;
    private Runnable onSaveCallback;

    private JTextField nomeField;
    private MyTextField emailField;
    private MyPasswordField passwordField;
    private JComboBox<String> funcaoCombo;
    private JCheckBox contaAtivaCheck;

    public UserFormDialog(JFrame parent, User user, AdminController controller, Runnable onSave) {
        super(parent, user != null ? "Editar Usuário" : "Adicionar Usuário", DEFAULT_MODALITY_TYPE);

        this.user = user;
        this.adminController = controller;
        this.onSaveCallback = onSave;

        setSize(800, 620);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(Color.WHITE);

        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new MigLayout("fill, insets 25 30 0 30", "[fill]", ""));
        mainPanel.setBackground(Color.WHITE);

        // Header
        mainPanel.add(createHeader(), "grow, wrap 20");

        // Separator
        JSeparator separator = new JSeparator();
        separator.setForeground(new Color(220, 220, 225));
        mainPanel.add(separator, "grow, wrap 20");

        // Form Fields
        mainPanel.add(createFormFields(), "grow, wrap 25");

        // Separator
        JSeparator separator2 = new JSeparator();
        separator2.setForeground(new Color(220, 220, 225));
        mainPanel.add(separator2, "grow, wrap 15");

        // Buttons
        mainPanel.add(createButtonPanel(), "grow, wrap 20");

        // Info Box
        mainPanel.add(createInfoBox(), "grow");

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(Color.WHITE);

        add(scrollPane);
    }

    private JPanel createHeader() {
        JPanel headerPanel = new JPanel(new MigLayout("insets 0", "[]push[]", "[]"));
        headerPanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel(user != null ? "Editar Usuário" : "Adicionar Usuário");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(33, 33, 33));
        headerPanel.add(titleLabel);

        JButton closeBtn = new JButton("✕");
        closeBtn.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        closeBtn.setBackground(Color.WHITE);
        closeBtn.setForeground(new Color(120, 120, 120));
        closeBtn.setBorder(BorderFactory.createEmptyBorder());
        closeBtn.setFocusPainted(false);
        closeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeBtn.setPreferredSize(new Dimension(30, 30));
        closeBtn.addActionListener(e -> dispose());
        headerPanel.add(closeBtn);

        return headerPanel;
    }

    private JPanel createFormFields() {
        JPanel formPanel = new JPanel(new MigLayout("fill", "[fill]", "[]10[]10[]10[]10[]10[]10[]"));
        formPanel.setBackground(Color.WHITE);

        // Nome Completo
        formPanel.add(new JLabel("Nome Completo *"));
        nomeField = new JTextField();
        nomeField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        nomeField.setText("Ex: João Silva");
        nomeField.setBorder(new RoundedFormBorder(6, new Color(220, 220, 225)));
        nomeField.setMargin(new Insets(8, 12, 8, 12));
        nomeField.setPreferredSize(new Dimension(200, 36));
        if (user != null) nomeField.setText(user.getUsername());
        formPanel.add(nomeField, "grow, wrap");

        // Email
        formPanel.add(new JLabel("Email *"));
        emailField = new MyTextField();
        emailField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        emailField.setText("exemplo@connectme.mz");
        emailField.setBorder(new RoundedFormBorder(6, new Color(220, 220, 225)));
        emailField.setMargin(new Insets(8, 12, 8, 12));
        emailField.setPreferredSize(new Dimension(200, 36));
        if (user != null && user.getEmail() != null) emailField.setText(user.getEmail());
        formPanel.add(emailField, "grow, wrap");

        // Senha
        formPanel.add(new JLabel("Senha *"));
        passwordField = new MyPasswordField();
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        passwordField.setBorder(new RoundedFormBorder(6, new Color(220, 220, 225)));
        passwordField.setMargin(new Insets(8, 12, 8, 12));
        passwordField.setPreferredSize(new Dimension(200, 36));
        formPanel.add(passwordField, "grow, wrap");

        // Função
        formPanel.add(new JLabel("Função *"));
        funcaoCombo = new JComboBox<>(new String[]{"Usuário", "Gerente", "Administrador"});
        funcaoCombo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        funcaoCombo.setBackground(Color.WHITE);
        funcaoCombo.setBorder(new RoundedFormBorder(6, new Color(220, 220, 225)));
        funcaoCombo.setPreferredSize(new Dimension(200, 36));
        if (user != null) {
            switch (user.getRole().name()) {
                case "ADMIN": funcaoCombo.setSelectedIndex(2); break;
                case "MANAGER": funcaoCombo.setSelectedIndex(1); break;
                default: funcaoCombo.setSelectedIndex(0);
            }
        }
        formPanel.add(funcaoCombo, "grow, wrap");

        // Helper text
        JLabel helperLabel = new JLabel("Usuários podem apenas gerenciar contactos");
        helperLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        helperLabel.setForeground(new Color(150, 150, 150));
        formPanel.add(helperLabel, "grow, wrap 15");

        // Conta Ativa
        contaAtivaCheck = new JCheckBox("Conta ativa");
        contaAtivaCheck.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        contaAtivaCheck.setBackground(Color.WHITE);
        contaAtivaCheck.setSelected(user == null || user.getStatus().name().equals("ATIVE"));
        formPanel.add(contaAtivaCheck, "wrap");

        return formPanel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new MigLayout("fill", "[fill]20[fill]", "[]"));
        buttonPanel.setBackground(Color.WHITE);

        MyButton cancelBtn = new MyButton();
        cancelBtn.setText("Cancelar");
        cancelBtn.setBackground(new Color(245, 245, 245));
        cancelBtn.setForeground(new Color(100, 100, 100));
        cancelBtn.setBorder(new RoundedFormBorder(6, new Color(220, 220, 225)));
        cancelBtn.addActionListener(e -> dispose());
        buttonPanel.add(cancelBtn, "grow");

        MyButton saveBtn = new MyButton();
        if (user != null) {
            saveBtn.setText("Atualizar");
        } else {
            saveBtn.setText("Adicionar Usuário");
        }
        saveBtn.setBackground(new Color(33, 150, 243));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.addActionListener(e -> saveUser());
        buttonPanel.add(saveBtn, "grow");

        return buttonPanel;
    }

    private JPanel createInfoBox() {
        JPanel infoBox = new JPanel(new MigLayout("fill, insets 12 15 12 15", "[fill]", "[]"));
        infoBox.setBackground(new Color(255, 248, 225));
        infoBox.setBorder(BorderFactory.createLineBorder(new Color(255, 193, 7), 1));

        JLabel infoLabel = new JLabel("<html><b>Importante:</b> Os dados são armazenados localmente no banco de dados.</html>");
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        infoLabel.setForeground(new Color(240, 124, 0));
        infoBox.add(infoLabel, "grow");

        return infoBox;
    }

    private void saveUser() {
        String username = nomeField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String role = funcaoCombo.getSelectedItem().toString();
        boolean isActive = contaAtivaCheck.isSelected();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Preencha todos os campos obrigatórios", "Validação", JOptionPane.WARNING_MESSAGE);
            return;
        }

        User newUser = user != null ? user : new User();
        newUser.setUsername(username);
        newUser.setEmail(email);
        newUser.setPasswordHash(password);

        switch (role) {
            case "Administrador": newUser.setRole(AccessLevel.ADMIN); break;
            case "Gerente": newUser.setRole(AccessLevel.MANAGER); break;
            default: newUser.setRole(AccessLevel.STAFF);
        }

        newUser.setStatus(isActive ? UserStatus.ATIVE : UserStatus.BLOCKED);

        boolean success;
        if (user == null) {
            newUser.setCreateDate(LocalDateTime.now());
            success = adminController.createUser(newUser);
        } else {
            success = adminController.updateUser(newUser);
        }

        if (success) {
            JOptionPane.showMessageDialog(this, user == null ? "Usuário adicionado com sucesso!" : "Usuário atualizado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            if (onSaveCallback != null) onSaveCallback.run();
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Erro ao salvar usuário!", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}