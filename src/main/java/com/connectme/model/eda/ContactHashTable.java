package com.connectme.model.eda;

import com.connectme.model.entities.Contact;

/**
 * HashTable com redimensionamento automático
 */
public class ContactHashTable {

    private static class Entry {
        String key;
        Contact value;
        Entry next;

        Entry(String key, Contact value) {
            this.key = key;
            this.value = value;
        }
    }

    private static final int INITIAL_SIZE = 16;
    private static final double LOAD_FACTOR = 0.75;
    
    private Entry[] table;
    private int size;

    public ContactHashTable() {
        this.table = new Entry[INITIAL_SIZE];
        this.size = 0;
    }

    private int hash(String key) {
        return Math.abs(key.hashCode()) % table.length;
    }

    public void put(String phone, Contact c) {
        if (phone == null || c == null) return;
        
        // Verificar se precisa redimensionar
        if ((double) size / table.length >= LOAD_FACTOR) {
            resize();
        }
        
        int index = hash(phone);
        Entry newEntry = new Entry(phone, c);

        // Verificar se já existe (atualizar)
        Entry cur = table[index];
        Entry prev = null;
        
        while (cur != null) {
            if (cur.key.equals(phone)) {
                // Atualizar valor existente
                cur.value = c;
                return;
            }
            prev = cur;
            cur = cur.next;
        }

        // Inserir novo
        if (prev == null) {
            table[index] = newEntry;
        } else {
            prev.next = newEntry;
        }
        size++;
    }

    public Contact get(String phone) {
        if (phone == null) return null;
        
        int index = hash(phone);
        Entry cur = table[index];
        
        while (cur != null) {
            if (cur.key.equals(phone)) return cur.value;
            cur = cur.next;
        }
        return null;
    }

    public boolean remove(String phone) {
        if (phone == null) return false;
        
        int index = hash(phone);
        Entry cur = table[index];
        Entry prev = null;
        
        while (cur != null) {
            if (cur.key.equals(phone)) {
                if (prev == null) {
                    table[index] = cur.next;
                } else {
                    prev.next = cur.next;
                }
                size--;
                return true;
            }
            prev = cur;
            cur = cur.next;
        }
        return false;
    }

    public boolean removeByContactId(int contactId) {
        boolean removed = false;
        for (int i = 0; i < table.length; i++) {
            Entry cur = table[i];
            Entry prev = null;
            
            while (cur != null) {
                if (cur.value.getId() == contactId) {
                    if (prev == null) {
                        table[i] = cur.next;
                    } else {
                        prev.next = cur.next;
                    }
                    size--;
                    removed = true;
                    break;
                }
                prev = cur;
                cur = cur.next;
            }
        }
        return removed;
    }

    private void resize() {
        Entry[] oldTable = table;
        table = new Entry[oldTable.length * 2];
        size = 0;

        // Rehash todos os elementos
        for (Entry entry : oldTable) {
            Entry cur = entry;
            while (cur != null) {
                put(cur.key, cur.value);
                cur = cur.next;
            }
        }
    }

    public int size() {
        return size;
    }
}