import { Component, OnInit, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Chart } from 'chart.js';
import * as dayjs from 'dayjs';
import { ApiService } from '../api.service';

import { Project, LintingResult, CheckResults } from '../schemas';

@Component({
  selector: 'app-repository-details',
  templateUrl: './repository-details.component.html',
  styleUrls: ['./repository-details.component.css'],
})
export class RepositoryDetailsComponent implements OnInit {
  project: Project;

  emojiMap = {
    notImportant: '🟡',
    warning: '🟠',
    false: '🔴',
    correct: '🟢',
    bug: '🐛',
  };

  getdata = false;
  myChart;
  canvas;
  context;
  lastLintTime;
  RepoName = '';
  RepoURL = '';
  RepoDescription = '';
  checksHighSeverity: CheckResults[]; // wird momentan nicht benützt
  checksMediumSeverity: CheckResults[]; // wird momentan nicht benützt
  checksLowSeverity: CheckResults[]; // wird momentan nicht benützt
  latestLintingIndex: number;
  latestLintingResults: CheckResults[];
  latestLintingResultsSortedPriority: CheckResults[];
  latestLintingResultsFailedChecks: CheckResults[];
  tags: String[];
  LintingResultsInTags: CheckResults[][];
  numberOfTestsPerSeverityInTags: number[][]; // 2D Array der Größe [tags+1, 4], 1 dim = tags, 2te dim [korrekt, hoch, medium, niedrig]
  chartNames: String[]; //wenn neue Tags hinzugefügt werden muss diese Variable erweitert werden
  maxColsForTiles = 9;
  tiles: Tile[] = [
    { text: 'Kategorien', cols: 5, rows: 6, color: 'white' }, // gibt es immer
    { text: 'Ergebnisse Aller Tests', cols: 4, rows: 2, color: 'white' }, // gibt es immer
    // Kacheln die hinzugefügt werden: Doughnut chart pro Tag
  ];
  constructor(
    public dialogRef: MatDialogRef<RepositoryDetailsComponent>,
    @Inject(MAT_DIALOG_DATA) public projectId: number,
    private api: ApiService
  ) {}

  ngOnInit(): void {
    this.api.getProject(this.projectId).subscribe((proj) => {
      this.project = proj;
      // initalisiere Arrays sortiert bei severtity void
      this.latestLintingIndex = this.project.lintingResults.length - 1;
      this.checksHighSeverity = new Array<CheckResults>();
      this.checksMediumSeverity = new Array<CheckResults>();
      this.checksLowSeverity = new Array<CheckResults>();
      this.latestLintingResultsSortedPriority = new Array<CheckResults>();
      this.initializeClassValuesAndTiles(); // sendet erste HTTP Anfrage ans backend
    });
  }

  ngAfterViewInit(): void {
    for (var i = 0; i < this.tags.length + 1; i++) {
      this.renderChart(
        this.chartNames[i],
        i,
        this.numberOfTestsPerSeverityInTags
      );
      this.myChart.update();
    }
  }

  renderChart(chartName, index, numberOfTestsPerSeverityInTags) {
    //rendert eine Chart
    console.log(chartName);
    const canvas = <HTMLCanvasElement>(
      document.getElementById(String(chartName))
    );
    canvas.width = 150;
    canvas.height = 150;
    var ctx = canvas.getContext('2d');

    this.myChart = new Chart(ctx, {
      type: 'doughnut',
      data: {
        labels: [
          'Bestanden',
          'unwichtiger Test nicht bestanden',
          'Test nicht bestanden',
          'wichtiger Test nicht bestanden',
        ],
        datasets: [
          {
            label: 'My First Dataset',
            data: numberOfTestsPerSeverityInTags[index],
            backgroundColor: [
              'rgb(51, 209, 40)', // green
              'rgb(252, 236, 3)', // yellow
              'rgb(252, 169, 3)', // orange
              'rgb(252, 32, 3)', // rot
            ],
            hoverOffset: 4,
          },
        ],
      },
      options: {
        legend: {
          display: false,
        },
        maintainAspectRatio: false,
      },
    });

    this.myChart.update();
  }

  closeDialog() {
    this.dialogRef.close();
  }

