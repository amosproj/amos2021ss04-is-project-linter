import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { RepositoryListComponent } from './repository-list/repository-list.component';
import { RepositoryDetailsComponent } from './repository-details/repository-details.component';

const routes: Routes = [
  {
    path: 'list',
    component: RepositoryListComponent,
    data: { title: 'List of Teams' }
  },
  {
    path: 'details/:id',
    component: RepositoryDetailsComponent,
    data: { title: 'Team Details' }
  },
  { path: '',
    redirectTo: '/list',
    pathMatch: 'full'
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
