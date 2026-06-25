import { Component, ViewChild, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { ProjectListComponent } from '../project-list/project-list.component';
import { ProjectFormComponent } from '../project-form/project-form.component';
import { ProjectService } from '../../../../services/project.service';
import { Project } from '../../../../models/project.model';

/**
 * Container component for the projects overview page. It orchestrates the
 * project list and project form, handles navigation to project details, and
 * manages the shared confirmation/success/error dialog used for create, edit
 * and delete flows.
 */
@Component({
  selector: 'app-project-page',
  standalone: true,
  imports: [CommonModule, ProjectListComponent, ProjectFormComponent],
  templateUrl: './project-page.component.html',
  styleUrl: './project-page.component.css'
})
export class ProjectPageComponent {
  showForm = false;
  projectToEdit: Project | null = null;
  refreshCounter = 0;

  showDialog = false;
  dialogMessage = '';
  isConfirmDialog = false;
  isSuccessDialog = false;
  confirmCallback: (() => void) | null = null;

  @ViewChild(ProjectListComponent) projectListComp!: ProjectListComponent;

  constructor(
    private router: Router,
    private projectService: ProjectService,
    private cdr: ChangeDetectorRef
  ) {}

  /**
   * Opens the project form in create mode.
   */
  openForm(): void {
    this.projectToEdit = null;
    this.showForm = true;
  }

  /**
   * Opens the project form in edit mode for the given project.
   * @param project The project to edit
   */
  openEditForm(project: Project): void {
    this.projectToEdit = project;
    this.showForm = true;
  }

  /**
   * Handles the form's close event. When refreshing, reloads the list and shows
   * a success message; otherwise asks the user to confirm discarding changes.
   * @param refresh Whether the form was saved successfully
   */
  closeForm(refresh: boolean): void {
    if (!refresh) {
      this.showConfirm('Möchten Sie wirklich abbrechen? Ungespeicherte Änderungen gehen verloren.', () => {
        this.resetToList();
      });
    } else {
      this.refreshCounter++;
      if (this.projectListComp) this.projectListComp.loadProjects();
      const msg = this.projectToEdit ? 'Projekt erfolgreich aktualisiert!' : 'Projekt erfolgreich erstellt!';
      this.showSuccessAlert(msg);
      this.projectToEdit = null;
    }
  }

  /**
   * Surfaces error messages emitted by the project form as an error alert.
   * @param event The message emitted by the form
   */
  handleFormMessage(event: { text: string; type: 'success' | 'error' }): void {
    if (event.type === 'error') {
      this.showErrorAlert(event.text);
    }
  }

  /**
   * Asks the user to confirm deletion and, if confirmed, deletes the project
   * and reloads the list.
   * @param id The ID of the project to delete
   */
  requestDelete(id: number): void {
    this.showConfirm('Sind Sie sicher, dass Sie dieses Projekt löschen wollen? Alle Tasks des Projekts werden ebenfalls gelöscht.', () => {
      this.projectService.deleteProject(id).subscribe({
        next: () => {
          this.refreshCounter++;
          if (this.projectListComp) this.projectListComp.loadProjects();
          this.showSuccessAlert('Projekt erfolgreich gelöscht.');
        },
        error: () => this.showErrorAlert('Fehler beim Löschen des Projekts.')
      });
    });
  }

  /**
   * Navigates to the detail page of the selected project.
   * @param project The selected project
   */
  selectProject(project: Project): void {
    this.router.navigate(['/projects', project.id]);
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
      this.resetToList();
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
   * Resets the page back to the project list view, hiding the form.
   */
  resetToList(): void {
    this.showForm = false;
    this.projectToEdit = null;
  }
}
