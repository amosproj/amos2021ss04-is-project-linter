import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Status, Project, Config, PagedProjects } from './schemas';

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
    query: string,
    sort: string[] = [],
    pageSize: number,
    currentPage: number
  ): Observable<PagedProjects> {
    const params = new HttpParams()
      .set('extended', String(extended))
      .set('delta', String(delta))
      .set('name', query)
      .set('page', String(currentPage))
      .set('size', String(pageSize))
      .set('sort', String(sort));
    console.log(params);

    return this.http.get<PagedProjects>(`${this.apiUrl}/projects`, { params });
  }

  // One Project
  getProject(id: number): Observable<Project> {
    return this.http.get<Project>(`${this.apiUrl}/project/${id}`);
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
