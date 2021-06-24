import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Status } from './schemas';

@Injectable({
  providedIn: 'root',
})
export class ApiService {
  apiUrl: string = environment.baseURL;
  headers = new HttpHeaders().set('Content-Type', 'application/json');

  constructor(private http: HttpClient) {}

  // All Project
  getAllProjects(extended: boolean): Observable<any> {
    return this.http.get(`${this.apiUrl}/projects`);
  }

  // One Project
  getProject(id: number): Observable<any> {
    return this.http.get(`${this.http}/project/${id}`);
  }

  // Start Crawler
  startCrawler(): Observable<string> {
    return this.http.post<string>(`${this.apiUrl}/crawler`, null);
  }

  // Crawler Status
  crawlerStatus(): Observable<Status> {
    return this.http.get<Status>(`${this.apiUrl}/crawler`);
  }

  // Config
  getConfig(): Observable<any> {
    return this.http.get(`${this.apiUrl}/config`);
  }
}
