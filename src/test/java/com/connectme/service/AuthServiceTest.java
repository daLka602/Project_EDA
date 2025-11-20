package com.connectme.service;

import com.connectme.config.DbConnection;
import com.connectme.dao.UserDAO;
import com.connectme.model.User;
import com.connectme.util.HashUtil;

import org.junit.jupiter.api.*;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

public class AuthServiceTest {

    private Connection conn;
    private AuthService authService;

    @BeforeEach
public void setupDb() throws Exception {

    // Criar BD H2 em memória
    conn = DriverManager.getConnection("jdbc:h2:mem:testdb;MODE=MySQL;DB_CLOSE_DELAY=-1");

    Statement stmt = conn.createStatement();

    // Apagar tabela SE existir (não dá erro se não existir)
    stmt.execute("DROP TABLE IF EXISTS users");

    // Criar tabela novamente
    stmt.execute("""
        CREATE TABLE users (
            id INT AUTO_INCREMENT PRIMARY KEY,
            username VARCHAR(50) UNIQUE,
            password_hash CHAR(64),
            status VARCHAR(10),
            failed_attempts INT DEFAULT 0,
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        )
    """);

    // Inserir user
    stmt.execute("INSERT INTO users (username, password_hash, status) VALUES (" +
            "'dalton', '" + HashUtil.sha256("1234") + "', 'ACTIVE'" +
            ")");

    stmt.close();

    // DAO + Serviço
    UserDAO userDAO = new UserDAO(conn);
    authService = new AuthService(userDAO);
}

    @Test
    public void testLoginSuccess() throws Exception {
        User user = authService.login("dalton", "1234");
        assertNotNull(user);
        assertEquals("dalton", user.getUsername());
    }

    @Test
    public void testLoginFailWrongPassword() {
        assertThrows(Exception.class, () -> {
            authService.login("dalton", "senha_errada");
        });
    }

    @Test
    public void testUserBlockedAfterAttempts() {

        // 3 tentativas falhadas
        for (int i = 0; i < 3; i++) {
            try {
                authService.login("dalton", "errado");
            } catch (Exception ignored) {}
        }

        // Agora deve dar bloqueado
        assertThrows(Exception.class, () -> {
            authService.login("dalton", "1234");
        });
    }
}
