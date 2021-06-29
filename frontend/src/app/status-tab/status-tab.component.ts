import { Component, OnInit } from '@angular/core';
import { timer } from 'rxjs';
import { concatMap } from 'rxjs/operators';
import { MatSnackBar } from '@angular/material/snack-bar';

import { ApiService } from '../api.service';
import { Status } from '../schemas';
import { AppComponent } from '../app.component';

@Component({
  selector: 'app-status-tab',
  templateUrl: './status-tab.component.html',
  styleUrls: ['./status-tab.component.css'],
})
export class StatusTabComponent implements OnInit {
  status: Status = {} as Status;

  constructor(private api: ApiService, private _snackBar: MatSnackBar,private appController: AppComponent) {}

  openSnackBar(message: string, action: string) {
    this._snackBar.open(message, action);
  }

  ngOnInit(): void {
    this.appController.selectTab(2);
    timer(1, 2000)
      .pipe(concatMap((_) => this.api.crawlerStatus()))
      .subscribe(
        (res) => {
          this.status = res;
        },
        (error) => {
          console.log(error);
          this.openSnackBar('Fehler beim holen von Crawler Status', 'OK');
        }
      );
  }

  startCrawler(): void {
    this.api.startCrawler().subscribe(
      (res) => {
        console.log(res);
      },
      (error) => {
        console.log(error);
        this.openSnackBar('Fehler beim starten des Crawlers', 'OK');
      }
    );
  }
}
