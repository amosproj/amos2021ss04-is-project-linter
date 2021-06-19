import { ComponentFactoryResolver } from '@angular/core';
import { ViewContainerRef } from '@angular/core';
import { ViewChild } from '@angular/core';
import { Component } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { OnInit } from '@angular/core';
import { MatChip } from '@angular/material/chips';
import { MatDialog } from '@angular/material/dialog';

import { environment } from 'src/environments/environment';
import { RepositoryComponent } from './repository/repository.component';
import { SpinnerComponentComponent } from './spinner-component/spinner-component.component';
import { Chart } from 'chart.js';
import { Project, Config, CheckResults, LintingResult } from './schemas';
import { ApiService } from './api.service';

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

  chartImportantChecks;
  dataImportantChecks;
  canvasImportantChecks;
  chartImportantChecksPercentage;
  dataImportantChecksPercentage;
  canvasImportantChecksPercentage;
  chartCheckPerCategorie;
  dataCheckPerCategorie;
  canvasCheckPerCategorie;
  chartCheckPerCategoriePercentage;
  dataCheckPerCategoriePercentage;
  canvasCheckPerCategoriePercantage;
  chartOptionsTotal;
  chartOptionsPercentage;

  chartcolors = {
    red: 'rgb(255, 99, 132)',
    orange: 'rgb(255, 159, 64)',
    yellow: 'rgb(255, 205, 86)',
    green: 'rgb(75, 192, 192)',
    blue: 'rgb(54, 162, 235)',
    purple: 'rgb(153, 102, 255)',
    grey: 'rgb(231,233,237)',
  };

  suchBegriff: string;
  @ViewChild('parent', { read: ViewContainerRef }) container: ViewContainerRef;

  /***********************************************************
   * Init Methods
   ***********************************************************/

  constructor(
    public dialog: MatDialog,
    fb: FormBuilder,
    private _cfr: ComponentFactoryResolver,
    private http: HttpClient,
    private apiService: ApiService
  ) {
    this.options = fb.group({
      hideRequired: this.hideRequiredControl,
      floatLabel: this.floatLabelControl,
    });
  }

  ngOnInit() {
    this.GetConfig();
    this.GetProjects();
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
        console.log(this.all_projects);
        this.pages = Math.floor(this.all_projects.length / 50);

        this.displayProjects();
      }); // momentan kann man nur die URL senden und nicht ein JSON Objekte
    this.prepareProjectDataForSorting();
    this.init_all_projects = this.all_projects.slice();
    dialogRef.close();
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
    if (this.currentPage == this.pages || this.suchBegriff != '') {
      return;
    } else {
      this.currentPage += 1;
      this.removeAllProjectsFromOverview();
      this.displayProjects();
    }
  }

  pageLeft() {
    if (this.currentPage == 0 || this.suchBegriff != '') {
      return;
    } else {
      this.currentPage -= 1;
      this.removeAllProjectsFromOverview();
      this.displayProjects();
    }
  }

  searchProject(value: string) {
    // Erstellt alle Komponenten im Repostiories Tab
    // TODO: Methoden Benennung ändern
    this.removeAllProjectsFromOverview();

    for (let item of this.all_projects) {
      if (
        item.name.toUpperCase().startsWith(value.toUpperCase()) ||
        value === ''
      ) {
        this.addComponent(item);
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

  setChartData() {
    this.dataImportantChecks = {
      labels: [
        '01.01.2020',
        '02.01.2020',
        '03.01.2020',
        '04.01.2020',
        '05.01.2020',
        '06.01.2020',
        '07.01.2020',
      ],
      datasets: [
        {
          label: 'Top 5',
          data: [10, 11, 11, 11, 12, 14, 14],
          backgroundcolor: this.chartcolors.red,
          bordercolor: this.chartcolors.red,
        },
        {
          label: 'Top 10',
          data: [7, 7, 7, 7, 8, 8, 9],
          backgroundcolor: this.chartcolors.blue,
          bordercolor: this.chartcolors.blue,
        },
      ],
    };

    this.dataImportantChecksPercentage = {
      labels: [
        '01.01.2020',
        '02.01.2020',
        '03.01.2020',
        '04.01.2020',
        '05.01.2020',
        '06.01.2020',
        '07.01.2020',
      ],
      datasets: [
        {
          label: 'Top 5',
          data: [20, 22, 22, 22, 24, 28, 28],
          fill: false,
          backgroundcolor: 'rgb(255, 99, 132)',
          bordercolor: 'rgb(255, 99, 132)',
          color: 'rgb(255, 99, 132)',
        },
      ],
    };

    this.dataCheckPerCategorie = {
      labels: ['0d', '10d', '20d', '30d', '40d', '50d', '60d'],
      datasets: [
        {
          label: 'Car Color',
          data: [0, 10, 5, 50, 20, 70, 45],
        },
      ],
    };

    this.dataCheckPerCategoriePercentage = {
      labels: ['0d', '10d', '20d', '30d', '40d', '50d', '60d'],
      datasets: [
        {
          label: 'Car Cost',
          data: [0, 100, 200, 50, 150, 70, 220],
          fill: false,
        },
      ],
    };

    this.chartOptionsTotal = {
      responsive: true,
      legend: {
        position: 'right',
        display: true,
      },
    };

    this.chartOptionsPercentage = {
      responsive: true,
      legend: {
        position: 'right',
        display: true,
      },
      /*
      scales:{
        y:{
          ticks:{
            callback: function(value, index) {
              return value + '%';
            }
          }
        }
      }
      */
    };
  }

  setOnStatistikTab() {
    this.setChartData();
    this.renderStatistikCharts();
  }

  renderStatistikCharts() {
    this.canvasImportantChecks = <HTMLCanvasElement>(
      document.getElementById('importantChecks')
    );

    this.chartImportantChecks = new Chart(
      this.canvasImportantChecks.getContext('2d'),
      {
        type: 'line',
        data: this.dataImportantChecks,
        options: this.chartOptionsTotal,
      }
    );

    this.chartImportantChecks.update();

    this.canvasImportantChecksPercentage = <HTMLCanvasElement>(
      document.getElementById('importantChecksPercentage')
    );

    this.chartImportantChecksPercentage = new Chart(
      this.canvasImportantChecksPercentage.getContext('2d'),
      {
        type: 'line',
        data: this.dataImportantChecksPercentage,
        options: this.chartOptionsPercentage,
      }
    );

    this.chartImportantChecksPercentage.update();

    this.canvasCheckPerCategorie = <HTMLCanvasElement>(
      document.getElementById('checksPerCategorie')
    );

    this.chartCheckPerCategorie = new Chart(
      this.canvasCheckPerCategorie.getContext('2d'),
      {
        type: 'line',
        data: this.dataCheckPerCategorie,
        options: this.chartOptionsTotal,
      }
    );

    this.chartCheckPerCategorie.update();

    this.canvasCheckPerCategoriePercantage = <HTMLCanvasElement>(
      document.getElementById('checksPerCategoriePercentage')
    );

    this.chartCheckPerCategoriePercentage = new Chart(
      this.canvasCheckPerCategoriePercantage.getContext('2d'),
      {
        type: 'line',
        data: this.dataCheckPerCategoriePercentage,
        options: this.chartOptionsPercentage,
      }
    );

    this.chartCheckPerCategoriePercentage.update();
  }

  toggleSelection(chip: MatChip) {
    chip.toggleSelected();
  }
}
