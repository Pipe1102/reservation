package com.example.backend.services;

import com.example.backend.entities.Users;
import com.example.backend.exception.domain.EmailExistsException;
import com.example.backend.exception.domain.NotAnImageFileException;
import com.example.backend.exception.domain.UserNameExistsException;
import com.example.backend.exception.domain.UserNotFoundException;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.List;

public interface UserService {

    Users register(String ime, String prezime, String username, String email) throws UserNotFoundException, UserNameExistsException, EmailExistsException, MessagingException;

    List<Users> getUsers();

    Users findUsersByUsername(String username);

    Users findUsersByEmail(String email);

    Users addNewUsers(String ime, String prezime, String username, String email,String poeni, String role, boolean isNotLocked, boolean isActive, MultipartFile imgUrl) throws UserNotFoundException, UserNameExistsException, EmailExistsException, IOException, NotAnImageFileException;

    Users updateUsers(String currentUsername,String newIme, String newPrezime, String newUsername, String newEmail,String poeni, String role, boolean isNotLocked, boolean isActive, MultipartFile imgUrl) throws UserNotFoundException, UserNameExistsException, EmailExistsException, IOException, NotAnImageFileException;

    void deleteUser(String username) throws IOException;

    void resetPassword(String email) throws MessagingException, EmailExistsException;

    Users updateProfileImage(String username,MultipartFile profileImage) throws UserNotFoundException, UserNameExistsException, EmailExistsException, IOException, NotAnImageFileException;



}
