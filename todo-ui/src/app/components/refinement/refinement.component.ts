import { Component, OnInit, OnDestroy } from '@angular/core';
import { RefinementService } from '../../services/refinement.service';
import {DataService} from "../../services/data.service";
import {ActivatedRoute} from "@angular/router";

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

  constructor(private refinementService: RefinementService, private dataService: DataService, private route: ActivatedRoute,) {}

  ngOnInit(): void {
    this.route.params.subscribe((params) => {
      this.teamId = params['teamId'];
      const role = params['role'];
      this.isAdmin = role === 'admin';

      if (this.isAdmin) {
        this.dataService.getAllTasks(this.teamId).subscribe((tasks) => {
          this.tasks = tasks;
        });
      }

      // Connect to WebSocket
      this.refinementService.connect(this.teamId);

      // Handle session updates via WebSocket
      this.refinementService.onSessionUpdate((data) => {
        this.updateSessionState(data);
      });
    });
  }


  ngOnDestroy(): void {
    this.refinementService.disconnect();
  }

  onTaskChange(): void {
    const selected = this.tasks.find((task) => task.id === this.selectedTask);
    if (selected) {
      this.selectedTaskTitle = selected.title;
      this.selectedTaskDescription = selected.description;

      this.refinementService.emitTaskUpdate(this.teamId, this.selectedTask);
    }
  }

  startVoting(): void {
    this.votingActive = true;
    this.userVoted = false;
    if (this.teamId) {
      this.refinementService.emitStartVoting(this.teamId);
    }
  }

  endVoting(): void {
    this.votingActive = false;
    this.userVoted = false;
    if (this.teamId) {
      this.refinementService.emitEndVoting(this.teamId);
    }
  }

  submitVote(value: number): void {
    this.userVoted = true;
    if (this.teamId) {
      this.refinementService.emitVote(this.teamId, value);
    }
  }

  updateSessionState(data: { participants: any[]; votingActive: boolean; currentTaskId?: string }): void {
    this.participants = data.participants;
    this.votingActive = data.votingActive;

    if (data.currentTaskId) {
      // Update selected task based on WebSocket update
      this.selectedTask = data.currentTaskId;
      const task = this.tasks.find((t) => t.id === this.selectedTask);
      if (task) {
        this.selectedTaskTitle = task.title;
        this.selectedTaskDescription = task.description;
      }
    }
  }
}
