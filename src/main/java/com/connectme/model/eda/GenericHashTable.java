package com.connectme.model.eda;

import java.util.function.Function;

public class GenericHashTable<T> {

    private static class Entry<T> {
        String key;
        T value;
        Entry<T> next;

        Entry(String key, T value) {
            this.key = key;
            this.value = value;
        }
    }

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
            if (cur.key.equals(key)) {
                cur.value = value;
                return;
            }
            prev = cur;
            cur = cur.next;
        }

        if (prev == null) {
            table[index] = newEntry;
        } else {
            prev.next = newEntry;
        }
        size++;
    }

    public T get(String key) {
        if (key == null) return null;

        int index = hash(key);
        Entry<T> cur = table[index];

        while (cur != null) {
            if (cur.key.equals(key)) return cur.value;
            cur = cur.next;
        }
        return null;
    }

    public boolean remove(String key) {
        if (key == null) return false;

        int index = hash(key);
        Entry<T> cur = table[index];
        Entry<T> prev = null;

        while (cur != null) {
            if (cur.key.equals(key)) {
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

    @SuppressWarnings("unchecked")
    private void resize() {
        Entry<T>[] oldTable = table;
        table = new Entry[oldTable.length * 2];
        size = 0;

        for (Entry<T> entry : oldTable) {
            Entry<T> cur = entry;
            while (cur != null) {
                put(cur.value);
                cur = cur.next;
            }
        }
    }

    public int size() {
        return size;
    }
}