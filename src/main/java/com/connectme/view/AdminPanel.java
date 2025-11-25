package com.connectme.view;

import com.connectme.controller.AdminController;
import com.connectme.model.entities.User;
import com.connectme.view.componet.NavButton;
import com.connectme.view.componet.RoundedBorder;
import com.connectme.view.componet.RoundedFormBorder;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class AdminPanel extends JPanel {
    private User currentUser;
    private AdminController adminController;
    private NavButton statsBtn;
    private NavButton usersBtn;
    private CardLayout cardLayout;
    private JPanel contentPanel;

    public AdminPanel(User user) {
        this.currentUser = user;
        this.adminController = new AdminController();

        setLayout(new MigLayout("fill, insets 0", "[fill]", "[]"));
        setBackground(new Color(248, 249, 252));

        initComponents();
    }

    private void initComponents() {
        // Header
        JPanel headerPanel = new JPanel(new MigLayout("fill, insets 20 25 0 15", "[]push", "[]"));
        headerPanel.setBackground(new Color(248, 249, 252));

        JPanel titlePanel = new JPanel(new MigLayout("wrap, insets 0", "[fill]", "[]10[]"));
        titlePanel.setBackground(new Color(248, 249, 252));

        JLabel titleLabel = new JLabel("Painel Administrativo");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(33, 33, 33));
        titlePanel.add(titleLabel, "grow");

        JLabel subtitleLabel = new JLabel("Gerencie usuários e visualize estatísticas do sistema");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(120, 120, 120));
        titlePanel.add(subtitleLabel, "grow");

        headerPanel.add(titlePanel, "grow");

        add(headerPanel, "grow, wrap");

        // Tabs Panel
        JPanel tabsPanel = new JPanel(new MigLayout("fill, insets 0 25 0 25", "[]20[]push", "[]"));
        tabsPanel.setBackground(new Color(248, 249, 252));

        statsBtn = new NavButton("📊 Estatísticas", true);
        statsBtn.addActionListener(e -> showStatistics());
        tabsPanel.add(statsBtn);

        usersBtn = new NavButton("👥 Gestão de Usuários", false);
        usersBtn.setPreferredSize(new Dimension(210, 46));
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

    private void showStatistics() {
        cardLayout.show(contentPanel, "STATS");
        updateTabButtons(true);
    }

    private void showUsers() {
        cardLayout.show(contentPanel, "USERS");
        updateTabButtons(false);
    }

    private void updateTabButtons(boolean isStatsActive) {
        statsBtn.setBackground(isStatsActive ? new Color(73, 80, 243) : new Color(225,220,220));
        statsBtn.setForeground(isStatsActive ? Color.WHITE : new Color(30, 34, 44, 255));
        statsBtn.setBorder(isStatsActive ? BorderFactory.createEmptyBorder()
                : BorderFactory.createEmptyBorder(15,20,15,20));

        usersBtn.setBackground(!isStatsActive ? new Color(73, 80, 243) : new Color(225,220,220));
        usersBtn.setForeground(!isStatsActive ? Color.WHITE : new Color(30, 34, 44, 255));
        usersBtn.setBorder(isStatsActive ? BorderFactory.createEmptyBorder()
                : BorderFactory.createEmptyBorder(15,20,15,30));
    }

    private JPanel createStatisticsPanel() {
        JPanel panel = new JPanel(new MigLayout("fill, insets 25", "[fill]", "[]30[fill]"));
        panel.setBackground(new Color(248, 249, 252));

        AdminController.SystemStats stats = adminController.getSystemStats();

        // Cards de estatísticas - 4 colunas
        JPanel statsCardsPanel = new JPanel(new MigLayout("", "[25%]15[25%]15[25%]15[25%]", "[]"));
        statsCardsPanel.setBackground(new Color(248, 249, 252));

        statsCardsPanel.add(createStatCard("👥", "Total de Usuários", String.valueOf(stats.totalUsers),Color.BLUE), "grow");
        statsCardsPanel.add(createStatCard("✓", "Usuários Ativos", String.valueOf(stats.activeUsers),new Color(76, 175, 80)), "grow");
        statsCardsPanel.add(createStatCard("📋", "Total de Contactos", String.valueOf(stats.totalContacts),new Color(156, 39, 172)), "grow");
        statsCardsPanel.add(createStatCard("📈", "Taxa de Ativação", String.format("%.1f%%", stats.activationRate),new Color(255, 152, 0)), "grow");

        panel.add(statsCardsPanel, "grow, wrap");

        // Distribuição de Contactos
        JPanel distPanel = new JPanel(new MigLayout("fill, insets 25", "[fill]", "[]20[fill]"));
        distPanel.setBackground(Color.WHITE);
        distPanel.setBorder(new RoundedBorder(20, new Color(220, 220, 225)));

        JLabel distLabel = new JLabel("Distribuição de Contactos por Tipo");
        distLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
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

    private JPanel createStatCard(String icon, String label, String value, Color color) {
        JPanel card = new JPanel(new MigLayout(" wrap, insets 20 35 20 35", "[center]", "[]10[]"));
        card.setBackground(Color.WHITE);
        card.setBorder(new RoundedBorder(20, new Color(220, 220, 225)));

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI", Font.BOLD, 30));
        iconLabel.setHorizontalAlignment(JLabel.CENTER);
        iconLabel.setForeground(color);
        card.add(iconLabel, "grow");

        JLabel labelLabel = new JLabel(label);
        labelLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        labelLabel.setForeground(new Color(30, 34, 44));
        card.add(labelLabel, "grow");

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        valueLabel.setHorizontalAlignment(JLabel.CENTER);
        valueLabel.setForeground(color);
        card.add(valueLabel, "grow");

        return card;
    }

    private JPanel createDistributionItemTop(String label, int value, Color color) {
        JPanel panel = new JPanel(new MigLayout("wrap, insets 15 30 15", "[center]", "[]8[]"));
        panel.setBackground(new Color(240, 248, 255));
        panel.setBorder(new RoundedBorder(20, new Color(220, 220, 225)));

        JLabel iconLabel = new JLabel(label.split(" ")[0]);
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 24));
        iconLabel.setForeground(color);
        iconLabel.setHorizontalAlignment(JLabel.CENTER);
        panel.add(iconLabel, "grow");

        JLabel textLabel = new JLabel(label.split(" ")[1]);
        textLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
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
        bar.setBorder(new RoundedBorder(10, new Color(220, 220, 225)));
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
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(new Color(33, 33, 33));
        titlePanel.add(titleLabel, "grow");

        java.util.List<User> users = adminController.listAllUsers();
        int userCount = users != null ? users.size() : 0;

        JLabel countLabel = new JLabel(userCount + " usuário" + (userCount != 1 ? "s" : "") + " registrado" + (userCount != 1 ? "s" : ""));
        countLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        countLabel.setForeground(new Color(120, 120, 120));
        titlePanel.add(countLabel, "grow");

        headerPanel.add(titlePanel, "grow");

        NavButton addUserBtn = new NavButton("+ Adicionar Usuário",true);
        addUserBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        addUserBtn.setPreferredSize(new Dimension(160, 45));
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
}