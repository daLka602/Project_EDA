package com.connectme.view;

import com.connectme.controller.AdminController;
import com.connectme.model.entities.User;
import com.connectme.model.enums.AccessLevel;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class UserCardPanel extends JPanel {
    private User user;
    private AdminController adminController;
    private Runnable onEditCallback;
    private Runnable onDeleteCallback;

    public UserCardPanel(User user, AdminController controller, Runnable onEdit, Runnable onDelete) {
        this.user = user;
        this.adminController = controller;
        this.onEditCallback = onEdit;
        this.onDeleteCallback = onDelete;

        setLayout(new MigLayout("fill, insets 15 20 15 20", "[]15[fill]push[]5[]", "[]8[]8[]"));
        setBackground(Color.WHITE);
        setBorder(new RoundedBorder(10, new Color(220, 220, 225)));
        setPreferredSize(new Dimension(700, 120));

        initComponents();
    }

    private void initComponents() {
        // Avatar/Icon
        JLabel iconLabel = new JLabel("👤");
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        iconLabel.setForeground(getRoleBadgeColor(user.getRole()));
        add(iconLabel);

        // Nome, Email e Função
        JPanel infoPanel = new JPanel(new MigLayout("wrap, insets 0", "[fill]", "[]5[]5[]"));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setOpaque(false);

        JLabel nameLabel = new JLabel(user.getUsername());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        nameLabel.setForeground(new Color(33, 33, 33));
        infoPanel.add(nameLabel, "grow");

        JLabel emailLabel = new JLabel("📧 " + (user.getEmail() != null ? user.getEmail() : "N/A"));
        emailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        emailLabel.setForeground(new Color(100, 100, 100));
        infoPanel.add(emailLabel, "grow");

        String roleDisplay = getRoleDisplay(user.getRole());
        JLabel roleLabel = new JLabel(roleDisplay);
        roleLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        roleLabel.setBackground(getRoleBadgeColor(user.getRole()));
        roleLabel.setForeground(Color.WHITE);
        roleLabel.setOpaque(true);
        roleLabel.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));
        infoPanel.add(roleLabel, "grow");

        add(infoPanel, "grow, wrap");

        // Botões de ação (direita)
        JPanel actionsPanel = new JPanel(new MigLayout("insets 0", "[]5[]", "[]"));
        actionsPanel.setBackground(Color.WHITE);
        actionsPanel.setOpaque(false);

        JButton editBtn = new JButton("✏️ Editar");
        editBtn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        editBtn.setBackground(new Color(245, 245, 245));
        editBtn.setForeground(new Color(33, 150, 243));
        editBtn.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 225), 1));
        editBtn.setFocusPainted(false);
        editBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        editBtn.setPreferredSize(new Dimension(90, 38));
        editBtn.addActionListener(e -> {
            if (onEditCallback != null) onEditCallback.run();
        });
        actionsPanel.add(editBtn);

        JButton deleteBtn = new JButton("🗑️ Deletar");
        deleteBtn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        deleteBtn.setBackground(new Color(245, 245, 245));
        deleteBtn.setForeground(new Color(244, 67, 54));
        deleteBtn.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 225), 1));
        deleteBtn.setFocusPainted(false);
        deleteBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        deleteBtn.setPreferredSize(new Dimension(90, 38));
        deleteBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Tem certeza que deseja deletar '" + user.getUsername() + "'?",
                    "Confirmar Eliminação",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm == JOptionPane.YES_OPTION) {
                if (adminController.deleteUser(user.getId())) {
                    JOptionPane.showMessageDialog(this, "Usuário deletado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    if (onDeleteCallback != null) onDeleteCallback.run();
                } else {
                    JOptionPane.showMessageDialog(this, "Erro ao deletar usuário!", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        actionsPanel.add(deleteBtn);

        add(actionsPanel, "wrap");
    }

    private String getRoleDisplay(AccessLevel role) {
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

    private Color getRoleBadgeColor(AccessLevel role) {
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

    public User getUser() {
        return user;
    }
}