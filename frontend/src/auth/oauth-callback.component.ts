import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from './auth.service';

@Component({
  selector: 'app-oauth-callback',
  template: '<p>Wird angemeldet...</p>'
})
export class OAuthCallbackComponent implements OnInit {

  constructor(private route: ActivatedRoute, private router: Router, private authService: AuthService) {}

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
