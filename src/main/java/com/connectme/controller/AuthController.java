package com.connectme.controller;

import com.connectme.model.entities.User;
import com.connectme.model.enums.PasswordStrength;
import com.connectme.model.service.AuthService;
import java.util.logging.Logger;

public class AuthController {

    private static final Logger logger = Logger.getLogger(AuthController.class.getName());
    private final AuthService authService;
    private User loggedInUser;

    public AuthController() {
        this.authService = new AuthService();
    }

    /**
     * Realizar login
     */
    public boolean login(String username, String plainPassword) {
        if (username == null || plainPassword == null) {
            logger.warning("Login com credenciais nulas");
            return false;
        }

        logger.info("Autenticando usuário: " + username);

        boolean authenticated = authService.login(username, plainPassword);
        if (authenticated) {
            this.loggedInUser = authService.findUser(username);
            if (loggedInUser != null) {
                logger.info("Usuário conectado com sucesso: " + username);
            } else {
                logger.warning("Usuário não encontrado após autenticação: " + username);
                return false;
            }
        } else {
            logger.warning("Autenticação falhou para: " + username);
        }
        return authenticated;
    }

    /**
     * Realizar logout
     */
    public void logout() {
        if (loggedInUser != null) {
            logger.info("Usuário desconectado: " + loggedInUser.getUsername());
        }
        this.loggedInUser = null;
    }

    /**
     * Verificar se há usuário autenticado
     */
    public boolean isAuthenticated() {
        return loggedInUser != null;
    }

    /**
     * Obter usuário autenticado
     */
    public User getLoggedInUser() {
        return loggedInUser;
    }

    /**
     * Registrar novo usuário
     */
    public boolean register(String username, String plainPassword) {
        if (username == null || plainPassword == null) {
            logger.warning("Registro com credenciais nulas");
            return false;
        }

        boolean registered = authService.register(username, plainPassword);
        if (registered) {
            logger.info("Novo usuário registrado: " + username);
        }
        return registered;
    }

    /**
     * Verificar força da senha
     */
    public PasswordStrength checkPasswordStrength(String password) {
        return authService.checkPasswordStrength(password);
    }

    /**
     * Verificar se usuário existe
     */
    public boolean userExists(String username) {
        return authService.userExists(username);
    }
}