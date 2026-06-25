import { CanActivateFn } from '@angular/router';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from './auth.service';
import { of } from 'rxjs';
import { map, catchError } from 'rxjs/operators';

/**
 * Route guard that prevents access to authenticated routes.
 * <p>
 * Loads the current user and allows navigation only if authentication is valid.
 * Otherwise, redirects to the login page.
 */
export const authGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  return authService.loadCurrentUser().pipe(
    map(user => {
      if (user) return true;
      router.navigate(['/login']);
      return false;
    }),
    catchError(() => {
      router.navigate(['/login']);
      return of(false);
    })
  );
};
