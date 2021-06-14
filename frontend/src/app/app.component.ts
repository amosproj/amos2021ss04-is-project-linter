import { ComponentFactoryResolver, Input } from '@angular/core';
import { ViewContainerRef } from '@angular/core';
import { ViewChild } from '@angular/core';
import { Component } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { RepositoryComponent } from './repository/repository.component';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { environment } from 'src/environments/environment';
import { MatChip, MatChipList } from '@angular/material/chips';
import { OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { SpinnerComponentComponent } from './spinner-component/spinner-component.component';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
})
export class AppComponent implements OnInit {
  /***********************************************************
   * Properties
   ***********************************************************/

  title = 'frontend';
  projectComponents = [];
  chipsControl = new FormControl('');
  chipsValue$ = this.chipsControl.valueChanges;
  kategorie = new FormControl('');
  all_projects: Project[];
  init_all_projects: Project[];
  options: FormGroup;
  forwardLinkWorked = true;
  errorMsgForwardLink = '';
  hideRequiredControl = new FormControl(false);
  floatLabelControl = new FormControl('auto');
  searchCriteria = new FormControl('');
  availableSearchCriteria: string[] = [
    'Bestandene Tests',
    'Neue bestandene Tests in den letzten 30 Tagen',
  ];
  chipOptions: string[];
  filterInfo = 'Momentan sortiert nach Kategorie: - und Sortierkriterium: -';
  toggleToTrue = true;
  csvExportLink = environment.baseURL + '/export/csv';
  currentPage: number = 0;
  pages: number;
  config: Config;

  @ViewChild('parent', { read: ViewContainerRef }) container: ViewContainerRef;

  /***********************************************************
   * Init Methods
   ***********************************************************/

  constructor(
    public dialog: MatDialog,
    fb: FormBuilder,
    private _cfr: ComponentFactoryResolver,
    private http: HttpClient
  ) {
    this.options = fb.group({
      hideRequired: this.hideRequiredControl,
      floatLabel: this.floatLabelControl,
    });
  }

  ngOnInit() {
    this.GetProjects();
    this.GetConfig();
  }

  ngAfterViewInit() {}

  /***********************************************************
   * Functions
   ***********************************************************/

  getIfForwardLinkWorked() {
    return this.forwardLinkWorked;
  }

  forwardLink(URL) {
    // Wird aktuell nicht benötigt
    const headers = { 'Content-Type': 'text/html' };

    let HTTPOptions: Object = {
      headers: new HttpHeaders({
        'Content-Type': 'application/json',
      }),
      responseType: 'text',
    };

    this.http
      .post<String>(`${environment.baseURL}/projects`, URL, HTTPOptions)
      .subscribe(
        (val: any) => {
          console.log('POST call successful value returned in body', val);
          var regex404 = new RegExp('404 NOT_FOUND', 'i');
          console.log(val.search(regex404));
          if (val.search(regex404) != -1) {
            this.errorMsgForwardLink = 'Fehler 404, bitte URL überprüfen';
            this.forwardLinkWorked = false;
            console.log(this.forwardLinkWorked);
          } else {
            this.forwardLinkWorked = true;
          }
          console.log(this.forwardLinkWorked);
        },
        (error) => {
          console.log('POST call in error', error);
          this.errorMsgForwardLink = 'Internal server error';
          this.forwardLinkWorked = false;
        }
      );
  }

  removeAllProjectsFromOverview() {
    // Löscht alle angezeigten Projekte
    this.container.clear();
  }

  async GetConfig() {
    await this.http
      .get(`${environment.baseURL}/config`)
      .toPromise()
      .then((results: any) => {
        this.config = results;
        this.getChipOptions();
      });
  }

  async GetProjects() {
    // Holt alle Projekte vom Backend-Server
    let dialogRef = this.dialog.open(SpinnerComponentComponent, {
      width: '100%',
      height: '100%',
      panelClass: 'custom-dialog-container',
    });

    await this.http
      .get(`${environment.baseURL}/projects?extended=true`)
      .toPromise()
      .then((results: any) => {
        this.all_projects = JSON.parse(JSON.stringify(results)) as Project[];
        console.log('projekte', this.all_projects);

        this.pages = Math.floor(this.all_projects.length / 50);

        this.displayProjects();

        this.prepareProjectDataForSorting();
        this.init_all_projects = this.all_projects.slice();
        dialogRef.close();
      }); // momentan kann man nur die URL senden und nicht ein JSON Objekt
  }

  addComponent(project) {
    // Fügt eine Komponente unter dem Tab Repositories hinzu
    var comp = this._cfr.resolveComponentFactory(RepositoryComponent);
    var expComponent = this.container.createComponent(comp);
    expComponent.instance._ref = expComponent;
    expComponent.instance.project = project;
    //Zum Suchen
    this.projectComponents.push(expComponent);
  }

  displayProjects() {
    for (
      var i = 50 * this.currentPage;
      i < 50 * (this.currentPage + 1) && i < this.all_projects.length;
      i++
    ) {
      this.addComponent(this.all_projects[i]);
    }
  }

  pageRight() {
    this.currentPage += 1;
    this.removeAllProjectsFromOverview();
    this.displayProjects();
  }

  pageLeft() {
    this.currentPage -= 1;
    this.removeAllProjectsFromOverview();
    this.displayProjects();
  }

