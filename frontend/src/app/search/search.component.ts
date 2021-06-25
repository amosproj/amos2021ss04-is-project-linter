import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';

import { ApiService } from '../api.service';
import { StateService } from '../state.service';

@Component({
  selector: 'app-search',
  templateUrl: './search.component.html',
  styleUrls: ['./search.component.css'],
})
export class SearchComponent implements OnInit {
  options: FormGroup;
  hideRequiredControl = new FormControl(false);
  floatLabelControl = new FormControl('auto');
  searchQuery: string;

  constructor(
    private api: ApiService,
    private state: StateService,
    fb: FormBuilder
  ) {
    this.options = fb.group({
      hideRequired: this.hideRequiredControl,
      floatLabel: this.floatLabelControl,
    });
  }

  ngOnInit(): void {}

  setSearch(query: string) {
    // add debounce
    this.state.updateSearchQuery(query);
  }
}
