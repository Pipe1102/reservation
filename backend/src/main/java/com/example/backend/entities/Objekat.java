package com.example.backend.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Entity
@Getter
@Setter
public class Objekat {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false,updatable = false)
    private Long id;

    private String kategorija;

    @OneToMany(cascade = CascadeType.ALL,mappedBy = "objekat")
    @JsonIgnore
    private Set<Rezervacija> rezervacije;

    @OneToMany(cascade = CascadeType.ALL,mappedBy = "objekat")
    @JsonIgnore
    private Set<Komentar> komentari;

    private String naziv;


    private String opis;

    private String adresa;

    private String telefon;

    private boolean isPetFriendly;

    private boolean isSmoking;

    private int brojOsoba;

    private boolean isParking;

    private boolean isWifi;

    private boolean isTv;

    private boolean isAirCondition;

    private String email;

    private int cena;

    private String lokacija;

    private String slikaUrl;

    private int velicina;
}
