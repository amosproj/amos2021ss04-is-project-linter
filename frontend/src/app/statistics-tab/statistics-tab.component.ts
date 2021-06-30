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

  chartImportantChecks;

  chartImportantChecksPercentage;

  chartCheckPerCategorie;

  chartCheckPerCategoriePercentage;
  
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
    this.initStats();
  }


  ngAfterViewInit(): void {}


  initStats() {
    this.getChartData('top', 'absolute');
    this.getChartData('top', 'percentage');
    this.getChartData('allTags', 'absolute');
    this.getChartData('allTags', 'percentage');
  }

  // Displays errors
  openSnackBar(message: string, action: string) {
    this._snackBar.open(message, action);
  }

  
  //---------------------------------------------------
  // Fetch Data for Charts
  //---------------------------------------------------

  async getChartData(apiCall: string, typ: string) {
    if (apiCall == 'allTags') {
      this.api.getProjectsByAllTags(typ).subscribe(
        (data) => {
          console.log(data);
          this.processStats(data, apiCall, typ);
        },
        (error) => {
          this.openSnackBar('Fehler beim Holen der Statistik-Datein', 'OK');
        }
      );
    } else if (apiCall == 'top') {
      this.api.getProjectsByTop(typ).subscribe(
        (data) => {
          console.log(data);
          this.processStats(data, apiCall, typ);
        },
        (error) => {
          this.openSnackBar('Fehler beim Holen der Statistik-Datein', 'OK');
        }
      );
    }
  }

  processStats(results: Statistics, apiCall: string, typ: string) {
    var timestamps: string[] = new Array();     // X-Axis values
    var tags: String[] = new Array();           // Number of lines in plot
    var values: number[][] = new Array();       // Y-Axis values 2D in shape of [lines][y-axisValues]

    this.inPlaceFilterForMultipleDays(results, apiCall, timestamps, tags, values)

    //var seriesValues = values;
    var seriesValues = this.transpose(values);

    //this.setChartData(timestamps, tags, seriesValues, typ, apiCall);
    this.renderStatisticCharts(timestamps, tags, seriesValues, apiCall, typ);
  }

  /*inPlaceFilterForMultipleDays_with_internal_transpose(results: Statistics, apiCall: string, xAxisValues: String[], lines: String[], yAxisValues: number[][]){

    // currently not working

    // get number of lines
    var num_of_lines = Object.keys(results).length;
    var y_axis_values = new Array(num_of_lines);
    if(num_of_lines == 0){
      this.openSnackBar('Fehler in den empfangenen Statistikdaten.', 'OK');
    }
    var daysWhichAlreadyWereAdded: string[] = new Array();
    for (let curr_x in results) {
      var dayAndTime = dayjs(curr_x).format('DD.MM.YYYY');
      if (daysWhichAlreadyWereAdded.includes(dayAndTime)) {
        continue;
      }
      daysWhichAlreadyWereAdded.push(dayAndTime);
      //timestamps.push(dateFns.setMinutes(dateFns.setHours(new Date(x),0),0).toISOString());
      xAxisValues.push(curr_x);
      var value: number[] = new Array();
      for (let y in results[curr_x]) {
        var index_of_line = -1;
        if (apiCall == 'top') {
          if (!lines.includes(y + ' wichtigsten')) {
           lines.push(y + ' wichtigsten');
          }
          index_of_line = lines.indexOf((y + ' wichtigsten'));
        } else {
          if (!lines.includes(y)) {
           lines.push(y);
          }
          index_of_line = lines.indexOf((y + ' wichtigsten'));
        }
        value.push(results[curr_x][y]);
        console.log(y_axis_values.length, " index", index_of_line, " y vals", y_axis_values)
        y_axis_values[index_of_line].push(results[curr_x][y])
        console.log("y",y, results[curr_x])
      }
      yAxisValues.push(value);
    }
    console.log("old",yAxisValues)
    console.log("transposed",y_axis_values)
 }*/

  inPlaceFilterForMultipleDays(results: Statistics, apiCall: string, xAxisValues: String[], lines: String[], yAxisValues: number[][]){
     // Filter only 
     var daysWhichAlreadyWereAdded: string[] = new Array();
     for (let curr_x in results) {
       var dayAndTime = dayjs(curr_x).format('DD.MM.YYYY');
       if (daysWhichAlreadyWereAdded.includes(dayAndTime)) {
         continue;
       }
       daysWhichAlreadyWereAdded.push(dayAndTime);
       //timestamps.push(dateFns.setMinutes(dateFns.setHours(new Date(x),0),0).toISOString());
       xAxisValues.push(curr_x);
       var value: number[] = new Array();
       for (let y in results[curr_x]) {
         if (apiCall == 'top') {
           if (!lines.includes(y + ' wichtigsten')) {
            lines.push(y + ' wichtigsten');
           }
         } else {
           if (!lines.includes(y)) {
            lines.push(y);
           }
         }
         value.push(results[curr_x][y]);
         console.log("y",y, results[curr_x])
       }
       console.log(value)
       yAxisValues.push(value);
     }
  }

  transpose(a) {
    return Object.keys(a[0]).map(function(c) {
        return a.map(function(r) { return r[c]; });
    });
  }


  
  //---------------------------------------------------
  // Create Charts
  //---------------------------------------------------

  
  renderStatisticCharts(timestamps, tags, seriesValues, apiCall, type) {
    // API Call for statistics are either top or allTags (best ones, or all sorted via category)
    // type is either absolute or in percentage
    var chartInterface: chartCanvasOptionsDataset;
    if (apiCall == 'top') {
      if (type == 'absolute') {
        var yAxisLabel = ''; //'Anzahl an Projekten, die die X wichtigsten Tests bestanden haben';
        var canvasElementID = 'importantChecks';
        var yAxisMaximum: number =
          this.getMaximumAndAddTenPercent(seriesValues);
        console.log('seriesValues', seriesValues);
        console.log('topMAax', yAxisMaximum);
        chartInterface = this.getChartInterfaceForCanvasChart(
          tags,
          seriesValues,
          yAxisLabel,
          canvasElementID,
          this.unchangedTicks,
          yAxisMaximum
        );
        this.chartImportantChecks = new Chart(
          chartInterface.canvas.getContext('2d'),
          {
            type: 'line',
            data: null,
            options: chartInterface.options,
          }
        );
        this.chartImportantChecks.data.labels = timestamps;
        console.log(timestamps);
        this.chartImportantChecks.data.datasets = chartInterface.dataset;
        this.chartImportantChecks.update();
      } else if (type == 'percentage') {
        var yAxisLabel = ''; //'Prozentzahl an Projekten, die die X wichtigsten Tests bestanden haben';
        var canvasElementID = 'importantChecksPercentage';
        var yAxisMaximum: number = 100;
        chartInterface = this.getChartInterfaceForCanvasChart(
          tags,
          seriesValues,
          yAxisLabel,
          canvasElementID,
          this.percentageTicks,
          yAxisMaximum
        );
        this.chartImportantChecksPercentage = new Chart(
          chartInterface.canvas.getContext('2d'),
          {
            type: 'line',
            data: null,
            options: chartInterface.options,
          }
        );
        this.chartImportantChecksPercentage.data.labels = timestamps;
        this.chartImportantChecksPercentage.data.datasets =
          chartInterface.dataset;
        this.chartImportantChecksPercentage.update();
      } else {
        console.log(
          'ERROR in statistics-tab.component.ts. No corresponding type for given parameter in renderStatisticCharts().'
        );
      }
    } else if (apiCall == 'allTags') {
      if (type == 'absolute') {
        var yAxisLabel = ''; //'Anzahl an Projekten, die alle Test der Kategorie X bestanden haben';
        var canvasElementID = 'checksPerCategorie';
        var yAxisMaximum: number =
          this.getMaximumAndAddTenPercent(seriesValues);
        chartInterface = this.getChartInterfaceForCanvasChart(
          tags,
          seriesValues,
          yAxisLabel,
          canvasElementID,
          this.unchangedTicks,
          yAxisMaximum
        );
        this.chartCheckPerCategorie = new Chart(
          chartInterface.canvas.getContext('2d'),
          {
            type: 'line',
            data: null,
            options: chartInterface.options,
          }
        );
        this.chartCheckPerCategorie.data.labels = timestamps;
        this.chartCheckPerCategorie.data.datasets = chartInterface.dataset;
        this.chartCheckPerCategorie.update();
      } else if (type == 'percentage') {
        var yAxisLabel = ''; // 'Prozentzahl an Projekten, die die X wichtigsten Tests bestanden haben';
        var canvasElementID = 'checksPerCategoriePercentage';
        var yAxisMaximum = 100;
        chartInterface = this.getChartInterfaceForCanvasChart(
          tags,
          seriesValues,
          yAxisLabel,
          canvasElementID,
          this.percentageTicks,
          yAxisMaximum
        );
        this.chartImportantChecksPercentage = new Chart(
          chartInterface.canvas.getContext('2d'),
          {
            type: 'line',
            data: null,
            options: chartInterface.options,
          }
        );
        this.chartImportantChecksPercentage.data.labels = timestamps;
        this.chartImportantChecksPercentage.data.datasets =
          chartInterface.dataset;
        this.chartImportantChecksPercentage.update();
        console.log(this.chartImportantChecksPercentage.data);
      } else {
        console.log(
          'ERROR in statistics-tab.component.ts. No corresponding type for given parameter in renderStatisticCharts().'
        );
      }
    } else {
      console.log(
        'ERROR in statistics-tab.component.ts. No corresponding apiCall for given parameter in renderStatisticCharts().'
      );
    }
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
      if (i != tags.length - 1) {
        datasets.push({
          label: tags[i],
          data: seriesValues[i],
          backgroundColor: this.chartColors[i % this.chartColors.length],
          borderColor: this.chartColors[i % this.chartColors.length],
          pointRadius: 3,
          fill: false,
        });
      } else {
        datasets.push({
          label: tags[i],
          data: seriesValues[i],
          backgroundColor: this.chartColors[i % this.chartColors.length],
          borderColor: this.chartColors[i % this.chartColors.length],
          pointRadius: 3,
          fill: false,
        });
      }
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

  
  getMaximumAndAddTenPercent(seriesValues) {
    var maxGlobal = 0;
    for (var i = 0; i < seriesValues.length; i++) {
      var maxLocal = 0;
      for (var j = 0; j < seriesValues[i].length; j++) {
        maxLocal =
          seriesValues[i][j] > maxLocal ? seriesValues[i][j] : maxLocal;
      }
      maxGlobal = maxLocal > maxGlobal ? maxLocal : maxGlobal;
    }
    maxGlobal = maxGlobal + (maxGlobal * 10) / 100;
    return Math.ceil(maxGlobal);
  }

  toggleSelection(chip: MatChip) {
    chip.toggleSelected();
  }
}

interface chartCanvasOptionsDataset {
  options;
  canvas;
  dataset;
}
