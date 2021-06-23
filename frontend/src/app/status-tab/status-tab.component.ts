import { Component, OnInit } from '@angular/core';

import { ApiService } from '../api.service';
import { Status } from '../schemas';

@Component({
  selector: 'app-status-tab',
  templateUrl: './status-tab.component.html',
  styleUrls: ['./status-tab.component.css'],
})
export class StatusTabComponent implements OnInit {
  status: Status;

  constructor(private api: ApiService) {}

  ngOnInit(): void {
    console.log('hello from status tab');
    this.api.crawlerStatus().subscribe(
      (res) => {
        this.status = res;
        console.log(res);
      },
      (err) => {
        console.log(err);
      }
    );
  }
}
