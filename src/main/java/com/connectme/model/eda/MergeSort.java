package com.connectme.model.eda;

import com.connectme.model.eda.componets.Node;
import com.connectme.model.enums.SortOrder;

import java.util.Comparator;

/**
 * Implementação genérica do algoritmo MergeSort com suporte a ordem crescente/decrescente
 * CORREÇÃO: Problema de perda de nós na lista encadeada
 */
public class MergeSort {

    /**
     * Ordena uma lista encadeada usando MergeSort com ordem especificada
     */
    public static <T> GenericLinkedList<T> sort(GenericLinkedList<T> list, Comparator<T> comparator, SortOrder order) {
        if (list == null || list.size() <= 1) {
            return cloneList(list); // SEMPRE retornar nova lista
        }

        Node<T> sorted = sortRec(list.getHead(), comparator, order);
        GenericLinkedList<T> result = new GenericLinkedList<>();
        result.setHead(sorted);
        return result;
    }

    /**
     * Ordena uma lista encadeada usando MergeSort (apenas crescente)
     */
    public static <T> GenericLinkedList<T> sort(GenericLinkedList<T> list, Comparator<T> comparator) {
        return sort(list, comparator, SortOrder.ASC);
    }

    private static <T> Node<T> sortRec(Node<T> head, Comparator<T> comparator, SortOrder order) {
        if (head == null || head.getNext() == null) {
            return head; // Caso base: lista vazia ou com um elemento
        }

        // Dividir a lista
        Node<T> middle = getMiddle(head);
        Node<T> nextOfMiddle = middle.getNext();
        middle.setNext(null); // Separar as duas metades

        // Ordenar recursivamente
        Node<T> left = sortRec(head, comparator, order);
        Node<T> right = sortRec(nextOfMiddle, comparator, order);

        // Mesclar as listas ordenadas
        return merge(left, right, comparator, order);
    }

    private static <T> Node<T> getMiddle(Node<T> head) {
        if (head == null) return head;

        Node<T> slow = head;
        Node<T> fast = head;

        while (fast.getNext() != null && fast.getNext().getNext() != null) {
            slow = slow.getNext();
            fast = fast.getNext().getNext();
        }

        return slow;
    }

    private static <T> Node<T> merge(Node<T> left,
                                                       Node<T> right,
                                                       Comparator<T> comparator,
                                                       SortOrder order) {
        // Nó dummy para facilitar a mesclagem
        Node<T> dummy = new Node<>(null);
        Node<T> current = dummy;

        Node<T> leftPtr = left;
        Node<T> rightPtr = right;

        // Mesclar enquanto houver elementos em ambas as listas
        while (leftPtr != null && rightPtr != null) {
            int comparison = comparator.compare(leftPtr.getData(), rightPtr.getData());

            // Aplicar ordem (crescente ou decrescente)
            if (order == SortOrder.DESC) {
                comparison = -comparison;
            }

            if (comparison <= 0) {
                current.setNext(leftPtr);
                leftPtr = leftPtr.getNext();
            } else {
                current.setNext(rightPtr);
                rightPtr = rightPtr.getNext();
            }
            current = current.getNext();
        }

        // Adicionar elementos restantes
        if (leftPtr != null) {
            current.setNext(leftPtr);
        } else {
            current.setNext(rightPtr);
        }

        return dummy.getNext();
    }

    /**
     * Clona uma lista encadeada (IMPORTANTE para não modificar a original)
     */
    private static <T> GenericLinkedList<T> cloneList(GenericLinkedList<T> original) {
        if (original == null) return new GenericLinkedList<>();

        GenericLinkedList<T> cloned = new GenericLinkedList<>();
        GenericLinkedList.Iterator<T> it = original.iterator();
        while (it.hasNext()) {
            T item = it.next();
            if (item != null) {
                cloned.add(cloneItem(item));
            }
        }
        return cloned;
    }

    /**
     * Clona um item (para tipos complexos, pode precisar de implementação específica)
     */
    @SuppressWarnings("unchecked")
    private static <T> T cloneItem(T item) {
        // Para objetos simples, retorna a mesma referência
        // Para objetos complexos, implementar clone específico
        return item;
    }

    /**
     * Ordena um array usando MergeSort com ordem especificada
     */
    public static <T> void sortArray(T[] array, Comparator<T> comparator, SortOrder order) {
        if (array == null || array.length <= 1) return;
        sortArrayRec(array, 0, array.length - 1, comparator, order);
    }

    /**
     * Ordena um array usando MergeSort (apenas crescente)
     */
    public static <T> void sortArray(T[] array, Comparator<T> comparator) {
        sortArray(array, comparator, SortOrder.ASC);
    }

    private static <T> void sortArrayRec(T[] array, int left, int right, Comparator<T> comparator, SortOrder order) {
        if (left < right) {
            int mid = left + (right - left) / 2;

            sortArrayRec(array, left, mid, comparator, order);
            sortArrayRec(array, mid + 1, right, comparator, order);
            mergeArrays(array, left, mid, right, comparator, order);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> void mergeArrays(T[] array, int left, int mid, int right, Comparator<T> comparator, SortOrder order) {
        int n1 = mid - left + 1;
        int n2 = right - mid;

        T[] leftArray = (T[]) new Object[n1];
        T[] rightArray = (T[]) new Object[n2];

        System.arraycopy(array, left, leftArray, 0, n1);
        System.arraycopy(array, mid + 1, rightArray, 0, n2);

        int i = 0, j = 0, k = left;

        while (i < n1 && j < n2) {
            int comparison = comparator.compare(leftArray[i], rightArray[j]);

            if (order == SortOrder.DESC) {
                comparison = -comparison;
            }

            if (comparison <= 0) {
                array[k] = leftArray[i];
                i++;
            } else {
                array[k] = rightArray[j];
                j++;
            }
            k++;
        }

        while (i < n1) {
            array[k] = leftArray[i];
            i++;
            k++;
        }

        while (j < n2) {
            array[k] = rightArray[j];
            j++;
            k++;
        }
    }
}