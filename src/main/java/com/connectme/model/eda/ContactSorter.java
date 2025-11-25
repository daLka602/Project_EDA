package com.connectme.model.eda;

import com.connectme.model.entities.Contact;

public class ContactSorter {

    public enum SortField {
        NAME, PHONE, EMAIL, COMPANY, TYPE
    }

    public enum SortOrder {
        ASC, DESC
    }

    /**
     * Ordena a lista usando MergeSort (Divide and Conquer)
     * Complexidade: O(n log n) no pior caso
     * Estável: Sim
     */
    public static ContactLinkedList mergeSort(ContactLinkedList list, SortField field, SortOrder order) {
        if (list == null || list.size() <= 1) {
            return list;
        }

        ContactLinkedList.Node sorted = mergeSortRec(list.getHead(), field, order);
        ContactLinkedList result = new ContactLinkedList();
        result.setHead(sorted);
        return result;
    }

    /**
     * Implementação recursiva do MergeSort para lista encadeada
     */
    private static ContactLinkedList.Node mergeSortRec(ContactLinkedList.Node head, SortField field, SortOrder order) {
        // Caso base: lista vazia ou com um elemento
        if (head == null || head.next == null) {
            return head;
        }

        // Dividir a lista ao meio
        ContactLinkedList.Node middle = getMiddle(head);
        ContactLinkedList.Node nextOfMiddle = middle.next;
        middle.next = null;

        // Ordenar recursivamente as duas metades
        ContactLinkedList.Node left = mergeSortRec(head, field, order);
        ContactLinkedList.Node right = mergeSortRec(nextOfMiddle, field, order);

        // Mesclar as listas ordenadas
        return merge(left, right, field, order);
    }

    /**
     * Encontra o nó do meio da lista usando técnica slow/fast pointer
     */
    private static ContactLinkedList.Node getMiddle(ContactLinkedList.Node head) {
        if (head == null) return head;

        ContactLinkedList.Node slow = head;
        ContactLinkedList.Node fast = head.next;

        // Fast avança 2x mais rápido que slow
        while (fast != null) {
            fast = fast.next;
            if (fast != null) {
                slow = slow.next;
                fast = fast.next;
            }
        }

        return slow;
    }

    /**
     * Mescla duas listas encadeadas ordenadas
     */
    private static ContactLinkedList.Node merge(ContactLinkedList.Node left, ContactLinkedList.Node right,
                                                SortField field, SortOrder order) {
        if (left == null) return right;
        if (right == null) return left;

        ContactLinkedList.Node result;

        int comparison = compare(left.data, right.data, field);
        if (order == SortOrder.DESC) {
            comparison = -comparison;
        }

        if (comparison <= 0) {
            result = left;
            result.next = merge(left.next, right, field, order);
        } else {
            result = right;
            result.next = merge(left, right.next, field, order);
        }

        return result;
    }

    /**
     * Ordena a lista usando QuickSort
     * Complexidade: O(n log n) em média, O(n²) no pior caso
     * Estável: Não
     */
    public static ContactLinkedList quickSort(ContactLinkedList list, SortField field, SortOrder order) {
        if (list == null || list.size() <= 1) {
            return list;
        }

        // Converter para array para QuickSort
        Contact[] arr = list.toArray();
        quickSortArray(arr, 0, arr.length - 1, field, order);
        return ContactLinkedList.fromArray(arr);
    }

    /**
     * Implementação recursiva do QuickSort para array
     */
    private static void quickSortArray(Contact[] arr, int low, int high, SortField field, SortOrder order) {
        if (low < high) {
            // Particionar e obter índice do pivô
            int pi = partition(arr, low, high, field, order);

            // Ordenar recursivamente os elementos antes e depois da partição
            quickSortArray(arr, low, pi - 1, field, order);
            quickSortArray(arr, pi + 1, high, field, order);
        }
    }

    /**
     * Particiona o array e retorna o índice do pivô
     * Usa o último elemento como pivô
     */
    private static int partition(Contact[] arr, int low, int high, SortField field, SortOrder order) {
        Contact pivot = arr[high];
        int i = low - 1;

        for (int j = low; j < high; j++) {
            int comparison = compare(arr[j], pivot, field);
            if (order == SortOrder.DESC) {
                comparison = -comparison;
            }

            if (comparison <= 0) {
                i++;
                // Trocar arr[i] e arr[j]
                Contact temp = arr[i];
                arr[i] = arr[j];
                arr[j] = temp;
            }
        }

        // Trocar arr[i+1] e arr[high] (pivô)
        Contact temp = arr[i + 1];
        arr[i + 1] = arr[high];
        arr[high] = temp;

        return i + 1;
    }

