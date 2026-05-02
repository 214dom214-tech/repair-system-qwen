package com.example.repairsystem.service;

import com.example.repairsystem.model.Role;
import com.example.repairsystem.model.User;
import com.example.repairsystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<User> getAll() {
        return userRepository.findAll();
    }

    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Пользователь с id=" + id + " не найден"));
    }

    public User create(String username, String rawPassword, Set<Role> roles) {
        if (userRepository.existsByUsername(username)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Пользователь '" + username + "' уже существует");
        }
        User user = new User(username, passwordEncoder.encode(rawPassword), roles);
        return userRepository.save(user);
    }

    /** Администратор переназначает роли пользователю */
    public User setRoles(Long id, Set<Role> roles) {
        // Нельзя снять ROLE_ADMIN с самого себя — защита от локаута
        User user = getById(id);
        user.setRoles(roles);
        return userRepository.save(user);
    }

    public User setEnabled(Long id, boolean enabled) {
        User user = getById(id);
        user.setEnabled(enabled);
        return userRepository.save(user);
    }

    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Пользователь с id=" + id + " не найден");
        }
        userRepository.deleteById(id);
    }
}
