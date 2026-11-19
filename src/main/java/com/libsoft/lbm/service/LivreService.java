package com.libsoft.lbm.service;

import com.libsoft.lbm.dto.LivreRequest;
import com.libsoft.lbm.entity.Livre;
import com.libsoft.lbm.exception.ConflictException;
import com.libsoft.lbm.exception.ResourceNotFoundException;
import com.libsoft.lbm.repository.LivreRepository;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class LivreService {
    private final LivreRepository repo;
    private final AuteurService auteurService;
    private final FileStorageService fileStorage;

    public LivreService(LivreRepository repo, AuteurService auteurService, FileStorageService fileStorage) {
        this.repo = repo;
        this.auteurService = auteurService;
        this.fileStorage = fileStorage;
    }

    public List<Livre> findAll() { return repo.findAll(); }

    public Livre findById(Long id) {
        return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Livre introuvable : " + id));
    }

    public Livre create(LivreRequest r) {
        if (repo.existsByIsbn(r.isbn())) throw new ConflictException("ISBN déjà utilisé : " + r.isbn());
        Livre l = new Livre();
        apply(l, r);
        return repo.save(l);
    }

    public Livre update(Long id, LivreRequest r) {
        Livre l = findById(id);
        if (repo.existsByIsbnAndIdNot(r.isbn(), id)) throw new ConflictException("ISBN déjà utilisé : " + r.isbn());
        apply(l, r);
        return repo.save(l);
    }

    public void delete(Long id) {
        Livre l = findById(id);
        fileStorage.deleteIfExists(l.getImageUrl());
        repo.deleteById(id);
    }

    public Livre uploadImage(Long id, MultipartFile file) {
        Livre l = findById(id);
        fileStorage.deleteIfExists(l.getImageUrl());
        String url = fileStorage.storeLivreImage(id, file);
        l.setImageUrl(url);
        return repo.save(l);
    }

    public Livre deleteImage(Long id) {
        Livre l = findById(id);
        fileStorage.deleteIfExists(l.getImageUrl());
        l.setImageUrl(null);
        return repo.save(l);
    }

    public ResponseEntity<Resource> getCoverResource(Long id) {
        Livre l = findById(id);
        Resource resource = fileStorage.loadAsResource(l.getImageUrl());
        if (resource == null) return ResponseEntity.notFound().build();
        String filename = resource.getFilename() != null ? resource.getFilename() : "cover.jpg";
        String contentType = "image/jpeg";
        if (filename.endsWith(".png")) contentType = "image/png";
        else if (filename.endsWith(".webp")) contentType = "image/webp";
        return ResponseEntity.ok()
                .header(HttpHeaders.CACHE_CONTROL, "max-age=3600")
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);
    }

    private void apply(Livre l, LivreRequest r) {
        l.setTitre(r.titre());
        l.setIsbn(r.isbn());
        l.setCategorie(r.categorie());
        l.setNbPages(r.nbPages());
        l.setNbExemplaires(r.nbExemplaires());
        l.setDescription(r.description());
        l.setAuteur(auteurService.findById(r.idAuteur()));
    }
}
