import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { AppConstants } from "../../constants/app.constants";
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class DashboardService {

  constructor(private http:HttpClient) { }

  getTasksDetails(email: String){
    return this.http.get(environment.rooturl + AppConstants.TASKS_API_URL+ "tasks?email="+email,{ observe: 'response',withCredentials: true });
  }

}
