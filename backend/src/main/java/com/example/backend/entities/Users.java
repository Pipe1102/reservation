package com.example.backend.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Entity
@Getter
@Setter
public class Users implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false,updatable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Long id;

    private String userId;

    private  String ime;

    private String prezime;

    private String username;

    private String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    private String imgUrl;

    private int points;

    private String role;

    private String[] authorities;

    private boolean isActive;

    private boolean isNotLocked;

    @OneToMany(cascade = CascadeType.ALL,mappedBy = "user")
    private Set<Rezervacija> rezervacije;




}
