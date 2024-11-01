import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { AppConstants } from "../../constants/app.constants";
import { environment } from '../../../environments/environment';
import { Observable } from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class DataService {

  constructor(private http: HttpClient) {
    console.log('DataService initialized');
  }

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

    return this.http.get(environment.rooturl + AppConstants.TASK_MANAGER_API_URL + AppConstants.TASKS_API_URL, { params });
  }

  getTaskDetails(id: number): Observable<any> {
    console.log(`getTaskDetails called for ID: ${id}`);
    const url = environment.rooturl + AppConstants.TASK_MANAGER_API_URL + AppConstants.TASKS_API_URL + "/" + id;
    console.log(`Request URL: ${url}`);
    return this.http.get(url);
  }

  createTask(task: any, email: string): Observable<any> {
    console.log('createTask called with:');
    console.log(`Email: ${email}, Task:`, task);
    const url = environment.rooturl + AppConstants.TASK_MANAGER_API_URL + AppConstants.TASKS_API_URL + "?email=" + email;
    console.log(`Request URL: ${url}`);
    return this.http.post(url, task);
  }

  updateTask(id: number, task: any): Observable<any> {
    console.log(`updateTask called with ID: ${id}`);
    console.log(`Task:`, task);
    const url = environment.rooturl + AppConstants.TASK_MANAGER_API_URL + AppConstants.TASKS_API_URL + "/" + id;
    console.log(`Request URL: ${url}`);
    return this.http.put(url, task);
  }

  deleteTask(id: number): Observable<any> {
    console.log(`deleteTask called for ID: ${id}`);
    const url = environment.rooturl + AppConstants.TASK_MANAGER_API_URL + AppConstants.TASKS_API_URL + "/" + id;
    console.log(`Request URL: ${url}`);
    return this.http.delete(url);
  }
}
