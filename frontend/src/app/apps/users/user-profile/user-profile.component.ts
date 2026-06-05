import {ChangeDetectorRef, Component, OnInit} from '@angular/core';
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

  constructor(private router: Router, private authService: AuthService, private cdr: ChangeDetectorRef) {}

  ngOnInit(): void {
    this.authService.loadCurrentUser().subscribe({
      next: user => {
        this.user = user;
        this.cdr.detectChanges();
      },
      error: err => console.error('failed to load current user', err)
    });
  }

  navigateToProject(project: Project): void {
    this.router.navigate(['/projects', project.id]);
  }

  navigateToTask(task: Task): void {
    this.router.navigate(['/tasks'], { queryParams: { projectId: task.projectId } });
  }

  getOpenTaskCount(user: User): number {
    return (user.tasks ?? []).filter(
      t => t.status === 'OPEN' || t.status === 'IN_PROGRESS'
    ).length;
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
