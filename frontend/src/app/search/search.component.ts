import { Component, ViewChild } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { ProjectsTabComponent } from '../projects-tab/projects-tab.component';
import { StatisticsTabComponent } from '../statistics-tab/statistics-tab.component';

@Component({
  selector: 'app-search',
  templateUrl: './search.component.html',
  styleUrls: ['./search.component.css'],
})
export class SearchComponent {
  constructor(fb: FormBuilder) {
    this.options = fb.group({
      hideRequired: this.hideRequiredControl,
      floatLabel: this.floatLabelControl,
    });
  }

  @ViewChild(ProjectsTabComponent) projectsTab: ProjectsTabComponent;
  @ViewChild(StatisticsTabComponent) statisticsTab: StatisticsTabComponent;

  options: FormGroup;

  hideRequiredControl = new FormControl(false);
  floatLabelControl = new FormControl('auto');
  suchBegriff;
}
