package com.connectme.controller;

import com.connectme.model.dao.ContactDAO;
import com.connectme.model.eda.*;
import com.connectme.model.entities.Contact;
import com.connectme.model.enums.ContactType;
import com.connectme.model.enums.SortOrder;
import com.connectme.model.util.ContactStateManager;
import com.connectme.model.eda.componets.Comparators;
import com.connectme.model.util.StringUtils;

import java.util.Comparator;
import java.util.function.Predicate;

public class ContactController {

    private final ContactDAO contactDAO;
    private GenericLinkedList<Contact> cachedContacts;
    private GenericBST<Contact> searchIndex;
    private GenericHashTable<Contact> phoneIndex;
    private ContactStateManager stateManager;

    public ContactController() {
        this.contactDAO = new ContactDAO();
        this.cachedContacts = new GenericLinkedList<>();
        this.searchIndex = new GenericBST<>(Contact::getName);
        this.phoneIndex = new GenericHashTable<>(Contact::getPhone);
        this.stateManager = new ContactStateManager();
        loadCache();
    }

    private void loadCache() {
        java.util.List<Contact> dbContacts = contactDAO.findAll();
        cachedContacts.clear();

        for (Contact c : dbContacts) {
            cachedContacts.add(c);
            searchIndex.insert(c);
            if (c.getPhone() != null) {
                phoneIndex.put(c);
            }
        }

        stateManager.setCurrentState(cachedContacts);
    }

    public GenericLinkedList<Contact> listAllAsLinkedList() {
        return cachedContacts;
    }

    public GenericArrayList<Contact> listAllAsArrayList() {
        GenericArrayList<Contact> arrayList = new GenericArrayList<>();
        GenericLinkedList.Iterator<Contact> it = cachedContacts.iterator();

        while (it.hasNext()) {
            Contact c = it.next();
            if (c != null) {
                arrayList.add(c);
            }
        }

        return arrayList;
    }

    public Contact findById(int id) {
        return LinearSearch.search(cachedContacts, c -> c != null && c.getId() == id);
    }

    public Contact searchByNameFast(String name) {
        return searchIndex.search(name);
    }

    public GenericArrayList<Contact> searchPartialName(String partialName) {
        return searchIndex.searchPartial(partialName);
    }

    public GenericArrayList<Contact> search(String query) {
        if (query == null || query.trim().isEmpty()) {
            return listAllAsArrayList();
        }

        final String finalQuery = StringUtils.normalize(query);

        // Usar LinearSearch genérico
        GenericLinkedList<Contact> results = LinearSearch.searchAll(cachedContacts,
                contact -> contact != null && matchesContact(contact, finalQuery));

        return linkedListToArrayList(results);
    }

    private boolean matchesContact(Contact contact, String normalizedQuery) {
        // Busca no nome
        if (contact.getName() != null &&
                StringUtils.containsIgnoreCase(contact.getName(), normalizedQuery)) {
            return true;
        }

        // Busca no telefone
        if (contact.getPhone() != null &&
                StringUtils.cleanPhoneNumber(contact.getPhone()).contains(normalizedQuery)) {
            return true;
        }

        // Busca no email
        if (contact.getEmail() != null &&
                StringUtils.containsIgnoreCase(contact.getEmail(), normalizedQuery)) {
            return true;
        }

        // Busca na empresa
        if (contact.getCompany() != null &&
                StringUtils.containsIgnoreCase(contact.getCompany(), normalizedQuery)) {
            return true;
        }

        return false;
    }

    public GenericArrayList<Contact> filterByType(ContactType type) {
        Predicate<Contact> predicate = type == null ?
                c -> c != null :
                c -> c != null && c.getType() == type;

        GenericLinkedList<Contact> results = LinearSearch.searchAll(cachedContacts, predicate);
        return linkedListToArrayList(results);
    }

    public boolean create(Contact contact) {
        if (!isValidContact(contact)) return false;

        stateManager.saveState(cachedContacts, "Adicionar: " + contact.getName());

        if (contactDAO.create(contact)) {
            cachedContacts.add(contact);
            searchIndex.insert(contact);
            if (contact.getPhone() != null) {
                phoneIndex.put(contact);
            }
            stateManager.setCurrentState(cachedContacts);
            return true;
        }

        cachedContacts = stateManager.getCurrentState();
        return false;
    }

