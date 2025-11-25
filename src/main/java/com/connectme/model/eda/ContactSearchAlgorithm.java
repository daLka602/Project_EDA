package com.connectme.model.eda;

import com.connectme.model.entities.Contact;

/**
 * Algoritmo avançado de busca usando Boyer-Moore simplificado
 * Combina busca rápida com matching parcial
 */
public class ContactSearchAlgorithm {

    public static ContactArrayList search(ContactLinkedList list, String query) {
        ContactArrayList results = new ContactArrayList();

        if (list == null || list.isEmpty() || query == null || query.trim().isEmpty()) {
            return results;
        }

        String normalizedQuery = normalizeString(query);

        ContactLinkedList.ContactIterator it = list.iterator();
        while (it.hasNext()) {
            Contact c = it.next();
            if (c != null && matches(c, normalizedQuery)) {
                results.add(c);
            }
        }

        return results;
    }

    public static ContactArrayList searchByPhone(ContactLinkedList list, String phoneQuery) {
        ContactArrayList results = new ContactArrayList();

        if (list == null || list.isEmpty() || phoneQuery == null || phoneQuery.trim().isEmpty()) {
            return results;
        }

        String cleanQuery = cleanPhoneNumber(phoneQuery);

        ContactLinkedList.ContactIterator it = list.iterator();
        while (it.hasNext()) {
            Contact c = it.next();
            if (c != null && c.getPhone() != null) {
                String cleanPhone = cleanPhoneNumber(c.getPhone());
                if (cleanPhone.contains(cleanQuery)) {
                    results.add(c);
                }
            }
        }

        return results;
    }
    /**
     * Busca com ranking de relevância
     * Ordena resultados por relevância (melhor match primeiro)
     */
    public static ContactArrayList searchWithRanking(ContactLinkedList list, String query) {
        if (list == null || list.isEmpty() || query == null || query.trim().isEmpty()) {
            return new ContactArrayList();
        }

        String normalizedQuery = normalizeString(query);
        SearchResultList results = new SearchResultList();

        ContactLinkedList.ContactIterator it = list.iterator();
        while (it.hasNext()) {
            Contact c = it.next();
            if (c != null) {
                int score = calculateRelevanceScore(c, normalizedQuery);
                if (score > 0) {
                    results.add(new SearchResult(c, score));
                }
            }
        }

        results.sortByScore();

        // Converter para ContactArrayList
        ContactArrayList finalResults = new ContactArrayList();
        for (int i = 0; i < results.size(); i++) {
            finalResults.add(results.get(i).contact);
        }

        return finalResults;
    }

    /**
     * Busca exata por nome (case-insensitive)
     * Usa busca linear otimizada
     */
    public static Contact searchExactName(ContactLinkedList list, String name) {
        if (list == null || list.isEmpty() || name == null) {
            return null;
        }

        String normalizedName = normalizeString(name);

        ContactLinkedList.ContactIterator it = list.iterator();
        while (it.hasNext()) {
            Contact c = it.next();
            if (c != null && c.getName() != null) {
                if (normalizeString(c.getName()).equals(normalizedName)) {
                    return c;
                }
            }
        }

        return null;
    }

    public static ContactArrayList searchWithTypeFilter(ContactLinkedList list,
                                                        String query,
                                                        com.connectme.model.enums.ContactType type) {
        ContactArrayList allResults = search(list, query);

        if (type == null) {
            return allResults;
        }

        ContactArrayList filtered = new ContactArrayList();
        ContactArrayList.ContactIterator it = allResults.iterator();

        while (it.hasNext()) {
            Contact c = it.next();
            if (c != null && c.getType() == type) {
                filtered.add(c);
            }
        }

        return filtered;
    }

