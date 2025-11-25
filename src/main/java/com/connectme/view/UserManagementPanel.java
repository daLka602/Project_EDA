package com.connectme.view;

import com.connectme.controller.AdminController;
import com.connectme.model.entities.User;
import com.connectme.view.componet.NavButton;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class UserManagementPanel extends JPanel {
    private AdminController adminController;
    private JPanel cardsPanel;
    private JLabel countLabel;

    public UserManagementPanel() {
        this.adminController = new AdminController();
        setLayout(new MigLayout("fill, insets 25", "[fill]", "[]20[fill]"));
        setBackground(new Color(248, 249, 252));
        initComponents();
    }

    private void initComponents() {
        // Header
        JPanel headerPanel = new JPanel(new MigLayout("fill", "[fill]push[150]", "[]"));
        headerPanel.setBackground(new Color(248, 249, 252));

        JPanel titlePanel = new JPanel(new MigLayout("wrap, insets 0", "[fill]", "[]2[]"));
        titlePanel.setBackground(new Color(248, 249, 252));

        JLabel titleLabel = new JLabel("Gestão de Usuários");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(new Color(33, 33, 33));
        titlePanel.add(titleLabel, "grow");

        List<User> users = adminController.listAllUsers();
        int userCount = users != null ? users.size() : 0;

        countLabel = new JLabel(userCount + " usuário" + (userCount != 1 ? "s" : "") + " registrado" + (userCount != 1 ? "s" : ""));
        countLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        countLabel.setForeground(new Color(120, 120, 120));
        titlePanel.add(countLabel, "grow");

        headerPanel.add(titlePanel, "grow");

        NavButton addUserBtn = new NavButton("+ Adicionar Usuário", true);
        addUserBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        addUserBtn.setPreferredSize(new Dimension(160, 45));
        addUserBtn.addActionListener(e -> {
            new UserFormDialog((JFrame) SwingUtilities.getWindowAncestor(this), null, adminController, this::refreshUsers).setVisible(true);
        });
        headerPanel.add(addUserBtn);

        add(headerPanel, "grow, wrap");

        // Cards Panel
        cardsPanel = new JPanel(new MigLayout("fill, wrap 1, insets 0", "[700]", "[]5"));
        cardsPanel.setBackground(new Color(248, 249, 252));

        loadUserCards();

        JScrollPane scrollPane = new JScrollPane(cardsPanel);
        scrollPane.setBackground(new Color(248, 249, 252));
        scrollPane.getViewport().setBackground(new Color(248, 249, 252));
        scrollPane.setBorder(null);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        add(scrollPane, "grow");
    }

    private void loadUserCards() {
        cardsPanel.removeAll();

        List<User> users = adminController.listAllUsers();
        int userCount = users != null ? users.size() : 0;
        countLabel.setText(userCount + " usuário" + (userCount != 1 ? "s" : "") + " registrado" + (userCount != 1 ? "s" : ""));

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

        cardsPanel.revalidate();
        cardsPanel.repaint();
    }

    public void refreshUsers() {
        loadUserCards();
    }
}