package com.connectme.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;
import java.util.logging.Logger;

public class DbConnection {

    private static final Logger logger = Logger.getLogger(DbConnection.class.getName());
    private static HikariDataSource dataSource;

    static {
        initializeDataSource();
    }

    private static void initializeDataSource() {
        try (InputStream input = DbConnection.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                throw new RuntimeException("application.properties não encontrado no classpath");
            }
            Properties prop = new Properties();
            prop.load(input);

            String url = prop.getProperty("db.url");
            String user = prop.getProperty("db.user");
            String password = prop.getProperty("db.password");

            // Configuração HikariCP
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(url);
            config.setUsername(user);
            config.setPassword(password);
            
            // Otimizações para MySQL
            config.setDriverClassName("com.mysql.cj.jdbc.Driver");
            config.setMaximumPoolSize(20);
            config.setMinimumIdle(5);
            config.setConnectionTimeout(30000); // 30 segundos
            config.setIdleTimeout(600000); // 10 minutos
            config.setMaxLifetime(1800000); // 30 minutos
            config.setConnectionTestQuery("SELECT 1");
            
            // Otimizações específicas MySQL
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            config.addDataSourceProperty("useServerPrepStmts", "true");
            config.addDataSourceProperty("useLocalSessionState", "true");
            config.addDataSourceProperty("rewriteBatchedStatements", "true");
            config.addDataSourceProperty("cacheResultSetMetadata", "true");
            config.addDataSourceProperty("cacheServerConfiguration", "true");
            config.addDataSourceProperty("elideSetAutoCommits", "true");
            config.addDataSourceProperty("maintainTimeStats", "false");

            dataSource = new HikariDataSource(config);
            logger.info("✅ HikariCP DataSource inicializado com sucesso");

        } catch (IOException e) {
            throw new RuntimeException("Erro ao carregar configurações DB: " + e.getMessage(), e);
        }
    }

    public static Connection getConnection() {
        try {
            Connection conn = dataSource.getConnection();
            logger.fine("✅ Conexão obtida do pool HikariCP");
            return conn;
        } catch (SQLException e) {
            logger.severe("❌ Erro ao obter conexão do pool: " + e.getMessage());
            throw new RuntimeException("Erro ao conectar ao banco de dados: " + e.getMessage(), e);
        }
    }

    /**
     * Fechar DataSource (para shutdown graceful)
     */
    public static void closeDataSource() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            logger.info("✅ DataSource HikariCP fechado");
        }
    }

    /**
     * Estatísticas do pool (útil para debug)
     */
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

    /**
     * Testar conexão
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            boolean isValid = conn != null && !conn.isClosed();
            if (isValid) {
                logger.info("✅ Teste de conexão MySQL: OK");
                printPoolStats();
            }
            return isValid;
        } catch (SQLException e) {
            logger.severe("❌ Teste de conexão MySQL falhou: " + e.getMessage());
            return false;
        }
    }
}