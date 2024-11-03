import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { TeamsService } from 'src/app/services/teams.service';
import {TeamDetailsDto} from "../../model/teamDetailsDto";

@Component({
  selector: 'app-team-members',
  templateUrl: './team-members.component.html',
  styleUrls: ['./team-members.component.css']
})
export class TeamMembersComponent implements OnInit {
  teams: TeamDetailsDto | null = null;

  constructor(
    private route: ActivatedRoute,
    private teamService: TeamsService
  ) {}

  ngOnInit(): void {
    const teamId = this.route.snapshot.paramMap.get('id');
    if (teamId) {
      this.teamService.getTeamDetails(teamId).subscribe((team) => {
        this.teams = team || null;
      });
    }
  }
}
