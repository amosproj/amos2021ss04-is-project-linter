import { ComponentFactoryResolver } from '@angular/core';
import { ViewContainerRef } from '@angular/core';
import { ViewChild } from '@angular/core';
import { Component } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { HttpClient, HttpParams, HttpHeaders } from '@angular/common/http';
import { OnInit } from '@angular/core';

import { MatChip } from '@angular/material/chips';
import { MatDialog } from '@angular/material/dialog';

import { environment } from 'src/environments/environment';
import { RepositoryComponent } from '../repository/repository.component';
import { SpinnerComponentComponent } from '../spinner-component/spinner-component.component';
import { Chart } from 'chart.js';
import { Project, Config, CheckResults, LintingResult } from '../schemas';
import { ApiService } from '../api.service';
import * as dayjs from 'dayjs';

@Component({
  selector: 'app-statistics-tab',
  templateUrl: './statistics-tab.component.html',
  styleUrls: ['./statistics-tab.component.css'],
})
export class StatisticsTabComponent implements OnInit {
  constructor(private http: HttpClient) {}

  ngOnInit(): void {}

  ngAfterViewInit(): void {}

  csvExportLink = environment.baseURL + '/export/csv';

  chartImportantChecks;
  dataImportantChecks;
  canvasImportantChecks;
  chartOptionsImportantChecks;
  chartImportantChecksPercentage;
  dataImportantChecksPercentage;
  canvasImportantChecksPercentage;
  chartOptionsImportantChecksPercentage;
  chartCheckPerCategorie;
  dataCheckPerCategorie;
  canvasCheckPerCategorie;
  chartOptionsCheckPerCategorie;
  chartCheckPerCategoriePercentage;
  dataCheckPerCategoriePercentage;
  canvasCheckPerCategoriePercantage;
  chartOptionsCheckPerCategoriePercantage;

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

  timestamps: string[] = new Array();
  tags: String[] = new Array();
  values: number[][] = new Array();

  seriesValues: number[][];

