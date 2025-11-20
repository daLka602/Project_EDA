package com.connectme.model.dao;

import com.connectme.model.entities.Contact;
import com.connectme.config.DbConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class ContactDAO {

    private static final Logger logger = Logger.getLogger(ContactDAO.class.getName());

    public boolean create(Contact c) {
        if (!isValidContact(c)) {
            logger.warning("Tentativa de criar contacto inválido: " + c);
            return false;
        }

        String sql = "INSERT INTO contacts (user_id, name, phone, email, address) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, c.getUserId());
            stmt.setString(2, c.getName().trim());
            stmt.setString(3, c.getPhone() != null ? c.getPhone().trim() : null);
            stmt.setString(4, c.getEmail() != null ? c.getEmail().trim() : null);
            stmt.setString(5, c.getAddress() != null ? c.getAddress().trim() : null);

            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                // Obter o ID gerado
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        c = new Contact(
                            generatedKeys.getInt(1),
                            c.getUserId(),
                            c.getName(),
                            c.getPhone(),
                            c.getEmail(),
                            c.getAddress()
                        );
                    }
                }
                return true;
            }
            return false;

        } catch (SQLException e) {
            logger.severe("Erro ao criar contato: " + e.getMessage());
            return false;
        }
    }

    public boolean update(Contact c) {
        if (!isValidContact(c) || c.getId() <= 0) {
            logger.warning("Tentativa de atualizar contacto inválido: " + c);
            return false;
        }

        String sql = "UPDATE contacts SET name=?, phone=?, email=?, address=? WHERE id=? AND user_id=?";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, c.getName().trim());
            stmt.setString(2, c.getPhone() != null ? c.getPhone().trim() : null);
            stmt.setString(3, c.getEmail() != null ? c.getEmail().trim() : null);
            stmt.setString(4, c.getAddress() != null ? c.getAddress().trim() : null);
            stmt.setInt(5, c.getId());
            stmt.setInt(6, c.getUserId()); // Segurança: só atualiza se for do user

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            logger.severe("Erro ao atualizar contato ID " + c.getId() + ": " + e.getMessage());
            return false;
        }
    }

    public boolean delete(int contactId) {
        if (contactId <= 0) return false;

        String sql = "DELETE FROM contacts WHERE id=?";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, contactId);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            logger.severe("Erro ao deletar contato ID " + contactId + ": " + e.getMessage());
            return false;
        }
    }

    public Contact findById(int id) {
        if (id <= 0) return null;

        String sql = "SELECT * FROM contacts WHERE id=?";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToContact(rs);
            }

        } catch (SQLException e) {
            logger.severe("Erro ao buscar contato ID " + id + ": " + e.getMessage());
        }

        return null;
    }

    public List<Contact> findAllByUser(int userId) {
        if (userId <= 0) return List.of();

        String sql = "SELECT * FROM contacts WHERE user_id=? ORDER BY name ASC";
        List<Contact> list = new ArrayList<>();

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(mapResultSetToContact(rs));
            }

        } catch (SQLException e) {
            logger.severe("Erro ao listar contatos do user " + userId + ": " + e.getMessage());
        }

        return list;
    }

    /**
     * NOVO: Busca com paginação
     */
    public List<Contact> findPaginatedByUser(int userId, int page, int pageSize) {
        if (userId <= 0 || page < 0 || pageSize <= 0) return List.of();

        String sql = "SELECT * FROM contacts WHERE user_id=? ORDER BY name ASC LIMIT ? OFFSET ?";
        List<Contact> list = new ArrayList<>();

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, pageSize);
            stmt.setInt(3, page * pageSize);
            
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(mapResultSetToContact(rs));
            }

        } catch (SQLException e) {
            logger.severe("Erro na paginação de contatos: " + e.getMessage());
        }

        return list;
    }

    /**
     * NOVO: Contar total de contactos por user
     */
    public int countByUser(int userId) {
        if (userId <= 0) return 0;

        String sql = "SELECT COUNT(*) FROM contacts WHERE user_id=?";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            logger.severe("Erro ao contar contatos: " + e.getMessage());
        }

        return 0;
    }

    private Contact mapResultSetToContact(ResultSet rs) throws SQLException {
        return new Contact(
            rs.getInt("id"),
            rs.getInt("user_id"),
            rs.getString("name"),
            rs.getString("phone"),
            rs.getString("email"),
            rs.getString("address")
        );
    }

    private boolean isValidContact(Contact c) {
        if (c == null) return false;
        if (c.getUserId() <= 0) return false;
        if (c.getName() == null || c.getName().trim().isEmpty()) return false;
        if (c.getPhone() == null || c.getPhone().trim().isEmpty()) return false;
        return true;
    }
}