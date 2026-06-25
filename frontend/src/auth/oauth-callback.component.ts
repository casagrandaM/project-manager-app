import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from './auth.service';

/**
 * Component responsible for handling OAuth callback after successful authentication.
 * <p>
 * Processes the JWT token returned from the API, stores it, loads the current user,
 * and redirects to the user profile.
 */
@Component({
  selector: 'app-oauth-callback',
  template: '<p>Wird angemeldet...</p>'
})
export class OAuthCallbackComponent implements OnInit {

  constructor(private route: ActivatedRoute, private router: Router, private authService: AuthService) {}

  /**
   * Handles the OAuth callback flow by extracting the JWT token,
   * storing it, loading the current user, and redirecting.
   */
  ngOnInit(): void {
    const token = this.route.snapshot.queryParamMap.get('jwt');

    if (!token) {
      this.router.navigate(['/login']);
      return;
    }

    this.authService.storeToken(token);

    this.authService.loadCurrentUser().subscribe(() => {
      this.router.navigate(['/user/profile']);
    });
  }
}
