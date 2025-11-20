package com.connectme.view;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;

import java.awt.Component;

import javax.swing.*;
import net.miginfocom.swing.MigLayout;
import com.connectme.controller.UserController;
import com.connectme.model.User;

public class DashboardScreen extends JFrame {

    private static final Component btnContacts = null;
    private static final Component btnLogout = null;
    private static final Component btnAdd = null;
    private UserController controller;
    private User user;
    private Component btnExport;

    public DashboardScreen(UserController controller) {
        super("ConnectMe - Dashboard");
        this.controller = controller;
        this.user = controller.getLoggedUser();

        initUI();
    }

    private void initUI() {
        setLayout(new MigLayout("wrap 1", "[300]", "20[]20[]20[]20[]20[]"));
        setSize(420, 350);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JLabel lblWelcome = new JLabel("Bem-vindo, " + user.getUsername());
        lblWelcome.setFont(lblWelcome.getFont().deriveFont(20f));
        add(lblWelcome, "align center");
        
    JButton btnTheme = new JButton("Alternar Tema");
add(btnContacts, "growx");
add(btnAdd, "growx");
add(btnExport, "growx");
add(btnTheme, "growx");
add(btnLogout, "growx");

btnTheme.addActionListener(e -> toggleTheme());


        JButton btnContacts = new JButton("Contactos");
        JButton btnAdd = new JButton("Adicionar Contacto");
        JButton btnExport = new JButton("Exportar Contactos");
        JButton btnLogout = new JButton("Logout");

        add(btnContacts, "growx");
        add(btnAdd, "growx");
        add(btnExport, "growx");
        add(btnLogout, "growx");

        btnContacts.addActionListener(e -> {
            new ContactListScreen(controller).setVisible(true);
            dispose();
        });

        btnAdd.addActionListener(e -> {
            new ContactFormScreen(controller, null).setVisible(true);
            dispose();
        });

        btnExport.addActionListener(e -> exportContacts());

        btnLogout.addActionListener(e -> {
            controller.logout();
            new LoginScreen().setVisible(true);
            dispose();
        });
    }
    
    private void toggleTheme() {
    try {
        // simples toggle: verifica se FlatLightLaf é o LookAndFeel atual
        if (UIManager.getLookAndFeel() instanceof com.formdev.flatlaf.FlatLightLaf) {
            FlatDarkLaf.setup();
        } else {
            FlatLightLaf.setup();
        }
        // refresh all windows
        SwingUtilities.updateComponentTreeUI(this);
    } catch (Exception ex) {
        ex.printStackTrace();
    }
}


    private void exportContacts() {
        String[] options = {"PDF", "TXT"};
        String choice = (String) JOptionPane.showInputDialog(
                this,
                "Escolha o formato",
                "Exportar Contactos",
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                "PDF"
        );

        if (choice == null) return;

        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new java.io.File("contactos." + choice.toLowerCase()));

        int result = chooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            boolean ok = controller.exportContacts(choice, chooser.getSelectedFile());
            if (ok) JOptionPane.showMessageDialog(this, "Exportado com sucesso!");
            else JOptionPane.showMessageDialog(this, "Erro ao exportar.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
