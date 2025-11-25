package com.connectme.controller;

import com.connectme.model.dao.ContactDAO;
import com.connectme.model.eda.*;
import com.connectme.model.entities.Contact;
import com.connectme.model.enums.ContactType;

import java.util.List;
import java.util.logging.Logger;

public class ContactController {

    private static final Logger logger = Logger.getLogger(ContactController.class.getName());
    private final ContactDAO contactDAO;
    private ContactLinkedList cachedContacts;
    private ContactBST searchIndex;
    private ContactHashTable phoneIndex;
    private ContactStateManager stateManager;

    public ContactController() {
        this.contactDAO = new ContactDAO();
        this.cachedContacts = new ContactLinkedList();
        this.searchIndex = new ContactBST();
        this.phoneIndex = new ContactHashTable();
        this.stateManager = new ContactStateManager();
        loadCache();
    }

    /**
     * Carrega todos os contatos em cache usando estruturas EDA
     */
    private void loadCache() {
        List<Contact> dbContacts = contactDAO.findAll();
        cachedContacts.clear();

        for (Contact c : dbContacts) {
            cachedContacts.add(c);
            searchIndex.insert(c);
            if (c.getPhone() != null) {
                phoneIndex.put(c.getPhone(), c);
            }
        }

        logger.info("Cache carregado com " + cachedContacts.size() + " contatos");
    }

    /**
     * Recarrega o cache
     */
    public void refreshCache() {
        searchIndex = new ContactBST();
        phoneIndex = new ContactHashTable();
        loadCache();
    }

    /**
     * Retorna lista encadeada de contatos
     */
    public ContactLinkedList listAllAsLinkedList() {
        return cachedContacts;
    }

    /**
     * Retorna array de contatos
     */
    public ContactArrayList listAllAsArrayList() {
        ContactArrayList arrayList = new ContactArrayList();
        ContactLinkedList.ContactIterator it = cachedContacts.iterator();

        while (it.hasNext()) {
            Contact c = it.next();
            if (c != null) {
                arrayList.add(c);
            }
        }

        return arrayList;
    }

    /**
     * Buscar contato por ID
     */
    public Contact findById(int id) {
        return cachedContacts.findById(id);
    }

    /**
     * Busca rápida por nome usando BST
     */
    public Contact searchByNameFast(String name) {
        return searchIndex.search(name);
    }

    /**
     * Busca parcial por nome usando BST
     */
    public ContactArrayList searchPartialName(String partialName) {
        ContactArrayList results = new ContactArrayList();
        List<Contact> found = searchIndex.searchPartial(partialName);

        for (Contact c : found) {
            results.add(c);
        }

        return results;
    }

    /**
     * Busca por telefone usando HashTable
     */
    public Contact searchByPhone(String phone) {
        return phoneIndex.get(phone);
    }

    /**
     * Pesquisar contatos (busca geral com ranking)
     */
    public ContactArrayList search(String query) {
        if (query == null || query.trim().isEmpty()) {
            return listAllAsArrayList();
        }

        return ContactSearchAlgorithm.searchWithRanking(cachedContacts, query);
    }

    /**
     * Filtrar por tipo de contato
     */
    public ContactArrayList filterByType(ContactType type) {
        ContactArrayList results = new ContactArrayList();
        ContactLinkedList.ContactIterator it = cachedContacts.iterator();

        while (it.hasNext()) {
            Contact c = it.next();
            if (c != null && (type == null || c.getType() == type)) {
                results.add(c);
            }
        }

        return results;
    }

    /**
     * Criar novo contato
     */
    public boolean create(Contact contact) {
        if (!isValidContact(contact)) {
            logger.warning("Tentativa de criar contato inválido");
            return false;
        }

        // Salvar estado antes da operação
        stateManager.saveState(cachedContacts, "Adicionar: " + contact.getName());

        if (contactDAO.create(contact)) {
            cachedContacts.add(contact);
            searchIndex.insert(contact);
            if (contact.getPhone() != null) {
                phoneIndex.put(contact.getPhone(), contact);
            }
            stateManager.updateCurrentState(cachedContacts);
            return true;
        }
        return false;
    }

