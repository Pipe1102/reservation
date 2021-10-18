import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import {NgbDateStruct,NgbDateParserFormatter, NgbDate } from '@ng-bootstrap/ng-bootstrap';
import { Komentar } from 'src/app/common/komentar';
import { Objekat } from 'src/app/common/objekat';
import { Rezervacija } from 'src/app/common/rezervacija';
import { NotificationType } from 'src/app/enum/notification-type.enum';
import { AuthenticationService } from 'src/app/service/authentication.service';
import { NotificationService } from 'src/app/service/notification.service';
import { ObjekatService } from 'src/app/service/objekat.service';
import { NgbDatepickerConfig} from '@ng-bootstrap/ng-bootstrap';
import { NavbarService } from 'src/app/service/navbar.service';
import { FooterService } from 'src/app/service/footer.service';
import { CustomHttpResponse } from 'src/app/common/custom-http-response';
import { Role } from 'src/app/enum/role.enum';

@Component({
  selector: 'app-objekat-details',
  templateUrl: './objekat-details.component.html',
  styleUrls: ['./objekat-details.component.css']
})
export class ObjekatDetailsComponent implements OnInit {
objekat:Objekat;
rezervacija=new Rezervacija();
datumOdForm:NgbDate;
datumDoForm:NgbDate;
userNaziv:string;
objekatNaziv:string;
ukupnaCena:string;
ukupnoDana:string;
rezervisaniDatumi:NgbDateStruct[]=[];
komentari:Komentar[];
public rezervacije:Rezervacija[]=[];
minDate:NgbDate;
public userPoeni:number;




  constructor(private route:ActivatedRoute,private objekatService:ObjekatService,
    private authinticationService:AuthenticationService,private notificationService:NotificationService,private config:NgbDatepickerConfig
    ,private ngbDateParserFormatter:NgbDateParserFormatter,public nav:NavbarService,public footer:FooterService) { 
     
    }

  ngOnInit(): void {
   this.userPoeni= this.authinticationService.getUserFromLocalCache().points;
    this.nav.show();
    this.footer.hide();
    this.getAllRezervisani();
    this.setNgbDates();
    this.route.paramMap.subscribe(()=>{
    this.hanleObjekatDetails();
    });
    this.getKomentar();
    this.getTest();
  
  }
  hanleObjekatDetails() {
    const naziv:string=this.route.snapshot.paramMap.get('naziv');
    this.objekatService.getObjekatByNaziv(naziv).subscribe(
    data=>{
      this.objekat=data;
      
        if(this.userPoeni>=20){
         const parsedCena=parseInt(this.objekat.cena);
         const popust=parsedCena*0.2;
         const objekatCena=(parsedCena-popust).toString();
         this.objekat.cena=objekatCena;
        }
        if(this.userPoeni>=30){
          const parsedCena=parseInt(this.objekat.cena);
          const popust=parsedCena*0.3;
          const objekatCena=(parsedCena-popust).toString();
          this.objekat.cena=objekatCena;
         }
         if(this.userPoeni>=50){
          const parsedCena=parseInt(this.objekat.cena);
          const popust=parsedCena*0.5;
          const objekatCena=(parsedCena-popust).toString();
          this.objekat.cena=objekatCena;
         }
        
      
    }
    );
  }
  getTest():NgbDateStruct[]{
    return this.objekatService.getDatumiFromLocalStorage();
  }

  isDisabled = (date: NgbDateStruct, current: {month: number}) => {
     if(this.rezervisaniDatumi.find(el=>el.day===date.day && el.month===current.month)){
       return true;
     }
     else return false;
}
onChange(){
const brojDana=this.datumDoForm.day-this.datumOdForm.day;
const cena=parseInt(this.objekat.cena);
const ukupno=cena*brojDana;
this.ukupnaCena=ukupno.toString();
this.ukupnoDana=brojDana.toString();
}
  
 


