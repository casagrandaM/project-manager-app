import {Component, OnInit} from '@angular/core';
import {User} from '../../../../models/user.model';
import {Router, RouterLink} from '@angular/router';
import {UserService} from '../../../../services/user.service';
import {Project} from '../../../../models/project.model';
import {Task} from '../../../../models/task.model';
import {AsyncPipe, DatePipe} from '@angular/common';
import {Observable} from 'rxjs';
import {AuthService} from '../../../../auth/auth.service';

@Component({
  selector: 'app-user-profile',
  standalone: true,
  imports: [DatePipe, RouterLink],
  templateUrl: 'user-profile.component.html'
})
export class UserProfileComponent implements OnInit {
  user?: User;

  constructor(private router: Router, private userService: UserService) {}

  ngOnInit(): void {
    this.user = this.userService.getCurrentUser();
  }

  navigateToProject(project: Project): void {
    this.router.navigate(['/projects', project.id]);
  }

  navigateToTask(task: Task): void {
    this.router.navigate(['/tasks'], { queryParams: { projectId: task.projectId } });
  }

  onProjectHover(event: MouseEvent, isHover: boolean): void {
    const el = event.currentTarget as HTMLElement;
    el.style.background = isHover ? '#f8faff' : '';
    el.style.borderColor = isHover ? '#dbeafe' : 'transparent';
  }

  onTaskHover(event: MouseEvent, isHover: boolean): void {
    const el = event.currentTarget as HTMLElement;
    el.style.background = isHover ? '#f8fdf9' : '';
    el.style.borderColor = isHover ? '#bbf7d0' : 'transparent';
  }

  getStatusColor(status: string | undefined): string {
    if (status === 'To Do') return '#d9534f';
    if (status === 'In Progress') return '#f0ad4e';
    if (status === 'Done') return '#5cb85c';
    return '#333';
  }
}
