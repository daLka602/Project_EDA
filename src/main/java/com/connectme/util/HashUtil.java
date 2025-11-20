package com.connectme.util;

import java.security.MessageDigest;

public class HashUtil {

    /**
     * Retorna o hash SHA-256 de um texto.
     */
    public static String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes("UTF-8"));

            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();

        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar SHA-256: " + e.getMessage());
        }
    }
}
