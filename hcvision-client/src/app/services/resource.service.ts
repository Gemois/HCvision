import {Injectable} from '@angular/core';
import {Observable} from "rxjs";
import {HttpClient} from "@angular/common/http";
import {OptimalResponse} from "../models/OptimalResponse";
import {Dataset} from "../models/Dataset";
import {DatasetResponse} from "../models/DatasetResponse";

@Injectable({
  providedIn: 'root'
})
export class ResourceService {

  private baseUrl = 'http://localhost:8080/api/v1/resources';

  constructor(private http: HttpClient) {
  }

  getOptimalResults(id: number): Observable<OptimalResponse> {
    const url = `${this.baseUrl}/optimal/${id}?resource=optimal_params`;
    return this.http.get<OptimalResponse>(url);
  }

  getAnalysisPlotResults(id: number, image: string): Observable<ArrayBuffer> {
    const url = `${this.baseUrl}/analysis/${id}?resource=${image}`;
    return this.http.get(url, {responseType: 'arraybuffer'});
  }

  getAnalysisAssignmentsResults(id: number): Observable<any[]> {
    const url = `${this.baseUrl}/analysis/${id}?resource=cluster_assignments`;
    return this.http.get<any[]>(url);
  }
}
