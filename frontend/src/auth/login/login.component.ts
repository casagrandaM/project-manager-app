import { Component } from '@angular/core';
import {Router, RouterLink} from '@angular/router';
import {AuthService} from '../auth.service';
import {FormsModule} from '@angular/forms';

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

  loginWithGoogle(): void {
    this.authService.loginWithGoogle();
  }

  loginWithGithub(): void {
    this.authService.loginWithGithub();
  }
}
