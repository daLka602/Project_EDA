package com.connectme.view;

import com.connectme.model.eda.GenericArrayList;
import com.connectme.model.eda.GenericLinkedList;
import com.connectme.model.eda.LinearSearch;
import com.connectme.model.util.StringUtils;
import com.connectme.model.entities.Contact;
import com.connectme.model.entities.User;
import com.connectme.view.componet.NavButton;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class AdminPanel extends JPanel {
    private User currentUser;
    private NavButton statsBtn;
    private NavButton usersBtn;
    private CardLayout cardLayout;
    private JPanel contentPanel;
    private StatisticsPanel statisticsPanel;
    private UserManagementPanel userManagementPanel;

    public AdminPanel(User user) {
        this.currentUser = user;
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

        // Inicializar os painéis
        statisticsPanel = new StatisticsPanel();
        userManagementPanel = new UserManagementPanel();

        contentPanel.add(statisticsPanel, "STATS");
        contentPanel.add(userManagementPanel, "USERS");

        add(contentPanel, "grow");

        // Mostrar estatísticas por padrão
        showStatistics();
    }

    private void showStatistics() {
        cardLayout.show(contentPanel, "STATS");
        updateTabButtons(true);
        statisticsPanel.refreshStatistics();
    }

    private void showUsers() {
        cardLayout.show(contentPanel, "USERS");
        updateTabButtons(false);
        userManagementPanel.refreshUsers();
    }

    private void updateTabButtons(boolean isStatsActive) {
        // Botão de Estatísticas
        statsBtn.setBackground(isStatsActive ? new Color(73, 80, 243) : new Color(225, 220, 220));
        statsBtn.setForeground(isStatsActive ? Color.WHITE : new Color(30, 34, 44, 255));

        // SEMPRE aplicar o mesmo border/padding para ambos os botões
        if (isStatsActive) {
            statsBtn.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
            usersBtn.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        } else {
            statsBtn.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
            usersBtn.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        }

        // Botão de Gestão de Usuários
        usersBtn.setBackground(!isStatsActive ? new Color(73, 80, 243) : new Color(225, 220, 220));
        usersBtn.setForeground(!isStatsActive ? Color.WHITE : new Color(30, 34, 44, 255));

        // Garantir que ambos os botões tenham o mesmo tamanho preferido
        statsBtn.setPreferredSize(new Dimension(180, 46));
        usersBtn.setPreferredSize(new Dimension(210, 46));
    }

    /**
     * Busca por telefone (parcial ou exata)
     */
    /**
     * Busca por telefone (parcial ou exata)
     */
    /**
     * Busca por telefone usando LinearSearch genérico
     */
    public static GenericArrayList<Contact> searchByPhone(GenericLinkedList<Contact> list, String phoneQuery) {
        GenericArrayList<Contact> results = new GenericArrayList<>();

        if (list == null || list.isEmpty() || phoneQuery == null || phoneQuery.trim().isEmpty()) {
            return results;
        }

        String cleanQuery = StringUtils.cleanPhoneNumber(phoneQuery);

        // Usar LinearSearch genérico
        GenericLinkedList<Contact> found = LinearSearch.searchAll(list, contact -> {
            if (contact == null || contact.getPhone() == null) return false;
            String cleanPhone = StringUtils.cleanPhoneNumber(contact.getPhone());
            return cleanPhone.contains(cleanQuery);
        });

        // Converter para ArrayList
        GenericLinkedList.Iterator<Contact> it = found.iterator();
        while (it.hasNext()) {
            Contact c = it.next();
            if (c != null) {
                results.add(c);
            }
        }

        return results;
    }

    public void refreshAll() {
        statisticsPanel.refreshStatistics();
        userManagementPanel.refreshUsers();
    }
}