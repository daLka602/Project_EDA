package com.connectme.controller;

import com.connectme.model.entities.User;
import com.connectme.model.entities.Contact;
import com.connectme.model.service.AuthService;
import com.connectme.model.dao.ContactDAO;
import com.connectme.model.eda.ContactLinkedList;
import com.connectme.model.eda.ContactBST;
import com.connectme.model.eda.ContactHashTable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.connectme.model.util.PdfUtil;

/**
 * Controller que orquestra Auth, ContactDAO e estruturas EDA em memória.
 */
public class UserController {

    private final AuthService authService;
    private final ContactDAO contactDAO;
    private User loggedUser;

    // EDA structures
    private ContactLinkedList linkedList;
    private ContactBST bst;
    private ContactHashTable hashTable;
    private List<Contact> cachedList;
    //private UserDAO userDAO;

    public UserController() {
        //userDAO = new
        this.authService = new AuthService();
        this.contactDAO = new ContactDAO();
    }

    public boolean login(String username, String plainPassword) {
        boolean ok = authService.login(username, plainPassword);
        if (ok) {
            this.loggedUser = authService.findUser(username);
            // load EDA structures after successful login
            loadEdaStructures();
        }
        return ok;
    }

    public void logout() {
        this.loggedUser = null;
        this.cachedList = null;
        this.linkedList = null;
        this.bst = null;
        this.hashTable = null;
    }

    public boolean isAuthenticated() {
        return this.loggedUser != null;
    }

    public User getLoggedUser() {
        return this.loggedUser;
    }

    /* ===========================
       CRUD CONTACTS (delegates)
       =========================== */
    public boolean addContact(Contact c) {
        if (!isAuthenticated()) return false;
        c = ensureUserId(c);
        boolean ok = contactDAO.create(c);
        if (ok) loadEdaStructures(); // refresh in-memory structures
        return ok;
    }

    public boolean updateContact(Contact c) {
        if (!isAuthenticated()) return false;
        c = ensureUserId(c);
        boolean ok = contactDAO.update(c);
        if (ok) loadEdaStructures();
        return ok;
    }

    public boolean deleteContact(int contactId) {
        if (!isAuthenticated()) return false;
        boolean ok = contactDAO.delete(contactId);
        if (ok) loadEdaStructures();
        return ok;
    }

    public List<Contact> listContactsForLoggedUser() {
        if (!isAuthenticated()) return List.of();
        if (cachedList == null) loadEdaStructures();
        return new ArrayList<>(cachedList); // defensive copy
    }

    public Contact findContactById(int id) {
        if (!isAuthenticated()) return null;
        return contactDAO.findById(id);
    }

    /* ===========================
       EDA Integration
       - load all contacts into LinkedList, BST and HashTable
       - expose search methods using those structures
       =========================== */

    public void loadEdaStructures() {
        if (!isAuthenticated()) return;
        List<Contact> list = contactDAO.findAllByUser(this.loggedUser.getId());
        this.cachedList = new ArrayList<>(list);

        // LinkedList
        this.linkedList = new ContactLinkedList();
        for (Contact c : list) linkedList.add(c);

        // BST (by name)
        this.bst = new ContactBST();
        for (Contact c : list) bst.insert(c);

        // HashTable (by phone)
        this.hashTable = new ContactHashTable();
        for (Contact c : list) {
            if (c.getPhone() != null && !c.getPhone().isBlank())
                hashTable.put(c.getPhone(), c);
        }
    }

    /**
     * Pesquisa por nome usando BST (first exact match), fallback to linear.
     */
    public Contact searchByName(String name) {
        if (!isAuthenticated() || name == null) return null;
        // use BST search (case-insensitive)
        Contact found = bst != null ? bst.search(name) : null;
        if (found != null) return found;

        // fallback linear scan on cachedList (partial matches)
        for (Contact c : cachedList) {
            if (c.getName() != null && c.getName().toLowerCase().contains(name.toLowerCase())) {
                return c;
            }
        }
        return null;
    }

    /**
     * Pesquisa por telefone usando HashTable (O(1) average).
     * Falls back to linear partial search.
     */
    public Contact searchByPhone(String phone) {
        if (!isAuthenticated() || phone == null) return null;
        Contact found = hashTable != null ? hashTable.get(phone) : null;
        if (found != null) return found;

        // fallback: partial match
        for (Contact c : cachedList) {
            if (c.getPhone() != null && c.getPhone().contains(phone)) return c;
        }
        return null;
    }

    /**
     * Retorna a lista filtrada por nome (partial) ou telefone (partial),
     * ou toda lista se ambos vazios. Usa cachedList + streaming sort.
     */
    public List<Contact> searchList(String nameQuery, String phoneQuery, String sortOrder) {
        if (!isAuthenticated()) return List.of();
        List<Contact> result = new ArrayList<>();

        for (Contact c : cachedList) {
            boolean matches = true;
            if (nameQuery != null && !nameQuery.isBlank()) {
                matches &= c.getName() != null && c.getName().toLowerCase().contains(nameQuery.toLowerCase());
            }
            if (phoneQuery != null && !phoneQuery.isBlank()) {
                matches &= c.getPhone() != null && c.getPhone().contains(phoneQuery);
            }
            if (matches) result.add(c);
        }

        // sort if requested
        if ("name_asc".equalsIgnoreCase(sortOrder)) {
            result.sort(Comparator.comparing(Contact::getName, String.CASE_INSENSITIVE_ORDER));
        } else if ("name_desc".equalsIgnoreCase(sortOrder)) {
            result.sort(Comparator.comparing(Contact::getName, String.CASE_INSENSITIVE_ORDER).reversed());
        }

        return result;
    }

    /* ===========================
       Export / util
       =========================== */
    public boolean exportContacts(String format, File destFile) {
        if (!isAuthenticated()) return false;
        List<Contact> list = listContactsForLoggedUser();
        try {
            if ("pdf".equalsIgnoreCase(format)) {
                PdfUtil.exportContactsToPdf(list, destFile);
            } else if ("txt".equalsIgnoreCase(format)) {
                PdfUtil.exportContactsToTxt(list, destFile);
            } else {
                throw new IllegalArgumentException("Formato não suportado: " + format);
            }
            return true;
        } catch (IOException e) {
            System.err.println("Erro ao exportar contactos: " + e.getMessage());
            return false;
        }
    }
    
// inside UserController
public boolean exportContactsList(String format, File destFile, List<Contact> contacts) {
    if (!isAuthenticated()) return false;
    try {
        if ("pdf".equalsIgnoreCase(format)) {
            PdfUtil.exportContactsToPdf(contacts, destFile);
        } else if ("txt".equalsIgnoreCase(format)) {
            PdfUtil.exportContactsToTxt(contacts, destFile);
        } else {
            throw new IllegalArgumentException("Formato não suportado: " + format);
        }
        return true;
    } catch (IOException e) {
        System.err.println("Erro ao exportar contactos: " + e.getMessage());
        return false;
    }
}


    private Contact ensureUserId(Contact c) {
        if (c.getUserId() == 0 && isAuthenticated()) {
            return new Contact(
                c.getUserId() == 0 ? this.loggedUser.getId() : c.getUserId(),
                c.getName(),
                c.getPhone(),
                c.getEmail(),
                c.getAddress()
            );
        }
        return c;
    }
}
