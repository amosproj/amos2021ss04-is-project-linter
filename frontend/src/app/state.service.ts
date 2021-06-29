import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { ApiService } from './api.service';
import { Config } from './schemas';

@Injectable({
  providedIn: 'root',
})
export class StateService {
  private searchQuerySource = new BehaviorSubject<string>('');
  searchQuery: Observable<string> = this.searchQuerySource.asObservable();

  config: Observable<Config>;

  constructor(private api: ApiService) {}

  loadConfig() {
    this.config = this.api.getConfig();
  }

  updateSearchQuery(query: string) {
    this.searchQuerySource.next(query);
  }
}
