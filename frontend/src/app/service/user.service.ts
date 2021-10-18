import { Injectable } from '@angular/core';
import { HttpClient, HttpEvent } from '@angular/common/http';
import {environment} from '../../environments/environment';
import { Observable } from 'rxjs';
import { User } from '../common/user';
import { JwtHelperService } from "@auth0/angular-jwt";
import { CustomHttpResponse } from '../common/custom-http-response';



@Injectable({
  providedIn: 'root'
})
export class UserService {
  
  private host=environment.apiUrl;

  constructor(private httpClient:HttpClient) { }

  public getUsers():Observable<User[]>{
    return this.httpClient.get<User[]>(`${this.host}/user/list`);
  }

  public addUser(formData:FormData):Observable<User>{
    return this.httpClient.post<User>(`${this.host}/user/add`,formData);
  }

  public updateUser(formData:FormData):Observable<User>{
    return this.httpClient.post<User>(`${this.host}/user/update`,formData);
  }

  public resetPassword(email:string):Observable<CustomHttpResponse>{
    return this.httpClient.get<CustomHttpResponse>(`${this.host}/user/resetpassword/${email}`);
  }

  public updateProfileImage(formData:FormData):Observable<HttpEvent<User>>{
    return this.httpClient.post<User>(`${this.host}/user/updateProfileImage`,formData,{reportProgress:true,observe:'events'});
  }

  public deleteUser(username:string):Observable<CustomHttpResponse>{
    return this.httpClient.delete<CustomHttpResponse>(`${this.host}/user/delete/${username}`);
  }

  public addUsersToLocalCache(users:User[]):void{
    localStorage.setItem('users',JSON.stringify(users));
  }

  public getUsersFromLocalCache():User[]{
    if(localStorage.getItem('users')){
      return JSON.parse (localStorage.getItem('users'));
    }
    else{
      return null;
    }
  }
 

  public createUserFormData(loggedInUsername:string,user:User,imgUrl:File):FormData{

    const formData=new FormData();
    formData.append('currentUsername',loggedInUsername);
    formData.append('ime',user.ime);
    formData.append('prezime',user.prezime);
    formData.append('username',user.username);
    formData.append('email',user.email);
    formData.append('role',user.role);
    formData.append('poeni',JSON.stringify(user.points));
    formData.append('imgUrl',imgUrl);
    formData.append('isActive',JSON.stringify(user.active));
    formData.append('isLocked',JSON.stringify(user.notLocked));
    return formData;

    
  }
}
