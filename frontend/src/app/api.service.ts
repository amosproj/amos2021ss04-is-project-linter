import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Status, Project, Config } from './schemas';

@Injectable({
  providedIn: 'root',
})
export class ApiService {
  apiUrl: string = environment.baseURL;
  headers = new HttpHeaders().set('Content-Type', 'application/json');

  constructor(private http: HttpClient) {}

  // All Project
  getAllProjects(
    extended: boolean = false,
    delta: boolean = false,
    query: string
  ): Observable<any> {
    const params = new HttpParams()
      .set('extended', String(extended))
      .set('delta', String(delta))
      .set('name', query)
      .set('page', String(0))
      .set('size', String(20));

    return this.http.get<Project[]>(`${this.apiUrl}/projects`, { params });
  }

  // One Project
  getProject(id: number): Observable<any> {
    return this.http.get(`${this.apiUrl}/project/${id}`);
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
  getConfig(): Observable<Config> {
    return this.http.get<Config>(`${this.apiUrl}/config`);
  }
}
