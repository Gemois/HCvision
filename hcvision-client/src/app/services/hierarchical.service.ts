import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class HierarchicalService {
  private baseUrl = 'http://localhost:8080/api/v1/hierarchical';

  constructor(private http: HttpClient) {
  }

  runOptimal(data: any): Observable<any> {
    return this.http.post<any>(`${this.baseUrl}/optimal`, data);
  }

  runAnalysis(data: any): Observable<any> {
    return this.http.post<any>(`${this.baseUrl}/analysis`, data);
  }

}
