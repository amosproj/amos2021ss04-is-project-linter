import { ComponentFactoryResolver } from '@angular/core';
import { ViewContainerRef } from '@angular/core';
import { ViewChild } from '@angular/core';
import { Component } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { OnInit } from '@angular/core';

import { ProjectsTabComponent } from './projects-tab/projects-tab.component';
import { StatisticsTabComponent } from './statistics-tab/statistics-tab.component';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
})
export class AppComponent implements OnInit {
  /***********************************************************
   * Properties
   ***********************************************************/

  @ViewChild(ProjectsTabComponent) projectsTab: ProjectsTabComponent;
  @ViewChild(StatisticsTabComponent) statisticsTab: StatisticsTabComponent;

  options: FormGroup;

  hideRequiredControl = new FormControl(false);
  floatLabelControl = new FormControl('auto');

  suchBegriff;

  /***********************************************************
   * Init Methods
   ***********************************************************/

  constructor(private http: HttpClient, fb: FormBuilder) {
    this.options = fb.group({
      hideRequired: this.hideRequiredControl,
      floatLabel: this.floatLabelControl,
    });
  }

  ngOnInit() {}

  ngAfterViewInit() {}

  checkTabChange($event) {
    if ($event.index == 1) {
      //Wechsel to Statistics-Tab
      this.statisticsTab.setOnStatistikTab();
    }
  }
}
