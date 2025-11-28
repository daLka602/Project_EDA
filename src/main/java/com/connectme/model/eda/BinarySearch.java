package com.connectme.model.eda;

import java.util.Comparator;
import java.util.function.Function;

/**
 * Implementação genérica do algoritmo BinarySearch
 * Pode buscar em qualquer estrutura ordenada
 */
public class BinarySearch {

    public static <T> T search(GenericLinkedList<T> sortedList, String key, Function<T, String> keyExtractor) {
        if (sortedList == null || sortedList.isEmpty() || key == null) {
            return null;
        }

        T[] arr = sortedList.toArray((T[]) new Object[0]);
        return searchArray(arr, key, 0, arr.length - 1, keyExtractor);
    }

    private static <T> T searchArray(T[] arr, String key, int low, int high, Function<T, String> keyExtractor) {
        if (low > high) return null;

        int mid = low + (high - low) / 2;
        String midKey = keyExtractor.apply(arr[mid]);

        if (midKey == null) return null;

        int cmp = key.compareToIgnoreCase(midKey);

        if (cmp == 0) {
            return arr[mid];
        } else if (cmp < 0) {
            return searchArray(arr, key, low, mid - 1, keyExtractor);
        } else {
            return searchArray(arr, key, mid + 1, high, keyExtractor);
        }
    }

    public static <T> T searchArray(T[] sortedArray, T target, Comparator<T> comparator) {
        if (sortedArray == null || sortedArray.length == 0 || target == null) {
            return null;
        }

        int low = 0;
        int high = sortedArray.length - 1;

        while (low <= high) {
            int mid = low + (high - low) / 2;
            int cmp = comparator.compare(sortedArray[mid], target);

            if (cmp == 0) {
                return sortedArray[mid];
            } else if (cmp < 0) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        return null;
    }

    /**
     * Busca binária com função de extração de chave
     */
    public static <T> T searchArray(T[] sortedArray, String key, Function<T, String> keyExtractor) {
        if (sortedArray == null || sortedArray.length == 0 || key == null) {
            return null;
        }

        int low = 0;
        int high = sortedArray.length - 1;

        while (low <= high) {
            int mid = low + (high - low) / 2;
            String midKey = keyExtractor.apply(sortedArray[mid]);

            if (midKey == null) continue;

            int cmp = key.compareToIgnoreCase(midKey);

            if (cmp == 0) {
                return sortedArray[mid];
            } else if (cmp < 0) {
                high = mid - 1;
            } else {
                low = mid + 1;
            }
        }

        return null;
    }
}