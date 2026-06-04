import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {User} from '../models/user.model';
import {AuthService} from '../auth/auth.service';

@Injectable({ providedIn: 'root' })
export class UserService {
  private baseUrl = 'http://localhost:8080/api/users';

  constructor(private http: HttpClient, private authService: AuthService) {}

  getCurrentUser(): User {
    return <User>this.authService.getCurrentUser();
  }

  getUserById(userId: number): Observable<User> {
    return this.http.get<User>(`${this.baseUrl}/${userId}`);
  }

  getAllUsers(): Observable<User[]> {
    return this.http.get<User[]>(this.baseUrl);
  }

  updateUser(id: number, data: { name: string; email: string }): Observable<User> {
    return this.http.put<User>(`${this.baseUrl}/${id}`, data);
  }
}
