package com.example.backend.dao;

import com.example.backend.entities.Objekat;
import org.springframework.data.jpa.repository.JpaRepository;



public interface ObjekatRepository extends JpaRepository<Objekat,Long> {

   Objekat findObjekatByNaziv(String naziv);

}
