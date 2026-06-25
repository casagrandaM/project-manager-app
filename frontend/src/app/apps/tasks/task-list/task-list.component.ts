import { Component, OnInit, Output, EventEmitter, Input, ChangeDetectorRef, OnChanges, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TaskService } from '../../../../services/task.service';
import { Task } from '../../../../models/task.model';

/**
 * List component for displaying tasks. It loads tasks for the current project,
 * supports filtering by a search term, and emits events for creating, editing,
 * and deleting tasks while leaving these actions to the parent component.
 */
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
  @Input() public projectId: number | null = null;

  @Output() create = new EventEmitter<void>();
  @Output() edit = new EventEmitter<Task>();
  @Output() requestDelete = new EventEmitter<number>();

  constructor(
    private taskService: TaskService,
    private cdr: ChangeDetectorRef
  ) {}

  /**
   * Loads the initial list of tasks when the component is initialized.
   */
  ngOnInit(): void {
    this.loadTasks();
  }

  /**
   * Reloads the task list whenever the refresh trigger input changes after the
   * initial component creation.
   * @param changes The changed input properties
   */
  ngOnChanges(changes: SimpleChanges): void {
    if (changes['refreshTrigger'] && !changes['refreshTrigger'].firstChange) {
      this.loadTasks();
    }
  }

  /**
   * Loads all tasks for the current project and sorts them by their ID.
   */
  public loadTasks(): void {
    this.taskService.getTasks(this.projectId ?? undefined).subscribe({
      next: (data) => {
        this.tasks = data.sort((a, b) => a.id - b.id);
        this.cdr.detectChanges();
      },
      error: (err) => console.error(err)
    });
  }

  /**
   * Returns the list of tasks filtered by the current search term and sorted by
   * their ID.
   */
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

  /**
   * Returns the color associated with a task status for display purposes.
   * @param status The task status
   */
  getStatusColor(status: string | undefined): string {
    if (status === 'To Do') return '#d9534f';
    if (status === 'In Progress') return '#f0ad4e';
    if (status === 'Done') return '#5cb85c';
    return '#333';
  }

  /**
   * Emits a request to delete the specified task.
   * @param id The ID of the task to delete
   */
  deleteTask(id: number): void {
    this.requestDelete.emit(id);
  }

  /**
   * Emits the selected task so it can be edited by the parent component.
   * @param task The task to edit
   */
  openEditTask(task: Task): void {
    this.edit.emit(task);
  }

  /**
   * Emits an event requesting that the parent component open the task creation
   * form.
   */
  openCreateTask(): void {
    this.create.emit();
  }
}
