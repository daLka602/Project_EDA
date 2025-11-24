// src/main/java/com/connectme/app/GenerateHashes.java
package com.connectme;

import com.connectme.model.util.HashUtil;

/**
 * Classe utilitária para gerar hashes SHA-256 das senhas
 * Execute esta classe para gerar os hashes corretos
 */
public class GenerateHashes {
    public static void main(String[] args) {
        System.out.println("=== GERANDO HASHES SHA-256 ===\n");

        String[][] credentials = {
                {"admin", "admin123"},
                {"usuario", "usuario123"},
                {"manager", "manager123"},
                {"staff", "user123"}
        };

        System.out.println("USE connectme;\n");
        System.out.println("DELETE FROM users;\n");
        System.out.println("INSERT INTO users (username, email, passwordHash, role, status) VALUES");

        for (int i = 0; i < credentials.length; i++) {
            String username = credentials[i][0];
            String password = credentials[i][1];
            String hash = HashUtil.sha256(password);

            String role = username.equals("admin") ? "ADMIN" :
                    username.equals("manager") ? "MANAGER" : "STAFF";

            System.out.print("('" + username + "', '" + username + "@connectme.mz', '" + hash + "', '" + role + "', 'ATIVE')");

            if (i < credentials.length - 1) {
                System.out.println(",");
            } else {
                System.out.println(";\n");
            }
        }

        System.out.println("\n=== TABELA DE REFERÊNCIA ===\n");
        System.out.println("USERNAME | SENHA       | HASH SHA-256");
        System.out.println("---------|-------------|----------------------------------------------");

        for (String[] cred : credentials) {
            String username = cred[0];
            String password = cred[1];
            String hash = HashUtil.sha256(password);
            System.out.printf("%-8s | %-11s | %s\n", username, password, hash);
        }
    }
}