  initializeClassValuesAndTiles() {
    // Initialisiert Klassenvariablen die unteranderem für das erstellen der Tiles nötig sind
    // fülle linting Kategorien array
    this.latestLintingResults =
      this.project.lintingResults[this.latestLintingIndex].checkResults;
    //this.fillSeverityArrays(); // muss momentan nicht benützt werden
    this.tags = this.getTagsArray(this.latestLintingResults);

    this.LintingResultsInTags =
      this.groupLintingResultsInTagsAndFillNumTestsPerSeverity(
        this.tags,
        this.latestLintingResults
      )[1];
    //console.log('LintingResultInTags', this.LintingResultsInTags);

    // Speichere Informationen
    this.RepoName = this.project.name;
    this.RepoURL = this.project.url;
    this.RepoDescription = this.project.description;
    this.lastLintTime = dayjs(
      this.project.lintingResults[this.latestLintingIndex].lintTime
    ).format('DD.MM.YYYY - HH:mm');
    // erstelle dynamisch fehlende tiles für die grid Liste korrespondierend zu ihrer grid Liste
    this.numberOfTestsPerSeverityInTags =
      this.groupLintingResultsInTagsAndFillNumTestsPerSeverity(
        this.tags,
        this.project.lintingResults[this.latestLintingIndex].checkResults
      )[0];
    this.chartNames = this.getChartNames(this.tags);
    this.addTilesForCategoryGraphAndTipps();
    // sortiere die Checks um die 3 besten Tipps darzustellen
    this.latestLintingResults.forEach((val) =>
      this.latestLintingResultsSortedPriority.push(Object.assign({}, val))
    );
    this.latestLintingResultsSortedPriority.sort(this.compareCheckResults);
    this.removePassedChecks();
  }

  getTagsArray(latestLintingResults) {
    // Erstellt ein Array aus allen in latestLintingResults enthaltenen Tags
    var tags = [];
    for (var i = 0; i < latestLintingResults.length; i++) {
      var tagAlreadyThere = false;
      // Prüfe ob tags Werte hat
      if (tags) {
        // Prüfe ob Kategorien die momentane Kategorie enthält
        for (var j = 0; j < tags.length; j++) {
          if (tags[j] == latestLintingResults[i].tag) {
            tagAlreadyThere = true;
          }
        }
      } else {
        tags = new Array<String>();
      }
      if (!tagAlreadyThere) {
        tags.push(latestLintingResults[i].tag);
      }
    }
    return tags;
  }

  getChartNames(tags) {
    if (!tags) {
      console.log(
        'Error in getChartNames() in repository-details.components.ts\n  this.tags is empty'
      );
      return [];
    }
    var chartNames: String[] = [];
    chartNames.push('Alle Tests:');
    for (var i = 0; i < tags.length; i++) {
      chartNames.push(tags[i]);
    }
    return chartNames;
  }

  groupLintingResultsInTagsAndFillNumTestsPerSeverity(
    tags,
    latestLintingResults
  ) {
    // Gibt einmal ein Array der Testergebnisse sortiert nach Kategorien (numberOfTestsPerSeverityInTags) zurück, sowie die Tests soriert nach Kategorien(LintingResultsInTags)
    var numberOfTestsPerSeverityInTags = new Array(tags.length + 1)
      .fill(0)
      .map(() => new Array(4).fill(0)); // 2D array der Größe [tags+1, 4], 1 dim = tags, 2te dim [korrekt, niedrig, medium, hoch]
    // Gruppiere die linting Ergebnisse nach deren korrespondierenden tags
    var LintingResultsInTags = new Array(tags.length);
    for (var i = 0; i < latestLintingResults.length; i++) {
      // Hole index des tag in tags
      var index = tags.indexOf(latestLintingResults[i].tag);
      // Check ob der Array initalisiert werden muss
      if (!LintingResultsInTags[index]) {
        LintingResultsInTags[index] = [];
      }
      // Schiebe test in die Kategorie des korrespondierenden index
      LintingResultsInTags[index].push(latestLintingResults[i]);
      numberOfTestsPerSeverityInTags = this.addTestToFillNumTestsPerSeverity(
        index,
        latestLintingResults[i],
        numberOfTestsPerSeverityInTags
      );
      //this.numberOfTestsPerSeverityInTags= numberOfTestsPerSeverityInTags;
    }

    return [numberOfTestsPerSeverityInTags, LintingResultsInTags];
  }

