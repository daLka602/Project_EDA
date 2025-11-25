package com.connectme.model.eda;

import com.connectme.model.entities.Contact;

public class ContactArrayList {
    private Contact[] data;
    private int size;
    private static final int INITIAL_CAPACITY = 10;

    public ContactArrayList() {
        this.data = new Contact[INITIAL_CAPACITY];
        this.size = 0;
    }

    public ContactArrayList(int capacity) {
        this.data = new Contact[capacity];
        this.size = 0;
    }

    public void add(Contact contact) {
        if (size == data.length) {
            resize();
        }
        data[size++] = contact;
    }

    public Contact get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        return data[index];
    }

    public void set(int index, Contact contact) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        data[index] = contact;
    }

    public boolean remove(int contactId) {
        for (int i = 0; i < size; i++) {
            if (data[i].getId() == contactId) {
                removeAt(i);
                return true;
            }
        }
        return false;
    }

    public void removeAt(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }

        for (int i = index; i < size - 1; i++) {
            data[i] = data[i + 1];
        }
        data[--size] = null;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void clear() {
        for (int i = 0; i < size; i++) {
            data[i] = null;
        }
        size = 0;
    }

    public boolean contains(Contact contact) {
        for (int i = 0; i < size; i++) {
            if (data[i].equals(contact)) {
                return true;
            }
        }
        return false;
    }

    public int indexOf(Contact contact) {
        for (int i = 0; i < size; i++) {
            if (data[i].equals(contact)) {
                return i;
            }
        }
        return -1;
    }

    public Contact[] toArray() {
        Contact[] result = new Contact[size];
        for (int i = 0; i < size; i++) {
            result[i] = data[i];
        }
        return result;
    }

    public ContactIterator iterator() {
        return new ContactIterator();
    }

    private void resize() {
        int newCapacity = data.length * 2;
        Contact[] newData = new Contact[newCapacity];
        for (int i = 0; i < size; i++) {
            newData[i] = data[i];
        }
        data = newData;
    }

    public class ContactIterator {
        private int currentIndex = 0;

        public boolean hasNext() {
            return currentIndex < size;
        }

        public Contact next() {
            if (!hasNext()) {
                return null;
            }
            return data[currentIndex++];
        }

        public int getCurrentIndex() {
            return currentIndex - 1;
        }
    }
}