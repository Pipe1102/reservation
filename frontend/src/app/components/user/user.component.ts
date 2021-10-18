import { HttpErrorResponse, HttpEvent, HttpEventType } from '@angular/common/http';
import { Component, OnInit, OnDestroy } from '@angular/core';
import { NgForm } from '@angular/forms';
import { Router } from '@angular/router';
import { BehaviorSubject } from 'rxjs';
import { CustomHttpResponse } from 'src/app/common/custom-http-response';
import { FileUploadStatus } from 'src/app/common/file-upload-status';
import { Objekat } from 'src/app/common/objekat';
import { User } from 'src/app/common/user';
import { NotificationType } from 'src/app/enum/notification-type.enum';
import { Role } from 'src/app/enum/role.enum';
import { AuthenticationService } from 'src/app/service/authentication.service';
import { FooterService } from 'src/app/service/footer.service';
import { NavbarService } from 'src/app/service/navbar.service';
import { NotificationService } from 'src/app/service/notification.service';
import { ObjekatService } from 'src/app/service/objekat.service';
import { UserService } from 'src/app/service/user.service';
import { SubSink } from 'subsink';

@Component({
  selector: 'app-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.css']
})
export class UserComponent implements OnInit, OnDestroy {
  private subs = new SubSink();
  private titleSubject = new BehaviorSubject<String>('Users');
  public titleAction$ = this.titleSubject.asObservable();
  public users: User[];
  public objekti:Objekat[];
  public editObjekat=new Objekat();
  public clickObjekat:Objekat;
  public selectedObjekat:Objekat;
  public curObjekatNaziv:string;
  public kategorija:string;
  public user: User;
  public editUser = new User();
  public clickUser: User = new User();
  public refreshing: boolean = false;
  public selectedUser: User;
  public fileName: string;
  public imgUrl: File;
  public currentIsActive: string;
  public currentIsNotLocked: string;
  private currentUsername: string;
  public fileStatus = new FileUploadStatus();
public lokacija:string;

  constructor(private userService: UserService, private notificationService: NotificationService,
    private authenticationService: AuthenticationService, private router: Router,private objekatService:ObjekatService,public nav:NavbarService,
    public footer:FooterService) { }

  ngOnInit(): void {
    this.nav.show();
    this.footer.hide();
    this.user = this.authenticationService.getUserFromLocalCache();
    this.getUsers(true);
    this.getObjekti();
  }


  public changeTitle(title: string): void {
    this.titleSubject.next(title);
  }

  public getUsers(showNotification: boolean): void {
    this.refreshing = true;
    this.subs.add(
      this.userService.getUsers().subscribe(
        (response: User[]) => {
          this.userService.addUsersToLocalCache(response);
          this.users = response;
          this.refreshing = false;
          if (showNotification) {
            this.sendNotification(NotificationType.SUCCESS, `${response.length} korisnik/ci uspesno ucitani `);
          }
        },
        (errorResponse: HttpErrorResponse) => {
          this.sendNotification(NotificationType.ERROR, `${errorResponse.error.message}`);
        }
    ));
  }
  public getObjekti(){
  this.subs.add(
    this.objekatService.getObjekti().subscribe(
      (response:Objekat[])=>{
        this.objekatService.addObjekatToLocalCache(response);
        this.objekti=response;
      }
    )
  );
  }

  public onSelectUser(selecetedUser: User): void {
    this.selectedUser = selecetedUser;
    this.clickButton('openUserInfo');
  }
  onSelectObjekat(selectedObjekat:Objekat):void{
    this.selectedObjekat=selectedObjekat;
    this.clickButton('openObjekatInfo');
  }
  public saveNewObjekat(): void {
    this.clickButton('new-objekat-save');
  }
  public onAddNewObjekat(objekatForm:NgForm):void{
    this.kategorija= objekatForm.value.kategorija;
    this.lokacija=objekatForm.value.lokacija;
     const formData= this.objekatService.createObjekatFormData(null,objekatForm.value,this.imgUrl,this.kategorija,this.lokacija);
     this.subs.add(
       this.objekatService.addObjekat(formData).subscribe(
         (response:Objekat)=>{
           this.clickButton('new-objekat-close');
           this.fileName=null;
           this.imgUrl=null;
           this.kategorija=null;
           objekatForm.reset();
           this.getObjekti();
           this.sendNotification(NotificationType.SUCCESS,`${response.naziv} uspesno dodat`);
         },
         (errorResponse: HttpErrorResponse) => {
           this.sendNotification(NotificationType.ERROR, errorResponse.error.message);
           this.imgUrl = null;
         }
       )
     );
   }

