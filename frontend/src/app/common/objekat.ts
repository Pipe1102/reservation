import { Objekat_Category } from "./objekat-category";

export class Objekat {

    public id:number;
    public naziv:string;
    public opis:string;
    public adresa:string;
    public telefon:string;
    public isPetFriendly:boolean;
    public isSmoking:boolean;
    public brojOsoba:string;
    public isParking:boolean;
    public isWifi:boolean;
    public isTv:boolean;
    public isAirCondition:boolean;
    public email:string;
    public cena:string;
    public lokacija:string;
    public slikaUrl:string;
    public velicina:string;
    public kategorija:string;

    constructor(){
        this.isPetFriendly=false;
        this.isSmoking=false;
        this.isParking=false;
        this.isWifi=false;
        this.isTv=false;
        this.isAirCondition=false;
    }
    

}
