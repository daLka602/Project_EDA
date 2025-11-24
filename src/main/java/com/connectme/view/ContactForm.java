// src/main/java/com/connectme/view/ContactForm.java
package com.connectme.view;

import com.connectme.controller.ContactController;
import com.connectme.model.entities.Contact;
import com.connectme.model.entities.User;
import com.connectme.model.enums.ContactType;
import com.connectme.view.componet.RoundedFormBorder;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.regex.Pattern;

public class ContactForm extends JDialog {
    private Contact contact;
    private User user;
    private ContactController contactController;
    private Runnable onSaveCallback;

    private JTextField nameField;
    private JTextField companyField;
    private JTextField phoneField;
    private JTextField emailField;
    private JComboBox<ContactType> typeCombo;
    private JTextField addressField;
    private JTextArea descriptionArea;

    public ContactForm(JPanel parent, Contact contact, User user,
                       ContactController controller, Runnable onSave) {
        super(SwingUtilities.getWindowAncestor(parent),
                contact != null ? "Editar Contacto" : "Adicionar Contacto",
                ModalityType.APPLICATION_MODAL);

        this.contact = contact;
        this.user = user;
        this.contactController = controller;
        this.onSaveCallback = onSave;

        setSize(700, 750);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setResizable(false);
        getContentPane().setBackground(Color.WHITE);

        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new MigLayout("fill, insets 30", "[fill]", "[]40[fill]40[]"));
        mainPanel.setBackground(Color.WHITE);

