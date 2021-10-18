export class User {

    public id: number;

    public userId: string;

    public ime: string;

    public prezime: string;

    public username: string

    public active: boolean

    public notLocked: boolean

    public email: string

    public password: string

    public imgUrl: string

    public points: number;

    public role: string

    public authorities: [];

    constructor() {
        this.userId='';
        this.ime = '';
        this.prezime = ''
        this.username = '';
        this.email = '';
        this.active = false;
        this.notLocked =false;
        this.imgUrl = '';
        this.role = '';
        this.points = 0;
        this.authorities = [];

    }

}
