package com.connectme.eda;

import com.connectme.model.Contact;

/**
 * HashTable simples (chave = numero de telefone).
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

    private final int SIZE = 101; // número primo → menos colisões
    private Entry[] table = new Entry[SIZE];

    private int hash(String key) {
        return Math.abs(key.hashCode()) % SIZE;
    }

    public void put(String phone, Contact c) {
        int index = hash(phone);
        Entry e = new Entry(phone, c);

        if (table[index] == null) {
            table[index] = e;
            return;
        }

        // colisões -> chaining
        Entry cur = table[index];
        while (cur.next != null) cur = cur.next;
        cur.next = e;
    }

    public Contact get(String phone) {
        int index = hash(phone);

        Entry cur = table[index];
        while (cur != null) {
            if (cur.key.equals(phone)) return cur.value;
            cur = cur.next;
        }

        return null;
    }
}
