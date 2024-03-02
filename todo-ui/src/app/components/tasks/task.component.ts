// task.component.ts

import { Component, OnInit } from '@angular/core';
import {DataService} from "../../services/dashboard/data.service";

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
  user: any;
  displayedColumns: string[] = ['No.', 'title', 'description', 'dueDate', 'priority', 'status', 'category'];

  constructor(private dataService: DataService) { }

  ngOnInit(): void {
    this.user = JSON.parse(sessionStorage.getItem('userdetails') || "");
    this.getData();
  }

  getData(): void {
    this.dataService.getData(this.currentPage, this.pageSize, this.user.email).subscribe((response: any) => {
      this.data = response.content;
      this.totalPages = response.totalPages;
    });
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
}
