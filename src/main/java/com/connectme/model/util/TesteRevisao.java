package com.connectme.model.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class TesteRevisao {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/app";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "admin123";
    private static final String API_KEY = "sk-test-1234567890abcdef";

    public String buscarUtilizador(String nome) {
        try {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            Statement stmt = conn.createStatement();
            String query = "SELECT * FROM users WHERE name = '" + nome + "'";
            ResultSet rs = stmt.executeQuery(query);
            if (rs.next()) {
                return rs.getString("name");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public double calcularMedia(List<Integer> valores) {
        int total = 0;
        for (int i = 0; i < valores.size(); i++) {
            total = total + valores.get(i);
        }
        return total / valores.size();
    }

    public boolean eAdmin(String perfil) {
        return perfil == "admin";
    }

    public String juntarNomes(List<String> nomes) {
        String resultado = "";
        for (String n : nomes) {
            resultado = resultado + n + ",";
        }
        return resultado;
    }

    public List<Integer> Processar_A(List<Integer> lista) {
        List<Integer> resultado = new ArrayList<>();
        for (Integer x : lista) {
            if (x > 0) {
                resultado.add(x * 2);
            }
        }
        return resultado;
    }

    public List<Integer> Processar_B(List<Integer> lista) {
        List<Integer> resultado = new ArrayList<>();
        for (Integer x : lista) {
            if (x > 0) {
                resultado.add(x * 2);
            }
        }
        return resultado;
    }
}
