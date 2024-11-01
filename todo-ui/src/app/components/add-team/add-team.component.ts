import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { TeamsService } from 'src/app/services/teams.service';
import { Team } from 'src/app/model/team.model';

@Component({
  selector: 'app-add-team',
  templateUrl: './add-team.component.html',
  styleUrls: ['./add-team.component.css']
})
export class AddTeamComponent {
  addTeamForm: FormGroup;

  constructor(
    private formBuilder: FormBuilder,
    private teamService: TeamsService,
    private router: Router
  ) {
    this.addTeamForm = this.formBuilder.group({
      name: ['', Validators.required],
      description: ['', Validators.required]
    });
  }

  onSubmit(): void {
    if (this.addTeamForm.valid) {
      const newTeam: Team = this.addTeamForm.value;
      this.teamService.createTeam(newTeam).subscribe(() => {
        this.router.navigate(['/teams']);
      });
    }
  }
}
