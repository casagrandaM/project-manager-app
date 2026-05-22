import { Component, OnInit, ViewChild, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { TaskListComponent } from '../../tasks/task-list/task-list.component';
import { TaskFormComponent } from '../../tasks/task-form/task-form.component';
import { KanbanBoardComponent } from '../../tasks/kanban-board/kanban-board.component';
import { ProjectService } from '../../../../services/project.service';
import { TaskService } from '../../../../services/task.service';
import { Project } from '../../../../models/project.model';
import { Task } from '../../../../models/task.model';
import { ActivityEvent } from '../../../../models/activity-event.model';

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

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.projectId = +params['id'];
      this.loadProject();
    });
  }

  loadProject(): void {
    this.projectService.getProjectById(this.projectId).subscribe({
      next: (p) => { this.project = p; this.cdr.detectChanges(); },
      error: (err) => console.error(err)
    });
    this.loadTaskProgress();
    this.loadActivity();
  }

  loadActivity(): void {
    this.projectService.getProjectActivity(this.projectId).subscribe({
      next: (events) => { this.activityEvents = events; this.cdr.detectChanges(); },
      error: (err) => console.error(err)
    });
  }

  getActivityIcon(type: string): string {
    if (type === 'TASK_CREATED') return 'created';
    if (type === 'STATUS_CHANGED') return 'status';
    return 'assigned';
  }

  formatActivityTime(timestamp: string): string {
    if (!timestamp) return '';
    const d = new Date(timestamp);
    return d.toLocaleDateString('de-AT', { day: '2-digit', month: '2-digit', year: 'numeric' })
      + ', ' + d.toLocaleTimeString('de-AT', { hour: '2-digit', minute: '2-digit' });
  }

  loadTaskProgress(): void {
    this.taskService.getTasks(this.projectId).subscribe({
      next: (tasks) => { this.tasks = tasks; this.cdr.detectChanges(); },
      error: (err) => console.error(err)
    });
  }

  get taskProgress(): number {
    if (this.tasks.length === 0) return 0;
    return Math.round((this.tasks.filter(t => t.status === 'Done').length / this.tasks.length) * 100);
  }

  get taskDoneCount(): number {
    return this.tasks.filter(t => t.status === 'Done').length;
  }

  get taskToDoCount(): number {
    return this.tasks.filter(t => t.status === 'To Do').length;
  }

  get taskInProgressCount(): number {
    return this.tasks.filter(t => t.status === 'In Progress').length;
  }

  goBack(): void {
    this.router.navigate(['/projects']);
  }

  toggleView(view: 'list' | 'kanban'): void {
    this.showList = view === 'list';
    this.showKanban = view === 'kanban';
    this.showForm = false;
  }

  openForm(): void {
    this.taskToEdit = null;
    this.showForm = true;
    this.showList = false;
    this.showKanban = false;
  }

  openEditForm(task: Task): void {
    this.taskToEdit = task;
    this.showForm = true;
    this.showList = false;
    this.showKanban = false;
  }

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

  handleFormMessage(event: { text: string; type: 'success' | 'error' }): void {
    if (event.type === 'error') {
      this.showErrorAlert(event.text);
    } else {
      this.showSuccessAlert(event.text);
    }
  }

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

  showConfirm(message: string, callback: () => void): void {
    this.dialogMessage = message;
    this.isConfirmDialog = true;
    this.isSuccessDialog = false;
    this.confirmCallback = callback;
    this.showDialog = true;
  }

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

  showErrorAlert(message: string): void {
    this.dialogMessage = message;
    this.isConfirmDialog = true;
    this.isSuccessDialog = false;
    this.showDialog = true;
  }

  closeDialog(result: boolean): void {
    this.showDialog = false;
    if (this.isConfirmDialog && result && this.confirmCallback) {
      this.confirmCallback();
    }
    this.confirmCallback = null;
  }

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

  resetToListView(): void {
    this.showForm = false;
    this.showKanban = false;
    this.showList = true;
    this.taskToEdit = null;
  }
}
