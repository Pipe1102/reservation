package com.example.backend.dao;

import com.example.backend.entities.Rezervacija;
import com.example.backend.entities.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<Users, Long> {
    Users findUsersByUsername(String username);

    Users findUsersByEmail(String email);


}
