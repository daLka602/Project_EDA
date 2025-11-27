package com.connectme.model.service;

import com.connectme.model.eda.GenericLinkedList;
import com.connectme.model.eda.GenericStack;
import com.connectme.model.entities.Contact;

public class ContactStateManager {

    public static class ListState {
        private GenericLinkedList<Contact> snapshot;
        private String description;
        private long timestamp;

        public ListState(GenericLinkedList<Contact> list, String description) {
            this.snapshot = cloneList(list);
            this.description = description;
            this.timestamp = System.currentTimeMillis();
        }

        public GenericLinkedList<Contact> getSnapshot() {
            return snapshot;
        }

        public String getDescription() {
            return description;
        }

        public long getTimestamp() {
            return timestamp;
        }

        private GenericLinkedList<Contact> cloneList(GenericLinkedList<Contact> original) {
            GenericLinkedList<Contact> cloned = new GenericLinkedList<>();
            if (original == null || original.isEmpty()) {
                return cloned;
            }

            GenericLinkedList.Iterator<Contact> it = original.iterator();
            while (it.hasNext()) {
                Contact c = it.next();
                if (c != null) {
                    cloned.add(cloneContact(c));
                }
            }
            return cloned;
        }

        private Contact cloneContact(Contact original) {
            Contact cloned = new Contact();
            cloned.setId(original.getId());
            cloned.setName(original.getName());
            cloned.setCompany(original.getCompany());
            cloned.setPhone(original.getPhone());
            cloned.setEmail(original.getEmail());
            cloned.setType(original.getType());
            cloned.setAddress(original.getAddress());
            cloned.setDescription(original.getDescription());
            cloned.setCreateDate(original.getCreateDate());
            return cloned;
        }

        @Override
        public String toString() {
            return String.format("State[%s, %d contatos, %tF %<tT]",
                    description,
                    snapshot.size(),
                    timestamp);
        }
    }

    private GenericStack<ListState> undoStack;
    private GenericStack<ListState> redoStack;
    private GenericLinkedList<Contact> currentState;

    public ContactStateManager() {
        this.undoStack = new GenericStack<>(50);
        this.redoStack = new GenericStack<>(50);
        this.currentState = new GenericLinkedList<>();
    }

    public ContactStateManager(int maxCapacity) {
        this.undoStack = new GenericStack<>(maxCapacity);
        this.redoStack = new GenericStack<>(maxCapacity);
        this.currentState = new GenericLinkedList<>();
    }

    /**
     * Salva o estado atual antes de uma operação
     */
    public void saveState(GenericLinkedList<Contact> list, String description) {
        if (list != null) {
            // Salva o estado atual no undo stack
            undoStack.push(new ListState(list, description));
            // Limpa redo ao fazer nova operação
            redoStack.clear();
            // Atualiza o estado atual
            this.currentState = cloneList(list);
        }
    }

    /**
     * Desfaz a última operação (Undo)
     */
    public GenericLinkedList<Contact> undo() {
        if (!canUndo()) {
            return currentState;
        }

        // Salva estado atual no redo antes de desfazer
        if (currentState != null) {
            redoStack.push(new ListState(currentState, "Redo point"));
        }

        // Recupera estado anterior do undo
        ListState previousState = undoStack.pop();
        this.currentState = previousState.getSnapshot();

        return currentState;
    }

    /**
     * Refaz a última operação desfeita (Redo)
     */
    public GenericLinkedList<Contact> redo() {
        if (!canRedo()) {
            return currentState;
        }

        // Salva estado atual no undo antes de refazer
        if (currentState != null) {
            undoStack.push(new ListState(currentState, "Undo point"));
        }

        // Recupera próximo estado do redo
        ListState nextState = redoStack.pop();
        this.currentState = nextState.getSnapshot();

        return currentState;
    }

    /**
     * Retorna o estado atual da lista
     */
    public GenericLinkedList<Contact> getCurrentState() {
        return currentState;
    }

    /**
     * Atualiza o estado atual (apenas para sincronização interna)
     */
    public void setCurrentState(GenericLinkedList<Contact> state) {
        this.currentState = state != null ? cloneList(state) : new GenericLinkedList<>();
    }

    public boolean canUndo() {
        return !undoStack.isEmpty();
    }

    public boolean canRedo() {
        return !redoStack.isEmpty();
    }

    public String getUndoDescription() {
        if (!canUndo()) {
            return null;
        }
        ListState state = undoStack.peek();
        return state != null ? state.getDescription() : null;
    }

    public String getRedoDescription() {
        if (!canRedo()) {
            return null;
        }
        ListState state = redoStack.peek();
        return state != null ? state.getDescription() : null;
    }

    public int getUndoStackSize() {
        return undoStack.size();
    }

    public int getRedoStackSize() {
        return redoStack.size();
    }

    public void clearHistory() {
        undoStack.clear();
        redoStack.clear();
        currentState = new GenericLinkedList<>();
    }

    public String getStateInfo() {
        return String.format("Undo: %d | Redo: %d | Atual: %d contatos",
                undoStack.size(),
                redoStack.size(),
                currentState != null ? currentState.size() : 0);
    }

    public String[] getUndoHistory() {
        if (undoStack.isEmpty()) {
            return new String[0];
        }

        Object[] statesArray = undoStack.toArray();
        String[] descriptions = new String[statesArray.length];

        for (int i = 0; i < statesArray.length; i++) {
            if (statesArray[i] instanceof ListState) {
                descriptions[i] = ((ListState) statesArray[i]).getDescription();
            }
        }

        return descriptions;
    }

    public String[] getRedoHistory() {
        if (redoStack.isEmpty()) {
            return new String[0];
        }

        Object[] statesArray = redoStack.toArray();
        String[] descriptions = new String[statesArray.length];

        for (int i = 0; i < statesArray.length; i++) {
            if (statesArray[i] instanceof ListState) {
                descriptions[i] = ((ListState) statesArray[i]).getDescription();
            }
        }

        return descriptions;
    }

    public GenericLinkedList<Contact> undoMultiple(int steps) {
        for (int i = 0; i < steps && canUndo(); i++) {
            undo();
        }
        return currentState;
    }

    public GenericLinkedList<Contact> redoMultiple(int steps) {
        for (int i = 0; i < steps && canRedo(); i++) {
            redo();
        }
        return currentState;
    }

    @Override
    public String toString() {
        return String.format("ContactStateManager[undo=%d, redo=%d, current=%d]",
                undoStack.size(),
                redoStack.size(),
                currentState != null ? currentState.size() : 0);
    }

    private GenericLinkedList<Contact> cloneList(GenericLinkedList<Contact> original) {
        GenericLinkedList<Contact> cloned = new GenericLinkedList<>();
        if (original == null || original.isEmpty()) {
            return cloned;
        }

        GenericLinkedList.Iterator<Contact> it = original.iterator();
        while (it.hasNext()) {
            Contact c = it.next();
            if (c != null) {
                cloned.add(cloneContact(c));
            }
        }
        return cloned;
    }

    private Contact cloneContact(Contact original) {
        Contact cloned = new Contact();
        cloned.setId(original.getId());
        cloned.setName(original.getName());
        cloned.setCompany(original.getCompany());
        cloned.setPhone(original.getPhone());
        cloned.setEmail(original.getEmail());
        cloned.setType(original.getType());
        cloned.setAddress(original.getAddress());
        cloned.setDescription(original.getDescription());
        cloned.setCreateDate(original.getCreateDate());
        return cloned;
    }
}