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

public class HomepageView extends JFrame {

    private UserController controller;
    private User user;
    private int totalContacts = 0;
    private JLabel lblContactCount;
    private JPanel mainContentPanel;

    public HomepageView(UserController controller) {
        super("ConnectMe - Agenda Telefónica");
        this.controller = controller;
        this.user = controller.getLoggedUser();

        initUI();
        loadStatistics();
    }

    private void initUI() {
        // Configuração principal
        setLayout(new BorderLayout());
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().setBackground(Color.WHITE);

        // Adicionar os painéis principais
        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createMainPanel(), BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createEmptyBorder(30, 30, 20, 30));

        // Painel superior com título
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(Color.WHITE);

        JLabel lblMainTitle = new JLabel("Agenda Telefónica");
        lblMainTitle.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblMainTitle.setForeground(new Color(15, 23, 42));

        JLabel lblSubtitle = new JLabel("Gerir os seus contactos de forma simples e eficiente");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblSubtitle.setForeground(new Color(100, 116, 139));
        lblSubtitle.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

        titlePanel.add(lblMainTitle, BorderLayout.NORTH);
        titlePanel.add(lblSubtitle, BorderLayout.CENTER);

        // Separador
        JSeparator separator = new JSeparator();
        separator.setForeground(new Color(226, 232, 240));
        separator.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        header.add(titlePanel, BorderLayout.NORTH);
        header.add(separator, BorderLayout.SOUTH);

