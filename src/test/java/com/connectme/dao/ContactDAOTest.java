package com.connectme.dao;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;
import java.sql.*;

class ContactDAOTest {

    private static Connection conn;

    @BeforeAll
    static void setup() throws Exception {
        conn = DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
        Statement st = conn.createStatement();
        st.execute("CREATE TABLE users (id INT AUTO_INCREMENT PRIMARY KEY, username VARCHAR(50) UNIQUE, password_hash CHAR(64), status VARCHAR(10), failed_attempts INT DEFAULT 0)");
        st.execute("CREATE TABLE contacts (id INT AUTO_INCREMENT PRIMARY KEY, user_id INT NOT NULL, name VARCHAR(100), phone VARCHAR(20), email VARCHAR(100), address VARCHAR(200))");
        st.execute("INSERT INTO users (username, password_hash, status) VALUES ('u1','aaa','ativo')");
        st.close();
    }

    @AfterAll
    static void tearDown() throws Exception {
        if (conn != null) conn.close();
    }

    @Test
    void testCreateAndFindContact() throws Exception {
        // create
        PreparedStatement ps = conn.prepareStatement("INSERT INTO contacts (user_id, name, phone, email, address) VALUES (?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
        ps.setInt(1, 1);
        ps.setString(2, "Maria");
        ps.setString(3, "847111111");
        ps.setString(4, "maria@test");
        ps.setString(5, "Rua A");
        ps.executeUpdate();

        ResultSet keys = ps.getGeneratedKeys();
        assertTrue(keys.next());
        int id = keys.getInt(1);
        ps.close();

        // find
        PreparedStatement q = conn.prepareStatement("SELECT * FROM contacts WHERE id = ?");
        q.setInt(1, id);
        ResultSet rs = q.executeQuery();
        assertTrue(rs.next());
        assertEquals("Maria", rs.getString("name"));
        rs.close();
        q.close();

        // update
        PreparedStatement up = conn.prepareStatement("UPDATE contacts SET phone=? WHERE id=?");
        up.setString(1, "847222222");
        up.setInt(2, id);
        int rows = up.executeUpdate();
        assertEquals(1, rows);
        up.close();

        // delete
        PreparedStatement del = conn.prepareStatement("DELETE FROM contacts WHERE id=?");
        del.setInt(1, id);
        int drows = del.executeUpdate();
        assertEquals(1, drows);
        del.close();
    }
}
