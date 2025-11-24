package com.connectme.controller;

import com.connectme.model.dao.ContactDAO;
import com.connectme.model.dao.UserDAO;
import com.connectme.model.entities.Contact;
import com.connectme.model.entities.User;
import com.connectme.model.enums.ContactType;
import com.connectme.model.enums.UserStatus;

import java.util.List;
import java.util.logging.Logger;

public class AdminController {

    private static final Logger logger = Logger.getLogger(AdminController.class.getName());
    private final UserDAO userDAO;
    private final ContactDAO contactDAO;

    public AdminController() {
        this.userDAO = new UserDAO();
        this.contactDAO = new ContactDAO();
    }

    // ===== GESTÃO DE USUÁRIOS =====

    /**
     * Listar todos os usuários
     */
    public List<User> listAllUsers() {
        return userDAO.findAll();
    }

    /**
     * Buscar usuário por ID
     */
    public User findUserById(int id) {
        return userDAO.findById(id);
    }

    /**
     * Buscar usuário por username
     */
    public User findUserByUsername(String username) {
        return userDAO.findByUsername(username);
    }

    /**
     * Criar novo usuário (apenas admin)
     */
    public boolean createUser(User user) {
        if (!isValidUser(user)) {
            logger.warning("Tentativa de criar usuário inválido");
            return false;
        }
        return userDAO.create(user);
    }

    /**
     * Atualizar usuário
     */
    public boolean updateUser(User user) {
        if (user == null || user.getId() <= 0) {
            logger.warning("Tentativa de atualizar usuário inválido");
            return false;
        }
        return userDAO.update(user);
    }

    /**
     * Deletar usuário
     */
    public boolean deleteUser(int userId) {
        if (userId <= 0) return false;
        return userDAO.delete(userId);
    }

    /**
     * Ativar/Desativar usuário
     */
    public boolean toggleUserStatus(int userId) {
        User user = userDAO.findById(userId);
        if (user == null) return false;

        UserStatus newStatus = user.getStatus() == UserStatus.ATIVE
                ? UserStatus.BLOCKED
                : UserStatus.ATIVE;

        user.setStatus(newStatus);
        return userDAO.update(user);
    }

    /**
     * Contar total de usuários
     */
    public int countTotalUsers() {
        return userDAO.countAll();
    }

    /**
     * Contar usuários ativos
     */
    public long countActiveUsers() {
        return userDAO.findAll().stream()
                .filter(u -> u.getStatus() == UserStatus.ATIVE)
                .count();
    }

    // ===== ESTATÍSTICAS =====

    /**
     * Obter estatísticas do sistema
     */
    public SystemStats getSystemStats() {
        List<User> users = userDAO.findAll();
        List<Contact> contacts = contactDAO.findAll();

        int totalUsers = users.size();
        int activeUsers = (int) users.stream()
                .filter(u -> u.getStatus() == UserStatus.ATIVE)
                .count();
        int totalContacts = contacts.size();
        double activationRate = totalUsers > 0 ? (activeUsers * 100.0 / totalUsers) : 0;

        long customers = contacts.stream()
                .filter(c -> c.getType() == ContactType.CUSTOMER)
                .count();
        long partners = contacts.stream()
                .filter(c -> c.getType() == ContactType.PARTNER)
                .count();
        long suppliers = contacts.stream()
                .filter(c -> c.getType() == ContactType.SUPPLIER)
                .count();

        return new SystemStats(totalUsers, activeUsers, totalContacts, activationRate,
                (int) customers, (int) partners, (int) suppliers);
    }

    private boolean isValidUser(User user) {
        if (user == null) return false;
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) return false;
        if (user.getPasswordHash() == null || user.getPasswordHash().isEmpty()) return false;
        if (user.getUsername().length() < 3) return false;
        return true;
    }

    // ===== CLASSE INTERNA =====

    public static class SystemStats {
        public final int totalUsers;
        public final int activeUsers;
        public final int totalContacts;
        public final double activationRate;
        public final int customers;
        public final int partners;
        public final int suppliers;

        public SystemStats(int totalUsers, int activeUsers, int totalContacts,
                           double activationRate, int customers, int partners, int suppliers) {
            this.totalUsers = totalUsers;
            this.activeUsers = activeUsers;
            this.totalContacts = totalContacts;
            this.activationRate = activationRate;
            this.customers = customers;
            this.partners = partners;
            this.suppliers = suppliers;
        }

        @Override
        public String toString() {
            return String.format(
                    "SystemStats{users=%d, active=%d, contacts=%d, rate=%.1f%%, customers=%d, partners=%d, suppliers=%d}",
                    totalUsers, activeUsers, totalContacts, activationRate, customers, partners, suppliers
            );
        }
    }
}
