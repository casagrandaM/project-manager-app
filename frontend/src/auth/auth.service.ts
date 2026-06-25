import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import {Observable, tap} from 'rxjs';
import {User} from '../models/user.model';

/**
 * Represents a login request containing user credentials.
 */
export interface LoginRequest {
  email: string;
  password: string;
}

/**
 * Represents a user registration request.
 */
export interface RegisterRequest {
  name: string;
  email: string;
  password: string;
}

/**
 * Represents an authentication response containing a JWT token.
 */
export interface AuthResponse {
  token: string;
}

/**
 * Service responsible for authentication operations such as login, registration,
 * token handling, and OAuth redirects.
 * <p>
 * Manages the current authenticated user state and JWT storage.
 */
@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private readonly apiUrl = 'http://localhost:8080/auth';
  private currentUser?: User;

  constructor(private http: HttpClient) {}

  /**
   * Registers a new user account.
   *
   * @param request The registration request containing user credentials
   * @return An observable emitting the authentication response
   */
  register(request: RegisterRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/register`, request);
  }

  /**
   * Authenticates a user using email and password credentials.
   *
   * @param request The login request containing user credentials
   * @return An observable emitting the authentication response
   */
  login(request: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, request);
  }

  /**
   * Loads and stores the currently authenticated user.
   *
   * @return An observable emitting the authenticated user
   */
  loadCurrentUser(): Observable<User> {
    return this.fetchCurrentUser().pipe(
      tap(user => {
        this.currentUser = user
      })
    );
  }

  /**
   * Retrieves the currently authenticated user from the backend.
   *
   * @return An observable emitting the current user
   */
  fetchCurrentUser(): Observable<User> {
    return this.http.get<User>(`${this.apiUrl}/me`);
  }

  /**
   * Returns the currently stored authenticated user.
   *
   * @return The current user, if loaded
   */
  getCurrentUser(): User | undefined {
    return this.currentUser;
  }

  /**
   * Initiates Google OAuth login by redirecting to the API OAuth endpoint.
   */
  loginWithGoogle(): void {
    window.location.href = 'http://localhost:8080/oauth2/authorization/google';
  }

  /**
   * Initiates GitHub OAuth login by redirecting to the API OAuth endpoint.
   */
  loginWithGithub(): void {
    window.location.href = 'http://localhost:8080/oauth2/authorization/github';
  }

  /**
   * Stores the JWT token in local storage.
   *
   * @param token The JWT token
   */
  storeToken(token: string): void {
    localStorage.setItem('jwt', token);
  }

  /**
   * Retrieves the stored JWT token.
   *
   * @return The JWT token, if present
   */
  getToken(): string | null {
    return localStorage.getItem('jwt');
  }

  /**
   * Checks whether a user is currently authenticated.
   *
   * @return {@code true} if a JWT token exists
   */
  isLoggedIn(): boolean {
    return !!this.getToken();
  }

  /**
   * Checks whether the current user has admin privileges.
   *
   * @return {@code true} if the user has the ADMIN role
   */
  isAdmin(): boolean {
    return this.currentUser?.role.name === 'ADMIN';
  }

  /**
   * Logs out the current user by clearing authentication data.
   */
  logout(): void {
    localStorage.removeItem('jwt');
    this.currentUser = undefined;
  }
}
