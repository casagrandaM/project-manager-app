import { Component, OnInit, EventEmitter, Output, Input, OnChanges, SimpleChanges, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProjectService } from '../../../../services/project.service';
import { Project } from '../../../../models/project.model';

@Component({
  selector: 'app-project-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './project-list.component.html',
  styleUrl: './project-list.component.css'
})
export class ProjectListComponent implements OnInit, OnChanges {
  projects: Project[] = [];

  @Input() refreshTrigger: number = 0;
  @Output() create = new EventEmitter<void>();
  @Output() edit = new EventEmitter<Project>();
  @Output() requestDelete = new EventEmitter<number>();
  @Output() select = new EventEmitter<Project>();

  constructor(private projectService: ProjectService, private cdr: ChangeDetectorRef) {}

  ngOnInit(): void {
    this.loadProjects();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['refreshTrigger'] && !changes['refreshTrigger'].firstChange) {
      this.loadProjects();
    }
  }

  loadProjects(): void {
    this.projectService.getProjects().subscribe({
      next: (data) => { this.projects = data; this.cdr.detectChanges(); },
      error: (err) => console.error(err)
    });
  }

  formatDate(dateStr?: string): string {
    if (!dateStr) return '-';
    return new Date(dateStr).toLocaleDateString('de-AT');
  }
}