    /**
     * Atualizar contato existente
     */
    public boolean update(Contact contact) {
        if (!isValidContact(contact) || contact.getId() <= 0) {
            logger.warning("Tentativa de atualizar contato inválido");
            return false;
        }

        // Salvar estado antes da operação
        stateManager.saveState(cachedContacts, "Atualizar: " + contact.getName());

        if (contactDAO.update(contact)) {
            refreshCache();
            stateManager.updateCurrentState(cachedContacts);
            return true;
        }
        return false;
    }

    /**
     * Deletar contato
     */
    public boolean delete(int contactId) {
        if (contactId <= 0) return false;

        Contact contact = findById(contactId);
        if (contact == null) return false;

        // Salvar estado antes da operação
        stateManager.saveState(cachedContacts, "Deletar: " + contact.getName());

        if (contactDAO.delete(contactId)) {
            cachedContacts.remove(contactId);
            searchIndex.remove(contactId);
            if (contact.getPhone() != null) {
                phoneIndex.remove(contact.getPhone());
            }
            stateManager.updateCurrentState(cachedContacts);
            return true;
        }
        return false;
    }

    /**
     * Desfazer última operação (Undo)
     */
    public boolean undo() {
        if (!stateManager.canUndo()) {
            return false;
        }

        ContactLinkedList restoredState = stateManager.undo();

        // Sincronizar BD com o estado restaurado
        boolean success = syncDatabaseWithState(restoredState);

        if (success) {
            cachedContacts = restoredState;
            rebuildIndexes();
            return true;
        }

        return false;
    }

    /**
     * Refazer operação desfeita (Redo)
     */
    public boolean redo() {
        if (!stateManager.canRedo()) {
            return false;
        }

        ContactLinkedList restoredState = stateManager.redo();

        // Sincronizar BD com o estado restaurado
        boolean success = syncDatabaseWithState(restoredState);

        if (success) {
            cachedContacts = restoredState;
            rebuildIndexes();
            return true;
        }

        return false;
    }

    /**
     * Verifica se pode desfazer
     */
    public boolean canUndo() {
        return stateManager.canUndo();
    }

    /**
     * Verifica se pode refazer
     */
    public boolean canRedo() {
        return stateManager.canRedo();
    }

    /**
     * Retorna descrição da operação de undo
     */
    public String getUndoDescription() {
        return stateManager.getUndoDescription();
    }

    /**
     * Retorna descrição da operação de redo
     */
    public String getRedoDescription() {
        return stateManager.getRedoDescription();
    }

