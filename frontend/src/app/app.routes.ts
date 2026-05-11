import { Routes } from '@angular/router';
import { TaskPageComponent } from './apps/tasks/task-page/task-page.component';
import { ProjectPageComponent } from './apps/projects/project-page/project-page.component';
import { ProjectDetailComponent } from './apps/projects/project-detail/project-detail.component';
import {UserProfileComponent} from './apps/users/user-profile/user-profile.component';
import {UserEditComponent} from './apps/users/user-edit/user-edit.component';

export const routes: Routes = [
  { path: 'user/1/edit', component: UserEditComponent },
  { path: 'user/1', component: UserProfileComponent },
  { path: 'projects', component: ProjectPageComponent },
  { path: 'projects/:id', component: ProjectDetailComponent },
  { path: 'tasks', component: TaskPageComponent },
  { path: '', redirectTo: 'user/1', pathMatch: 'full' }
];
