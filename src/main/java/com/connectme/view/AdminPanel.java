package com.connectme.view;

import com.connectme.controller.AdminController;
import com.connectme.model.entities.User;
import com.connectme.view.componet.RoundedFormBorder;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class AdminPanel extends JPanel {
    private User currentUser;
    private AdminController adminController;
    private JButton statsBtn;
    private JButton usersBtn;
    private CardLayout cardLayout;
    private JPanel contentPanel;

    public AdminPanel(User user) {
        this.currentUser = user;
        this.adminController = new AdminController();

        setLayout(new MigLayout("fill, insets 0", "[fill]", "[]0[fill]"));
        setBackground(new Color(248, 249, 252));

        initComponents();
    }

    private void initComponents() {
        // Header
        JPanel headerPanel = new JPanel(new MigLayout("fill, insets 20 25 15 25", "[]push", "[]"));
        headerPanel.setBackground(new Color(248, 249, 252));

        JPanel titlePanel = new JPanel(new MigLayout("wrap, insets 0", "[fill]", "[]3[]"));
        titlePanel.setBackground(new Color(248, 249, 252));

        JLabel titleLabel = new JLabel("Painel Administrativo");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(33, 33, 33));
        titlePanel.add(titleLabel, "grow");

        JLabel subtitleLabel = new JLabel("Gerencie usuários e visualize estatísticas do sistema");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitleLabel.setForeground(new Color(120, 120, 120));
        titlePanel.add(subtitleLabel, "grow");

        headerPanel.add(titlePanel, "grow");

        add(headerPanel, "grow, wrap");

        // Tabs Panel
        JPanel tabsPanel = new JPanel(new MigLayout("fill, insets 12 25 12 25", "[140]10[140]push", "[]"));
        tabsPanel.setBackground(Color.WHITE);
        tabsPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 225)));

        statsBtn = createTabButton("📊 Estatísticas", true);
        statsBtn.addActionListener(e -> showStatistics());
        tabsPanel.add(statsBtn);

        usersBtn = createTabButton("👥 Gestão de Usuários", false);
        usersBtn.addActionListener(e -> showUsers());
        tabsPanel.add(usersBtn);

        add(tabsPanel, "grow, wrap");

        // Content Panel com CardLayout
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(new Color(248, 249, 252));

        contentPanel.add(createStatisticsPanel(), "STATS");
        contentPanel.add(createUsersPanel(), "USERS");

        add(contentPanel, "grow");
    }

    private JButton createTabButton(String text, boolean isActive) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn.setPreferredSize(new Dimension(140, 36));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        if (isActive) {
            btn.setBackground(new Color(33, 150, 243));
            btn.setForeground(Color.WHITE);
            btn.setBorder(BorderFactory.createEmptyBorder());
        } else {
            btn.setBackground(Color.WHITE);
            btn.setForeground(new Color(100, 100, 100));
            btn.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 205), 1));
        }

        return btn;
    }

    private void showStatistics() {
        cardLayout.show(contentPanel, "STATS");
        updateTabButtons(true);
    }

    private void showUsers() {
        cardLayout.show(contentPanel, "USERS");
        updateTabButtons(false);
    }

    private void updateTabButtons(boolean isStatsActive) {
        statsBtn.setBackground(isStatsActive ? new Color(33, 150, 243) : Color.WHITE);
        statsBtn.setForeground(isStatsActive ? Color.WHITE : new Color(100, 100, 100));
        statsBtn.setBorder(isStatsActive ? BorderFactory.createEmptyBorder()
                : BorderFactory.createLineBorder(new Color(200, 200, 205), 1));

        usersBtn.setBackground(!isStatsActive ? new Color(33, 150, 243) : Color.WHITE);
        usersBtn.setForeground(!isStatsActive ? Color.WHITE : new Color(100, 100, 100));
        usersBtn.setBorder(!isStatsActive ? BorderFactory.createEmptyBorder()
                : BorderFactory.createLineBorder(new Color(200, 200, 205), 1));
    }

    private JPanel createStatisticsPanel() {
        JPanel panel = new JPanel(new MigLayout("fill, insets 25", "[fill]", "[]30[fill]"));
        panel.setBackground(new Color(248, 249, 252));

        AdminController.SystemStats stats = adminController.getSystemStats();

        // Cards de estatísticas - 4 colunas
        JPanel statsCardsPanel = new JPanel(new MigLayout("", "[25%]15[25%]15[25%]15[25%]", "[]"));
        statsCardsPanel.setBackground(new Color(248, 249, 252));

        statsCardsPanel.add(createStatCard("👥", "Total de Usuários", String.valueOf(stats.totalUsers)), "grow");
        statsCardsPanel.add(createStatCard("✓", "Usuários Ativos", String.valueOf(stats.activeUsers)), "grow");
        statsCardsPanel.add(createStatCard("📋", "Total de Contactos", String.valueOf(stats.totalContacts)), "grow");
        statsCardsPanel.add(createStatCard("📈", "Taxa de Ativação", String.format("%.1f%%", stats.activationRate)), "grow");

        panel.add(statsCardsPanel, "grow, wrap");

        // Distribuição de Contactos
        JPanel distPanel = new JPanel(new MigLayout("fill, insets 25", "[fill]", "[]20[fill]"));
        distPanel.setBackground(Color.WHITE);
        distPanel.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 225), 1));

        JLabel distLabel = new JLabel("Distribuição de Contactos por Tipo");
        distLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        distLabel.setForeground(new Color(33, 33, 33));
        distPanel.add(distLabel, "grow, wrap");

        // Items de distribuição - 3 em linha
        JPanel topItemsPanel = new JPanel(new MigLayout("fill, insets 0", "[33%]15[33%]15[33%]", "[]"));
        topItemsPanel.setBackground(Color.WHITE);

        topItemsPanel.add(createDistributionItemTop("👤 Clientes", stats.customers, new Color(76, 175, 80)), "grow");
        topItemsPanel.add(createDistributionItemTop("🤝 Parceiros", stats.partners, new Color(255, 152, 0)), "grow");
        topItemsPanel.add(createDistributionItemTop("📦 Fornecedores", stats.suppliers, new Color(156, 39, 172)), "grow");

        distPanel.add(topItemsPanel, "grow, wrap");

        // Barras de progresso em coluna
        JPanel barsPanel = new JPanel(new MigLayout("fill, insets 0", "[fill]15[30]", "[]15[]15[]"));
        barsPanel.setBackground(Color.WHITE);

        JLabel clientesLabel = new JLabel("Clientes");
        clientesLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        clientesLabel.setForeground(new Color(60, 60, 60));
        barsPanel.add(clientesLabel);

        JLabel clientesNum = new JLabel(String.valueOf(stats.customers));
        clientesNum.setFont(new Font("Segoe UI", Font.BOLD, 12));
        clientesNum.setForeground(new Color(33, 33, 33));
        barsPanel.add(clientesNum, "wrap");

        barsPanel.add(createDistributionBar(stats.customers, stats.totalContacts, new Color(33, 150, 243)), "grow");
        barsPanel.add(new JLabel(), "wrap");

        JLabel parceirosLabel = new JLabel("Parceiros");
        parceirosLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        parceirosLabel.setForeground(new Color(60, 60, 60));
        barsPanel.add(parceirosLabel);

        JLabel parceirosNum = new JLabel(String.valueOf(stats.partners));
        parceirosNum.setFont(new Font("Segoe UI", Font.BOLD, 12));
        parceirosNum.setForeground(new Color(33, 33, 33));
        barsPanel.add(parceirosNum, "wrap");

        barsPanel.add(createDistributionBar(stats.partners, stats.totalContacts, new Color(240, 240, 245)), "grow");
        barsPanel.add(new JLabel(), "wrap");

        JLabel fornecedoresLabel = new JLabel("Fornecedores");
        fornecedoresLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        fornecedoresLabel.setForeground(new Color(60, 60, 60));
        barsPanel.add(fornecedoresLabel);

        JLabel fornecedoresNum = new JLabel(String.valueOf(stats.suppliers));
        fornecedoresNum.setFont(new Font("Segoe UI", Font.BOLD, 12));
        fornecedoresNum.setForeground(new Color(33, 33, 33));
        barsPanel.add(fornecedoresNum, "wrap");

        barsPanel.add(createDistributionBar(stats.suppliers, stats.totalContacts, new Color(156, 39, 172)), "grow");

        distPanel.add(barsPanel, "grow");

        panel.add(distPanel, "grow");

        return panel;
    }

    private JPanel createStatCard(String icon, String label, String value) {
        JPanel card = new JPanel(new MigLayout("fill, wrap, insets 20", "[center]", "[]10[]10[]"));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 225), 1));

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 32));
        iconLabel.setHorizontalAlignment(JLabel.CENTER);
        card.add(iconLabel, "grow");

        JLabel labelLabel = new JLabel(label);
        labelLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        labelLabel.setForeground(new Color(120, 120, 120));
        labelLabel.setHorizontalAlignment(JLabel.CENTER);
        card.add(labelLabel, "grow");

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        valueLabel.setForeground(new Color(33, 33, 33));
        valueLabel.setHorizontalAlignment(JLabel.CENTER);
        card.add(valueLabel, "grow");

        return card;
    }

    private JPanel createDistributionItemTop(String label, int value, Color color) {
        JPanel panel = new JPanel(new MigLayout("wrap, insets 15", "[center]", "[]8[]"));
        panel.setBackground(new Color(240, 248, 255));
        panel.setBorder(BorderFactory.createLineBorder(new Color(220, 235, 255), 1));

        JLabel iconLabel = new JLabel(label.split(" ")[0]);
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 24));
        iconLabel.setForeground(color);
        iconLabel.setHorizontalAlignment(JLabel.CENTER);
        panel.add(iconLabel, "grow");

        JLabel textLabel = new JLabel(label.split(" ")[1]);
        textLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        textLabel.setForeground(new Color(100, 100, 100));
        textLabel.setHorizontalAlignment(JLabel.CENTER);
        panel.add(textLabel, "grow");

        JLabel valueLabel = new JLabel(String.valueOf(value));
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        valueLabel.setForeground(color);
        valueLabel.setHorizontalAlignment(JLabel.CENTER);
        panel.add(valueLabel, "grow");

        return panel;
    }

    private JProgressBar createDistributionBar(int current, int total, Color color) {
        JProgressBar bar = new JProgressBar(0, Math.max(total, 1));
        bar.setValue(current);
        bar.setForeground(color);
        bar.setBackground(new Color(240, 240, 245));
        bar.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 225), 1));
        bar.setPreferredSize(new Dimension(200, 8));
        bar.setStringPainted(false);
        return bar;
    }

    private JPanel createUsersPanel() {
        JPanel panel = new JPanel(new MigLayout("fill, insets 25", "[fill]", "[]20[fill]"));
        panel.setBackground(new Color(248, 249, 252));

        // Header
        JPanel headerPanel = new JPanel(new MigLayout("fill", "[fill]push[150]", "[]"));
        headerPanel.setBackground(new Color(248, 249, 252));

        JPanel titlePanel = new JPanel(new MigLayout("wrap, insets 0", "[fill]", "[]2[]"));
        titlePanel.setBackground(new Color(248, 249, 252));

        JLabel titleLabel = new JLabel("Gestão de Usuários");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(33, 33, 33));
        titlePanel.add(titleLabel, "grow");

        java.util.List<User> users = adminController.listAllUsers();
        int userCount = users != null ? users.size() : 0;

        JLabel countLabel = new JLabel(userCount + " usuário" + (userCount != 1 ? "s" : "") + " registrado" + (userCount != 1 ? "s" : ""));
        countLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        countLabel.setForeground(new Color(120, 120, 120));
        titlePanel.add(countLabel, "grow");

        headerPanel.add(titlePanel, "grow");

        JButton addUserBtn = new JButton("👤 Adicionar Usuário");
        addUserBtn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        addUserBtn.setBackground(new Color(33, 150, 243));
        addUserBtn.setForeground(Color.WHITE);
        addUserBtn.setBorder(BorderFactory.createEmptyBorder());
        addUserBtn.setFocusPainted(false);
        addUserBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addUserBtn.setPreferredSize(new Dimension(150, 32));
        addUserBtn.addActionListener(e -> {
            new UserFormDialog((JFrame) SwingUtilities.getWindowAncestor(this), null, adminController, this::refreshUsers).setVisible(true);
        });
        headerPanel.add(addUserBtn);

        panel.add(headerPanel, "grow, wrap");

        // Cards Panel
        JPanel cardsPanel = new JPanel(new MigLayout("fill, wrap 1, insets 0", "[700]", "[]5"));
        cardsPanel.setBackground(new Color(248, 249, 252));

        if (users == null || users.isEmpty()) {
            JPanel emptyPanel = new JPanel(new MigLayout("center, wrap", "[fill]", "[]20[]"));
            emptyPanel.setBackground(new Color(248, 249, 252));
            emptyPanel.setPreferredSize(new Dimension(700, 300));

            JLabel emptyIcon = new JLabel("👤");
            emptyIcon.setFont(new Font("Segoe UI", Font.PLAIN, 48));
            emptyPanel.add(emptyIcon);

            JLabel emptyLabel = new JLabel("Nenhum usuário registrado");
            emptyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            emptyLabel.setForeground(new Color(120, 120, 120));
            emptyPanel.add(emptyLabel);

            cardsPanel.add(emptyPanel);
        } else {
            for (User user : users) {
                UserCardPanel userCard = new UserCardPanel(
                        user,
                        adminController,
                        () -> new UserFormDialog((JFrame) SwingUtilities.getWindowAncestor(this), user, adminController, this::refreshUsers).setVisible(true),
                        this::refreshUsers
                );
                cardsPanel.add(userCard, BorderLayout.CENTER);
            }
        }

        JScrollPane scrollPane = new JScrollPane(cardsPanel);
        scrollPane.setBackground(new Color(248, 249, 252));
        scrollPane.getViewport().setBackground(new Color(248, 249, 252));
        scrollPane.setBorder(null);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        panel.add(scrollPane, "grow");

        return panel;
    }

    private void refreshUsers() {
        contentPanel.removeAll();
        contentPanel.add(createUsersPanel(), "USERS");
        cardLayout.show(contentPanel, "USERS");
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private String getRoleDisplay(com.connectme.model.enums.AccessLevel role) {
        switch (role) {
            case ADMIN:
                return "Administrador";
            case MANAGER:
                return "Gerente";
            case STAFF:
                return "Atendente";
            default:
                return "Usuário";
        }
    }

    private Color getRoleBadgeColor(com.connectme.model.enums.AccessLevel role) {
        switch (role) {
            case ADMIN:
                return new Color(156, 39, 172);
            case MANAGER:
                return new Color(33, 150, 243);
            case STAFF:
                return new Color(76, 175, 80);
            default:
                return new Color(100, 100, 100);
        }
    }

    private void openUserForm(User user) {
        JDialog dialog = new JDialog();
        dialog.setTitle(user != null ? "Editar Usuário" : "Adicionar Usuário");
        dialog.setSize(550, 500);
        dialog.setLocationRelativeTo(null);
        dialog.setModalityType(JDialog.DEFAULT_MODALITY_TYPE);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.getContentPane().setBackground(Color.WHITE);

        JPanel mainPanel = new JPanel(new MigLayout("fill, insets 25 30 0 30", "[fill]", ""));
        mainPanel.setBackground(Color.WHITE);

        // Header
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
        closeBtn.addActionListener(e -> dialog.dispose());
        headerPanel.add(closeBtn);

        mainPanel.add(headerPanel, "grow, wrap 20");

        // Separator
        JSeparator separator = new JSeparator();
        separator.setForeground(new Color(220, 220, 225));
        mainPanel.add(separator, "grow, wrap 20");

        // Nome Completo
        mainPanel.add(new JLabel("Nome Completo *"));
        JTextField nomeField = new JTextField();
        nomeField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        nomeField.setText("Ex: João Silva");
        nomeField.setBorder(new RoundedFormBorder(6, new Color(220, 220, 225)));
        nomeField.setMargin(new Insets(8, 12, 8, 12));
        nomeField.setPreferredSize(new Dimension(200, 36));
        if (user != null) nomeField.setText(user.getUsername());
        mainPanel.add(nomeField, "grow, wrap 15");

        // Email
        mainPanel.add(new JLabel("Email *"));
        JTextField emailField = new JTextField();
        emailField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        emailField.setText("exemplo@connectme.mz");
        emailField.setBorder(new RoundedFormBorder(6, new Color(220, 220, 225)));
        emailField.setMargin(new Insets(8, 12, 8, 12));
        emailField.setPreferredSize(new Dimension(200, 36));
        if (user != null && user.getEmail() != null) emailField.setText(user.getEmail());
        mainPanel.add(emailField, "grow, wrap 15");

        // Senha
        mainPanel.add(new JLabel("Senha *"));
        JPasswordField passwordField = new JPasswordField();
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        passwordField.setBorder(new RoundedFormBorder(6, new Color(220, 220, 225)));
        passwordField.setMargin(new Insets(8, 12, 8, 12));
        passwordField.setPreferredSize(new Dimension(200, 36));
        mainPanel.add(passwordField, "grow, wrap 15");

        // Função
        mainPanel.add(new JLabel("Função *"));
        JComboBox<String> funcaoCombo = new JComboBox<>(new String[]{"Usuário", "Gerente", "Administrador"});
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
        mainPanel.add(funcaoCombo, "grow, wrap 15");

        // Helper text
        JLabel helperLabel = new JLabel("Usuários podem apenas gerenciar contactos");
        helperLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        helperLabel.setForeground(new Color(150, 150, 150));
        mainPanel.add(helperLabel, "grow, wrap 20");

        // Conta Ativa
        JCheckBox contaAtivaCheck = new JCheckBox("Conta ativa");
        contaAtivaCheck.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        contaAtivaCheck.setBackground(Color.WHITE);
        contaAtivaCheck.setSelected(user == null || user.getStatus().name().equals("ATIVE"));
        mainPanel.add(contaAtivaCheck, "wrap 25");

        // Separator
        JSeparator separator2 = new JSeparator();
        separator2.setForeground(new Color(220, 220, 225));
        mainPanel.add(separator2, "grow, wrap 15");

        // Buttons
        JPanel buttonPanel = new JPanel(new MigLayout("fill", "[fill]20[fill]", "[]"));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JButton cancelBtn = new JButton("Cancelar");
        cancelBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cancelBtn.setBackground(new Color(245, 245, 245));
        cancelBtn.setForeground(new Color(100, 100, 100));
        cancelBtn.setBorder(new RoundedFormBorder(6, new Color(220, 220, 225)));
        cancelBtn.setFocusPainted(false);
        cancelBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cancelBtn.setPreferredSize(new Dimension(120, 40));
        cancelBtn.addActionListener(e -> dialog.dispose());
        buttonPanel.add(cancelBtn, "grow");

        JButton saveBtn = new JButton(user != null ? "Atualizar" : "Adicionar Usuário");
        saveBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        saveBtn.setBackground(new Color(33, 150, 243));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setBorder(BorderFactory.createEmptyBorder());
        saveBtn.setFocusPainted(false);
        saveBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        saveBtn.setPreferredSize(new Dimension(150, 40));
        saveBtn.addActionListener(e -> {
            String username = nomeField.getText().trim();
            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();
            String role = funcaoCombo.getSelectedItem().toString();
            boolean isActive = contaAtivaCheck.isSelected();

            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Preencha todos os campos obrigatórios", "Validação", JOptionPane.WARNING_MESSAGE);
                return;
            }

            User newUser = user != null ? user : new User();
            newUser.setUsername(username);
            newUser.setEmail(email);
            newUser.setPasswordHash(password);

            switch (role) {
                case "Administrador": newUser.setRole(com.connectme.model.enums.AccessLevel.ADMIN); break;
                case "Gerente": newUser.setRole(com.connectme.model.enums.AccessLevel.MANAGER); break;
                default: newUser.setRole(com.connectme.model.enums.AccessLevel.STAFF);
            }

            newUser.setStatus(isActive ? com.connectme.model.enums.UserStatus.ATIVE : com.connectme.model.enums.UserStatus.BLOCKED);

            boolean success;
            if (user == null) {
                newUser.setCreateDate(java.time.LocalDateTime.now());
                success = adminController.createUser(newUser);
            } else {
                success = adminController.updateUser(newUser);
            }

            if (success) {
                JOptionPane.showMessageDialog(dialog, user == null ? "Usuário adicionado com sucesso!" : "Usuário atualizado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                // Recarregar cards
                contentPanel.removeAll();
                contentPanel.add(createUsersPanel(), "USERS");
                cardLayout.show(contentPanel, "USERS");
                contentPanel.revalidate();
                contentPanel.repaint();
            } else {
                JOptionPane.showMessageDialog(dialog, "Erro ao salvar usuário!", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });
        buttonPanel.add(saveBtn, "grow");

        mainPanel.add(buttonPanel, "grow, wrap 20");

        // Info box
        JPanel infoBox = new JPanel(new MigLayout("fill, insets 12 15 12 15", "[fill]", "[]"));
        infoBox.setBackground(new Color(255, 248, 225));
        infoBox.setBorder(BorderFactory.createLineBorder(new Color(255, 193, 7), 1));

        JLabel infoLabel = new JLabel("<html><b>Importante:</b> Os dados são armazenados localmente no navegador. Em produção, implemente autenticação segura com backend.</html>");
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        infoLabel.setForeground(new Color(240, 124, 0));
        infoBox.add(infoLabel, "grow");

        mainPanel.add(infoBox, "grow");

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(Color.WHITE);

        dialog.add(scrollPane);
        dialog.setVisible(true);
    }

    private void deleteUser(User user) {
        int confirm = JOptionPane.showConfirmDialog(
                null,
                "Tem certeza que deseja deletar o usuário '" + user.getUsername() + "'?",
                "Confirmar Eliminação",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            if (adminController.deleteUser(user.getId())) {
                JOptionPane.showMessageDialog(null, "Usuário deletado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                // Recarregar a view
                contentPanel.removeAll();
                contentPanel.add(createUsersPanel(), "USERS");
                cardLayout.show(contentPanel, "USERS");
                contentPanel.revalidate();
                contentPanel.repaint();
            } else {
                JOptionPane.showMessageDialog(null, "Erro ao deletar usuário!", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}