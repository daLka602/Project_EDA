package com.connectme.model.eda;

import java.util.Comparator;
import java.util.function.Function;

/**
 * Fábrica de comparadores genéricos
 * Pode criar comparadores para qualquer tipo de dado
 */
public class Comparators {

    /**
     * Cria comparator para campo string (case-insensitive)
     */
    public static <T> Comparator<T> stringComparator(Function<T, String> fieldExtractor) {
        return (c1, c2) -> {
            if (c1 == null && c2 == null) return 0;
            if (c1 == null) return -1;
            if (c2 == null) return 1;

            String s1 = fieldExtractor.apply(c1);
            String s2 = fieldExtractor.apply(c2);

            if (s1 == null && s2 == null) return 0;
            if (s1 == null) return -1;
            if (s2 == null) return 1;
            return s1.compareToIgnoreCase(s2);
        };
    }

    /**
     * Cria comparator para campo numérico
     */
    public static <T> Comparator<T> integerComparator(Function<T, Integer> fieldExtractor) {
        return (c1, c2) -> {
            if (c1 == null && c2 == null) return 0;
            if (c1 == null) return -1;
            if (c2 == null) return 1;

            Integer i1 = fieldExtractor.apply(c1);
            Integer i2 = fieldExtractor.apply(c2);

            if (i1 == null && i2 == null) return 0;
            if (i1 == null) return -1;
            if (i2 == null) return 1;
            return i1.compareTo(i2);
        };
    }

    /**
     * Cria comparator com ordenação especificada
     */
    public static <T> Comparator<T> withOrder(Comparator<T> comparator, MergeSort.SortOrder order) {
        return (c1, c2) -> {
            int result = comparator.compare(c1, c2);
            return (order == MergeSort.SortOrder.DESC) ? -result : result;
        };
    }

    /**
     * Cria comparator com ordenação reversa
     */
    public static <T> Comparator<T> reversed(Comparator<T> comparator) {
        return (c1, c2) -> comparator.compare(c2, c1);
    }

    /**
     * Cria comparator composto (múltiplos campos)
     */
    @SafeVarargs
    public static <T> Comparator<T> composite(Comparator<T>... comparators) {
        return (c1, c2) -> {
            for (Comparator<T> comparator : comparators) {
                int result = comparator.compare(c1, c2);
                if (result != 0) {
                    return result;
                }
            }
            return 0;
        };
    }
}