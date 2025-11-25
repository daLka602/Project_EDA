package com.connectme.model.dao;

import com.connectme.model.entities.User;
import com.connectme.model.enums.AccessLevel;
import com.connectme.model.enums.UserStatus;
import com.connectme.config.DbConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class UserDAO implements IUserDAO {

    private static final Logger logger = Logger.getLogger(UserDAO.class.getName());
    private static final int MIN_PASSWORD_LENGTH = 6;

    @Override
    public boolean create(User user) {
        if (!isValidUser(user)) {
            logger.warning("Tentativa de criar usuário inválido: " + user.getUsername());
            return false;
        }

        if (findByUsername(user.getUsername()) != null) {
            logger.warning("Username já existe: " + user.getUsername());
            return false;
        }

        String sql = "INSERT INTO users (username, email, passwordHash, role, status, createDate) " +
                "VALUES (?, ?, ?, ?, ?, NOW())";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getUsername().trim().toLowerCase());
            stmt.setString(2, user.getEmail().trim().toLowerCase());
            stmt.setString(3, user.getPasswordHash());
            stmt.setString(4, user.getRole() != null ? user.getRole().name() : AccessLevel.STAFF.name());
            stmt.setString(5, user.getStatus() != null ? user.getStatus().name() : UserStatus.ATIVE.name());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                logger.info("Usuário criado com sucesso: " + user.getUsername());
                return true;
            }
            return false;

        } catch (SQLException e) {
            logger.severe("Erro ao criar usuário " + user.getUsername() + ": " + e.getMessage());
            e.printStackTrace();
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
                    return mapResultSetToUser(rs);
                }
            }

        } catch (SQLException e) {
            logger.severe("Erro ao buscar usuário " + username + ": " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public User findById(int id) {
        if (id <= 0) return null;

        String sql = "SELECT * FROM users WHERE id = ?";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }

        } catch (SQLException e) {
            logger.severe("Erro ao buscar usuário ID " + id + ": " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public List<User> findAll() {
        String sql = "SELECT * FROM users ORDER BY username ASC";
        List<User> list = new ArrayList<>();

        try (Connection conn = DbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(mapResultSetToUser(rs));
            }

        } catch (SQLException e) {
            logger.severe("Erro ao listar usuários: " + e.getMessage());
            e.printStackTrace();
        }

        return list;
    }

    @Override
    public boolean update(User user) {
        if (user == null || user.getId() <= 0) {
            logger.warning("Tentativa de atualizar usuário inválido");
            return false;
        }

        String sql = "UPDATE users SET username=?, email=?, passwordHash=?, role=?, status=? WHERE id=?";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getUsername().trim().toLowerCase());
            stmt.setString(2, user.getEmail().trim().toLowerCase());
            stmt.setString(3, user.getPasswordHash());
            stmt.setString(4, user.getRole() != null ? user.getRole().name() : AccessLevel.STAFF.name());
            stmt.setString(5, user.getStatus() != null ? user.getStatus().name() : UserStatus.ATIVE.name());
            stmt.setInt(6, user.getId());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                logger.info("Usuário4 atualizado com sucesso: " + user.getUsername());
            }
            return affectedRows > 0;

        } catch (SQLException e) {
            logger.severe("Erro ao atualizar usuário ID " + user.getId() + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(int userId) {
        if (userId <= 0) return false;

        String sql = "DELETE FROM users WHERE id=?";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                logger.info("Usuário deletado com sucesso: ID " + userId);
            }
            return affectedRows > 0;

        } catch (SQLException e) {
            logger.severe("Erro ao deletar usuário ID " + userId + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean authenticate(String username, String passwordHash) {
        if (username == null || passwordHash == null) return false;

        User user = findByUsername(username);

        if (user == null) {
            logger.warning("Tentativa de login com usuário inexistente: " + username);
            return false;
        }

        if (user.getStatus() == UserStatus.BLOCKED) {
            logger.warning("Tentativa de login com usuário bloqueado: " + username);
            return false;
        }

        if (user.getPasswordHash().equals(passwordHash)) {
            updateLastLogin(user.getId());
            logger.info("Login bem-sucedido: " + username);
            return true;
        } else {
            logger.warning("Senha incorreta para usuário: " + username);
            return false;
        }
    }

    @Override
    public void updateLastLogin(int userId) {
        String sql = "UPDATE users SET lastLogin = NOW() WHERE id = ?";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            logger.severe("Erro ao atualizar último login: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public boolean isPasswordStrong(String password) {
        if (password == null) return false;
        return password.length() >= MIN_PASSWORD_LENGTH;
    }

    @Override
    public int countAll() {
        String sql = "SELECT COUNT(*) FROM users";

        try (Connection conn = DbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            logger.severe("Erro ao contar usuários: " + e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }

    @Override
    public User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setUsername(rs.getString("username"));
        user.setEmail(rs.getString("email"));
        user.setPasswordHash(rs.getString("passwordHash"));

        String roleStr = rs.getString("role");
        if (roleStr != null) {
            try {
                user.setRole(AccessLevel.valueOf(roleStr));
            } catch (IllegalArgumentException e) {
                logger.warning("Role inválido: " + roleStr);
                user.setRole(AccessLevel.STAFF);
            }
        }

        String statusStr = rs.getString("status");
        if (statusStr != null) {
            try {
                user.setStatus(UserStatus.valueOf(statusStr));
            } catch (IllegalArgumentException e) {
                logger.warning("Status inválido: " + statusStr);
                user.setStatus(UserStatus.ATIVE);
            }
        }

        Timestamp createDate = rs.getTimestamp("createDate");
        if (createDate != null) {
            user.setCreateDate(createDate.toLocalDateTime());
        }

        Timestamp lastLogin = rs.getTimestamp("lastLogin");
        if (lastLogin != null) {
            user.setLastLogin(lastLogin.toLocalDateTime());
        }

        return user;
    }

    @Override
    public boolean isValidUser(User user) {
        if (user == null) return false;
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) return false;
        if (user.getPasswordHash() == null || user.getPasswordHash().isEmpty()) return false;
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) return false;
        return user.getUsername().length() >= 3;
    }
}