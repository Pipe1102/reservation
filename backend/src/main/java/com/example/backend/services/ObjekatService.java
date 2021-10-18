package com.example.backend.services;

import com.example.backend.entities.Komentar;
import com.example.backend.entities.Objekat;
import com.example.backend.entities.Rezervacija;
import com.example.backend.exception.domain.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

public interface ObjekatService {

    List<Objekat> getAll();

    List<Rezervacija> getRezervacijaForUser(String username);
    List<Komentar> getAllKoment(String naziv);

    List<LocalDateTime> getReservedDays(String naziv);

    Objekat addObjekat(String naziv, String opis ,String adresa,String telefon,boolean isPetFriendly,boolean isSmoking,int brojOsoba,boolean isParking,boolean isWifi,boolean isTv,boolean isAirConditiong, String email, int cena, String lokacija, MultipartFile slikaUrl, int velicina, String kategorija) throws ObjekatExistsException, ObjekatNotFoundException, IOException, NotAnImageFileException;

    Objekat updateObjekat(String curNaziv, String naziv, String opis,String adresa,String telefon, boolean isPetFriendly,boolean isSmoking,int brojOsoba,boolean isParking,boolean isWifi,boolean isTv,boolean isAirConditiong,String email, int parseInt1, String lokacija, MultipartFile slikaUrl, int parseInt2, String kategorija) throws ObjekatExistsException, ObjekatNotFoundException, IOException, NotAnImageFileException;

    void deleteObjekat(String naziv) throws IOException;

    void deleteKomentar(Long id) throws IOException;

    Objekat updateProfileImage(String naziv, MultipartFile profileImage) throws ObjekatNotFoundException, ObjekatExistsException, IOException, NotAnImageFileException;

    Objekat findByNaziv(String naziv);

    Rezervacija addRezervacija(String userNaziv,String objekatNaziv,String datumOd,String datumDo,String ukupnaCena,String ukupnoDana,String brojGositju) throws DatesExistsException;

    Komentar addKomentar(String userNaziv,String objekatNaziv,String ocena,String opis);

}