    /**
     * Sincroniza banco de dados com o estado fornecido
     * Remove contatos que não estão no estado
     * Adiciona/atualiza contatos que estão no estado
     */
    private boolean syncDatabaseWithState(ContactLinkedList targetState) {
        try {
            // 1. Obter todos os contatos atuais do BD
            List<Contact> dbContacts = contactDAO.findAll();
            ContactHashTable dbMap = new ContactHashTable();

            for (Contact c : dbContacts) {
                dbMap.put(String.valueOf(c.getId()), c);
            }

            // 2. Criar mapa do estado alvo
            ContactHashTable targetMap = new ContactHashTable();
            ContactLinkedList.ContactIterator it = targetState.iterator();

            while (it.hasNext()) {
                Contact c = it.next();
                if (c != null) {
                    targetMap.put(String.valueOf(c.getId()), c);
                }
            }

            // 3. Deletar contatos que não estão no estado alvo
            for (Contact dbContact : dbContacts) {
                String id = String.valueOf(dbContact.getId());
                if (targetMap.get(id) == null) {
                    // Contato está no BD mas não no estado alvo - deletar
                    contactDAO.delete(dbContact.getId());
                    logger.info("Undo/Redo: Deletado contato ID " + dbContact.getId());
                }
            }

            // 4. Adicionar/Atualizar contatos do estado alvo
            ContactLinkedList.ContactIterator targetIt = targetState.iterator();
            while (targetIt.hasNext()) {
                Contact targetContact = targetIt.next();
                if (targetContact != null) {
                    String id = String.valueOf(targetContact.getId());
                    Contact dbContact = dbMap.get(id);

                    if (dbContact == null) {
                        // Contato não existe no BD - adicionar
                        contactDAO.create(targetContact);
                        logger.info("Undo/Redo: Adicionado contato " + targetContact.getName());
                    } else {
                        // Contato existe - verificar se precisa atualizar
                        if (needsUpdate(dbContact, targetContact)) {
                            contactDAO.update(targetContact);
                            logger.info("Undo/Redo: Atualizado contato " + targetContact.getName());
                        }
                    }
                }
            }

            return true;

        } catch (Exception e) {
            logger.severe("Erro ao sincronizar BD com estado: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Verifica se um contato precisa ser atualizado
     */
    private boolean needsUpdate(Contact dbContact, Contact targetContact) {
        if (!equals(dbContact.getName(), targetContact.getName())) return true;
        if (!equals(dbContact.getPhone(), targetContact.getPhone())) return true;
        if (!equals(dbContact.getEmail(), targetContact.getEmail())) return true;
        if (!equals(dbContact.getCompany(), targetContact.getCompany())) return true;
        if (!equals(dbContact.getAddress(), targetContact.getAddress())) return true;
        if (!equals(dbContact.getDescription(), targetContact.getDescription())) return true;
        if (dbContact.getType() != targetContact.getType()) return true;
        return false;
    }

    /**
     * Compara duas strings considerando nulos
     */
    private boolean equals(String s1, String s2) {
        if (s1 == null && s2 == null) return true;
        if (s1 == null || s2 == null) return false;
        return s1.equals(s2);
    }

    /**
     * Reconstrói os índices de busca
     */
    private void rebuildIndexes() {
        searchIndex = new ContactBST();
        phoneIndex = new ContactHashTable();

        ContactLinkedList.ContactIterator it = cachedContacts.iterator();
        while (it.hasNext()) {
            Contact c = it.next();
            if (c != null) {
                searchIndex.insert(c);
                if (c.getPhone() != null) {
                    phoneIndex.put(c.getPhone(), c);
                }
            }
        }
    }

    /**
     * Contar total de contatos
     */
    public int countAll() {
        return cachedContacts.size();
    }

    /**
     * Contar contatos por tipo
     */
    public int countByType(ContactType type) {
        int count = 0;
        ContactLinkedList.ContactIterator it = cachedContacts.iterator();

        while (it.hasNext()) {
            Contact c = it.next();
            if (c != null && c.getType() == type) {
                count++;
            }
        }

        return count;
    }

    /**
     * Verificar se telefone já existe
     */
    public boolean phoneExists(String phone) {
        return phone != null && phoneIndex.get(phone) != null;
    }

    /**
     * Verificar se email já existe
     */
    public boolean emailExists(String email) {
        if (email == null || email.isEmpty()) return false;

        ContactLinkedList.ContactIterator it = cachedContacts.iterator();
        while (it.hasNext()) {
            Contact c = it.next();
            if (c != null && email.equalsIgnoreCase(c.getEmail())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Ordenar contatos usando MergeSort
     */
    public ContactLinkedList sortWithMergeSort(ContactSorter.SortField field, ContactSorter.SortOrder order) {
        return ContactSorter.mergeSort(cachedContacts, field, order);
    }

    /**
     * Ordenar contatos usando QuickSort
     */
    public ContactLinkedList sortWithQuickSort(ContactSorter.SortField field, ContactSorter.SortOrder order) {
        return ContactSorter.quickSort(cachedContacts, field, order);
    }

    private boolean isValidContact(Contact contact) {
        if (contact == null) return false;
        if (contact.getName() == null || contact.getName().trim().isEmpty()) return false;
        if (contact.getPhone() == null || contact.getPhone().trim().isEmpty()) return false;
        return true;
    }
}