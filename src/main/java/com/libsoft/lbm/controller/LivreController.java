package com.libsoft.lbm.controller;

import com.libsoft.lbm.dto.LivreRequest;
import com.libsoft.lbm.entity.Livre;
import com.libsoft.lbm.service.LivreService;
import jakarta.validation.Valid;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/livres")
public class LivreController {
    private final LivreService service;

    public LivreController(LivreService service) { this.service = service; }

    @GetMapping
    public List<Livre> all() { return service.findAll(); }

    @GetMapping("/{id}")
    public Livre one(@PathVariable Long id) { return service.findById(id); }

    @PostMapping
    public ResponseEntity<Livre> create(@Valid @RequestBody LivreRequest r) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(r));
    }

    @PutMapping("/{id}")
    public Livre update(@PathVariable Long id, @Valid @RequestBody LivreRequest r) {
        return service.update(id, r);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Upload ou remplacement de l'image de couverture.
     * Content-Type: multipart/form-data
     * Champ: file (ou image)
     */
    @PostMapping(value = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Livre uploadImage(@PathVariable Long id,
                             @RequestParam("file") MultipartFile file) {
        return service.uploadImage(id, file);
    }

    @DeleteMapping("/{id}/image")
    public Livre deleteImage(@PathVariable Long id) {
        return service.deleteImage(id);
    }

    /**
     * Sert l'image de couverture (public, pour les balises <img>).
     * Retourne 404 si pas d'image.
     */
    @GetMapping("/{id}/cover")
    public ResponseEntity<Resource> getCover(@PathVariable Long id) {
        return service.getCoverResource(id);
    }

}
