package com.example.backend.services.impl;
import com.example.backend.dao.KomentarRepository;
import com.example.backend.dao.ObjekatRepository;
import com.example.backend.dao.RezervacijaRepository;
import com.example.backend.dao.UserRepository;
import com.example.backend.entities.Komentar;
import com.example.backend.entities.Objekat;
import com.example.backend.entities.Rezervacija;
import com.example.backend.entities.Users;
import com.example.backend.exception.domain.*;
import com.example.backend.services.ObjekatService;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static com.example.backend.constant.FileConstant.*;
import static com.example.backend.constant.FileConstant.JPG_EXTENSION;
import static com.example.backend.constant.ObjekatImplConstant.*;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.springframework.http.MediaType.*;

@Service
@Transactional
@Qualifier("objekatService")
public class ObjekatServiceImpl implements ObjekatService {

    private ObjekatRepository objekatRepository;
    private UserRepository userRepository;
    private RezervacijaRepository rezervacijaRepository;
    private KomentarRepository komentarRepository;

    @Autowired
   public ObjekatServiceImpl(ObjekatRepository objekatRepository,UserRepository userRepository,RezervacijaRepository rezervacijaRepository,KomentarRepository komentarRepository){
       this.objekatRepository=objekatRepository;
       this.userRepository=userRepository;
       this.rezervacijaRepository=rezervacijaRepository;
       this.komentarRepository=komentarRepository;
   }


    @Override
    public List<Objekat> getAll() {
        return this.objekatRepository.findAll();
    }

    @Override
    public List<Rezervacija> getRezervacijaForUser(String username) {
        Users user=userRepository.findUsersByUsername(username);
        return rezervacijaRepository.findRezervacijaByUser(user);
    }


    @Override
    public List<Komentar> getAllKoment(String naziv) {
        Objekat objekat=findByNaziv(naziv);
        return komentarRepository.findKomentarByObjekat(objekat);
    }

    @Override
    public List<LocalDateTime> getReservedDays(String naziv) {
        Objekat objekat=objekatRepository.findObjekatByNaziv(naziv) ;
        List<Rezervacija> rezervacije=rezervacijaRepository.findRezervacijaByObjekat(objekat);
        List<LocalDate> days=new ArrayList<>();
        List<LocalDateTime> newList=new ArrayList<>();
        for (Rezervacija rez: rezervacije) {
            LocalDate startDate=rez.getDatumOd().toLocalDate();
            LocalDate endDate=rez.getDatumDo().toLocalDate();
            days.addAll(startDate.datesUntil(endDate).collect(Collectors.toList()));
        }
        for (LocalDate l:days) {
            LocalDateTime localDateTime=l.atStartOfDay();
            newList.add(localDateTime);
        }
        return newList;
    }


    @Override
    public Objekat addObjekat(String naziv, String opis,String adresa,String telefon, boolean isPetFriendly, boolean isSmoking, int brojOsoba, boolean isParking, boolean isWifi, boolean isTv,
                              boolean isAirConditiong, String email, int cena, String lokacija, MultipartFile slikaUrl, int velicina, String kategorija)
            throws ObjekatExistsException, ObjekatNotFoundException, IOException, NotAnImageFileException {

        validateNaziv(EMPTY,naziv);
        Objekat objekat=new Objekat();
        objekat.setNaziv(naziv);
        objekat.setOpis(opis);
        objekat.setAdresa(adresa);
        objekat.setTelefon(telefon);
        objekat.setPetFriendly(isPetFriendly);
        objekat.setSmoking(isSmoking);
        objekat.setBrojOsoba(brojOsoba);
        objekat.setParking(isParking);
        objekat.setWifi(isWifi);
        objekat.setTv(isTv);
        objekat.setAirCondition(isAirConditiong);
        objekat.setEmail(email);
        objekat.setCena(cena);
        objekat.setLokacija(lokacija);
        objekat.setSlikaUrl(getTempObjekatImg(naziv));
        objekat.setVelicina(velicina);
        objekat.setKategorija(kategorija);
        objekatRepository.save(objekat);
        saveProfileImg(objekat,slikaUrl);
        return objekat;
    }

