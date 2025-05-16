package com.controle.fechamentocaixa.service;

import com.controle.fechamentocaixa.model.User;
import java.util.List;

public interface UserService {
    User createUser(User user);
    User updateUser(String id, User user);
    void deleteUser(String id);
    User getUserById(String id);
    User getUserByUsername(String username);
    List<User> getAllUsers();
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
} 