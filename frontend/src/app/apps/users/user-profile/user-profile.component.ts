import {Component, OnInit} from '@angular/core';
import {User} from '../../../../models/user.model';
import {Router, RouterLink} from '@angular/router';
import {UserService} from '../../../../services/user.service';
import {Project} from '../../../../models/project.model';
import {Task} from '../../../../models/task.model';
import {AsyncPipe, DatePipe} from '@angular/common';
import {Observable} from 'rxjs';

@Component({
  selector: 'app-user-profile',
  standalone: true,
  imports: [DatePipe, AsyncPipe, RouterLink],
  templateUrl: 'user-profile.component.html'
})
export class UserProfileComponent implements OnInit {
  user$!: Observable<User>;

  constructor(
    private router: Router,
    private userService: UserService
  ) {}

  ngOnInit(): void {
    this.user$ = this.userService.getUser(1);
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

  getStatusColor(status?: string): { bg: string; text: string; icon: string } {
    switch (status?.toUpperCase()) {
      case 'DONE':
        return { bg: '#f0fdf4', text: '#15803d', icon: '#16a34a' };
      case 'IN_PROGRESS':
        return { bg: '#eff6ff', text: '#1d4ed8', icon: '#2563eb' };
      case 'REVIEW':
        return { bg: '#fdf4ff', text: '#7e22ce', icon: '#9333ea' };
      case 'BLOCKED':
        return { bg: '#fff1f2', text: '#be123c', icon: '#e11d48' };
      case 'OPEN':
      default:
        return { bg: '#fefce8', text: '#a16207', icon: '#ca8a04' };
    }
  }

  getStatusLabel(status?: string): string {
    switch (status?.toUpperCase()) {
      case 'DONE':        return 'Erledigt';
      case 'IN_PROGRESS': return 'In Bearbeitung';
      case 'REVIEW':      return 'Review';
      case 'BLOCKED':     return 'Blockiert';
      case 'OPEN':
      default:            return 'Offen';
    }
  }
}
