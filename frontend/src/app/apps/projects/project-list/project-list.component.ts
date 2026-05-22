import { Component, OnInit, EventEmitter, Output, Input, OnChanges, SimpleChanges, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ProjectService } from '../../../../services/project.service';
import { TaskService } from '../../../../services/task.service';
import { Project } from '../../../../models/project.model';

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

  ngOnInit(): void {
    this.loadProjects();
    this.loadTaskProgress();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['refreshTrigger'] && !changes['refreshTrigger'].firstChange) {
      this.loadProjects();
      this.loadTaskProgress();
    }
  }

  loadProjects(): void {
    this.projectService.getProjects().subscribe({
      next: (data) => { this.projects = data; this.cdr.detectChanges(); },
      error: (err) => console.error(err)
    });
  }

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

  getProjectProgress(projectId: number): number {
    const entry = this.projectProgress.get(projectId);
    if (!entry || entry.total === 0) return 0;
    return Math.round((entry.done / entry.total) * 100);
  }

  getProjectTaskCounts(projectId: number): { done: number; total: number } {
    return this.projectProgress.get(projectId) ?? { done: 0, total: 0 };
  }

  get filteredProjects(): Project[] {
    const term = this.searchTerm.trim().toLowerCase();
    if (!term) return this.projects;
    return this.projects.filter(p =>
      p.title.toLowerCase().includes(term) ||
      (p.description ?? '').toLowerCase().includes(term)
    );
  }

  formatDate(dateStr?: string): string {
    if (!dateStr) return '-';
    return new Date(dateStr).toLocaleDateString('de-AT');
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

  getInitial(title: string): string {
    return title ? title.charAt(0).toUpperCase() : '?';
  }
}
