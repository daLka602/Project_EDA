-- ===========================================
--   CONNECTME - BASE DE DADOS OFICIAL
-- ===========================================

DROP DATABASE IF EXISTS connectme_db;
CREATE DATABASE connectme_db;
USE connectme_db;

-- ===========================================
--   Tabela: users
-- ===========================================

CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash CHAR(64) NOT NULL,
    status ENUM('ativo', 'bloqueado') DEFAULT 'ativo',
    failed_attempts INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Usuário admin default (senha: admin)
INSERT INTO users (username, password_hash, status, failed_attempts)
VALUES (
    'admin',
    '8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918', -- SHA-256 de "admin"
    'ativo',
    0
);

-- ===========================================
--   Tabela: contacts
-- ===========================================

CREATE TABLE contacts (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    email VARCHAR(100),
    address VARCHAR(200),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Índices importantes
CREATE INDEX idx_contact_name ON contacts(name ASC);
CREATE INDEX idx_contact_phone ON contacts(phone ASC);

-- Dados de exemplo
INSERT INTO contacts (user_id, name, phone, email, address) VALUES
(1, 'Dalton Gomes', '847000000', 'dalton@example.com', 'Maputo'),
(1, 'Ana Silva', '846000000', 'ana@example.com', 'Matola');
