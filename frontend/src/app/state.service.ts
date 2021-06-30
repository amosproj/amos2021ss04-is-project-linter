import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { share } from 'rxjs/operators';
import { ApiService } from './api.service';
import { Config } from './schemas';

@Injectable({
  providedIn: 'root',
})
export class StateService {
  private searchQuerySource = new BehaviorSubject<string>('');
  searchQuery: Observable<string> = this.searchQuerySource
    .asObservable()
    .pipe(share());
  config: Observable<Config>;

  constructor(private api: ApiService) {}

  loadConfig() {
    this.config = this.api.getConfig().pipe(share());
  }

  updateSearchQuery(query: string) {
    this.searchQuerySource.next(query);
  }

  getTags(config: Config): String[] {
    let tags: String[] = [];
    for (let [key, value] of Object.entries(config.checks)) {
      if (!tags.includes(value.tag)) {
        tags.push(value.tag);
      }
    }
    return tags;
  }
}
