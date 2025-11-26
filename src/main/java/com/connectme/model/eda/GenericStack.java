package com.connectme.model.eda;

/**
 * Stack (Pilha) genérica para implementação de Undo/Redo
 * LIFO - Last In First Out
 */
public class GenericStack<T> {

    private static class Node<T> {
        T data;
        Node<T> next;

        Node(T data) {
            this.data = data;
        }
    }

    private Node<T> top;
    private int size;
    private int maxSize;

    public GenericStack() {
        this(100); // Limite padrão de 100 operações
    }

    public GenericStack(int maxSize) {
        this.top = null;
        this.size = 0;
        this.maxSize = maxSize;
    }

    /**
     * Adiciona elemento no topo da pilha (Push)
     */
    public void push(T data) {
        if (size >= maxSize) {
            removeBottom(); // Remove o mais antigo se atingir limite
        }

        Node<T> newNode = new Node<>(data);
        newNode.next = top;
        top = newNode;
        size++;
    }

    /**
     * Remove e retorna o elemento do topo (Pop)
     */
    public T pop() {
        if (isEmpty()) {
            return null;
        }

        T data = top.data;
        top = top.next;
        size--;
        return data;
    }

    /**
     * Retorna o elemento do topo sem remover (Peek)
     */
    public T peek() {
        if (isEmpty()) {
            return null;
        }
        return top.data;
    }

    public boolean isEmpty() {
        return top == null;
    }

    public int size() {
        return size;
    }

    public void clear() {
        top = null;
        size = 0;
    }

    /**
     * Remove o elemento da base (para limitar tamanho)
     */
    private void removeBottom() {
        if (isEmpty() || size == 1) {
            top = null;
            size = 0;
            return;
        }

        Node<T> current = top;
        Node<T> prev = null;

        while (current.next != null) {
            prev = current;
            current = current.next;
        }

        if (prev != null) {
            prev.next = null;
            size--;
        }
    }

    /**
     * Converte a pilha para array (do topo para a base)
     */
    public Object[] toArray() {
        Object[] arr = new Object[size];
        Node<T> current = top;
        int i = 0;

        while (current != null) {
            arr[i++] = current.data;
            current = current.next;
        }

        return arr;
    }

    @Override
    public String toString() {
        if (isEmpty()) {
            return "Stack[empty]";
        }

        StringBuilder sb = new StringBuilder("Stack[");
        Node<T> current = top;

        while (current != null) {
            sb.append(current.data);
            if (current.next != null) {
                sb.append(", ");
            }
            current = current.next;
        }

        sb.append("]");
        return sb.toString();
    }
}