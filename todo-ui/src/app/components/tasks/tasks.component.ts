import { Component, OnInit } from '@angular/core';
import { Tasks } from 'src/app/model/tasks.model';
import { User } from 'src/app/model/user.model';
import { DashboardService } from '../../services/dashboard/dashboard.service';

@Component({
  selector: 'app-tasks',
  templateUrl: './tasks.component.html',
  styleUrls: ['./tasks.component.css']
})
export class TasksComponent implements OnInit {

  user = new User();
  tasks = new Array();
  currOutstandingBalance: number = 0;

  constructor(private dashboardService: DashboardService) { }

  ngOnInit(): void {
    this.user = JSON.parse(sessionStorage.getItem('userdetails') || "");
    if(this.user){
      this.dashboardService.getTasksDetails(this.user.email).subscribe(
        responseData => {
        this.tasks = <any> responseData.body;
        this.tasks.forEach(function (this: TasksComponent, loan: Tasks) {
          this.currOutstandingBalance = this.currOutstandingBalance+loan.outstandingAmount;
        }.bind(this));
        });
    }
  }
}
