package com.libsoft.lbm.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "livres")
public class Livre {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String titre;

    @NotBlank
    @Column(nullable = false, unique = true)
    private String isbn;

    private String categorie;
    private Integer nbPages;
    private Integer nbExemplaires;

    @Column(length = 2000)
    private String description;

    /** Chemin relatif de l'image de couverture (ex. /uploads/livres/12-abc.jpg) */
    private String imageUrl;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "id_auteur", nullable = false)
    private Auteur auteur;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public String getCategorie() { return categorie; }
    public void setCategorie(String categorie) { this.categorie = categorie; }
    public Integer getNbPages() { return nbPages; }
    public void setNbPages(Integer nbPages) { this.nbPages = nbPages; }
    public Integer getNbExemplaires() { return nbExemplaires; }
    public void setNbExemplaires(Integer nbExemplaires) { this.nbExemplaires = nbExemplaires; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public Auteur getAuteur() { return auteur; }
    public void setAuteur(Auteur auteur) { this.auteur = auteur; }
}
