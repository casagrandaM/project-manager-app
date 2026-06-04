import { Component } from '@angular/core';
import {RouterOutlet, RouterLink, RouterLinkActive, Router} from '@angular/router';
import {AuthService} from '../auth/auth.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './app.html'
})
export class App {
  constructor(
    private authService: AuthService,
    private router: Router,
  ) {}

  ngOnInit(): void {

    if (this.authService.isLoggedIn()) {

      this.authService
        .loadCurrentUser()
        .subscribe();
    }
  }

  showNavbar(): boolean {
    return !['/login', '/register'].includes(this.router.url);
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
