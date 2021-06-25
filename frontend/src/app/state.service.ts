import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class StateService {
  private searchQuerySource = new BehaviorSubject<string>('');
  searchQuery: Observable<string> = this.searchQuerySource.asObservable();

  constructor() {}

  updateSearchQuery(query: string) {
    this.searchQuerySource.next(query);
  }
}
