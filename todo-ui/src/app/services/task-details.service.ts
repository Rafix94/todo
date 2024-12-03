import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {environment} from "../../environments/environment";
import {AppConstants} from "../constants/app.constants";

@Injectable({
  providedIn: 'root'
})
export class TaskDetailsService {
  private baseUrl = 'https://api.yourbackend.com/tasks';

  constructor(private http: HttpClient) {}

  getTaskDetails(taskId: string): Observable<any> {
    return this.http.get(environment.rooturl + AppConstants.TASK_MANAGER_API_URL + AppConstants.TASKS_API_URL + "/" + taskId + "/details");
  }

  addComment(taskId: string, formData: FormData): Observable<any> {
    return this.http.post(environment.rooturl + AppConstants.TASK_MANAGER_API_URL + AppConstants.TASKS_API_URL + "/" + taskId + "/comments", formData);
  }
}
