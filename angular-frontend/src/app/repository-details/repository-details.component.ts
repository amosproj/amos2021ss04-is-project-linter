import { HttpClient } from '@angular/common/http';
import { Component, OnInit, Inject } from '@angular/core';
import {
  MatDialogModule,
  MatDialogRef,
  MAT_DIALOG_DATA,
} from '@angular/material/dialog';
import { Chart } from '../../../node_modules/chart.js';
import * as dayjs from 'dayjs';
import { environment } from 'src/environments/environment';

@Component({
  selector: 'app-repository-details',
  templateUrl: './repository-details.component.html',
  styleUrls: ['./repository-details.component.css'],
})
export class RepositoryDetailsComponent implements OnInit {
  // Diese Klasse ist nötig fürs Anzeigen des Dialogs
  // Aktuell muss man die gleichen Informationen 2-mal getten, da die HTTP-get Aufrufe asynchron sind.
  //      (1 mal onNGinit für die Erstellung der Tiles (geht nicht später), und 1 mal onngAfterView für die Graphen (Canvas ist davor undefined))

  // TODO: chartNames nicht dynamisch erstellen

  emojiMap = {
    /*unwichtig:"〰️",
    warning: "⚠️",
    false: "❌",
    correct: "✅"*/
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
  checksHighSeverity: CheckResults[];   // wird momentan nicht benützt
  checksMediumSeverity: CheckResults[]; // wird momentan nicht benützt
  checksLowSeverity: CheckResults[];    // wird momentan nicht benützt
  latestLintingResults: CheckResults[];
  tags: String[];
  LintingResultsInTags: CheckResults[][];
  numberOfTestsPerSeverityInTags: number[][]; // 2D Array der Größe [tags+1, 4], 1 dim = tags, 2te dim [korrekt, hoch, medium, niedrig]
  //wenn neue Tags hinzugefügt werden muss folgende Variable erweitert werden
  chartNames : string[] = ['totalChart', 'userChart', 'programmerChart' ];
  maxColsForTiles = 9;
  tiles: Tile[] = [
    { text: 'Kategorien', cols: 5, rows: 6, color: 'white' },             // gibt es immer
    { text: 'Ergebnisse Aller Tests', cols: 4, rows: 2, color: 'white' }, // gibt es immer
                                                                          // Kacheln die hinzugefügt werden: Doughnut chart pro Tag
  ];
  constructor(
    public dialogRef: MatDialogRef<RepositoryDetailsComponent>,
    @Inject(MAT_DIALOG_DATA) public data: DialogData,
    private http: HttpClient
  ) {}

  ngOnInit(): void{
    // initalisiere Arrays sortiert bei severtity void
    this.checksHighSeverity = new Array<CheckResults>();
    this.checksMediumSeverity = new Array<CheckResults>();
    this.checksLowSeverity = new Array<CheckResults>();
    this.ShowProjectDetails(this.data.projectID); // sendet erste HTTP Anfrage ans backend
  }
 
  ngAfterViewInit(): void {
    // sendet zweite Http Anfrage um Daten für die Charts zu bekommen 
    this.http.get(`${environment.baseURL}/project/${this.data.projectID}`).subscribe(
      (val: any) => {
        var tags = this.getTagsArray(val.lintingResults[val.lintingResults.length - 1].checkResults);
        this.numberOfTestsPerSeverityInTags = this.groupLintingResultsInTagsAndFillNumTestsPerSeverity(tags, val.lintingResults[val.lintingResults.length - 1].checkResults)[0];
        console.log('in after', this.numberOfTestsPerSeverityInTags);
        for(var i = 0 ; i < this.tags.length + 1; i++){
          this.renderChart(i, this.numberOfTestsPerSeverityInTags);
          this.myChart.update();
        }
      }
    );

  }

  renderChart(index, numberOfTestsPerSeverityInTags) {
    //rendert eine Chart
    const canvas = <HTMLCanvasElement>document.getElementById(this.chartNames[index]);
    canvas.width = 150;
    canvas.height = 150;
    var ctx = canvas.getContext('2d');
   
      this.myChart = new Chart(ctx, {
        type: 'doughnut',
        data: {
          labels: ['Bestanden',  'unwichtiger Test nicht bestanden', 'Test nicht bestanden', 'wichtiger Test nicht bestanden'],
          datasets: [
            {
              label: 'My First Dataset',
              data: numberOfTestsPerSeverityInTags[index],
              backgroundColor: [
                'rgb(51, 209, 40)',  // green
                'rgb(252, 236, 3)', // yellow
                'rgb(252, 169, 3)', // orange
                'rgb(252, 32, 3)',  // rot
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

  ShowProjectDetails(gitID) {
    // Initialisiert Klassenvariablen die unteranderem für das erstellen der Tiles nötig sind
    this.http.get(`${environment.baseURL}/project/${gitID}`).subscribe(
      (val: any) => {
        console.log('GET call successful value returned in body', val);
        // fülle linting Kategorien array
        var last_entry = val.lintingResults.length;
        this.latestLintingResults =
         val.lintingResults[last_entry - 1].checkResults;
        //this.fillSeverityArrays(); // muss momentan nicht benützt werden
        this.tags = this.getTagsArray(this.latestLintingResults);

        this.LintingResultsInTags = this.groupLintingResultsInTagsAndFillNumTestsPerSeverity(this.tags, this.latestLintingResults)[1];
        console.log('LintingResultInTags', this.LintingResultsInTags);

        // Speichere Informationen
        this.RepoName = val.name;
        this.RepoURL = val.url;
        this.lastLintTime = dayjs(
          val.lintingResults[last_entry - 1].lintTime
        ).format('DD.MM.YYYY - H:mm');
        // erstelle dynamisch fehlende tiles für die grid Liste korrespondierend zu ihrer grid Liste 
        this.addTilesForCategoryGraph();
      });
  }

  getTagsArray(latestLintingResults){
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

  groupLintingResultsInTagsAndFillNumTestsPerSeverity(tags, latestLintingResults) {
    // Gibt einmal ein Array der Testergebnisse sortiert nach Kategorien (numberOfTestsPerSeverityInTags) zurück, sowie die Tests soriert nach Kategorien(LintingResultsInTags)
    var numberOfTestsPerSeverityInTags = new Array(tags.length + 1).fill(0).map(() => new Array(4).fill(0)); // 2D array der Größe [tags+1, 4], 1 dim = tags, 2te dim [korrekt, niedrig, medium, hoch]
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
      numberOfTestsPerSeverityInTags = this.addTestToFillNumTestsPerSeverity(index, latestLintingResults[i], numberOfTestsPerSeverityInTags);
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

  addTestToFillNumTestsPerSeverity(index, lintingResult ,numberOfTestsPerSeverityInTags) {
    // Analysiert das Testergebnis
    index = index + 1 // da die erste Spalte für die Statistik der gesamten Checks unabhängig von den tags ist 
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

  addTilesForCategoryGraph() {
    // Erstellt zusätzliche Tiles
    for (var i = 0; i < this.tags.length; i++) {
      var t = <Tile>{ color: 'white', cols: 2, rows: 2, text: this.tags[i] };
      this.tiles.push(t);
    }
  }

  returnEmojiBasedOnSeverity(input) {
    // Benutzt die Emojimap
    if (input.result) return this.emojiMap.correct;
    else if (input.severity == 'HIGH') return this.emojiMap.false;
    else if (input.severity == 'MEDIUM') return this.emojiMap.warning;
    else if (input.severity == 'LOW') return this.emojiMap.notImportant;
    else return this.emojiMap.bug;
  }
}

declare const require: any; // wird benützt um das svg zu laden

// Um das Projekt zu bekommen
export interface DialogData {
  projectID: number;
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
  message: string; // ist Fehlermeldung
}

// FÜr angular tiles
export interface Tile {
  color: string;
  cols: number;
  rows: number;
  text: string;
}

// Zum speichern der Daten der HTTP Anfrage
interface GetResponse {
  lintingResults: LintingResult[];
  name: string;
  url: string;
}

// Zum Speichern der Daten eines LintingResult
interface LintingResult {
  projectId: number;
  id: number;
  lintTime: string;
  checkResults: CheckResults[];
}
