package com.libsoft.lbm.exception;

import com.libsoft.lbm.dto.ApiError;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> notFound(ResourceNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiError(404, e.getMessage()));
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiError> conflict(ConflictException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiError(409, e.getMessage()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> integrity(DataIntegrityViolationException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiError(409, "Violation de contrainte (doublon ou ressource référencée)"));
    }

    /** Toute erreur d'authentification (mauvais identifiants, compte désactivé…) => 401 JSON, jamais 500. */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiError> authentication(AuthenticationException e) {
        String msg = (e instanceof BadCredentialsException || !(e instanceof DisabledException))
                ? "Email ou mot de passe incorrect" : "Compte désactivé";
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiError(401, msg));
    }

    @ExceptionHandler({IllegalArgumentException.class, HttpMessageNotReadableException.class})
    public ResponseEntity<ApiError> badRequest(Exception e) {
        String msg = e instanceof IllegalArgumentException ? e.getMessage() : "Corps de requête invalide";
        return ResponseEntity.badRequest().body(new ApiError(400, msg));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> validation(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .map(f -> f.getField() + " : " + f.getDefaultMessage())
                .collect(Collectors.joining(" ; "));
        return ResponseEntity.badRequest().body(new ApiError(400, msg));
    }
}
