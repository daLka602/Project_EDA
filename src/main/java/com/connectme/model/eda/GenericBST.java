package com.connectme.model.eda;

import com.connectme.model.eda.componets.Node;

import java.util.function.Function;

public class GenericBST<T> {

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
        String nodeKey = keyExtractor.apply(node.getData());

        int cmp = key.compareToIgnoreCase(nodeKey);

        if (cmp < 0)
            node.setLeft(insertRec(node.getLeft(), data));
        else
            node.setRight(insertRec(node.getRight(), data));

        return node;
    }

    public T search(String key) {
        return searchRec(root, key);
    }

    private T searchRec(Node<T> node, String key) {
        if (node == null) return null;

        String nodeKey = keyExtractor.apply(node.getData());
        int cmp = key.compareToIgnoreCase(nodeKey);

        if (cmp == 0) return node.getData();
        if (cmp < 0) return searchRec(node.getLeft(), key);
        else return searchRec(node.getRight(), key);
    }

    public GenericArrayList<T> searchPartial(String partialKey) {
        GenericArrayList<T> results = new GenericArrayList<>();
        searchPartialRec(root, partialKey.toLowerCase(), results);
        return results;
    }

    private void searchPartialRec(Node<T> node, String partial, GenericArrayList<T> results) {
        if (node == null) return;

        String key = keyExtractor.apply(node.getData()).toLowerCase();
        if (key.contains(partial)) {
            results.add(node.getData());
        }

        searchPartialRec(node.getLeft(), partial, results);
        searchPartialRec(node.getRight(), partial, results);
    }
}