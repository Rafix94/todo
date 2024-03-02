// task.component.ts

import { Component, OnInit } from '@angular/core';
import {DataService} from "../../services/dashboard/data.service";
import { Sort} from '@angular/material/sort';
import { Router } from '@angular/router';

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
  displayedColumns: string[] = ['No.', 'title', 'description', 'dueDate', 'priority', 'status', 'category'];
  sortField: string = 'title';
  sortDir: string = 'asc';
  searchQuery: string = '';
  row: any;

  constructor(private dataService: DataService, private router: Router) { }

  ngOnInit(): void {
    this.user = JSON.parse(sessionStorage.getItem('userdetails') || "");
    this.getData();
  }

  getData(): void {
    this.dataService.getTasks(this.currentPage, this.pageSize, this.user.email, this.sortField, this.sortDir, this.searchQuery).subscribe((response: any) => {
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

  search(query: string) {
    this.searchQuery = query;
    this.getData();
  }

  getRowDetails(task: any): void {
    this.router.navigate(['/tasks', task.id]);
  }

}
