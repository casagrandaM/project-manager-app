import { Component, ViewChild, ChangeDetectorRef, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { TaskListComponent } from '../task-list/task-list.component';
import { TaskFormComponent } from '../task-form/task-form.component';
import { KanbanBoardComponent } from '../kanban-board/kanban-board.component';
import { Task } from '../../../../models/task.model';
import { TaskService } from '../../../../services/task.service';

/**
 * Main task management page. It coordinates the task list, kanban board, and
 * task form, switches between the different views, handles task creation,
 * editing and deletion, and manages shared confirmation, success, and error
 * dialogs.
 */
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

  /**
   * Reads the optional project ID from the query parameters to filter the
   * displayed tasks.
   */
  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      this.projectId = params['projectId'] ? +params['projectId'] : null;
    });
  }

  /**
   * Switches between the task list and kanban board views.
   * @param view The view to display
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
   * Opens the task form in edit mode for the selected task.
   * @param task The task to edit
   */
  openEditForm(task: Task): void {
    this.taskToEdit = task;
    this.showForm = true;
    this.showList = false;
    this.showKanban = false;
  }

  /**
   * Handles the task form's close event. When changes were saved, refreshes the
   * task views and shows a success message. Otherwise, asks the user to confirm
   * discarding any unsaved changes.
   * @param refresh Whether the form was saved successfully
   */
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

      this.taskToEdit = null;

      // Zuerst zurück zur Liste, dann Dialog anzeigen
      this.resetToListView();
      this.showSuccessAlert(msg);
    }
  }

  /**
   * Displays success or error messages emitted by the task form.
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
   * Opens a confirmation dialog and stores the callback to execute if the user
   * confirms the action.
   * @param message The confirmation message
   * @param callback The action to execute on confirmation
   */
  showConfirm(message: string, callback: () => void) {
    this.dialogMessage = message;
    this.isConfirmDialog = true;
    this.isSuccessDialog = false;
    this.confirmCallback = callback;
    this.showDialog = true;
  }

  /**
   * Displays a transient success dialog that automatically closes after two
   * seconds.
   * @param message The success message
   */
  showSuccessAlert(message: string) {
    this.dialogMessage = message;
    this.isConfirmDialog = false;
    this.isSuccessDialog = true;
    this.showDialog = true;

    setTimeout(() => {
      this.showDialog = false;
      this.cdr.detectChanges();
    }, 2000);
  }

  /**
   * Displays an error dialog.
   * @param message The error message
   */
  showErrorAlert(message: string) {
    this.dialogMessage = message;
    this.isConfirmDialog = true;
    this.isSuccessDialog = false;
    this.showDialog = true;
  }

  /**
   * Closes the active dialog and executes the stored callback if the user
   * confirmed a confirmation dialog.
   * @param result Whether the user confirmed the dialog
   */
  closeDialog(result: boolean) {
    this.showDialog = false;

    if (this.isConfirmDialog && result && this.confirmCallback) {
      this.confirmCallback();
    }

    this.confirmCallback = null;
  }

  /**
   * Resets the page to the task list view, hiding the form and kanban board.
   */
  resetToListView() {
    this.showForm = false;
    this.showKanban = false;
    this.showList = true;
    this.taskToEdit = null;
  }

  /**
   * Asks the user to confirm deletion and, if confirmed, deletes the selected
   * task and refreshes the task list and kanban board.
   * @param id The ID of the task to delete
   */
  requestDelete(id: number) {
    this.showConfirm('Sind Sie sicher, dass Sie diesen Task löschen wollen?', () => {
      this.taskService.deleteTask(id).subscribe({
        next: () => {
          this.refreshCounter++;

          if (this.taskListComp) this.taskListComp.loadTasks();
          if (this.kanbanComp) this.kanbanComp.loadData();
        },
        error: (err) => {
          console.error('Fehler beim Löschen', err);
          this.showErrorAlert('Fehler beim Löschen des Tasks.');
        }
      });
    });
  }
}
