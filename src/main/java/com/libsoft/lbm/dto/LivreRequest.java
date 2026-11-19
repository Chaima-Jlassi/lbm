package com.libsoft.lbm.dto;

import jakarta.validation.constraints.*;

public record LivreRequest(
        @NotBlank String titre,
        @NotBlank String isbn,
        String categorie,
        @Min(1) Integer nbPages,
        @Min(0) Integer nbExemplaires,
        String description,
        @NotNull Long idAuteur) {}
