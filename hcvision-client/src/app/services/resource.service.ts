import { Injectable } from '@angular/core';
import {Observable} from "rxjs";
import {HttpClient} from "@angular/common/http";
import {OptimalResponce} from "../models/OptimalResponce";

@Injectable({
  providedIn: 'root'
})
export class ResourceService {

  private baseUrl = 'http://localhost:8080/api/v1/resources'; // Replace with your API base URL

  constructor(private http: HttpClient) { }

  getOptimalResult(id:number): Observable<OptimalResponce> {
    const url = `${this.baseUrl}/optimal/${id}?resource=optimal_params`;
    return this.http.get<OptimalResponce>(url);
  }
}
