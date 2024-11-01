import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Team } from 'src/app/model/team.model';
import { TeamsService } from 'src/app/services/teams.service';

@Component({
  selector: 'app-show-teams',
  templateUrl: './show-teams.component.html',
  styleUrls: ['./show-teams.component.css']
})
export class ShowTeamsComponent implements OnInit {
  teams: Team[] = [];

  constructor(private teamsService: TeamsService, private router: Router) {}

  ngOnInit(): void {
    // Fetch teams from the service
    this.teamsService.getAllTeams().subscribe((data) => {
      this.teams = data;
    });
  }

  // Navigate to team details page (Optional)
  viewTeamDetails(teamId: string): void {
    this.router.navigate(['/team-members', teamId]);
  }
}
