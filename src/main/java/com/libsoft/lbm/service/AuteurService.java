package com.libsoft.lbm.service;

import com.libsoft.lbm.entity.Auteur;
import com.libsoft.lbm.exception.ConflictException;
import com.libsoft.lbm.exception.ResourceNotFoundException;
import com.libsoft.lbm.repository.AuteurRepository;
import com.libsoft.lbm.repository.LivreRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuteurService {
    private final AuteurRepository repo;
    private final LivreRepository livreRepo;

    public AuteurService(AuteurRepository repo, LivreRepository livreRepo) {
        this.repo = repo;
        this.livreRepo = livreRepo;
    }

    public List<Auteur> findAll() { return repo.findAll(); }

    public Auteur findById(Long id) {
        return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Auteur introuvable : " + id));
    }

    public Auteur create(Auteur a) { a.setId(null); return repo.save(a); }

    public Auteur update(Long id, Auteur in) {
        Auteur a = findById(id);
        a.setNom(in.getNom());
        a.setPrenom(in.getPrenom());
        a.setNationalite(in.getNationalite());
        a.setEmail(in.getEmail());
        return repo.save(a);
    }

    public void delete(Long id) {
        findById(id);
        if (!livreRepo.findByAuteurId(id).isEmpty())
            throw new ConflictException("Impossible de supprimer : cet auteur a des livres associés");
        repo.deleteById(id);
    }
}
