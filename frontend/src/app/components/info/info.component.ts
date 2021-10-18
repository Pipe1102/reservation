import { Component, OnInit } from '@angular/core';
import {Loader} from '@googlemaps/js-api-loader';
@Component({
  selector: 'app-info',
  templateUrl: './info.component.html',
  styleUrls: ['./info.component.css']
})
export class InfoComponent implements OnInit {
zoom:number=6;
lat=51.233334;
lng=6.78333;
  constructor() { }

  ngOnInit(): void {
    let loader=new Loader({
      apiKey:'AIzaSyAAcvtJPpt0HLFjagCmrv3-ULCUL_h66Ps'
      })
      loader.load().then(()=>{
        new google.maps.Map(document.getElementById('maps'),{
          center:{lat:51.233334,lng:6.78333},
          zoom:6
        });
      })
  }

}
