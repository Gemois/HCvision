import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Observable} from "rxjs";
import {Dataset} from "../models/Dataset";
import {DatasetResponse} from "../models/DatasetResponse";

@Injectable({
  providedIn: 'root'
})
export class DatasetService {

  private baseUrl = 'http://localhost:8080/api/v1/datasets';

  constructor(private http: HttpClient) {
  }

  readDataset(dataset: Dataset): Observable<DatasetResponse> {
    const url = `${this.baseUrl}/read?dataset=${dataset.dataset}&access_type=${dataset.access_type}`;
    return this.http.get<DatasetResponse>(url);
  }

  getDatasetList(): Observable<Dataset[]> {
    const url = `${this.baseUrl}`;
    return this.http.get<Dataset[]>(url);
  }

  uploadDataset(file: File, accessType: String): Observable<any> {
    const formData = new FormData();
    formData.append('file', file);
    // @ts-ignore
    formData.append('access_type', accessType);
    const url = `${this.baseUrl}/upload`;
    return this.http.post(url, formData);
  }

  deleteDataset(dataset: Dataset): Observable<void> {
    const url = `${this.baseUrl}/delete?dataset=${dataset.dataset}&access_type=${dataset.access_type}`;
    return this.http.delete<void>(url);
  }


  downloadDataset(dataset: Dataset): Observable<Blob> {
    const url = `${this.baseUrl}/download?dataset=${dataset.dataset}&access_type=${dataset.access_type}`;
    return this.http.get(url, {responseType: 'blob'});
  }

}
