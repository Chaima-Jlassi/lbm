package com.libsoft.lbm.security;

import com.libsoft.lbm.entity.User;
import com.libsoft.lbm.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository repo;

    public CustomUserDetailsService(UserRepository repo) { this.repo = repo; }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User u = repo.findByEmailIgnoreCase(email == null ? "" : email.trim()).orElseThrow(() -> new UsernameNotFoundException(email));
        return new org.springframework.security.core.userdetails.User(
                u.getEmail(), u.getPassword(),
                u.getRoles().stream().map(r -> new SimpleGrantedAuthority("ROLE_" + r.getLibelle())).toList());
    }
}
