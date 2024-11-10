import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ShowTeamsComponent } from './show-teams.component';

describe('ShowTeamsComponent', () => {
  let component: ShowTeamsComponent;
  let fixture: ComponentFixture<ShowTeamsComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ShowTeamsComponent]
    });
    fixture = TestBed.createComponent(ShowTeamsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
