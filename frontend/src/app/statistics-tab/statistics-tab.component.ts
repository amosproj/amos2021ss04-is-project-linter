import { Component } from '@angular/core';
import { OnInit } from '@angular/core';
import { MatChip } from '@angular/material/chips';
import { environment } from 'src/environments/environment';
import { Chart } from 'chart.js';
import * as dateFns from 'date-fns';
import 'chartjs-adapter-date-fns';
import * as dayjs from 'dayjs';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ApiService } from '../api.service';
import { Statistics } from '../schemas';
import { newArray } from '@angular/compiler/src/util';

@Component({
  selector: 'app-statistics-tab',
  templateUrl: './statistics-tab.component.html',
  styleUrls: ['./statistics-tab.component.css'],
})
export class StatisticsTabComponent implements OnInit {
  //---------------------------------------------------
  // Class variables
  //----------------------------------------------------
  csvExportLink = environment.baseURL + '/export/csv';
  charts = {
    'ImportantChecks': Chart,
    'ImportantChecksPercentage': Chart,
    'CheckPerCategorie': Chart,
    'CheckPerCategoriePercentage': Chart,
  }
  chartNames = [
    'Anzahl an Projekten, die die X wichtigsten Tests bestanden haben',
    'Prozentzahl an Projekten, die die X wichtigsten Tests bestanden haben',
    'Anzahl an Projekten, die alle Test der Kategorie X bestanden haben',
    'Prozentzahl an Projekten, die alle Test der Kategorie X bestanden haben',
  ];
  chartColors = [
    //green:
    'rgb(75, 192, 192)',
    //red:
    'rgb(255, 99, 132)',
    //orange:
    'rgb(255, 159, 64)',
    //yellow:
    'rgb(255, 205, 86)',
    //blue:
    'rgb(54, 162, 235)',
    //purple:
    'rgb(153, 102, 255)',
    //grey:
    'rgb(231,233,237)',
  ];

  //---------------------------------------------------
  // Init Methods
  //---------------------------------------------------

  constructor(private api: ApiService, private _snackBar: MatSnackBar) {}

  ngOnInit(): void {
    this.getChartData('top', 'absolute');
    this.getChartData('top', 'percentage');
    this.getChartData('allTags', 'absolute');
    this.getChartData('allTags', 'percentage');
  }

  openSnackBar(message: string, action: string) {
    this._snackBar.open(message, action);
  }

  getChartData(apiCall: string, typ: string) {
    if (apiCall == 'allTags') {
      this.api.getProjectsByAllTags(typ).subscribe(
        (data) => {
          this.processStats(data, apiCall, typ);
        },
        (error) => {
          this.openSnackBar('Fehler beim Holen der Statistik-Datein', 'OK');
        }
      );
    } else if (apiCall == 'top') {
      this.api.getProjectsByTop(typ).subscribe(
        (data) => {
          this.processStats(data, apiCall, typ);
        },
        (error) => {
          this.openSnackBar('Fehler beim Holen der Statistik-Datein', 'OK');
        }
      );
    }
  }

  processStats(results: Statistics, apiCall: string, typ: string) {
    var timestamps: string[] = new Array(); // X-Axis values
    var tags: String[] = new Array(); // Number of lines in plot
    var keys: string[] = new Array(); // Name/key of the lines for access in result
    var values: number[][] = new Array(); // Y-Axis values 2D in shape of [lines][y-axisValues]

    if (Object.keys(results).length == 0) {
      this.openSnackBar('Fehler in den empfangenen Statistikdaten.', 'OK');
      return;
    }

    this.inPlaceFillTimestamps(results, timestamps);
    this.inPlaceFillKeysAndLines(results, apiCall, timestamps, tags, keys);
    values = new Array(tags.length)
      .fill(0)
      .map(() => new Array(timestamps.length).fill(0));
    this.inPlaceFillValues(results, apiCall, timestamps, keys, values);
    this.renderStatisticCharts(timestamps, tags, values, apiCall, typ);
  }

  inPlaceFillTimestamps(results: Statistics, xAxisValues: String[]) {
    var daysWhichAlreadyWereAdded: string[] = new Array();
    for (let curr_x in results) {
      var timestamp = dayjs(curr_x).format('DD.MM.YYYY');
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
    apiCall: string,
    xAxisValues: String[],
    keys: string[],
    yAxisValues: number[][]
  ) {
    for (let x_val = 0; x_val < xAxisValues.length; x_val++) {
      for (let line_idx = 0; line_idx < keys.length; line_idx++) {
        var curr_timestamp = results[String(xAxisValues[x_val])];
        yAxisValues[line_idx][x_val] = curr_timestamp[keys[line_idx]];
      }
    }
  }

  renderStatisticCharts(timestamps, tags, seriesValues, apiCall, type) {
    var chartInterface: chartCanvasOptionsDataset;
    var yAxisLabel = '';
    var ticksFunction;
    var yAxisMaximum: number;
    var canvasElementID: string;
    if (type == 'absolute') {
      ticksFunction = this.unchangedTicks;
      var max_y_values =this.getMaximum(seriesValues)
      yAxisMaximum = Math.ceil(max_y_values + (0.1*max_y_values));
      if (apiCall == 'top') {
        canvasElementID = 'importantChecks';
      }else if (apiCall == 'allTags') {
        canvasElementID = 'checksPerCategorie';
      }else{
        this.openSnackBar('Fehler beim rendern der Statistikcharts.', 'OK');
        return;
      }
    }else if (type == 'percentage') {
      ticksFunction = this.percentageTicks;
      yAxisMaximum = 100;
      if (apiCall == 'top') {
        canvasElementID = 'importantChecksPercentage';
      }else if (apiCall == 'allTags') {
        canvasElementID = 'checksPerCategoriePercentage';
      }else{
        this.openSnackBar('Fehler beim rendern der Statistikcharts.', 'OK');
        return;
      }
    }else{
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
    var options = {
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
    var canvas = <HTMLCanvasElement>document.getElementById(canvasElementID);
    var datasets = [];
    for (var i = 0; i < tags.length; i++) {
      datasets.push({
        label: tags[i],
        data: seriesValues[i],
        backgroundColor: this.chartColors[i % this.chartColors.length],
        borderColor: this.chartColors[i % this.chartColors.length],
        pointRadius: 3,
        fill: false,
      });
    }
    var dataset = datasets;
    var x: chartCanvasOptionsDataset = {
      options: options,
      canvas: canvas,
      dataset: dataset,
    };
    return x;
  }

  unchangedTicks(value, index, values) {
    // Ticks for y axis
    return value;
  }

  percentageTicks(value, index, values) {
    // Ticks for y axis
    return value + '%';
  }

  getMaximum(Arr2D: number[][]) {
    var maxGlobal = 0;
    for (var i = 0; i < Arr2D.length; i++) {
      var maxLocal = 0;
      for (var j = 0; j < Arr2D[i].length; j++) {
        maxLocal =
        Arr2D[i][j] > maxLocal ? Arr2D[i][j] : maxLocal;
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
