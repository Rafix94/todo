import {Component, OnInit, OnDestroy, HostListener} from '@angular/core';
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
  participants: { mail: string; firstName: string; lastName: string; voted: boolean }[] = [];
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

      this.refinementService.connect();
      this.refinementService.connected$.subscribe((connected) => {
        if (connected) {
          console.log('Subscribing to updates');

          // Subscribe to participant updates
          this.refinementService.subscribeToParticipants((data) => {
            this.updateParticipantList(data);
          });

          // Subscribe to voting status updates
          this.refinementService.subscribeToVotingStatus((data) => {
            this.handleVotingStatusUpdate(data);
          });

          // Subscribe to task updates
          this.refinementService.subscribeToTaskUpdates((data) => {
            this.handleTaskUpdate(data);
          });
        }
      });

      // Join the session
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
    this.votingActive = true;
    this.userVoted = false;

    this.refinementService.emitStartVoting(this.teamId);
  }

  endVoting(): void {
    this.votingActive = false;
    this.userVoted = false;

    this.refinementService.emitStopVoting(this.teamId);
  }

  submitVote(value: number): void {
    this.userVoted = true;

    this.refinementService.emitVote(this.teamId, this.userId, value);
  }

  private updateParticipantList(data: { participants: { mail: string, firstName: string, lastName: string }[] }): void {
    this.participants = data.participants.map((p) => ({
      mail: p.mail || 'Unknown',
      firstName: p.firstName || 'Unknown',
      lastName: p.lastName || 'Unknown',
      voted: false,
    }));
  }

  async getUserId(): Promise<string> {
    const userProfile = await this.keycloakService.loadUserProfile();
    return userProfile.id || '';
  }

  private handleVotingStatusUpdate(data: { sessionId: number; teamId: string; active: boolean }): void {
    console.log('Voting Status Update:', data);
    this.votingActive = data.active;
  }

  private handleTaskUpdate(data: { taskTitle: string; taskDescription: string }): void {
    console.log('Task Update Received:', data);
    this.selectedTaskTitle = data.taskTitle;
    this.selectedTaskDescription = data.taskDescription;
  }

  @HostListener('window:beforeunload', ['$event'])
  handleBeforeUnload(event: BeforeUnloadEvent): void {
    this.refinementService.emitLeaveSession(this.teamId, this.userId);

    event.preventDefault();
    event.returnValue = '';
  }

}
