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

  canvas;
  context;
  lastLintTime;
  RepoName = '';
  RepoURL = '';
  checksHighSeverity: CheckResults[]; // currently not in use
  checksMediumSeverity: CheckResults[]; // currently not in use
  checksLowSeverity: CheckResults[]; // currently not in use
  latestLintingResults: CheckResults[];
  tags: String[];
  LintingResultsInTags: CheckResults[][];
  numberOfTestsPerSeverityInTags: number[][]; // 2D array of size [tags+1, 4], 1 dim = tags, 2nd dim [correct, high, medium, low]
  maxColsForTiles = 9;
  tiles: Tile[] = [
    { text: 'Kategorien', cols: 5, rows: 6, color: 'white' },
    { text: 'Ergebnisse Aller Tests', cols: 4, rows: 2, color: 'white' },
  ];
  constructor(
    public dialogRef: MatDialogRef<RepositoryDetailsComponent>,
    @Inject(MAT_DIALOG_DATA) public data: DialogData,
    private http: HttpClient
  ) {}

  ngOnInit(): void {
    // initialyze arrays sorted via severity void
    this.checksHighSeverity = new Array<CheckResults>();
    this.checksMediumSeverity = new Array<CheckResults>();
    this.checksLowSeverity = new Array<CheckResults>();
    this.ShowProjectDetails(this.data.projectID);
  }

  ngAfterViewInit(): void {
    this.renderChart();
  }
  renderChart() {
    const canvas = <HTMLCanvasElement>document.getElementById('myChart');
    canvas.width = 150;
    canvas.height = 150;
    var ctx = canvas.getContext('2d');
    var myChart = new Chart(ctx, {
      type: 'doughnut',
      data: {
        labels: ['Red', 'Blue', 'Yellow'],
        datasets: [
          {
            label: 'My First Dataset',
            data: [300, 50, 100],
            backgroundColor: [
              'rgb(255, 99, 132)',
              'rgb(54, 162, 235)',
              'rgb(255, 205, 86)',
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
  }

  closeDialog() {
    this.dialogRef.close();
  }

  ShowProjectDetails(gitID) {
    this.http.get(`${environment.baseURL}/project/${gitID}`).subscribe(
      (val: any) => {
        console.log('GET call successful value returned in body', val);
        // fill linting category array
        var last_entry = val.lintingResults.length;
        this.latestLintingResults =
          val.lintingResults[last_entry - 1].checkResults;
        //this.fillSeverityArrays(); // currently does not need to be used
        this.groupLintingResultsInTagsAndFillNumTestsPerSeverity();
        // save information
        this.RepoName = val.name;
        this.RepoURL = val.url;
        this.lastLintTime = dayjs(
          val.lintingResults[last_entry - 1].lintTime
        ).format('DD.MM.YYYY - H:mm');
        // dynamically create missing tiles for grid list corresponding to their grid list
        this.addTilesForCategoryGraphAndFooter();
      },
      (response) => {
        console.log('GET call in error', response);
      },
      () => {
        console.log('The GET observable is now completed.');
      }
    );
  }

  fillTagsArray() {
    for (var i = 0; i < this.latestLintingResults.length; i++) {
      var tagAlreadyThere = false;
      // Check if tags has entries
      if (this.tags) {
        // Check if categories contains the current category
        for (var j = 0; j < this.tags.length; j++) {
          if (this.tags[j] == this.latestLintingResults[i].tag) {
            tagAlreadyThere = true;
          }
        }
      } else {
        this.tags = new Array<String>();
      }
      if (!tagAlreadyThere) {
        this.tags.push(this.latestLintingResults[i].tag);
      }
    }
  }

  groupLintingResultsInTagsAndFillNumTestsPerSeverity() {
    // Determine tags
    this.fillTagsArray();
    this.numberOfTestsPerSeverityInTags = new Array(this.tags.length + 1)
      .fill(0)
      .map(() => new Array(4).fill(0)); // 2D array of size [tags+1, 4], 1 dim = tags, 2nd dim [correct, low, medium, high]
    // Group the linting results to their corresponding tags
    this.LintingResultsInTags = new Array(this.tags.length);
    for (var i = 0; i < this.latestLintingResults.length; i++) {
      // Get index of tag in tags
      var index = this.tags.indexOf(this.latestLintingResults[i].tag);
      // Check if array needs to be initialized
      if (!this.LintingResultsInTags[index]) {
        this.LintingResultsInTags[index] = [];
      }
      // Push test into category corresponding index
      this.LintingResultsInTags[index].push(this.latestLintingResults[i]);
      this.addTestToFillNumTestsPerSeverity(index);
    }
  }

  fillSeverityArrays() {
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
  addTestToFillNumTestsPerSeverity(index) {
    if (this.latestLintingResults[index].result) {
      this.numberOfTestsPerSeverityInTags[index][0] += 1;
    } else if (this.latestLintingResults[index].severity == 'MEDIUM') {
      this.numberOfTestsPerSeverityInTags[index][1] += 1;
    } else if (this.latestLintingResults[index].severity == 'Low') {
      this.numberOfTestsPerSeverityInTags[index][2] += 1;
    } else if (this.latestLintingResults[index].severity == 'HIGH') {
      this.numberOfTestsPerSeverityInTags[index][3] += 1;
    } else {
      console.log(
        'In Repository Details component: addTestToFillNumTestsPerSeverity() found test which does not have a severity label!'
      );
    }
    return;
  }

  addTilesForCategoryGraphAndFooter() {
    for (var i = 0; i < this.tags.length; i++) {
      var t = <Tile>{ color: 'white', cols: 2, rows: 2, text: this.tags[i] };
      this.tiles.push(t);
    }
  }

  returnEmojiBasedOnSeverity(input) {
    if (input.result) return this.emojiMap.correct;
    else if (input.severity == 'HIGH') return this.emojiMap.false;
    else if (input.severity == 'MEDIUM') return this.emojiMap.warning;
    else if (input.severity == 'Low') return this.emojiMap.notImportant;
    else return this.emojiMap.bug;
  }
}

declare const require: any; //used for loading svg

// For getting the project
export interface DialogData {
  projectID: number;
}

// For storing the information on the project
interface CheckResults {
  checkName: string;
  severity: string;
  result: boolean;
  category: string;
  description: string;
  tag: string;
  fix: string;
  message: string; // is errormessage
  // errormessage:string
}

// For angular tiles
export interface Tile {
  color: string;
  cols: number;
  rows: number;
  text: string;
}
