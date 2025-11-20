package com.connectme.model.dao;

import com.connectme.model.entities.User;
import com.connectme.config.DbConnection;

import java.sql.*;

public class UserDAO implements IUserDAO {

    private static final int MAX_ATTEMPTS = 3;

    @Override
    public boolean create(User user) {
        String sql = "INSERT INTO users (username, password_hash, status, failed_attempts) VALUES (?, ?, 'ativo', 0)";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPasswordHash());

            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Erro ao criar usuário: " + e.getMessage());
            return false;
        }
    }

    @Override
    public User findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {

                if (rs.next()) {
                    return new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password_hash"),
                        rs.getString("status"),
                        rs.getInt("failed_attempts")
                    );
                }
            }

        } catch (SQLException e) {
            System.out.println("Erro ao buscar usuário: " + e.getMessage());
        }

        return null;
    }

    @Override
    public boolean authenticate(String username, String hash) {
        User user = findByUsername(username);

        if (user == null) return false;

        if (user.getStatus().equals("bloqueado")) {
            System.out.println("Usuário bloqueado.");
            return false;
        }

        if (user.getPasswordHash().equals(hash)) {
            resetAttempts(username);
            return true;
        } else {
            incrementAttempts(username);
            return false;
        }
    }

    private void resetAttempts(String username) {
        String sql = "UPDATE users SET failed_attempts = 0 WHERE username = ?";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Erro ao resetar tentativas: " + e.getMessage());
        }
    }

    private void incrementAttempts(String username) {
        User user = findByUsername(username);

        if (user == null) return;

        int attempts = user.getFailedAttempts() + 1;
        String status = attempts >= MAX_ATTEMPTS ? "bloqueado" : "ativo";

        String sql = "UPDATE users SET failed_attempts = ?, status = ? WHERE username = ?";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, attempts);
            stmt.setString(2, status);
            stmt.setString(3, username);
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Erro ao atualizar tentativas: " + e.getMessage());
        }
    }
}