    public boolean update(Contact contact) {
        if (!isValidContact(contact) || contact.getId() <= 0) return false;

        stateManager.saveState(cachedContacts, "Atualizar: " + contact.getName());

        if (contactDAO.update(contact)) {
            refreshCache();
            stateManager.setCurrentState(cachedContacts);
            return true;
        }

        cachedContacts = stateManager.getCurrentState();
        return false;
    }

    public boolean delete(int contactId) {
        if (contactId <= 0) return false;

        Contact contact = findById(contactId);
        if (contact == null) return false;

        stateManager.saveState(cachedContacts, "Deletar: " + contact.getName());

        if (contactDAO.delete(contactId)) {
            cachedContacts.remove(contact);
            rebuildIndexes();
            stateManager.setCurrentState(cachedContacts);
            return true;
        }

        cachedContacts = stateManager.getCurrentState();
        return false;
    }

    public boolean undo() {
        if (!stateManager.canUndo()) {
            return false;
        }

        GenericLinkedList<Contact> restoredState = stateManager.undo();
        boolean success = syncDatabaseWithState(restoredState);

        if (success) {
            cachedContacts = restoredState;
            rebuildIndexes();
            return true;
        }

        return false;
    }

    public boolean redo() {
        if (!stateManager.canRedo()) {
            return false;
        }

        GenericLinkedList<Contact> restoredState = stateManager.redo();
        boolean success = syncDatabaseWithState(restoredState);

        if (success) {
            cachedContacts = restoredState;
            rebuildIndexes();
            return true;
        }

        return false;
    }

    public boolean canUndo() {
        return stateManager.canUndo();
    }

    public boolean canRedo() {
        return stateManager.canRedo();
    }

    public String getUndoDescription() {
        return stateManager.getUndoDescription();
    }

    public String getRedoDescription() {
        return stateManager.getRedoDescription();
    }

    /**
     * Ordenar contatos usando MergeSort genérico
     */
    public GenericLinkedList<Contact> sortWithMergeSort() {
        // Usar apenas MergeSort genérico
        return MergeSort.sort(cachedContacts, Comparators.stringComparator(Contact::getName));
    }

    /**
     * Ordenar contatos usando MergeSort genérico
     * CORREÇÃO: Sempre trabalhar com cópia da lista
     */
    public GenericLinkedList<Contact> sortWithMergeSort(SortOrder order) {
        // IMPORTANTE: Não modificar cachedContacts, trabalhar com cópia
        GenericLinkedList<Contact> listToSort = cloneContactList(cachedContacts);
        return MergeSort.sort(listToSort, Comparators.stringComparator(Contact::getName), order);
    }

    /**
     * Ordenar por campo específico usando MergeSort
     */
    public GenericLinkedList<Contact> sortWithMergeSort(String field, SortOrder order) {
        // IMPORTANTE: Não modificar cachedContacts, trabalhar com cópia
        GenericLinkedList<Contact> listToSort = cloneContactList(cachedContacts);
        Comparator<Contact> comparator = getComparatorForField(field);
        return MergeSort.sort(listToSort, comparator, order);
    }

    /**
     * Clona a lista de contatos (IMPORTANTE para não afetar o cache)
     */
    private GenericLinkedList<Contact> cloneContactList(GenericLinkedList<Contact> original) {
        GenericLinkedList<Contact> cloned = new GenericLinkedList<>();
        if (original == null) return cloned;

        GenericLinkedList.Iterator<Contact> it = original.iterator();
        while (it.hasNext()) {
            Contact c = it.next();
            if (c != null) {
                cloned.add(cloneContact(c));
            }
        }
        return cloned;
    }

    /**
     * Clona um contato individual
     */
    private Contact cloneContact(Contact original) {
        Contact cloned = new Contact();
        cloned.setId(original.getId());
        cloned.setName(original.getName());
        cloned.setCompany(original.getCompany());
        cloned.setPhone(original.getPhone());
        cloned.setEmail(original.getEmail());
        cloned.setType(original.getType());
        cloned.setAddress(original.getAddress());
        cloned.setDescription(original.getDescription());
        cloned.setCreateDate(original.getCreateDate());
        return cloned;
    }

