import { Component, ViewChild, ChangeDetectorRef, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { TaskListComponent } from '../task-list/task-list.component';
import { TaskFormComponent } from '../task-form/task-form.component';
import { KanbanBoardComponent } from '../kanban-board/kanban-board.component';
import { Task } from '../../../../models/task.model';
import { TaskService } from '../../../../services/task.service';

@Component({
  selector: 'app-task-page',
  standalone: true,
  imports: [CommonModule, FormsModule, TaskListComponent, TaskFormComponent, KanbanBoardComponent],
  templateUrl: './task-page.component.html'
})
export class TaskPageComponent implements OnInit {

  showList = true;
  showKanban = false;
  showForm = false;

  searchTerm: string = '';
  refreshCounter: number = 0;
  projectId: number | null = null;

  taskToEdit: Task | null = null;

  showDialog = false;
  dialogMessage = '';
  isConfirmDialog = false;
  isSuccessDialog = false;
  confirmCallback: (() => void) | null = null;

  @ViewChild(TaskListComponent) taskListComp!: TaskListComponent;
  @ViewChild(KanbanBoardComponent) kanbanComp!: KanbanBoardComponent;

  constructor(
    private taskService: TaskService,
    private cdr: ChangeDetectorRef,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      this.projectId = params['projectId'] ? +params['projectId'] : null;
    });
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
      this.showConfirm(
        'Möchten Sie wirklich abbrechen? Ungespeicherte Änderungen gehen verloren.',
        () => {
          this.resetToListView();
        }
      );
    } else {
      this.refreshCounter++;

      if (this.kanbanComp) this.kanbanComp.loadData();
      if (this.taskListComp) this.taskListComp.loadTasks();

      const msg = this.taskToEdit
        ? 'Task erfolgreich aktualisiert!'
        : 'Task erfolgreich erstellt!';

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

  showConfirm(message: string, callback: () => void) {
    this.dialogMessage = message;
    this.isConfirmDialog = true;
    this.isSuccessDialog = false;
    this.confirmCallback = callback;
    this.showDialog = true;
  }

  showSuccessAlert(message: string) {
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

  showErrorAlert(message: string) {
    this.dialogMessage = message;
    this.isConfirmDialog = true;
    this.isSuccessDialog = false;
    this.showDialog = true;
  }

  closeDialog(result: boolean) {
    this.showDialog = false;

    if (this.isConfirmDialog && result && this.confirmCallback) {
      this.confirmCallback();
    }

    this.confirmCallback = null;
  }

  resetToListView() {
    this.showForm = false;
    this.showKanban = false;
    this.showList = true;
    this.taskToEdit = null;
  }

  requestDelete(id: number) {
    this.showConfirm('Sind Sie sicher, dass Sie diesen Task löschen wollen?', () => {
      this.taskService.deleteTask(id).subscribe({
        next: () => {
          this.refreshCounter++;

          if (this.taskListComp) this.taskListComp.loadTasks();
          if (this.kanbanComp) this.kanbanComp.loadData();

          this.showSuccessAlert('Task erfolgreich gelöscht.');
        },
        error: (err) => {
          console.error('Fehler beim Löschen', err);
          this.showErrorAlert('Fehler beim Löschen des Tasks.');
        }
      });
    });
  }
}
