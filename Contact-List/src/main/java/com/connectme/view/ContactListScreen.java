package com.connectme.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

import net.miginfocom.swing.MigLayout;
import com.connectme.controller.UserController;
import com.connectme.model.Contact;

public class ContactListScreen extends JFrame {

    private UserController controller;
    private JTable table;

    private JTextField tfNameFilter;
    private JTextField tfPhoneFilter;
    private JComboBox<String> cbSort;

    public ContactListScreen(UserController controller) {
        super("ConnectMe - Contactos");
        this.controller = controller;

        initUI();
        loadContacts(); // initial load
    }

    private void initUI() {
        setLayout(new MigLayout("wrap 1", "[grow]", "[][grow]20[]"));
        setSize(800, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JLabel lblTitle = new JLabel("Lista de Contactos");
        lblTitle.setFont(lblTitle.getFont().deriveFont(18f));
        add(lblTitle, "align center, gapbottom 10");

        // Filters panel
        JPanel filters = new JPanel(new MigLayout("", "[grow][grow][120]"));
        tfNameFilter = new JTextField();
        tfPhoneFilter = new JTextField();
        cbSort = new JComboBox<>(new String[] { "Ordenar: Nenhum", "Nome ↑", "Nome ↓" });

        filters.add(new JLabel("Nome:"), "split 2");
        filters.add(tfNameFilter, "growx");
        filters.add(new JLabel("Telefone:"), "split 2");
        filters.add(tfPhoneFilter, "growx");
        filters.add(cbSort, "wrap");

        JButton btnSearch = new JButton("Pesquisar");
        JButton btnClear = new JButton("Limpar");
        filters.add(btnSearch, "split 2");
        filters.add(btnClear);

        add(filters, "growx");

        table = new JTable();
        add(new JScrollPane(table), "growx, growy");

        JButton btnAdd = new JButton("Adicionar");
        JButton btnEdit = new JButton("Editar");
        JButton btnDelete = new JButton("Apagar");
        JButton btnBack = new JButton("Voltar");
        JButton btnExport = new JButton("Exportar Seleção");

        JPanel panel = new JPanel(new MigLayout("insets 0", "[grow][grow][grow][grow][grow]"));
        panel.add(btnAdd, "growx");
        panel.add(btnEdit, "growx");
        panel.add(btnDelete, "growx");
        panel.add(btnExport, "growx");
        panel.add(btnBack, "growx");

        add(panel, "growx");

        // Actions
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

        btnSearch.addActionListener(e -> loadContactsFiltered());
        btnClear.addActionListener(e -> {
            tfNameFilter.setText("");
            tfPhoneFilter.setText("");
            cbSort.setSelectedIndex(0);
            loadContacts();
        });
    }

    private void loadContacts() {
        List<Contact> list = controller.listContactsForLoggedUser();
        fillTable(list);
    }

    private void loadContactsFiltered() {
        String nameQ = tfNameFilter.getText().trim();
        String phoneQ = tfPhoneFilter.getText().trim();
        String sortOrder = switch (cbSort.getSelectedIndex()) {
            case 1 -> "name_asc";
            case 2 -> "name_desc";
            default -> "";
        };
        List<Contact> result = controller.searchList(nameQ, phoneQ, sortOrder);
        fillTable(result);
    }

    private void fillTable(List<Contact> list) {
        String[] headers = { "ID", "Nome", "Telefone", "Email", "Morada" };
        DefaultTableModel model = new DefaultTableModel(headers, 0);

        for (Contact c : list) {
            model.addRow(new Object[] {
                    c.getId(),
                    c.getName(),
                    c.getPhone(),
                    c.getEmail(),
                    c.getAddress()
            });
        }
        table.setModel(model);
    }

    private void editSelected() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um contacto.");
            return;
        }

        int id = (int) table.getValueAt(row, 0);
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

        int id = (int) table.getValueAt(row, 0);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Deseja realmente apagar este contacto?",
                "Confirmar",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            controller.deleteContact(id);
            loadContactsFiltered();
        }
    }

    private void exportFiltered() {
        String nameQ = tfNameFilter.getText().trim();
        String phoneQ = tfPhoneFilter.getText().trim();
        String sortOrder = switch (cbSort.getSelectedIndex()) {
            case 1 -> "name_asc";
            case 2 -> "name_desc";
            default -> "";
        };
        List<Contact> result = controller.searchList(nameQ, phoneQ, sortOrder);

        String[] options = { "PDF", "TXT" };
        String choice = (String) JOptionPane.showInputDialog(
                this,
                "Escolha o formato",
                "Exportar Contactos",
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                "PDF");

        if (choice == null)
            return;

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
