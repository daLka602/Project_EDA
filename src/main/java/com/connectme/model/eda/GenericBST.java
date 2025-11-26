package com.connectme.model.eda;

import java.util.function.Function;

public class GenericBST<T> {

    private static class Node<T> {
        T data;
        Node<T> left, right;

        Node(T data) {
            this.data = data;
        }
    }

    private Node<T> root;
    private final Function<T, String> keyExtractor;

    public GenericBST(Function<T, String> keyExtractor) {
        this.keyExtractor = keyExtractor;
    }

    public void insert(T data) {
        root = insertRec(root, data);
    }

    private Node<T> insertRec(Node<T> node, T data) {
        if (node == null) return new Node<>(data);

        String key = keyExtractor.apply(data);
        String nodeKey = keyExtractor.apply(node.data);

        int cmp = key.compareToIgnoreCase(nodeKey);

        if (cmp < 0)
            node.left = insertRec(node.left, data);
        else
            node.right = insertRec(node.right, data);

        return node;
    }

    public T search(String key) {
        return searchRec(root, key);
    }

    private T searchRec(Node<T> node, String key) {
        if (node == null) return null;

        String nodeKey = keyExtractor.apply(node.data);
        int cmp = key.compareToIgnoreCase(nodeKey);

        if (cmp == 0) return node.data;
        if (cmp < 0) return searchRec(node.left, key);
        else return searchRec(node.right, key);
    }

    public GenericArrayList<T> searchPartial(String partialKey) {
        GenericArrayList<T> results = new GenericArrayList<>();
        searchPartialRec(root, partialKey.toLowerCase(), results);
        return results;
    }

    private void searchPartialRec(Node<T> node, String partial, GenericArrayList<T> results) {
        if (node == null) return;

        String key = keyExtractor.apply(node.data).toLowerCase();
        if (key.contains(partial)) {
            results.add(node.data);
        }

        searchPartialRec(node.left, partial, results);
        searchPartialRec(node.right, partial, results);
    }
}