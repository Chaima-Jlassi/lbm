package com.libsoft.lbm.service;

import com.libsoft.lbm.dto.AuthResponse;
import com.libsoft.lbm.dto.LoginRequest;
import com.libsoft.lbm.dto.RegisterRequest;
import com.libsoft.lbm.entity.Role;
import com.libsoft.lbm.entity.User;
import com.libsoft.lbm.exception.ConflictException;
import com.libsoft.lbm.repository.RoleRepository;
import com.libsoft.lbm.repository.UserRepository;
import com.libsoft.lbm.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class AuthService {
    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authManager;
    private final JwtService jwt;
    private final MailService mail;

    public AuthService(UserRepository userRepo, RoleRepository roleRepo, PasswordEncoder encoder,
                       AuthenticationManager authManager, JwtService jwt, MailService mail) {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.encoder = encoder;
        this.authManager = authManager;
        this.jwt = jwt;
        this.mail = mail;
    }

    public AuthResponse register(RegisterRequest r) {
        String email = normalize(r.email());
        if (userRepo.existsByEmailIgnoreCase(email)) throw new ConflictException("Email déjà utilisé : " + email);
        User u = new User();
        u.setEmail(email);
        u.setPassword(encoder.encode(r.password()));
        u.setTel(r.tel());
        u.setNom(r.nom());
        u.setPrenom(r.prenom());
        // Sécurité : on ne s'auto-attribue jamais ADMIN à l'inscription
        Role biblio = roleRepo.findByLibelle("BIBLIOTHECAIRE").orElseThrow();
        u.setRoles(new HashSet<>(Set.of(biblio)));
        userRepo.save(u);
        mail.notifyAdminNewRegistration(u);
        return toResponse(u);
    }

    public AuthResponse login(LoginRequest r) {
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(normalize(r.email()), r.password()));
        User u = userRepo.findByEmailIgnoreCase(auth.getName()).orElseThrow();
        return toResponse(u);
    }

    private static String normalize(String email) { return email == null ? "" : email.trim().toLowerCase(); }

    private AuthResponse toResponse(User u) {
        List<String> roles = u.getRoles().stream().map(Role::getLibelle).sorted().toList();
        return new AuthResponse(jwt.generateToken(u.getEmail(), roles), u.getId(), u.getEmail(),
                u.getNom(), u.getPrenom(), roles);
    }
}
