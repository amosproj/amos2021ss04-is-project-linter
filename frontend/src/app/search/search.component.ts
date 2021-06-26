import { Component, OnInit } from '@angular/core';
import { FormControl } from '@angular/forms';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';

import { ApiService } from '../api.service';
import { StateService } from '../state.service';

@Component({
  selector: 'app-search',
  templateUrl: './search.component.html',
  styleUrls: ['./search.component.css'],
})
export class SearchComponent implements OnInit {
  searchQuery: FormControl = new FormControl('');

  constructor(private api: ApiService, private state: StateService) {}

  ngOnInit(): void {
    this.searchQuery.valueChanges
      .pipe(debounceTime(500), distinctUntilChanged())
      .subscribe((res) => {
        this.setSearch(res);
      });
  }

  setSearch(query: string) {
    this.state.updateSearchQuery(query);
  }
}
