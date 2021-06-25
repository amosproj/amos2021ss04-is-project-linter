import { ViewChild } from '@angular/core';
import { Component } from '@angular/core';
import { OnInit } from '@angular/core';

import { ProjectsTabComponent } from './projects-tab/projects-tab.component';
import { StatisticsTabComponent } from './statistics-tab/statistics-tab.component';
import { Router } from '@angular/router';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
})
export class AppComponent implements OnInit {
  @ViewChild(ProjectsTabComponent) projectsTab: ProjectsTabComponent;
  @ViewChild(StatisticsTabComponent) statisticsTab: StatisticsTabComponent;

  constructor(private router: Router) {}

  ngOnInit() {
    this.router.navigate(['projects']);
  }

  ngAfterViewInit() {}

  checkTabChange($event) {
    if ($event.index == 0) {
      //Wechsel to Statistics-Tab
      this.router.navigate(['projects']);
    }
    if ($event.index == 1) {
      //Wechsel to Statistics-Tab
      this.router.navigate(['statistics']);
    }
    if ($event.index == 2) {
      //Wechsel to Statistics-Tab
      this.router.navigate(['status']);
    }
  }
}
