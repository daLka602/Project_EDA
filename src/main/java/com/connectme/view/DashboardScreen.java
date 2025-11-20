package com.connectme.view;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import net.miginfocom.swing.MigLayout;
import com.connectme.controller.UserController;
import com.connectme.model.entities.User;
import com.connectme.model.entities.Contact;

import java.util.List;
import java.awt.*;

public class DashboardScreen extends JFrame {

    private UserController controller;
    private User user;

    // Componentes para estatísticas
    private JLabel lblTotalContacts;
    private JLabel lblRecentActivity;
    private JPanel statsPanel;

    public DashboardScreen(UserController controller) {
        super("ConnectMe - Dashboard");
        this.controller = controller;
        this.user = controller.getLoggedUser();

        initUI();
        loadStatistics();
    }

    private void initUI() {
        setLayout(new MigLayout("wrap 1", "[400]", "20[]10[]10[]10[]10[]10[]10[]"));
        setSize(450, 500);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Cabeçalho
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, "growx");

        // Painel de estatísticas
        statsPanel = createStatsPanel();
        add(statsPanel, "growx");

        // Separador
        add(new JSeparator(), "growx, gaptop 10, gapbottom 10");

        // Painel de ações principais
        JPanel actionsPanel = createActionsPanel();
        add(actionsPanel, "growx");

