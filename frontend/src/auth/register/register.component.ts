import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../auth.service';

/**
 * Component responsible for user registration and OAuth login options.
 * <p>
 * Creates a new user account and automatically logs the user in after successful registration.
 */
@Component({
  selector: 'app-register',
  imports: [FormsModule, RouterLink],
  templateUrl: './register.component.html'
})
export class RegisterComponent {

  name = '';
  email = '';
  password = '';
  confirmPassword = '';

  constructor(private authService: AuthService, private router: Router) {}

  /**
   * Initiates registration flow to register a new user account.
   */
  register(): void {
    if (this.password !== this.confirmPassword) {
      alert('Passwords do not match');
      return;
    }

    this.authService.register({
      name: this.name,
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
        alert('Invalid Credentials');
      }
    });
  }

  /**
   * Initiates Google OAuth login/registration flow (coming from the registration page).
   */
  loginWithGoogle() {
    this.authService.loginWithGoogle();
  }

  /**
   * Initiates GitHub OAuth login/registration flow (coming from the registration page).
   */
  loginWithGithub() {
    this.authService.loginWithGithub();
  }
}