   public onEditObjekat(editObjekat:Objekat){
    this.editObjekat=editObjekat;
    this.curObjekatNaziv=editObjekat.naziv;
    this.clickButton('openObjekatEdit');
  }
  public onUpdateObjekat(){
    this.kategorija=this.editObjekat.kategorija;
    this.lokacija=this.editObjekat.lokacija;
    const formData=this.objekatService.createObjekatFormData(this.curObjekatNaziv,this.editObjekat,this.imgUrl,this.kategorija,this.lokacija);
    this.subs.add(
      this.objekatService.updateObjekat(formData).subscribe(
        (response:Objekat)=>{
          this.clickButton('closeEditObjekatModalButton');
          this.fileName=null;
          this.imgUrl=null;
          this.sendNotification(NotificationType.SUCCESS, `Objekat: ${response.naziv}  uspesno promenjen`);
        },
        (error:HttpErrorResponse)=>{
          this.sendNotification(NotificationType.ERROR, error.error.message);
        }
      )
    );
  }
  public onDeleteObjekat(naziv:string){
    
  
    this.subs.add(
      this.objekatService.deleteObjekat(naziv).subscribe(
        (response: CustomHttpResponse) => {
          this.sendNotification(NotificationType.SUCCESS, response.message);
         this.getObjekti();
        },
        (errorResponse: HttpErrorResponse) => {
          this.sendNotification(NotificationType.ERROR, errorResponse.error.message);
        }
      )
    );
  }
  

  public onProfileImageChange(fileName: string, imgUrl: File): void {
    this.fileName = fileName;
    this.imgUrl = imgUrl;
  }
 public saveNewUser(): void {
    this.clickButton('new-user-save');
  }

  public onAddNewUser(userForm: NgForm): void {
    const formData = this.userService.createUserFormData(null, userForm.value, this.imgUrl);

    this.subs.add(
      this.userService.addUser(formData).subscribe(
        (response: User) => {
          this.clickButton('new-user-close');
          this.getUsers(false);
          this.fileName = null;
          this.imgUrl = null;
          userForm.reset();
          this.sendNotification(NotificationType.SUCCESS, `${response.ime} ${response.prezime} uspesno dodat`);
        },
        (errorResponse: HttpErrorResponse) => {
          this.sendNotification(NotificationType.ERROR, errorResponse.error.message);
          this.imgUrl = null;
        }
      )
    );
  }
  public onEditUser(editUser: User): void {
    this.editUser = editUser;
    this.currentUsername = editUser.username;
    this.clickButton('openUserEdit');
  }

  public onUpdateUser(): void {

    const formData = this.userService.createUserFormData(this.currentUsername, this.editUser, this.imgUrl);
    this.subs.add(
      this.userService.updateUser(formData).subscribe(
        (response: User) => {
          this.clickButton('closeEditUserModalButton');
          this.getUsers(false);
          this.fileName = null;
          this.imgUrl = null;
          this.sendNotification(NotificationType.SUCCESS, `${response.ime} ${response.prezime} uspesno promenjen`);
        },
        (errorResponse: HttpErrorResponse) => {
          this.sendNotification(NotificationType.ERROR, errorResponse.error.message);
          this.imgUrl = null;
        }
      )
    );
  }

  public onDeleteUder(username: string): void {
    this.subs.add(
      this.userService.deleteUser(username).subscribe(
        (response: CustomHttpResponse) => {
          this.sendNotification(NotificationType.SUCCESS, response.message);
          this.getUsers(true);
        },
        (errorResponse: HttpErrorResponse) => {
          this.sendNotification(NotificationType.ERROR, errorResponse.error.message);
        }
      )
    );
  }