    @Override
    public Objekat updateObjekat(String curNaziv, String naziv, String opis,String adresa,String telefon, boolean isPetFriendly, boolean isSmoking, int brojOsoba, boolean isParking, boolean isWifi, boolean isTv, boolean isAirConditiong, String email, int cena, String lokacija, MultipartFile slikaUrl, int velicina, String kategorija) throws ObjekatExistsException, ObjekatNotFoundException, IOException, NotAnImageFileException {
        validateNaziv(curNaziv,naziv);
        Objekat objekat=objekatRepository.findObjekatByNaziv(curNaziv);
        objekat.setNaziv(naziv);
        objekat.setOpis(opis);
        objekat.setAdresa(adresa);
        objekat.setTelefon(telefon);
        objekat.setPetFriendly(isPetFriendly);
        objekat.setSmoking(isSmoking);
        objekat.setBrojOsoba(brojOsoba);
        objekat.setParking(isParking);
        objekat.setWifi(isWifi);
        objekat.setTv(isTv);
        objekat.setAirCondition(isAirConditiong);
        objekat.setEmail(email);
        objekat.setCena(cena);
        objekat.setLokacija(lokacija);
        objekat.setVelicina(velicina);
        objekat.setKategorija(kategorija);
        objekatRepository.save(objekat);
        saveProfileImg(objekat,slikaUrl);
        return objekat;
    }





    @Override
    public void deleteObjekat(String naziv) throws IOException {
    Objekat objekat= objekatRepository.findObjekatByNaziv(naziv);
        Path objekatFolder=Paths.get(OBJEKAT_FOLDER + objekat.getNaziv()).toAbsolutePath().normalize();
        FileUtils.deleteDirectory(new File(objekatFolder.toString()));
    objekatRepository.deleteById(objekat.getId());
    }

    @Override
    public void deleteKomentar(Long id) throws IOException {
        Komentar komentar=komentarRepository.findKomentarById(id);
        komentarRepository.delete(komentar);
    }

    @Override
    public Objekat updateProfileImage(String naziv, MultipartFile profileImage) throws ObjekatNotFoundException, ObjekatExistsException, IOException, NotAnImageFileException {
        Objekat objekat=validateNaziv(naziv,null);
        saveProfileImg(objekat,profileImage);
        return objekat;
    }

    @Override
    public Objekat findByNaziv(String naziv) {
      Objekat objekat=  objekatRepository.findObjekatByNaziv(naziv);
      return  objekat;
    }

