import { Component } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';

import { Project, Config, CheckResults } from '../schemas';
import { SpinnerComponentComponent } from '../spinner-component/spinner-component.component';
import { ApiService } from '../api.service';
import { StateService } from '../state.service';

@Component({
  selector: 'app-projects-tab',
  templateUrl: './projects-tab.component.html',
  styleUrls: ['./projects-tab.component.css'],
})
export class ProjectsTabComponent implements OnInit {
  title = 'frontend';
  projectComponents = [];
  chipsControl = new FormControl('');
  chipsValue$ = this.chipsControl.valueChanges;
  kategorie = new FormControl('');
  all_projects: Project[];
  init_all_projects: Project[];
  forwardLinkWorked = true;
  errorMsgForwardLink = '';
  searchCriteria = new FormControl('');
  availableSearchCriteria: string[] = [
    'Bestandene Tests',
    'Neue bestandene Tests in den letzten 30 Tagen',
  ];
  chipOptions: string[];
  filterInfo = 'Momentan sortiert nach Kategorie: - und Sortierkriterium: -';
  toggleToTrue = true;

  config: Config;
  suchBegriff: string;
  projects: Project[];
  searchQuery: string = '';

  constructor(
    public dialog: MatDialog,
    private http: HttpClient,
    private api: ApiService,
    private state: StateService
  ) {}

  ngOnInit(): void {
    // get Config
    this.api.getConfig().subscribe((data) => {
      this.config = data;
    });

    this.getProjects();

    // search query
    this.state.searchQuery.subscribe((query) => {
      this.searchQuery = query;
      console.log(query);
    });
  }

  getProjects() {
    // Holt alle Projekte vom Backend-Server
    let dialogRef = this.dialog.open(SpinnerComponentComponent, {
      width: '0px',
      height: '0px',
      panelClass: 'custom-dialog-container',
    });

    this.api.getAllProjects(false, true, this.searchQuery).subscribe((data) => {
      console.log(data);
      this.projects = data;
    });

    dialogRef.close();
  }
}
