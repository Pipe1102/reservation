package com.example.backend.resource;
import com.example.backend.entities.UserPrincipal;
import com.example.backend.entities.Users;
import com.example.backend.exception.domain.*;
import com.example.backend.response.HttpResponse;
import com.example.backend.services.UserService;
import com.example.backend.utility.JWTTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static com.example.backend.constant.FileConstant.*;
import static com.example.backend.constant.SecurityConstant.JWT_TOKEN_HEADER;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;

@RestController
@RequestMapping(path ={ "/", "/user"})

public class UserResource extends ExceptionHandling {
    public static final String EMAIL_SENT = "Email sa novom sifrom je poslat na: ";
    public static final String KLIJENT_USPESNO_OBIRSAN = "Klijent uspesno obirsan";
    private UserService userService;
    private AuthenticationManager authenticationManager;
    private JWTTokenProvider tokenProvider;

    @Autowired
    public UserResource(UserService userService,AuthenticationManager authenticationManager,JWTTokenProvider tokenProvider) {
        this.userService = userService;
        this.authenticationManager=authenticationManager;
        this.tokenProvider=tokenProvider;
    }

    @PostMapping("/login")
    public ResponseEntity<Users> login(@RequestBody Users user) throws UserNotFoundException, UserNameExistsException, EmailExistsException {
        authenticate(user.getUsername(),user.getPassword());
        Users loginUser=userService.findUsersByUsername(user.getUsername());
        UserPrincipal userPrincipal=new UserPrincipal(loginUser);
        HttpHeaders jwtHeader= getJwtHeader(userPrincipal);
        return new ResponseEntity<>(loginUser, jwtHeader,HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<Users> register(@RequestBody Users user) throws UserNotFoundException, UserNameExistsException, EmailExistsException, MessagingException {
        Users newUser = userService.register(user.getIme(),user.getPrezime(),user.getUsername(),user.getEmail());
        return new ResponseEntity<>(newUser, HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<Users> addNewUser(@RequestParam("ime")String ime,
                                            @RequestParam("prezime") String prezime,
                                            @RequestParam("username") String username,
                                            @RequestParam("email") String email,
                                            @RequestParam("poeni") String poeni,
                                            @RequestParam("role") String role,
                                            @RequestParam("isLocked") String isLocked,
                                            @RequestParam("isActive") String isActive,
                                            @RequestParam(value = "imgUrl",required = false)MultipartFile imgUrl)
            throws UserNotFoundException, UserNameExistsException, EmailExistsException, IOException, NotAnImageFileException {


        Users user=userService.addNewUsers(ime,prezime,username,email,poeni,role,Boolean.parseBoolean(isLocked),Boolean.parseBoolean(isActive),imgUrl);
        return new ResponseEntity<>(user,HttpStatus.OK);
    }

    @PostMapping("/update")
    public ResponseEntity<Users> updateUser(@RequestParam("currentUsername")String currentUsername,
                                            @RequestParam("ime")String ime,
                                            @RequestParam("prezime") String prezime,
                                            @RequestParam("username") String username,
                                            @RequestParam("email") String email,
                                            @RequestParam("poeni") String poeni,
                                            @RequestParam("role") String role,
                                            @RequestParam("isLocked") String isLocked,
                                            @RequestParam("isActive") String isActive,
                                            @RequestParam(value = "imgUrl",required = false)MultipartFile imgUrl)
            throws UserNotFoundException, UserNameExistsException, EmailExistsException, IOException, NotAnImageFileException {


        Users updatedUser=userService.updateUsers(currentUsername, ime, prezime, username, email,poeni, role,
                Boolean.parseBoolean(isLocked), Boolean.parseBoolean(isActive), imgUrl);

        return new ResponseEntity<>(updatedUser,HttpStatus.OK);
    }

    @GetMapping("/find/{username}")
    public ResponseEntity<Users> getUser(@PathVariable("username") String username){
        Users user= userService.findUsersByUsername(username);
        return new ResponseEntity<>(user,HttpStatus.OK);
    }

    @GetMapping("/user/list")
    public ResponseEntity<List<Users>> getALlUsers() {
        List<Users> users=userService.getUsers();
        return new ResponseEntity<>(users,HttpStatus.OK);
    }

    @GetMapping("/resetpassword/{email}")
    public ResponseEntity<HttpResponse> resetPassword(@PathVariable("email") String email) throws MessagingException, EmailExistsException {
        userService.resetPassword(email);
        return response(HttpStatus.OK, EMAIL_SENT +email);
    }

    @DeleteMapping("/delete/{username}")
    @PreAuthorize("hasAnyAuthority('user:delete')")
    public ResponseEntity<HttpResponse> delete(@PathVariable("username") String username) throws IOException {
        userService.deleteUser(username);
        return response(HttpStatus.OK, KLIJENT_USPESNO_OBIRSAN);
    }

    @PostMapping("/updateProfileImage")
    public ResponseEntity<Users> updateProfileImage(   @RequestParam("username") String username, @RequestParam(value = "imgUrl") MultipartFile imgUrl) throws UserNotFoundException, UserNameExistsException, EmailExistsException, IOException, NotAnImageFileException {

        Users user= userService.updateProfileImage(username,imgUrl);
        return new ResponseEntity<>(user,HttpStatus.OK);
    }

    @GetMapping(path = "/image/{username}/{filename}",produces = IMAGE_JPEG_VALUE)
    public byte[] getProfileImage(@PathVariable("username")String username,@PathVariable("filename")String filename) throws IOException {
        return Files.readAllBytes(Paths.get(USER_FOLDER + username + FORWARD_SLASH + filename));
    }

    @GetMapping(path = "/image/profile/{username}",produces = IMAGE_JPEG_VALUE)
    public byte[] getTempProfileImage(@PathVariable("username")String username) throws IOException {
        URL url= new URL(TEMP_PROFILE_IMAGE_BASE_URL + username);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try(InputStream inputStream = url.openStream()){
            int bytesRead;
            byte[] chunk=new byte[1024];
            while((bytesRead = inputStream.read(chunk))>0){
                byteArrayOutputStream.write(chunk,0,bytesRead);
            }
        }
        return byteArrayOutputStream.toByteArray();
    }

    private ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String message) {
        return new ResponseEntity<>(new HttpResponse(httpStatus.value(),httpStatus,httpStatus.getReasonPhrase().toUpperCase()
                                     ,message.toUpperCase()),httpStatus);
    }


    private HttpHeaders getJwtHeader(UserPrincipal userPrincipal) {
        HttpHeaders headers=new HttpHeaders();
        headers.add(JWT_TOKEN_HEADER,tokenProvider.generateJwtToken(userPrincipal));
        return headers;
    }

    private void authenticate(String username, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username,password));
    }
}