        return header;
    }

    private JPanel createMainPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(0, 30, 30, 30));

        // Painel de conteúdo
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);

        // Adicionar seção de contactos
        contentPanel.add(createContactsSection(), BorderLayout.NORTH);

        // Adicionar área de conteúdo principal
        mainContentPanel = createContentArea();
        contentPanel.add(mainContentPanel, BorderLayout.CENTER);

        // Adicionar rodapé
        contentPanel.add(createFooterPanel(), BorderLayout.SOUTH);

        mainPanel.add(contentPanel, BorderLayout.CENTER);
        return mainPanel;
    }

    private JPanel createContactsSection() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // Painel esquerdo - título e contador
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftPanel.setBackground(Color.WHITE);

        JLabel lblTitle = new JLabel("Meus Contactos");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(new Color(15, 23, 42));

        lblContactCount = new JLabel("0 contactos guardados");
        lblContactCount.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblContactCount.setForeground(new Color(100, 116, 139));
        lblContactCount.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

        leftPanel.add(lblTitle);
        leftPanel.add(lblContactCount);

        // Painel direito - botões
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightPanel.setBackground(Color.WHITE);

        JButton btnList = createTextButton("Listar");
        JButton btnAdd = createTextButton("+ Adicionar");
        JButton btnSearch = createTextButton("Procurar");
        JButton btnExport = createTextButton("Exportar");
        JButton btnImport = createTextButton("Importar");

        btnList.addActionListener(e -> openContactList());
        btnAdd.addActionListener(e -> openAddContact());
        btnSearch.addActionListener(e -> showSearchDialog());
        btnExport.addActionListener(e -> exportContacts());
        btnImport.addActionListener(e -> importContacts());

        rightPanel.add(btnList);
        rightPanel.add(btnAdd);
        rightPanel.add(btnSearch);
        rightPanel.add(btnExport);
        rightPanel.add(btnImport);

        panel.add(leftPanel, BorderLayout.WEST);
        panel.add(rightPanel, BorderLayout.EAST);

        return panel;
    }

    private JPanel createContentArea() {
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);

        // Painel principal com duas colunas
        JPanel mainContent = new JPanel(new GridLayout(1, 2, 20, 0));
        mainContent.setBackground(Color.WHITE);
        mainContent.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        // Coluna esquerda - Ações principais
        mainContent.add(createLeftColumn());

        // Coluna direita - Utilidades e Gestão
        mainContent.add(createRightColumn());

        contentPanel.add(mainContent, BorderLayout.CENTER);
        return contentPanel;
    }

    private JPanel createLeftColumn() {
        JPanel leftColumn = new JPanel(new BorderLayout());
        leftColumn.setBackground(new Color(248, 250, 252));
        leftColumn.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));

        if (totalContacts == 0) {
            leftColumn.add(createEmptyState(), BorderLayout.CENTER);
        } else {
            leftColumn.add(createQuickStats(), BorderLayout.CENTER);
        }

        return leftColumn;
    }

    private JPanel createRightColumn() {
        JPanel rightColumn = new JPanel(new GridLayout(2, 1, 0, 20));
        rightColumn.setBackground(Color.WHITE);

        // Secção de Gestão
        rightColumn.add(createManagementSection());

        // Secção de Utilidades
        rightColumn.add(createUtilitiesSection());

        return rightColumn;
    }

    private JPanel createEmptyState() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(248, 250, 252));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(5, 0, 5, 0);

        JLabel lblMessage = new JLabel("Ainda não tem contactos guardados.");
        lblMessage.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblMessage.setForeground(new Color(100, 116, 139));

        JLabel lblAction = new JLabel("Adicione o seu primeiro contacto!");
        lblAction.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblAction.setForeground(new Color(15, 23, 42));

        JButton btnAddFirst = createPrimaryButton("+ Adicionar Primeiro Contacto");
        btnAddFirst.addActionListener(e -> openAddContact());
        btnAddFirst.setPreferredSize(new Dimension(250, 45));

        panel.add(lblMessage, gbc);
        panel.add(lblAction, gbc);
        panel.add(Box.createVerticalStrut(20), gbc);
        panel.add(btnAddFirst, gbc);

        return panel;
    }

    private JPanel createQuickStats() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(248, 250, 252));

        JPanel statsCard = new JPanel(new BorderLayout());
        statsCard.setBackground(Color.WHITE);
        statsCard.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));
        statsCard.setPreferredSize(new Dimension(300, 150));

        JLabel lblStatsTitle = new JLabel("📊 Resumo");
        lblStatsTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblStatsTitle.setForeground(new Color(15, 23, 42));
        lblStatsTitle.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        JLabel lblTotal = new JLabel("Total: " + totalContacts + " contactos");
        lblTotal.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblTotal.setForeground(new Color(100, 116, 139));
        lblTotal.setBorder(BorderFactory.createEmptyBorder(0, 20, 10, 20));

        JButton btnViewAll = createTextButton("Ver todos →");
        btnViewAll.addActionListener(e -> openContactList());
        btnViewAll.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        statsCard.add(lblStatsTitle, BorderLayout.NORTH);
        statsCard.add(lblTotal, BorderLayout.CENTER);
        statsCard.add(btnViewAll, BorderLayout.SOUTH);

        panel.add(statsCard);

        return panel;
    }

    private JPanel createManagementSection() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel lblTitle = new JLabel("👥 Gestão de Contactos");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(new Color(15, 23, 42));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        JPanel buttonsPanel = new JPanel(new GridLayout(4, 1, 0, 10));
        buttonsPanel.setBackground(Color.WHITE);

        JButton btnListContacts = createManagementButton("📋 Lista de Contactos");
        JButton btnAddContact = createManagementButton("➕ Adicionar Contacto");
        JButton btnAdvancedSearch = createManagementButton("🔎 Busca Avançada");
        JButton btnExportContacts = createManagementButton("📤 Exportar Contactos");

        btnListContacts.addActionListener(e -> openContactList());
        btnAddContact.addActionListener(e -> openAddContact());
        btnAdvancedSearch.addActionListener(e -> openAdvancedSearch());
        btnExportContacts.addActionListener(e -> exportContacts());

        buttonsPanel.add(btnListContacts);
        buttonsPanel.add(btnAddContact);
        buttonsPanel.add(btnAdvancedSearch);
        buttonsPanel.add(btnExportContacts);

        panel.add(lblTitle, BorderLayout.NORTH);
        panel.add(buttonsPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createUtilitiesSection() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel lblTitle = new JLabel("🛠️ Utilidades");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(new Color(15, 23, 42));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        JPanel buttonsPanel = new JPanel(new GridLayout(4, 1, 0, 10));
        buttonsPanel.setBackground(Color.WHITE);

        JButton btnTheme = createUtilityButton("🎨 Alternar Tema");
        JButton btnBackup = createUtilityButton("💾 Backup");
        JButton btnStats = createUtilityButton("📊 Estatísticas Detalhadas");
        JButton btnLogout = createUtilityButton("🚪 Logout");

        btnTheme.addActionListener(e -> toggleTheme());
        btnBackup.addActionListener(e -> createBackup());
        btnStats.addActionListener(e -> showDetailedStats());
        btnLogout.addActionListener(e -> logout());

        buttonsPanel.add(btnTheme);
        buttonsPanel.add(btnBackup);
        buttonsPanel.add(btnStats);
        buttonsPanel.add(btnLogout);

        panel.add(lblTitle, BorderLayout.NORTH);
        panel.add(buttonsPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createFooterPanel() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.LEFT));
        footer.setBackground(Color.WHITE);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(226, 232, 240)));
        footer.setPreferredSize(new Dimension(100, 60));

        JCheckBox checkBox = new JCheckBox();
        checkBox.setSelected(true);
        checkBox.setEnabled(false);

        JLabel lblInfo = new JLabel("Os dados são guardados automaticamente na base de dados");
        lblInfo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblInfo.setForeground(new Color(100, 116, 139));

        footer.add(checkBox);
        footer.add(lblInfo);

        return footer;
    }

    private JButton createTextButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setBackground(Color.WHITE);
        button.setForeground(new Color(59, 130, 246));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setForeground(new Color(37, 99, 235));
                button.setBackground(new Color(239, 246, 255));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setForeground(new Color(59, 130, 246));
                button.setBackground(Color.WHITE);
            }
        });

        return button;
    }

    private JButton createPrimaryButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(new Color(59, 130, 246));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(37, 99, 235));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(59, 130, 246));
            }
        });

        return button;
    }

    private JButton createManagementButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setBackground(Color.WHITE);
        button.setForeground(new Color(15, 23, 42));
        button.setFocusPainted(false);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(248, 250, 252));
                button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(59, 130, 246)),
                        BorderFactory.createEmptyBorder(12, 15, 12, 15)
                ));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(Color.WHITE);
                button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(226, 232, 240)),
                        BorderFactory.createEmptyBorder(12, 15, 12, 15)
                ));
            }
        });

        return button;
    }

    private JButton createUtilityButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setBackground(new Color(248, 250, 252));
        button.setForeground(new Color(15, 23, 42));
        button.setFocusPainted(false);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(239, 246, 255));
                button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(59, 130, 246)),
                        BorderFactory.createEmptyBorder(12, 15, 12, 15)
                ));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(248, 250, 252));
                button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(226, 232, 240)),
                        BorderFactory.createEmptyBorder(12, 15, 12, 15)
                ));
            }
        });

        return button;
    }

    private void loadStatistics() {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                List<Contact> contacts = controller.listContactsForLoggedUser();
                totalContacts = contacts.size();
                return null;
            }

            @Override
            protected void done() {
                updateContactCount();
                updateContentArea();
            }
        };
        worker.execute();
    }

    private void updateContactCount() {
        if (lblContactCount != null) {
            lblContactCount.setText(totalContacts + " contactos guardados");
        }
    }

    private void updateContentArea() {
        if (mainContentPanel != null) {
            mainContentPanel.removeAll();
            mainContentPanel.add(createContentArea(), BorderLayout.CENTER);
            mainContentPanel.revalidate();
            mainContentPanel.repaint();
        }
    }

    // === MÉTODOS DE AÇÃO COMPLETOS ===

    private void openContactList() {
        new ContactListScreen(controller).setVisible(true);
        dispose();
    }

    private void openAddContact() {
        new ContactFormScreen(controller, null).setVisible(true);
        dispose();
    }

    private void openAdvancedSearch() {
        JOptionPane.showMessageDialog(this,
                "Funcionalidade de busca avançada em desenvolvimento",
                "Busca Avançada",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void showSearchDialog() {
        JDialog searchDialog = new JDialog(this, "Procurar Contacto", true);
        searchDialog.setLayout(new BorderLayout());
        searchDialog.setSize(500, 300);
        searchDialog.setLocationRelativeTo(this);
        searchDialog.getContentPane().setBackground(Color.WHITE);

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        contentPanel.setBackground(Color.WHITE);

        JLabel lblTitle = new JLabel("Procurar Contacto");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(new Color(15, 23, 42));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JLabel lblName = new JLabel("Nome a procurar");
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblName.setForeground(new Color(15, 23, 42));

        JTextField txtSearch = new JTextField();
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));

        JLabel lblHint = new JLabel("Digite um nome para procurar contactos");
        lblHint.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblHint.setForeground(new Color(100, 116, 139));
        lblHint.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));

        JButton btnSearch = createPrimaryButton("Procurar");
        JButton btnCancel = createTextButton("Cancelar");

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(btnSearch);
        buttonPanel.add(btnCancel);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(Color.WHITE);

        formPanel.add(lblTitle);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(lblName);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(txtSearch);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(lblHint);
        formPanel.add(Box.createVerticalStrut(20));
        formPanel.add(buttonPanel);

        contentPanel.add(formPanel, BorderLayout.CENTER);
        searchDialog.add(contentPanel, BorderLayout.CENTER);

        btnSearch.addActionListener(e -> {
            String query = txtSearch.getText().trim();
            if (!query.isEmpty()) {
                performSearch(query);
                searchDialog.dispose();
            }
        });

        btnCancel.addActionListener(e -> searchDialog.dispose());
        searchDialog.setVisible(true);
    }

    private void performSearch(String query) {
        List<Contact> results = controller.searchList(query, "", "");

        if (results.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Nenhum contacto encontrado para: " + query,
                    "Resultados",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            ContactListScreen listScreen = new ContactListScreen(controller);
            listScreen.setVisible(true);
            dispose();
        }
    }

    private void exportContacts() {
        String[] options = {"PDF", "TXT"};
        String choice = (String) JOptionPane.showInputDialog(
                this, "Escolha o formato de exportação:", "Exportar Contactos",
                JOptionPane.PLAIN_MESSAGE, null, options, "PDF"
        );

        if (choice != null) {
            JFileChooser chooser = new JFileChooser();
            chooser.setSelectedFile(new java.io.File("contactos_connectme." + choice.toLowerCase()));

            if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                boolean ok = controller.exportContacts(choice, chooser.getSelectedFile());
                if (ok) {
                    JOptionPane.showMessageDialog(this, "Exportação realizada com sucesso!");
                } else {
                    JOptionPane.showMessageDialog(this, "Erro ao exportar contactos.", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void importContacts() {
        JOptionPane.showMessageDialog(this,
                "Funcionalidade de importação em desenvolvimento",
                "Importar Contactos",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void toggleTheme() {
        try {
            if (UIManager.getLookAndFeel() instanceof FlatLightLaf) {
                UIManager.setLookAndFeel(new FlatDarkLaf());
            } else {
                UIManager.setLookAndFeel(new FlatLightLaf());
            }
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao alternar tema.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void createBackup() {
        JOptionPane.showMessageDialog(this,
                "Funcionalidade de backup em desenvolvimento",
                "Backup",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void showDetailedStats() {
        List<Contact> contacts = controller.listContactsForLoggedUser();
        long withEmail = contacts.stream().filter(c -> c.getEmail() != null && !c.getEmail().isEmpty()).count();
        long withAddress = contacts.stream().filter(c -> c.getAddress() != null && !c.getAddress().isEmpty()).count();

        String stats = String.format(
                "📊 Estatísticas Detalhadas:\n\n" +
                        "• Total de Contactos: %d\n" +
                        "• Com Email: %d\n" +
                        "• Com Morada: %d\n" +
                        "• Sem Informação Extra: %d",
                contacts.size(), withEmail, withAddress, (contacts.size() - withEmail - withAddress)
        );

        JOptionPane.showMessageDialog(this, stats, "Estatísticas Detalhadas", JOptionPane.INFORMATION_MESSAGE);
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