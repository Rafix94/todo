import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';

@Component({
  selector: 'app-table',
  templateUrl: './table.component.html',
  styleUrls: ['./table.component.css']
})
export class TableComponent implements OnInit {
  @Input() data: any[] = [];
  @Input() totalPages: number = 0;
  @Input() currentPage: number = 0;
  @Output() prevPage = new EventEmitter<void>();
  @Output() nextPage = new EventEmitter<void>();
  columnsToDisplay: string[] = ['id', 'title', 'description', 'dueDate', 'priority', 'status', 'category'];

  constructor() { }

  ngOnInit(): void {
  }
}