  fillSeverityArrays() {
    // Zurzeit nicht benutzt
    for (var i = 0; i < this.latestLintingResults.length; i++) {
      if (this.latestLintingResults[i].severity == 'HIGH') {
        this.checksHighSeverity.push(this.latestLintingResults[i]);
      } else if (this.latestLintingResults[i].severity == 'MEDIUM') {
        this.checksMediumSeverity.push(this.latestLintingResults[i]);
      } else if (this.latestLintingResults[i].severity == 'LOW') {
        this.checksLowSeverity.push(this.latestLintingResults[i]);
      }
    }
  }

  addTestToFillNumTestsPerSeverity(
    index,
    lintingResult,
    numberOfTestsPerSeverityInTags
  ) {
    // Analysiert das Testergebnis
    index = index + 1; // da die erste Spalte für die Statistik der gesamten Checks unabhängig von den tags ist
    if (lintingResult.result) {
      numberOfTestsPerSeverityInTags[index][0] += 1;
      numberOfTestsPerSeverityInTags[0][0] += 1;
    } else if (lintingResult.severity == 'LOW') {
      numberOfTestsPerSeverityInTags[index][1] += 1;
      numberOfTestsPerSeverityInTags[0][1] += 1;
    } else if (lintingResult.severity == 'MEDIUM') {
      numberOfTestsPerSeverityInTags[index][2] += 1;
      numberOfTestsPerSeverityInTags[0][2] += 1;
    } else if (lintingResult.severity == 'HIGH') {
      numberOfTestsPerSeverityInTags[index][3] += 1;
      numberOfTestsPerSeverityInTags[0][3] += 1;
    } else {
      console.log(
        'In Repository Details component: addTestToFillNumTestsPerSeverity() found test which does not have a severity label!'
      );
    }
    return numberOfTestsPerSeverityInTags;
  }

  addTilesForCategoryGraphAndTipps() {
    // Erstellt zusätzliche Tiles
    for (var i = 0; i < this.tags.length; i++) {
      var t = <Tile>{ color: 'white', cols: 2, rows: 2, text: this.tags[i] };
      this.tiles.push(t);
    }
    this.tiles.push(<Tile>{
      color: 'white',
      cols: 4, // set to 4 (maxColsForTiles should not change)
      rows: 1,
      text: 'Informationen',
    });
    this.tiles.push(<Tile>{
      color: 'white',
      cols: 5, // set to 5 (maxColsForTiles should not change)
      rows: 1,
      text: 'Top 3 Tipps',
    });
  }

  returnEmojiBasedOnSeverity(input) {
    // Benutzt die Emojimap
    if (input.result) return this.emojiMap.correct;
    else if (input.severity == 'HIGH') return this.emojiMap.false;
    else if (input.severity == 'MEDIUM') return this.emojiMap.warning;
    else if (input.severity == 'LOW') return this.emojiMap.notImportant;
    else return this.emojiMap.bug;
  }

  compareCheckResults(a, b) {
    if (a.priority < b.priority) {
      return -1;
    }
    if (a.priority > b.priority) {
      return 1;
    }
    return 0;
  }

  removePassedChecks() {
    console.log(this.tiles);
    this.latestLintingResultsFailedChecks = new Array();
    for (var i = 0; i < this.latestLintingResultsSortedPriority.length; i++) {
      if (!this.latestLintingResultsSortedPriority[i].result) {
        this.latestLintingResultsFailedChecks.push(
          this.latestLintingResultsSortedPriority[i]
        );
      }
    }
    if (this.latestLintingResultsFailedChecks.length == 0) {
      this.tiles[this.tiles.length - 1].text =
        'Glückwunsch! Alle Tests bestanden.';
    } else if (this.latestLintingResultsFailedChecks.length < 2) {
      this.tiles[this.tiles.length - 1].text = 'Top Tipp';
    } else if (this.latestLintingResultsFailedChecks.length < 3) {
      this.tiles[this.tiles.length - 1].text =
        'Top ' + this.latestLintingResultsFailedChecks.length + ' Tipps';
    }
  }

  fixOverflowingDescription() {
    return this.RepoDescription.substring(0, 150) + '...';
  }
}

// FÜr angular tiles
export interface Tile {
  color: string;
  cols: number;
  rows: number;
  text: string;
}
