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
import java.util.logging.Logger;

import com.connectme.model.util.PdfUtil;

public class UserController {

    private static final Logger logger = Logger.getLogger(UserController.class.getName());
    
    private final AuthService authService;
    private final ContactDAO contactDAO;
    private User loggedUser;

    // EDA structures
    private ContactLinkedList linkedList;
    private ContactBST bst;
    private ContactHashTable hashTable;
    private List<Contact> cachedList;

    public UserController() {
        this.authService = new AuthService();
        this.contactDAO = new ContactDAO();
    }

    // ===== MÉTODOS DE AUTENTICAÇÃO =====
    
    public boolean login(String username, String plainPassword) {
        boolean ok = authService.login(username, plainPassword);
        if (ok) {
            this.loggedUser = authService.findUser(username);
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

    // ===== CRUD CONTACTS =====
    
    public boolean addContact(Contact c) {
        if (!isAuthenticated() || !isValidContact(c)) return false;
        c = ensureUserId(c);
        
        boolean ok = contactDAO.create(c);
        if (ok && cachedList != null) {
            // Atualização incremental
            cachedList.add(c);
            if (linkedList != null) linkedList.add(c);
            if (bst != null) bst.insert(c);
            if (hashTable != null && c.getPhone() != null && !c.getPhone().isBlank()) {
                hashTable.put(c.getPhone(), c);
            }
        }
        return ok;
    }

    public boolean updateContact(Contact c) {
        if (!isAuthenticated() || !isValidContact(c)) return false;
        c = ensureUserId(c);
        
        boolean ok = contactDAO.update(c);
        if (ok && cachedList != null) {
            loadEdaStructures(); // Recarrega para simplificar
        }
        return ok;
    }

    public boolean deleteContact(int contactId) {
        if (!isAuthenticated()) return false;
        
        boolean ok = contactDAO.delete(contactId);
        if (ok && cachedList != null) {
            // Atualização incremental
            cachedList.removeIf(contact -> contact.getId() == contactId);
            if (linkedList != null) linkedList.remove(contactId);
            if (bst != null) bst.remove(contactId);
            if (hashTable != null) hashTable.removeByContactId(contactId);
        }
        return ok;
    }

    public List<Contact> listContactsForLoggedUser() {
        if (!isAuthenticated()) return List.of();
        if (cachedList == null) loadEdaStructures();
        return new ArrayList<>(cachedList);
    }

    public Contact findContactById(int id) {
        if (!isAuthenticated()) return null;
        return contactDAO.findById(id);
    }

    // ===== EDA INTEGRATION =====
    
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

    public Contact searchByName(String name) {
        if (!isAuthenticated() || name == null || name.isBlank()) return null;
        
        // Busca exata na BST
        Contact found = bst != null ? bst.search(name) : null;
        if (found != null) return found;

        // Busca parcial no cachedList
        for (Contact c : cachedList) {
            if (c.getName() != null && c.getName().toLowerCase().contains(name.toLowerCase())) {
                return c;
            }
        }
        return null;
    }

    public Contact searchByPhone(String phone) {
        if (!isAuthenticated() || phone == null || phone.isBlank()) return null;
        
        Contact found = hashTable != null ? hashTable.get(phone) : null;
        if (found != null) return found;

        // fallback: partial match
        for (Contact c : cachedList) {
            if (c.getPhone() != null && c.getPhone().contains(phone)) return c;
        }
        return null;
    }

    public List<Contact> searchList(String nameQuery, String phoneQuery, String sortOrder) {
        if (!isAuthenticated()) return List.of();
        List<Contact> result = new ArrayList<>();

        for (Contact c : cachedList) {
            boolean matches = true;
            if (nameQuery != null && !nameQuery.isBlank()) {
                matches &= c.getName() != null && 
                          c.getName().toLowerCase().contains(nameQuery.toLowerCase());
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

    // ===== NOVOS MÉTODOS AVANÇADOS =====

    /**
     * Estatísticas do usuário para o Dashboard
     */
    public UserStats getUserStats() {
        if (!isAuthenticated()) {
            return new UserStats(0, 0, 0, 0);
        }

        List<Contact> contacts = listContactsForLoggedUser();
        long withEmail = contacts.stream()
            .filter(c -> c.getEmail() != null && !c.getEmail().isEmpty())
            .count();
        long withAddress = contacts.stream()
            .filter(c -> c.getAddress() != null && !c.getAddress().isEmpty())
            .count();
        long completeProfiles = contacts.stream()
            .filter(c -> (c.getEmail() != null && !c.getEmail().isEmpty()) && 
                        (c.getAddress() != null && !c.getAddress().isEmpty()))
            .count();

        return new UserStats(
            contacts.size(), 
            (int) withEmail, 
            (int) withAddress, 
            (int) completeProfiles
        );
    }

    /**
     * Buscar contactos recentes (últimos 10)
     */
    public List<Contact> getRecentContacts(int limit) {
        if (!isAuthenticated()) return List.of();
        
        List<Contact> contacts = listContactsForLoggedUser();
        return contacts.stream()
            .sorted(Comparator.comparing(Contact::getId).reversed())
            .limit(limit)
            .toList();
    }

    /**
     * Busca avançada com múltiplos critérios
     */
    public List<Contact> advancedSearch(String name, String phone, String email, String address) {
        if (!isAuthenticated()) return List.of();
        
        List<Contact> results = new ArrayList<>();
        for (Contact c : listContactsForLoggedUser()) {
            boolean matches = true;
            
            if (name != null && !name.isBlank()) {
                matches &= c.getName().toLowerCase().contains(name.toLowerCase());
            }
            if (phone != null && !phone.isBlank()) {
                matches &= c.getPhone().contains(phone);
            }
            if (email != null && !email.isBlank()) {
                matches &= c.getEmail() != null && 
                          c.getEmail().toLowerCase().contains(email.toLowerCase());
            }
            if (address != null && !address.isBlank()) {
                matches &= c.getAddress() != null && 
                          c.getAddress().toLowerCase().contains(address.toLowerCase());
            }
            
            if (matches) results.add(c);
        }
        return results;
    }

    /**
     * Validar se telefone já existe
     */
    public boolean phoneExists(String phone) {
        if (!isAuthenticated() || phone == null) return false;
        return searchByPhone(phone) != null;
    }

    /**
     * Validar se email já existe
     */
    public boolean emailExists(String email) {
        if (!isAuthenticated() || email == null) return false;
        
        for (Contact c : listContactsForLoggedUser()) {
            if (email.equalsIgnoreCase(c.getEmail())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Getter para AuthService (necessário para registro)
     */
    public AuthService getAuthService() {
        return this.authService;
    }

    /**
     * Backup de contactos (múltiplos formatos)
     */
    public boolean backupContacts(File backupDir, String... formats) {
        if (!isAuthenticated() || backupDir == null) return false;
        
        if (!backupDir.exists() && !backupDir.mkdirs()) {
            logger.severe("Não foi possível criar diretório de backup: " + backupDir);
            return false;
        }

        List<Contact> contacts = listContactsForLoggedUser();
        boolean allSuccess = true;

        for (String format : formats) {
            try {
                File backupFile = new File(backupDir, 
                    "backup_contacts_" + System.currentTimeMillis() + "." + format.toLowerCase());
                
                switch (format.toUpperCase()) {
                    case "PDF":
                        PdfUtil.exportContactsToPdf(contacts, backupFile);
                        break;
                    case "TXT":
                        PdfUtil.exportContactsToTxt(contacts, backupFile);
                        break;
                    case "HTML":
                        PdfUtil.exportContactsToHtml(contacts, backupFile);
                        break;
                    default:
                        logger.warning("Formato de backup não suportado: " + format);
                        allSuccess = false;
                        continue;
                }
                
                logger.info("Backup criado: " + backupFile.getAbsolutePath());
                
            } catch (IOException e) {
                logger.severe("Erro ao criar backup " + format + ": " + e.getMessage());
                allSuccess = false;
            }
        }
        
        return allSuccess;
    }

    /**
     * Limpar cache EDA (para testes/debug)
     */
    public void clearCache() {
        this.cachedList = null;
        this.linkedList = null;
        this.bst = null;
        this.hashTable = null;
        logger.info("Cache EDA limpo");
    }

    /**
     * Informações de debug do sistema
     */
    public DebugInfo getDebugInfo() {
        if (!isAuthenticated()) return new DebugInfo();
        
        return new DebugInfo(
            cachedList != null ? cachedList.size() : 0,
            linkedList != null ? linkedList.size() : 0,
            bst != null ? "Ativa" : "Inativa",
            hashTable != null ? "Ativa" : "Inativa"
        );
    }

    // ===== MÉTODOS DE EXPORTAÇÃO =====
    
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
    
    public boolean exportContactsList(String format, File destFile, List<Contact> contacts) {
        if (!isAuthenticated()) return false;
        try {
            if ("pdf".equalsIgnoreCase(format)) {
                PdfUtil.exportContactsToPdf(contacts, destFile);
            } else if ("txt".equalsIgnoreCase(format)) {
                PdfUtil.exportContactsToTxt(contacts, destFile);
            } else if ("html".equalsIgnoreCase(format)) {
                PdfUtil.exportContactsToHtml(contacts, destFile);
            } else {
                throw new IllegalArgumentException("Formato não suportado: " + format);
            }
            return true;
        } catch (IOException e) {
            System.err.println("Erro ao exportar contactos: " + e.getMessage());
            return false;
        }
    }

    // ===== MÉTODOS PRIVADOS =====
    
    private boolean isValidContact(Contact c) {
        if (c == null) return false;
        if (c.getName() == null || c.getName().trim().isEmpty()) return false;
        if (c.getPhone() == null || c.getPhone().trim().isEmpty()) return false;
        return true;
    }

    private Contact ensureUserId(Contact c) {
        if (c.getUserId() == 0 && isAuthenticated()) {
            return new Contact(
                c.getId(),
                this.loggedUser.getId(),
                c.getName(),
                c.getPhone(),
                c.getEmail(),
                c.getAddress()
            );
        }
        return c;
    }

    // ===== CLASSES INTERNAS =====

    /**
     * Classe para estatísticas do usuário
     */
    public static class UserStats {
        public final int totalContacts;
        public final int contactsWithEmail;
        public final int contactsWithAddress;
        public final int completeProfiles;

        public UserStats(int totalContacts, int contactsWithEmail, 
                        int contactsWithAddress, int completeProfiles) {
            this.totalContacts = totalContacts;
            this.contactsWithEmail = contactsWithEmail;
            this.contactsWithAddress = contactsWithAddress;
            this.completeProfiles = completeProfiles;
        }

        public double getEmailPercentage() {
            return totalContacts > 0 ? (contactsWithEmail * 100.0 / totalContacts) : 0;
        }

        public double getAddressPercentage() {
            return totalContacts > 0 ? (contactsWithAddress * 100.0 / totalContacts) : 0;
        }

        public double getCompletePercentage() {
            return totalContacts > 0 ? (completeProfiles * 100.0 / totalContacts) : 0;
        }
    }

    /**
     * Classe para informações de debug
     */
    public static class DebugInfo {
        public final int cachedContacts;
        public final int linkedListSize;
        public final String bstStatus;
        public final String hashTableStatus;

        public DebugInfo() {
            this(0, 0, "Inativa", "Inativa");
        }

        public DebugInfo(int cachedContacts, int linkedListSize, 
                        String bstStatus, String hashTableStatus) {
            this.cachedContacts = cachedContacts;
            this.linkedListSize = linkedListSize;
            this.bstStatus = bstStatus;
            this.hashTableStatus = hashTableStatus;
        }

        @Override
        public String toString() {
            return String.format(
                "DebugInfo[cached=%d, linkedList=%d, bst=%s, hashTable=%s]",
                cachedContacts, linkedListSize, bstStatus, hashTableStatus
            );
        }
    }
}