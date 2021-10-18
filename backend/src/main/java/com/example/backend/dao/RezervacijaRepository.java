package com.example.backend.dao;

import com.example.backend.entities.Objekat;
import com.example.backend.entities.Rezervacija;
import com.example.backend.entities.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface RezervacijaRepository extends JpaRepository<Rezervacija,Long> {

    List<Rezervacija> findRezervacijaByUser(Users user);

    Rezervacija findRezervacijaByDatumOd(LocalDateTime date);
    Rezervacija findRezervacijaByDatumDo(LocalDateTime date);

    List<Rezervacija> findRezervacijaByObjekat(Objekat objekat);

}
