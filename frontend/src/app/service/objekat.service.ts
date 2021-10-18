import { HttpClient, HttpEvent } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { NgbDate, NgbDateStruct } from '@ng-bootstrap/ng-bootstrap';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { CustomHttpResponse } from '../common/custom-http-response';
import { Komentar } from '../common/komentar';
import { Objekat } from '../common/objekat';
import { Rezervacija } from '../common/rezervacija';

@Injectable({
  providedIn: 'root'
})
export class ObjekatService {

  private host= environment.apiUrl;
  constructor(private httpClient:HttpClient) { }

  public getObjekti():Observable<Objekat[]>{
    return this.httpClient.get<Objekat[]>(`${this.host}/objekti/list`);
  }

  public addObjekat(formData:FormData):Observable<Objekat>{
    return this.httpClient.post<Objekat>(`${this.host}/objekat/add`,formData);
  }
  public updateObjekat(formData:FormData):Observable<Objekat>{
    return this.httpClient.post<Objekat>(`${this.host}/objekat/update`,formData);
  }
  public deleteObjekat(naziv:string):Observable<CustomHttpResponse>{
    return this.httpClient.delete<CustomHttpResponse>(`${this.host}/objekat/delete/${naziv}`);
  }
  public updateProfileImage(formData:FormData):Observable<HttpEvent<Objekat>>{
    return this.httpClient.post<Objekat>(`${this.host}/objekat/updateProfileImage`,formData,{reportProgress:true,observe:'events'});
  }
  public addObjekatToLocalCache(objekti:Objekat[]):void{
    localStorage.setItem('objekti',JSON.stringify(objekti));
  }

  public getObjekatFromLocalCache():Objekat[]{
    if(localStorage.getItem('objekti')){
      return JSON.parse (localStorage.getItem('objekti'));
    }
    else{
      return null;
    }
  }

  public getObjekatByNaziv(naziv:string):Observable<Objekat>{
    return this.httpClient.get<Objekat>(`${this.host}/objekat/findByNaziv/${naziv}`);
  }
  public addRezervacija(formData:FormData):Observable<Rezervacija>{
    return this.httpClient.post<Rezervacija>(`${this.host}/objekat/rezervacija`,formData);
  }
  public getRezervacije(username:string):Observable<Rezervacija[]>{
    return this.httpClient.get<Rezervacija[]>(`${this.host}/objekat/listrezervacija/${username}`);
  }

  public getAllRezervacije(objekatNaziv:string):Observable<string[]>{
    return this.httpClient.get<string[]>(`${this.host}/objekat/listrez/${objekatNaziv}`);
  }

  public addKomentar(formData:FormData):Observable<Komentar>{
    return this.httpClient.post<Komentar>(`${this.host}/objekat/komentar/add`,formData);
  }

  public getKomentar(naziv:string):Observable<Komentar[]>{
    return this.httpClient.get<Komentar[]>(`${this.host}/objekat/listkomentar/${naziv}`);
  }
  public deleteKomentar(id:number):Observable<CustomHttpResponse>{
    return this.httpClient.delete<CustomHttpResponse>(`${this.host}/objekat/komentar/delete/${id}`)
  }

  public addRezToLocalStorage(rez:Rezervacija[]){
    localStorage.setItem('rezervacije',JSON.stringify(rez));
  }

  public addDatumiToLocalStorage(date:NgbDateStruct[]){
    localStorage.setItem('datumi',JSON.stringify(date));
  }
  public getDatumiFromLocalStorage():NgbDateStruct[]{
    if(localStorage.getItem('datumi')){
      return JSON.parse (localStorage.getItem('datumi'));
    }
    else{
      return null;
    }
  }
  public addZahtevToLocalStorage(rez:Rezervacija[]){
    localStorage.setItem('zahtevi',JSON.stringify(rez));
  }
  public getZahtevFromLocalStorage():Rezervacija[]{
    if(localStorage.getItem('zahtevi')){
      return JSON.parse (localStorage.getItem('zahtevi'));
    }
    else{
      return null;
    }
  }

  public createObjekatFormData(curNaziv:string,objekat:Objekat,slikaUrl:File,kategorija:string,lokacija:string):FormData{

    const formData=new FormData();
    formData.append('curNaziv',curNaziv);
    formData.append('naziv',objekat.naziv);
    formData.append('opis',objekat.opis);
    formData.append('adresa',objekat.adresa);
    formData.append('telefon',objekat.telefon);
    formData.append('isPetFriendly',JSON.stringify(objekat.isPetFriendly));
    formData.append('isSmoking',JSON.stringify(objekat.isSmoking));
    formData.append('brojOsoba',objekat.brojOsoba);
    formData.append('isParking',JSON.stringify(objekat.isParking));
    formData.append('isWifi',JSON.stringify(objekat.isWifi));
    formData.append('isTv',JSON.stringify(objekat.isTv));
    formData.append('isAirCondition',JSON.stringify(objekat.isAirCondition));
    formData.append('cena',objekat.cena);
    formData.append('email',objekat.email);
    formData.append('lokacija',lokacija);
    formData.append('slikaUrl',slikaUrl);
    formData.append('velicina',objekat.velicina);
    formData.append('kategorija',kategorija);
    return formData;

    
  }
  public createRezervacijaFormData(userNaziv:string,objekatNaziv:string,datumOd:string,datumDo:string,rezervacija:Rezervacija):FormData{
    const formData=new FormData();
    formData.append('userNaziv',userNaziv);
    formData.append('objekatNaziv',objekatNaziv);
    formData.append('datumOd',datumOd);
    formData.append('datumDo',datumDo);
    formData.append('ukupnoDana',rezervacija.ukupnoDana);
    formData.append('ukupnaCena',rezervacija.ukupnaCena);
    formData.append('brojGostiju',rezervacija.brojGostiju);
    return formData;
  }

  public createKomentarFormData(userNaziv:string,objekatNaziv:string,komentar:Komentar):FormData{
    const formData=new FormData();
    formData.append('userNaziv',userNaziv);
    formData.append('objekatNaziv',objekatNaziv);
    formData.append('opis',komentar.opis);
    formData.append('ocena',komentar.ocena);
    return formData;
  }

 
 

}
