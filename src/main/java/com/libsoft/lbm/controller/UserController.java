package com.libsoft.lbm.controller;

import com.libsoft.lbm.dto.UserRequest;
import com.libsoft.lbm.entity.Role;
import com.libsoft.lbm.entity.User;
import com.libsoft.lbm.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserController {
    private final UserService service;

    public UserController(UserService service) { this.service = service; }

    @GetMapping("/api/roles") public List<Role> roles() { return service.findAllRoles(); }

    @GetMapping("/api/users") public List<User> all() { return service.findAll(); }
    @GetMapping("/api/users/{id}") public User one(@PathVariable Long id) { return service.findById(id); }

    @PostMapping("/api/users")
    public ResponseEntity<User> create(@Valid @RequestBody UserRequest r) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(r));
    }

    @PutMapping("/api/users/{id}")
    public User update(@PathVariable Long id, @Valid @RequestBody UserRequest r) { return service.update(id, r); }

    @DeleteMapping("/api/users/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
