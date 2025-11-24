package com.connectme.model.dao;

import com.connectme.model.entities.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public interface IUserDAO {
    boolean create(User user);
    User findByUsername(String username);

    User findById(int id);

    List<User> findAll();

    boolean update(User user);

    boolean delete(int userId);

    boolean authenticate(String username, String passwordHash);

    void updateLastLogin(int userId);

    boolean isPasswordStrong(String password);

    int countAll();

    User mapResultSetToUser(ResultSet rs) throws SQLException;

    boolean isValidUser(User user);
}
