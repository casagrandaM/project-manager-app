import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Status } from '../models/status.model';

@Injectable({
  providedIn: 'root'
})
export class StatusService {

  private baseUrl = 'http://localhost:8080/api/statuses';

  constructor(private http: HttpClient) {}

  getAllStatuses(): Observable<Status[]> {
    return this.http.get<Status[]>(this.baseUrl);
  }

  getStatusById(id: number): Observable<Status> {
    return this.http.get<Status>(`${this.baseUrl}/${id}`);
  }
}