  setChartData() {
    console.log('timestamps', this.timestamps);

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
          backgroundColor: this.chartColors[0],
          borderColor: this.chartColors[0],
          pointRadius: 0,
          fill: 1,
        },
        {
          label: 'Top 10',
          data: [7, 7, 7, 7, 8, 8, 9],
          backgroundColor: this.chartColors[1],
          borderColor: this.chartColors[1],
          pointRadius: 0,
          fill: 2,
        },
        {
          label: 'Top 15',
          data: [5, 6, 6, 7, 7, 7, 8],
          backgroundColor: this.chartColors[2],
          borderColor: this.chartColors[2],
          pointRadius: 0,
          fill: true,
        },
      ],
    };

    this.chartOptionsImportantChecks = {
      responsive: true,
      legend: {
        position: 'right',
        display: true,
      },
      scales: {
        yAxes: [
          {
            id: 'yAxis',
            display: true,
            position: 'left',
            ticks: {
              beginAtZero: true,
            },
            scaleLabel: {
              display: true,
              labelString:
                'Anzahl an Projekten, die die X wichtigsten Tests bestanden haben',
            },
          },
        ],
      },
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
          backgroundColor: 'rgb(255, 99, 132)',
          borderColor: 'rgb(255, 99, 132)',
          color: 'rgb(255, 99, 132)',
        },
      ],
    };

    this.chartOptionsImportantChecksPercentage = {
      responsive: true,
      legend: {
        position: 'right',
        display: true,
      },
      scales: {
        yAxes: [
          {
            id: 'yAxis',
            display: true,
            position: 'left',
            ticks: {
              beginAtZero: true,
              callback: function (value, index, values) {
                return value + '%';
              },
              max: 100,
            },
            scaleLabel: {
              display: true,
              labelString:
                'Prozentzahl an Projekten, die die X wichtigsten Tests bestanden haben',
            },
          },
        ],
      },
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

    this.chartOptionsCheckPerCategorie = {
      responsive: true,
      legend: {
        position: 'right',
        display: true,
      },
      scales: {
        yAxes: [
          {
            id: 'yAxis',
            display: true,
            position: 'left',
            ticks: {
              beginAtZero: true,
            },
            scaleLabel: {
              display: true,
              labelString:
                'Anzahl an Projekten, die alle Test der Kategorie X bestanden haben',
            },
          },
        ],
      },
    };

    this.dataCheckPerCategoriePercentage = {
      labels: ['0d', '10d', '20d', '30d', '40d', '50d', '60d'],
      yAxisID: 'yAxis',
      datasets: [
        {
          label: 'Car Cost',
          data: [0, 10, 20, 5, 23, 22, 25],
          fill: false,
        },
      ],
    };

    this.chartOptionsCheckPerCategoriePercantage = {
      responsive: true,
      legend: {
        position: 'right',
        display: true,
      },
      scales: {
        yAxes: [
          {
            id: 'yAxis',
            display: true,
            position: 'left',
            ticks: {
              beginAtZero: true,
              callback: function (value, index, values) {
                return value + '%';
              },
              max: 100,
            },
            scaleLabel: {
              display: true,
              labelString:
                'Prozentzahl an Projekten, die alle Test der Kategorie X bestanden haben',
            },
          },
        ],
      },
    };
  }

  setOnStatistikTab() {
    this.getChartData();
  }

  async getChartData() {
    let params = new HttpParams().set("type","absolute")
    await this.http
      .get(`${environment.baseURL}/projects/allTags`,  {params: params})
      .toPromise()
      .then((results: any) => {
        //console.log('new api returns',results);
        for (let x in results) {
          //console.log('timestamp',x);
          if (this.timestamps.includes(dayjs(x).format('DD.MM.YYYY'))) {
            continue;
          }
          this.timestamps.push(dayjs(x).format('DD.MM.YYYY'));
          var value: number[] = new Array();
          for (let y in results[x]) {
            //console.log('tag',y);
            if (!this.tags.includes(y)) {
              this.tags.push(y);
            }
            //console.log('value',results[x][y]);
            value.push(results[x][y]);
          }
          this.values.push(value);
        }
        //console.log(this.timestamps);
        //console.log(this.tags);
        //console.log(this.values);

        this.seriesValues = new Array(this.tags.length);
        for (var i = 0; i < this.tags.length; i++) {
          this.seriesValues[i] = new Array(this.timestamps.length);
        }

        for (var i = 0; i < this.timestamps.length; i++) {
          for (var j = 0; j < this.tags.length; j++) {
            this.seriesValues[j][i] = this.values[i][j];
          }
        }

        console.log('timestamps in http', this.timestamps);

        this.setChartData();
        this.renderStatistikCharts();
      });
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
        options: this.chartOptionsImportantChecks,
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
        options: this.chartOptionsImportantChecksPercentage,
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
        options: this.chartOptionsCheckPerCategorie,
      }
    );

    //console.log('timestamp in renderChart',this.timestamps);
    this.chartCheckPerCategorie.data.labels = this.timestamps;

    var datasets = [];
    for (var i = 0; i < this.tags.length; i++) {
      if (i != this.tags.length - 1) {
        datasets.push({
          label: this.tags[i],
          data: this.seriesValues[i],
          backgroundColor: this.chartColors[i % this.chartColors.length],
          borderColor: this.chartColors[i % this.chartColors.length],
          pointRadius: 0,
          fill: i + 1,
        });
      } else {
        datasets.push({
          label: this.tags[i],
          data: this.seriesValues[i],
          backgroundColor: this.chartColors[i % this.chartColors.length],
          borderColor: this.chartColors[i % this.chartColors.length],
          pointRadius: 0,
          fill: true,
        });
      }
    }
    //test.push({label: this.tags[0], data: this.seriesValues[0]})
    //var test = [{label: this.tags[0], data: this.seriesValues[0]}];

    //this.chartCheckPerCategorie.data.datasets = [{label : 'Series1', data: [1,3]}];

    this.chartCheckPerCategorie.data.datasets = datasets;

    this.chartCheckPerCategorie.update();

    //console.log(this.chartCheckPerCategorie.data.labels);
    //console.log('Dataset',this.chartCheckPerCategorie.data.datasets);

    this.canvasCheckPerCategoriePercantage = <HTMLCanvasElement>(
      document.getElementById('checksPerCategoriePercentage')
    );

    this.chartCheckPerCategoriePercentage = new Chart(
      this.canvasCheckPerCategoriePercantage.getContext('2d'),
      {
        type: 'line',
        options: this.chartOptionsCheckPerCategoriePercantage,
      }
    );

    for (var i = 0; i < this.tags.length; i++) {
      datasets[i].data = datasets[i].data.map((x) => (x * 100) / 10);
      datasets[i].fill = false;
    }

    this.chartCheckPerCategoriePercentage.data.labels = this.timestamps;
    this.chartCheckPerCategoriePercentage.data.datasets = datasets;

    this.chartCheckPerCategoriePercentage.update();
    console.log(
      'Dataset Percentage',
      this.chartCheckPerCategoriePercentage.data.datasets
    );
  }

  toggleSelection(chip: MatChip) {
    chip.toggleSelected();
  }

  addData(chart, label, data) {
    chart.data.labels.push(label);
    chart.data.datasets.forEach((dataset) => {
      dataset.data.push(data);
    });
    chart.update();
  }
}
