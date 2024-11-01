import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Team } from '../model/team.model';

@Injectable({
  providedIn: 'root'
})
export class TeamService {
  private baseUrl = 'http://localhost:8093/api/teams';

  constructor(private http: HttpClient) {}

  getUserTeams(): Observable<Team[]> {
    return this.http.get<Team[]>(`${this.baseUrl}/user-teams`);
  }

  createTeam(team: Team): Observable<Team> {
    return this.http.post<Team>(`${this.baseUrl}/create`, team);
  }

  selectTeam(teamId: string) {
    sessionStorage.setItem('selectedTeamId', teamId);
  }

  getSelectedTeamId(): string | null {
    return sessionStorage.getItem('selectedTeamId');
  }
}
