import { HttpInterceptorFn, HttpRequest, HttpHandlerFn, HttpEvent, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { Observable, throwError, BehaviorSubject } from 'rxjs';
import { catchError, filter, switchMap, take } from 'rxjs/operators';
import { MatDialog } from '@angular/material/dialog';
import { AuthService } from '../../service/auth.service';
import { AuthDialog } from '../../component/auth-dialog/auth-dialog';

let isRefreshing = false;
let refreshTokenSubject: BehaviorSubject<string | null> = new BehaviorSubject<string | null>(null);

export const authInterceptor: HttpInterceptorFn = (req: HttpRequest<any>, next: HttpHandlerFn): Observable<HttpEvent<any>> => {
  const authService = inject(AuthService);
  const dialog = inject(MatDialog);

  if (req.url.includes('/login') || req.url.includes('/refresh')) {
    return next(req);
  }

  const token = authService.getAccessToken();
  let authReq = req;

  if (token) {
    authReq = req.clone({
      setHeaders: { Authorization: `Bearer ${token}` }
    });
  }

  return next(authReq).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status === 401 || error.status === 403) {
        return handleAuthError(req, next, authService, dialog, error);
      }
      return throwError(() => error);
    })
  );
};

function handleAuthError(req: HttpRequest<any>, next: HttpHandlerFn, authService: AuthService, dialog: MatDialog, originalError: HttpErrorResponse) {

  if (!isRefreshing) {
    isRefreshing = true;
    refreshTokenSubject.next(null);

    return authService.refreshToken().pipe(
      switchMap((newTokens: any) => {
        isRefreshing = false;
        refreshTokenSubject.next(newTokens.accessToken);

        return next(req.clone({
          setHeaders: { Authorization: `Bearer ${newTokens.accessToken}` }
        }));
      }),
      catchError((err) => {
        isRefreshing = false;
        if (dialog.openDialogs.length === 0) {
          dialog.open(AuthDialog, { width: '400px' });
        }
        return throwError(() => originalError);
      })
    );
  } else {
    return refreshTokenSubject.pipe(
      filter(token => token !== null),
      take(1),
      switchMap(jwt => {
        return next(req.clone({
          setHeaders: { Authorization: `Bearer ${jwt}` }
        }));
      })
    );
  }
}