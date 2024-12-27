import { Component, OnInit, OnDestroy, HostListener } from '@angular/core';
import { RefinementService } from '../../services/refinement.service';
import { ActivatedRoute } from '@angular/router';
import { KeycloakService } from "keycloak-angular";
import {DataService} from "../../services/data.service";

@Component({
  selector: 'app-refinement',
  templateUrl: './refinement.component.html',
  styleUrls: ['./refinement.component.css'],
})
export class RefinementComponent implements OnInit, OnDestroy {
  isAdmin: boolean = false;
  votingState: 'ACTIVE' | 'IDLE' | 'REVEALED' = 'IDLE';
  userVoted: boolean = false;
  selectedTask: string | null = null;
  selectedTaskTitle: string | null = null;
  selectedTaskDescription: string | null = null;
  loading: boolean = false;
  participants: { firstName: string; lastName: string; score: number | null; voted: boolean; userId: string }[] = [];
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

      this.refinementService.connect();
      this.refinementService.connected$.subscribe((connected) => {
        if (connected) {
          console.log('Subscribing to updates');
          this.refinementService.subscribeToSessionStateUpdates(this.teamId, (state) => {
            this.updateSessionState(state);
          });
        }
      });

      this.refinementService.emitJoinSession(this.teamId, this.userId);
    });
  }

  ngOnDestroy(): void {
    this.refinementService.emitLeaveSession(this.teamId, this.userId);
    this.refinementService.disconnectSocket();
  }

  onTaskChange(): void {
    const selected = this.tasks.find((task) => task.id === this.selectedTask);
    if (selected) {
      this.selectedTaskTitle = selected.title;
      this.selectedTaskDescription = selected.description;
      this.refinementService.emitTaskUpdate(this.teamId, selected.title, selected.description);
    }
  }

  startVoting(): void {
    this.loading = true;
    this.refinementService.emitStartVoting(this.teamId);
  }

  endVoting(): void {
    this.loading = true;
    this.refinementService.emitStopVoting(this.teamId);
  }

  resetVoting(): void {
    this.loading = true;
    this.refinementService.emitResetVoting(this.teamId);
  }

  submitVote(value: number): void {
    this.loading = true;
    this.userVoted = true;
    this.refinementService.emitVote(this.teamId, this.userId, value);
  }

  private updateSessionState(state: {
    participantsVotes: Record<string, { voted: boolean | null; score: number | null }>;
    adminId: string;
    votingState: 'ACTIVE' | 'IDLE' | 'REVEALED';
    task: { title: string; description: string };
  }): void {
    console.log('Session State Updated:', state);

    this.loading = false;

    this.participants = Object.entries(state.participantsVotes).map(([key, value]) => {
      const userRegex = /UserDataDTO\[id=(.+), firstName=(.+), lastName=(.+)\]/;
      const match = userRegex.exec(key);

      if (match) {
        const [, userId, firstName, lastName] = match;
        return {
          userId,
          firstName,
          lastName,
          voted: !!value.voted,
          score: value.score,
        };
      }

      return {
        userId: 'Unknown',
        firstName: 'Unknown',
        lastName: 'Unknown',
        voted: false,
        score: null,
      };
    });

    this.isAdmin = this.userId === state.adminId;

    if (this.isAdmin) {
      this.dataService.getAllTasks(this.teamId).subscribe((tasks) => {
        this.tasks = tasks;
      });
    }

    this.votingState = state.votingState;
    this.selectedTaskTitle = state.task.title || 'No task selected';
    this.selectedTaskDescription = state.task.description || 'No description available';

    const loggedInUser = this.participants.find((p) => p.userId === this.userId);
    this.userVoted = loggedInUser?.voted || false;
  }

  async getUserId(): Promise<string> {
    const userProfile = await this.keycloakService.loadUserProfile();
    return userProfile.id || '';
  }

  @HostListener('window:beforeunload', ['$event'])
  handleBeforeUnload(event: BeforeUnloadEvent): void {
    this.refinementService.emitLeaveSession(this.teamId, this.userId);
    event.preventDefault();
    event.returnValue = '';
  }
}
