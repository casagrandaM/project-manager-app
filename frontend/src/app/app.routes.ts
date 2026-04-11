import { Routes } from '@angular/router';
import { TaskPageComponent } from './apps/tasks/task-page/task-page.component';

export const routes: Routes = [
  { path: 'tasks', component: TaskPageComponent },
  { path: '', redirectTo: 'tasks', pathMatch: 'full' }
];
