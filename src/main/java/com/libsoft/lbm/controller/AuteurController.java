package com.libsoft.lbm.controller;

import com.libsoft.lbm.entity.Auteur;
import com.libsoft.lbm.service.AuteurService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auteurs")
public class AuteurController {
    private final AuteurService service;

    public AuteurController(AuteurService service) { this.service = service; }

    @GetMapping public List<Auteur> all() { return service.findAll(); }
    @GetMapping("/{id}") public Auteur one(@PathVariable Long id) { return service.findById(id); }

    @PostMapping
    public ResponseEntity<Auteur> create(@Valid @RequestBody Auteur a) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(a));
    }

    @PutMapping("/{id}")
    public Auteur update(@PathVariable Long id, @Valid @RequestBody Auteur a) { return service.update(id, a); }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
