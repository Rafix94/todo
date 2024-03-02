import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TaskDetailsComponent } from './task-details.component';

describe('TaskDetailsModificationComponent', () => {
  let component: TaskDetailsComponent;
  let fixture: ComponentFixture<TaskDetailsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ TaskDetailsComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TaskDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
