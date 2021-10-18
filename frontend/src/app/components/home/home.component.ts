import { Component, OnInit } from '@angular/core';
import { Objekat } from 'src/app/common/objekat';
import { FooterService } from 'src/app/service/footer.service';
import { NavbarService } from 'src/app/service/navbar.service';
import { ObjekatService } from 'src/app/service/objekat.service';
import { SubSink } from 'subsink';
@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {
objekti:Objekat[];
  constructor(private service:ObjekatService,public nav:NavbarService,public footer:FooterService) { }

  ngOnInit(): void {
    this.footer.show();
   this.nav.show();
    this.getObjekti();
  }

  public getObjekti():void{
    this.service.getObjekti().subscribe(
      (res:Objekat[])=>{
        this.objekti=res;
        this.service.addObjekatToLocalCache(res);        
      }
    );
  }

}
