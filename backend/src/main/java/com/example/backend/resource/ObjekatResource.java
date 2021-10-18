package com.example.backend.resource;

import com.example.backend.entities.Komentar;
import com.example.backend.entities.Objekat;
import com.example.backend.entities.Rezervacija;
import com.example.backend.exception.domain.*;
import com.example.backend.response.HttpResponse;
import com.example.backend.services.ObjekatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

import static com.example.backend.constant.FileConstant.*;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;

@RestController
@RequestMapping(path ={"/","/objekat"})
public class ObjekatResource extends ExceptionHandling {
    public static final String OBJEKAT_USPESNO_OBRISAN= "Uspesno ste obrisali objekat";
    public static final String KOMENTAR_USPESNO_OBRISAN="Komentar uspesno obrisan";
    private ObjekatService objekatService;

    @Autowired
    public ObjekatResource(ObjekatService objekatService){
        this.objekatService=objekatService;
    }

    @GetMapping("/objekti/list")
    public ResponseEntity<List<Objekat>> getAll(){
        List<Objekat> objekti=objekatService.getAll();
        return new ResponseEntity<>(objekti, HttpStatus.OK);
    }
    @GetMapping("/listrezervacija/{username}")
    public ResponseEntity<List<Rezervacija>> getRezervacije(@PathVariable("username")String username){
       List<Rezervacija> rezervacije=this.objekatService.getRezervacijaForUser(username);
       return new  ResponseEntity<>(rezervacije,HttpStatus.OK);
    }
    @GetMapping("/listrez/{objekatNaziv}")
    public ResponseEntity<List<LocalDateTime>> getAllRezervacijeForObjekata(@PathVariable("objekatNaziv")String objekatNaziv){

        List<LocalDateTime> datumi=this.objekatService.getReservedDays(objekatNaziv);
        return new  ResponseEntity<>(datumi,HttpStatus.OK);
    }
    @GetMapping("/listkomentar/{naziv}")
    public ResponseEntity<List<Komentar>> getKomentari(@PathVariable("naziv")String naziv){
        List<Komentar> komentari=objekatService.getAllKoment(naziv);
        return new  ResponseEntity<>(komentari,HttpStatus.OK);
    }
    @PostMapping("/rezervacija")
    public ResponseEntity<Rezervacija> addRezervacija(@RequestParam("userNaziv")String userNaziv,
                                                      @RequestParam("objekatNaziv")String objekatNaziv,
                                                      @RequestParam("datumOd")String datumOd,
                                                      @RequestParam("datumDo")String datumDo,
                                                      @RequestParam("ukupnaCena")String ukupnaCena,
                                                      @RequestParam("ukupnoDana")String ukupnoDana,
                                                      @RequestParam("brojGostiju")String brojGostiju) throws DatesExistsException {
    Rezervacija newRezervacija=objekatService.addRezervacija(userNaziv,objekatNaziv,datumOd,datumDo,ukupnaCena,ukupnoDana,brojGostiju);
    return new ResponseEntity<>(newRezervacija,HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<Objekat> addNew(@RequestParam("naziv") String naziv,
                                          @RequestParam("opis") String opis,
                                          @RequestParam("adresa") String adresa,
                                          @RequestParam("telefon") String telefon,
                                          @RequestParam("isPetFriendly")String isPetFriendly,
                                          @RequestParam("isSmoking")String isSmoking,
                                          @RequestParam("brojOsoba")String brojOsoba,
                                          @RequestParam("isParking")String isParking,
                                          @RequestParam("isWifi")String isWifi,
                                          @RequestParam("isTv")String isTv,
                                          @RequestParam("isAirCondition")String isAirCondition,
                                          @RequestParam("email") String email,
                                          @RequestParam("cena") String cena,
                                          @RequestParam("lokacija") String lokacija,
                                          @RequestParam("velicina") String velicina,
                                          @RequestParam("kategorija") String objekat_Category,
                                          @RequestParam(value = "slikaUrl",required = false) MultipartFile slikaUrl) throws ObjekatExistsException, ObjekatNotFoundException, IOException, NotAnImageFileException {

        Objekat newObjekat= objekatService.addObjekat(naziv,opis,adresa,telefon,Boolean.parseBoolean(isPetFriendly),Boolean.parseBoolean(isSmoking),Integer.parseInt(brojOsoba),Boolean.parseBoolean(isParking),
                Boolean.parseBoolean(isWifi),Boolean.parseBoolean(isTv),Boolean.parseBoolean(isAirCondition),
                email,Integer.parseInt(cena),lokacija,slikaUrl,Integer.parseInt(velicina),objekat_Category);
        return new ResponseEntity<>(newObjekat,HttpStatus.OK);
    }
    @PostMapping("/update")
    public ResponseEntity<Objekat> updateObjekat(@RequestParam("curNaziv")String curNaziv,
                                                 @RequestParam("naziv")String naziv,
                                                 @RequestParam("opis")String opis,
                                                 @RequestParam("adresa") String adresa,
                                                 @RequestParam("telefon") String telefon,
                                                 @RequestParam("isPetFriendly")String isPetFriendly,
                                                 @RequestParam("isSmoking")String isSmoking,
                                                 @RequestParam("brojOsoba")String brojOsoba,
                                                 @RequestParam("isParking")String isParking,
                                                 @RequestParam("isWifi")String isWifi,
                                                 @RequestParam("isTv")String isTv,
                                                 @RequestParam("isAirCondition")String isAirCondition,
                                                 @RequestParam("email")String email,
                                                 @RequestParam("cena")String cena,
                                                 @RequestParam("lokacija")String lokacija,
                                                 @RequestParam("velicina")String velicina,
                                                 @RequestParam("kategorija")String objekat_Category,
                                                 @RequestParam(value = "slikaUrl",required = false) MultipartFile slikaUrl) throws ObjekatExistsException, ObjekatNotFoundException, IOException, NotAnImageFileException {

        Objekat updateObjekat= objekatService.updateObjekat(curNaziv,naziv,opis,adresa,telefon,Boolean.parseBoolean(isPetFriendly),Boolean.parseBoolean(isSmoking),Integer.parseInt(brojOsoba),Boolean.parseBoolean(isParking),
                Boolean.parseBoolean(isWifi),Boolean.parseBoolean(isTv),Boolean.parseBoolean(isAirCondition),
                email,Integer.parseInt(cena),lokacija,slikaUrl,Integer.parseInt(velicina),objekat_Category);
        return new ResponseEntity<>(updateObjekat,HttpStatus.OK);
    }
    @PostMapping("/komentar/add")
    public ResponseEntity<Komentar> addKomentar(@RequestParam("userNaziv")String userNaziv,
                                                @RequestParam("objekatNaziv")String objekatNaziv,
                                                @RequestParam("opis")String opis,
                                                @RequestParam("ocena") String ocena){
        Komentar komentar=objekatService.addKomentar(userNaziv,objekatNaziv,ocena,opis);
        return new ResponseEntity<>(komentar,HttpStatus.OK);

    }

    @DeleteMapping("/delete/{naziv}")
    public ResponseEntity<HttpResponse> deleteObjekat(@PathVariable("naziv")String naziv) throws IOException {
    objekatService.deleteObjekat(naziv);
    return response(HttpStatus.OK,OBJEKAT_USPESNO_OBRISAN);
    }

    @DeleteMapping("/komentar/delete/{id}")
    public ResponseEntity<HttpResponse> deleteKomentar(@PathVariable("id")Long id) throws IOException {
        objekatService.deleteKomentar(id);
        return response(HttpStatus.OK,KOMENTAR_USPESNO_OBRISAN);
    }

    @PostMapping("/updateProfileImage")
    public ResponseEntity<Objekat> updateProfileImage(@RequestParam("naziv") String username, @RequestParam(value = "slikaUrl") MultipartFile slikaUrl) throws ObjekatNotFoundException, ObjekatExistsException, IOException, NotAnImageFileException {

        Objekat objekat= objekatService.updateProfileImage(username,slikaUrl);
        return new ResponseEntity<>(objekat,HttpStatus.OK);
    }
    @GetMapping(path = "/image/{naziv}/{filename}",produces = IMAGE_JPEG_VALUE)
    public byte[] getProfileImage(@PathVariable("naziv")String naziv,@PathVariable("filename")String filename) throws IOException {
        return Files.readAllBytes(Paths.get(OBJEKAT_FOLDER + naziv + FORWARD_SLASH + filename));
    }

    @GetMapping(path = "/image/profile/{naziv}",produces = IMAGE_JPEG_VALUE)
    public byte[] getTempProfileImage(@PathVariable("naziv")String naziv) throws IOException {
        URL url= new URL(TEMP_PROFILE_IMAGE_BASE_URL + naziv);
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

    @GetMapping(path="/findByNaziv/{naziv}")
    public ResponseEntity<Objekat> findByNaziv(@PathVariable("naziv")String naziv){
        Objekat objekat =objekatService.findByNaziv(naziv);
        return new ResponseEntity<>(objekat,HttpStatus.OK);
    }



    private ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String message) {
        return new ResponseEntity<>(new HttpResponse(httpStatus.value(),httpStatus,httpStatus.getReasonPhrase().toUpperCase()
                ,message.toUpperCase()),httpStatus);
    }
}
