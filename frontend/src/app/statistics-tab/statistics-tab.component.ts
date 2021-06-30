import { Component } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { OnInit } from '@angular/core';
import { MatChip } from '@angular/material/chips';
import { environment } from 'src/environments/environment';
import { Chart } from 'chart.js';
import * as dayjs from 'dayjs';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-statistics-tab',
  templateUrl: './statistics-tab.component.html',
  styleUrls: ['./statistics-tab.component.css'],
})
export class StatisticsTabComponent implements OnInit {
  constructor(private http: HttpClient, private _snackBar: MatSnackBar) {}

  ngOnInit(): void {
    console.log('Statistik ausgewählt');
    this.initStats();
  }
  openSnackBar(message: string, action: string) {
    this._snackBar.open(message, action);
  }
  ngAfterViewInit(): void {}

  chartNames = [
    'Anzahl an Projekten, die die X wichtigsten Tests bestanden haben',
    'Prozentzahl an Projekten, die die X wichtigsten Tests bestanden haben',
    'Anzahl an Projekten, die alle Test der Kategorie X bestanden haben',
    'Prozentzahl an Projekten, die alle Test der Kategorie X bestanden haben',
  ];

  csvExportLink = environment.baseURL + '/export/csv';

  chartImportantChecks;

  chartImportantChecksPercentage;

  chartCheckPerCategorie;

  chartCheckPerCategoriePercentage;

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

  initStats() {
    this.getChartData('top', 'absolute');
    this.getChartData('top', 'percentage');
    this.getChartData('allTags', 'absolute');
    this.getChartData('allTags', 'percentage');
  }

  async getChartData(apiCall: string, typ: string) {
    let params = new HttpParams().set('type', typ);
    // TODO use API Service instead
    await this.http
      .get(`${environment.baseURL}/projects/` + apiCall, { params: params })
      .toPromise()
      .then((results: any) => {
        console.log('results of call ' + apiCall + params, results);

        var timestamps: string[] = new Array();
        var tags: String[] = new Array();
        var values: number[][] = new Array();
        var dayAndTime;
        var allDayAndTime: string[] = new Array();

        for (let x in results) {
          dayAndTime = dayjs(x).format('DD.MM.YYYY');
          if (allDayAndTime.includes(dayAndTime)) {
            continue;
          }
          allDayAndTime.push(dayAndTime);
          timestamps.push(x);
          var value: number[] = new Array();
          for (let y in results[x]) {
            if (apiCall == 'top') {
              if (!tags.includes(y + ' wichtigsten')) {
                tags.push(y + ' wichtigsten');
              }
            } else {
              if (!tags.includes(y)) {
                tags.push(y);
              }
            }
            value.push(results[x][y]);
          }
          values.push(value);
        }

        var seriesValues = new Array(tags.length);
        for (var i = 0; i < tags.length; i++) {
          seriesValues[i] = new Array(timestamps.length);
        }

        for (var i = 0; i < timestamps.length; i++) {
          for (var j = 0; j < tags.length; j++) {
            seriesValues[j][i] = values[i][j];
          }
        }

        //this.setChartData(timestamps, tags, seriesValues, typ, apiCall);
        this.renderStatisticCharts(
          timestamps,
          tags,
          seriesValues,
          apiCall,
          typ
        );
      },
      (error)=>{
        console.log(error);
        this.openSnackBar('Fehler beim Holen der Statistik-Datein', 'OK');
      }
      );
  }

  unchangedTicks(value, index, values) {
    return value;
  }

  percentageTicks(value, index, values) {
    return value + '%';
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
      responsive: false,
      legend: {
        position: 'bottom',
        display: true,
      },
      scales: {
        yAxes: [
          {
            gridLines: {
              display: true,
            },
            id: 'yAxis',
            display: true,
            position: 'left',
            ticks: {
              beginAtZero: true,
              callback: ticksCallbackFunction,
              max: yAxisMaximum,
            },
            scaleLabel: {
              display: true,
              labelString: yAxisLabel,
            },
          },
        ],
        xAxes: [
          {
            gridLines: {
              display: true,
            },
            type: 'time',
            time: {
              displayFormats: {
                millisecond: 'DD MM YYYY',
                second: 'DD MM YYYY',
                minute: 'DD MM YYYY',
                hour: 'DD MM YYYY',
                day: 'DD MM YYYY',
                week: 'DD MM YYYY',
                month: 'DD MM YYYY',
                quarter: 'DD MM YYYY',
                year: 'DD MM YYYY',
              },
            },
            ticks: {
              autoSkip: true,
              maxTicksLimit: 10,
            },
          },
        ],
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
          pointRadius: 0,
          fill: false,
        });
      } else {
        datasets.push({
          label: tags[i],
          data: seriesValues[i],
          backgroundColor: this.chartColors[i % this.chartColors.length],
          borderColor: this.chartColors[i % this.chartColors.length],
          pointRadius: 0,
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

  getMaximumAndAddTenPercent(seriesValues) {
    //console.log('seriesValues',seriesValues);
    var maxGlobal = 0;
    for (var i = 0; i < seriesValues.length; i++) {
      var maxLocal = 0;
      for (var j = 0; j < seriesValues[i].length; j++) {
        maxLocal =
          seriesValues[i][j] > maxLocal ? seriesValues[i][j] : maxLocal;
      }
      //console.log('maxLocal',maxLocal);
      maxGlobal = maxLocal > maxGlobal ? maxLocal : maxGlobal;
    }
    //console.log('maxGlobal',maxGlobal);
    maxGlobal = maxGlobal + (maxGlobal * 10) / 100;
    return Math.ceil(maxGlobal);
  }

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
            options: chartInterface.options,
          }
        );
        this.chartImportantChecks.data.labels = timestamps;
        console.log(timestamps);
        this.chartImportantChecks.data.datasets = chartInterface.dataset;
        this.chartImportantChecks.update();
        //console.log(this.chartImportantChecks.data);
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
            options: chartInterface.options,
          }
        );
        this.chartImportantChecksPercentage.data.labels = timestamps;
        this.chartImportantChecksPercentage.data.datasets =
          chartInterface.dataset;
        this.chartImportantChecksPercentage.update();
        //console.log(this.chartImportantChecksPercentage.data);
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
            options: chartInterface.options,
          }
        );
        this.chartCheckPerCategorie.data.labels = timestamps;
        this.chartCheckPerCategorie.data.datasets = chartInterface.dataset;
        this.chartCheckPerCategorie.update();
        //console.log(this.chartCheckPerCategorie.data);
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

  toggleSelection(chip: MatChip) {
    chip.toggleSelected();
  }
}

interface chartCanvasOptionsDataset {
  options;
  canvas;
  dataset;
}
