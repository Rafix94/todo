import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { AuthKeyClockGuard } from './routeguards/auth.route';
import { HomeComponent } from './components/home/home.component';

import { TaskComponent } from './components/tasks/task.component';
import { TeamsComponent } from './components/teams/teams.component';
import { AddTeamComponent } from './components/add-team/add-team.component';
import { ShowTeamsComponent } from './components/show-teams/show-teams.component';
import { TeamMembersComponent } from "./components/team-members/team-members.component";
import { TaskDetailsComponent } from "./components/task-details/task-details.component";
import {RefinementComponent} from "./components/refinement/refinement.component";

const routes: Routes = [
  { path: '', redirectTo: '/home', pathMatch: 'full'},
  { path: 'home', component: HomeComponent},
  { path: 'dashboard', component: DashboardComponent, canActivate: [AuthKeyClockGuard],data: {}},
  { path: 'tasks', component: TaskComponent, canActivate: [AuthKeyClockGuard],data: {}},
  { path: 'teams', component: TeamsComponent, data: {}, canActivate: [AuthKeyClockGuard]},
  { path: 'add-team', component: AddTeamComponent, data: {}, canActivate: [AuthKeyClockGuard]},
  { path: 'show-teams', component: ShowTeamsComponent, data: {}, canActivate: [AuthKeyClockGuard]},
  { path: 'team-members/:id', component: TeamMembersComponent, data: {}, canActivate: [AuthKeyClockGuard]},
  { path: 'tasks/:id', component: TaskDetailsComponent, data: {}, canActivate: [AuthKeyClockGuard] },
  { path: 'refinement/:teamId/:role', component: RefinementComponent, canActivate: [AuthKeyClockGuard]}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
