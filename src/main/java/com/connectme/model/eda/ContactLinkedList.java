package com.connectme.model.eda;

import com.connectme.model.entities.Contact;

public class ContactLinkedList {

    public static class Node {
        public Contact data;
        public Node next;

        public Node(Contact data) {
            this.data = data;
        }
    }

    private Node head;
    private int size = 0;

    public void add(Contact c) {
        Node n = new Node(c);
        if (head == null) {
            head = n;
        } else {
            Node cur = head;
            while (cur.next != null) cur = cur.next;
            cur.next = n;
        }
        size++;
    }

    public boolean remove(int contactId) {
        if (head == null) return false;

        if (head.data.getId() == contactId) {
            head = head.next;
            size--;
            return true;
        }

        Node cur = head;
        while (cur.next != null) {
            if (cur.next.data.getId() == contactId) {
                cur.next = cur.next.next;
                size--;
                return true;
            }
            cur = cur.next;
        }
        return false;
    }

    public Contact findByName(String name) {
        Node cur = head;
        while (cur != null) {
            if (cur.data.getName().equalsIgnoreCase(name)) {
                return cur.data;
            }
            cur = cur.next;
        }
        return null;
    }

    public Contact findById(int contactId) {
        Node cur = head;
        while (cur != null) {
            if (cur.data.getId() == contactId) {
                return cur.data;
            }
            cur = cur.next;
        }
        return null;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return head == null;
    }

    public Node getHead() {
        return head;
    }

    public void setHead(Node newHead) {
        this.head = newHead;
        recalculateSize();
    }

    private void recalculateSize() {
        size = 0;
        Node cur = head;
        while (cur != null) {
            size++;
            cur = cur.next;
        }
    }

    /**
     * Limpa toda a lista
     */
    public void clear() {
        head = null;
        size = 0;
    }

    /**
     * Obtém o contato na posição index
     */
    public Contact get(int index) {
        if (index < 0 || index >= size) return null;

        Node cur = head;
        for (int i = 0; i < index; i++) {
            cur = cur.next;
        }
        return cur.data;
    }

    /**
     * Converte para array de Contacts
     */
    public Contact[] toArray() {
        Contact[] arr = new Contact[size];
        Node cur = head;
        int i = 0;
        while (cur != null) {
            arr[i++] = cur.data;
            cur = cur.next;
        }
        return arr;
    }

    /**
     * Cria uma lista a partir de um array
     */
    public static ContactLinkedList fromArray(Contact[] arr) {
        ContactLinkedList list = new ContactLinkedList();
        for (Contact c : arr) {
            if (c != null) {
                list.add(c);
            }
        }
        return list;
    }

    /**
     * Iterador personalizado
     */
    public ContactIterator iterator() {
        return new ContactIterator(head);
    }

    public static class ContactIterator {
        private Node current;

        public ContactIterator(Node head) {
            this.current = head;
        }

        public boolean hasNext() {
            return current != null;
        }

        public Contact next() {
            if (!hasNext()) return null;
            Contact data = current.data;
            current = current.next;
            return data;
        }
    }
}