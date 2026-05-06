import { ApplicationConfig, inject, provideAppInitializer, provideBrowserGlobalErrorListeners } from '@angular/core';
import { provideRouter } from '@angular/router';

import { routes } from './app.routes';
import { HttpInterceptorFn, provideHttpClient, withInterceptors } from '@angular/common/http';
import { authInterceptor } from './core/auth/auth.interceptor';
import { catchError, of } from 'rxjs';
import { AuthService } from './service/auth.service';

export const corsInterceptor: HttpInterceptorFn = (req, next) => {

  const isFormData = req.body instanceof FormData;

  const clonedRequest = req.clone({
    withCredentials: true,
    ...(isFormData ? {} : {
      setHeaders: {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
      }
    })
  });
  return next(clonedRequest);
};

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideRouter(routes),
    provideHttpClient(withInterceptors([corsInterceptor, authInterceptor])),
    AuthService,
    provideAppInitializer(() => {
      const authService = inject(AuthService);
      return authService.refreshToken().pipe(catchError(() => of(null)));
    }),
  ]
};
