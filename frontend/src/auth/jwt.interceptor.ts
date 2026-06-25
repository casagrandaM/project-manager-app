import { HttpInterceptorFn } from '@angular/common/http';

/**
 * HTTP interceptor that attaches the JWT token to outgoing requests.
 * <p>
 * If a JWT token exists in localStorage, it is added as a Bearer token
 * in the Authorization header for all HTTP requests.
 */
export const jwtInterceptor: HttpInterceptorFn =
  (req, next) => {

    const token = localStorage.getItem('jwt');

    if (!token) {
      return next(req);
    }

    const cloned = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });

    return next(cloned);
  };