  public onResetPassword(emailForm: NgForm) {
    this.refreshing = true;
    const email = emailForm.value['reset-password-email'];
    this.subs.add(
      this.userService.resetPassword(email).subscribe(
        (response: CustomHttpResponse) => {
          this.sendNotification(NotificationType.SUCCESS, response.message);
          this.refreshing = false;
        },
        (errorResponse: HttpErrorResponse) => {
          this.sendNotification(NotificationType.WARNING, errorResponse.error.message);
          this.refreshing = false;
        },
        () => emailForm.reset()
      )
    );
  }
  public onUpdateCurrentUser(user: User): void {
    this.refreshing = true;
    this.currentUsername = this.authenticationService.getUserFromLocalCache().username;
    const formData = this.userService.createUserFormData(this.currentUsername, user, this.imgUrl);
    this.subs.add(
      this.userService.updateUser(formData).subscribe(
        (response: User) => {
          this.authenticationService.addUserToLocalCache(response);
          this.getUsers(false);
          this.fileName = null;
          this.imgUrl = null;
          this.sendNotification(NotificationType.SUCCESS, `${response.ime} ${response.prezime} uspesno promenjen`);
        },
        (errorResponse: HttpErrorResponse) => {
          this.sendNotification(NotificationType.ERROR, errorResponse.error.message);
          this.imgUrl = null;
          this.refreshing = true;
        }
      )
    );
  }



  public updateProfileImage() {
    this.clickButton('profile-image-input');
  }
  public onUpdateProfileImage(): void {
    const formData = new FormData();
    formData.append('username', this.user.username);
    formData.append('imgUrl', this.imgUrl);
    this.subs.add(
      this.userService.updateProfileImage(formData).subscribe(
        (response: HttpEvent<any>) => {
          this.reportUploadProgress(response);
        },
        (errorResponse: HttpErrorResponse) => {
          this.sendNotification(NotificationType.ERROR, errorResponse.error.message);
          this.fileStatus.status = 'done';
        }
      )
    );
  }
  private reportUploadProgress(event: HttpEvent<any>): void {
    switch (event.type) {
      case HttpEventType.UploadProgress:
        this.fileStatus.percentage = Math.round(100 * event.loaded / event.total);
        this.fileStatus.status = 'progress';
        break;
      case HttpEventType.Response:
        if (event.status === 200) {
          this.user.imgUrl = `${event.body.imgUrl}?time=${new Date().getTime()}`;
          this.sendNotification(NotificationType.SUCCESS, `${event.body.ime} \s slika uspesno promenjena`);
          this.fileStatus.status = 'done';
          break;
        }
        else {
          this.sendNotification(NotificationType.SUCCESS, `Neuspenso dodavanje slike,probajte ponovo`);
          break;
        }
      default:
        `Gotovi svi procesi`;
    }
  }


  public onLogOut() {
    this.authenticationService.logOut();
    this.router.navigate(['/login']);
    this.sendNotification(NotificationType.SUCCESS, 'Uspesno ste se odjavili');
  }

  public searchUsers(searchTerm: string): void {
    const result: User[] = [];
    for (const user of this.userService.getUsersFromLocalCache()) {
      if (user.ime.toLowerCase().indexOf(searchTerm.toLowerCase()) !== -1 ||
        user.prezime.toLowerCase().indexOf(searchTerm.toLowerCase()) !== -1 ||
        user.username.toLowerCase().indexOf(searchTerm.toLowerCase()) !== -1 ||
        user.userId.toLowerCase().indexOf(searchTerm.toLowerCase()) !== -1) {
        result.push(user);
      }
    }
    this.users = result;
    if (result.length === 0 || !searchTerm) {
      this.users = this.userService.getUsersFromLocalCache();
    }
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
    return this.authenticationService.getUserFromLocalCache().role;
  }



  private sendNotification(notificationType: NotificationType, message: string): void {
    if (message) {
      this.notificationService.notify(notificationType, message);
    }
    else {
      this.notificationService.notify(notificationType, 'Error.POKUSAJTE PONOVO');
    }
  }

  private clickButton(buttonId: string): void {
    document.getElementById(buttonId).click();
  }


  ngOnDestroy(): void {
    this.subs.unsubscribe();
  }




}


