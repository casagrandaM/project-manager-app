import { Component, OnInit, EventEmitter, Output, Input, OnChanges, SimpleChanges, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ProjectService } from '../../../../services/project.service';
import { TaskService } from '../../../../services/task.service';
import { Project } from '../../../../models/project.model';

/**
 * Component that displays the user's projects as searchable cards, including a
 * per-project task completion progress indicator. Selection, creation, editing
 * and deletion are delegated to the parent via output events.
 */
@Component({
  selector: 'app-project-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './project-list.component.html',
  styleUrl: './project-list.component.css'
})
export class ProjectListComponent implements OnInit, OnChanges {
  projects: Project[] = [];
  searchTerm = '';
  projectProgress: Map<number, { done: number; total: number }> = new Map();

  @Input() refreshTrigger: number = 0;
  @Output() create = new EventEmitter<void>();
  @Output() edit = new EventEmitter<Project>();
  @Output() requestDelete = new EventEmitter<number>();
  @Output() select = new EventEmitter<Project>();

  constructor(private projectService: ProjectService, private taskService: TaskService, private cdr: ChangeDetectorRef) {}

  /**
   * Loads the projects and their task progress on component initialization.
   */
  ngOnInit(): void {
    this.loadProjects();
    this.loadTaskProgress();
  }

  /**
   * Reloads projects and task progress whenever the parent increments the
   * refresh trigger (e.g. after a project was created, edited or deleted).
   */
  ngOnChanges(changes: SimpleChanges): void {
    if (changes['refreshTrigger'] && !changes['refreshTrigger'].firstChange) {
      this.loadProjects();
      this.loadTaskProgress();
    }
  }

  /**
   * Fetches the current user's projects from the backend.
   */
  loadProjects(): void {
    this.projectService.getProjects().subscribe({
      next: (data) => { this.projects = data; this.cdr.detectChanges(); },
      error: (err) => console.error(err)
    });
  }

  /**
   * Loads all tasks and aggregates them per project into done/total counts
   * used to render each project's progress bar.
   */
  loadTaskProgress(): void {
    this.taskService.getTasks().subscribe({
      next: (tasks) => {
        const map = new Map<number, { done: number; total: number }>();
        for (const task of tasks) {
          if (task.projectId == null) continue;
          const entry = map.get(task.projectId) ?? { done: 0, total: 0 };
          entry.total++;
          if (task.status === 'Done') entry.done++;
          map.set(task.projectId, entry);
        }
        this.projectProgress = map;
        this.cdr.detectChanges();
      },
      error: (err) => console.error(err)
    });
  }

  /**
   * Returns the completion percentage (0–100) of a project based on its
   * done/total task counts.
   * @param projectId The project ID
   */
  getProjectProgress(projectId: number): number {
    const entry = this.projectProgress.get(projectId);
    if (!entry || entry.total === 0) return 0;
    return Math.round((entry.done / entry.total) * 100);
  }

  /**
   * Returns the done/total task counts for a project.
   * @param projectId The project ID
   */
  getProjectTaskCounts(projectId: number): { done: number; total: number } {
    return this.projectProgress.get(projectId) ?? { done: 0, total: 0 };
  }

  /**
   * Returns the projects filtered by the current search term, matching against
   * title and description.
   */
  get filteredProjects(): Project[] {
    const term = this.searchTerm.trim().toLowerCase();
    if (!term) return this.projects;
    return this.projects.filter(p =>
      p.title.toLowerCase().includes(term) ||
      (p.description ?? '').toLowerCase().includes(term)
    );
  }

  /**
   * Formats an ISO date string into a localized (de-AT) date, or "-" if absent.
   * @param dateStr The ISO date string
   */
  formatDate(dateStr?: string): string {
    if (!dateStr) return '-';
    return new Date(dateStr).toLocaleDateString('de-AT');
  }

  /**
   * Deterministically derives a CSS gradient for a project card from its ID so
   * that each project keeps a consistent color.
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
   * Returns the uppercase first letter of a project title for the card avatar.
   * @param title The project title
   */
  getInitial(title: string): string {
    return title ? title.charAt(0).toUpperCase() : '?';
  }
}
