package com.connectme.model.service;

import com.connectme.model.dao.IUserDAO;
import com.connectme.model.dao.UserDAO;
import com.connectme.model.entities.User;
import com.connectme.model.util.HashUtil;

/**
 * Serviço de autenticação: encapsula regras de negócio ligadas a login e registo.
 */
public class AuthService {

    private IUserDAO userDAO;

    public AuthService(UserDAO userDAO2) {
        // implementa a criação direta do DAO usando a conexão centralizada
        this.userDAO = new UserDAO();
    }

    public AuthService() {

    }

    /**
     * Tenta autenticar o utilizador.
     *
     * @param username nome do utilizador
     * @param plainPassword senha em texto puro
     * @return true se autenticação bem sucedida
     */
    public boolean login(String username, String plainPassword) {
        if (username == null || username.isBlank() || plainPassword == null) return false;

        String hash = HashUtil.sha256(plainPassword);
        return userDAO.authenticate(username, hash);
    }

    /**
     * Regista um novo utilizador no sistema.
     * - Gera hash SHA-256 da senha
     * - Garante unicidade no DAO (UserDAO.create deve tratar exceção)
     *
     * @param username nome de utilizador
     * @param plainPassword senha em texto puro
     * @return true se criado com sucesso
     */
    public boolean register(String username, String plainPassword) {
        if (username == null || username.isBlank() || plainPassword == null || plainPassword.length() < 4) {
            return false;
        }

        String hash = HashUtil.sha256(plainPassword);
        User user = new User(username, hash);
        return userDAO.create(user);
    }

    /**
     * Método utilitário para verificar a existência de um utilizador.
     */
    public User findUser(String username) {
        return userDAO.findByUsername(username);
    }
}
