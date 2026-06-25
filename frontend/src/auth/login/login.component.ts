import { Component } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../auth.service';
import { FormsModule } from '@angular/forms';

/**
 * Component responsible for user login via email/password and OAuth providers.
 * <p>
 * Handles authentication requests and redirects the user after successful login.
 */
@Component({
  selector: 'app-login',
  imports: [
    FormsModule,
    RouterLink
  ],
  templateUrl: './login.component.html'
})
export class LoginComponent {

  email = '';
  password = '';

  constructor(private authService: AuthService, private router: Router) {}

  /**
   * Initiates login flow to authenticate a user with email and password credentials.
   */
  login(): void {
    this.authService.login({
      email: this.email,
      password: this.password
    }).subscribe({
      next: response => {
        this.authService.storeToken(response.token);

        this.authService.loadCurrentUser().subscribe(() => {
          this.router.navigate(['/user/profile']);
        });
      },
      error: () => {
        alert('Invalid credentials');
      }
    });
  }

  /**
   * Initiates Google OAuth login flow.
   */
  loginWithGoogle(): void {
    this.authService.loginWithGoogle();
  }

  /**
   * Initiates GitHub OAuth login flow.
   */
  loginWithGithub(): void {
    this.authService.loginWithGithub();
  }
}
