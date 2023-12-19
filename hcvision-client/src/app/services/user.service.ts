import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private baseUrl = 'http://localhost:8080/api/v1/users';

  constructor(private http: HttpClient) {
  }

  getUser(): Observable<any> {
    return this.http.get(`${this.baseUrl}`);
  }

  updateUser(userData: any): Observable<any> {
    return this.http.post(`${this.baseUrl}/update`, userData);
  }

  deleteUser(): Observable<any> {
    return this.http.delete(`${this.baseUrl}/delete`);
  }


  resetPassword(newPassword: string, confirmationToken: string): Observable<any> {
    const resetPasswordUrl = `${this.baseUrl}/password/reset`;

    const requestBody = {
      password: newPassword,
      confirmation_token: confirmationToken
    };

    return this.http.post(resetPasswordUrl, requestBody);
  }
  sendOTP(email: string): Observable<any> {
    const sendOtpUrl = `${this.baseUrl}/password/forgot`;

    const requestBody = {
      email: email
    };

    return this.http.post(sendOtpUrl, requestBody);
  }



}