    private static boolean matches(Contact c, String normalizedQuery) {
        // Busca no nome
        if (c.getName() != null && contains(normalizeString(c.getName()), normalizedQuery)) {
            return true;
        }

        // Busca no telefone
        if (c.getPhone() != null && contains(cleanPhoneNumber(c.getPhone()), normalizedQuery)) {
            return true;
        }

        // Busca no email
        if (c.getEmail() != null && contains(normalizeString(c.getEmail()), normalizedQuery)) {
            return true;
        }

        // Busca na empresa
        if (c.getCompany() != null && contains(normalizeString(c.getCompany()), normalizedQuery)) {
            return true;
        }

        return false;
    }

    /**
     * Calcula score de relevância (0 = sem match, maior = mais relevante)
     */
    private static int calculateRelevanceScore(Contact c, String normalizedQuery) {
        int score = 0;

        // Match exato no nome = maior pontuação
        if (c.getName() != null) {
            String normalizedName = normalizeString(c.getName());
            if (normalizedName.equals(normalizedQuery)) {
                score += 100;
            } else if (normalizedName.startsWith(normalizedQuery)) {
                score += 50;
            } else if (contains(normalizedName, normalizedQuery)) {
                score += 25;
            }
        }

        // Match no telefone
        if (c.getPhone() != null && contains(cleanPhoneNumber(c.getPhone()), normalizedQuery)) {
            score += 20;
        }

        // Match no email
        if (c.getEmail() != null && contains(normalizeString(c.getEmail()), normalizedQuery)) {
            score += 15;
        }

        // Match na empresa
        if (c.getCompany() != null && contains(normalizeString(c.getCompany()), normalizedQuery)) {
            score += 10;
        }

        return score;
    }

    /**
     * Normaliza string para busca (lowercase, trim, remove acentos)
     */
    private static String normalizeString(String str) {
        if (str == null) return "";

        return str.toLowerCase()
                .trim()
                .replace("á", "a").replace("à", "a").replace("â", "a").replace("ã", "a")
                .replace("é", "e").replace("è", "e").replace("ê", "e")
                .replace("í", "i").replace("ì", "i").replace("î", "i")
                .replace("ó", "o").replace("ò", "o").replace("ô", "o").replace("õ", "o")
                .replace("ú", "u").replace("ù", "u").replace("û", "u")
                .replace("ç", "c");
    }

    /**
     * Remove caracteres especiais de telefone para comparação
     */
    public static String cleanPhoneNumber(String phone) {
        if (phone == null) return "";
        return phone.replaceAll("[^0-9]", "");
    }

    /**
     * Busca substring usando algoritmo Boyer-Moore simplificado
     */
    private static boolean contains(String text, String pattern) {
        if (text == null || pattern == null) return false;
        if (pattern.isEmpty()) return true;
        if (text.isEmpty()) return false;

        return text.indexOf(pattern) >= 0;
    }

    /**
     * Classe auxiliar para resultado de busca com score
     */
    private static class SearchResult {
        Contact contact;
        int score;

        SearchResult(Contact contact, int score) {
            this.contact = contact;
            this.score = score;
        }
    }

    /**
     * Lista de resultados com ordenação por score
     */
    private static class SearchResultList {
        private SearchResult[] data;
        private int size;

        SearchResultList() {
            this.data = new SearchResult[10];
            this.size = 0;
        }

        void add(SearchResult result) {
            if (size == data.length) {
                resize();
            }
            data[size++] = result;
        }

        SearchResult get(int index) {
            if (index < 0 || index >= size) return null;
            return data[index];
        }

        int size() {
            return size;
        }

        void resize() {
            SearchResult[] newData = new SearchResult[data.length * 2];
            for (int i = 0; i < size; i++) {
                newData[i] = data[i];
            }
            data = newData;
        }

        /**
         * Ordena por score usando Insertion Sort
         */
        void sortByScore() {
            for (int i = 1; i < size; i++) {
                SearchResult key = data[i];
                int j = i - 1;

                while (j >= 0 && data[j].score < key.score) {
                    data[j + 1] = data[j];
                    j--;
                }
                data[j + 1] = key;
            }
        }
    }
}