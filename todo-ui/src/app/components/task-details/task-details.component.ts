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
  isSubmittingComment = false;
  isAnalyzingFile: { [key: number]: boolean } = {};

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
        this.task = response;
        this.comments = response.commentDtos || [];
        this.files = this.extractFilesFromComments(response.commentDtos || []);
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error loading task details:', err);
        this.isLoading = false;
      }
    });
  }

  extractFilesFromComments(comments: any[]): any[] {
    return comments
      .filter((comment) => comment.fileDto)
      .map((comment) => comment.fileDto);
  }

  onFileSelect(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input?.files?.[0];
    if (file) {
      this.commentForm.patchValue({ file });
      this.commentForm.get('file')?.updateValueAndValidity();
    }
  }

  submitComment(): void {
    this.isSubmittingComment = true;
    const formData = new FormData();
    formData.append('comment', this.commentForm.value.comment || '');
    if (this.commentForm.value.file) {
      formData.append('file', this.commentForm.value.file);
    }

    this.taskDetailsService.addComment(this.taskId, formData).subscribe({
      next: () => {
        this.loadTaskDetails();
        this.commentForm.reset({ comment: '', file: null });
        this.isSubmittingComment = false;
      },
      error: (err) => {
        console.error('Error submitting comment:', err);
        this.isSubmittingComment = false;
      }
    });
  }

  analyzeFile(fileId: number): void {
    this.isAnalyzingFile[fileId] = true;
    this.taskDetailsService.analyzeFile(fileId).subscribe({
      next: (response) => {
        const fileIndex = this.files.findIndex((file) => file.id === fileId);
        if (fileIndex >= 0) {
          this.files[fileIndex].analyzedFileUrl = response.analyzedFileUrl;
        }
        this.isAnalyzingFile[fileId] = false;
      },
      error: (err) => {
        console.error(`Error analyzing file (ID: ${fileId}):`, err);
        this.isAnalyzingFile[fileId] = false;
      }
    });
  }
}
