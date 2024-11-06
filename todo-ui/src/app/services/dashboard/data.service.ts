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

  getTasks(page: number, pageSize: number, selectedTeam: string, sortField: string, sortDirection: string, searchQuery: string): Observable<any> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', pageSize.toString());

    if (sortField && sortDirection) {
      params = params.set('sort', `${sortField},${sortDirection}`);
    }
    if (searchQuery) {
      params = params.set('searchQuery', searchQuery);
    }
    if (selectedTeam) {
      params = params.set('teamId', selectedTeam);
    }

    return this.http.get(environment.rooturl + AppConstants.TASK_MANAGER_API_URL + AppConstants.TASKS_API_URL, { params });
  }

  getTaskDetails(id: number): Observable<any> {
    const url = environment.rooturl + AppConstants.TASK_MANAGER_API_URL + AppConstants.TASKS_API_URL + "/" + id;
    return this.http.get(url);
  }

  createTask(task: any): Observable<any> {
    const url = environment.rooturl + AppConstants.TASK_MANAGER_API_URL + AppConstants.TASKS_API_URL;
    return this.http.post(url, task);
  }

  updateTask(id: number, task: any): Observable<any> {
    const url = environment.rooturl + AppConstants.TASK_MANAGER_API_URL + AppConstants.TASKS_API_URL + "/" + id;
    return this.http.put(url, task);
  }

  deleteTask(id: number): Observable<any> {
    const url = environment.rooturl + AppConstants.TASK_MANAGER_API_URL + AppConstants.TASKS_API_URL + "/" + id;
    return this.http.delete(url);
  }


  assignTask(taskId: number): Observable<Task> {
    // Calls the API endpoint with the taskId in the URL path
    return this.http.put<Task>(environment.rooturl + AppConstants.TASK_MANAGER_API_URL + AppConstants.TASKS_API_URL + "/" + taskId + "/" + "assign", {});
  }
}
