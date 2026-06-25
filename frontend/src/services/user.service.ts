import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {User} from '../models/user.model';
import {AuthService} from '../auth/auth.service';

/**
 * Service for handling user-related operations and communication with the backend API.
 * <p>
 * Provides methods for retrieving, updating, and deleting user data from the API.
 */
@Injectable({ providedIn: 'root' })
export class UserService {
  private baseUrl = 'http://localhost:8080/api/users';

  constructor(private http: HttpClient, private authService: AuthService) {}

  /**
   * Retrieves the currently authenticated user from the {@link AuthService}.
   *
   * @return The current user
   */
  getCurrentUser(): User {
    return <User>this.authService.getCurrentUser();
  }

  /**
   * Retrieves a user by their ID from the API.
   *
   * @param userId The user ID
   * @return An observable emitting the user
   */
  getUserById(userId: number): Observable<User> {
    return this.http.get<User>(`${this.baseUrl}/${userId}`);
  }

  /**
   * Retrieves all users from the API.
   *
   * @return An observable emitting a list of users
   */
  getAllUsers(): Observable<User[]> {
    return this.http.get<User[]>(this.baseUrl);
  }

  /**
   * Updates an existing user with new data.
   *
   * @param id   The user ID
   * @param data The updated user information
   * @return An observable emitting the updated user
   */
  updateUser(id: number, data: { name: string; email: string }): Observable<User> {
    return this.http.put<User>(`${this.baseUrl}/${id}`, data);
  }

  /**
   * Deletes a user by their ID.
   *
   * @param id The user ID
   * @return An observable emitting the deleted user
   */
  deleteUser(id: number): Observable<User> {
    return this.http.delete<User>(`${this.baseUrl}/${id}`);
  }
}
