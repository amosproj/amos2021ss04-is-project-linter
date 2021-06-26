import { Component } from '@angular/core';
import { FormControl } from '@angular/forms';
import { OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';

import { Config, PagedProjects } from '../schemas';
import { SpinnerComponentComponent } from '../spinner-component/spinner-component.component';
import { ApiService } from '../api.service';
import { StateService } from '../state.service';

@Component({
  selector: 'app-projects-tab',
  templateUrl: './projects-tab.component.html',
  styleUrls: ['./projects-tab.component.css'],
})
export class ProjectsTabComponent implements OnInit {
  // for search form
  availableSearchCriteria: string[] = [
    'Bestandene Tests',
    'Neue bestandene Tests in den letzten 30 Tagen',
  ];
  chipsControl = new FormControl('');
  searchCriteria = new FormControl(this.availableSearchCriteria[0]);
  filterInfo = 'Momentan sortiert nach Kategorie: - und Sortierkriterium: -';
  // query, sorting parameters
  delta: boolean = false; // whether the bottom radio button is selected
  searchQuery: string = ''; // query from the search bar
  sort: string[]; // selected chips
  // other params
  chipOptions: string[];
  config: Config;
  projects: PagedProjects = <PagedProjects>{ content: [], totalElements: 0 };

  constructor(
    public dialog: MatDialog,
    private api: ApiService,
    private state: StateService
  ) {}

  ngOnInit(): void {
    // get Config
    this.api.getConfig().subscribe((data) => {
      this.config = data;
      this.getChipOptions();
    });

    this.getProjects();

    // search query
    this.state.searchQuery.subscribe((query) => {
      this.searchQuery = query;
      console.log(query);
    });
  }

  ngAfterViewInit() {
    this.chipsControl.valueChanges.subscribe((data) => {
      this.sort = data;
    });

    this.searchCriteria.valueChanges.subscribe((data) => {
      this.delta = data == this.availableSearchCriteria[1];
    });
  }

  getProjects() {
    // Holt alle Projekte vom Backend-Server
    let dialogRef = this.dialog.open(SpinnerComponentComponent, {
      width: '0px',
      height: '0px',
      panelClass: 'custom-dialog-container',
    });

    this.api
      .getAllProjects(true, this.delta, this.searchQuery, this.sort)
      .subscribe((data) => {
        console.log(data);
        this.projects = data;
      });

    dialogRef.close();
  }

  getChipOptions() {
    //hole alle verschiedenen tags aus der config.json datei
    this.chipOptions = [];
    for (let [key, value] of Object.entries(this.config.checks)) {
      if (!this.chipOptions.includes(value.tag)) {
        this.chipOptions.push(value.tag);
      }
    }
  }
}
