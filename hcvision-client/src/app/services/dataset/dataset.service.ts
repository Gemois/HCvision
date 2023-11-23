import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Observable} from "rxjs";
import {Dataset} from "../../models/Dataset";
import {AuthService} from "../auth/auth.service";
import {DatasetResponse} from "../../models/DatasetResponse";

@Injectable({
  providedIn: 'root'
})
export class DatasetService {

  private baseUrl = 'http://localhost:8080/api/v1/datasets';

  constructor(private http: HttpClient,
              private authService: AuthService) {
  }

  getDataset(dataset: Dataset): Observable<DatasetResponse> {
    const url = `${this.baseUrl}/read?dataset=${dataset.dataset}&access_type=${dataset.access_type}`;
    const headers = this.createHeaders();
    return this.http.get<DatasetResponse>(url, {headers});
  }

  getDatasets(): Observable<Dataset[]> {
    const url = `${this.baseUrl}`;
    const headers = this.createHeaders();
    return this.http.get<Dataset[]>(url, {headers});
  }

  uploadFile(file: File, accessType: String): Observable<any> {
    const formData = new FormData();
    formData.append('file', file);
    // @ts-ignore
    formData.append('access_type', accessType);
    const url = `${this.baseUrl}/upload`;
    const headers = this.createHeaders();
    return this.http.post(url, formData, {headers});
  }

  deleteDataset(datasetId: string): Observable<void> {
    const url = `${this.baseUrl}/delete?datasetId=${datasetId}`;
    return this.http.delete<void>(url);
  }

  private createHeaders(): HttpHeaders {
    const token = this.authService.getToken();
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });
  }
}
