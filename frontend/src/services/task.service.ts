import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Task } from '../models/task.model';

/**
 * Service that communicates with the backend task API and exposes
 * task CRUD operations as well as task status updates.
 */
@Injectable({ providedIn: 'root' })
export class TaskService {
  private baseUrl = 'http://localhost:8080/api/tasks';

  constructor(private http: HttpClient) {}

  /**
   * Retrieves all tasks or, if specified, only the tasks belonging to a
   * particular project.
   * @param projectId The optional project ID used to filter tasks
   */
  getTasks(projectId?: number): Observable<Task[]> {
    const url = projectId != null ? `${this.baseUrl}?projectId=${projectId}` : this.baseUrl;
    return this.http.get<Task[]>(url);
  }

  /**
   * Creates a new task.
   * @param task The task creation payload
   */
  createTask(task: any): Observable<Task> {
    return this.http.post<Task>(this.baseUrl, task);
  }

  /**
   * Updates an existing task.
   * @param id The task ID
   * @param task The updated task payload
   */
  updateTask(id: number, task: any): Observable<Task> {
    return this.http.put<Task>(`${this.baseUrl}/${id}`, task);
  }

  /**
   * Deletes a task.
   * @param id The task ID
   */
  deleteTask(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  /**
   * Changes the status of a task.
   * @param taskId The task ID
   * @param statusId The ID of the new status
   */
  changeTaskStatus(taskId: number, statusId: number): Observable<void> {
    return this.http.post<void>(`${this.baseUrl}/${taskId}/status/${statusId}`, {});
  }
}
