// src/main/java/com/connectme/model/service/AuthService.java
package com.connectme.model.service;

import com.connectme.model.dao.IUserDAO;
import com.connectme.model.dao.UserDAO;
import com.connectme.model.entities.User;
import com.connectme.model.util.HashUtil;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class AuthService {

    private static final Logger logger = Logger.getLogger(AuthService.class.getName());
    private IUserDAO userDAO;

    // Padrões para validação
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{3,20}$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^.{4,}$"); // Mínimo 4 caracteres

    public AuthService() {
        this.userDAO = new UserDAO();
    }

    public AuthService(IUserDAO userDAO) {
        this.userDAO = userDAO;
    }

    /**
     * Login com validação melhorada
     */
    public boolean login(String username, String plainPassword) {
        if (!isValidUsername(username) && !isValidUsernameLoose(username)) {
            logger.warning("Tentativa de login com username inválido: " + username);
            return false;
        }

        if (!isValidPassword(plainPassword)) {
            logger.warning("Tentativa de login com senha inválida");
            return false;
        }

        logger.info("Buscando usuário: " + username);
        User user = userDAO.findByUsername(username);

        if (user == null) {
            logger.warning("Usuário não encontrado: " + username);
            return false;
        }

        logger.info("Usuário encontrado. Gerando hash da senha...");
        String hash = HashUtil.sha256(plainPassword);
        logger.info("Hash gerado. Hash armazenado: " + user.getPasswordHash());
        logger.info("Hash calculado: " + hash);

        boolean authenticated = userDAO.authenticate(username, hash);

        if (authenticated) {
            logger.info("Login bem-sucedido para: " + username);
        } else {
            logger.warning("Falha no login para: " + username);
        }

        return authenticated;
    }

    /**
     * Registro com validação robusta
     */
    public boolean register(String username, String plainPassword) {
        if (!isValidUsername(username)) {
            logger.warning("Username inválido para registro: " + username);
            return false;
        }

        if (!isValidPassword(plainPassword)) {
            logger.warning("Senha inválida para registro");
            return false;
        }

        // Verificar se usuário já existe
        if (userDAO.findByUsername(username) != null) {
            logger.warning("Tentativa de registrar username existente: " + username);
            return false;
        }

        String hash = HashUtil.sha256(plainPassword);
        User user = new User();
        user.setUsername(username);
        user.setPasswordHash(hash);

        boolean created = userDAO.create(user);

        if (created) {
            logger.info("Usuário1 registrado com sucesso: " + username);
        } else {
            logger.severe("Falha ao registrar usuário: " + username);
        }

        return created;
    }

    /**
     * Verificar se usuário existe
     */
    public boolean userExists(String username) {
        return userDAO.findByUsername(username) != null;
    }

    /**
     * Verificar força da senha
     */
    public PasswordStrength checkPasswordStrength(String password) {
        if (password == null) return PasswordStrength.WEAK;

        int score = 0;
        if (password.length() >= 8) score++;
        if (password.matches(".*[A-Z].*")) score++;
        if (password.matches(".*[a-z].*")) score++;
        if (password.matches(".*[0-9].*")) score++;
        if (password.matches(".*[!@#$%^&*()].*")) score++;

        switch (score) {
            case 0:
            case 1:
            case 2:
                return PasswordStrength.WEAK;
            case 3:
                return PasswordStrength.MEDIUM;
            case 4:
                return PasswordStrength.STRONG;
            case 5:
                return PasswordStrength.VERY_STRONG;
            default:
                return PasswordStrength.WEAK;
        }
    }

    public User findUser(String username) {
        return userDAO.findByUsername(username);
    }

    // Validações
    private boolean isValidUsername(String username) {
        return username != null &&
                USERNAME_PATTERN.matcher(username).matches() &&
                !username.trim().isEmpty();
    }

    private boolean isValidUsernameLoose(String username) {
        return username != null &&
                username.length() >= 3 &&
                !username.trim().isEmpty();
    }

    private boolean isValidPassword(String password) {
        return password != null &&
                PASSWORD_PATTERN.matcher(password).matches();
    }

    // Enum para força da senha
    public enum PasswordStrength {
        WEAK, MEDIUM, STRONG, VERY_STRONG
    }
}