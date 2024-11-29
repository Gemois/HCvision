import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {map, Observable} from 'rxjs';
import {Router} from "@angular/router";
import {AuthResponse} from "../models/AuthResponse";

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'http://localhost:8080/api/v1/auth';
  public redirectUrl: string | null = null;
  confirmed: boolean = false;

  constructor(private http: HttpClient, private router: Router) {
  }

  login(credentials: { email: string; password: string }): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/authenticate`, credentials)
      .pipe(
        map((response: AuthResponse) => {
          this.confirmed = response.confirmed || false;
          sessionStorage.setItem('confirmed', String(this.confirmed));
          return response;
        })
      );
  }

  register(userData: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/register`, userData);
  }

  setToken(access_token: string): void {
    sessionStorage.setItem('access_token', access_token);
    this.router.navigateByUrl(this.redirectUrl || '/');
    this.redirectUrl = null;
  }

  getToken(): string | null {
    return sessionStorage.getItem('access_token');
  }

  isAuthenticated(): boolean {
    const token = this.getToken();
    return !!token;
  }

  isConfirmed(): boolean {
    return sessionStorage.getItem('confirmed') == 'true';
  }

  confirmEmail(token: string): Observable<any> {
    const apiUrl = `${this.apiUrl}/confirm?token=${token}`;
    return this.http.get(apiUrl);
  }

  logout(): void {
    sessionStorage.removeItem('access_token');
  }

}
