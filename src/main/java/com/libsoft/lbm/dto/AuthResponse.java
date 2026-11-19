package com.libsoft.lbm.dto;

import java.util.List;

public record AuthResponse(String token, Long id, String email, String nom, String prenom, List<String> roles) {}