    /**
     * Ordenar por campo específico (apenas crescente)
     */
    public GenericLinkedList<Contact> sortWithMergeSort(String field) {
        return sortWithMergeSort(field, SortOrder.ASC);
    }

    private Comparator<Contact> getComparatorForField(String field) {
        switch (field.toLowerCase()) {
            case "phone":
                return Comparators.stringComparator(Contact::getPhone);
            case "email":
                return Comparators.stringComparator(Contact::getEmail);
            case "company":
                return Comparators.stringComparator(Contact::getCompany);
            case "type":
                return (c1, c2) -> {
                    if (c1.getType() == null && c2.getType() == null) return 0;
                    if (c1.getType() == null) return -1;
                    if (c2.getType() == null) return 1;
                    return c1.getType().name().compareTo(c2.getType().name());
                };
            default: // name
                return Comparators.stringComparator(Contact::getName);
        }
    }

    public GenericArrayList<Contact> searchByPhone(String phone) {
        GenericLinkedList<Contact> results = LinearSearch.searchAll(cachedContacts,
                c -> c != null && c.getPhone() != null &&
                        StringUtils.cleanPhoneNumber(c.getPhone()).contains(StringUtils.cleanPhoneNumber(phone)));

        return linkedListToArrayList(results);
    }

    public boolean phoneExists(String phone) {
        return phone != null && phoneIndex.get(phone) != null;
    }

    public boolean emailExists(String email) {
        return LinearSearch.search(cachedContacts,
                c -> c != null && email.equalsIgnoreCase(c.getEmail())) != null;
    }

    public int countAll() {
        return cachedContacts.size();
    }

    public int countByType(ContactType type) {
        GenericLinkedList<Contact> results = LinearSearch.searchAll(cachedContacts,
                c -> c != null && c.getType() == type);
        return results.size();
    }

    public void refreshCache() {
        searchIndex = new GenericBST<>(Contact::getName);
        phoneIndex = new GenericHashTable<>(Contact::getPhone);
        loadCache();
    }

    private void rebuildIndexes() {
        searchIndex = new GenericBST<>(Contact::getName);
        phoneIndex = new GenericHashTable<>(Contact::getPhone);

        GenericLinkedList.Iterator<Contact> it = cachedContacts.iterator();
        while (it.hasNext()) {
            Contact c = it.next();
            if (c != null) {
                searchIndex.insert(c);
                if (c.getPhone() != null) {
                    phoneIndex.put(c);
                }
            }
        }
    }

    private GenericArrayList<Contact> linkedListToArrayList(GenericLinkedList<Contact> linkedList) {
        GenericArrayList<Contact> arrayList = new GenericArrayList<>();
        GenericLinkedList.Iterator<Contact> it = linkedList.iterator();
        while (it.hasNext()) {
            Contact c = it.next();
            if (c != null) {
                arrayList.add(c);
            }
        }
        return arrayList;
    }

    private boolean syncDatabaseWithState(GenericLinkedList<Contact> targetState) {
        try {
            java.util.List<Contact> dbContacts = contactDAO.findAll();

            // Deletar contatos que não estão no estado alvo
            for (Contact dbContact : dbContacts) {
                boolean exists = LinearSearch.search(targetState,
                        c -> c != null && c.getId() == dbContact.getId()) != null;
                if (!exists) {
                    contactDAO.delete(dbContact.getId());
                }
            }

            // Adicionar/Atualizar contatos do estado alvo
            GenericLinkedList.Iterator<Contact> targetIt = targetState.iterator();
            while (targetIt.hasNext()) {
                Contact targetContact = targetIt.next();
                if (targetContact != null) {
                    Contact dbContact = contactDAO.findById(targetContact.getId());
                    if (dbContact == null) {
                        contactDAO.create(targetContact);
                    } else if (needsUpdate(dbContact, targetContact)) {
                        contactDAO.update(targetContact);
                    }
                }
            }

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

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

    private boolean equals(String s1, String s2) {
        if (s1 == null && s2 == null) return true;
        if (s1 == null || s2 == null) return false;
        return s1.equals(s2);
    }

    private boolean isValidContact(Contact contact) {
        if (contact == null) return false;
        if (contact.getName() == null || contact.getName().trim().isEmpty()) return false;
        if (contact.getPhone() == null || contact.getPhone().trim().isEmpty()) return false;
        return true;
    }
}