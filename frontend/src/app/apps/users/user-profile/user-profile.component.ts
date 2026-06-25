import {ChangeDetectorRef, Component, OnInit} from '@angular/core';
import {User} from '../../../../models/user.model';
import {Router, RouterLink} from '@angular/router';
import {UserService} from '../../../../services/user.service';
import {Project} from '../../../../models/project.model';
import {Task} from '../../../../models/task.model';
import {AsyncPipe, DatePipe} from '@angular/common';
import {Observable} from 'rxjs';
import {AuthService} from '../../../../auth/auth.service';

/**
 * Component responsible for displaying the profile of the currently authenticated user.
 */
@Component({
  selector: 'app-user-profile',
  standalone: true,
  imports: [DatePipe, RouterLink],
  templateUrl: 'user-profile.component.html'
})
export class UserProfileComponent implements OnInit {
  user?: User;

  constructor(private router: Router, private authService: AuthService, private cdr: ChangeDetectorRef) {}


  /**
   * Loads the currently authenticated user and initializes the view.
   */
  ngOnInit(): void {
    this.authService.loadCurrentUser().subscribe({
      next: user => {
        this.user = user;
        this.cdr.detectChanges();
      },
      error: err => console.error('failed to load current user', err)
    });
  }

  /**
   * Navigates to a specific project view.
   *
   * @param project The project to navigate to
   */
  navigateToProject(project: Project): void {
    this.router.navigate(['/projects', project.id]);
  }


  /**
   * Navigates to the task list filtered by project.
   *
   * @param task The task containing the project reference
   */
  navigateToTask(task: Task): void {
    this.router.navigate(['/tasks'], { queryParams: { projectId: task.projectId } });
  }


  /**
   * Calculates the number of open or in-progress tasks for a user.
   *
   * @param user The user whose tasks are evaluated
   * @return The number of open tasks
   */
  getOpenTaskCount(user: User): number {
    return (user.tasks ?? []).filter(
      t => t.status === 'OPEN' || t.status === 'IN_PROGRESS'
    ).length;
  }

  /**
   * Applies hover styling to project elements.
   *
   * @param event   The mouse event
   * @param isHover Whether hover is active
   */
  onProjectHover(event: MouseEvent, isHover: boolean): void {
    const el = event.currentTarget as HTMLElement;
    el.style.background = isHover ? '#f8faff' : '';
    el.style.borderColor = isHover ? '#dbeafe' : 'transparent';
  }


  /**
   * Applies hover styling to task elements.
   *
   * @param event   The mouse event
   * @param isHover Whether hover is active
   */
  onTaskHover(event: MouseEvent, isHover: boolean): void {
    const el = event.currentTarget as HTMLElement;
    el.style.background = isHover ? '#f8fdf9' : '';
    el.style.borderColor = isHover ? '#bbf7d0' : 'transparent';
  }

  /**
   * Returns a color representing the status of a task.
   *
   * @param status The task status
   * @return The color code for the status
   */
  getStatusColor(status: string | undefined): string {
    if (status === 'To Do') return '#d9534f';
    if (status === 'In Progress') return '#f0ad4e';
    if (status === 'Done') return '#5cb85c';
    return '#333';
  }
}
