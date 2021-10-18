import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { AuthenticationService } from './service/authentication.service';
import { UserService } from './service/user.service';
import { AuthInterceptor } from './interceptor/auth.interceptor';
import { AuthenticationGuard } from './guard/authentication.guard';
import { NotificationModule } from './notification.module';
import { NotificationService } from './service/notification.service';
import { LoginComponent } from './components/login/login.component';
import { RegisterComponent } from './components/register/register.component';
import { UserComponent } from './components/user/user.component';
import { FormsModule } from '@angular/forms';
import { HomeComponent } from './components/home/home.component';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { ObjectListComponent } from './components/object-list/object-list.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ObjekatDetailsComponent } from './components/objekat-details/objekat-details.component';
import { SearchResultComponent } from './components/search-result/search-result.component';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { RezervacijaListComponent } from './components/rezervacija-list/rezervacija-list.component';
import { HeaderComponent } from './components/header/header.component';
import { InfoComponent } from './components/info/info.component';
import { FooterComponent } from './components/footer/footer.component';









@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    RegisterComponent,
    UserComponent,
    HomeComponent,
    ObjectListComponent,
    ObjekatDetailsComponent,
    SearchResultComponent,
    RezervacijaListComponent,
    HeaderComponent,
    InfoComponent,
    FooterComponent,

  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    HttpClientModule,
    NotificationModule,
    BrowserAnimationsModule,
    NgbModule,

  ],
  schemas:[
    NO_ERRORS_SCHEMA
  ],
  providers: [NotificationService,AuthenticationGuard, AuthenticationService, UserService
    , { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true }],
  bootstrap: [AppComponent]
})
export class AppModule { }
