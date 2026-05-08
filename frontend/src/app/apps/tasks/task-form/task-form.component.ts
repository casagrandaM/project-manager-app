import { Component, EventEmitter, Output, Input, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TaskService } from '../../../../services/task.service';
import { StatusService } from '../../../../services/status.service';
import { ProjectService } from '../../../../services/project.service';
import { Task, CreateTask } from '../../../../models/task.model';
import { Status } from '../../../../models/status.model';
import { Project } from '../../../../models/project.model';

@Component({
  selector: 'app-task-form',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './task-form.component.html',
  styleUrl: './task-form.component.css'
})
export class TaskFormComponent implements OnInit {

  @Output() close = new EventEmitter<boolean>();
  @Output() showMessage = new EventEmitter<{ text: string, type: 'success' | 'error' }>();

  @Input() taskToEdit: Task | null = null;
  @Input() projectId: number | null = null;

  task = {
    title: '',
    description: '',
    deadline: '',
    statusId: 0,
    projectId: null as number | null
  };

  isEditMode = false;
  statuses: Status[] = [];
  projects: Project[] = [];

  constructor(
    private taskService: TaskService,
    private statusService: StatusService,
    private projectService: ProjectService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.isEditMode = !!this.taskToEdit;

    if (this.isEditMode && this.taskToEdit) {
      this.task.title = this.taskToEdit.title;
      this.task.description = this.taskToEdit.description || '';
      this.task.deadline = this.taskToEdit.deadline || '';
      this.task.projectId = this.taskToEdit.projectId ?? null;
    } else if (this.projectId) {
      this.task.projectId = this.projectId;
    }

    this.statusService.getAllStatuses().subscribe({
      next: (data) => {
        this.statuses = data;

        if (this.isEditMode && this.taskToEdit) {
          const currentStatus = this.statuses.find(s => s.name === this.taskToEdit?.status);
          if (currentStatus) {
            this.task.statusId = currentStatus.id;
          } else {
            const todo = this.statuses.find(s => s.name === 'To Do');
            this.task.statusId = todo ? todo.id : 0;
          }
        } else {
          const todo = this.statuses.find(s => s.name === 'To Do');
          if (todo) this.task.statusId = todo.id;
        }
        this.cdr.detectChanges();
      },
      error: (err) => console.error('Fehler beim Laden der Statusse', err)
    });

    if (!this.projectId) {
      this.projectService.getProjects().subscribe({
        next: (data) => {
          this.projects = data;
          this.cdr.detectChanges();
        },
        error: (err) => console.error('Fehler beim Laden der Projekte', err)
      });
    }
  }

  validateForm(): boolean {
    const errors: string[] = [];

    if (!this.task.title || this.task.title.trim() === '') {
      errors.push('Titel');
    }
    if (!this.task.description || this.task.description.trim() === '') {
      errors.push('Beschreibung');
    }
    if (!this.task.deadline) {
      errors.push('Deadline');
    }
    if (errors.length > 0) {
      const message = `Bitte füllen Sie folgende Pflichtfelder aus: ${errors.join(', ')}.`;
      this.showMessage.emit({ text: message, type: 'error' });
      return false;
    }

    return true;
  }

  saveTask(): void {
    if (!this.validateForm()) {
      return;
    }

    if (!this.taskToEdit) {
      const dto: CreateTask = {
        title: this.task.title,
        description: this.task.description,
        deadline: this.task.deadline,
        projectId: this.task.projectId!,
        createdById: 1
      };
      this.taskService.createTask(dto).subscribe({
        next: () => this.close.emit(true),
        error: (err) => {
          console.error(err);
          this.showMessage.emit({ text: 'Fehler beim Erstellen des Tasks.', type: 'error' });
        }
      });
    } else {
      const taskId = this.taskToEdit.id;
      const dto = {
        title: this.task.title,
        description: this.task.description,
        deadline: this.task.deadline,
        lastStepDesc: this.taskToEdit.lastStepDesc
      };

      this.taskService.updateTask(taskId, dto).subscribe({
        next: () => {
          if (this.task.statusId > 0) {
            this.taskService.changeTaskStatus(taskId, this.task.statusId).subscribe({
              next: () => this.close.emit(true),
              error: () => this.close.emit(true)
            });
          } else {
            this.close.emit(true);
          }
        },
        error: (err) => {
          console.error(err);
          this.showMessage.emit({ text: 'Fehler beim Aktualisieren.', type: 'error' });
        }
      });
    }
  }

  cancel(): void {
    this.close.emit(false);
  }
}
