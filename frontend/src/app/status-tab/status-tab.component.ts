import { Component, OnInit } from '@angular/core';
import { Observable, timer, Subscription, Subject } from 'rxjs';
import {
  switchMap,
  startWith,
  tap,
  share,
  retry,
  takeUntil,
  concatMap,
  map,
} from 'rxjs/operators';

import { ApiService } from '../api.service';
import { Status } from '../schemas';

@Component({
  selector: 'app-status-tab',
  templateUrl: './status-tab.component.html',
  styleUrls: ['./status-tab.component.css'],
})
export class StatusTabComponent implements OnInit {
  status: Status = {} as Status;

  constructor(private api: ApiService) {}

  ngOnInit(): void {
    console.log('hello from status tab');

    timer(1, 3000)
      .pipe(concatMap((_) => this.api.crawlerStatus()))
      .subscribe(
        (res) => {
          this.status = res;
        },
        (error) => {
          console.log(error);
        }
      );
  }

  startCrawler(): void {
    this.api.startCrawler().subscribe((error) => {
      console.log(error);
    });
  }
}
