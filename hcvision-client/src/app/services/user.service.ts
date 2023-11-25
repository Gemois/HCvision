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
}
