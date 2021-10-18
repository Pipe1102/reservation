import { Component, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Objekat } from 'src/app/common/objekat';
import { AuthenticationService } from 'src/app/service/authentication.service';
import { ObjekatService } from 'src/app/service/objekat.service';
import {Loader} from '@googlemaps/js-api-loader';
import { FooterService } from 'src/app/service/footer.service';
import { NavbarService } from 'src/app/service/navbar.service';
@Component({
  selector: 'app-search-result',
  templateUrl: './search-result.component.html',
  styleUrls: ['./search-result.component.css']
})
export class SearchResultComponent implements OnInit {
objekti:Objekat[]
lokacija:string;
public userPoeni:number;

  constructor(private objekatService:ObjekatService,private router:ActivatedRoute,private authService:AuthenticationService,
    public footer:FooterService,public nav:NavbarService) { }
  ngOnInit(): void {
    this.userPoeni=this.authService.getUserFromLocalCache().points;
    this.nav.show();
    this.footer.hide();
  this.getList();
  }
  
  getList():void{
    this.objekti=this.objekatService.getObjekatFromLocalCache();
    const uslov:string=this.router.snapshot.paramMap.get('lokacija');
    this.lokacija=uslov;
    this.objekti=this.objekti.filter(
      objekat=>objekat.lokacija===uslov
    );
    this.objekti.forEach(element => {
      if(this.userPoeni>20){
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

  onAddFilter(form:NgForm){
    if(form.value.isPetFriendly===""){
      form.value.isPetFriendly=false;
    }
    const isPetFriendly=form.value.isPetFriendly;

    if(form.value.isSmoking===""){
      form.value.isSmoking=false;
    }
    const isSmoking=form.value.isSmoking;

    if(form.value.isParking===""){
      form.value.isParking=false;
    }
    const isParking=form.value.isParking;

    if(form.value.isWifi===""){
      form.value.isWifi=false;
    }
    const isWifi=form.value.isWifi;

    if(form.value.isTv===""){
      form.value.isTv=false;
    }
    const isTv=form.value.isTv;
    
    if(form.value.isAirCondition===""){
      form.value.isAirCondition=false;
    }
    const isAirCondition=form.value.isAirCondition;

    
    
    this.objekti=this.objekti.filter(o=>o.isPetFriendly===isPetFriendly || o.isSmoking===isSmoking||o.isParking===isParking
      ||o.isWifi===isWifi||o.isAirCondition===isAirCondition||o.isTv===isTv);
    this.clickButton('filters-close');
  }

  applyFilters(){
    this.clickButton('new-filter-save');
  }

  private clickButton(buttonId: string): void {
    document.getElementById(buttonId).click();
  }
}


