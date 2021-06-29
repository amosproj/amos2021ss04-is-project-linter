import { Component, OnInit } from '@angular/core';
import { Config } from '../schemas';
import { StateService } from '../state.service';

@Component({
  selector: 'app-footer',
  templateUrl: './footer.component.html',
  styleUrls: ['./footer.component.css'],
})
export class FooterComponent implements OnInit {
  config: Config;

  constructor(private state: StateService) {}

  ngOnInit() {
    this.state.config.subscribe((data) => {
      this.config = data;
    });
  }
}
