import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {Dataset} from "../models/Dataset";

@Injectable({
  providedIn: 'root'
})
export class HistoryService {

  private baseUrl = 'http://localhost:8080/api/v1/hierarchical/history';

  constructor(private http: HttpClient) {
  }

  getHistoryList(): Observable<any[]> {
    return this.http.get<any[]>(this.baseUrl);
  }

  getHistoryById(id: number): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/${id}`);
  }


  deleteHistoryById(id: number): Observable<any> {
    return this.http.delete<any>(`${this.baseUrl}/${id}`);
  }


}
