import { Component, OnInit, Input, Output, EventEmitter, ChangeDetectorRef, OnChanges, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TaskService } from '../../../../services/task.service';
import { StatusService } from '../../../../services/status.service';
import { Task } from '../../../../models/task.model';
import { Status } from '../../../../models/status.model';

/**
 * Kanban board component for displaying project tasks grouped by their status.
 * It loads available task statuses and tasks, organizes them into columns,
 * supports moving tasks between statuses, and emits events for editing tasks
 * and notifying the parent component about status changes.
 */
@Component({
  selector: 'app-kanban-board',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './kanban-board.component.html',
  styleUrl: './kanban-board.component.css'
})
export class KanbanBoardComponent implements OnInit, OnChanges {

  @Input() refreshTrigger: number = 0;
  @Input() projectId: number | null = null;

  @Output() edit = new EventEmitter<Task>();
  @Output() statusChanged = new EventEmitter<void>();

  todoTasks: Task[] = [];
  progressTasks: Task[] = [];
  doneTasks: Task[] = [];

  allStatuses: Status[] = [];

  statusIds = {
    todo: 0,
    progress: 0,
    done: 0
  };

  constructor(
    private taskService: TaskService,
    private statusService: StatusService,
    private cdr: ChangeDetectorRef
  ) {}

  /**
   * Loads the initial task and status data when the component is initialized.
   */
  ngOnInit(): void {
    this.loadData();
  }

  /**
   * Reloads the board whenever the refresh trigger input changes after the
   * initial component creation.
   * @param changes The changed input properties
   */
  ngOnChanges(changes: SimpleChanges): void {
    // Wenn sich der Trigger ändert (und es nicht das initiale Laden ist), Daten neu laden
    if (changes['refreshTrigger'] && !changes['refreshTrigger'].firstChange) {
      this.loadData();
    }
  }

  /**
   * Loads all available statuses and tasks, maps the status IDs, and sorts the
   * tasks into their corresponding kanban columns.
   */
  loadData(): void {
    // 1. Statusse laden (um IDs zu mappen)
    this.statusService.getAllStatuses().subscribe({
      next: (statuses) => {
        this.allStatuses = statuses;
        this.mapStatusIds();

        // 2. Tasks laden
        this.taskService.getTasks(this.projectId ?? undefined).subscribe({
          next: (tasks) => {
            this.sortTasksIntoColumns(tasks);
            this.cdr.detectChanges();
          },
          error: (err) => console.error(err)
        });
      },
      error: (err) => console.error('Status load error', err)
    });
  }

  /**
   * Maps the predefined status names to their corresponding backend IDs.
   */
  mapStatusIds(): void {
    const todo = this.allStatuses.find(s => s.name === 'To Do');
    const progress = this.allStatuses.find(s => s.name === 'In Progress');
    const done = this.allStatuses.find(s => s.name === 'Done');

    if (todo) this.statusIds.todo = todo.id;
    if (progress) this.statusIds.progress = progress.id;
    if (done) this.statusIds.done = done.id;
  }

  /**
   * Distributes the provided tasks into the To Do, In Progress, and Done
   * columns based on their current status.
   * @param tasks The tasks to organize
   */
  sortTasksIntoColumns(tasks: Task[]): void {
    this.todoTasks = [];
    this.progressTasks = [];
    this.doneTasks = [];

    tasks.forEach(task => {
      // Wir nutzen den Status-Namen aus dem Task-Objekt
      if (task.status === 'To Do') {
        this.todoTasks.push(task);
      } else if (task.status === 'In Progress') {
        this.progressTasks.push(task);
      } else if (task.status === 'Done') {
        this.doneTasks.push(task);
      } else {
        // Fallback
        this.todoTasks.push(task);
      }
    });
  }

  /**
   * Moves a task one step forward or backward through the workflow, updates its
   * status in the backend, reloads the board, and notifies the parent component.
   * @param task The task to move
   * @param direction The direction in which to move the task
   */
  moveTask(task: Task, direction: 'forward' | 'backward'): void {
    let newStatusId = 0;

    if (direction === 'forward') {
      if (task.status === 'To Do') newStatusId = this.statusIds.progress;
      else if (task.status === 'In Progress') newStatusId = this.statusIds.done;
    }
    else {
      if (task.status === 'Done') newStatusId = this.statusIds.progress;
      else if (task.status === 'In Progress') newStatusId = this.statusIds.todo;
    }

    if (newStatusId > 0) {
      this.taskService.changeTaskStatus(task.id, newStatusId).subscribe({
        next: () => {
          this.loadData();
          this.statusChanged.emit();
        },
        error: (err) => {
          console.error('Move failed', err);
          alert('Verschieben fehlgeschlagen');
        }
      });
    }
  }

  /**
   * Updates a task to a specific status, applies the new status locally, and
   * re-sorts the kanban columns without reloading all tasks.
   * @param task The task to update
   * @param newStatusId The ID of the target status
   */
  updateTaskStatus(task: Task, newStatusId: number): void {
    this.taskService.changeTaskStatus(task.id, newStatusId).subscribe({
      next: () => {
        const newStatusName = this.allStatuses.find(s => s.id === newStatusId)?.name;
        if (newStatusName) {
          task.status = newStatusName;
          this.sortTasksIntoColumns([...this.todoTasks, ...this.progressTasks, ...this.doneTasks]);
        }
      },
      error: (err) => {
        console.error('Move failed', err);
        alert('Verschieben fehlgeschlagen');
      }
    });
  }

  /**
   * Emits the selected task so it can be opened in the parent component's edit
   * form.
   * @param task The task to edit
   */
  openEdit(task: Task): void {
    this.edit.emit(task);
  }
}
