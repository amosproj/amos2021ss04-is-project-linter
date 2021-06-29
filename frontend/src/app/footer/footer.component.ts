import { Component, OnInit } from '@angular/core';
import { ApiService } from '../api.service';
import { Config } from '../schemas';

@Component({
  selector: 'app-footer',
  templateUrl: './footer.component.html',
  styleUrls: ['./footer.component.css'],
})
export class FooterComponent implements OnInit {
  constructor(private api: ApiService) {}

  config: Config;

  ngOnInit(): void {
    this.api.getConfig().subscribe((data) => {
      this.config = data;
    });
  }
}