        // Painel de utilidades
        JPanel utilsPanel = createUtilsPanel();
        add(utilsPanel, "growx");
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new MigLayout("", "[grow][]"));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setBackground(new Color(240, 240, 240));

        JLabel lblWelcome = new JLabel("Bem-vindo, " + user.getUsername());
        lblWelcome.setFont(lblWelcome.getFont().deriveFont(18f).deriveFont(Font.BOLD));
        
        JLabel lblStatus = new JLabel("✓ Online");
        lblStatus.setForeground(new Color(0, 128, 0));
        lblStatus.setFont(lblStatus.getFont().deriveFont(Font.ITALIC));

        panel.add(lblWelcome, "growx");
        panel.add(lblStatus, "wrap");
        
        return panel;
    }

    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new MigLayout("wrap 2", "[grow][grow]", "[]10[]"));
        panel.setBorder(BorderFactory.createTitledBorder("Estatísticas"));

        lblTotalContacts = new JLabel("Carregando...");
        lblTotalContacts.setFont(lblTotalContacts.getFont().deriveFont(14f));
        
        lblRecentActivity = new JLabel("Última atividade: --");
        lblRecentActivity.setFont(lblRecentActivity.getFont().deriveFont(12f));

        JLabel lblQuickActions = new JLabel("Ações Rápidas:");
        lblQuickActions.setFont(lblQuickActions.getFont().deriveFont(Font.BOLD));

        panel.add(lblTotalContacts, "span 2");
        panel.add(lblRecentActivity, "span 2");
        panel.add(lblQuickActions, "span 2, gaptop 10");
        
        // Botões de ação rápida
        JButton btnQuickAdd = new JButton("+ Contacto");
        JButton btnQuickSearch = new JButton("🔍 Buscar");
        
        btnQuickAdd.addActionListener(e -> quickAddContact());
        btnQuickSearch.addActionListener(e -> quickSearch());
        
        panel.add(btnQuickAdd, "growx");
        panel.add(btnQuickSearch, "growx");

        return panel;
    }

    private JPanel createActionsPanel() {
        JPanel panel = new JPanel(new MigLayout("wrap 1", "[grow]", "[]5[]5[]5[]"));
        panel.setBorder(BorderFactory.createTitledBorder("Gestão de Contactos"));

        JButton btnContacts = new JButton("📋 Lista de Contactos");
        JButton btnAdd = new JButton("➕ Adicionar Contacto");
        JButton btnSearchAdvanced = new JButton("🔎 Busca Avançada");
        JButton btnExport = new JButton("📤 Exportar Contactos");

        // Estilizar botões principais
        styleMainButton(btnContacts);
        styleMainButton(btnAdd);
        styleMainButton(btnSearchAdvanced);
        styleMainButton(btnExport);

        btnContacts.addActionListener(e -> openContactList());
        btnAdd.addActionListener(e -> openAddContact());
        btnSearchAdvanced.addActionListener(e -> openAdvancedSearch());
        btnExport.addActionListener(e -> exportContacts());

        panel.add(btnContacts, "growx");
        panel.add(btnAdd, "growx");
        panel.add(btnSearchAdvanced, "growx");
        panel.add(btnExport, "growx");

        return panel;
    }

    private JPanel createUtilsPanel() {
        JPanel panel = new JPanel(new MigLayout("wrap 2", "[grow][grow]", "[]5[]"));
        panel.setBorder(BorderFactory.createTitledBorder("Utilidades"));

        JButton btnTheme = new JButton("🎨 Alternar Tema");
        JButton btnBackup = new JButton("💾 Backup");
        JButton btnStats = new JButton("📊 Estatísticas Detalhadas");
        JButton btnLogout = new JButton("🚪 Logout");

        // Estilizar botões de utilidade
        styleUtilButton(btnTheme);
        styleUtilButton(btnBackup);
        styleUtilButton(btnStats);
        styleUtilButton(btnLogout);

        btnTheme.addActionListener(e -> toggleTheme());
        btnBackup.addActionListener(e -> createBackup());
        btnStats.addActionListener(e -> showDetailedStats());
        btnLogout.addActionListener(e -> logout());

        panel.add(btnTheme, "growx");
        panel.add(btnBackup, "growx");
        panel.add(btnStats, "growx");
        panel.add(btnLogout, "growx");

        return panel;
    }

    private void styleMainButton(JButton button) {
        button.setFont(button.getFont().deriveFont(Font.BOLD));
        button.setPreferredSize(new Dimension(200, 35));
    }

    private void styleUtilButton(JButton button) {
        button.setPreferredSize(new Dimension(150, 30));
    }

    private void loadStatistics() {
        // Carregar estatísticas em background
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            private int totalContacts;
            private String lastActivity;

            @Override
            protected Void doInBackground() throws Exception {
                List<Contact> contacts = controller.listContactsForLoggedUser();
                totalContacts = contacts.size();
                
                // Simular última atividade (em implementação real, buscar do BD)
                lastActivity = "Hoje às " + java.time.LocalTime.now().format(
                    java.time.format.DateTimeFormatter.ofPattern("HH:mm"));
                
                return null;
            }

            @Override
            protected void done() {
                lblTotalContacts.setText("📞 Total de Contactos: " + totalContacts);
                lblRecentActivity.setText("🕒 " + lastActivity);
                
                // Atualizar tooltip com mais informações
                statsPanel.setToolTipText(String.format(
                    "Estatísticas detalhadas:\n- Total de contactos: %d\n- Última atividade: %s",
                    totalContacts, lastActivity
                ));
            }
        };
        worker.execute();
    }

    // === AÇÕES PRINCIPAIS ===
    
    private void openContactList() {
        new ContactListScreen(controller).setVisible(true);
        dispose();
    }

    private void openAddContact() {
        new ContactFormScreen(controller, null).setVisible(true);
        dispose();
    }

    private void openAdvancedSearch() {
        // Diálogo de busca avançada
        JDialog searchDialog = new JDialog(this, "Busca Avançada", true);
        searchDialog.setLayout(new MigLayout("wrap 2", "[][grow]"));
        
        JTextField txtSearch = new JTextField(20);
        JComboBox<String> cbSearchType = new JComboBox<>(
            new String[]{"Por Nome", "Por Telefone", "Por Email", "Por Morada"});
        
        JCheckBox cbExact = new JCheckBox("Busca exata");
        JCheckBox cbCaseSensitive = new JCheckBox("Case sensitive");
        
        searchDialog.add(new JLabel("Buscar:"));
        searchDialog.add(txtSearch, "growx, wrap");
        searchDialog.add(new JLabel("Tipo:"));
        searchDialog.add(cbSearchType, "growx, wrap");
        searchDialog.add(cbExact, "span 2");
        searchDialog.add(cbCaseSensitive, "span 2, wrap");
        
        JButton btnSearch = new JButton("Buscar");
        JButton btnCancel = new JButton("Cancelar");
        
        searchDialog.add(btnSearch, "split 2, align right");
        searchDialog.add(btnCancel);
        
        btnSearch.addActionListener(e -> {
            String query = txtSearch.getText().trim();
            if (!query.isEmpty()) {
                performAdvancedSearch(query, cbSearchType.getSelectedIndex(), 
                                    cbExact.isSelected(), cbCaseSensitive.isSelected());
                searchDialog.dispose();
            }
        });
        
        btnCancel.addActionListener(e -> searchDialog.dispose());
        
        searchDialog.pack();
        searchDialog.setLocationRelativeTo(this);
        searchDialog.setVisible(true);
    }

    private void performAdvancedSearch(String query, int searchType, 
                                     boolean exactMatch, boolean caseSensitive) {
        // Implementar busca avançada
        List<Contact> results = controller.searchList(
            searchType == 0 ? query : "", 
            searchType == 1 ? query : "", 
            ""
        );
        
        if (results.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nenhum resultado encontrado.");
        } else {
            JOptionPane.showMessageDialog(this, 
                String.format("Encontrados %d contactos.", results.size()));
            
            // Abrir lista com resultados filtrados
            ContactListScreen listScreen = new ContactListScreen(controller);
            listScreen.setVisible(true);
            dispose();
        }
    }

    private void exportContacts() {
        String[] options = {"PDF", "TXT"};
        String choice = (String) JOptionPane.showInputDialog(
            this,
            "Escolha o formato de exportação:",
            "Exportar Contactos",
            JOptionPane.PLAIN_MESSAGE,
            null,
            options,
            "PDF"
        );

        if (choice == null) return;

        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new java.io.File("contactos_connectme." + choice.toLowerCase()));

        int result = chooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            boolean ok = controller.exportContacts(choice, chooser.getSelectedFile());
            if (ok) {
                JOptionPane.showMessageDialog(this, 
                    "Exportação realizada com sucesso!", "Sucesso", 
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Erro ao exportar contactos.", "Erro", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // === AÇÕES RÁPIDAS ===
    
    private void quickAddContact() {
        // Diálogo rápido para adicionar contacto
        JDialog quickDialog = new JDialog(this, "Adicionar Contacto Rápido", true);
        quickDialog.setLayout(new MigLayout("wrap 2", "[][grow]"));
        
        JTextField txtName = new JTextField(15);
        JTextField txtPhone = new JTextField(15);
        
        quickDialog.add(new JLabel("Nome:*"));
        quickDialog.add(txtName, "growx, wrap");
        quickDialog.add(new JLabel("Telefone:*"));
        quickDialog.add(txtPhone, "growx, wrap");
        
        JButton btnSave = new JButton("Salvar");
        JButton btnCancel = new JButton("Cancelar");
        
        quickDialog.add(btnSave, "split 2, align right");
        quickDialog.add(btnCancel);
        
        btnSave.addActionListener(e -> {
            String name = txtName.getText().trim();
            String phone = txtPhone.getText().trim();
            
            if (name.isEmpty() || phone.isEmpty()) {
                JOptionPane.showMessageDialog(quickDialog, 
                    "Nome e telefone são obrigatórios.");
                return;
            }
            
            Contact contact = new Contact(controller.getLoggedUser().getId(), 
                                        name, phone, "", "");
            boolean success = controller.addContact(contact);
            
            if (success) {
                JOptionPane.showMessageDialog(quickDialog, "Contacto adicionado!");
                quickDialog.dispose();
                loadStatistics(); // Atualizar estatísticas
            } else {
                JOptionPane.showMessageDialog(quickDialog, 
                    "Erro ao adicionar contacto.", "Erro", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        btnCancel.addActionListener(e -> quickDialog.dispose());
        
        quickDialog.pack();
        quickDialog.setLocationRelativeTo(this);
        quickDialog.setVisible(true);
    }

    private void quickSearch() {
        String query = JOptionPane.showInputDialog(this, 
            "Digite o nome ou telefone para buscar:");
            
        if (query != null && !query.trim().isEmpty()) {
            Contact result = controller.searchByName(query);
            if (result == null) {
                result = controller.searchByPhone(query);
            }
            
            if (result != null) {
                showContactQuickView(result);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Nenhum contacto encontrado para: " + query);
            }
        }
    }

    private void showContactQuickView(Contact contact) {
        JDialog viewDialog = new JDialog(this, "Detalhes do Contacto", true);
        viewDialog.setLayout(new MigLayout("wrap 2", "[][grow]"));
        
        viewDialog.add(new JLabel("Nome:"));
        viewDialog.add(new JLabel(contact.getName()), "growx, wrap");
        
        viewDialog.add(new JLabel("Telefone:"));
        viewDialog.add(new JLabel(contact.getPhone()), "growx, wrap");
        
        if (contact.getEmail() != null && !contact.getEmail().isEmpty()) {
            viewDialog.add(new JLabel("Email:"));
            viewDialog.add(new JLabel(contact.getEmail()), "growx, wrap");
        }
        
        if (contact.getAddress() != null && !contact.getAddress().isEmpty()) {
            viewDialog.add(new JLabel("Morada:"));
            viewDialog.add(new JLabel(contact.getAddress()), "growx, wrap");
        }
        
        JButton btnEdit = new JButton("Editar");
        JButton btnClose = new JButton("Fechar");
        
        viewDialog.add(btnEdit, "split 2, align right");
        viewDialog.add(btnClose);
        
        btnEdit.addActionListener(e -> {
            new ContactFormScreen(controller, contact).setVisible(true);
            viewDialog.dispose();
            dispose();
        });
        
        btnClose.addActionListener(e -> viewDialog.dispose());
        
        viewDialog.pack();
        viewDialog.setLocationRelativeTo(this);
        viewDialog.setVisible(true);
    }

    // === UTILITÁRIOS ===
    
    private void toggleTheme() {
        try {
            if (UIManager.getLookAndFeel() instanceof FlatLightLaf) {
                FlatDarkLaf.setup();
                JOptionPane.showMessageDialog(this, "Tema escuro ativado!");
            } else {
                FlatLightLaf.setup();
                JOptionPane.showMessageDialog(this, "Tema claro ativado!");
            }
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Erro ao alternar tema: " + ex.getMessage(), "Erro", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void createBackup() {
        JOptionPane.showMessageDialog(this, 
            "Funcionalidade de backup em desenvolvimento.\n" +
            "Use a exportação para criar cópias dos seus contactos.",
            "Backup", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showDetailedStats() {
        List<Contact> contacts = controller.listContactsForLoggedUser();
        
        // Estatísticas simples
        long withEmail = contacts.stream().filter(c -> 
            c.getEmail() != null && !c.getEmail().isEmpty()).count();
        long withAddress = contacts.stream().filter(c -> 
            c.getAddress() != null && !c.getAddress().isEmpty()).count();
        
        String stats = String.format(
            "📊 Estatísticas Detalhadas:\n\n" +
            "• Total de Contactos: %d\n" +
            "• Com Email: %d (%.1f%%)\n" +
            "• Com Morada: %d (%.1f%%)\n" +
            "• Sem Informação Extra: %d",
            contacts.size(),
            withEmail, (withEmail * 100.0 / contacts.size()),
            withAddress, (withAddress * 100.0 / contacts.size()),
            contacts.size() - withEmail - withAddress
        );
        
        JOptionPane.showMessageDialog(this, stats, "Estatísticas", 
                                    JOptionPane.INFORMATION_MESSAGE);
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Deseja realmente sair?",
            "Confirmar Logout",
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            controller.logout();
            new LoginScreen().setVisible(true);
            dispose();
        }
    }
}