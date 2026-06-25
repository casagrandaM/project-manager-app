import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Status } from '../models/status.model';

/**
 * Service that communicates with the backend status API and exposes
 * operations for retrieving available task statuses.
 */
@Injectable({
  providedIn: 'root'
})
export class StatusService {

  private baseUrl = 'http://localhost:8080/api/statuses';

  constructor(private http: HttpClient) {}

  /**
   * Retrieves all available task statuses.
   */
  getAllStatuses(): Observable<Status[]> {
    return this.http.get<Status[]>(this.baseUrl);
  }

  /**
   * Retrieves a single task status by ID.
   * @param id The status ID
   */
  getStatusById(id: number): Observable<Status> {
    return this.http.get<Status>(`${this.baseUrl}/${id}`);
  }
}
