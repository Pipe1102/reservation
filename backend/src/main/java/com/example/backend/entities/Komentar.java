package com.example.backend.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class Komentar {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false,updatable = false)
    private Long id;

    private String opis;

    private int ocena;

    private String userNaziv;

    private String userSlika;

    @ManyToOne
    @JoinColumn(name = "objekat_id")
    private Objekat objekat;

}
