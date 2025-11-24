// src/main/java/com/connectme/view/ContactPanel.java
package com.connectme.view;

import com.connectme.controller.ContactController;
import com.connectme.controller.ExportController;
import com.connectme.model.entities.Contact;
import com.connectme.model.entities.User;
import com.connectme.model.enums.ContactType;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.List;

public class ContactPanel extends JPanel {
    private User currentUser;
    private ContactController contactController;
    private ExportController exportController;
    private JTextField searchField;
    private JPanel cardsPanel;
    private List<Contact> allContacts;
    private JLabel countLabel;
    private JButton currentFilterBtn;

    public ContactPanel(User user) {
        this.currentUser = user;
        this.contactController = new ContactController();
        this.exportController = new ExportController();

        setLayout(new MigLayout("fill, insets 0", "[fill]", "[]0[fill]"));
        setBackground(new Color(248, 249, 252));

        initComponents();
        loadContacts();
    }

    private void initComponents() {
        // Header Panel
        JPanel headerPanel = new JPanel(new MigLayout("fill, insets 60 90 30 80", "[]15[]push[100]10[100]", "[]"));
        headerPanel.setBackground(new Color(248, 249, 252));
        //headerPanel.setMaximumSize(new Dimension(1700, 160));

        JLabel iconLabel = new JLabel("📋");
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 32));
        headerPanel.add(iconLabel);

        JPanel titlePanel = new JPanel(new MigLayout("wrap, insets 0", "[fill]", "[]3[]"));
        titlePanel.setBackground(new Color(248, 249, 252));

        JLabel titleLabel = new JLabel("Agenda Telefónica");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(33, 33, 33));
        titlePanel.add(titleLabel, "grow");

        countLabel = new JLabel("0 contactos guardados");
        countLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        countLabel.setForeground(new Color(120, 120, 120));
        titlePanel.add(countLabel, "grow");

        headerPanel.add(titlePanel, "grow");

        headerPanel.add(new JLabel(), "grow");

        JButton anteriorBtn = createNavButton("Anterior");
        headerPanel.add(anteriorBtn, "w 90!, h 38!");

        JButton depoisBtn = createNavButton("Depois");
        headerPanel.add(depoisBtn, "w 90!, h 38!, gapleft 10");

        JButton exportBtn = createActionButton("Exportar");
        exportBtn.addActionListener(e -> handleExport());
        headerPanel.add(exportBtn, "w 100!, h 38!, gapleft 10");

        JButton importBtn = createActionButton("Importar");
        importBtn.addActionListener(e -> handleImport());
        headerPanel.add(importBtn, "w 100!, h 38!, gapleft 10");

        add(headerPanel, BorderLayout.NORTH);

        // Search and Filter Panel
        JPanel searchPanel = new JPanel(new MigLayout("fill, insets 20 40 20 40", "[][][]0[grow][]", "center"));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(new RoundedBorder(1, new Color(220, 220, 225)));
        searchPanel.setMaximumSize(new Dimension(1500, 160));

        searchField = new JTextField();
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.setText("Pesquisar contactos...");
        searchField.setForeground(new Color(150, 150, 150));
        setupPlaceholder(searchField, " Pesquisar contactos...");
        searchPanel.add(searchField, "w 230!, h 40!");

        JButton searchBtn = createActionButton("Pesquisar");
        searchBtn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchBtn.addActionListener(e -> performSearch());
        searchPanel.add(searchBtn, "w 100!, h 40!, gapleft 10");

        searchPanel.add(new JLabel(), "w 30!");

        String[] filterLabels = {"Todos", "Clientes", "Parceiros", "Fornecedores"};
        ContactType[] filterTypes = {null, ContactType.CUSTOMER, ContactType.PARTNER, ContactType.SUPPLIER};

        for (int i = 0; i < filterLabels.length; i++) {
            final int index = i;
            JButton filterBtn = createFilterButton(filterLabels[i]);
            filterBtn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            if (i == 0) {
                currentFilterBtn = filterBtn;
                filterBtn.setBackground(new Color(33, 150, 243));
                filterBtn.setForeground(Color.WHITE);
            }
            final JButton finalFilterBtn = filterBtn;
            filterBtn.addActionListener(e -> applyFilter(filterTypes[index], finalFilterBtn));
            searchPanel.add(filterBtn, "w 110!, h 40!, gapleft 10");
        }

        // Espaço vazio
        searchPanel.add(new JLabel(), "grow");

        // Add contact button (right)
        JButton addBtn = createActionButton(" + Adicionar");
        addBtn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        addBtn.addActionListener(e -> openContactForm(null));
        searchPanel.add(addBtn, "w 110!, h 40!");

        add(searchPanel, BorderLayout.CENTER);

        // Cards Panel com ScrollPane
        cardsPanel = new JPanel(new MigLayout("fill, wrap 1, insets 0 40 0 40", "[]", "Center"));
        cardsPanel.setBackground(new Color(248, 249, 252));

        JScrollPane scrollPane = new JScrollPane(cardsPanel);
        scrollPane.setBackground(new Color(248, 249, 252));
        scrollPane.getViewport().setBackground(new Color(248, 249, 252));
        scrollPane.setBorder(null);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        add(scrollPane, "grow, SOUTH");
    }

    private void handleImport() {
    }

    private JButton createNavButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setBackground(Color.WHITE);
        btn.setForeground(new Color(100, 100, 100));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 225), 1));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton createActionButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setBackground(new Color(33, 150, 243));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder());
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton createFilterButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setBackground(Color.WHITE);
        btn.setForeground(new Color(100, 100, 100));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 225), 1));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void setupPlaceholder(JTextField field, String placeholder) {
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(new Color(150, 150, 150));
                }
            }
        });
    }

    private void loadContacts() {
        allContacts = contactController.listAll();
        updateCards(allContacts);
        updateCountLabel();
    }

    private void performSearch() {
        String query = searchField.getText().trim();
        if (query.isEmpty() || query.equals("Pesquisar contactos...")) {
            loadContacts();
        } else {
            List<Contact> results = contactController.search(query);
            updateCards(results);
        }
    }

    private void applyFilter(ContactType type, JButton selectedBtn) {
        if (currentFilterBtn != null && currentFilterBtn != selectedBtn) {
            currentFilterBtn.setBackground(new Color(245, 245, 245));
            currentFilterBtn.setForeground(new Color(33, 150, 243));
            currentFilterBtn.setBorder(BorderFactory.createLineBorder(new Color(245, 245, 245), 1));
        }

        selectedBtn.setBackground(new Color(33, 150, 243));
        selectedBtn.setForeground(Color.WHITE);
        selectedBtn.setBorder(BorderFactory.createLineBorder(new Color(33, 150, 243), 1));
        currentFilterBtn = selectedBtn;

        List<Contact> filtered = type == null
                ? contactController.listAll()
                : contactController.filterByType(type);
        updateCards(filtered);
    }

    private void updateCards(List<Contact> contacts) {
        cardsPanel.removeAll();

        if (contacts.isEmpty()) {
            JPanel emptyPanel = new JPanel(new MigLayout("center, wrap", "[fill]", "[]20[]"));
            emptyPanel.setBackground(new Color(248, 249, 252));
            emptyPanel.setPreferredSize(new Dimension(1000, 300));

            JLabel emptyIcon = new JLabel("👤");
            emptyIcon.setFont(new Font("Segoe UI", Font.PLAIN, 48));
            emptyPanel.add(emptyIcon);

            JLabel emptyLabel = new JLabel("Ainda não tem contactos guardados");
            emptyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            emptyLabel.setForeground(new Color(120, 120, 120));
            emptyPanel.add(emptyLabel);

            cardsPanel.add(emptyPanel, "grow");
        } else {
            for (Contact contact : contacts) {
                JPanel contactCard = createContactCard(contact);
                contactCard.setAlignmentX(Component.CENTER_ALIGNMENT);
                contactCard.setMaximumSize(new Dimension(1400, 140)); // Largura máxima de 1000px (≈85% de 1200px)
                cardsPanel.add(contactCard, BorderLayout.CENTER);

                // Adicionar um pequeno espaço entre os cards
                cardsPanel.add(Box.createVerticalStrut(10));
            }
        }

        cardsPanel.revalidate();
        cardsPanel.repaint();
    }

    private JPanel createContactCard(Contact contact) {
        JPanel card = new JPanel(new MigLayout("fill, insets 20 40 20 40", "[]", "center"));
        card.setBackground(Color.WHITE);
        card.setBorder(new RoundedBorder(5, new Color(220, 220, 225)));
        card.setPreferredSize(new Dimension(1400, 150));

        // Nome e Badge
        JPanel namePanel = new JPanel(new MigLayout("insets 0", "[]10[]", "[]"));
        namePanel.setBackground(Color.WHITE);
        namePanel.setOpaque(false);

        JLabel nameLabel = new JLabel(contact.getName());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        nameLabel.setForeground(new Color(33, 33, 33));
        namePanel.add(nameLabel );

        JLabel badgeLabel = new JLabel(getTypeDisplay(contact.getType()));
        badgeLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        badgeLabel.setBackground(getTypeBadgeColor(contact.getType()));
        badgeLabel.setForeground(Color.WHITE);
        badgeLabel.setOpaque(true);
        badgeLabel.setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));
        namePanel.add(badgeLabel);

        card.add(namePanel, "grow, wrap");

        JPanel detailsPanel = new JPanel(new MigLayout("insets 0, gap 15", "[][][][]", "center"));
        detailsPanel.setBackground(Color.WHITE);

        // Empresa
        if (contact.getCompany() != null && !contact.getCompany().isEmpty()) {
            JLabel companyLabel = new JLabel("📁 " + contact.getCompany());
            companyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            companyLabel.setForeground(new Color(100, 100, 100));
            detailsPanel.add(companyLabel, "grow");
        }

        // Telefone
        JLabel phoneLabel = new JLabel("📞 " + contact.getPhone());
        phoneLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        phoneLabel.setForeground(new Color(100, 100, 100));
        detailsPanel.add(phoneLabel, "grow");

        // Email
        if (contact.getEmail() != null && !contact.getEmail().isEmpty()) {
            JLabel emailLabel = new JLabel("📧 " + contact.getEmail());
            emailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            emailLabel.setForeground(new Color(100, 100, 100));
            detailsPanel.add(emailLabel, "grow, wrap");
        }

        // Descrição
        if (contact.getDescription() != null && !contact.getDescription().isEmpty()) {
            JLabel descLabel = new JLabel(contact.getDescription());
            descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            descLabel.setForeground(new Color(150, 150, 150));
            detailsPanel.add(descLabel, "grow, wrap");
        }

        card.add(detailsPanel, "grow");

        // Botões de ação (direita)
        JPanel actionsPanel = new JPanel(new MigLayout("insets 0, gap 12", "[][]", "center"));
        actionsPanel.setBackground(Color.WHITE);
        actionsPanel.setOpaque(false);

        JButton editBtn = new JButton("✏️ Editar");
        editBtn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        editBtn.setBackground(new Color(245, 245, 245));
        editBtn.setForeground(new Color(33, 150, 243));
        editBtn.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 225), 1));
        editBtn.setFocusPainted(false);
        editBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        editBtn.addActionListener(e -> openContactForm(contact));
        actionsPanel.add(editBtn, "w 120!, h 40!");

        JButton deleteBtn = new JButton("🗑️ Deletar");
        deleteBtn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        deleteBtn.setBackground(new Color(245, 245, 245));
        deleteBtn.setForeground(new Color(244, 67, 54));
        deleteBtn.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 225), 1));
        deleteBtn.setFocusPainted(false);
        deleteBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        deleteBtn.addActionListener(e -> deleteContact(contact));
        actionsPanel.add(deleteBtn, "w 120!, h 40!");

        card.add(actionsPanel,"right");

        return card;
    }

    private Color getTypeBadgeColor(ContactType type) {
        switch (type) {
            case CUSTOMER:
                return new Color(76, 175, 80);
            case PARTNER:
                return new Color(255, 152, 0);
            case SUPPLIER:
                return new Color(156, 39, 176);
            default:
                return new Color(33, 150, 243);
        }
    }

    private String getTypeDisplay(ContactType type) {
        if (type == null) return "-";
        switch (type) {
            case CUSTOMER:
                return "Cliente";
            case PARTNER:
                return "Parceiro";
            case SUPPLIER:
                return "Fornecedor";
            default:
                return type.name();
        }
    }

    private void updateCountLabel() {
        int count = allContacts.size();
        countLabel.setText(count + " contacto" + (count != 1 ? "s" : "") + " guardado" + (count != 1 ? "s" : ""));
    }

    private void openContactForm(Contact contact) {
        new ContactForm(this, contact, currentUser, contactController, this::loadContacts).setVisible(true);
    }

    private void deleteContact(Contact contact) {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Tem certeza que deseja deletar '" + contact.getName() + "'?",
                "Confirmar Eliminação",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            if (contactController.delete(contact.getId())) {
                JOptionPane.showMessageDialog(this, "Contacto deletado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                loadContacts();
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao deletar contacto!", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleExport() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Selecione o local para exportar");

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File selectedDir = fileChooser.getSelectedFile();

            String[] options = {"TXT", "HTML", "Ambos"};
            int choice = JOptionPane.showOptionDialog(
                    this,
                    "Escolha o formato de exportação:",
                    "Exportar Contactos",
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[2]
            );

            boolean success = false;
            if (choice == 0) {
                success = exportController.exportToTxt(allContacts, selectedDir);
            } else if (choice == 1) {
                success = exportController.exportToHtml(allContacts, selectedDir);
            } else if (choice == 2) {
                success = exportController.exportMultiple(allContacts, selectedDir, "txt", "html");
            }

            if (success) {
                JOptionPane.showMessageDialog(this, "Contactos exportados com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao exportar contactos!", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}