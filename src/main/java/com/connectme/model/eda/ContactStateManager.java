package com.connectme.model.eda;

import com.connectme.model.entities.Contact;

/**
 * Gerenciador de estados para funcionalidade Undo/Redo
 * Usa duas pilhas: uma para histórico (undo) e outra para refazer (redo)
 *
 * Exemplo de uso:
 * ContactStateManager manager = new ContactStateManager();
 * manager.saveState(myList, "Adicionar contato");
 * ContactLinkedList previous = manager.undo();
 */
public class ContactStateManager {

    /**
     * Classe interna que representa um snapshot completo do estado da lista
     * Inclui timestamp e descrição da operação
     */
    public static class ListState {
        private ContactLinkedList snapshot;
        private String description;
        private long timestamp;

        public ListState(ContactLinkedList list, String description) {
            this.snapshot = cloneList(list);
            this.description = description;
            this.timestamp = System.currentTimeMillis();
        }

        public ContactLinkedList getSnapshot() {
            return snapshot;
        }

        public String getDescription() {
            return description;
        }

        public long getTimestamp() {
            return timestamp;
        }

        /**
         * Clona uma lista encadeada completa (deep copy)
         * Cria uma nova lista com cópias de todos os contatos
         */
        private ContactLinkedList cloneList(ContactLinkedList original) {
            ContactLinkedList cloned = new ContactLinkedList();

            if (original == null || original.isEmpty()) {
                return cloned;
            }

            ContactLinkedList.ContactIterator it = original.iterator();
            while (it.hasNext()) {
                Contact c = it.next();
                if (c != null) {
                    cloned.add(cloneContact(c));
                }
            }

            return cloned;
        }

        /**
         * Clona um contato individual (deep copy)
         * Copia todos os campos do contato original
         */
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

    private ContactStack<ListState> undoStack;
    private ContactStack<ListState> redoStack;
    private ContactLinkedList currentState;

    /**
     * Construtor padrão
     * Cria pilhas com capacidade de 50 operações cada
     */
    public ContactStateManager() {
        this.undoStack = new ContactStack<>(50); // Máximo 50 undos
        this.redoStack = new ContactStack<>(50); // Máximo 50 redos
        this.currentState = new ContactLinkedList();
    }

    /**
     * Construtor com capacidade personalizada
     * @param maxCapacity número máximo de operações em cada pilha
     */
    public ContactStateManager(int maxCapacity) {
        this.undoStack = new ContactStack<>(maxCapacity);
        this.redoStack = new ContactStack<>(maxCapacity);
        this.currentState = new ContactLinkedList();
    }

    /**
     * Salva o estado atual antes de uma operação
     * @param list lista de contatos atual
     * @param description descrição da operação (ex: "Adicionar: João Silva")
     */
    public void saveState(ContactLinkedList list, String description) {
        if (list != null) {
            undoStack.push(new ListState(list, description));
            redoStack.clear(); // Limpa redo ao fazer nova operação
            this.currentState = list;
        }
    }

    /**
     * Desfaz a última operação (Undo)
     * Move o estado atual para a pilha de redo
     * Recupera o estado anterior da pilha de undo
     *
     * @return lista de contatos do estado anterior, ou estado atual se não houver undo
     */
    public ContactLinkedList undo() {
        if (!canUndo()) {
            return currentState;
        }

        // Salva estado atual no redo antes de desfazer
        redoStack.push(new ListState(currentState, "Redo point"));

        // Recupera estado anterior do undo
        ListState previousState = undoStack.pop();
        this.currentState = previousState.getSnapshot();

        return currentState;
    }

    /**
     * Refaz a última operação desfeita (Redo)
     * Move o estado atual para a pilha de undo
     * Recupera o próximo estado da pilha de redo
     *
     * @return lista de contatos do próximo estado, ou estado atual se não houver redo
     */
    public ContactLinkedList redo() {
        if (!canRedo()) {
            return currentState;
        }

        // Salva estado atual no undo antes de refazer
        undoStack.push(new ListState(currentState, "Undo point"));

        // Recupera próximo estado do redo
        ListState nextState = redoStack.pop();
        this.currentState = nextState.getSnapshot();

        return currentState;
    }

    /**
     * Verifica se é possível desfazer uma operação
     * @return true se há operações na pilha de undo
     */
    public boolean canUndo() {
        return !undoStack.isEmpty();
    }

    /**
     * Verifica se é possível refazer uma operação
     * @return true se há operações na pilha de redo
     */
    public boolean canRedo() {
        return !redoStack.isEmpty();
    }

    /**
     * Retorna o estado atual da lista
     * @return lista de contatos atual
     */
    public ContactLinkedList getCurrentState() {
        return currentState;
    }

    /**
     * Atualiza o estado atual sem salvar no histórico
     * Útil para sincronizações que não devem gerar undo
     *
     * @param list nova lista de contatos
     */
    public void updateCurrentState(ContactLinkedList list) {
        this.currentState = list;
    }

    /**
     * Retorna descrição da próxima operação de undo
     * @return descrição ou null se não houver undo disponível
     */
    public String getUndoDescription() {
        if (!canUndo()) {
            return null;
        }

        ListState state = undoStack.peek();
        return state != null ? state.getDescription() : null;
    }

    /**
     * Retorna descrição da próxima operação de redo
     * @return descrição ou null se não houver redo disponível
     */
    public String getRedoDescription() {
        if (!canRedo()) {
            return null;
        }

        ListState state = redoStack.peek();
        return state != null ? state.getDescription() : null;
    }

    /**
     * Retorna o número de operações disponíveis para undo
     * @return tamanho da pilha de undo
     */
    public int getUndoStackSize() {
        return undoStack.size();
    }

    /**
     * Retorna o número de operações disponíveis para redo
     * @return tamanho da pilha de redo
     */
    public int getRedoStackSize() {
        return redoStack.size();
    }

    /**
     * Limpa todo o histórico de undo e redo
     * Útil para resetar o gerenciador após sincronização completa
     */
    public void clearHistory() {
        undoStack.clear();
        redoStack.clear();
    }

    /**
     * Retorna informações formatadas sobre o estado atual
     * @return string com informações de undo, redo e contatos atuais
     */
    public String getStateInfo() {
        return String.format("Undo: %d | Redo: %d | Atual: %d contatos",
                undoStack.size(),
                redoStack.size(),
                currentState != null ? currentState.size() : 0);
    }

    /**
     * Retorna todas as descrições das operações de undo disponíveis
     * @return array de strings com as descrições
     */
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

    /**
     * Retorna todas as descrições das operações de redo disponíveis
     * @return array de strings com as descrições
     */
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

    /**
     * Desfaz múltiplas operações de uma vez
     * @param steps número de operações para desfazer
     * @return lista após desfazer as operações
     */
    public ContactLinkedList undoMultiple(int steps) {
        for (int i = 0; i < steps && canUndo(); i++) {
            undo();
        }
        return currentState;
    }

    /**
     * Refaz múltiplas operações de uma vez
     * @param steps número de operações para refazer
     * @return lista após refazer as operações
     */
    public ContactLinkedList redoMultiple(int steps) {
        for (int i = 0; i < steps && canRedo(); i++) {
            redo();
        }
        return currentState;
    }

    /**
     * Retorna representação em string do gerenciador
     */
    @Override
    public String toString() {
        return String.format("ContactStateManager[undo=%d, redo=%d, current=%d]",
                undoStack.size(),
                redoStack.size(),
                currentState != null ? currentState.size() : 0);
    }
}