    /**
     * Compara dois contatos baseado no campo especificado
     * Retorna: negativo se c1 < c2, zero se iguais, positivo se c1 > c2
     */
    private static int compare(Contact c1, Contact c2, SortField field) {
        if (c1 == null && c2 == null) return 0;
        if (c1 == null) return -1;
        if (c2 == null) return 1;

        switch (field) {
            case NAME:
                return compareStrings(c1.getName(), c2.getName());

            case PHONE:
                return compareStrings(c1.getPhone(), c2.getPhone());

            case EMAIL:
                return compareStrings(c1.getEmail(), c2.getEmail());

            case COMPANY:
                return compareStrings(c1.getCompany(), c2.getCompany());

            case TYPE:
                if (c1.getType() == null && c2.getType() == null) return 0;
                if (c1.getType() == null) return -1;
                if (c2.getType() == null) return 1;
                return c1.getType().compareTo(c2.getType());

            default:
                return 0;
        }
    }

    /**
     * Comparação de strings case-insensitive com tratamento de nulos
     */
    private static int compareStrings(String s1, String s2) {
        if (s1 == null && s2 == null) return 0;
        if (s1 == null) return -1;
        if (s2 == null) return 1;
        return s1.compareToIgnoreCase(s2);
    }

    /**
     * Busca binária em lista ordenada (requer lista ordenada por NAME)
     * Complexidade: O(log n)
     */
    public static Contact binarySearch(ContactLinkedList sortedList, String name) {
        if (sortedList == null || sortedList.isEmpty() || name == null) {
            return null;
        }

        Contact[] arr = sortedList.toArray();
        return binarySearchArray(arr, name, 0, arr.length - 1);
    }

    /**
     * Implementação recursiva da busca binária
     */
    private static Contact binarySearchArray(Contact[] arr, String name, int low, int high) {
        if (low > high) return null;

        int mid = low + (high - low) / 2;
        String midName = arr[mid].getName();

        if (midName == null) return null;

        int cmp = name.compareToIgnoreCase(midName);

        if (cmp == 0) {
            return arr[mid];
        } else if (cmp < 0) {
            return binarySearchArray(arr, name, low, mid - 1);
        } else {
            return binarySearchArray(arr, name, mid + 1, high);
        }
    }

    /**
     * Busca sequencial em lista não ordenada
     * Complexidade: O(n)
     */
    public static Contact linearSearch(ContactLinkedList list, String name) {
        if (list == null || list.isEmpty() || name == null) {
            return null;
        }

        ContactLinkedList.ContactIterator it = list.iterator();
        while (it.hasNext()) {
            Contact c = it.next();
            if (c != null && c.getName() != null &&
                    c.getName().equalsIgnoreCase(name)) {
                return c;
            }
        }

        return null;
    }

    /**
     * Verifica se a lista está ordenada
     */
    public static boolean isSorted(ContactLinkedList list, SortField field, SortOrder order) {
        if (list == null || list.size() <= 1) {
            return true;
        }

        ContactLinkedList.Node current = list.getHead();
        while (current != null && current.next != null) {
            int comparison = compare(current.data, current.next.data, field);
            if (order == SortOrder.DESC) {
                comparison = -comparison;
            }

            if (comparison > 0) {
                return false;
            }
            current = current.next;
        }

        return true;
    }

    /**
     * Retorna estatísticas sobre a ordenação (útil para debug/análise)
     */
    public static class SortStats {
        public long timeMs;
        public int comparisons;
        public int swaps;
        public String algorithm;

        public SortStats(String algorithm) {
            this.algorithm = algorithm;
        }

        @Override
        public String toString() {
            return String.format("%s: %dms, Comparações: %d, Trocas: %d",
                    algorithm, timeMs, comparisons, swaps);
        }
    }

    /**
     * Ordena com estatísticas de performance
     */
    public static SortResult sortWithStats(ContactLinkedList list, SortField field,
                                           SortOrder order, String algorithm) {
        long startTime = System.currentTimeMillis();
        ContactLinkedList sorted;

        if ("MergeSort".equalsIgnoreCase(algorithm)) {
            sorted = mergeSort(list, field, order);
        } else {
            sorted = quickSort(list, field, order);
        }

        long endTime = System.currentTimeMillis();

        SortResult result = new SortResult();
        result.sortedList = sorted;
        result.timeMs = endTime - startTime;
        result.algorithm = algorithm;
        result.size = list.size();

        return result;
    }

    public static class SortResult {
        public ContactLinkedList sortedList;
        public long timeMs;
        public String algorithm;
        public int size;

        @Override
        public String toString() {
            return String.format("Algoritmo: %s | Tamanho: %d | Tempo: %dms",
                    algorithm, size, timeMs);
        }
    }
}