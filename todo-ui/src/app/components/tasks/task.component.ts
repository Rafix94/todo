// task.component.ts

import { Component, OnInit } from '@angular/core';
import {DataService} from "../../services/dashboard/data.service";
import { Sort} from '@angular/material/sort';

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

  constructor(private dataService: DataService) { }

  ngOnInit(): void {
    this.user = JSON.parse(sessionStorage.getItem('userdetails') || "");
    this.getData();
  }

  getData(): void {
    this.dataService.getData(this.currentPage, this.pageSize, this.user.email, this.sortField, this.sortDir, this.searchQuery).subscribe((response: any) => {
      this.data = response.content;
      this.totalPages = response.totalPages;
      this.totalElements = response.totalElements;
    });
  }
  updateSize(pageSize: number) {
    this.pageSize = pageSize;
    this.getData();
  }

  nextPage(): void {
    if (this.currentPage < this.totalPages - 1) {
      this.currentPage++;
      this.getData();
    }
  }

  prevPage(): void {
    if (this.currentPage > 0) {
      this.currentPage--;
      this.getData();
    }
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

}
