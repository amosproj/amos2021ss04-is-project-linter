import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { share } from 'rxjs/operators';
import { ApiService } from './api.service';
import { Config } from './schemas';

@Injectable({
  providedIn: 'root',
})
export class StateService {
  config: Observable<Config>;

  constructor(private api: ApiService) {}

  loadConfig() {
    this.config = this.api.getConfig().pipe(share());
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
