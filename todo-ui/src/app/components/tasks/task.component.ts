import { Component, OnInit, ViewChild } from '@angular/core';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { MatSort, Sort } from '@angular/material/sort';
import { DataService } from "../../services/dashboard/data.service";
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
  displayedColumns: string[] = ['title', 'description', 'assignedTo', 'actions'];
  sortField: string = 'title'; // Default sorting column
  sortDir: string = 'asc';      // Default sorting direction
  searchQuery: string = '';

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  constructor(
    private dataService: DataService,
    private teamService: TeamsService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.user = JSON.parse(sessionStorage.getItem('userdetails') || "{}");
    this.loadTeams(); // Load the teams the user belongs to
  }

  ngAfterViewInit(): void {
    // Bind the MatSort and MatPaginator to the data table
    this.sort.sortChange.subscribe((sortState: Sort) => this.announceSortChange(sortState));
    this.paginator.page.subscribe((pageEvent: PageEvent) => this.onPageChange(pageEvent));
  }

  loadTeams(): void {
    this.teamService.getAllTeams('MEMBER').subscribe((teams: any[]) => {
      this.teams = teams;
      if (this.teams.length > 0) {
        this.selectedTeam = this.teams[0].id; // Select the first team by default
        this.getData(); // Fetch initial data for the first team
      }
    });
  }

  getData(): void {
    if (!this.selectedTeam) return; // Ensure a team is selected

    // Fetch the data with the updated sorting and pagination values
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
    this.getData(); // Fetch the data again with updated sorting parameters
  }

  applyFilter(event: Event): void {
    const filterValue = (event.target as HTMLInputElement).value.trim().toLowerCase();
    this.searchQuery = filterValue;
    this.currentPage = 0; // Reset to the first page for a new filter
    this.getData();
  }

  onTeamChange(event: any): void {
    this.selectedTeam = event.value;
    this.currentPage = 0; // Reset to the first page for a new team selection
    this.getData();
  }

  deleteRow(task: Task): void {
    this.dataService.deleteTask(task.id).subscribe(() => {
      this.getData(); // Refresh data after deletion
    });
  }

  addRow(): void {
    this.router.navigate(['/tasks/add'], { queryParams: { mode: 'add' } });
  }
}
