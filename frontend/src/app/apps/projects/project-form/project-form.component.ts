import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ProjectService } from '../../../../services/project.service';
import { Project } from '../../../../models/project.model';

@Component({
  selector: 'app-project-form',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './project-form.component.html',
  styleUrl: './project-form.component.css'
})
export class ProjectFormComponent implements OnInit {
  @Input() projectToEdit: Project | null = null;
  @Output() close = new EventEmitter<boolean>();
  @Output() showMessage = new EventEmitter<{ text: string; type: 'success' | 'error' }>();

  form = { title: '', description: '' };
  isEditMode = false;

  constructor(private projectService: ProjectService) {}

  ngOnInit(): void {
    this.isEditMode = !!this.projectToEdit;
    if (this.isEditMode && this.projectToEdit) {
      this.form.title = this.projectToEdit.title;
      this.form.description = this.projectToEdit.description || '';
    }
  }

  save(): void {
    if (!this.form.title.trim()) {
      this.showMessage.emit({ text: 'Bitte geben Sie einen Projekttitel ein.', type: 'error' });
      return;
    }

    if (!this.isEditMode) {
      this.projectService.createProject({ title: this.form.title, description: this.form.description }).subscribe({
        next: () => this.close.emit(true),
        error: () => this.showMessage.emit({ text: 'Fehler beim Erstellen des Projekts.', type: 'error' })
      });
    } else {
      this.projectService.updateProject(this.projectToEdit!.id, { title: this.form.title, description: this.form.description }).subscribe({
        next: () => this.close.emit(true),
        error: () => this.showMessage.emit({ text: 'Fehler beim Aktualisieren des Projekts.', type: 'error' })
      });
    }
  }

  cancel(): void {
    this.close.emit(false);
  }
}
