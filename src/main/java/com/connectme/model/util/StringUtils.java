package com.connectme.model.util;

/**
 * Utilitários genéricos para manipulação de strings
 * Pode ser usado em qualquer sistema
 */
public class StringUtils {

    /**
     * Normaliza string para busca (lowercase, trim, remove acentos)
     */
    public static String normalize(String str) {
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
     * Remove caracteres não numéricos de telefone
     */
    public static String cleanPhoneNumber(String phone) {
        if (phone == null) return "";
        return phone.replaceAll("[^0-9]", "");
    }

    /**
     * Verifica se string contém substring (case-insensitive)
     */
    public static boolean containsIgnoreCase(String text, String substring) {
        if (text == null || substring == null) return false;
        if (substring.isEmpty()) return true;
        return normalize(text).contains(normalize(substring));
    }

    /**
     * Compara strings ignorando case e acentos
     */
    public static boolean equalsIgnoreCaseAndAccents(String s1, String s2) {
        if (s1 == null && s2 == null) return true;
        if (s1 == null || s2 == null) return false;
        return normalize(s1).equals(normalize(s2));
    }

    /**
     * Verifica se string começa com prefixo (case-insensitive)
     */
    public static boolean startsWithIgnoreCase(String text, String prefix) {
        if (text == null || prefix == null) return false;
        return normalize(text).startsWith(normalize(prefix));
    }
}