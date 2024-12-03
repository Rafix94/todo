import { Component, Inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  selector: 'app-add-task-dialog',
  templateUrl: './add-task-dialog.component.html',
  styleUrls: ['./add-task-dialog.component.css']
})
export class AddTaskDialogComponent {
  taskForm: FormGroup;

  constructor(
    private fb: FormBuilder,
    public dialogRef: MatDialogRef<AddTaskDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { teamId: string }
  ) {
    this.taskForm = this.fb.group({
      title: ['', Validators.required],
      description: ['', Validators.required],
      teamId: [data.teamId, Validators.required]  // Pass the teamId here
    });
  }

  onSubmit(): void {
    if (this.taskForm.valid) {
      const newTask = this.taskForm.value;
      this.dialogRef.close(newTask);
    }
  }

  onCancel(): void {
    this.dialogRef.close();
  }
}
