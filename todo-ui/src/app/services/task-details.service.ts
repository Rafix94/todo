import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { AppConstants } from '../constants/app.constants';

@Injectable({
  providedIn: 'root',
})
export class TaskDetailsService {
  private baseUrl = environment.rooturl + AppConstants.TASK_MANAGER_API_URL + AppConstants.TASKS_API_URL;

  constructor(private http: HttpClient) {}

  getTaskDetails(taskId: string): Observable<any> {
    return this.http.get(`${this.baseUrl}/${taskId}/details`);
  }

  addComment(taskId: string, formData: FormData): Observable<any> {
    return this.http.post(`${this.baseUrl}/${taskId}/comments`, formData);
  }

  analyzeFile(fileId: number): Observable<{ analyzedFileUrl: string }> {
    return this.http.post<{ analyzedFileUrl: string }>(
      `${this.baseUrl}/files/${fileId}/analyze`,
      {}
    );
  }
}
