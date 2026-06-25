import { Routes } from '@angular/router';
import { TaskPageComponent } from './apps/tasks/task-page/task-page.component';
import { ProjectPageComponent } from './apps/projects/project-page/project-page.component';
import { ProjectDetailComponent } from './apps/projects/project-detail/project-detail.component';
import { UserProfileComponent } from './apps/users/user-profile/user-profile.component';
import { UserEditComponent } from './apps/users/user-edit/user-edit.component';
import { OAuthCallbackComponent } from '../auth/oauth-callback.component';
import { authGuard } from '../auth/auth.guard';
import { LoginComponent } from '../auth/login/login.component';
import { RegisterComponent } from '../auth/register/register.component';
import { UserListComponent } from './apps/users/user-list/user-list.component';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'oauth/callback', component: OAuthCallbackComponent },
  { path: 'user/edit', component: UserEditComponent, canActivate: [authGuard] },
  { path: 'user/profile', component: UserProfileComponent, canActivate: [authGuard] },
  { path: 'projects', component: ProjectPageComponent, canActivate: [authGuard] },
  { path: 'projects/:id', component: ProjectDetailComponent, canActivate: [authGuard] },
  { path: 'tasks', component: TaskPageComponent, canActivate: [authGuard] },
  { path: 'users', component: UserListComponent, canActivate: [authGuard] },
  { path: '', redirectTo: 'user/profile', pathMatch: 'full' }
];
