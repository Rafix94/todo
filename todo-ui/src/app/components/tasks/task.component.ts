import { Component, OnInit, ViewChild } from '@angular/core';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { MatSort, Sort } from '@angular/material/sort';
import { DataService } from "../../services/dashboard/data.service";
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

  ngAfterViewInit(): void {
    this.sort.sortChange.subscribe((sortState: Sort) => this.announceSortChange(sortState));
    this.paginator.page.subscribe((pageEvent: PageEvent) => this.onPageChange(pageEvent));
  }

  loadTeams(): void {
    this.teamService.getAllTeams('MEMBER').subscribe((teams: any[]) => {
      this.teams = teams;
      if (this.teams.length > 0) {
        this.selectedTeam = this.teams[0].id;
        this.getData();
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
  }

  announceSortChange(sortState: Sort): void {
    this.sortDir = sortState.direction || 'asc';
    this.sortField = sortState.active || 'title';
    this.getData();
  }

  applyFilter(event: Event): void {
    const filterValue = (event.target as HTMLInputElement).value.trim().toLowerCase();
    this.searchQuery = filterValue;
    this.currentPage = 0;
    this.getData();
  }

  onTeamChange(event: any): void {
    this.selectedTeam = event.value;
    this.currentPage = 0;
    this.getData();
    this.checkSessionStatus(); // Check session status for the selected team
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
    const assignedTo = this.user.id; // Assign to the current user’s ID
    this.dataService.assignTask(task.id).subscribe(() => {
      this.getData(); // Refresh the task list after assignment
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
      this.sessionActive = true;
      this.dataService.emitCreateSession(this.selectedTeam);
      this.router.navigate(['/refinement', { sessionId: this.currentSession, role: 'admin' }]);
    });
  }

  joinSession() {
    this.router.navigate(['/refinement', { sessionId: this.currentSession, role: 'participant' }]);
  }
}
