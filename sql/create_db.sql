-- ConnectMe Database Schema
-- Versão 2.0 - Com índices e otimizações

CREATE DATABASE IF NOT EXISTS connectme_db;
USE connectme_db;

-- Tabela de usuários com melhorias
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(64) NOT NULL, -- SHA-256 = 64 chars hex
    status ENUM('ativo', 'bloqueado') DEFAULT 'ativo',
    failed_attempts INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP NULL,
    INDEX idx_username (username),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabela de contactos com melhorias
CREATE TABLE IF NOT EXISTS contacts (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    email VARCHAR(100),
    address TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_name (name),
    INDEX idx_phone (phone),
    INDEX idx_email (email),
    INDEX idx_created_at (created_at),
    UNIQUE KEY unique_user_phone (user_id, phone) -- Evita telefones duplicados por usuário
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabela de logs do sistema (opcional)
CREATE TABLE IF NOT EXISTS system_logs (
    id INT AUTO_INCREMENT PRIMARY KEY,
    level ENUM('INFO', 'WARNING', 'ERROR') NOT NULL,
    message TEXT NOT NULL,
    user_id INT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_level (level),
    INDEX idx_created_at (created_at),
    INDEX idx_user_id (user_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Inserir usuário de exemplo (senha: "admin123")
INSERT IGNORE INTO users (username, password_hash) VALUES 
('admin', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9');

-- Inserir contactos de exemplo
INSERT IGNORE INTO contacts (user_id, name, phone, email, address) VALUES 
(1, 'Dalton Falaque', '+258878600296', 'lauterdaltongomes@gmail.com', 'Av. karl max, Maputo'),
(1, 'Maria Santos', '+351923456789', 'maria.santos@email.com', 'Avenida Central, 456, Porto'),
(1, 'Carlos Oliveira', '+351934567890', NULL, NULL),
(1, 'Ana Costa', '+351945678901', 'ana.costa@email.com', 'Travessa do Comércio, 789, Braga'),
(1, 'Pedro Martins', '+351956789012', NULL, 'Praça da Liberdade, 321, Coimbra');