import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { Team } from '../model/team.model';

@Injectable({
  providedIn: 'root'
})
export class TeamsService {
  // Sample data for mocking
  private teams: Team[] = [
    {
      id: '1',
      name: 'Development Team',
      description: 'Responsible for development tasks',
      members: ['user1@example.com', 'user2@example.com']
    },
    {
      id: '2',
      name: 'Design Team',
      description: 'Responsible for design tasks',
      members: ['user3@example.com', 'user4@example.com']
    },
    {
      id: '3',
      name: 'Marketing Team',
      description: 'Responsible for marketing strategies',
      members: ['user5@example.com']
    }
  ];

  getUserTeams(): Observable<Team[]> {
    // Here you can filter teams based on user logic if needed
    return of(this.teams);
  }

  getAllTeams(): Observable<Team[]> {
    return of(this.teams);
  }

  createTeam(team: Team): Observable<Team> {
    // Simulating the creation of a team by pushing it to the teams array
    this.teams.push({ ...team, id: (this.teams.length + 1).toString() });
    return of(team);
  }

  selectTeam(teamId: string) {
    sessionStorage.setItem('selectedTeamId', teamId);
  }

  getSelectedTeamId(): string | null {
    return sessionStorage.getItem('selectedTeamId');
  }
}
