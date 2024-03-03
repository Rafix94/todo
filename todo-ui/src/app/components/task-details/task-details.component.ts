import {Component, OnInit, ChangeDetectorRef, Input} from '@angular/core';
import { Router, ActivatedRoute} from '@angular/router';
import {DataService} from "../../services/dashboard/data.service";
import { Task } from "../../model/task.model";


@Component({
  selector: 'app-task-details',
  templateUrl: './task-details.component.html',
  styleUrls: ['./task-details.component.css']
})
export class TaskDetailsComponent implements OnInit {
  @Input() editMode: boolean | undefined;
  taskId: number | undefined;
  task: Task;

  constructor(private router: Router, private route: ActivatedRoute, private dataService: DataService, private cdr: ChangeDetectorRef) {
    this.task = new Task();
  }

  getTaskDetails(): void {
    // @ts-ignore
    this.dataService.getTaskDetails(this.taskId)
      .subscribe(task => {
        this.task = task;
        this.cdr.detectChanges();
      });
  }

  saveChanges(): void {
    // @ts-ignore
    this.dataService.updateTask(this.task.id, this.task)
      .subscribe(() => this.router.navigate(['/tasks']));
  }

  goBack(): void {
    this.router.navigate(['/tasks']);
  }

  priorityLabel(priority: string): string {
    switch(priority) {
      case 'H':
        return 'High';
      case 'M':
        return 'Medium';
      case 'L':
        return 'Low';
      default:
        return '';
    }
  }

  statusLabel(status: string): string {
    switch(status) {
      case 'P':
        return 'In Progress';
      case 'D':
        return 'Done';
      case 'T':
        return 'To Do';
      default:
        return '';
    }
  }

  ngOnInit(): void {
    // @ts-ignore
    this.taskId = +this.route.snapshot.paramMap.get('taskId');
    this.getTaskDetails();
    this.route.queryParams.subscribe(params => {
      this.editMode = params['editMode'] === 'true';
    });
  }
}
