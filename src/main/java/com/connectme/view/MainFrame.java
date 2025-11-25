package com.connectme.view;

import com.connectme.controller.AuthController;
import com.connectme.model.entities.User;
import com.connectme.model.enums.AccessLevel;
import com.connectme.view.componet.MyButton;
import com.connectme.view.componet.NavButton;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class MainFrame extends JFrame {
    private User currentUser;
    private AuthController authController;
    private CardLayout cardLayout;
    private JPanel contentPanel;
    private ContactPanel contactPanel;
    private AdminPanel adminPanel;
    private NavButton contactsBtn;
    private NavButton adminBtn;

    public MainFrame(User user, AuthController authController) {
        this.currentUser = user;
        this.authController = authController;

        setTitle("ConnectMe - Sistema de Gestão de Contactos");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new MigLayout("fill, insets 0", "[]", "Center"));
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

        mainPanel.add(contentPanel, "grow, wmax 85%, center");

        add(mainPanel);

        showContactPanel();
    }

    private JPanel createNavBar() {
        JPanel navPanel = new JPanel(new MigLayout("fill, insets 12 25", "[]", "[]"));
        navPanel.setBackground(Color.WHITE);
        navPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 225)));

        // Logo
        JPanel navInfo = new JPanel(new MigLayout("fill, insets 0 35 5 35", "[]20[]10[]", "[]"));
        navInfo.setBackground(Color.WHITE);

        JLabel title = new JLabel("ConnectMe");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(new Color(5, 17, 27, 245));
        navInfo.add(title);

        // Navigation Buttons
        contactsBtn = new NavButton("Contactos", true);
        contactsBtn.addActionListener(e -> showContactPanel());
        navInfo.add(contactsBtn,"grow");

        if (currentUser.getRole() == AccessLevel.ADMIN || currentUser.getRole() == AccessLevel.MANAGER) {
            adminBtn = new NavButton("Administração", false);
            adminBtn.addActionListener(e -> showAdminPanel());
            navInfo.add(adminBtn, "grow");
        }

        navPanel.add(navInfo);

        // User Info Panel
        JPanel info = new JPanel(new MigLayout("fill, insets 0 20","[]30[]", "[]"));
        info.setBackground(Color.WHITE);

        JPanel userInfoPanel = new JPanel(new MigLayout("wrap, insets 0", "[]", "[]2[]"));
        userInfoPanel.setBackground(Color.WHITE);

        String roleDisplay = getRoleDisplay(currentUser.getRole());
        JLabel roleLabel = new JLabel(roleDisplay);
        roleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        roleLabel.setForeground(new Color(33, 33, 33));
        userInfoPanel.add(roleLabel);

        JLabel userLabel = new JLabel(currentUser.getUsername());
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        userLabel.setForeground(new Color(120, 120, 120));
        userInfoPanel.add(userLabel,"gapleft 5");

        info.add(userInfoPanel);

        // Logout Button
        MyButton logoutBtn = new MyButton();
        logoutBtn.setText("SAIR");
        logoutBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        logoutBtn.setBackground(new Color(244, 67, 54));
        logoutBtn.setPreferredSize(new Dimension(160, 30));
        logoutBtn.addActionListener(e -> handleLogout());
        info.add(logoutBtn, "right");

        navPanel.add(info, "right");

        return navPanel;
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
        contactsBtn.setBackground(isContactsActive ? new Color(46, 17, 233) : new Color(225,220,220));
        contactsBtn.setForeground(isContactsActive ? Color.WHITE : new Color(30, 34, 44, 255));
        contactsBtn.setBorder(isContactsActive ? BorderFactory.createEmptyBorder()
                : BorderFactory.createEmptyBorder(15,20,15,20));

        if (adminBtn != null) {
            adminBtn.setBackground(!isContactsActive ? new Color(46, 17, 233) : new Color(225, 220, 220));
            adminBtn.setForeground(!isContactsActive ? Color.WHITE : new Color(5, 17, 27, 232));
            adminBtn.setBorder(!isContactsActive ? BorderFactory.createEmptyBorder()
                    : BorderFactory.createEmptyBorder(15,20,15,20));
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