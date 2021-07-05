import { Component } from '@angular/core';
import { OnInit } from '@angular/core';
import { Chart } from 'chart.js';
import 'chartjs-adapter-date-fns';
import * as dayjs from 'dayjs';
import { MatSnackBar } from '@angular/material/snack-bar';
import { combineLatest, Observable } from 'rxjs';
import { MatDialog } from '@angular/material/dialog';

import { environment } from 'src/environments/environment';
import { SpinnerComponent } from '../spinner/spinner.component';
import { ApiService } from '../api.service';
import { Statistics } from '../schemas';

// FIXME basically refactor whole thing so apiCall and type are not always passed around ...

@Component({
  selector: 'app-statistics-tab',
  templateUrl: './statistics-tab.component.html',
  styleUrls: ['./statistics-tab.component.css'],
})
export class StatisticsTabComponent implements OnInit {
  csvExportLink = environment.baseURL + '/export/csv';
  charts = {
    ImportantChecks: Chart,
    ImportantChecksPercentage: Chart,
    CheckPerCategorie: Chart,
    CheckPerCategoriePercentage: Chart,
  };
  chartNames = [
    'Anzahl an Projekten, die die X wichtigsten Tests bestanden haben',
    'Prozentzahl an Projekten, die die X wichtigsten Tests bestanden haben',
    'Anzahl an Projekten, die alle Test der Kategorie X bestanden haben',
    'Prozentzahl an Projekten, die alle Test der Kategorie X bestanden haben',
  ];
  chartColors = [
    'rgb(75, 192, 192)', //green
    'rgb(255, 99, 132)', //red
    'rgb(255, 159, 64)', //orange
    'rgb(255, 205, 86)', //yellow
    'rgb(54, 162, 235)', //blue
    'rgb(153, 102, 255)', //purple
    'rgb(231,233,237)', //grey
  ];

  constructor(
    private api: ApiService,
    private _snackBar: MatSnackBar,
    public dialog: MatDialog
  ) {}

  ngOnInit(): void {
    // open spinner
    let dialogRef = this.dialog.open(SpinnerComponent, {
      width: '0px',
      height: '0px',
      panelClass: 'custom-dialog-container',
    });

    combineLatest([
      this.api.getProjectsByAllTags('absolute'),
      this.api.getProjectsByAllTags('percentage'),
      this.api.getProjectsByTop('absolute'),
      this.api.getProjectsByTop('percentage'),
    ])
      .subscribe(
        ([res1, res2, res3, res4]) => {
          this.processStats(res1, 'allTags', 'absolute');
          this.processStats(res2, 'allTags', 'percentage');
          this.processStats(res3, 'top', 'absolute');
          this.processStats(res4, 'top', 'percentage');
        },
        (error) => {
          this.openSnackBar('Fehler beim Holen der Statistik-Datein', 'OK');
        }
      )
      .add(() => {
        // finally close spinner
        dialogRef.close();
      });
  }

  openSnackBar(message: string, action: string) {
    this._snackBar.open(message, action);
  }

  processStats(results: Statistics, apiCall: string, typ: string) {
    let timestamps: string[] = new Array(); // X-Axis values
    let tags: String[] = new Array(); // Number of lines in plot
    let keys: string[] = new Array(); // Name/key of the lines for access in result
    let values: number[][] = new Array(); // Y-Axis values 2D in shape of [lines][y-axisValues]

    if (Object.keys(results).length == 0) {
      this.openSnackBar('Fehler in den empfangenen Statistikdaten.', 'OK');
      return;
    }

    this.inPlaceFillTimestamps(results, timestamps);
    this.inPlaceFillKeysAndLines(results, apiCall, timestamps, tags, keys);
    values = new Array(tags.length)
      .fill(0)
      .map(() => new Array(timestamps.length).fill(0));
    this.inPlaceFillValues(results, timestamps, keys, values);
    this.renderStatisticCharts(timestamps, tags, values, apiCall, typ);
  }

  inPlaceFillTimestamps(results: Statistics, xAxisValues: String[]) {
    let daysWhichAlreadyWereAdded: string[] = new Array();
    for (let curr_x in results) {
      let timestamp = dayjs(curr_x).format('DD.MM.YYYY');
      if (daysWhichAlreadyWereAdded.includes(timestamp)) {
        continue;
      }
      daysWhichAlreadyWereAdded.push(timestamp);
      xAxisValues.push(curr_x);
    }
  }

  inPlaceFillKeysAndLines(
    results: Statistics,
    apiCall: string,
    xAxisValues: String[],
    lines: String[],
    keys: string[]
  ) {
    for (let y in results[String(xAxisValues[0])]) {
      if (apiCall == 'top') {
        if (!lines.includes(y + ' wichtigsten')) {
          lines.push(y + ' wichtigsten');
          keys.push(y);
        } else {
          this.openSnackBar('Fehler in den empfangenen Statistikdaten.', 'OK'); // double labels for a single timestamp
        }
      } else {
        if (!lines.includes(y)) {
          lines.push(y);
          keys.push(y);
        } else {
          this.openSnackBar('Fehler in den empfangenen Statistikdaten.', 'OK'); // double labels for a single timestamp
        }
      }
    }
  }

