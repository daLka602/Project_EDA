package com.connectme.view;

import javax.swing.*;
import net.miginfocom.swing.MigLayout;
import com.connectme.controller.UserController;
import com.connectme.model.entities.Contact;
import java.util.regex.Pattern;
import java.awt.*;

public class ContactFormScreen extends JFrame {

    private UserController controller;
    private Contact editingContact;

    private JTextField txtName, txtPhone, txtEmail, txtAddress;
    private JLabel lblNameError, lblPhoneError, lblEmailError;

    // Patterns para validação
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^[+]*[(]{0,1}[0-9]{1,4}[)]{0,1}[-\\s\\./0-9]*$");

    public ContactFormScreen(UserController controller, Contact editingContact) {
        super(editingContact == null ? "Novo Contacto" : "Editar Contacto");

        this.controller = controller;
        this.editingContact = editingContact;

        initUI();
        loadData();
    }

    public ContactFormScreen() {
        initUI();
        loadData();
    }

    private void initUI() {
        // Layout otimizado para 1920x1080
        setLayout(new MigLayout("wrap 2", "[400!][400!]", "[30!][30!][15!][30!][15!][30!][15!][30!][40!]"));
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // Título centralizado
        JLabel lblTitle = new JLabel(editingContact == null ? "NOVO CONTACTO" : "EDITAR CONTACTO");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        add(lblTitle, "span 2, growx, gapbottom 30");

        // Campos com labels de erro - tamanhos otimizados
        txtName = new JTextField(30);
        txtName.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        txtName.setPreferredSize(new Dimension(400, 35));
        lblNameError = createErrorLabel();

        txtPhone = new JTextField(30);
        txtPhone.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        txtPhone.setPreferredSize(new Dimension(400, 35));
        lblPhoneError = createErrorLabel();

        txtEmail = new JTextField(30);
        txtEmail.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        txtEmail.setPreferredSize(new Dimension(400, 35));
        lblEmailError = createErrorLabel();

        txtAddress = new JTextField(30);
        txtAddress.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        txtAddress.setPreferredSize(new Dimension(400, 35));

        // Labels com fonte maior
        JLabel lblName = new JLabel("Nome:*");
        JLabel lblPhone = new JLabel("Telefone:*");
        JLabel lblEmail = new JLabel("Email:");
        JLabel lblAddress = new JLabel("Morada:");

        Font labelFont = new Font("Segoe UI", Font.BOLD, 16);
        lblName.setFont(labelFont);
        lblPhone.setFont(labelFont);
        lblEmail.setFont(labelFont);
        lblAddress.setFont(labelFont);

        // Adicionar componentes ao layout
        add(lblName, "align right");
        add(txtName, "growx");
        add(lblNameError, "span 2, growx, h 15!");

        add(lblPhone, "align right");
        add(txtPhone, "growx");
        add(lblPhoneError, "span 2, growx, h 15!");

        add(lblEmail, "align right");
        add(txtEmail, "growx");
        add(lblEmailError, "span 2, growx, h 15!");

        add(lblAddress, "align right");
        add(txtAddress, "growx, wrap 40");

        // Listeners para validação em tempo real
        setupValidationListeners();

        // Botões maiores e melhor posicionados
        JButton btnSave = new JButton("SALVAR");
        JButton btnCancel = new JButton("CANCELAR");

        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnCancel.setFont(new Font("Segoe UI", Font.BOLD, 16));

        btnSave.setPreferredSize(new Dimension(200, 45));
        btnCancel.setPreferredSize(new Dimension(200, 45));

        btnSave.setBackground(new Color(70, 130, 180));
        btnSave.setForeground(Color.WHITE);
        btnCancel.setBackground(new Color(220, 80, 60));
        btnCancel.setForeground(Color.WHITE);

        // Painel para centralizar botões
        JPanel buttonPanel = new JPanel(new MigLayout("insets 0", "[][100!][]", "[]"));
        buttonPanel.add(btnSave, "w 200!, h 45!");
        buttonPanel.add(Box.createHorizontalStrut(20));
        buttonPanel.add(btnCancel, "w 200!, h 45!");

        add(buttonPanel, "span 2, align center, gaptop 20");

        btnSave.addActionListener(e -> save());
        btnCancel.addActionListener(e -> {
            new ContactListScreen(controller).setVisible(true);
            dispose();
        });
    }

    private JLabel createErrorLabel() {
        JLabel label = new JLabel();
        label.setForeground(Color.RED);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        return label;
    }

    private void setupValidationListeners() {
        // Validação em tempo real do nome
        txtName.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { validateName(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { validateName(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { validateName(); }
        });

        // Validação em tempo real do telefone
        txtPhone.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { validatePhone(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { validatePhone(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { validatePhone(); }
        });

        // Validação em tempo real do email
        txtEmail.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { validateEmail(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { validateEmail(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { validateEmail(); }
        });
    }

    private void validateName() {
        String name = txtName.getText().trim();
        if (name.isEmpty()) {
            lblNameError.setText("Nome é obrigatório");
        } else if (name.length() < 2) {
            lblNameError.setText("Nome deve ter pelo menos 2 caracteres");
        } else {
            lblNameError.setText("");
        }
    }

    private void validatePhone() {
        String phone = txtPhone.getText().trim();
        if (phone.isEmpty()) {
            lblPhoneError.setText("Telefone é obrigatório");
        } else if (!PHONE_PATTERN.matcher(phone).matches()) {
            lblPhoneError.setText("Formato de telefone inválido");
        } else {
            lblPhoneError.setText("");
        }
    }

    private void validateEmail() {
        String email = txtEmail.getText().trim();
        if (!email.isEmpty() && !EMAIL_PATTERN.matcher(email).matches()) {
            lblEmailError.setText("Formato de email inválido");
        } else {
            lblEmailError.setText("");
        }
    }

    private void loadData() {
        if (editingContact != null) {
            txtName.setText(editingContact.getName());
            txtPhone.setText(editingContact.getPhone());
            txtEmail.setText(editingContact.getEmail());
            txtAddress.setText(editingContact.getAddress());

            // Validar dados carregados
            validateName();
            validatePhone();
            validateEmail();
        }
    }

    private boolean isFormValid() {
        validateName();
        validatePhone();
        validateEmail();

        return lblNameError.getText().isEmpty() &&
                lblPhoneError.getText().isEmpty() &&
                lblEmailError.getText().isEmpty();
    }

    private void save() {
        if (!isFormValid()) {
            JOptionPane.showMessageDialog(this,
                    "Por favor, corrija os erros no formulário.",
                    "Erro de Validação",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        String name = txtName.getText().trim();
        String phone = txtPhone.getText().trim();
        String email = txtEmail.getText().trim();
        String address = txtAddress.getText().trim();

        boolean ok;

        if (editingContact == null) {
            Contact c = new Contact(
                    controller.getLoggedUser().getId(),
                    name,
                    phone,
                    email.isEmpty() ? null : email,
                    address.isEmpty() ? null : address
            );
            ok = controller.addContact(c);
        } else {
            Contact c = new Contact(
                    editingContact.getId(),
                    controller.getLoggedUser().getId(),
                    name,
                    phone,
                    email.isEmpty() ? null : email,
                    address.isEmpty() ? null : address
            );
            ok = controller.updateContact(c);
        }

        if (ok) {
            JOptionPane.showMessageDialog(this, "Guardado com sucesso!");
            new ContactListScreen(controller).setVisible(true);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Erro ao guardar. Verifique se o telefone já existe.",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}