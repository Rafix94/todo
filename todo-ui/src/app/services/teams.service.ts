import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { AppConstants } from 'src/app/constants/app.constants';
import { environment } from "../../environments/environment";
import { TeamDetailsDto } from '../model/teamDetailsDto';
import {TeamSummaryDto} from "../model/teamSummaryDto";

@Injectable({
  providedIn: 'root'
})
export class TeamsService {
  private baseUrl = environment.rooturl + AppConstants.USER_AGENT_API_URL + AppConstants.TEAMS_API_URL;

  constructor(private http: HttpClient) {
    console.log('TeamsService initialized');
  }

  getAllTeams(): Observable<TeamSummaryDto[]> {
    const url = `${this.baseUrl}`;
    return this.http.get<TeamSummaryDto[]>(url);
  }

  getTeamDetails(teamId: string): Observable<TeamDetailsDto> {
    const url = `${this.baseUrl}/${teamId}`;
    return this.http.get<TeamDetailsDto>(url);
  }

  createTeam(team: TeamDetailsDto): Observable<TeamDetailsDto> {
    const url = `${this.baseUrl}`;
    return this.http.post<TeamDetailsDto>(url, team);
  }

  joinTeam(teamId: string): Observable<void> {
    return this.http.post<void>(`${this.baseUrl}/join/${teamId}`, {});
  }

  getTeamMembers(teamId: string): Observable<TeamSummaryDto[]> {
    return this.http.get<TeamSummaryDto[]>(`${this.baseUrl}/${teamId}/members`);
  }
}
