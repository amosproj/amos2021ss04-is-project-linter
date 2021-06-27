import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import {
  HttpClient,
  HttpHeaders,
  HttpParams,
  HttpErrorResponse,
} from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { Status, Project, Config, PagedProjects } from './schemas';

@Injectable({
  providedIn: 'root',
})
export class ApiService {
  apiUrl: string = environment.baseURL;
  headers = new HttpHeaders().set('Content-Type', 'application/json');

  constructor(private http: HttpClient) {}

  // Handle Errors
  error(error: HttpErrorResponse): Observable<String> {
    let errorMessage: string = '';
    if (error.error instanceof ErrorEvent) {
      errorMessage = error.error.message;
    } else {
      errorMessage = `Error Code: ${error.status}\nMessage: ${error.message}`;
    }
    return throwError(errorMessage);
  }

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
    return this.http.get<PagedProjects>(`${this.apiUrl}/projects`, { params });
  }

  // One Project
  getProject(id: number): Observable<Project> {
    return this.http.get<Project>(`${this.apiUrl}/project/${id}`);
  }

  // Start Crawler
  startCrawler(): Observable<String> {
    return this.http.post<String>(`${this.apiUrl}/crawler`, null);
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
