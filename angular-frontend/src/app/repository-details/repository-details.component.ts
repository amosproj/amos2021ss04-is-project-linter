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
  getdata = false;
   myChart;
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

  //initFinsished = false;
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
  //if there should be new tags this has to be extended
  chartNames : string[] = ['totalChart', 'userChart', 'programmerChart' ];
  //static numberOfTestsPerSeverityInTags: number[][];
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

  ngOnInit(): void{
    // initialyze arrays sorted via severity void
    this.checksHighSeverity = new Array<CheckResults>();
    this.checksMediumSeverity = new Array<CheckResults>();
    this.checksLowSeverity = new Array<CheckResults>();
    this.ShowProjectDetails(this.data.projectID);
  }
 
  ngAfterViewInit(): void {
    //render the charts
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
    console.log('Print chartName', this.chartNames[index]);
    console.log('Print numbers for Chart', numberOfTestsPerSeverityInTags[index]);
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
                'rgb(3, 252, 40)',  // green
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
    //let data = await this.http.get<GetResponse>(`${environment.baseURL}/project/${gitID}`).toPromise();
    this.http.get(`${environment.baseURL}/project/${gitID}`).subscribe(
      (val: any) => {
        console.log('GET call successful value returned in body', val);
        // fill linting category array
        var last_entry = val.lintingResults.length;
        this.latestLintingResults =
         val.lintingResults[last_entry - 1].checkResults;
        //this.fillSeverityArrays(); // currently does not need to be used
        this.tags = this.getTagsArray(this.latestLintingResults);

        this.LintingResultsInTags = this.groupLintingResultsInTagsAndFillNumTestsPerSeverity(this.tags, this.latestLintingResults)[1];
        console.log('LintingResultInTags', this.LintingResultsInTags);

        // save information
        this.RepoName = val.name;
        this.RepoURL = val.url;
        this.lastLintTime = dayjs(
          val.lintingResults[last_entry - 1].lintTime
        ).format('DD.MM.YYYY - H:mm');
        // dynamically create missing tiles for grid list corresponding to their grid list
        this.addTilesForCategoryGraphAndFooter();
      });
  }

  getTagsArray(latestLintingResults){
    var tags = [];
    for (var i = 0; i < latestLintingResults.length; i++) {
      var tagAlreadyThere = false;
      // Check if tags has entries
      if (tags) {
        // Check if categories contains the current category
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
    var numberOfTestsPerSeverityInTags = new Array(tags.length + 1).fill(0).map(() => new Array(4).fill(0)); // 2D array of size [tags+1, 4], 1 dim = tags, 2nd dim [correct, low, medium, high]
    // Group the linting results to their corresponding tags
    var LintingResultsInTags = new Array(tags.length);
    for (var i = 0; i < latestLintingResults.length; i++) {
      // Get index of tag in tags
      var index = tags.indexOf(latestLintingResults[i].tag);
      // Check if array needs to be initialized
      if (!LintingResultsInTags[index]) {
        LintingResultsInTags[index] = [];
      }
      // Push test into category corresponding index
      LintingResultsInTags[index].push(latestLintingResults[i]);
      // ok console.log('index', index);
      numberOfTestsPerSeverityInTags = this.addTestToFillNumTestsPerSeverity(index, latestLintingResults[i], numberOfTestsPerSeverityInTags);
      //this.numberOfTestsPerSeverityInTags= numberOfTestsPerSeverityInTags;
    }

    return [numberOfTestsPerSeverityInTags, LintingResultsInTags];
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
  addTestToFillNumTestsPerSeverity(index, lintingResult ,numberOfTestsPerSeverityInTags) {
    index = index +1 // since first row is the statistic for all tests
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
    else if (input.severity == 'LOW') return this.emojiMap.notImportant;
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

// For storing the information of the http get request
interface GetResponse {
  lintingResults: LintingResult[];
  name: string;
  url: string;
}

// For storing the information of a LintingResult
interface LintingResult {
  projectId: number;
  id: number;
  lintTime: string;
  checkResults: CheckResults[];
}
