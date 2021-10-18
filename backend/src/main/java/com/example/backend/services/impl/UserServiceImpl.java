package com.example.backend.services.impl;

import com.example.backend.dao.UserRepository;
import com.example.backend.entities.Users;
import com.example.backend.entities.UserPrincipal;
import com.example.backend.enumeration.Role;
import com.example.backend.exception.domain.EmailExistsException;
import com.example.backend.exception.domain.NotAnImageFileException;
import com.example.backend.exception.domain.UserNameExistsException;
import com.example.backend.exception.domain.UserNotFoundException;
import com.example.backend.services.EmailService;
import com.example.backend.services.LoginAttemptService;
import com.example.backend.services.UserService;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import javax.mail.MessagingException;
import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;


import static com.example.backend.constant.UserImplConstant.*;
import static com.example.backend.constant.FileConstant.*;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.springframework.http.MediaType.*;

@Service
@Transactional
@Qualifier("userDetailsService")
public class UserServiceImpl implements UserService, UserDetailsService {
    private Logger LOGGER= LoggerFactory.getLogger(getClass());
    private UserRepository userRepository;
    private BCryptPasswordEncoder passwordEncoder;
    private LoginAttemptService loginAttemptService;
    private EmailService emailService;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder,
                           LoginAttemptService loginAttemptService,EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder=passwordEncoder;
        this.loginAttemptService=loginAttemptService;
        this.emailService=emailService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users user=userRepository.findUsersByUsername(username);
        if(user==null){
            LOGGER.error("User not found by username:" +username);
            throw new UsernameNotFoundException(NO_USERS_FOUND_BY_USERNAME +username);
        }
        else{
            validateLoginAttempt(user);
            userRepository.save(user);
            UserPrincipal userPrincipal=new UserPrincipal(user);
            LOGGER.info(FOUND_USER_BY_USERNAME +username);
            return userPrincipal;
        }

    }


    @Override
    public Users register(String ime, String prezime, String username, String email) throws UserNotFoundException, UserNameExistsException, EmailExistsException, MessagingException {
        validateNewUserNameAndEmail(EMPTY,username,email);
        Users user=new Users();
        user.setUserId(generateUserId());
        String password=generatePassword();
        String encodedPassword=encodePassword(password);
        user.setIme(ime);
        user.setPrezime(prezime);
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(encodedPassword);
        user.setActive(true);
        user.setNotLocked(true);
        user.setRole(Role.ROLE_USER.name());
        user.setAuthorities(Role.ROLE_USER.getAuthorities());
        user.setImgUrl(getTempProfileImg(username));
        userRepository.save(user);
        LOGGER.info("New user password: "+password);
        emailService.sendNewPasswordEmail(ime,password,email);
        return user;
    }

    @Override
    public Users addNewUsers(String ime, String prezime, String username, String email,String poeni, String role, boolean isNotLocked, boolean isActive, MultipartFile imgUrl) throws UserNotFoundException, UserNameExistsException, EmailExistsException, IOException, NotAnImageFileException {
        validateNewUserNameAndEmail(EMPTY,username,email);
        Users addUser= new Users();
        String password= generatePassword();
        addUser.setUserId(generateUserId());
        addUser.setIme(ime);
        addUser.setPrezime(prezime);
        addUser.setUsername(username);
        addUser.setEmail(email);
        addUser.setPoints(Integer.parseInt(poeni));
        addUser.setPassword(encodePassword(password));
        addUser.setActive(isActive);
        addUser.setNotLocked(isNotLocked);
        addUser.setRole(getRoleEnumName(role).name());
        addUser.setAuthorities(getRoleEnumName(role).getAuthorities());
        addUser.setImgUrl(getTempProfileImg(username));
        userRepository.save(addUser);
        saveProfileImg(addUser, imgUrl);
        LOGGER.info("Nova sifra: "+password);
        return addUser;
    }



    @Override
    public Users updateUsers(String currentUsername, String newIme, String newPrezime, String newUsername, String newEmail,String newPoeni, String role, boolean isNotLocked, boolean isActive, MultipartFile imgUrl) throws UserNotFoundException, UserNameExistsException, EmailExistsException, IOException, NotAnImageFileException {
        Users currentUser= validateNewUserNameAndEmail(currentUsername,newUsername,newEmail);
        currentUser.setIme(newIme);
        currentUser.setPrezime(newPrezime);
        currentUser.setUsername(newUsername);
        currentUser.setEmail(newEmail);
        currentUser.setPoints(Integer.parseInt(newPoeni));
        currentUser.setActive(isActive);
        currentUser.setNotLocked(isNotLocked);
        currentUser.setRole(getRoleEnumName(role).name());
        currentUser.setAuthorities(getRoleEnumName(role).getAuthorities());
        userRepository.save(currentUser);
        saveProfileImg(currentUser, imgUrl);
        return currentUser;
    }

    @Override
    public void deleteUser(String username) throws IOException {
        Users user=userRepository.findUsersByUsername(username);
        Path userFolder=Paths.get(USER_FOLDER + user.getUsername()).toAbsolutePath().normalize();
        FileUtils.deleteDirectory(new File(userFolder.toString()));
        userRepository.deleteById(user.getId());

    }

    @Override
    public void resetPassword(String email) throws MessagingException, EmailExistsException {
    Users user=userRepository.findUsersByEmail(email);
    if(user==null){
        throw new EmailExistsException(NO_USER_FOUND_BY_EMAIL+email);
    }
    String password=generatePassword();
    user.setPassword(encodePassword(password));
    userRepository.save(user);
    emailService.sendNewPasswordEmail(user.getIme(),password,user.getEmail());
    LOGGER.info("New user password: "+password);
    }

    @Override
    public Users updateProfileImage(String username, MultipartFile imgUrl) throws UserNotFoundException, UserNameExistsException, EmailExistsException, IOException, NotAnImageFileException {
        Users user=validateNewUserNameAndEmail(username,null,null);
        saveProfileImg(user,imgUrl);
        return user;
    }

    @Override
    public List<Users> getUsers() {
        return userRepository.findAll();
    }

    @Override
    public Users findUsersByUsername(String username) {
        return userRepository.findUsersByUsername(username);
    }

    @Override
    public Users findUsersByEmail(String email) {
        return userRepository.findUsersByEmail(email);
    }


    private void saveProfileImg(Users user, MultipartFile imgUrl) throws IOException, NotAnImageFileException {

        if (imgUrl != null) {
            if(!Arrays.asList(IMAGE_JPEG_VALUE, IMAGE_PNG_VALUE, IMAGE_GIF_VALUE).contains(imgUrl.getContentType())) {
                throw new NotAnImageFileException(imgUrl.getOriginalFilename() + NOT_AN_IMAGE_FILE);
            }
            Path userFolder = Paths.get(USER_FOLDER + user.getUsername()).toAbsolutePath().normalize();
            if(!Files.exists(userFolder)) {
                Files.createDirectories(userFolder);
                LOGGER.info(DIRECTORY_CREATED + userFolder);
            }
            Files.deleteIfExists(Paths.get(userFolder + user.getUsername() + DOT + JPG_EXTENSION));
            Files.copy(imgUrl.getInputStream(), userFolder.resolve(user.getUsername() + DOT + JPG_EXTENSION), REPLACE_EXISTING);
            user.setImgUrl(setProfileImageUrl(user.getUsername()));
            userRepository.save(user);
            LOGGER.info(FILE_SAVED_IN_FILE_SYSTEM + imgUrl.getOriginalFilename());
        }
    }

    private String setProfileImageUrl(String username) {
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(USER_IMAGE_PATH + username + FORWARD_SLASH + username + DOT + JPG_EXTENSION).toUriString();
    }

    private Role getRoleEnumName(String role) {
    return Role.valueOf(role.toUpperCase());
    }


    private String getTempProfileImg(String username) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path(DEFAULT_USER_IMAGE_PATH + username).toUriString();
    }

    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    private String generatePassword() {
        return RandomStringUtils.randomAlphanumeric(10);
    }

    private String generateUserId() {
        return RandomStringUtils.randomNumeric(10);
    }


    private Users validateNewUserNameAndEmail(String currentUsername,String newUsername,String newEmail) throws UserNotFoundException, UserNameExistsException, EmailExistsException {
        Users userByNewUsername = findUsersByUsername(newUsername);
        Users userByNewEmail = findUsersByEmail(newEmail);
        if(StringUtils.isNotBlank(currentUsername)) {
            Users currentUser = findUsersByUsername(currentUsername);
            if(currentUser == null) {
                throw new UserNotFoundException(NO_USERS_FOUND_BY_USERNAME + currentUsername);
            }
            if(userByNewUsername != null && !currentUser.getId().equals(userByNewUsername.getId())) {
                throw new UserNameExistsException(USERNAME_ALREADY_EXISTS);
            }
            if(userByNewEmail != null && !currentUser.getId().equals(userByNewEmail.getId())) {
                throw new EmailExistsException(EMAIL_ALREADY_EXISTS);
            }
            return currentUser;
        } else {
            if(userByNewUsername != null) {
                throw new UserNameExistsException(USERNAME_ALREADY_EXISTS);
            }
            if(userByNewEmail != null) {
                throw new EmailExistsException(EMAIL_ALREADY_EXISTS);
            }
            return null;
        }
    }







    private void validateLoginAttempt(Users user) {
        if(user.isNotLocked()) {
            if(loginAttemptService.hasExceededMaxAttempts(user.getUsername())) {
                user.setNotLocked(false);
            } else {
                user.setNotLocked(true);
            }
        } else {
            loginAttemptService.evictUserFromLoginAttemptCache(user.getUsername());
        }
    }
}
