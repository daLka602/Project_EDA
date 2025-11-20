package com.connectme.dao;

import com.connectme.model.User;

public interface IUserDAO {
    boolean create(User user);
    User findByUsername(String username);
    boolean authenticate(String username, String passwordHash);
}
