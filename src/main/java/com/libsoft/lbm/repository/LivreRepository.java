package com.libsoft.lbm.repository;

import com.libsoft.lbm.entity.Livre;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LivreRepository extends JpaRepository<Livre, Long> {

    boolean existsByIsbn(String isbn);
    boolean existsByIsbnAndIdNot(String isbn, Long id);
    List<Livre> findByAuteurId(Long auteurId);
}
