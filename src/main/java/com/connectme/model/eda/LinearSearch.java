package com.connectme.model.eda;

import java.util.function.Predicate;

/**
 * Implementação genérica do algoritmo LinearSearch
 * Busca sequencial em qualquer estrutura
 */
public class LinearSearch {

    /**
     * Busca linear em lista encadeada
     */
    public static <T> T search(GenericLinkedList<T> list, Predicate<T> predicate) {
        if (list == null || list.isEmpty()) {
            return null;
        }

        GenericLinkedList.Iterator<T> it = list.iterator();
        while (it.hasNext()) {
            T item = it.next();
            if (item != null && predicate.test(item)) {
                return item;
            }
        }

        return null;
    }

    /**
     * Busca linear em array
     */
    public static <T> T searchArray(T[] array, Predicate<T> predicate) {
        if (array == null || array.length == 0) {
            return null;
        }

        for (T item : array) {
            if (item != null && predicate.test(item)) {
                return item;
            }
        }

        return null;
    }

    /**
     * Busca todas as ocorrências em lista encadeada
     */
    public static <T> GenericLinkedList<T> searchAll(GenericLinkedList<T> list, Predicate<T> predicate) {
        GenericLinkedList<T> results = new GenericLinkedList<>();

        if (list == null || list.isEmpty()) {
            return results;
        }

        GenericLinkedList.Iterator<T> it = list.iterator();
        while (it.hasNext()) {
            T item = it.next();
            if (item != null && predicate.test(item)) {
                results.add(item);
            }
        }

        return results;
    }

    /**
     * Busca todas as ocorrências em array
     */
    public static <T> GenericArrayList<T> searchAllArray(T[] array, Predicate<T> predicate) {
        GenericArrayList<T> results = new GenericArrayList<>();

        if (array == null || array.length == 0) {
            return results;
        }

        for (T item : array) {
            if (item != null && predicate.test(item)) {
                results.add(item);
            }
        }

        return results;
    }
}