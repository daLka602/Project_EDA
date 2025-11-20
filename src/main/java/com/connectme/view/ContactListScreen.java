package com.connectme.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

import net.miginfocom.swing.MigLayout;
import com.connectme.controller.UserController;
import com.connectme.model.entities.Contact;

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
    private int pageSize = 20;
    private int totalContacts = 0;

    public ContactListScreen(UserController controller) {
        super("ConnectMe - Contactos");
        this.controller = controller;

        initUI();
        loadContacts();
    }

    private void initUI() {
        setLayout(new MigLayout("wrap 1", "[grow]", "[][][grow]20[]"));
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JLabel lblTitle = new JLabel("Lista de Contactos");
        lblTitle.setFont(lblTitle.getFont().deriveFont(18f));
        add(lblTitle, "align center, gapbottom 10");

        // Painel de filtros avançado
        JPanel filters = new JPanel(new MigLayout("", "[][grow][][120][][80]"));
        
        tfNameFilter = new JTextField();
        tfPhoneFilter = new JTextField();
        cbSort = new JComboBox<>(new String[] { "Ordenar: Nenhum", "Nome ↑", "Nome ↓", "Telefone ↑", "Telefone ↓" });
        
        spPageSize = new JSpinner(new SpinnerNumberModel(20, 5, 100, 5));
        lblPageInfo = new JLabel("Carregando...");

        filters.add(new JLabel("Nome:"));
        filters.add(tfNameFilter, "growx");
        filters.add(new JLabel("Telefone:"));
        filters.add(tfPhoneFilter, "growx");
        filters.add(new JLabel("Ordenar:"));
        filters.add(cbSort, "wrap");
        
        filters.add(new JLabel("Itens por página:"));
        filters.add(spPageSize, "growx");
        filters.add(lblPageInfo, "span 2");
        
        JButton btnSearch = new JButton("Pesquisar");
        JButton btnClear = new JButton("Limpar");
        JButton btnAdvanced = new JButton("Busca Avançada");
        
        filters.add(btnSearch, "split 3");
        filters.add(btnClear);
        filters.add(btnAdvanced);

        add(filters, "growx");

        // Tabela
        table = new JTable();
        table.setAutoCreateRowSorter(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(table), "growx, growy");

        // Painel de controle
        JPanel controlPanel = new JPanel(new MigLayout("insets 0", "[grow][grow][grow][grow][grow]"));
        
        JButton btnFirst = new JButton("⏮");
        JButton btnPrev = new JButton("◀");
        JButton btnNext = new JButton("▶");
        JButton btnLast = new JButton("⏭");
        
        JButton btnAdd = new JButton("Adicionar");
        JButton btnEdit = new JButton("Editar");
        JButton btnDelete = new JButton("Apagar");
        JButton btnExport = new JButton("Exportar");
        JButton btnBack = new JButton("Voltar");

        controlPanel.add(btnFirst);
        controlPanel.add(btnPrev);
        controlPanel.add(btnNext);
        controlPanel.add(btnLast);
        controlPanel.add(btnAdd, "growx");
        controlPanel.add(btnEdit, "growx");
        controlPanel.add(btnDelete, "growx");
        controlPanel.add(btnExport, "growx");
        controlPanel.add(btnBack, "growx");

        add(controlPanel, "growx");

        // Ações
        setupActions(btnSearch, btnClear, btnAdvanced, btnFirst, btnPrev, btnNext, btnLast, 
                    btnAdd, btnEdit, btnDelete, btnExport, btnBack);
    }

    private void setupActions(JButton btnSearch, JButton btnClear, JButton btnAdvanced,
                            JButton btnFirst, JButton btnPrev, JButton btnNext, JButton btnLast,
                            JButton btnAdd, JButton btnEdit, JButton btnDelete, JButton btnExport, JButton btnBack) {
        
        btnBack.addActionListener(e -> {
            new DashboardScreen(controller).setVisible(true);
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
            spPageSize.setValue(20);
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
        dialog.setLayout(new MigLayout("wrap 2"));
        
        JTextField tfEmail = new JTextField(20);
        JTextField tfAddress = new JTextField(20);
        JCheckBox cbExactMatch = new JCheckBox("Busca exata");
        
        dialog.add(new JLabel("Email:"));
        dialog.add(tfEmail, "growx");
        dialog.add(new JLabel("Morada:"));
        dialog.add(tfAddress, "growx");
        dialog.add(cbExactMatch, "span 2");
        
        JButton btnSearch = new JButton("Buscar");
        JButton btnCancel = new JButton("Cancelar");
        
        dialog.add(btnSearch, "split 2");
        dialog.add(btnCancel);
        
        btnSearch.addActionListener(e -> {
            // Implementar busca avançada aqui
            JOptionPane.showMessageDialog(dialog, "Funcionalidade em desenvolvimento");
            dialog.dispose();
        });
        
        btnCancel.addActionListener(e -> dialog.dispose());
        
        dialog.pack();
        dialog.setLocationRelativeTo(this);
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