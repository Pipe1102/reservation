import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { NgbDate, NgbDateStruct } from '@ng-bootstrap/ng-bootstrap';
import { CustomHttpResponse } from 'src/app/common/custom-http-response';
import { Rezervacija } from 'src/app/common/rezervacija';
import { NotificationType } from 'src/app/enum/notification-type.enum';
import { FooterService } from 'src/app/service/footer.service';
import { NavbarService } from 'src/app/service/navbar.service';
import { NotificationService } from 'src/app/service/notification.service';
import { ObjekatService } from 'src/app/service/objekat.service';


@Component({
  selector: 'app-rezervacija-list',
  templateUrl: './rezervacija-list.component.html',
  styleUrls: ['./rezervacija-list.component.css']
})
export class RezervacijaListComponent implements OnInit {
rezervacije:Rezervacija[];
zahtevi:Rezervacija[]=[];
  constructor(private objekatService:ObjekatService,private router:ActivatedRoute,public nav:NavbarService,public footer:FooterService,
    private notificationService:NotificationService) { }

  ngOnInit(): void {
    this.getAll();
    this.nav.show();
    this.footer.hide();
    this.getZahtevi();
  
   

  }

  public getAll():void{
    const username=this.router.snapshot.paramMap.get('username');
 
    this.objekatService.getRezervacije(username).subscribe(
      (response:Rezervacija[])=>{
        this.rezervacije=response;
        this.objekatService.addRezToLocalStorage(this.rezervacije);
      },
      (error:CustomHttpResponse)=>{
        this.rezervacije=[];
      }
    );
  }
  public getZahtevi(){
   this.zahtevi=this.objekatService.getZahtevFromLocalStorage();
  }
  public addRez(){
  this.rezervacije.forEach(element => {
    const date=element.datumOd;
  });
  }
  
  public onDeleteRez(rez:Rezervacija){

   /* if(this.zahtevi.find(el=>el.id===rez.id)){
      this.notificationService.notify(NotificationType.WARNING,'Zahtev je vec poslat');
    }*/
  this.zahtevi.push(rez);
    
    this.objekatService.addZahtevToLocalStorage(this.zahtevi);
    this.notificationService.notify(NotificationType.SUCCESS,`Zahtev za otkazivanje rezervacije ${rez.objekatNaziv} je poslat`);
    
  }

}
