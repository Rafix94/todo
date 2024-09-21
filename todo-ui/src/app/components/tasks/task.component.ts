import { Component, OnInit } from '@angular/core';
import { DataService } from "../../services/dashboard/data.service";
import { Sort } from '@angular/material/sort';
import { Router } from '@angular/router';
import { Task } from "../../model/task.model";

@Component({
  selector: 'app-task',
  templateUrl: './task.component.html',
  styleUrls: ['./task.component.css']
})
export class TaskComponent implements OnInit {
  data: any[] = [];
  currentPage = 0;
  pageSize = 10;
  totalPages = 0;
  totalElements = 0;
  user: any;
  displayedColumns: string[] = ['No.', 'title', 'description', 'dueDate', 'priority', 'status', 'category', 'actions'];
  sortField: string = 'title';
  sortDir: string = 'asc';
  searchQuery: string = '';
  row: any;

  constructor(private dataService: DataService, private router: Router) { }

  ngOnInit(): void {
    // Fetch user details from session storage and initialize data
    this.user = JSON.parse(sessionStorage.getItem('userdetails') || "{}");
    console.log('User details:', this.user); // Log user details
    this.getData(); // Call to fetch initial data
  }

  getData(): void {
    this.dataService.getTasks(this.currentPage, this.pageSize, this.user.email, this.sortField, this.sortDir, this.searchQuery).subscribe((response: any) => {
      this.data = response.content;
      this.totalPages = response.totalPages;
      this.totalElements = response.totalElements;
    });
  }

  updateSize(pageSize: number) {
    this.pageSize = pageSize; // Update page size
    console.log('Page size updated to:', this.pageSize); // Log new page size
    this.getData(); // Fetch data again with new page size
  }

  announceSortChange(sortState: Sort) {
    this.sortDir = sortState.direction || 'asc'; // Set sort direction
    this.sortField = sortState.active || 'title'; // Set active sort field
    console.log('Sort changed:', this.sortField, this.sortDir); // Log sort changes
    this.getData(); // Fetch data with updated sort
  }

  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value; // Get filter value from input
    this.searchQuery = filterValue.trim().toLowerCase(); // Update search query
    console.log('Filter applied:', this.searchQuery); // Log filter value
    this.getData(); // Fetch data with updated search
  }

  showRow(task: Task): void {
    // Navigate to task detail view
    this.router.navigate(['/tasks', task.id], { state: { mode: 'show' } });
  }

  editRow(task: Task) {
    // Navigate to task edit view
    this.router.navigate(['/tasks', task.id], { queryParams: { mode: 'edit' } });
  }

  deleteRow(task: Task) {
    // Call service to delete task and refresh data
    this.dataService.deleteTask(task.id).subscribe(() => {
      console.log('Task deleted:', task.id); // Log deletion
      this.getData(); // Refresh data after deletion
    });
  }

  addRow() {
    // Navigate to add task view
    this.router.navigate(['/tasks/add'], { queryParams: { mode: 'add' } });
  }
}
