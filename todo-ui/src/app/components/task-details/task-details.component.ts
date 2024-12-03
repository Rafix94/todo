import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { FormBuilder, FormGroup } from '@angular/forms';
import { TaskDetailsService } from '../../services/task-details.service';

@Component({
  selector: 'app-task-details',
  templateUrl: './task-details.component.html',
  styleUrls: ['./task-details.component.css']
})
export class TaskDetailsComponent implements OnInit {
  taskId!: string;
  task: any;
  comments: any[] = [];
  files: any[] = [];
  commentForm: FormGroup;
  isLoading = true;

  constructor(
    private route: ActivatedRoute,
    private taskDetailsService: TaskDetailsService,
    private fb: FormBuilder
  ) {
    this.commentForm = this.fb.group({
      comment: [''],
      file: [null]
    });
  }

  ngOnInit(): void {
    this.taskId = this.route.snapshot.paramMap.get('id')!;
    this.loadTaskDetails();
  }

  loadTaskDetails(): void {
    this.taskDetailsService.getTaskDetails(this.taskId).subscribe({
      next: (response) => {
        this.task = response.task;
        this.comments = response.comments;
        this.files = response.files;
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error loading task details:', err);
        this.isLoading = false;
      }
    });
  }

  onFileSelect(event: any): void {
    const file = event.target.files[0];
    this.commentForm.patchValue({ file: file });
    this.commentForm.get('file')?.updateValueAndValidity();
  }

  submitComment(): void {
    const formData = new FormData();
    formData.append('comment', this.commentForm.value.comment);
    if (this.commentForm.value.file) {
      formData.append('file', this.commentForm.value.file);
    }

    this.taskDetailsService.addComment(this.taskId, formData).subscribe({
      next: () => {
        this.loadTaskDetails();
        this.commentForm.reset();
      },
      error: (err) => {
        console.error('Error submitting comment:', err);
      }
    });
  }
}
