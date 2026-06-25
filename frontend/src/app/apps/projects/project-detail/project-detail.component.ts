import { Component, OnInit, ViewChild, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { TaskListComponent } from '../../tasks/task-list/task-list.component';
import { TaskFormComponent } from '../../tasks/task-form/task-form.component';
import { KanbanBoardComponent } from '../../tasks/kanban-board/kanban-board.component';
import { ProjectService } from '../../../../services/project.service';
import { TaskService } from '../../../../services/task.service';
import {Project} from '../../../../models/project.model';
import { Task } from '../../../../models/task.model';
import {ActivityEvent} from '../../../../models/activity-event.model';

/**
 * Detail page for a single project. It loads the project, its tasks and its
 * activity feed, computes task statistics (progress, status counts), switches
 * between the task list and kanban views, and hosts the task form together with
 * a shared confirmation/success/error dialog for task create, edit and delete.
 */
@Component({
  selector: 'app-project-detail',
  standalone: true,
  imports: [CommonModule, FormsModule, TaskListComponent, TaskFormComponent, KanbanBoardComponent],
  templateUrl: './project-detail.component.html',
  styleUrl: './project-detail.component.css'
})
export class ProjectDetailComponent implements OnInit {
  project: Project | null = null;
  projectId!: number;
  tasks: Task[] = [];
  activityEvents: ActivityEvent[] = [];
  showActivity = false;

  showList = true;
  showKanban = false;
  showForm = false;
  taskToEdit: Task | null = null;
  refreshCounter = 0;
  searchTerm = '';

  showDialog = false;
  dialogMessage = '';
  isConfirmDialog = false;
  isSuccessDialog = false;
  confirmCallback: (() => void) | null = null;

  @ViewChild(TaskListComponent) taskListComp!: TaskListComponent;
  @ViewChild(KanbanBoardComponent) kanbanComp!: KanbanBoardComponent;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private projectService: ProjectService,
    private taskService: TaskService,
    private cdr: ChangeDetectorRef
  ) {}

  /**
   * Reads the project ID from the route and loads the project on initialization.
   */
  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.projectId = +params['id'];
      this.loadProject();
    });
  }

  /**
   * Loads the project together with its task progress and activity feed.
   */
  loadProject(): void {
    this.projectService.getProjectById(this.projectId).subscribe({
      next: (p) => { this.project = p; this.cdr.detectChanges(); },
      error: (err) => console.error(err)
    });
    this.loadTaskProgress();
    this.loadActivity();
  }

  /**
   * Loads the project's activity feed from the backend.
   */
  loadActivity(): void {
    this.projectService.getProjectActivity(this.projectId).subscribe({
      next: (events) => { this.activityEvents = events; this.cdr.detectChanges(); },
      error: (err) => console.error(err)
    });
  }

  /**
   * Maps an activity event type to its icon identifier used in the template.
   * @param type The activity event type
   */
  getActivityIcon(type: string): string {
    if (type === 'TASK_CREATED') return 'created';
    if (type === 'STATUS_CHANGED') return 'status';
    return 'assigned';
  }

  /**
   * Formats an ISO timestamp into a localized (de-AT) date and time string.
   * @param timestamp The ISO timestamp
   */
  formatActivityTime(timestamp: string): string {
    if (!timestamp) return '';
    const d = new Date(timestamp);
    return d.toLocaleDateString('de-AT', { day: '2-digit', month: '2-digit', year: 'numeric' })
      + ', ' + d.toLocaleTimeString('de-AT', { hour: '2-digit', minute: '2-digit' });
  }

  /**
   * Loads the tasks belonging to the current project.
   */
  loadTaskProgress(): void {
    this.taskService.getTasks(this.projectId).subscribe({
      next: (tasks) => { this.tasks = tasks; this.cdr.detectChanges(); },
      error: (err) => console.error(err)
    });
  }

  /**
   * The percentage (0–100) of the project's tasks that are done.
   */
  get taskProgress(): number {
    if (this.tasks.length === 0) return 0;
    return Math.round((this.tasks.filter(t => t.status === 'Done').length / this.tasks.length) * 100);
  }

  /**
   * The number of tasks in the "Done" status.
   */
  get taskDoneCount(): number {
    return this.tasks.filter(t => t.status === 'Done').length;
  }

  /**
   * The number of tasks in the "To Do" status.
   */
  get taskToDoCount(): number {
    return this.tasks.filter(t => t.status === 'To Do').length;
  }

  /**
   * The number of tasks in the "In Progress" status.
   */
  get taskInProgressCount(): number {
    return this.tasks.filter(t => t.status === 'In Progress').length;
  }

  /**
   * Navigates back to the projects overview.
   */
  goBack(): void {
    this.router.navigate(['/projects']);
  }

  /**
   * Switches between the task list and kanban board views.
   * @param view The view to show
   */
  toggleView(view: 'list' | 'kanban'): void {
    this.showList = view === 'list';
    this.showKanban = view === 'kanban';
    this.showForm = false;
  }

  /**
   * Opens the task form in create mode.
   */
  openForm(): void {
    this.taskToEdit = null;
    this.showForm = true;
    this.showList = false;
    this.showKanban = false;
  }

  /**
   * Opens the task form in edit mode for the given task.
   * @param task The task to edit
   */
  openEditForm(task: Task): void {
    this.taskToEdit = task;
    this.showForm = true;
    this.showList = false;
    this.showKanban = false;
  }

  /**
   * Handles the task form's close event. When refreshing, reloads tasks,
   * progress and activity and shows a success message; otherwise asks the user
   * to confirm discarding changes.
   * @param refresh Whether the form was saved successfully
   */
  closeForm(refresh: boolean): void {
    if (!refresh) {
      this.showConfirm('Möchten Sie wirklich abbrechen? Ungespeicherte Änderungen gehen verloren.', () => {
        this.resetToListView();
      });
    } else {
      this.refreshCounter++;
      if (this.kanbanComp) this.kanbanComp.loadData();
      if (this.taskListComp) this.taskListComp.loadTasks();
      this.loadTaskProgress();
      this.loadActivity();
      const msg = this.taskToEdit ? 'Task erfolgreich aktualisiert!' : 'Task erfolgreich erstellt!';
      this.showSuccessAlert(msg);
      this.taskToEdit = null;
    }
  }

  /**
   * Surfaces messages emitted by the task form as success or error alerts.
   * @param event The message emitted by the form
   */
  handleFormMessage(event: { text: string; type: 'success' | 'error' }): void {
    if (event.type === 'error') {
      this.showErrorAlert(event.text);
    } else {
      this.showSuccessAlert(event.text);
    }
  }

  /**
   * Asks the user to confirm deletion and, if confirmed, deletes the task and
   * refreshes the views, progress and activity feed.
   * @param id The ID of the task to delete
   */
  requestDelete(id: number): void {
    this.showConfirm('Sind Sie sicher, dass Sie diesen Task löschen wollen?', () => {
      this.taskService.deleteTask(id).subscribe({
        next: () => {
          this.refreshCounter++;
          if (this.taskListComp) this.taskListComp.loadTasks();
          if (this.kanbanComp) this.kanbanComp.loadData();
          this.loadTaskProgress();
          this.loadActivity();
          this.showSuccessAlert('Task erfolgreich gelöscht.');
        },
        error: () => this.showErrorAlert('Fehler beim Löschen des Tasks.')
      });
    });
  }

  /**
   * Opens a confirmation dialog and stores the callback to run if confirmed.
   * @param message The confirmation message
   * @param callback The action to execute on confirmation
   */
  showConfirm(message: string, callback: () => void): void {
    this.dialogMessage = message;
    this.isConfirmDialog = true;
    this.isSuccessDialog = false;
    this.confirmCallback = callback;
    this.showDialog = true;
  }

  /**
   * Shows a transient success dialog that auto-dismisses after 2 seconds and
   * returns to the list view.
   * @param message The success message
   */
  showSuccessAlert(message: string): void {
    this.dialogMessage = message;
    this.isConfirmDialog = false;
    this.isSuccessDialog = true;
    this.showDialog = true;
    setTimeout(() => {
      this.showDialog = false;
      this.resetToListView();
      this.cdr.detectChanges();
    }, 2000);
  }

  /**
   * Shows an error alert dialog.
   * @param message The error message
   */
  showErrorAlert(message: string): void {
    this.dialogMessage = message;
    this.isConfirmDialog = true;
    this.isSuccessDialog = false;
    this.showDialog = true;
  }

  /**
   * Closes the active dialog and, for a confirmed confirmation dialog, runs the
   * stored callback.
   * @param result Whether the user confirmed the dialog
   */
  closeDialog(result: boolean): void {
    this.showDialog = false;
    if (this.isConfirmDialog && result && this.confirmCallback) {
      this.confirmCallback();
    }
    this.confirmCallback = null;
  }

  /**
   * Deterministically derives a CSS gradient for the project header from its ID.
   * @param projectId The project ID
   */
  getCardGradient(projectId: number): string {
    const gradients = [
      'linear-gradient(135deg, #38bdf8 0%, #0ea5e9 100%)',
      'linear-gradient(135deg, #60a5fa 0%, #3b82f6 100%)',
      'linear-gradient(135deg, #3b82f6 0%, #2563eb 100%)',
      'linear-gradient(135deg, #2563eb 0%, #1d4ed8 100%)',
      'linear-gradient(135deg, #1d4ed8 0%, #1e40af 100%)',
      'linear-gradient(135deg, #1e3a8a 0%, #172554 100%)',
    ];
    let h = projectId;
    h = Math.imul(h ^ (h >>> 16), 0x45d9f3b);
    h = Math.imul(h ^ (h >>> 16), 0x45d9f3b);
    h = h ^ (h >>> 16);
    return gradients[Math.abs(h) % gradients.length];
  }

  /**
   * Resets the detail page back to the task list view, hiding the form and
   * kanban board.
   */
  resetToListView(): void {
    this.showForm = false;
    this.showKanban = false;
    this.showList = true;
    this.taskToEdit = null;
  }
}