    @Override
    public Rezervacija addRezervacija(String userNaziv, String objekatNaziv, String datumOd, String datumDo, String ukupnaCena, String ukupnoDana,String brojGostiju) throws DatesExistsException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX", Locale.ENGLISH);
        LocalDateTime newDateOd=LocalDateTime.parse(datumOd,formatter);
        LocalDateTime newDateDo=LocalDateTime.parse(datumDo,formatter);
        Rezervacija rezervacija=new Rezervacija();
        Objekat objekat=objekatRepository.findObjekatByNaziv(objekatNaziv);
        validateDates(newDateOd,newDateDo,objekat);
        Users user=userRepository.findUsersByUsername(userNaziv);
        rezervacija.setObjekat(objekat);
        rezervacija.setUser(user);
        rezervacija.setObjekatNaziv(objekatNaziv);
        rezervacija.setBrojGostiju(Integer.parseInt(brojGostiju));
        rezervacija.setDatumOd(newDateOd);
        rezervacija.setDatumDo(newDateDo);
        rezervacija.setUkupnaCena(Integer.parseInt(ukupnaCena));
        rezervacija.setUkupnoDana(Integer.parseInt(ukupnoDana));
        rezervacijaRepository.save(rezervacija);
        return rezervacija;
    }

    @Override
    public Komentar addKomentar(String userNaziv,String objekatNaziv,String ocena, String opis) {
        Komentar komentar=new Komentar();
        Users user=userRepository.findUsersByUsername(userNaziv);
        Objekat objekat=objekatRepository.findObjekatByNaziv(objekatNaziv);
        komentar.setUserNaziv(user.getUsername());
        komentar.setUserSlika(user.getImgUrl());
        komentar.setObjekat(objekat);
        komentar.setOpis(opis);
        komentar.setOcena(Integer.parseInt(ocena));
        komentarRepository.save(komentar);
        return komentar;
    }


    private String getTempObjekatImg(String naziv) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path(DEFAULT_OBJEKAT_IMAGE_PATH + naziv).toUriString();
    }


    private String setObjekatImageUrl(String naziv) {
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(OBJEKAT_IMAGE_PATH + naziv + FORWARD_SLASH + naziv + DOT + JPG_EXTENSION).toUriString();
    }

    private void saveProfileImg(Objekat objekat, MultipartFile slikaUrl) throws IOException, NotAnImageFileException {

        if (slikaUrl != null) {
            if(!Arrays.asList(IMAGE_JPEG_VALUE, IMAGE_PNG_VALUE, IMAGE_GIF_VALUE).contains(slikaUrl.getContentType())) {
                throw new NotAnImageFileException(slikaUrl.getOriginalFilename() + NOT_AN_IMAGE_FILE);
            }
            Path objekatFolder = Paths.get(OBJEKAT_FOLDER + objekat.getNaziv()).toAbsolutePath().normalize();
            if(!Files.exists(objekatFolder)) {
                Files.createDirectories(objekatFolder);
            }
            Files.deleteIfExists(Paths.get(objekatFolder + objekat.getNaziv() + DOT + JPG_EXTENSION));
            Files.copy(slikaUrl.getInputStream(), objekatFolder.resolve(objekat.getNaziv() + DOT + JPG_EXTENSION), REPLACE_EXISTING);
            objekat.setSlikaUrl(setObjekatImageUrl(objekat.getNaziv()));
            objekatRepository.save(objekat);
        }
    }
    private Objekat validateNaziv(String curNaziv,String newNaziv) throws ObjekatNotFoundException, ObjekatExistsException {
        Objekat newObjekat=objekatRepository.findObjekatByNaziv(newNaziv);
        if(StringUtils.isNotBlank(curNaziv)){
            Objekat currentObjekat=objekatRepository.findObjekatByNaziv(curNaziv);
            if(currentObjekat==null){
                throw new ObjekatNotFoundException(NO_OBJEKAT_FOUND_BY_NAZIV + curNaziv);
            }
            if(newObjekat!=null && !currentObjekat.getId().equals(newObjekat.getId())){
                throw new ObjekatExistsException(OBJEKAT_NAZIV_EXISTS);
            }
            return currentObjekat;
        }
        else{
            if(newObjekat!=null){
                throw new ObjekatExistsException(OBJEKAT_NAZIV_EXISTS);
            }
            return null;
        }

    }

    private boolean validateDates(LocalDateTime date1,LocalDateTime date2,Objekat objekat) throws DatesExistsException {
        List<Rezervacija> lista=this.rezervacijaRepository.findRezervacijaByObjekat(objekat);
        for (Rezervacija rez:lista) {
            if(rez.getDatumOd().toLocalDate().equals(date1) || rez.getDatumDo().equals(date2)){
                throw new DatesExistsException(DATUM_EXISTS);
            }
        }
        int brDana=date2.getDayOfYear() - date1.getDayOfYear();
        if(brDana<=-1) {
            throw new DatesExistsException(DATUM_INVALID);
        }
        return  true;
        }


    }


