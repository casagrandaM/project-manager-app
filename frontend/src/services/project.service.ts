import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Project, CreateProject, UpdateProject } from '../models/project.model';
import { ActivityEvent } from '../models/activity-event.model';

/**
 * Service that communicates with the backend project API and exposes
 * project CRUD operations as well as the project activity feed.
 */
@Injectable({ providedIn: 'root' })
export class ProjectService {
  private baseUrl = 'http://localhost:8080/api/projects';

  constructor(private http: HttpClient) {}

  /**
   * Retrieves all projects the current user is a member of.
   */
  getProjects(): Observable<Project[]> {
    return this.http.get<Project[]>(this.baseUrl);
  }

  /**
   * Retrieves a single project by ID.
   * @param id The project ID
   */
  getProjectById(id: number): Observable<Project> {
    return this.http.get<Project>(`${this.baseUrl}/${id}`);
  }

  /**
   * Creates a new project.
   * @param project The project creation payload (title and description)
   */
  createProject(project: CreateProject): Observable<Project> {
    return this.http.post<Project>(this.baseUrl, project);
  }

  /**
   * Updates an existing project.
   * @param id The project ID
   * @param project The updated project payload
   */
  updateProject(id: number, project: UpdateProject): Observable<Project> {
    return this.http.put<Project>(`${this.baseUrl}/${id}`, project);
  }

  /**
   * Deletes a project together with all of its tasks.
   * @param id The project ID
   */
  deleteProject(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  /**
   * Retrieves the aggregated activity feed for a project.
   * @param id The project ID
   */
  getProjectActivity(id: number): Observable<ActivityEvent[]> {
    return this.http.get<ActivityEvent[]>(`${this.baseUrl}/${id}/activity`);
  }
}
