import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpClient, HttpParams } from '@angular/common/http';
import { AppConstants } from 'src/app/constants/app.constants';
import { environment } from "../../environments/environment";
import { TeamDetailsDto } from '../model/teamDetailsDto';
import { TeamSummaryDto } from "../model/teamSummaryDto";

@Injectable({
  providedIn: 'root'
})
export class TeamsService {
  private baseUrl = environment.rooturl + AppConstants.USER_AGENT_API_URL + AppConstants.TEAMS_API_URL;

  constructor(private http: HttpClient) {
    console.log('TeamsService initialized');
  }

  // Add filter parameter for flexible filtering options
  getAllTeams(membershipStatus: 'MEMBER' | 'NOT_MEMBER' | 'ALL' = 'ALL'): Observable<TeamSummaryDto[]> {
    let url = `${this.baseUrl}`;
    let params = new HttpParams();

    // Add filter to the query params if provided
    if (['MEMBER', 'NOT_MEMBER', 'ALL'].includes(membershipStatus)) {
      params = params.set('membershipStatus', membershipStatus);
    }

    return this.http.get<TeamSummaryDto[]>(url, { params });
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
}
