import { Component, OnInit, OnDestroy } from '@angular/core';
import { Subject, timer } from 'rxjs';
import { concatMap, share, takeUntil } from 'rxjs/operators';
import { MatSnackBar } from '@angular/material/snack-bar';

import { ApiService } from '../api.service';
import { Status } from '../schemas';

@Component({
  selector: 'app-status-tab',
  templateUrl: './status-tab.component.html',
  styleUrls: ['./status-tab.component.css'],
})
export class StatusTabComponent implements OnInit, OnDestroy {
  status: Status = {} as Status;
  private stopPolling = new Subject();

  constructor(private api: ApiService, private _snackBar: MatSnackBar) {}

  openSnackBar(message: string, action: string) {
    this._snackBar.open(message, action);
  }

  ngOnInit(): void {
    timer(1, 2000)
      .pipe(
        concatMap((_) => this.api.crawlerStatus()),
        share(),
        takeUntil(this.stopPolling)
      )
      .subscribe(
        (res) => {
          this.status = res;
        },
        (error) => {
          this.openSnackBar('Fehler beim holen von Crawler Status', 'OK');
        }
      );
  }

  ngOnDestroy() {
    this.stopPolling.next();
  }

  startCrawler(): void {
    this.api.startCrawler().subscribe(
      (res) => {
        console.log(res);
      },
      (error) => {
        this.openSnackBar('Fehler beim starten des Crawlers', 'OK');
      }
    );
  }
}
