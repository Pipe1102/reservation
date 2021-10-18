package com.example.backend.dao;

import com.example.backend.entities.Komentar;
import com.example.backend.entities.Objekat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface KomentarRepository extends JpaRepository<Komentar, Long> {

    List<Komentar> findKomentarByObjekat(Objekat objekat);

    Komentar findKomentarById(Long id);
}
