import { Component, OnInit, OnDestroy } from '@angular/core';
import { RefinementService } from '../../services/refinement.service';
import { DataService } from '../../services/data.service';
import { ActivatedRoute } from '@angular/router';
import {KeycloakService} from "keycloak-angular";

@Component({
  selector: 'app-refinement',
  templateUrl: './refinement.component.html',
  styleUrls: ['./refinement.component.css'],
})
export class RefinementComponent implements OnInit, OnDestroy {
  isAdmin: boolean = false;
  votingActive: boolean = false;
  userVoted: boolean = false;
  selectedTask: string | null = null;
  selectedTaskTitle: string | null = null;
  selectedTaskDescription: string | null = null;
  participants: { name: string; voted: boolean }[] = [];
  tasks: { id: string; title: string; description: string }[] = [];
  votingValues: number[] = [1, 2, 3, 5, 8, 13, 21];
  private teamId: string = '';
  private userId: string = '';

  constructor(
    private refinementService: RefinementService,
    private dataService: DataService,
    private route: ActivatedRoute,
    private keycloakService: KeycloakService
  ) {}
  async ngOnInit(): Promise<void> {
    this.userId = await this.getUserId();

    this.route.params.subscribe((params) => {
      this.teamId = params['teamId'];
      const role = params['role'];
      this.isAdmin = role === 'admin';

      if (this.isAdmin) {
        this.dataService.getAllTasks(this.teamId).subscribe((tasks) => {
          this.tasks = tasks;
        });
      }

      this.refinementService.connect(this.teamId);
      this.refinementService.connected$.subscribe((connected) => {
        if (connected) {
          console.log('Subscribing to participant updates');
          this.refinementService.subscribeToParticipants((data) => {
            this.updateParticipantList(data);
          });
        }
      });

      this.refinementService.emitJoinSession(this.teamId, this.userId);

    });
  }

  ngOnDestroy(): void {
    this.refinementService.disconnectSocket();
  }

  onTaskChange(): void {
    const selected = this.tasks.find((task) => task.id === this.selectedTask);
    if (selected) {
      this.selectedTaskTitle = selected.title;
      this.selectedTaskDescription = selected.description;

      this.refinementService.send('/app/session/task/update', {
        teamId: this.teamId,
        currentTaskId: this.selectedTask,
      });
    }
  }

  startVoting(): void {
    this.votingActive = true;
    this.userVoted = false;

    this.refinementService.send('/app/session/voting/start', { teamId: this.teamId });
  }

  endVoting(): void {
    this.votingActive = false;
    this.userVoted = false;

    this.refinementService.send('/app/session/voting/end', { teamId: this.teamId });
  }

  submitVote(value: number): void {
    this.userVoted = true;

    this.refinementService.send('/app/session/voting/vote', { teamId: this.teamId, vote: value });
  }

  private updateParticipantList(data: { participants: { mail: string }[] }): void {
    this.participants = data.participants.map((p) => ({
      name: p.mail || 'Unknown',
      voted: false,
    }));
  }

  async getUserId(): Promise<string> {
    const userProfile = await this.keycloakService.loadUserProfile();
    return userProfile.id || '';
  }
}
