package com.connectme.model.eda;

import com.connectme.model.entities.Contact;

/**
 * Lista ligada com métodos melhorados
 */
public class ContactLinkedList {

    private static class Node {
        Contact data;
        Node next;

        Node(Contact data) {
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

        // Caso especial: remover head
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

    /**
     * NOVO: Busca por ID
     */
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

    /**
     * NOVO: Converter para lista (útil para iteração)
     */
    public java.util.List<Contact> toList() {
        java.util.List<Contact> list = new java.util.ArrayList<>();
        Node cur = head;
        while (cur != null) {
            list.add(cur.data);
            cur = cur.next;
        }
        return list;
    }
}