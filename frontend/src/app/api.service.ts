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
import { Status, Project, Config, PagedProjects, Statistics } from './schemas';

@Injectable({
  providedIn: 'root',
})
export class ApiService {
  apiUrl: string = environment.baseURL;
  headers = new HttpHeaders().set('Content-Type', 'application/json');

  constructor(private http: HttpClient) {}

  // Handle Errors -- TODO use it to have better and consistent error messages. (but do we need them?)
  error(error: HttpErrorResponse): Observable<String> {
    let errorMessage: string = '';
    if (error.error instanceof ErrorEvent) {
      errorMessage = error.error.message;
    } else {
      errorMessage = `Error Code: ${error.status}\nMessage: ${error.message}`;
    }
    return throwError(errorMessage);
  }

  // All Project - /projects
  getAllProjects(
    delta: boolean = false,
    query: string = '',
    sort: string[] = [],
    pageSize: number,
    currentPage: number
  ): Observable<PagedProjects> {
    const params = new HttpParams()
      .set('delta', String(delta))
      .set('name', query)
      .set('page', String(currentPage))
      .set('size', String(pageSize))
      .set('sort', String(sort));
    return this.http.get<PagedProjects>(`${this.apiUrl}/projects`, { params });
  }

  // One Project - /project/:id
  getProject(id: number): Observable<Project> {
    return this.http.get<Project>(`${this.apiUrl}/project/${id}`);
  }

  // Start Crawler
  startCrawler(): Observable<any> {
    return this.http.post(`${this.apiUrl}/crawler`, null);
  }

  // Crawler Status
  crawlerStatus(): Observable<Status> {
    return this.http.get<Status>(`${this.apiUrl}/crawler`);
  }

  // Config
  getConfig(): Observable<Config> {
    return this.http.get<Config>(`${this.apiUrl}/config`);
  }

  // Projects By All Tags - /projects/allTags
  getProjectsByAllTags(type: String): Observable<Statistics> {
    const params = new HttpParams().set('type', String(type));
    return this.http.get<Statistics>(`${this.apiUrl}/projects/allTags`, {
      params,
    });
  }

  // Get Projects By top - /projects/top
  getProjectsByTop(type: String): Observable<Statistics> {
    const params = new HttpParams().set('type', String(type));
    return this.http.get<Statistics>(`${this.apiUrl}/projects/top`, {
      params,
    });
  }
}
