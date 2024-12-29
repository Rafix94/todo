import {Component, HostListener, OnDestroy, OnInit} from "@angular/core";
import {RefinementService} from "../../services/refinement.service";
import {DataService} from "../../services/data.service";
import {ActivatedRoute} from "@angular/router";
import {KeycloakService} from "keycloak-angular";

@Component({
  selector: 'app-refinement',
  templateUrl: './refinement.component.html',
  styleUrls: ['./refinement.component.css'],
})
export class RefinementComponent implements OnInit, OnDestroy {
  isAdmin: boolean = false;
  votingState: 'ACTIVE' | 'IDLE' | 'REVEALED' = 'IDLE';
  userVoted: boolean = false;
  selectedTask: number | null = null;
  selectedTaskTitle: string | null = null;
  selectedTaskDescription: string | null = null;
  loading: boolean = false;
  participants: { firstName: string; lastName: string; score: number | null; voted: boolean; userId: string }[] = [];
  tasks: { id: number; title: string; description: string }[] = [];
  votingValues: number[] = [1, 2, 3, 5, 8, 13, 21];
  averageScore: number | null = null; // Store average score
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

      this.refinementService.connect();
      this.refinementService.connected$.subscribe((connected) => {
        if (connected) {
          console.log('Subscribing to updates');
          this.refinementService.subscribeToSessionStateUpdates(this.teamId, (state) => {
            this.updateSessionState(state);
          });
          this.refinementService.subscribeToParticipantsStateUpdates(this.teamId, (state) => {
            this.updateParticipantState(state);
          });
          this.refinementService.subscribeToAdminUpdates(this.teamId, (state) => {
            this.updateAdminState(state);
          });
          this.refinementService.subscribeToTaskUpdates(this.teamId, (state) => {
            this.updateTaskState(state);
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

  updateTaskWeight(): void {
    if (this.averageScore !== null && this.selectedTask) {
      this.dataService.updateTaskWeight(this.selectedTask, this.averageScore).subscribe(() => {
        console.log('Task weight updated successfully.');
        this.averageScore = null;
      });
    }
  }

  submitVote(value: number): void {
    this.loading = true;
    this.userVoted = true;
    this.refinementService.emitVote(this.teamId, this.userId, value);
  }

  private updateSessionState(state: {
    votingState: 'ACTIVE' | 'IDLE' | 'REVEALED';
  }): void {
    console.log('Session State Updated:', state);

    this.votingState = state.votingState;
  }

  private updateTaskState(state: {
    taskTitle: string,
    taskDescription: string
  }): void {
    this.selectedTaskTitle = state.taskTitle;
    this.selectedTaskDescription= state.taskDescription;
  }

  private updateAdminState(adminId: string): void {
    console.log('Admin State Updated:', adminId);

    this.isAdmin = this.userId === adminId;

    if (this.isAdmin) {
      this.dataService.getAllTasks(this.teamId).subscribe((tasks) => {
        this.tasks = tasks;
      });
    }
  }

  private updateParticipantState(state: Record<string, { voted: boolean | null; score: number | null }>): void {
    console.log('Session State Updated:', state);

    this.loading = false;

    if (!state || Object.keys(state).length === 0) {
      console.warn('State is empty or undefined');
      this.participants = [];
      return;
    }

    this.participants = Object.entries(state).map(([key, value]) => {
      const userRegex = /UserDataDTO\[id=([^,]+), firstName=([^,]+), lastName=([^,\]]+)\]/;
      const match = userRegex.exec(key);

      if (match) {
        const [, userId, firstName, lastName] = match;
        return {
          userId,
          firstName: firstName.trim() || 'Unknown',
          lastName: lastName.trim() || 'Unknown',
          voted: !!value.voted,
          score: value.score,
        };
      }

      return {
        userId: 'Unknown',
        firstName: 'Unknown',
        lastName: 'Unknown',
        voted: !!value.voted,
        score: value.score,
      };
    });

    if (this.votingState === 'REVEALED') {
      const scores = this.participants.map((p) => p.score).filter((score) => score !== null) as number[];
      if (scores.length > 0) {
        const total = scores.reduce((acc, score) => acc + score, 0);
        this.averageScore = Math.round(total / scores.length);
      } else {
        this.averageScore = null;
      }
    }

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
