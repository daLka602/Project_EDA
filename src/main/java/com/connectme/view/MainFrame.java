package com.connectme.view;

import com.connectme.controller.AuthController;
import com.connectme.model.entities.User;
import com.connectme.model.enums.AccessLevel;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private User currentUser;
    private AuthController authController;
    private CardLayout cardLayout;
    private JPanel contentPanel;
    private ContactPanel contactPanel;
    private AdminPanel adminPanel;
    private JButton contactsBtn;
    private JButton adminBtn;

    public MainFrame(User user, AuthController authController) {
        this.currentUser = user;
        this.authController = authController;

        setTitle("ConnectMe - Sistema de Gestão de Contactos");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 750);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new MigLayout("fill, insets 0", "[fill]", "[]0[fill]"));
        mainPanel.setBackground(new Color(248, 249, 252));

        mainPanel.add(createNavBar(), "grow, wrap");

        // CardLayout for content switching
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(new Color(248, 249, 252));

        contactPanel = new ContactPanel(currentUser);
        contentPanel.add(contactPanel, "CONTACTS");

        if (currentUser.getRole() == AccessLevel.ADMIN || currentUser.getRole() == AccessLevel.MANAGER) {
            adminPanel = new AdminPanel(currentUser);
            contentPanel.add(adminPanel, "ADMIN");
        }

        mainPanel.add(contentPanel, "grow");

        add(mainPanel);

        showContactPanel();
    }

    private JPanel createNavBar() {
        JPanel navPanel = new JPanel(new MigLayout("fill, insets 12 25", "[]20[]push", "[]"));
        navPanel.setBackground(Color.WHITE);
        navPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 225)));

        // Logo
        JLabel title = new JLabel("ConnectMe");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(new Color(33, 150, 243));
        navPanel.add(title);

        // Navigation Buttons
        contactsBtn = createNavButton("Contactos", true);
        contactsBtn.addActionListener(e -> showContactPanel());
        navPanel.add(contactsBtn);

        if (currentUser.getRole() == AccessLevel.ADMIN || currentUser.getRole() == AccessLevel.MANAGER) {
            adminBtn = createNavButton("Administração", false);
            adminBtn.addActionListener(e -> showAdminPanel());
            navPanel.add(adminBtn);
        }

        // User Info Panel
        JPanel userInfoPanel = new JPanel(new MigLayout("wrap, insets 0", "[fill]", "[]2[]"));
        userInfoPanel.setBackground(Color.WHITE);

        JLabel userLabel = new JLabel(currentUser.getUsername());
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        userLabel.setForeground(new Color(33, 33, 33));
        userInfoPanel.add(userLabel, "align right");

        String roleDisplay = getRoleDisplay(currentUser.getRole());
        JLabel roleLabel = new JLabel(roleDisplay);
        roleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        roleLabel.setForeground(new Color(120, 120, 120));
        userInfoPanel.add(roleLabel, "align right");

        navPanel.add(userInfoPanel, "gap 20");

        // Logout Button
        JButton logoutBtn = new JButton("Sair");
        logoutBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        logoutBtn.setBackground(new Color(244, 67, 54));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFocusPainted(false);
        logoutBtn.setBorder(BorderFactory.createEmptyBorder());
        logoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutBtn.setPreferredSize(new Dimension(80, 36));
        logoutBtn.addActionListener(e -> handleLogout());
        navPanel.add(logoutBtn, "gap 15");

        return navPanel;
    }

    private JButton createNavButton(String text, boolean isActive) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn.setPreferredSize(new Dimension(120, 36));
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

    private String getRoleDisplay(AccessLevel role) {
        switch (role) {
            case ADMIN:
                return "Administrador";
            case MANAGER:
                return "Gerente";
            case STAFF:
                return "Atendente";
            default:
                return "Utilizador";
        }
    }

    private void showContactPanel() {
        cardLayout.show(contentPanel, "CONTACTS");
        updateNavButtons(true);
    }

    private void showAdminPanel() {
        cardLayout.show(contentPanel, "ADMIN");
        updateNavButtons(false);
    }

    private void updateNavButtons(boolean isContactsActive) {
        contactsBtn.setBackground(isContactsActive ? new Color(33, 150, 243) : Color.WHITE);
        contactsBtn.setForeground(isContactsActive ? Color.WHITE : new Color(100, 100, 100));
        contactsBtn.setBorder(isContactsActive ? BorderFactory.createEmptyBorder()
                : BorderFactory.createLineBorder(new Color(200, 200, 205), 1));

        if (adminBtn != null) {
            adminBtn.setBackground(!isContactsActive ? new Color(33, 150, 243) : Color.WHITE);
            adminBtn.setForeground(!isContactsActive ? Color.WHITE : new Color(100, 100, 100));
            adminBtn.setBorder(!isContactsActive ? BorderFactory.createEmptyBorder()
                    : BorderFactory.createLineBorder(new Color(200, 200, 205), 1));
        }
    }

    private void handleLogout() {
        int option = JOptionPane.showConfirmDialog(
                this,
                "Tem certeza que deseja sair?",
                "Confirmar Saída",
                JOptionPane.YES_NO_OPTION
        );

        if (option == JOptionPane.YES_OPTION) {
            authController.logout();
            dispose();
            LoginScreen loginScreen = new LoginScreen();
            loginScreen.setVisible(true);
        }
    }
}