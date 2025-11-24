package com.connectme.controller;

import com.connectme.model.dao.ContactDAO;
import com.connectme.model.entities.Contact;
import com.connectme.model.enums.ContactType;

import java.util.List;
import java.util.logging.Logger;

public class ContactController {

    private static final Logger logger = Logger.getLogger(ContactController.class.getName());
    private final ContactDAO contactDAO;

    public ContactController() {
        this.contactDAO = new ContactDAO();
    }

    /**
     * Listar todos os contactos
     */
    public List<Contact> listAll() {
        return contactDAO.findAll();
    }

    /**
     * Buscar contacto por ID
     */
    public Contact findById(int id) {
        return contactDAO.findById(id);
    }

    /**
     * Pesquisar contactos com múltiplos critérios
     */
    public List<Contact> search(String query) {
        if (query == null || query.trim().isEmpty()) {
            return listAll();
        }
        return contactDAO.search(query.trim());
    }

    /**
     * Filtrar por tipo de contacto
     */
    public List<Contact> filterByType(ContactType type) {
        if (type == null) {
            return listAll();
        }
        return contactDAO.findByType(type);
    }

    /**
     * Criar novo contacto
     */
    public boolean create(Contact contact) {
        if (!isValidContact(contact)) {
            logger.warning("Tentativa de criar contacto inválido");
            return false;
        }
        return contactDAO.create(contact);
    }

    /**
     * Atualizar contacto existente
     */
    public boolean update(Contact contact) {
        if (!isValidContact(contact) || contact.getId() <= 0) {
            logger.warning("Tentativa de atualizar contacto inválido");
            return false;
        }
        return contactDAO.update(contact);
    }

    /**
     * Deletar contacto
     */
    public boolean delete(int contactId) {
        if (contactId <= 0) return false;
        return contactDAO.delete(contactId);
    }

    /**
     * Contar total de contactos
     */
    public int countAll() {
        return contactDAO.countAll();
    }

    /**
     * Contar contactos por tipo
     */
    public long countByType(ContactType type) {
        return contactDAO.findByType(type).size();
    }

    /**
     * Verificar se telefone já existe
     */
    public boolean phoneExists(String phone) {
        if (phone == null || phone.isEmpty()) return false;
        List<Contact> results = contactDAO.search(phone);
        return results.stream()
                .anyMatch(c -> c.getPhone().equals(phone));
    }

    /**
     * Verificar se email já existe
     */
    public boolean emailExists(String email) {
        if (email == null || email.isEmpty()) return false;
        List<Contact> results = contactDAO.search(email);
        return results.stream()
                .anyMatch(c -> email.equalsIgnoreCase(c.getEmail()));
    }

    /**
     * Paginação de contactos
     */
    public List<Contact> getPaginated(int page, int pageSize) {
        return contactDAO.findPaginated(page, pageSize);
    }

    private boolean isValidContact(Contact contact) {
        if (contact == null) return false;
        if (contact.getName() == null || contact.getName().trim().isEmpty()) return false;
        if (contact.getPhone() == null || contact.getPhone().trim().isEmpty()) return false;
        return true;
    }
}
