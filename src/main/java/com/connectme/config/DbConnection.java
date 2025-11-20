package com.connectme.config;

import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

public class DbConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/connectme_db";
    private static final String USER = "root"; // <-- Substitua pelo seu usuário MySQL
    private static final String PASSWORD = "Mirrine22!";
    private static final Logger logger = Logger.getLogger(DbConnection.class.getName());
    private static HikariDataSource dataSource;

    public static Connection getConnection() throws SQLException {
        try {
            // Carrega o driver JDBC do MySQL (necessário para versões mais antigas do JDBC)
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {

            throw new SQLException("Driver JDBC não encontrado. Verifique se o mysql-connector-j.jar está no classpath.", e);
        }
    }

    public static void closeDataSource() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            logger.info("✅ DataSource HikariCP fechado");
        }
    }

    public static void printPoolStats() {
        if (dataSource != null) {
            logger.info(String.format(
                    "🏊 HikariCP Stats - Active: %d, Idle: %d, Total: %d, Waiting: %d",
                    dataSource.getHikariPoolMXBean().getActiveConnections(),
                    dataSource.getHikariPoolMXBean().getIdleConnections(),
                    dataSource.getHikariPoolMXBean().getTotalConnections(),
                    dataSource.getHikariPoolMXBean().getThreadsAwaitingConnection()
            ));
        }
    }
}