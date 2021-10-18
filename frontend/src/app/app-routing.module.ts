import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HomeComponent } from './components/home/home.component';
import { InfoComponent } from './components/info/info.component';
import { LoginComponent } from './components/login/login.component';
import { ObjectListComponent } from './components/object-list/object-list.component';
import { ObjekatDetailsComponent } from './components/objekat-details/objekat-details.component';
import { RegisterComponent } from './components/register/register.component';
import { RezervacijaListComponent } from './components/rezervacija-list/rezervacija-list.component';
import { SearchResultComponent } from './components/search-result/search-result.component';
import { UserComponent } from './components/user/user.component';
import { AuthenticationGuard } from './guard/authentication.guard';

const routes: Routes = [
  {path:'login',component:LoginComponent},
  {path:'register',component:RegisterComponent},
  {path:'user/management',component:UserComponent,canActivate: [AuthenticationGuard]},
  {path:'home',component:HomeComponent},
  {path:'asortiman',component:ObjectListComponent},
  {path:'objekat/:naziv',component:ObjekatDetailsComponent,canActivate: [AuthenticationGuard]},
  {path:'pretraga/:lokacija',component:SearchResultComponent},
  {path:'rezervacije/:username',component:RezervacijaListComponent,canActivate: [AuthenticationGuard]},
  {path:'info',component:InfoComponent},
  {path:'',redirectTo:'/login',pathMatch:'full'}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
