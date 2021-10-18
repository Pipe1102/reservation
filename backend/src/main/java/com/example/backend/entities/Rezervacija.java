package com.example.backend.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;


@Entity
@Getter
@Setter
public class Rezervacija {
    @Id
    @Column(updatable = false,nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private LocalDateTime datumOd;

    private LocalDateTime datumDo;

    private int ukupnoDana;

    private int ukupnaCena;

    private String objekatNaziv;

    private int brojGostiju;

    @ManyToOne
    @JoinColumn(name = "objekatId",nullable = false)
    @JsonIgnore
    private Objekat objekat;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private Users user;
}
