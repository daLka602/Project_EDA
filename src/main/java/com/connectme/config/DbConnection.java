package com.connectme.config;

import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

public class DbConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/connectme";
    private static final String USER = "root"; // <-- Substitua pelo seu usuário MySQL
    private static final String PASSWORD = "Mirrine22!";
    private static final Logger logger = Logger.getLogger(DbConnection.class.getName());
    private static HikariDataSource dataSource;

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {

            throw new SQLException("Driver JDBC não encontrado. Verifique se o mysql-connector-j.jar está no classpath.", e);
        }
    }

    public static void initialize() {
        try (Connection conn = getConnection()) {
            String[] sqlStatements = {
                    // Tabela de contactos com enums corretos
                    "CREATE TABLE IF NOT EXISTS contacts (" +
                            "id INT AUTO_INCREMENT PRIMARY KEY, " +
                            "name VARCHAR(100) NOT NULL, " +
                            "company VARCHAR(100), " +
                            "phone VARCHAR(20) NOT NULL, " +
                            "email VARCHAR(100), " +
                            "type ENUM('CUSTOMER', 'PARTNER', 'SUPPLIER') DEFAULT 'CUSTOMER', " +
                            "address VARCHAR(255), " +
                            "description TEXT, " +
                            "createDate DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                            "INDEX idx_name (name), " +
                            "INDEX idx_type (type)" +
                            ")"
            };

            for (String sql : sqlStatements) {
                try (Statement stmt = conn.createStatement()) {
                    stmt.execute(sql);
                }
            }

            logger.info("Database initialized successfully with correct ENUMs");
        } catch (SQLException e) {
            logger.severe("Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void closeDataSource() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            logger.info("✅ DataSource HikariCP fechado");
        }
    }
}