package com.connectme.model.dao;

import com.connectme.model.entities.User;

public interface IUserDAO {
    boolean create(User user);
    User findByUsername(String username);
    boolean authenticate(String username, String passwordHash);
}
