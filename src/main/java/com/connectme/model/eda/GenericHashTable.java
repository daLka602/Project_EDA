package com.connectme.model.eda;

import com.connectme.model.eda.componets.Entry;

import java.util.function.Function;

public class GenericHashTable<T> {

    private static final int INITIAL_SIZE = 16;
    private static final double LOAD_FACTOR = 0.75;
    private Entry<T>[] table;
    private int size;
    private final Function<T, String> keyExtractor;

    @SuppressWarnings("unchecked")
    public GenericHashTable(Function<T, String> keyExtractor) {
        this.table = new Entry[INITIAL_SIZE];
        this.size = 0;
        this.keyExtractor = keyExtractor;
    }

    private int hash(String key) {
        return Math.abs(key.hashCode()) % table.length;
    }

    public void put(T value) {
        if (value == null) return;

        String key = keyExtractor.apply(value);
        if (key == null) return;

        if ((double) size / table.length >= LOAD_FACTOR) {
            resize();
        }

        int index = hash(key);
        Entry<T> newEntry = new Entry<>(key, value);

        Entry<T> cur = table[index];
        Entry<T> prev = null;

        while (cur != null) {
            if (cur.getKey().equals(key)) {
                cur.setValue(value);
                return;
            }
            prev = cur;
            cur = cur.getNext();
        }

        if (prev == null) {
            table[index] = newEntry;
        } else {
            prev.setNext(newEntry);
        }
        size++;
    }

    public T get(String key) {
        if (key == null) return null;

        int index = hash(key);
        Entry<T> cur = table[index];

        while (cur != null) {
            if (cur.getKey().equals(key)) return cur.getValue();
            cur = cur.getNext();
        }
        return null;
    }

    public boolean remove(String key) {
        if (key == null) return false;

        int index = hash(key);
        Entry<T> cur = table[index];
        Entry<T> prev = null;

        while (cur != null) {
            if (cur.getKey().equals(key)) {
                if (prev == null) {
                    table[index] = cur.getNext();
                } else {
                    prev.setNext(cur.getNext());
                }
                size--;
                return true;
            }
            prev = cur;
            cur = cur.getNext();
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    private void resize() {
        Entry<T>[] oldTable = table;
        table = new Entry[oldTable.length * 2];
        size = 0;

        for (Entry<T> entry : oldTable) {
            Entry<T> cur = entry;
            while (cur != null) {
                put(cur.getValue());
                cur = cur.getNext();
            }
        }
    }

    public int size() {
        return size;
    }
}