  public getKomentar(){
    const naziv:string=this.route.snapshot.paramMap.get('naziv');
    this.objekatService.getKomentar(naziv).subscribe(
      data=>{
        this.komentari=data;
      }
    );
  }
  onDeleteKomentar(id:number){
    this.objekatService.deleteKomentar(id).subscribe(
      (response:CustomHttpResponse)=>{
        this.notificationService.notify(NotificationType.SUCCESS,response.message);
        this.getKomentar();
      }
    );
  }
  public onAddRezervacija(objekat:Objekat){
    this.objekat=objekat;
    this.clickButton('openRezervacija');
  }
  public saveRezervacija(n:NgForm):void{
 const datoOd:Date=new Date(
   n.value.datumOdForm.year,
   n.value.datumOdForm.month-1,
   n.value.datumOdForm.day+1
   );
 const dateDo:Date=new Date(
  n.value.datumDoForm.year,
  n.value.datumDoForm.month-1,
  n.value.datumDoForm.day+2
  );
 
  this.rezervacija.ukupnaCena=this.ukupnaCena;
  this.rezervacija.ukupnoDana=this.ukupnoDana;
  this.rezervacija.brojGostiju=this.objekat.brojOsoba;
  this.userNaziv=this.authinticationService.getUserFromLocalCache().username;
  const date1:string =(datoOd).toISOString();
  const date2:string=(dateDo).toISOString();
  const formData=this.objekatService.createRezervacijaFormData(this.userNaziv,this.objekat.naziv,date1,date2,this.rezervacija);
  this.objekatService.addRezervacija(formData).subscribe(
    (response:Rezervacija)=>{
      this.clickButton('new-rezervacija-close');
      this.sendNotification(NotificationType.SUCCESS,`Uspesno ste rezervisali ${this.objekat.naziv}`);
    },
    (errorResponse:HttpErrorResponse)=>{
      this.sendNotification(NotificationType.ERROR,errorResponse.error.message);
    }
  )
  
  }
  private clickButton(buttonId: string): void {
    document.getElementById(buttonId).click();
  }
  public saveNewRezervacija(){
    this.clickButton('new-rezervacija-save');
  }
  private sendNotification(notificationType: NotificationType, message: string): void {
    if (message) {
      this.notificationService.notify(notificationType, message);
    }
    else {
      this.notificationService.notify(notificationType, 'Error.POKUSAJTE PONOVO');
    }
  }
  
  
 

  saveKomentar(komentarForm:NgForm){
    this.userNaziv=this.authinticationService.getUserFromLocalCache().username;
    this.objekatNaziv=this.objekat.naziv;
    const formData=this.objekatService.createKomentarFormData(this.userNaziv,this.objekatNaziv,komentarForm.value);
    this.objekatService.addKomentar(formData).subscribe(
      (response:Komentar)=>{
        this.sendNotification(NotificationType.SUCCESS,"Uspesno ste dodali komentar");
        this.clickButton('new-komentar-close');
        this.getKomentar();
      },
      (error:HttpErrorResponse)=>{
        this.sendNotification(NotificationType.ERROR,error.error.message);
      }
    )
  
  }

  saveNewKomentar(){
 this.clickButton('new-komentar-save');
  }

  onAddKomentar(objekat:Objekat){
    this.objekat=objekat;
    this.clickButton('openKomentar');
  }




 getAllRezervisani(){
    this.objekatService.getAllRezervacije(this.route.snapshot.paramMap.get('naziv')).subscribe(
      (datumi:string[])=>{
    datumi.forEach(element => {
      let format=this.ngbDateParserFormatter.parse(element);
      this.rezervisaniDatumi.push(format);
      this.objekatService.addDatumiToLocalStorage(this.rezervisaniDatumi);
    });
      }
    );
  }
setNgbDates(){
  const today=new Date();
  this.datumOdForm=new NgbDate(today.getFullYear(),today.getMonth()+1,today.getDate());
  this.datumDoForm=this.minDate=new NgbDate(today.getFullYear(),today.getMonth()+1,today.getDate()+1)
  this.config.minDate={year:today.getFullYear(),month:today.getMonth()+1,day:today.getDate()};
  this.config.outsideDays="hidden";
  this.minDate=new NgbDate(today.getFullYear(),today.getMonth()+1,today.getDate()+1)
}

public get isAdmin(): boolean {
  return this.getUserRole() === Role.ADMIN;
}

public get isManager(): boolean {
  return this.isAdmin || this.getUserRole() === Role.MANAGER;
}

public get isAdminOrManager(): boolean {
  return this.isAdmin || this.isManager
}

private getUserRole(): string {
  return this.authinticationService.getUserFromLocalCache().role;
}
      
    }