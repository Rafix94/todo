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

  getTasks(page: number, pageSize: number, email: string, sortField: string, sortDirection: string, searchQuery: string): Observable<any> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', pageSize.toString());

    if (sortField && sortDirection) {
      params = params.set('sort', `${sortField},${sortDirection}`);
    }
    if (searchQuery) {
      params = params.set('searchQuery', searchQuery);
    }

    return this.http.get(environment.rooturl + AppConstants.USER_AGENT_API_URL + AppConstants.TASKS_API_URL + "?email=" + email, { params });
  }

  getTaskDetails(id: number): Observable<any> {
    return this.http.get(environment.rooturl + AppConstants.USER_AGENT_API_URL + AppConstants.TASKS_API_URL + "/" + id);
  }

  createTask(task: any, email: string): Observable<any> {
    return this.http.post(environment.rooturl + AppConstants.USER_AGENT_API_URL + AppConstants.TASKS_API_URL + "?email=" + email, task);
  }

  updateTask(id: number, task: any): Observable<any> {
    return this.http.put(environment.rooturl + AppConstants.USER_AGENT_API_URL + AppConstants.TASKS_API_URL + "/" + id, task);
  }

  deleteTask(id: number): Observable<any> {
    return this.http.delete(environment.rooturl + AppConstants.USER_AGENT_API_URL + AppConstants.TASKS_API_URL + "/" + id);
  }
}
