import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ProjectsTabComponent } from './projects-tab/projects-tab.component';
import { StatisticsTabComponent } from './statistics-tab/statistics-tab.component';
import { StatusTabComponent } from './status-tab/status-tab.component';

const routes: Routes = [
  { path: 'projects', component: ProjectsTabComponent },
  { path: 'statistics', component: StatisticsTabComponent },
  { path: 'status', component: StatusTabComponent },
  { path: '**', redirectTo: 'projects' },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
export const routingComponents = [
  ProjectsTabComponent,
  StatisticsTabComponent,
  StatusTabComponent,
];