        // Header
        JLabel titleLabel = new JLabel(contact != null ? "Editar Contacto" : "Adicionar Contacto");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(33, 33, 33));
        mainPanel.add(titleLabel, "wrap");

        // Form Panel com MigLayout adequado
        JPanel formPanel = new JPanel(new MigLayout("fill, insets 0", "[fill]", "[]8[]8[]8[]8[]8[]"));
        formPanel.setBackground(Color.WHITE);

        // Nome Completo
        formPanel.add(createLabel("Nome Completo *"), "wrap");
        nameField = createTextField("Ex: Domingos Silva");
        formPanel.add(nameField, "grow, wrap");

        // Empresa
        formPanel.add(createLabel("Empresa"), "wrap");
        companyField = createTextField("Ex: ConnectMe Lda");
        formPanel.add(companyField, "grow, wrap");

        // Telefone e Tipo lado a lado
        formPanel.add(createLabel("Telefone *"));
        formPanel.add(createLabel("Tipo *"), "wrap");

        phoneField = createTextField("+258 84 123 4567");
        formPanel.add(phoneField, "grow, sg 1");

        typeCombo = new JComboBox<>(ContactType.values());
        typeCombo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        typeCombo.setBackground(Color.WHITE);
        typeCombo.setBorder(new RoundedFormBorder(6, new Color(220, 220, 225)));
        typeCombo.setPreferredSize(new Dimension(150, 36));
        typeCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                if (value instanceof ContactType) {
                    switch ((ContactType) value) {
                        case CUSTOMER:
                            value = "Cliente";
                            break;
                        case PARTNER:
                            value = "Parceiro";
                            break;
                        case SUPPLIER:
                            value = "Fornecedor";
                            break;
                    }
                }
                return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }
        });
        formPanel.add(typeCombo, "grow, sg 1, wrap");

        // Email
        formPanel.add(createLabel("Email *"), "wrap");
        emailField = createTextField("exemplo@email.com");
        formPanel.add(emailField, "grow, wrap");

        // Morada
        formPanel.add(createLabel("Morada"), "wrap");
        addressField = createTextField("Ex: Maputo, Mozambique");
        formPanel.add(addressField, "grow, wrap");

        // Notas
        formPanel.add(createLabel("Notas"), "aligny top, wrap");
        descriptionArea = new JTextArea(5, 30);
        descriptionArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setBackground(Color.WHITE);
        descriptionArea.setBorder(new RoundedFormBorder(6, new Color(220, 220, 225)));
        descriptionArea.setMargin(new Insets(10, 10, 10, 10));
        JScrollPane scrollPane = new JScrollPane(descriptionArea);
        scrollPane.setBorder(new RoundedFormBorder(6, new Color(220, 220, 225)));
        scrollPane.setBackground(Color.WHITE);
        scrollPane.getViewport().setBackground(Color.WHITE);
        formPanel.add(scrollPane, "grow, wrap");

        mainPanel.add(formPanel, "grow, wrap");

        // Buttons Panel
        JPanel buttonPanel = new JPanel(new MigLayout("fill", "[fill]15[fill]", "[]"));
        buttonPanel.setBackground(Color.WHITE);

        JButton cancelBtn = new JButton("Cancelar");
        cancelBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cancelBtn.setBackground(new Color(245, 245, 245));
        cancelBtn.setForeground(new Color(100, 100, 100));
        cancelBtn.setBorder(new RoundedFormBorder(6, new Color(220, 220, 225)));
        cancelBtn.setFocusPainted(false);
        cancelBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cancelBtn.setPreferredSize(new Dimension(120, 40));
        cancelBtn.addActionListener(e -> dispose());
        buttonPanel.add(cancelBtn, "grow");

        JButton saveBtn = new JButton(contact != null ? "Atualizar" : "Adicionar Contacto");
        saveBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        saveBtn.setBackground(new Color(33, 150, 243));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setBorder(BorderFactory.createEmptyBorder());
        saveBtn.setFocusPainted(false);
        saveBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        saveBtn.setPreferredSize(new Dimension(140, 40));
        saveBtn.addActionListener(e -> saveContact());
        buttonPanel.add(saveBtn, "grow");

        mainPanel.add(buttonPanel, "grow");

        if (contact != null) {
            populateFields();
        }

        JScrollPane mainScroll = new JScrollPane(mainPanel);
        mainScroll.setBorder(null);
        mainScroll.getViewport().setBackground(Color.WHITE);
        add(mainScroll);
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        label.setForeground(new Color(60, 60, 60));
        return label;
    }

    private JTextField createTextField(String placeholder) {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        field.setText(placeholder);
        field.setForeground(new Color(150, 150, 150));
        field.setBackground(Color.WHITE);
        field.setBorder(new RoundedFormBorder(6, new Color(220, 220, 225)));
        field.setMargin(new Insets(8, 12, 8, 12));
        field.setPreferredSize(new Dimension(100, 36));

        field.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (field.getText().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(new Color(150, 150, 150));
                }
            }
        });

        return field;
    }

    private void populateFields() {
        nameField.setText(contact.getName());
        nameField.setForeground(Color.BLACK);

        if (contact.getCompany() != null && !contact.getCompany().isEmpty()) {
            companyField.setText(contact.getCompany());
            companyField.setForeground(Color.BLACK);
        }

        phoneField.setText(contact.getPhone());
        phoneField.setForeground(Color.BLACK);

        if (contact.getEmail() != null && !contact.getEmail().isEmpty()) {
            emailField.setText(contact.getEmail());
            emailField.setForeground(Color.BLACK);
        }

        typeCombo.setSelectedItem(contact.getType());

        if (contact.getAddress() != null && !contact.getAddress().isEmpty()) {
            addressField.setText(contact.getAddress());
            addressField.setForeground(Color.BLACK);
        }

        if (contact.getDescription() != null && !contact.getDescription().isEmpty()) {
            descriptionArea.setText(contact.getDescription());
            descriptionArea.setForeground(Color.BLACK);
        }
    }

    private boolean validateForm() {
        String name = nameField.getText().trim();
        String phone = phoneField.getText().trim();
        String email = emailField.getText().trim();

        if (name.isEmpty() || name.equals("Ex: Domingos Silva")) {
            JOptionPane.showMessageDialog(this, "Nome é obrigatório", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (phone.isEmpty() || !isValidPhone(phone)) {
            JOptionPane.showMessageDialog(this, "Telefone inválido", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (email.isEmpty() || !isValidEmail(email)) {
            JOptionPane.showMessageDialog(this, "Email inválido", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    private boolean isValidPhone(String phone) {
        return Pattern.matches("^(\\+258)?[0-9]{9}$", phone.replace(" ", ""));
    }

    private boolean isValidEmail(String email) {
        return Pattern.matches("^[A-Za-z0-9+_.-]+@(.+)$", email);
    }

    private void saveContact() {
        if (!validateForm()) return;

        Contact c = contact != null ? contact : new Contact();
        c.setName(nameField.getText().trim());
        c.setCompany(companyField.getText().trim());
        c.setPhone(phoneField.getText().trim());
        c.setEmail(emailField.getText().trim());
        c.setType((ContactType) typeCombo.getSelectedItem());
        c.setAddress(addressField.getText().trim());
        c.setDescription(descriptionArea.getText().trim());

        boolean success = false;
        if (contact == null) {
            c.setCreateDate(LocalDateTime.now());
            success = contactController.create(c);
        } else {
            success = contactController.update(c);
        }

        if (success) {
            JOptionPane.showMessageDialog(this,
                    contact == null ? "Contacto adicionado com sucesso!" : "Contacto atualizado com sucesso!",
                    "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            if (onSaveCallback != null) onSaveCallback.run();
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Erro ao guardar contacto!", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}