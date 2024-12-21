import { Component, OnInit, ViewChild } from '@angular/core';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { DataService } from "../../services/data.service";
import { Router } from '@angular/router';
import { Task } from "../../model/task.model";
import { TeamsService } from 'src/app/services/teams.service';
import { MatDialog } from "@angular/material/dialog";
import { AddTaskDialogComponent } from "../add-task-dialog/add-task-dialog.component";

@Component({
  selector: 'app-task',
  templateUrl: './task.component.html',
  styleUrls: ['./task.component.css']
})
export class TaskComponent implements OnInit {
  data: any[] = [];
  teams: any[] = [];
  selectedTeam: string = '';
  currentPage = 0;
  pageSize = 10;
  totalPages = 0;
  totalElements = 0;
  user: any;
  displayedColumns: string[] = ['title', 'description', 'assignedTo', 'createdBy', 'actions'];
  sortField: string = 'title';
  sortDir: string = 'asc';
  searchQuery: string = '';
  sessionActive = false;
  currentSession: string = '';

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  constructor(
    private dataService: DataService,
    private teamService: TeamsService,
    private router: Router,
    private dialog: MatDialog
  ) { }

  ngOnInit(): void {
    this.user = JSON.parse(sessionStorage.getItem('userdetails') || "{}");
    this.loadTeams();
  }

  loadTeams(): void {
    this.teamService.getAllTeams('MEMBER').subscribe((teams: any[]) => {
      this.teams = teams;
      if (this.teams.length > 0) {
        this.selectedTeam = this.teams[0].id;
        this.getData();
        this.checkSessionStatus();
      }
    });
  }

  getData(): void {
    if (!this.selectedTeam) return;

    this.dataService.getTasks(
      this.currentPage,
      this.pageSize,
      this.selectedTeam,
      this.sortField,
      this.sortDir,
      this.searchQuery
    ).subscribe((response: any) => {
      this.data = response.content;
      this.totalPages = response.totalPages;
      this.totalElements = response.totalElements;
    });
  }

  onPageChange(event: PageEvent): void {
    this.pageSize = event.pageSize;
    this.currentPage = event.pageIndex;
    this.getData();
    this.checkSessionStatus();
  }


  onTeamChange(event: any): void {
    this.selectedTeam = event.value;
    this.currentPage = 0;
    this.getData();
    this.checkSessionStatus();
  }

  deleteRow(task: Task): void {
    this.dataService.deleteTask(task.id).subscribe(() => {
      this.getData();
    });
  }

  openAddTaskDialog(): void {
    const dialogRef = this.dialog.open(AddTaskDialogComponent, {
      width: '400px',
      data: { teamId: this.selectedTeam }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.addTask(result);
      }
    });
  }

  addTask(newTask: Task): void {
    this.dataService.createTask(newTask)
      .subscribe(() => {
        this.getData();
      });
  }

  assignTask(task: Task): void {
    this.dataService.assignTask(task.id).subscribe(() => {
      this.getData();
    });
  }

  openTaskDetails(taskId: string): void {
    this.router.navigate(['/tasks', taskId]);
  }

  checkSessionStatus() {
    this.dataService.checkSessionStatus(this.selectedTeam).subscribe((response) => {
      this.sessionActive = response.active;
      this.currentSession = response.sessionId || '';
    });
  }

  toggleSession() {
    if (!this.sessionActive) {
      this.createSession();
    } else {
      this.joinSession();
    }
  }

  createSession() {
    this.dataService.createSession(this.selectedTeam).subscribe((response) => {
      this.currentSession = response.sessionId;
      this.router.navigate(['/refinement', this.selectedTeam, 'admin']);
    });
  }

  joinSession() {
    this.router.navigate(['/refinement', this.selectedTeam, 'participant']);
  }
}
