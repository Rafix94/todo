import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { AppConstants } from "../../constants/app.constants";
import { environment } from '../../../environments/environment';
import { Observable } from "rxjs";
import {io, Socket} from "socket.io-client";

@Injectable({
  providedIn: 'root'
})
export class DataService {

  private socket: Socket;

  constructor(private http: HttpClient) {
    console.log('DataService initialized');
    this.socket = io(environment.rooturl + AppConstants.REFINEMENT_SERVICE_API_URL + "/ws");
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

  checkSessionStatus(teamId: string): Observable<{ sessionId: string; active: boolean }> {
    return this.http.get<{ sessionId: string; active: boolean }>(`/api/teams/${teamId}/session`);
  }

  createSession(teamId: string): Observable<{ sessionId: string }> {
    return this.http.post<{ sessionId: string }>(`/api/teams/${teamId}/session`, {});
  }


  emitCreateSession(teamId: string): void {
    this.socket.emit('/app/session/create', { teamId });
  }

  emitJoinSession(teamId: string): void {
    this.socket.emit('/app/session/join', { teamId });
  }

  emitTaskUpdate(teamId: string, currentTask: string): void {
    this.socket.emit('/app/session/task/update', { teamId, currentTask });
  }

  onSessionUpdate(callback: (data: { teamId: string; sessionId: string; status: string; currentTask?: string }) => void): void {
    this.socket.on('/topic/session-updates', callback);
  }

  disconnectSocket(): void {
    this.socket.disconnect();
  }

}
