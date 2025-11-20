package com.connectme.model.eda;

import com.connectme.model.entities.Contact;
import java.util.ArrayList;
import java.util.List;

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

        int cmp = c.getName().compareToIgnoreCase(node.data.getName());
        
        // Resolver duplicatas com email/telefone
        if (cmp == 0) {
            cmp = nullSafeCompare(c.getEmail(), node.data.getEmail());
            if (cmp == 0) {
                cmp = nullSafeCompare(c.getPhone(), node.data.getPhone());
            }
        }

        if (cmp < 0)
            node.left = insertRec(node.left, c);
        else
            node.right = insertRec(node.right, c);

        return node;
    }

    private int nullSafeCompare(String s1, String s2) {
        if (s1 == null && s2 == null) return 0;
        if (s1 == null) return -1;
        if (s2 == null) return 1;
        return s1.compareTo(s2);
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

    /**
     * NOVO: Busca parcial por nome (case-insensitive)
     */
    public List<Contact> searchPartial(String partialName) {
        List<Contact> results = new ArrayList<>();
        searchPartialRec(root, partialName.toLowerCase(), results);
        return results;
    }

    private void searchPartialRec(Node node, String partial, List<Contact> results) {
        if (node == null) return;
        
        String contactName = node.data.getName().toLowerCase();
        if (contactName.contains(partial)) {
            results.add(node.data);
        }
        
        // Continuar busca em ambas subárvores
        searchPartialRec(node.left, partial, results);
        searchPartialRec(node.right, partial, results);
    }

    /**
     * NOVO: Remover contacto por ID
     */
    public boolean remove(int contactId) {
        if (root == null) return false;
        
        Node parent = null;
        Node current = root;
        boolean isLeftChild = false;
        
        // Encontrar o nó a remover
        while (current != null && current.data.getId() != contactId) {
            parent = current;
            int cmp = current.data.getName().compareToIgnoreCase(
                String.valueOf(contactId)); // Simplificação
            
            if (cmp > 0) {
                current = current.left;
                isLeftChild = true;
            } else {
                current = current.right;
                isLeftChild = false;
            }
        }
        
        if (current == null) return false; // Não encontrado
        
        return removeNode(parent, current, isLeftChild);
    }

    private boolean removeNode(Node parent, Node node, boolean isLeftChild) {
        // Caso 1: Nó sem filhos
        if (node.left == null && node.right == null) {
            if (parent == null) {
                root = null;
            } else if (isLeftChild) {
                parent.left = null;
            } else {
                parent.right = null;
            }
            return true;
        }
        
        // Caso 2: Nó com um filho
        if (node.left == null) {
            if (parent == null) {
                root = node.right;
            } else if (isLeftChild) {
                parent.left = node.right;
            } else {
                parent.right = node.right;
            }
            return true;
        } else if (node.right == null) {
            if (parent == null) {
                root = node.left;
            } else if (isLeftChild) {
                parent.left = node.left;
            } else {
                parent.right = node.left;
            }
            return true;
        }
        
        // Caso 3: Nó com dois filhos
        Node successor = findMin(node.right);
        node.data = successor.data;
        return removeNode(node, node.right, false);
    }

    private Node findMin(Node node) {
        while (node.left != null) {
            node = node.left;
        }
        return node;
    }
}