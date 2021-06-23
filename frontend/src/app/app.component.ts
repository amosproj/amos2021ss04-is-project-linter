import { Component, ViewChild } from '@angular/core';
import { StatisticsTabComponent } from './statistics-tab/statistics-tab.component';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
})
export class AppComponent {
  @ViewChild(StatisticsTabComponent) statisticsTab: StatisticsTabComponent;

  constructor() {}

  checkTabChange($event) {
    if ($event.index == 1) {
      //Wechsel to Statistics-Tab
      this.statisticsTab.setOnStatistikTab();
    }
  }
}
