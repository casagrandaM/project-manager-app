import { Routes } from '@angular/router';
import { TaskPageComponent } from './apps/tasks/task-page/task-page.component';
import { ProjectPageComponent } from './apps/projects/project-page/project-page.component';
import { ProjectDetailComponent } from './apps/projects/project-detail/project-detail.component';

export const routes: Routes = [
  { path: 'projects', component: ProjectPageComponent },
  { path: 'projects/:id', component: ProjectDetailComponent },
  { path: 'tasks', component: TaskPageComponent },
  { path: '', redirectTo: 'tasks', pathMatch: 'full' }
];
