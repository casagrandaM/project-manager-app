import { Component, ViewChild, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { ProjectListComponent } from '../project-list/project-list.component';
import { ProjectFormComponent } from '../project-form/project-form.component';
import { ProjectService } from '../../../../services/project.service';
import { Project } from '../../../../models/project.model';

@Component({
  selector: 'app-project-page',
  standalone: true,
  imports: [CommonModule, ProjectListComponent, ProjectFormComponent],
  templateUrl: './project-page.component.html'
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

  openForm(): void {
    this.projectToEdit = null;
    this.showForm = true;
  }

  openEditForm(project: Project): void {
    this.projectToEdit = project;
    this.showForm = true;
  }

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

  handleFormMessage(event: { text: string; type: 'success' | 'error' }): void {
    if (event.type === 'error') {
      this.showErrorAlert(event.text);
    }
  }

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

  selectProject(project: Project): void {
    this.router.navigate(['/projects', project.id]);
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
      this.resetToList();
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

  resetToList(): void {
    this.showForm = false;
    this.projectToEdit = null;
  }
}
