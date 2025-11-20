package com.connectme.model.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.logging.Logger;

public class HashUtil {

    private static final Logger logger = Logger.getLogger(HashUtil.class.getName());
    private static final SecureRandom secureRandom = new SecureRandom();

    /**
     * Retorna o hash SHA-256 de um texto
     */
    public static String sha256(String input) {
        return hash(input, "SHA-256");
    }

    /**
     * Retorna o hash SHA-512 de um texto (mais seguro)
     */
    public static String sha512(String input) {
        return hash(input, "SHA-512");
    }

    /**
     * NOVO: Hash com salt para maior segurança
     */
    public static String sha256WithSalt(String input, String salt) {
        return hash(input + salt, "SHA-256");
    }

    /**
     * NOVO: Gerar salt aleatório
     */
    public static String generateSalt() {
        byte[] salt = new byte[16];
        secureRandom.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    /**
     * NOVO: Verificar hash com salt
     */
    public static boolean verifyWithSalt(String input, String hash, String salt) {
        String computedHash = sha256WithSalt(input, salt);
        return computedHash.equals(hash);
    }

    /**
     * NOVO: Hash MD5 (apenas para compatibilidade, não seguro)
     */
    public static String md5(String input) {
        return hash(input, "MD5");
    }

    /**
     * Método genérico para hashing
     */
    private static String hash(String input, String algorithm) {
        if (input == null) {
            throw new IllegalArgumentException("Input não pode ser nulo");
        }

        try {
            MessageDigest digest = MessageDigest.getInstance(algorithm);
            byte[] hashBytes = digest.digest(input.getBytes("UTF-8"));

            // Converter para hexadecimal
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            logger.severe("Algoritmo de hash não disponível: " + algorithm);
            throw new RuntimeException("Algoritmo " + algorithm + " não disponível", e);
        } catch (Exception e) {
            logger.severe("Erro ao gerar hash " + algorithm + ": " + e.getMessage());
            throw new RuntimeException("Erro ao gerar hash: " + e.getMessage(), e);
        }
    }

    /**
     * NOVO: Verificar força do hash (para migração futura)
     */
    public static boolean isHashSecure(String hash) {
        if (hash == null) return false;
        return hash.length() == 64; // SHA-256 tem 64 caracteres hex
    }

    /**
     * NOVO: Benchmark de performance dos algoritmos
     */
    public static void benchmarkHash(String input, int iterations) {
        logger.info("Iniciando benchmark de hashing...");
        
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < iterations; i++) {
            sha256(input);
        }
        long sha256Time = System.currentTimeMillis() - startTime;
        
        startTime = System.currentTimeMillis();
        for (int i = 0; i < iterations; i++) {
            sha512(input);
        }
        long sha512Time = System.currentTimeMillis() - startTime;
        
        logger.info(String.format(
            "Benchmark (%d iterações): SHA-256=%dms, SHA-512=%dms",
            iterations, sha256Time, sha512Time
        ));
    }
}