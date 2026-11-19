package com.libsoft.lbm.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

/** Utilisé par l'admin pour créer / modifier un utilisateur. Mot de passe optionnel en modification. */
public record UserRequest(
        @NotBlank @Email String email,
        String password,
        String tel,
        @NotBlank String nom,
        @NotBlank String prenom,
        List<String> roles) {}
