import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { TeamsService } from 'src/app/services/teams.service';
import {TeamSummaryDto} from "../../model/teamSummaryDto";

@Component({
  selector: 'app-show-teams',
  templateUrl: './show-teams.component.html',
  styleUrls: ['./show-teams.component.css']
})
export class ShowTeamsComponent implements OnInit {
  teams: TeamSummaryDto[] = [];

  constructor(private teamsService: TeamsService, private router: Router) {}

  ngOnInit(): void {
    this.teamsService.getAllTeams().subscribe((data) => {
      this.teams = data;
    });
  }

  viewTeamDetails(teamId: string): void {
    this.router.navigate(['/team-members', teamId]);
  }

  joinTeam(teamId: string): void {
    this.teamsService.joinTeam(teamId).subscribe(() => {
      const teamMember = this.teams.find(t => t.id === teamId);
      if (teamMember) teamMember.isMember = true;
    });
  }
}
