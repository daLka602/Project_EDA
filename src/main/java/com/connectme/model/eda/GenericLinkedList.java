package com.connectme.model.eda;

public class GenericLinkedList<T> {

    public static class Node<T> {
        public T data;
        public Node<T> next;

        public Node(T data) {
            this.data = data;
        }
    }

    private Node<T> head;
    private int size = 0;

    public void add(T data) {
        Node<T> n = new Node<>(data);
        if (head == null) {
            head = n;
        } else {
            Node<T> cur = head;
            while (cur.next != null) cur = cur.next;
            cur.next = n;
        }
        size++;
    }

    public boolean remove(T data) {
        if (head == null) return false;

        if (head.data.equals(data)) {
            head = head.next;
            size--;
            return true;
        }

        Node<T> cur = head;
        while (cur.next != null) {
            if (cur.next.data.equals(data)) {
                cur.next = cur.next.next;
                size--;
                return true;
            }
            cur = cur.next;
        }
        return false;
    }

    public T find(java.util.function.Predicate<T> predicate) {
        Node<T> cur = head;
        while (cur != null) {
            if (predicate.test(cur.data)) {
                return cur.data;
            }
            cur = cur.next;
        }
        return null;
    }

    public GenericLinkedList<T> findAll(java.util.function.Predicate<T> predicate) {
        GenericLinkedList<T> results = new GenericLinkedList<>();
        Node<T> cur = head;
        while (cur != null) {
            if (predicate.test(cur.data)) {
                results.add(cur.data);
            }
            cur = cur.next;
        }
        return results;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return head == null;
    }

    public Node<T> getHead() {
        return head;
    }

    public void setHead(Node<T> newHead) {
        this.head = newHead;
        recalculateSize();
    }

    private void recalculateSize() {
        size = 0;
        Node<T> cur = head;
        while (cur != null) {
            size++;
            cur = cur.next;
        }
    }

    public void clear() {
        head = null;
        size = 0;
    }

    public T get(int index) {
        if (index < 0 || index >= size) return null;

        Node<T> cur = head;
        for (int i = 0; i < index; i++) {
            cur = cur.next;
        }
        return cur.data;
    }

    public T[] toArray(T[] array) {
        if (array.length < size) {
            array = (T[]) java.lang.reflect.Array.newInstance(
                    array.getClass().getComponentType(), size);
        }

        Node<T> cur = head;
        int i = 0;
        while (cur != null) {
            array[i++] = cur.data;
            cur = cur.next;
        }

        if (array.length > size) {
            array[size] = null;
        }
        return array;
    }

    public static <T> GenericLinkedList<T> fromArray(T[] arr) {
        GenericLinkedList<T> list = new GenericLinkedList<>();
        for (T item : arr) {
            if (item != null) {
                list.add(item);
            }
        }
        return list;
    }

    public GenericLinkedList<T> copy() {
        GenericLinkedList<T> copy = new GenericLinkedList<>();
        Node<T> current = head;
        while (current != null) {
            copy.add(current.data);
            current = current.next;
        }
        return copy;
    }

    public Iterator<T> iterator() {
        return new Iterator<>(head);
    }

    public static class Iterator<E> {
        private Node<E> current;

        public Iterator(Node<E> head) {
            this.current = head;
        }

        public boolean hasNext() {
            return current != null;
        }

        public E next() {
            if (!hasNext()) return null;
            E data = current.data;
            current = current.next;
            return data;
        }
    }
}