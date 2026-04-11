import { Component, OnInit, Output, EventEmitter, Input, ChangeDetectorRef, OnChanges, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TaskService } from '../../../../services/task.service';
import { Task } from '../../../../models/task.model';

@Component({
  selector: 'app-task-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './task-list.component.html',
  styleUrl: './task-list.component.css'
})
export class TaskListComponent implements OnInit, OnChanges {

  tasks: Task[] = [];

  @Input() public searchTerm: string = '';
  @Input() public refreshTrigger: number = 0;

  @Output() create = new EventEmitter<void>();
  @Output() edit = new EventEmitter<Task>();
  @Output() requestDelete = new EventEmitter<number>();

  constructor(
    private taskService: TaskService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadTasks();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['refreshTrigger'] && !changes['refreshTrigger'].firstChange) {
      this.loadTasks();
    }
  }

  public loadTasks(): void {
    this.taskService.getTasks().subscribe({
      next: (data) => {
        this.tasks = data.sort((a, b) => a.id - b.id);
        this.cdr.detectChanges();
      },
      error: (err) => console.error(err)
    });
  }

  get filteredTasks(): Task[] {
    let result = this.tasks;
    if (this.searchTerm) {
      const term = this.searchTerm.toLowerCase();
      result = this.tasks.filter(task =>
        task.title.toLowerCase().includes(term) ||
        (task.description && task.description.toLowerCase().includes(term))
      );
    }
    return result.sort((a, b) => a.id - b.id);
  }

  getStatusColor(status: string | undefined): string {
    if (status === 'To Do') return '#d9534f';
    if (status === 'In Progress') return '#f0ad4e';
    if (status === 'Done') return '#5cb85c';
    return '#333';
  }

  deleteTask(id: number): void {
    this.requestDelete.emit(id);
  }

  openEditTask(task: Task): void {
    this.edit.emit(task);
  }

  openCreateTask(): void {
    this.create.emit();
  }
}
