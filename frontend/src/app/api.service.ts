import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import {
  HttpClient,
  HttpHeaders,
  HttpErrorResponse,
} from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';

@Injectable({
  providedIn: 'root',
})
export class ApiService {
  apiUrl: string = environment.baseURL;
  headers = new HttpHeaders().set('Content-Type', 'application/json');

  constructor(private http: HttpClient) {}

  // All Project
  getAllProjects(extended: boolean): Observable<any> {
    return this.http
      .get(`${this.apiUrl}/projects`)
      .pipe(catchError(this.error));
  }

  // One Project
  getProject(id: number): Observable<any> {
    return this.http
      .get(`${this.http}/project/${id}`)
      .pipe(catchError(this.error));
  }

  // Start Crawler
  startCrawler(): Observable<any> {
    return this.http
      .post(`${this.apiUrl}/crawler`, null)
      .pipe(catchError(this.error));
  }

  // Crawler Status
  crawlerStatus(): Observable<any> {
    return this.http.get(`${this.apiUrl}/crawler`).pipe(catchError(this.error));
  }

  // Config
  getConfig(): Observable<any> {
    return this.http.get(`${this.apiUrl}/config`).pipe(catchError(this.error));
  }

  // Handle Errors
  error(error: HttpErrorResponse) {
    let errorMessage = '';
    if (error.error instanceof ErrorEvent) {
      errorMessage = error.error.message;
    } else {
      errorMessage = `Error Code: ${error.status}\nMessage: ${error.message}`;
    }
    console.log(errorMessage);
    return throwError(errorMessage);
  }
}
