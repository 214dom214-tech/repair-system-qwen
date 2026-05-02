package com.example.repairsystem.controller;

import com.example.repairsystem.model.Role;
import com.example.repairsystem.model.User;
import com.example.repairsystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/admin/users")
public class AdminController {

    @Autowired
    private UserService userService;

    /** GET /api/admin/users — список всех пользователей */
    @GetMapping
    public List<User> getAll() {
        return userService.getAll();
    }

    /** POST /api/admin/users — создать пользователя
     *  Body: { "username": "ivan", "password": "pass123", "roles": ["ROLE_CREATOR", "ROLE_WORKER"] }
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(@RequestBody CreateUserRequest req) {
        return userService.create(req.username(), req.password(), req.roles());
    }

    /** PUT /api/admin/users/{id}/roles — назначить роли
     *  Body: ["ROLE_CREATOR", "ROLE_WORKER"]
     */
    @PutMapping("/{id}/roles")
    public User setRoles(@PathVariable Long id, @RequestBody Set<Role> roles) {
        return userService.setRoles(id, roles);
    }

    /** PATCH /api/admin/users/{id}/enabled — заблокировать / разблокировать
     *  Body: { "enabled": false }
     */
    @PatchMapping("/{id}/enabled")
    public User setEnabled(@PathVariable Long id, @RequestBody Map<String, Boolean> body) {
        return userService.setEnabled(id, body.get("enabled"));
    }

    /** DELETE /api/admin/users/{id} */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id) {
        userService.delete(id);
    }

    // DTO через record (Java 16+)
    public record CreateUserRequest(String username, String password, Set<Role> roles) {}
}
