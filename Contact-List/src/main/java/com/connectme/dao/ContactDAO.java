package com.connectme.dao;

import com.connectme.model.Contact;
import com.connectme.config.DbConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ContactDAO {

    public boolean create(Contact c) {
        String sql = "INSERT INTO contacts (user_id, name, phone, email, address) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, c.getUserId());
            stmt.setString(2, c.getName());
            stmt.setString(3, c.getPhone());
            stmt.setString(4, c.getEmail());
            stmt.setString(5, c.getAddress());

            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Erro ao criar contato: " + e.getMessage());
            return false;
        }
    }

    public boolean update(Contact c) {
        String sql = "UPDATE contacts SET name=?, phone=?, email=?, address=? WHERE id=?";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, c.getName());
            stmt.setString(2, c.getPhone());
            stmt.setString(3, c.getEmail());
            stmt.setString(4, c.getAddress());
            stmt.setInt(5, c.getId());

            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Erro ao atualizar contato: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(int contactId) {
        String sql = "DELETE FROM contacts WHERE id=?";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, contactId);
            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Erro ao deletar contato: " + e.getMessage());
            return false;
        }
    }

    public Contact findById(int id) {
        String sql = "SELECT * FROM contacts WHERE id=?";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Contact(
                    rs.getInt("id"),
                    rs.getInt("user_id"),
                    rs.getString("name"),
                    rs.getString("phone"),
                    rs.getString("email"),
                    rs.getString("address")
                );
            }

        } catch (SQLException e) {
            System.out.println("Erro ao buscar contato: " + e.getMessage());
        }

        return null;
    }

    public List<Contact> findAllByUser(int userId) {
        String sql = "SELECT * FROM contacts WHERE user_id=? ORDER BY name ASC";
        List<Contact> list = new ArrayList<>();

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Contact c = new Contact(
                    rs.getInt("id"),
                    rs.getInt("user_id"),
                    rs.getString("name"),
                    rs.getString("phone"),
                    rs.getString("email"),
                    rs.getString("address")
                );
                list.add(c);
            }

        } catch (SQLException e) {
            System.out.println("Erro ao listar contatos: " + e.getMessage());
        }

        return list;
    }
}
