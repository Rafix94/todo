import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { AuthKeyClockGuard } from './routeguards/auth.route';
import { HomeComponent } from './components/home/home.component';

import { TaskComponent } from './components/tasks/task.component';
import { TaskDetailsComponent } from "./components/task-details/task-details.component";
import { RegistrationComponent } from "./components/registration/registration.component";
import { TeamsComponent } from './components/teams/teams.component';
import { AddTeamComponent } from './components/add-team/add-team.component';
import { ShowTeamsComponent } from './components/show-teams/show-teams.component';
import {TeamMembersComponent} from "./components/team-members/team-members.component";

const routes: Routes = [
  { path: '', redirectTo: '/home', pathMatch: 'full'},
  { path: 'home', component: HomeComponent},
  { path: 'dashboard', component: DashboardComponent, canActivate: [AuthKeyClockGuard],data: {}},
  { path: 'tasks', component: TaskComponent, canActivate: [AuthKeyClockGuard],data: {}},
  { path: 'tasks/:taskId', component: TaskDetailsComponent,  data: {}, canActivate: [AuthKeyClockGuard]},
  { path: 'tasks/add', component: TaskDetailsComponent,  data: {}, canActivate: [AuthKeyClockGuard]},
  { path: 'teams', component: TeamsComponent },
  { path: 'add-team', component: AddTeamComponent },
  { path: 'show-teams', component: ShowTeamsComponent },
  { path: 'team-members/:id', component: TeamMembersComponent },
  { path: 'register', component: RegistrationComponent,  data: {}}

];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
