package com.connectme.view;

import javax.swing.*;
import net.miginfocom.swing.MigLayout;
import com.connectme.controller.UserController;
import com.connectme.model.entities.Contact;

public class ContactFormScreen extends JFrame {

    private UserController controller;
    private Contact editingContact;

    private JTextField txtName, txtPhone, txtEmail, txtAddress;

    public ContactFormScreen(UserController controller, Contact editingContact) {
        super(editingContact == null ? "Novo Contacto" : "Editar Contacto");

        this.controller = controller;
        this.editingContact = editingContact;

        initUI();
        loadData();
    }

    private void initUI() {
        setLayout(new MigLayout("wrap 2", "[grow][grow]", "[][][][]20[]"));
        setSize(500, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        txtName = new JTextField();
        txtPhone = new JTextField();
        txtEmail = new JTextField();
        txtAddress = new JTextField();

        add(new JLabel("Nome:"));
        add(txtName, "growx");

        add(new JLabel("Telefone:"));
        add(txtPhone, "growx");

        add(new JLabel("Email:"));
        add(txtEmail, "growx");

        add(new JLabel("Morada:"));
        add(txtAddress, "growx");

        JButton btnSave = new JButton("Salvar");
        JButton btnCancel = new JButton("Cancelar");

        add(btnSave, "growx, span 2, split 2");
        add(btnCancel, "growx");

        btnSave.addActionListener(e -> save());
        btnCancel.addActionListener(e -> {
            new ContactListScreen(controller).setVisible(true);
            dispose();
        });
    }

    private void loadData() {
        if (editingContact != null) {
            txtName.setText(editingContact.getName());
            txtPhone.setText(editingContact.getPhone());
            txtEmail.setText(editingContact.getEmail());
            txtAddress.setText(editingContact.getAddress());
        }
    }

    private void save() {
        String name = txtName.getText().trim();
        String phone = txtPhone.getText().trim();
        String email = txtEmail.getText().trim();
        String address = txtAddress.getText().trim();

        if (name.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nome e telefone são obrigatórios.");
            return;
        }

        boolean ok;

        if (editingContact == null) {
            Contact c = new Contact(
                    controller.getLoggedUser().getId(),
                    name,
                    phone,
                    email,
                    address
            );
            ok = controller.addContact(c);
        } else {
            Contact c = new Contact(
                    editingContact.getId(),
                    controller.getLoggedUser().getId(),
                    name,
                    phone,
                    email,
                    address
            );
            ok = controller.updateContact(c);
        }

        if (ok) {
            JOptionPane.showMessageDialog(this, "Guardado com sucesso!");
            new ContactListScreen(controller).setVisible(true);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Erro ao guardar.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
