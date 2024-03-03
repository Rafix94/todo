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
  task: Task = new Task();
  mode: string = 'show';
  user: any;
  formErrors: any = {};

  constructor(private router: Router, private route: ActivatedRoute, private dataService: DataService, private cdr: ChangeDetectorRef) {
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
    // Add or update task based on mode
    if (this.mode === 'add') {
      // Reset formErrors object
      this.formErrors = {};

      // Call the service method to create a new task
      this.dataService.createTask(this.task, this.user.email)
        .subscribe(
          () => {
            // Task created successfully, navigate to the task list
            this.router.navigate(['/tasks']);
          },
          error => {
            this.handleServerError(error);
          }
        );
    } else if (this.mode === 'edit') {
      // Call the service method to update an existing task
      this.dataService.updateTask(this.task.id, this.task)
        .subscribe(
          () => {
            // Task updated successfully, navigate to the task list
            this.router.navigate(['/tasks']);
          },
          error => {
            this.handleServerError(error);
          }
        );
    }
  }

  private handleServerError(error: any): void {
    if (error.status === 400) {
      const errorResponse = error.error;
      for (const key in errorResponse) {
        if (errorResponse.hasOwnProperty(key)) {
          this.formErrors[key] = errorResponse[key];
        }
      }
    } else {
      console.error('An error occurred:', error);
    }
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

    this.user = JSON.parse(sessionStorage.getItem('userdetails') || "");

    this.task.priority = 'H'; // Default priority is 'High'
    this.task.status = 'T'; // Default status is 'In Progress'

    this.route.queryParams.subscribe(params => {
      this.mode = params['mode'] || 'show';
    });
    if (this.mode === 'edit' || this.mode === 'show') {
      this.getTaskDetails();
    }
  }
}
