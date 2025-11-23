package com.connectme.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

import net.miginfocom.swing.MigLayout;
import com.connectme.controller.UserController;
import com.connectme.model.entities.Contact;
import java.awt.*;

public class ContactListScreen extends JFrame {

    private UserController controller;
    private JTable table;
    private DefaultTableModel tableModel;

    // Filtros e paginação
    private JTextField tfNameFilter;
    private JTextField tfPhoneFilter;
    private JComboBox<String> cbSort;
    private JSpinner spPageSize;
    private JLabel lblPageInfo;

    private int currentPage = 0;
    private int pageSize = 50; // Aumentado para 1920x1080
    private int totalContacts = 0;

    public ContactListScreen(UserController controller) {
        super("ConnectMe - Contactos");
        this.controller = controller;

        initUI();
        loadContacts();
    }

    public ContactListScreen() {
        super("ConnectMe - Contactos");
        initUI();
        loadContacts();
    }

    private void initUI() {
        setLayout(new MigLayout("wrap 1", "[grow]", "[80!][100!][grow][60!]"));
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // Título
        JLabel lblTitle = new JLabel("LISTA DE CONTACTOS");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        add(lblTitle, "growx, h 80!");

        // Painel de filtros otimizado para 1920x1080
        JPanel filters = new JPanel(new MigLayout("", "[120!][250!][120!][250!][150!][120!][150!]"));
        filters.setBorder(BorderFactory.createTitledBorder("Filtros e Ordenação"));

        tfNameFilter = new JTextField();
        tfNameFilter.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tfNameFilter.setPreferredSize(new Dimension(250, 35));

        tfPhoneFilter = new JTextField();
        tfPhoneFilter.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tfPhoneFilter.setPreferredSize(new Dimension(250, 35));

        cbSort = new JComboBox<>(new String[] { "Ordenar: Nenhum", "Nome ↑", "Nome ↓", "Telefone ↑", "Telefone ↓" });
        cbSort.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cbSort.setPreferredSize(new Dimension(200, 35));

        spPageSize = new JSpinner(new SpinnerNumberModel(50, 10, 200, 10));
        spPageSize.setPreferredSize(new Dimension(80, 35));

        lblPageInfo = new JLabel("Carregando...");
        lblPageInfo.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        // Primeira linha de filtros
        filters.add(new JLabel("Nome:"));
        filters.add(tfNameFilter, "growx");
        filters.add(new JLabel("Telefone:"));
        filters.add(tfPhoneFilter, "growx");
        filters.add(new JLabel("Ordenar:"));
        filters.add(cbSort, "wrap");

        // Segunda linha de controles
        filters.add(new JLabel("Itens por página:"));
        filters.add(spPageSize, "growx");
        filters.add(lblPageInfo, "span 3");

        JButton btnSearch = new JButton("PESQUISAR");
        JButton btnClear = new JButton("LIMPAR");
        JButton btnAdvanced = new JButton("BUSCA AVANÇADA");

        // Estilizar botões
        styleFilterButton(btnSearch);
        styleFilterButton(btnClear);
        styleFilterButton(btnAdvanced);

        filters.add(btnSearch, "split 3, gaptop 10");
        filters.add(btnClear);
        filters.add(btnAdvanced);

        add(filters, "growx, h 100!");

        // Tabela com altura otimizada
        table = new JTable();
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(30);
        table.setAutoCreateRowSorter(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(1800, 800));
        add(scrollPane, "grow");

        // Painel de controle otimizado
        JPanel controlPanel = new JPanel(new MigLayout("insets 10", "[grow][grow][grow][grow][grow][grow][grow][grow][grow]"));
        controlPanel.setBorder(BorderFactory.createEtchedBorder());

        JButton btnFirst = new JButton("⏮ PRIMEIRA");
        JButton btnPrev = new JButton("◀ ANTERIOR");
        JButton btnNext = new JButton("PRÓXIMA ▶");
        JButton btnLast = new JButton("ÚLTIMA ⏭");

        JButton btnAdd = new JButton("➕ ADICIONAR");
        JButton btnEdit = new JButton("✏️ EDITAR");
        JButton btnDelete = new JButton("🗑️ APAGAR");
        JButton btnExport = new JButton("📤 EXPORTAR");
        JButton btnBack = new JButton("← VOLTAR");

        // Estilizar todos os botões
        styleControlButton(btnFirst);
        styleControlButton(btnPrev);
        styleControlButton(btnNext);
        styleControlButton(btnLast);
        styleControlButton(btnAdd);
        styleControlButton(btnEdit);
        styleControlButton(btnDelete);
        styleControlButton(btnExport);
        styleControlButton(btnBack);

        controlPanel.add(btnFirst, "growx");
        controlPanel.add(btnPrev, "growx");
        controlPanel.add(btnNext, "growx");
        controlPanel.add(btnLast, "growx");
        controlPanel.add(btnAdd, "growx");
        controlPanel.add(btnEdit, "growx");
        controlPanel.add(btnDelete, "growx");
        controlPanel.add(btnExport, "growx");
        controlPanel.add(btnBack, "growx");

        add(controlPanel, "growx, h 60!");

        // Ações
        setupActions(btnSearch, btnClear, btnAdvanced, btnFirst, btnPrev, btnNext, btnLast,
                btnAdd, btnEdit, btnDelete, btnExport, btnBack);
    }

