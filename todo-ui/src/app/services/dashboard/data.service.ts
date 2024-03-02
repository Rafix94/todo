import { Injectable } from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import { AppConstants } from "../../constants/app.constants";
import { environment } from '../../../environments/environment';
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class DataService {

  constructor(private http:HttpClient) { }

  getTasksDetails(email: String){
    return this.http.get(environment.rooturl + AppConstants.TASKS_API_URL+ "/tasks?email="+email,{ observe: 'response',withCredentials: true });
  }

  getData(page: number, pageSize: number, email: string): Observable<any> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('pageSize', pageSize.toString())
      // .set('observe', 'response')
      .set('withCredentials', true);

    return this.http.get(environment.rooturl + AppConstants.TASKS_API_URL+"/tasks?email=" + email, { params });
  }

}