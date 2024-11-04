import { Component, OnInit } from '@angular/core';
import { DataService } from "../../services/dashboard/data.service";
import { Sort } from '@angular/material/sort';
import { Router } from '@angular/router';
import { Task } from "../../model/task.model";
import { TeamsService } from 'src/app/services/teams.service';

@Component({
  selector: 'app-task',
  templateUrl: './task.component.html',
  styleUrls: ['./task.component.css']
})
export class TaskComponent implements OnInit {
  data: any[] = [];
  teams: any[] = [];
  selectedTeam: string | null = null;
  currentPage = 0;
  pageSize = 10;
  totalPages = 0;
  totalElements = 0;
  user: any;
  displayedColumns: string[] = ['No.', 'title', 'description', 'dueDate', 'priority', 'status', 'category', 'actions'];
  sortField: string = 'title';
  sortDir: string = 'asc';
  searchQuery: string = '';

  constructor(private dataService: DataService, private teamService: TeamsService, private router: Router) { }

  ngOnInit(): void {
    this.user = JSON.parse(sessionStorage.getItem('userdetails') || "{}");
    this.loadTeams(); // Load the teams the user belongs to
  }

  loadTeams(): void {
    this.teamService.getAllTeams( 'MEMBER' ).subscribe((teams: any[]) => {
      this.teams = teams;
      if (this.teams.length > 0) {
        this.selectedTeam = this.teams[0].id; // Select the first team by default
        this.getData(); // Fetch initial data for the first team
      }
    });
  }

  getData(): void {
    if (!this.selectedTeam) return; // Ensure a team is selected
    this.dataService.getTasks(this.currentPage, this.pageSize, this.user.email, this.selectedTeam, this.sortField, this.sortDir, this.searchQuery)
      .subscribe((response: any) => {
        this.data = response.content;
        this.totalPages = response.totalPages;
        this.totalElements = response.totalElements;
      });
  }

  updateSize(pageSize: number) {
    this.pageSize = pageSize;
    this.getData();
  }

  announceSortChange(sortState: Sort) {
    this.sortDir = sortState.direction || 'asc';
    this.sortField = sortState.active || 'title';
    this.getData();
  }

  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.searchQuery = filterValue.trim().toLowerCase();
    this.getData();
  }

  onTeamChange(event: any) {
    this.selectedTeam = event.value;
    this.currentPage = 0; // Reset to the first page for new team selection
    this.getData();
  }

  showRow(task: Task): void {
    this.router.navigate(['/tasks', task.id], { state: { mode: 'show' } });
  }

  editRow(task: Task) {
    this.router.navigate(['/tasks', task.id], { queryParams: { mode: 'edit' } });
  }

  deleteRow(task: Task) {
    this.dataService.deleteTask(task.id).subscribe(() => {
      this.getData();
    });
  }

  addRow() {
    this.router.navigate(['/tasks/add'], { queryParams: { mode: 'add' } });
  }
}
