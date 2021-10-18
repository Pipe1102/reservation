import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { NotificationType } from 'src/app/enum/notification-type.enum';
import { Role } from 'src/app/enum/role.enum';
import { AuthenticationService } from 'src/app/service/authentication.service';
import { NavbarService } from 'src/app/service/navbar.service';
import { NotificationService } from 'src/app/service/notification.service';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit {
username:string;
isLoggedIn:boolean;
  constructor(private auth:AuthenticationService,public nav:NavbarService,private router:Router,private notificationService:NotificationService) { }

  ngOnInit(): void {
    this.isUserLoggedIn();
    if(this.isLoggedIn){
    this.username=this.auth.getUserFromLocalCache().username;
    }
    
  }
  isUserLoggedIn():void{
    this.isLoggedIn=this.auth.isUserLoggedIn();
    
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
    return this.auth.getUserFromLocalCache().role;
  }
  public onLogOut() {
    this.auth.logOut();
    this.router.navigate(['/login']);
    this.sendNotification(NotificationType.SUCCESS, 'Uspesno ste se odjavili');
  }
  private sendNotification(notificationType: NotificationType, message: string): void {
    if (message) {
      this.notificationService.notify(notificationType, message);
    }
    else {
      this.notificationService.notify(notificationType, 'Error.POKUSAJTE PONOVO');
    }
  }

}
