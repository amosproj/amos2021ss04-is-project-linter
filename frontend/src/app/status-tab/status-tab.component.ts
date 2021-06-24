import { Component, OnInit } from '@angular/core';
import { timer } from 'rxjs';
import { concatMap } from 'rxjs/operators';
//import { MatSnackBar } from '@angular/material/snack-bar';

import { ApiService } from '../api.service';
import { Status } from '../schemas';

@Component({
  selector: 'app-status-tab',
  templateUrl: './status-tab.component.html',
  styleUrls: ['./status-tab.component.css'],
})
export class StatusTabComponent implements OnInit {
  status: Status = {} as Status;

  constructor(private api: ApiService) {} //, private _snackBar: MatSnackBar) {}

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
    // FIXME always prints error
    this.api.startCrawler().subscribe((error) => {
      //this._snackBar.open(error, 'ok');
      console.log(error);
    });
  }
}