    private void styleFilterButton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setPreferredSize(new Dimension(140, 35));
    }

    private void styleControlButton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setPreferredSize(new Dimension(150, 40));
    }

    private void setupActions(JButton btnSearch, JButton btnClear, JButton btnAdvanced,
                              JButton btnFirst, JButton btnPrev, JButton btnNext, JButton btnLast,
                              JButton btnAdd, JButton btnEdit, JButton btnDelete, JButton btnExport, JButton btnBack) {

        btnBack.addActionListener(e -> {
            new HomepageView(controller).setVisible(true);
            dispose();
        });

        btnAdd.addActionListener(e -> {
            new ContactFormScreen(controller, null).setVisible(true);
            dispose();
        });

        btnEdit.addActionListener(e -> editSelected());
        btnDelete.addActionListener(e -> deleteSelected());
        btnExport.addActionListener(e -> exportFiltered());

        btnSearch.addActionListener(e -> {
            currentPage = 0;
            loadContactsFiltered();
        });

        btnClear.addActionListener(e -> {
            tfNameFilter.setText("");
            tfPhoneFilter.setText("");
            cbSort.setSelectedIndex(0);
            spPageSize.setValue(50);
            currentPage = 0;
            loadContacts();
        });

        btnAdvanced.addActionListener(e -> showAdvancedSearch());

        // Paginação
        btnFirst.addActionListener(e -> {
            currentPage = 0;
            loadContactsFiltered();
        });

        btnPrev.addActionListener(e -> {
            if (currentPage > 0) {
                currentPage--;
                loadContactsFiltered();
            }
        });

        btnNext.addActionListener(e -> {
            if ((currentPage + 1) * pageSize < totalContacts) {
                currentPage++;
                loadContactsFiltered();
            }
        });

        btnLast.addActionListener(e -> {
            currentPage = (int) Math.ceil((double) totalContacts / pageSize) - 1;
            if (currentPage < 0) currentPage = 0;
            loadContactsFiltered();
        });

        // Atualizar página quando mudar tamanho
        spPageSize.addChangeListener(e -> {
            pageSize = (int) spPageSize.getValue();
            currentPage = 0;
            loadContactsFiltered();
        });
    }

    private void loadContacts() {
        List<Contact> list = controller.listContactsForLoggedUser();
        totalContacts = list.size();
        fillTable(list);
        updatePaginationInfo();
    }

    private void loadContactsFiltered() {
        String nameQ = tfNameFilter.getText().trim();
        String phoneQ = tfPhoneFilter.getText().trim();
        String sortOrder = getSortOrder();

        List<Contact> result = controller.searchList(nameQ, phoneQ, sortOrder);
        totalContacts = result.size();

        // Aplicar paginação
        int fromIndex = currentPage * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, result.size());

        if (fromIndex < result.size()) {
            List<Contact> page = result.subList(fromIndex, toIndex);
            fillTable(page);
        } else {
            fillTable(List.of());
        }

        updatePaginationInfo();
    }

    private void fillTable(List<Contact> list) {
        String[] headers = { "ID", "Nome", "Telefone", "Email", "Morada" };
        tableModel = new DefaultTableModel(headers, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Tabela não editável
            }
        };

        for (Contact c : list) {
            tableModel.addRow(new Object[] {
                    c.getId(),
                    c.getName(),
                    c.getPhone(),
                    c.getEmail() != null ? c.getEmail() : "",
                    c.getAddress() != null ? c.getAddress() : ""
            });
        }
        table.setModel(tableModel);

        // Esconder coluna ID
        table.removeColumn(table.getColumnModel().getColumn(0));

        // Ajustar largura das colunas para 1920x1080
        if (table.getColumnCount() >= 4) {
            table.getColumnModel().getColumn(0).setPreferredWidth(400); // Nome
            table.getColumnModel().getColumn(1).setPreferredWidth(250); // Telefone
            table.getColumnModel().getColumn(2).setPreferredWidth(400); // Email
            table.getColumnModel().getColumn(3).setPreferredWidth(600); // Morada
        }
    }

    private void updatePaginationInfo() {
        int totalPages = (int) Math.ceil((double) totalContacts / pageSize);
        if (totalPages == 0) totalPages = 1;

        int startItem = currentPage * pageSize + 1;
        int endItem = Math.min((currentPage + 1) * pageSize, totalContacts);

        lblPageInfo.setText(String.format("Página %d de %d - Itens %d-%d de %d",
                currentPage + 1, totalPages, startItem, endItem, totalContacts));
    }

    private String getSortOrder() {
        int selectedIndex = cbSort.getSelectedIndex();

        switch (selectedIndex) {
            case 1:
                return "name_asc";
            case 2:
                return "name_desc";
            case 3:
                return "phone_asc";
            case 4:
                return "phone_desc";
            default:
                return "";
        }
    }

    private void showAdvancedSearch() {
        JDialog dialog = new JDialog(this, "Busca Avançada", true);
        dialog.setLayout(new MigLayout("wrap 2", "[150!][300!]", "[]10[]10[]10[]20[]"));
        dialog.setSize(500, 300);
        dialog.setLocationRelativeTo(this);

        JTextField tfEmail = new JTextField();
        tfEmail.setPreferredSize(new Dimension(300, 35));
        JTextField tfAddress = new JTextField();
        tfAddress.setPreferredSize(new Dimension(300, 35));
        JCheckBox cbExactMatch = new JCheckBox("Busca exata");
        cbExactMatch.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        dialog.add(new JLabel("Email:"));
        dialog.add(tfEmail, "growx");
        dialog.add(new JLabel("Morada:"));
        dialog.add(tfAddress, "growx");
        dialog.add(cbExactMatch, "span 2");

        JButton btnSearch = new JButton("BUSCAR");
        JButton btnCancel = new JButton("CANCELAR");

        btnSearch.setPreferredSize(new Dimension(120, 35));
        btnCancel.setPreferredSize(new Dimension(120, 35));

        dialog.add(btnSearch, "split 2, align right");
        dialog.add(btnCancel);

        btnSearch.addActionListener(e -> {
            // Implementar busca avançada aqui
            JOptionPane.showMessageDialog(dialog, "Funcionalidade em desenvolvimento");
            dialog.dispose();
        });

        btnCancel.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }

    private void editSelected() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um contacto.");
            return;
        }

        // Converter índice da view para modelo (devido à ordenação)
        int modelRow = table.convertRowIndexToModel(row);
        int id = (int) tableModel.getValueAt(modelRow, 0);
        Contact c = controller.findContactById(id);

        new ContactFormScreen(controller, c).setVisible(true);
        dispose();
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um contacto.");
            return;
        }

        int modelRow = table.convertRowIndexToModel(row);
        int id = (int) tableModel.getValueAt(modelRow, 0);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Deseja realmente apagar este contacto?",
                "Confirmar",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = controller.deleteContact(id);
            if (success) {
                loadContactsFiltered();
                JOptionPane.showMessageDialog(this, "Contacto apagado com sucesso!");
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao apagar contacto.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void exportFiltered() {
        String nameQ = tfNameFilter.getText().trim();
        String phoneQ = tfPhoneFilter.getText().trim();
        String sortOrder = getSortOrder();
        List<Contact> result = controller.searchList(nameQ, phoneQ, sortOrder);

        if (result.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nenhum contacto para exportar.");
            return;
        }

        String[] options = { "PDF", "TXT" };
        String choice = (String) JOptionPane.showInputDialog(
                this,
                "Escolha o formato",
                "Exportar Contactos",
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                "PDF");

        if (choice == null) return;

        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new java.io.File("contactos." + choice.toLowerCase()));

        int resultDialog = chooser.showSaveDialog(this);
        if (resultDialog == JFileChooser.APPROVE_OPTION) {
            boolean ok = controller.exportContactsList(choice, chooser.getSelectedFile(), result);

            if (ok)
                JOptionPane.showMessageDialog(this, "Exportado com sucesso!");
            else
                JOptionPane.showMessageDialog(this, "Erro ao exportar.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}