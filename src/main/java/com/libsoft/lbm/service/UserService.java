package com.libsoft.lbm.service;

import com.libsoft.lbm.dto.UserRequest;
import com.libsoft.lbm.entity.Role;
import com.libsoft.lbm.entity.User;
import com.libsoft.lbm.exception.ConflictException;
import com.libsoft.lbm.exception.ResourceNotFoundException;
import com.libsoft.lbm.repository.RoleRepository;
import com.libsoft.lbm.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserService {
    private final UserRepository repo;
    private final RoleRepository roleRepo;
    private final PasswordEncoder encoder;

    public UserService(UserRepository repo, RoleRepository roleRepo, PasswordEncoder encoder) {
        this.repo = repo;
        this.roleRepo = roleRepo;
        this.encoder = encoder;
    }

    public List<User> findAll() { return repo.findAll(); }

    public User findById(Long id) {
        return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable : " + id));
    }

    public List<Role> findAllRoles() { return roleRepo.findAll(); }

    public User create(UserRequest r) {
        if (repo.existsByEmailIgnoreCase(r.email().trim())) throw new ConflictException("Email déjà utilisé : " + r.email());
        if (r.password() == null || r.password().length() < 6)
            throw new IllegalArgumentException("Mot de passe obligatoire (6 caractères minimum)");
        User u = new User();
        apply(u, r);
        u.setPassword(encoder.encode(r.password()));
        return repo.save(u);
    }

    public User update(Long id, UserRequest r) {
        User u = findById(id);
        if (!u.getEmail().equalsIgnoreCase(r.email().trim()) && repo.existsByEmailIgnoreCase(r.email().trim()))
            throw new ConflictException("Email déjà utilisé : " + r.email());
        apply(u, r);
        if (r.password() != null && !r.password().isBlank()) u.setPassword(encoder.encode(r.password()));
        return repo.save(u);
    }

    public void delete(Long id) {
        findById(id);
        repo.deleteById(id);
    }

    private void apply(User u, UserRequest r) {
        u.setEmail(r.email().trim().toLowerCase());
        u.setNom(r.nom());
        u.setPrenom(r.prenom());
        u.setTel(r.tel());
        Set<Role> roles = new HashSet<>();
        if (r.roles() != null) {
            for (String libelle : r.roles()) {
                roles.add(roleRepo.findByLibelle(libelle)
                        .orElseThrow(() -> new ResourceNotFoundException("Rôle introuvable : " + libelle)));
            }
        }
        u.setRoles(roles);
    }
}
