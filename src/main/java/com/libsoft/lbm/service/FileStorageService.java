package com.libsoft.lbm.service;

import com.libsoft.lbm.exception.ConflictException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

@Service
public class FileStorageService {

    private static final Set<String> ALLOWED_TYPES = Set.of(
            "image/jpeg", "image/png", "image/webp", "image/jpg"
    );
    private static final long MAX_SIZE = 2 * 1024 * 1024;

    private final Path uploadRoot;

    public FileStorageService(@Value("${app.upload.dir:uploads}") String uploadDir) throws IOException {
        this.uploadRoot = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(uploadRoot.resolve("livres"));
        System.out.println("[FileStorage] Upload root = " + uploadRoot);
    }

    public String storeLivreImage(Long livreId, MultipartFile file) {
        validate(file);
        String original = file.getOriginalFilename() != null ? file.getOriginalFilename() : "cover";
        String ext = extension(original, file.getContentType());
        String filename = livreId + "-" + UUID.randomUUID().toString().substring(0, 8) + ext;
        Path target = uploadRoot.resolve("livres").resolve(filename);
        try {
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new ConflictException("Impossible d'enregistrer l'image : " + e.getMessage());
        }
        return "/uploads/livres/" + filename;
    }

    public Resource loadAsResource(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) return null;
        String relative = imageUrl.replaceFirst("^/uploads/", "");
        Path path = uploadRoot.resolve(relative).normalize();
        if (!path.startsWith(uploadRoot) || !Files.exists(path)) return null;
        try {
            Resource resource = new UrlResource(path.toUri());
            return resource.exists() ? resource : null;
        } catch (MalformedURLException e) {
            return null;
        }
    }

    public void deleteIfExists(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) return;
        String relative = imageUrl.replaceFirst("^/uploads/", "");
        Path path = uploadRoot.resolve(relative).normalize();
        if (!path.startsWith(uploadRoot)) return;
        try { Files.deleteIfExists(path); } catch (IOException ignored) {}
    }

    private void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) throw new ConflictException("Fichier image manquant");
        if (file.getSize() > MAX_SIZE) throw new ConflictException("Image trop volumineuse (max 2 Mo)");
        String type = file.getContentType();
        if (type == null || !ALLOWED_TYPES.contains(type.toLowerCase()))
            throw new ConflictException("Format non autorisé (JPEG, PNG ou WebP uniquement)");
    }

    private String extension(String filename, String contentType) {
        int dot = filename.lastIndexOf('.');
        if (dot > 0) {
            String e = filename.substring(dot).toLowerCase();
            if (e.matches("\\.(jpe?g|png|webp)")) return e;
        }
        if (contentType != null) {
            if (contentType.contains("png")) return ".png";
            if (contentType.contains("webp")) return ".webp";
        }
        return ".jpg";
    }
}
