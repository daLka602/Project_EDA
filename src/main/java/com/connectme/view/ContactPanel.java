package com.connectme.view;

import com.connectme.controller.ContactController;
import com.connectme.controller.ExportController;
import com.connectme.model.eda.*;
import com.connectme.model.entities.Contact;
import com.connectme.model.entities.User;
import com.connectme.model.enums.ContactType;
import com.connectme.view.componet.NavButton;
import com.connectme.view.componet.RoundedBorder;
import com.connectme.view.icons.IconUtils;
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
    private GenericLinkedList<Contact> currentContacts;
    private boolean isSorted = false;
    private JLabel countLabel;
    private JButton currentFilterBtn;
    private NavButton undoBtn;
    private NavButton redoBtn;

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
        // Header Panel (mantém o mesmo)
        JPanel headerPanel = new JPanel(new MigLayout("fill, insets 60 90 30 80", "[]15[]push[]10[]", "[]"));
        headerPanel.setBackground(new Color(248, 249, 252));

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

        // Botões de controle
        JPanel controlPanel = new JPanel(new MigLayout("insets 0, gap 10", "[][][][]", "[]"));
        controlPanel.setBackground(new Color(248, 249, 252));

        // Botão Atualizar
        NavButton refreshBtn = new NavButton("🔄 Atualizar", true);
        refreshBtn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        refreshBtn.setToolTipText("Recarregar contatos do banco de dados");
        refreshBtn.addActionListener(e -> handleRefresh());
        controlPanel.add(refreshBtn, "w 130!, h 45!");

        undoBtn = new NavButton("⬅ Anterior", true);
        undoBtn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        undoBtn.setBackground(new Color(225, 220, 220));
        undoBtn.setForeground(new Color(30, 34, 44));
        undoBtn.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        undoBtn.setToolTipText("Desfazer última operação");
        undoBtn.addActionListener(e -> handleUndo());
        controlPanel.add(undoBtn, "w 120!, h 45!");

        redoBtn = new NavButton("➡ Depois", true);
        redoBtn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        redoBtn.setBackground(new Color(225, 220, 220));
        redoBtn.setForeground(new Color(30, 34, 44));
        redoBtn.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        redoBtn.setToolTipText("Refazer operação desfeita");
        redoBtn.addActionListener(e -> handleRedo());
        controlPanel.add(redoBtn, "w 120!, h 45!");

        NavButton sortBtn = new NavButton("⬍ Organizar", true);
        sortBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        sortBtn.setToolTipText("Ordenar contatos");
        sortBtn.addActionListener(e -> showSortDialog());
        controlPanel.add(sortBtn, "w 160!, h 45!");

        NavButton exportBtn = new NavButton("📤 Exportar", true);
        exportBtn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        exportBtn.setToolTipText("Exportar contatos");
        exportBtn.addActionListener(e -> handleExport());
        controlPanel.add(exportBtn, "w 130!, h 45!");

        headerPanel.add(controlPanel, "right");

        add(headerPanel, BorderLayout.NORTH);

        // Search and Filter Panel (mantém o mesmo)
        JPanel searchPanel = new JPanel(new MigLayout("fill, insets 20 40 20 40", "[][][]0[grow][]", "center"));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(new RoundedBorder(1, new Color(220, 220, 225)));

        ImageIcon originalSearchIcon = new ImageIcon("src/main/java/com/connectme/view/icons/search.png");
        ImageIcon searchIcon = IconUtils.colorizeIcon(originalSearchIcon, Color.WHITE);

        searchField = new JTextField(" Pesquisar contactos...");
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.setForeground(new Color(150, 150, 150));
        setupPlaceholder(searchField, " Pesquisar contactos...");
        searchPanel.add(Box.createHorizontalStrut(10));
        searchPanel.add(searchField, "w 240!, h 40!");

        searchPanel.add(Box.createHorizontalStrut(10));
        NavButton searchBtn = new NavButton(" Pesquisar", true);
        searchBtn.setIcon(searchIcon);
        searchBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        searchBtn.addActionListener(e -> performSearch());
        searchPanel.add(searchBtn, "w 160!, h 45!, gapleft 10");

        searchPanel.add(new JLabel(), "w 90!");

        String[] filterLabels = {"Todos", "Clientes", "Parceiros", "Fornecedores"};
        ContactType[] filterTypes = {null, ContactType.CUSTOMER, ContactType.PARTNER, ContactType.SUPPLIER};

        for (int i = 0; i < filterLabels.length; i++) {
            final int index = i;
            JButton filterBtn = createFilterButton(filterLabels[i]);
            filterBtn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            filterBtn.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
            if (i == 0) {
                currentFilterBtn = filterBtn;
                filterBtn.setBackground(new Color(73, 80, 243));
                filterBtn.setForeground(Color.WHITE);
            }
            final JButton finalFilterBtn = filterBtn;
            filterBtn.addActionListener(e -> applyFilter(filterTypes[index], finalFilterBtn));
            searchPanel.add(filterBtn, "grow, h 43!, gapleft 15");
        }

        searchPanel.add(new JLabel(), "grow");

        NavButton addBtn = new NavButton(" + Adicionar", true);
        addBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        addBtn.addActionListener(e -> openContactForm(null));
        searchPanel.add(addBtn, "w 140!, h 45!, gapleft 10");

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

    private JButton createFilterButton(String text) {
        NavButton btn = new NavButton(text, true);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setBackground(new Color(225, 220, 220));
        btn.setForeground(new Color(9, 30, 30));
        btn.setFocusPainted(false);
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
        currentContacts = contactController.listAllAsLinkedList();
        isSorted = false;
        updateCards(currentContacts);
        updateCountLabel();
        updateUndoRedoButtons();
    }

    private void handleRefresh() {
        contactController.refreshCache();
        loadContacts();
        JOptionPane.showMessageDialog(this,
                "Contatos atualizados com sucesso!",
                "Atualização",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void updateUndoRedoButtons() {
        // Atualizar botão Undo
        if (contactController.canUndo()) {
            undoBtn.setBackground(new Color(73, 80, 243));
            undoBtn.setForeground(Color.WHITE);
            undoBtn.setBorder(BorderFactory.createEmptyBorder());
            undoBtn.setEnabled(true);
            String desc = contactController.getUndoDescription();
            undoBtn.setToolTipText(desc != null ? "Desfazer: " + desc : "Desfazer última operação");
        } else {
            undoBtn.setBackground(new Color(225, 220, 220));
            undoBtn.setForeground(new Color(30, 34, 44));
            undoBtn.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
            undoBtn.setEnabled(false);
            undoBtn.setToolTipText("Nenhuma operação para desfazer");
        }

        // Atualizar botão Redo
        if (contactController.canRedo()) {
            redoBtn.setBackground(new Color(73, 80, 243));
            redoBtn.setForeground(Color.WHITE);
            redoBtn.setBorder(BorderFactory.createEmptyBorder());
            redoBtn.setEnabled(true);
            String desc = contactController.getRedoDescription();
            redoBtn.setToolTipText(desc != null ? "Refazer: " + desc : "Refazer operação desfeita");
        } else {
            redoBtn.setBackground(new Color(225, 220, 220));
            redoBtn.setForeground(new Color(30, 34, 44));
            redoBtn.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
            redoBtn.setEnabled(false);
            redoBtn.setToolTipText("Nenhuma operação para refazer");
        }
    }

    private void handleUndo() {
        if (!contactController.canUndo()) {
            JOptionPane.showMessageDialog(this,
                    "Não há operações para desfazer",
                    "Undo",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String description = contactController.getUndoDescription();

        if (contactController.undo()) {
            currentContacts = contactController.listAllAsLinkedList();
            updateCards(currentContacts);
            updateCountLabel();
            updateUndoRedoButtons();

            JOptionPane.showMessageDialog(this,
                    "Operação desfeita: " + (description != null ? description : ""),
                    "Undo",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Erro ao desfazer operação",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleRedo() {
        if (!contactController.canRedo()) {
            JOptionPane.showMessageDialog(this,
                    "Não há operações para refazer",
                    "Redo",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String description = contactController.getRedoDescription();

        if (contactController.redo()) {
            currentContacts = contactController.listAllAsLinkedList();
            updateCards(currentContacts);
            updateCountLabel();
            updateUndoRedoButtons();

            JOptionPane.showMessageDialog(this,
                    "Operação refeita: " + (description != null ? description : ""),
                    "Redo",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Erro ao refazer operação",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void applyFilter(ContactType type, JButton selectedBtn) {
        if (currentFilterBtn != null && currentFilterBtn != selectedBtn) {
            currentFilterBtn.setBackground(new Color(225, 220, 220));
            currentFilterBtn.setForeground(new Color(30, 34, 44));
        }

        selectedBtn.setBackground(new Color(73, 80, 249));
        selectedBtn.setForeground(Color.WHITE);
        currentFilterBtn = selectedBtn;

        GenericArrayList<Contact> filtered = contactController.filterByType(type);
        currentContacts = arrayListToLinkedList(filtered);
        updateCards(currentContacts);
    }

    private void showSortDialog() {
        JDialog sortDialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Organizar Contactos", true);
        sortDialog.setLayout(new MigLayout("fill, insets 25", "[fill]", "[]15[]15[]20[]"));
        sortDialog.setSize(400, 450);
        sortDialog.setLocationRelativeTo(this);

        JLabel titleLabel = new JLabel("⬍ Organizar Contactos");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        sortDialog.add(titleLabel, "wrap");

        // Campo de ordenação
        JLabel fieldLabel = new JLabel("Ordenar por:");
        fieldLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        sortDialog.add(fieldLabel, "wrap");

        JComboBox<String> fieldCombo = new JComboBox<>(new String[]{
                "Nome", "Telefone", "Email", "Empresa", "Tipo"
        });
        fieldCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        sortDialog.add(fieldCombo, "grow, h 40!, wrap");

        // Ordem
        JLabel orderLabel = new JLabel("Ordem:");
        orderLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        sortDialog.add(orderLabel, "wrap");

        JComboBox<String> orderCombo = new JComboBox<>(new String[]{"Crescente (A-Z)", "Decrescente (Z-A)"});
        orderCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        sortDialog.add(orderCombo, "grow, h 40!, wrap");

        // Info algoritmo
        JLabel algoLabel = new JLabel("Algoritmo: MergeSort");
        algoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        algoLabel.setForeground(new Color(100, 100, 100));
        sortDialog.add(algoLabel, "wrap");

        // Botões
        JPanel btnPanel = new JPanel(new MigLayout("", "[grow][grow]", "[]"));
        btnPanel.setOpaque(false);

        JButton cancelBtn = new JButton("Cancelar");
        cancelBtn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cancelBtn.addActionListener(e -> sortDialog.dispose());
        btnPanel.add(cancelBtn, "grow, h 45!");

        NavButton sortBtn = new NavButton("Organizar", true);
        sortBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        sortBtn.addActionListener(e -> {
            performSort(
                    fieldCombo.getSelectedIndex(),
                    orderCombo.getSelectedIndex()
            );
            sortDialog.dispose();
        });
        btnPanel.add(sortBtn, "grow, h 45!");

        sortDialog.add(btnPanel, "grow");
        sortDialog.setVisible(true);
    }

    private void performSort(int fieldIndex, int orderIndex) {
        String field;
        switch (fieldIndex) {
            case 0: field = "name"; break;
            case 1: field = "phone"; break;
            case 2: field = "email"; break;
            case 3: field = "company"; break;
            case 4: field = "type"; break;
            default: field = "name";
        }

        // Determinar ordem
        MergeSort.SortOrder order = (orderIndex == 0) ? MergeSort.SortOrder.ASC : MergeSort.SortOrder.DESC;

        long startTime = System.currentTimeMillis();

        // Obter lista ordenada (NÃO modifica o cache)
        GenericLinkedList<Contact> sorted = contactController.sortWithMergeSort(field, order);

        long endTime = System.currentTimeMillis();

        // Atualizar display com lista ordenada
        currentContacts = sorted;
        isSorted = true;
        updateCards(currentContacts);

        String orderText = (order == MergeSort.SortOrder.ASC) ? "Crescente (A-Z)" : "Decrescente (Z-A)";
        JOptionPane.showMessageDialog(this,
                "Ordenação concluída com MergeSort\n" +
                        "Campo: " + field + "\n" +
                        "Ordem: " + orderText + "\n" +
                        "Tempo: " + (endTime - startTime) + "ms",
                "Sucesso",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void performSearch() {
        String query = searchField.getText().trim();
        if (query.isEmpty() || query.equals("Pesquisar contactos...")) {
            // Se estava ordenado, manter ordenação, senão recarregar original
            if (!isSorted) {
                loadContacts();
            }
        } else {
            GenericArrayList<Contact> results = contactController.search(query);
            currentContacts = arrayListToLinkedList(results);
            isSorted = false; // Resultados de busca não são considerados ordenação
            updateCards(currentContacts);
        }
    }

    private void updateCards(GenericLinkedList<Contact> contacts) {
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
            GenericLinkedList.Iterator<Contact> it = contacts.iterator();
            while (it.hasNext()) {
                Contact contact = it.next();
                if (contact != null) {
                    JPanel contactCard = createContactCard(contact);
                    contactCard.setAlignmentX(Component.CENTER_ALIGNMENT);
                    contactCard.setMaximumSize(new Dimension(1400, 140));
                    cardsPanel.add(contactCard, BorderLayout.CENTER);
                    cardsPanel.add(Box.createVerticalStrut(10));
                }
            }
        }

        cardsPanel.revalidate();
        cardsPanel.repaint();
    }

    private JPanel createContactCard(Contact contact) {
        // Mantém a mesma implementação do card
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
        namePanel.add(nameLabel);

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

        if (contact.getCompany() != null && !contact.getCompany().isEmpty()) {
            JLabel companyLabel = new JLabel("🏢 " + contact.getCompany());
            companyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            companyLabel.setForeground(new Color(100, 100, 100));
            detailsPanel.add(companyLabel, "grow");
        }

        JLabel phoneLabel = new JLabel("📞 " + contact.getPhone());
        phoneLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        phoneLabel.setForeground(new Color(100, 100, 100));
        detailsPanel.add(phoneLabel, "grow");

        if (contact.getEmail() != null && !contact.getEmail().isEmpty()) {
            JLabel emailLabel = new JLabel("📧 " + contact.getEmail());
            emailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            emailLabel.setForeground(new Color(100, 100, 100));
            detailsPanel.add(emailLabel, "grow, wrap");
        }

        if (contact.getDescription() != null && !contact.getDescription().isEmpty()) {
            JLabel descLabel = new JLabel(contact.getDescription());
            descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            descLabel.setForeground(new Color(150, 150, 150));
            detailsPanel.add(descLabel, "grow, wrap");
        }

        card.add(detailsPanel, "grow");

        // Botões de ação
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

        card.add(actionsPanel, "right");

        return card;
    }

    private Color getTypeBadgeColor(ContactType type) {
        switch (type) {
            case CUSTOMER: return new Color(76, 175, 80);
            case PARTNER: return new Color(255, 152, 0);
            case SUPPLIER: return new Color(156, 39, 176);
            default: return new Color(33, 150, 243);
        }
    }

    private String getTypeDisplay(ContactType type) {
        if (type == null) return "-";
        switch (type) {
            case CUSTOMER: return "Cliente";
            case PARTNER: return "Parceiro";
            case SUPPLIER: return "Fornecedor";
            default: return type.name();
        }
    }

    private void updateCountLabel() {
        int count = currentContacts.size();
        countLabel.setText(count + " contacto" + (count != 1 ? "s" : "") + " guardado" + (count != 1 ? "s" : ""));
    }

    private void openContactForm(Contact contact) {
        new ContactForm(this, contact, currentUser, contactController, () -> {
            loadContacts();
            updateUndoRedoButtons();
        }).setVisible(true);
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
                updateUndoRedoButtons();
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

            // Converter GenericLinkedList para List para exportação
            List<Contact> contactList = linkedListToList(currentContacts);

            boolean success = false;
            if (choice == 0) {
                success = exportController.exportToTxt(contactList, selectedDir);
            } else if (choice == 1) {
                success = exportController.exportToHtml(contactList, selectedDir);
            } else if (choice == 2) {
                success = exportController.exportMultiple(contactList, selectedDir, "txt", "html");
            }

            if (success) {
                JOptionPane.showMessageDialog(this, "Contactos exportados com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao exportar contactos!", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private GenericLinkedList<Contact> arrayListToLinkedList(GenericArrayList<Contact> arrayList) {
        GenericLinkedList<Contact> linkedList = new GenericLinkedList<>();
        GenericArrayList.Iterator<Contact> it = arrayList.iterator();
        while (it.hasNext()) {
            Contact c = it.next();
            if (c != null) {
                linkedList.add(c);
            }
        }
        return linkedList;
    }

    private List<Contact> linkedListToList(GenericLinkedList<Contact> linkedList) {
        java.util.ArrayList<Contact> list = new java.util.ArrayList<>();
        GenericLinkedList.Iterator<Contact> it = linkedList.iterator();
        while (it.hasNext()) {
            Contact c = it.next();
            if (c != null) {
                list.add(c);
            }
        }
        return list;
    }
}