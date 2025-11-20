package com.connectme.model.eda;

import com.connectme.model.entities.Contact;

public class ContactBST {

    private static class Node {
        Contact data;
        Node left, right;

        Node(Contact data) {
            this.data = data;
        }
    }

    private Node root;

    public void insert(Contact c) {
        root = insertRec(root, c);
    }

    private Node insertRec(Node node, Contact c) {
        if (node == null) return new Node(c);

        if (c.getName().compareToIgnoreCase(node.data.getName()) < 0)
            node.left = insertRec(node.left, c);
        else
            node.right = insertRec(node.right, c);

        return node;
    }

    public Contact search(String name) {
        return searchRec(root, name);
    }

    private Contact searchRec(Node node, String name) {
        if (node == null) return null;

        int cmp = name.compareToIgnoreCase(node.data.getName());

        if (cmp == 0) return node.data;
        if (cmp < 0) return searchRec(node.left, name);
        else return searchRec(node.right, name);
    }
}
