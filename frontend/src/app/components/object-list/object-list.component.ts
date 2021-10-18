import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { NgbDate } from '@ng-bootstrap/ng-bootstrap';
import { CustomHttpResponse } from 'src/app/common/custom-http-response';
import { Objekat } from 'src/app/common/objekat';
import { Rezervacija } from 'src/app/common/rezervacija';
import { NotificationType } from 'src/app/enum/notification-type.enum';
import { AuthenticationService } from 'src/app/service/authentication.service';
import { FooterService } from 'src/app/service/footer.service';
import { NavbarService } from 'src/app/service/navbar.service';
import { NotificationService } from 'src/app/service/notification.service';
import { ObjekatService } from 'src/app/service/objekat.service';
import { SubSink } from 'subsink';

@Component({
  selector: 'app-object-list',
  templateUrl: './object-list.component.html',
  styleUrls: ['./object-list.component.css']
})
export class ObjectListComponent implements OnInit {
public objekti:Objekat[]=[];
public editObjekat:Objekat;
public curNaziv:string;
 private subs=new SubSink();
 public fileName: string;
 public slikaUrl: File;
 public kategorija:string=null;
 public naziv:string;
public brojOsoba:string;
document:Document;
public cenaPopust:string;
public userPoeni:number;


  constructor(private objectService:ObjekatService,private notificationService:NotificationService,public nav:NavbarService,
    private authService:AuthenticationService,public footer:FooterService) { }

  ngOnInit(): void {
    this.nav.show();
    this.footer.show();
    this.userPoeni=this.authService.getUserFromLocalCache().points;
   this.getAll();

  }
  
  public getKategorija(uslov:string){
    
   if(uslov==="all"){
     this.getAll();
   }
   else{
  this.objekti=this.objekti.filter(el=>el.kategorija===uslov);
  if(this.objekti.length===0){
    this.getAll();
    this.objekti=this.objekti.filter(el=>el.kategorija===uslov);
  }
   }
   
  }

  getAll(){
    this.objekti= this.objectService.getObjekatFromLocalCache();
    this.objekti.forEach(element => {
      if(this.userPoeni>=20){
       const parsedCena=parseInt(element.cena);
       const popust=parsedCena*0.2;
       const objekatCena=(parsedCena-popust).toString();
       element.cena=objekatCena;
      }
      if(this.userPoeni>=30){
        const parsedCena=parseInt(element.cena);
        const popust=parsedCena*0.3;
        const objekatCena=(parsedCena-popust).toString();
        element.cena=objekatCena;
       }
       if(this.userPoeni>=50){
        const parsedCena=parseInt(element.cena);
        const popust=parsedCena*0.5;
        const objekatCena=(parsedCena-popust).toString();
        element.cena=objekatCena;
       }

    });
  }





  public getLokacija(lokacija:string){
    this.objekti=this.objekti.filter(o=>o.lokacija===lokacija);
    if(this.objekti.length===0){
      this.getAll();
      this.objekti=this.objekti.filter(o=>o.lokacija===lokacija);
    }
  }

  public sortByMinCena(){
    this.objekti.sort((a,b)=>{
    return  parseInt(a.cena) -parseInt(b.cena);
       
    });
  }
  public sortByMaxCena(){
    this.objekti.sort((a,b)=>{
    return  parseInt(b.cena) -parseInt(a.cena);
       
    });
  }
  public sortByVelicina(){
    this.objekti.sort((a,b)=>{
      return  parseInt(b.velicina) -parseInt(a.velicina);
    })
  }
 
 

 
  
 


  private sendNotification(notificationType: NotificationType, message: string): void {
    if (message) {
      this.notificationService.notify(notificationType, message);
    }
    else {
      this.notificationService.notify(notificationType, 'Error.POKUSAJTE PONOVO');
    }
  }
  public onProfileImageChange(fileName: string, slikaUrl: File): void {
    this.fileName = fileName;
    this.slikaUrl = slikaUrl;
  }

  private clickButton(buttonId: string): void {
    document.getElementById(buttonId).click();
  }

  public saveNewObjekat(): void {
    this.clickButton('new-objekat-save');
  }
  public onDeleteObjekat(naziv: string): void {
    this.subs.add(
      this.objectService.deleteObjekat(naziv).subscribe(
        (response: CustomHttpResponse) => {
          this.sendNotification(NotificationType.SUCCESS, response.message);
        },
        (errorResponse: HttpErrorResponse) => {
          this.sendNotification(NotificationType.ERROR, errorResponse.error.message);
        }
      )
    );
  }
  

}