  inPlaceFillValues(
    results: Statistics,
    xAxisValues: String[],
    keys: string[],
    yAxisValues: number[][]
  ) {
    for (let x_val = 0; x_val < xAxisValues.length; x_val++) {
      for (let line_idx = 0; line_idx < keys.length; line_idx++) {
        let curr_timestamp = results[String(xAxisValues[x_val])];
        yAxisValues[line_idx][x_val] = curr_timestamp[keys[line_idx]];
      }
    }
  }

  renderStatisticCharts(timestamps, tags, seriesValues, apiCall, type) {
    let chartInterface: chartCanvasOptionsDataset;
    let yAxisLabel = '';
    let ticksFunction;
    let yAxisMaximum: number;
    let canvasElementID: string;
    if (type == 'absolute') {
      ticksFunction = this.unchangedTicks;
      let max_y_values = this.getMaximum(seriesValues);
      yAxisMaximum = Math.ceil(max_y_values + 0.1 * max_y_values);
      if (apiCall == 'top') {
        canvasElementID = 'importantChecks';
      } else if (apiCall == 'allTags') {
        canvasElementID = 'checksPerCategorie';
      } else {
        this.openSnackBar('Fehler beim rendern der Statistikcharts.', 'OK');
        return;
      }
    } else if (type == 'percentage') {
      ticksFunction = this.percentageTicks;
      yAxisMaximum = 100;
      if (apiCall == 'top') {
        canvasElementID = 'importantChecksPercentage';
      } else if (apiCall == 'allTags') {
        canvasElementID = 'checksPerCategoriePercentage';
      } else {
        this.openSnackBar('Fehler beim rendern der Statistikcharts.', 'OK');
        return;
      }
    } else {
      this.openSnackBar('Fehler beim rendern der Statistikcharts.', 'OK');
      return;
    }

    chartInterface = this.getChartInterfaceForCanvasChart(
      tags,
      seriesValues,
      yAxisLabel,
      canvasElementID,
      ticksFunction,
      yAxisMaximum
    );
    this.charts[canvasElementID] = new Chart(
      chartInterface.canvas.getContext('2d'),
      {
        type: 'line',
        data: null,
        options: chartInterface.options,
      }
    );

    this.charts[canvasElementID].data.labels = timestamps;
    this.charts[canvasElementID].data.datasets = chartInterface.dataset;
    this.charts[canvasElementID].update();
  }

  getChartInterfaceForCanvasChart(
    tags,
    seriesValues,
    yAxisLabel,
    canvasElementID,
    ticksCallbackFunction,
    yAxisMaximum
  ) {
    let options = {
      responsive: true,
      plugins: {
        legend: {
          position: 'bottom',
          display: true,
        },
      },
      scales: {
        y: {
          gridLines: {
            display: true,
          },
          display: true,
          position: 'left',
          beginAtZero: true,
          max: yAxisMaximum,
          ticks: {
            callback: ticksCallbackFunction,
          },
          title: yAxisLabel,
        },
        x: {
          gridLines: {
            display: true,
          },
          type: 'time',
          time: {
            round: 'day',
            displayFormats: {
              day: 'dd.MM.yy',
            },
            tooltipFormat: 'dd.MM.yyyy',
            minUnit: 'day',
          },
          ticks: {
            maxTicksLimit: 10,
            source: 'auto',
          },
        },
      },
    };
    let canvas = <HTMLCanvasElement>document.getElementById(canvasElementID);
    let datasets = [];
    for (let i = 0; i < tags.length; i++) {
      datasets.push({
        label: tags[i],
        data: seriesValues[i],
        backgroundColor: this.chartColors[i % this.chartColors.length],
        borderColor: this.chartColors[i % this.chartColors.length],
        pointRadius: 3,
        fill: false,
      });
    }
    let dataset = datasets;
    let x: chartCanvasOptionsDataset = {
      options: options,
      canvas: canvas,
      dataset: dataset,
    };
    return x;
  }

  unchangedTicks(value) {
    // Ticks for y axis
    return value;
  }

  percentageTicks(value) {
    // Ticks for y axis
    return value + '%';
  }

  getMaximum(Arr2D: number[][]) {
    let maxGlobal = 0;
    for (let i = 0; i < Arr2D.length; i++) {
      let maxLocal = 0;
      for (let j = 0; j < Arr2D[i].length; j++) {
        maxLocal = Arr2D[i][j] > maxLocal ? Arr2D[i][j] : maxLocal;
      }
      maxGlobal = maxLocal > maxGlobal ? maxLocal : maxGlobal;
    }
    return maxGlobal;
  }
}

interface chartCanvasOptionsDataset {
  options;
  canvas;
  dataset;
}
