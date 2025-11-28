package com.connectme.model.eda;

import com.connectme.model.eda.componets.Node;

public class GenericLinkedList<T> {

    private Node<T> head;
    private int size = 0;

    public void add(T data) {
        Node<T> n = new Node<>(data);
        if (head == null) {
            head = n;
        } else {
            Node<T> cur = head;
            while (cur.getNext() != null) cur = cur.getNext();
            cur.setNext(n);
        }
        size++;
    }

    public boolean remove(T data) {
        if (head == null) return false;

        if (head.getData().equals(data)) {
            head = head.getNext();
            size--;
            return true;
        }

        Node<T> cur = head;
        while (cur.getNext() != null) {
            if (cur.getNext().getData().equals(data)) {
                cur.setNext(cur.getNext().getNext());
                size--;
                return true;
            }
            cur = cur.getNext();
        }
        return false;
    }

    public T find(java.util.function.Predicate<T> predicate) {
        Node<T> cur = head;
        while (cur != null) {
            if (predicate.test(cur.getData())) {
                return cur.getData();
            }
            cur = cur.getNext();
        }
        return null;
    }

    public GenericLinkedList<T> findAll(java.util.function.Predicate<T> predicate) {
        GenericLinkedList<T> results = new GenericLinkedList<>();
        Node<T> cur = head;
        while (cur != null) {
            if (predicate.test(cur.getData())) {
                results.add(cur.getData());
            }
            cur = cur.getNext();
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
            cur = cur.getNext();
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
            cur = cur.getNext();
        }
        return cur.getData();
    }

    public T[] toArray(T[] array) {
        if (array.length < size) {
            array = (T[]) java.lang.reflect.Array.newInstance(
                    array.getClass().getComponentType(), size);
        }

        Node<T> cur = head;
        int i = 0;
        while (cur != null) {
            array[i++] = cur.getData();
            cur = cur.getNext();
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
            copy.add(current.getData());
            current = current.getNext();
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
            E data = current.getData();
            current = current.getNext();
            return data;
        }
    }
}