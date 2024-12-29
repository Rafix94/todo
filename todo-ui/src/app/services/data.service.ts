import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { AppConstants } from "../constants/app.constants";
import { environment } from '../../environments/environment';
import { Observable } from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class DataService {

  constructor(private http: HttpClient) {
    console.log('DataService initialized');
  }

  getTasks(
    page: number | null,
    pageSize: number | null,
    selectedTeam: string,
    sortField: string | null,
    sortDirection: string | null,
    searchQuery: string | null
  ): Observable<any> {
    let params = new HttpParams();

    if (page !== null && pageSize !== null) {
      params = params.set('page', page.toString()).set('size', pageSize.toString());
    }

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

  getAllTasks(selectedTeam: string): Observable<any> {
    const params = new HttpParams().set('teamId', selectedTeam);
    return this.http.get(environment.rooturl + AppConstants.TASK_MANAGER_API_URL + AppConstants.TASKS_API_URL + '/all', { params });
  }

  createTask(task: any): Observable<any> {
    const url = environment.rooturl + AppConstants.TASK_MANAGER_API_URL + AppConstants.TASKS_API_URL;
    return this.http.post(url, task);
  }


  deleteTask(id: number): Observable<any> {
    const url = environment.rooturl + AppConstants.TASK_MANAGER_API_URL + AppConstants.TASKS_API_URL + "/" + id;
    return this.http.delete(url);
  }


  assignTask(taskId: number): Observable<Task> {
    return this.http.put<Task>(environment.rooturl + AppConstants.TASK_MANAGER_API_URL + AppConstants.TASKS_API_URL + "/" + taskId + "/" + "assign", {});
  }

  updateTaskWeight(taskId: number, weight: number): Observable<Task> {
    return this.http.put<Task>(environment.rooturl + AppConstants.TASK_MANAGER_API_URL + AppConstants.TASKS_API_URL + "/" + taskId + "/" + "weight", {weight});
  }

}
