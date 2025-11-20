package com.connectme.eda;

import com.connectme.model.Contact;

/**
 * Implementação simples de lista ligada para armazenar contactos em memória.
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
        if (head == null) head = n;
        else {
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

    public int size() {
        return size;
    }
}
