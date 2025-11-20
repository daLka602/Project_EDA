package com.connectme.model.dao;

import com.connectme.model.entities.User;
import com.connectme.config.DbConnection;

import java.sql.*;
import java.util.logging.Logger;

public class UserDAO implements IUserDAO {

    private static final Logger logger = Logger.getLogger(UserDAO.class.getName());
    private static final int MAX_ATTEMPTS = 3;
    private static final int MIN_PASSWORD_LENGTH = 4;

    @Override
    public boolean create(User user) {
        if (!isValidUser(user)) {
            logger.warning("Tentativa de criar usuário inválido: " + user.getUsername());
            return false;
        }

        // Verificar se username já existe
        if (findByUsername(user.getUsername()) != null) {
            logger.warning("Username já existe: " + user.getUsername());
            return false;
        }

        String sql = "INSERT INTO users (username, password_hash, status, failed_attempts) VALUES (?, ?, 'ativo', 0)";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getUsername().trim().toLowerCase());
            stmt.setString(2, user.getPasswordHash());

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            logger.severe("Erro ao criar usuário " + user.getUsername() + ": " + e.getMessage());
            return false;
        }
    }

    @Override
    public User findByUsername(String username) {
        if (username == null || username.isBlank()) return null;

        String sql = "SELECT * FROM users WHERE username = ?";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username.trim().toLowerCase());

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
            logger.severe("Erro ao buscar usuário " + username + ": " + e.getMessage());
        }

        return null;
    }

    @Override
    public boolean authenticate(String username, String hash) {
        if (username == null || hash == null) return false;

        User user = findByUsername(username);

        if (user == null) {
            logger.warning("Tentativa de login com usuário inexistente: " + username);
            return false;
        }

        if ("bloqueado".equals(user.getStatus())) {
            logger.warning("Tentativa de login com usuário bloqueado: " + username);
            return false;
        }

        if (user.getPasswordHash().equals(hash)) {
            resetAttempts(username);
            logger.info("Login bem-sucedido: " + username);
            return true;
        } else {
            incrementAttempts(username);
            logger.warning("Senha incorreta para usuário: " + username);
            return false;
        }
    }

    /**
     * NOVO: Verificar força da senha
     */
    public boolean isPasswordStrong(String password) {
        if (password == null) return false;
        return password.length() >= MIN_PASSWORD_LENGTH;
    }

    /**
     * NOVO: Desbloquear usuário
     */
    public boolean unlockUser(String username) {
        String sql = "UPDATE users SET status = 'ativo', failed_attempts = 0 WHERE username = ?";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username.trim().toLowerCase());
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                logger.info("Usuário desbloqueado: " + username);
                return true;
            }
            return false;

        } catch (SQLException e) {
            logger.severe("Erro ao desbloquear usuário " + username + ": " + e.getMessage());
            return false;
        }
    }

    private void resetAttempts(String username) {
        String sql = "UPDATE users SET failed_attempts = 0 WHERE username = ?";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username.trim().toLowerCase());
            stmt.executeUpdate();

        } catch (SQLException e) {
            logger.severe("Erro ao resetar tentativas: " + e.getMessage());
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
            stmt.setString(3, username.trim().toLowerCase());
            stmt.executeUpdate();

            if ("bloqueado".equals(status)) {
                logger.warning("Usuário bloqueado após " + attempts + " tentativas: " + username);
            }

        } catch (SQLException e) {
            logger.severe("Erro ao atualizar tentativas: " + e.getMessage());
        }
    }

    private boolean isValidUser(User user) {
        if (user == null) return false;
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) return false;
        if (user.getPasswordHash() == null || user.getPasswordHash().isEmpty()) return false;
        return user.getUsername().length() >= 3; // Mínimo 3 caracteres
    }
}