  searchProject(value: string) {
    // Erstellt alle Komponenten im Repostiories Tab
    // TODO: Methoden Benennung ändern
    this.removeAllProjectsFromOverview();

    for (let item of this.projectComponents) {
      if (item.instance.name.startsWith(value) || value === '') {
        var comp = this._cfr.resolveComponentFactory(RepositoryComponent);
        var expComponent = this.container.createComponent(comp);
        expComponent.instance._ref = expComponent;
        expComponent.instance.project = item.instance.project;
      }
    }
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

  prepareProjectDataForSorting() {
    for (var i = 0; i < this.all_projects.length; i++) {
      var checkResults: CheckResults[] =
        this.all_projects[i].lintingResults[
          this.all_projects[i].lintingResults.length - 1
        ].checkResults;
      var checkResultsLastMonth: CheckResults[] =
        this.all_projects[i].lintingResults[0].checkResults;
      //Zähler für erfolgreiche Checks pro Tag
      var checksPassed: number[] = new Array(this.chipOptions.length).fill(0);
      //Zähler für erfolgreiche Checks letzten Monat pro Tag
      var checksPassedLastMonth: number[] = new Array(
        this.chipOptions.length
      ).fill(0);
      for (var j = 0; j < checkResults.length - 1; j++) {
        // wenn der Check erfolgreich war erhöhe die Zähler
        if (checkResults[j].result) {
          checksPassed[this.chipOptions.indexOf(checkResults[j].tag)] += 1;
        }
        if (checkResultsLastMonth[j].result) {
          checksPassedLastMonth[
            this.chipOptions.indexOf(checkResults[j].tag)
          ] += 1;
        }
      }
      var newChecksPassedLastMonth: number[] = new Array(
        this.chipOptions.length
      );
      for (var j = 0; j < this.chipOptions.length; j++) {
        newChecksPassedLastMonth[j] =
          checksPassed[j] - checksPassedLastMonth[j];
      }
      //var info : GridInfo = {project : val.name, testsPassed: checksPassed};
      this.all_projects[i].passedTestsPerTag = checksPassed;
      this.all_projects[i].newPassedTestsPerTagLastMonth =
        newChecksPassedLastMonth;
      this.all_projects[i].passedTestsInFilter = 0;
      this.all_projects[i].newPassedTestsLastMonth = 0;
    }
  }

  sortProjects() {
    //stelle die initale unsortierte Reihenfolge der Projekte wieder her
    this.all_projects = this.init_all_projects.slice();

    //aktualisiere die Filter Info
    this.filterInfo = 'Momentan sortiert nach Kategorie: ';
    for (var i = 0; i < this.chipsControl.value.length; i++) {
      this.filterInfo += this.chipsControl.value[i];
      if (i != this.chipsControl.value.length - 1) {
        this.filterInfo += ', ';
      }
    }
    this.filterInfo += ' und Sortierkriterium: ' + this.searchCriteria.value;

    //setze die notwendigen variablen fürs sortieren der projekte nach den gewählten tags
    for (let project in this.all_projects) {
      this.all_projects[project].passedTestsInFilter = 0;
      this.all_projects[project].newPassedTestsLastMonth = 0;
      for (var i = 0; i < this.chipOptions.length; i++) {
        for (var j = 0; j < this.chipsControl.value.length; j++) {
          if (this.chipsControl.value[j] == this.chipOptions[i]) {
            this.all_projects[project].passedTestsInFilter +=
              this.all_projects[project].passedTestsPerTag[i];
            this.all_projects[project].newPassedTestsLastMonth +=
              this.all_projects[project].newPassedTestsPerTagLastMonth[i];
          }
        }
      }
    }

    //wähle die sortier funktion nach eingabe
    console.log('Projekte vor sortieren!', this.all_projects);
    switch (this.searchCriteria.value) {
      case 'Bestandene Tests': {
        this.all_projects.sort(this.compareTestsPassedPerActivFilter);
        break;
      }
      case 'Neue bestandene Tests in den letzten 30 Tagen': {
        this.all_projects.sort(this.compareNewTestsPassedSinceLastMonthFilter);
        break;
      }
    }
    console.log('Projekte nach sortieren', this.all_projects);

    this.removeAllProjectsFromOverview();

    this.displayProjects();
  }

  compareNewTestsPassedSinceLastMonthFilter(a, b) {
    if (a.newPassedTestsLastMonth < b.newPassedTestsLastMonth) {
      return 1;
    }
    if (a.newPassedTestsLastMonth > b.newPassedTestsLastMonth) {
      return -1;
    }
    return 0;
  }

  compareTestsPassedPerActivFilter(a, b) {
    if (a.passedTestsInFilter < b.passedTestsInFilter) {
      return 1;
    }
    if (a.passedTestsInFilter > b.passedTestsInFilter) {
      return -1;
    }
    return 0;
  }

  toggleSelection(chip: MatChip) {
    chip.toggleSelected();
  }
} // Ende von AppComponent

/***********************************************************
 * Interfaces
 ***********************************************************/

// Interface für die repository Komponente welche grob die Informationen des repository zeigt
interface Project {
  gitlabInstance: string;
  gitlabProjectId: number;
  id: number;
  name: string;
  description: string;
  results: [];
  url: string;
  passedTestsInFilter: number;
  newPassedTestsLastMonth: number;
  passedTestsPerTag: number[];
  newPassedTestsPerTagLastMonth: number[];

  lintingResults: LintingResult[];
}

// Zum speichern der Daten des Projekts
interface CheckResults {
  checkName: string;
  severity: string;
  result: boolean;
  category: string;
  description: string;
  tag: string;
  fix: string;
  priority: number;
  message: string; // ist Fehlermeldung
}

// Zum Speichern der Daten eines LintingResult
interface LintingResult {
  projectId: number;
  id: number;
  lintTime: string;
  checkResults: CheckResults[];
}

interface Config {
  checks: Check[];
}

interface Check {
  enabled: boolean;
  severity: string;
  description: string;
  message: string;
  fix: string;
  priority: number;
  tag: string;
}
