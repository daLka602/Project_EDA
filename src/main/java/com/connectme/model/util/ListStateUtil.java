package com.connectme.model.util;

import com.connectme.model.eda.GenericLinkedList;
import com.connectme.model.entities.Contact;

public class ListStateUtil {
    private GenericLinkedList<Contact> snapshot;
    private String description;
    private long timestamp;

    public ListStateUtil(GenericLinkedList<Contact> list, String description) {
        this.snapshot = cloneList(list);
        this.description = description;
        this.timestamp = System.currentTimeMillis();
    }

    public GenericLinkedList<Contact> getSnapshot() {
        return snapshot;
    }

    public String getDescription() {
        return description;
    }

    public long getTimestamp() {
        return timestamp;
    }

    private GenericLinkedList<Contact> cloneList(GenericLinkedList<Contact> original) {
        GenericLinkedList<Contact> cloned = new GenericLinkedList<>();
        if (original == null || original.isEmpty()) {
            return cloned;
        }

        GenericLinkedList.Iterator<Contact> it = original.iterator();
        while (it.hasNext()) {
            Contact c = it.next();
            if (c != null) {
                cloned.add(cloneContact(c));
            }
        }
        return cloned;
    }

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

    @Override
    public String toString() {
        return String.format("State[%s, %d contatos, %tF %<tT]",
                description,
                snapshot.size(),
                timestamp);
    }
}
