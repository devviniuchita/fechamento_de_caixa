package com.seucodigo.fecharcaixa.service;

import com.seucodigo.fecharcaixa.dto.UserDTO;
import com.seucodigo.fecharcaixa.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {
    User createUser(UserDTO userDTO);
    User updateUser(String id, UserDTO userDTO);
    void deleteUser(String id);
    User getUserById(String id);
    User getUserByUsername(String username);
    List<User> getAllUsers();
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
} 