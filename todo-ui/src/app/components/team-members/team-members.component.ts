import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { TeamsService } from 'src/app/services/teams.service';
import { Team } from 'src/app/model/team.model';

@Component({
  selector: 'app-team-members',
  templateUrl: './team-members.component.html',
  styleUrls: ['./team-members.component.css']
})
export class TeamMembersComponent implements OnInit {
  teams: Team | null = null;

  constructor(
    private route: ActivatedRoute,
    private teamService: TeamsService
  ) {}

  ngOnInit(): void {
    const teamId = this.route.snapshot.paramMap.get('id'); // Get the team ID from the route
    if (teamId) {
      this.teamService.getAllTeams().subscribe((teams) => {
        this.teams = teams.find(t => t.id === teamId) || null; // Find the team by ID
      });
    }
  }
}
