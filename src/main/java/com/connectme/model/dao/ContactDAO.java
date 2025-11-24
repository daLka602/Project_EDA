// src/main/java/com/connectme/model/dao/ContactDAO.java
package com.connectme.model.dao;

import com.connectme.model.entities.Contact;
import com.connectme.model.enums.ContactType;
import com.connectme.config.DbConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class ContactDAO implements IContactDAO {

    private static final Logger logger = Logger.getLogger(ContactDAO.class.getName());

    @Override
    public boolean create(Contact contact) {
        if (!isValidContact(contact)) {
            logger.warning("Tentativa de criar contacto inválido: " + contact);
            return false;
        }

        String sql = "INSERT INTO contacts (name, company, phone, email, type, address, description, createDate) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, NOW())";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, contact.getName().trim());
            stmt.setString(2, contact.getCompany() != null ? contact.getCompany().trim() : null);
            stmt.setString(3, contact.getPhone() != null ? contact.getPhone().trim() : null);
            stmt.setString(4, contact.getEmail() != null ? contact.getEmail().trim() : null);

            // Usar enum.name() para salvar CUSTOMER, PARTNER, SUPPLIER
            stmt.setString(5, contact.getType() != null ? contact.getType().name() : ContactType.CUSTOMER.name());

            stmt.setString(6, contact.getAddress() != null ? contact.getAddress().trim() : null);
            stmt.setString(7, contact.getDescription() != null ? contact.getDescription().trim() : null);

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        contact.setId(generatedKeys.getInt(1));
                    }
                }
                logger.info("Contacto criado com sucesso: " + contact.getName());
                return true;
            }
            return false;

        } catch (SQLException e) {
            logger.severe("Erro ao criar contacto: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(Contact contact) {
        if (!isValidContact(contact) || contact.getId() <= 0) {
            logger.warning("Tentativa de atualizar contacto inválido: " + contact);
            return false;
        }

        String sql = "UPDATE contacts SET name=?, company=?, phone=?, email=?, type=?, address=?, description=? WHERE id=?";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, contact.getName().trim());
            stmt.setString(2, contact.getCompany() != null ? contact.getCompany().trim() : null);
            stmt.setString(3, contact.getPhone() != null ? contact.getPhone().trim() : null);
            stmt.setString(4, contact.getEmail() != null ? contact.getEmail().trim() : null);
            stmt.setString(5, contact.getType() != null ? contact.getType().name() : ContactType.CUSTOMER.name());
            stmt.setString(6, contact.getAddress() != null ? contact.getAddress().trim() : null);
            stmt.setString(7, contact.getDescription() != null ? contact.getDescription().trim() : null);
            stmt.setInt(8, contact.getId());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                logger.info("Contacto atualizado com sucesso: " + contact.getName());
            }
            return affectedRows > 0;

        } catch (SQLException e) {
            logger.severe("Erro ao atualizar contacto ID " + contact.getId() + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(int contactId) {
        if (contactId <= 0) return false;

        String sql = "DELETE FROM contacts WHERE id=?";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, contactId);
            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                logger.info("Contacto deletado com sucesso: ID " + contactId);
            }
            return affectedRows > 0;

        } catch (SQLException e) {
            logger.severe("Erro ao deletar contacto ID " + contactId + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
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
            logger.severe("Erro ao buscar contacto ID " + id + ": " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public List<Contact> findAll() {
        String sql = "SELECT * FROM contacts ORDER BY name ASC";
        List<Contact> list = new ArrayList<>();

        try (Connection conn = DbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(mapResultSetToContact(rs));
            }

        } catch (SQLException e) {
            logger.severe("Erro ao listar contactos: " + e.getMessage());
            e.printStackTrace();
        }

        return list;
    }

    @Override
    public List<Contact> findByType(ContactType type) {
        if (type == null) return new ArrayList<>();

        String sql = "SELECT * FROM contacts WHERE type=? ORDER BY name ASC";
        List<Contact> list = new ArrayList<>();

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, type.name());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(mapResultSetToContact(rs));
            }

        } catch (SQLException e) {
            logger.severe("Erro ao buscar contactos por tipo: " + e.getMessage());
            e.printStackTrace();
        }

        return list;
    }

    @Override
    public List<Contact> search(String query) {
        if (query == null || query.trim().isEmpty()) {
            return findAll();
        }

        String searchTerm = "%" + query.toLowerCase() + "%";
        String sql = "SELECT * FROM contacts WHERE LOWER(name) LIKE ? OR LOWER(email) LIKE ? " +
                "OR phone LIKE ? OR LOWER(company) LIKE ? ORDER BY name ASC";
        List<Contact> list = new ArrayList<>();

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, searchTerm);
            stmt.setString(2, searchTerm);
            stmt.setString(3, searchTerm);
            stmt.setString(4, searchTerm);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(mapResultSetToContact(rs));
            }

        } catch (SQLException e) {
            logger.severe("Erro ao pesquisar contactos: " + e.getMessage());
            e.printStackTrace();
        }

        return list;
    }

    @Override
    public int countAll() {
        String sql = "SELECT COUNT(*) FROM contacts";

        try (Connection conn = DbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            logger.severe("Erro ao contar contactos: " + e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }

    @Override
    public List<Contact> findPaginated(int page, int pageSize) {
        if (page < 0 || pageSize <= 0) return new ArrayList<>();

        String sql = "SELECT * FROM contacts ORDER BY name ASC LIMIT ? OFFSET ?";
        List<Contact> list = new ArrayList<>();

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, pageSize);
            stmt.setInt(2, page * pageSize);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(mapResultSetToContact(rs));
            }

        } catch (SQLException e) {
            logger.severe("Erro na paginação de contactos: " + e.getMessage());
            e.printStackTrace();
        }

        return list;
    }

    @Override
    public Contact mapResultSetToContact(ResultSet rs) throws SQLException {
        Contact contact = new Contact();
        contact.setId(rs.getInt("id"));
        contact.setName(rs.getString("name"));
        contact.setCompany(rs.getString("company"));
        contact.setPhone(rs.getString("phone"));
        contact.setEmail(rs.getString("email"));

        String typeStr = rs.getString("type");
        if (typeStr != null) {
            try {
                contact.setType(ContactType.valueOf(typeStr));
            } catch (IllegalArgumentException e) {
                logger.warning("Tipo de contacto inválido: " + typeStr);
                contact.setType(ContactType.CUSTOMER);
            }
        }

        contact.setAddress(rs.getString("address"));
        contact.setDescription(rs.getString("description"));

        Timestamp createDate = rs.getTimestamp("createDate");
        if (createDate != null) {
            contact.setCreateDate(createDate.toLocalDateTime());
        }

        return contact;
    }

    @Override
    public boolean isValidContact(Contact contact) {
        if (contact == null) return false;
        if (contact.getName() == null || contact.getName().trim().isEmpty()) return false;
        if (contact.getPhone() == null || contact.getPhone().trim().isEmpty()) return false;
        return true;
    }
}