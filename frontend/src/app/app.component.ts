import { ComponentFactoryResolver, Input } from '@angular/core';
import { ViewContainerRef } from '@angular/core';
import { ViewChild } from '@angular/core';
import { Component } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { RepositoryComponent } from './repository/repository.component';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { environment } from 'src/environments/environment';
import { MatChip, MatChipList } from '@angular/material/chips';
import { OnInit } from '@angular/core';
import * as configFile from '../../../server/src/main/resources/config.json';
import { MatTableDataSource } from '@angular/material/table';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
})
export class AppComponent implements OnInit {
  title = 'frontend';
  projectComponents = [];
  chipsControl = new FormControl('');
  chipsValue$ = this.chipsControl.valueChanges;
  //SearchBarValue = '';
  kategorie = new FormControl('');
  all_projects: Project[];
  options: FormGroup;
  forwardLinkWorked = true;
  errorMsgForwardLink = '';
  hideRequiredControl = new FormControl(false);
  floatLabelControl = new FormControl('auto');

  chipOptions: string[];

  gridInfo: GridInfo[] = new Array<GridInfo>();

  dataArray: GridInfo[] = new Array<GridInfo>();
  displayColumns: string[] = [
    'project',
    'testsPassed',
    'testsPassedPerActivChip',
    'newTestsPassedSinceLastMonth',
  ];

  columnsToDisplay: string[] = this.displayColumns.slice();
  data = new MatTableDataSource<GridInfo>(this.dataArray);

  @ViewChild('parent', { read: ViewContainerRef }) container: ViewContainerRef;

  constructor(
    fb: FormBuilder,
    private _cfr: ComponentFactoryResolver,
    private http: HttpClient
  ) {
    this.options = fb.group({
      hideRequired: this.hideRequiredControl,
      floatLabel: this.floatLabelControl,
    });
  }

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

  GetProjects() {
    // Holt alle Projekte vom Backend-Server (Ohne LintingResults)
    this.http.get(`${environment.baseURL}/projects`).subscribe((results) => {
      this.all_projects = JSON.parse(JSON.stringify(results)) as Project[];
      console.log(this.all_projects);

      for (let project in this.all_projects) {
        this.addComponent(
          this.all_projects[project].name,
          this.all_projects[project].id,
          this.all_projects[project].url
        );
      }
    }); // momentan kann man nur die URL senden und nicht ein JSON Objekt
  }

  ngOnInit() {
    this.GetProjects();
    this.dataArray = new Array<GridInfo>();
  }

  addComponent(name, id, gitlabInstance) {
    // Fügt eine Komponente unter dem Tab Repositories hinzu
    var comp = this._cfr.resolveComponentFactory(RepositoryComponent);
    var expComponent = this.container.createComponent(comp);
    expComponent.instance._ref = expComponent;
    expComponent.instance.name = name;
    expComponent.instance.id = id;
    expComponent.instance.gitlabInstance = gitlabInstance;
    //Zum Suchen
    this.projectComponents.push(expComponent);
  }

  searchProject(value: string) {
    // Erstellt alle Komponenten im Repostiories Tab
    // TODO: Methoden Benennung ändern
    this.removeAllProjectsFromOverview();

    for (let item of this.projectComponents) {
      if (item.instance.name.startsWith(value) || value === '') {
        var comp = this._cfr.resolveComponentFactory(RepositoryComponent);
        var expComponent = this.container.createComponent(comp);
        expComponent.instance._ref = expComponent;
        expComponent.instance.name = item.instance.name;
        expComponent.instance.id = item.instance.id;
        expComponent.instance.gitlabInstance = item.instance.gitlabInstance;
      }
    }
  }
  getChipOptions() {
    //hole alle verschiedenen tags aus der config.json datei
    this.chipOptions = [];
    for (let [key, value] of Object.entries(configFile.checks)) {
      if (!this.chipOptions.includes(value.tag)) {
        this.chipOptions.push(value.tag);
      }
    }
    return this.chipOptions;
  }

  createStatistik(event: Event) {
    this.getProjectInfoForStatistik();
  }

  getProjectInfoForStatistik() {
    //lade projekte, vlt überflüssig
    this.GetProjects();
    //für jedes projekt
    for (var i = 0; i < this.all_projects.length; i++) {
      //lade ergebnisse der checks aus dem backend
      this.http
        .get(
          `${environment.baseURL}/project/${this.all_projects[i].id}/lastMonth`
        )
        .subscribe((val: any) => {
          var checkResults: CheckResults[] =
            val.lintingResults[val.lintingResults.length - 1].checkResults;
          var checkResultsLastMonth: CheckResults[] =
            val.lintingResults[0].checkResults;
          //Zähler für erfolgreiche Checks
          var checksPassed = 0;
          //Zähler für erfolgreiche Checks pro Tag
          var checksPassedPerActivChip = 0;
          //Zähler für erfolgreiche Checks letzten Monat
          var checksPassedLastMonth = 0;
          for (var j = 0; j < checkResults.length; j++) {
            // wenn der Check erfolgreich war erhöhe die Zähler
            if (checkResults[j].result) {
              checksPassed = checksPassed + 1;
              for (var k = 0; k < this.chipsControl.value.length; k++) {
                //console.log('chipControl', this.chipsControl.value[k]);
                //console.log('tag in backend', checkResults[j].tag);
                if (
                  this.chipsControl.value[k].toLowerCase().trim() ==
                  checkResults[j].tag.toLowerCase().trim()
                ) {
                  checksPassedPerActivChip = checksPassedPerActivChip + 1;
                }
              }
            }
            if (checkResultsLastMonth[j].result) {
              checksPassedLastMonth = checksPassedLastMonth + 1;
            }
          }
          //var info : GridInfo = {project : val.name, testsPassed: checksPassed};
          var info: GridInfo = {
            project: val.name,
            testsPassed: checksPassed,
            testsPassedPerActivChip: checksPassedPerActivChip,
            newTestsPassedSinceLastMonth: checksPassed - checksPassedLastMonth,
          };
          this.gridInfo.push(info);
        });
    }

    //wähle die sortier funktion nach eingabe
    switch (this.kategorie.value) {
      case 'bestandene_tests': {
        //this.gridInfo = this.bubbleSort(this.gridInfo, this.compareTestsPassed);
        this.gridInfo.sort(this.compareTestsPassed);
        break;
      }
      case 'bestandene_tests_letzter_monat': {
        //this.gridInfo = this.bubbleSort(this.gridInfo, this.compareNewTestsPassedSinceLastMonth);
        this.gridInfo.sort(this.compareNewTestsPassedSinceLastMonth);
        break;
      }
      case 'bestandene_tests_pro_kategorie': {
        //this.gridInfo = this.bubbleSort(this.gridInfo, this.compareTestsPassedPerTag);
        this.gridInfo.sort(this.compareTestsPassedPerActivChip);
        break;
      }
    }
    console.log('Grid Info', this.gridInfo);
    var item: GridInfo;
    for (var index in this.gridInfo) {
      item = this.gridInfo[index];
      this.dataArray.push(item);
    }

    this.data.data = this.dataArray;
  }

  /*
  bubbleSort(gridInfoArray: GridInfo[], cmp: (a: any, b: any, c:any) => number) : GridInfo[]{
    let i = 0, j = 0, len = gridInfoArray.length, swapped = false;
    var currentValue, nextValue;
    for (i=0; i < len; i++){
      swapped = false;
      for (j=0; j < len-1; j++) {
        currentValue = gridInfoArray[j];
        nextValue = gridInfoArray[j + 1];
        if (cmp(currentValue, nextValue, this.chipsControl.value) > 0) {  // compare the adjacent elements 
            gridInfoArray[j] = nextValue;  // swap them
            gridInfoArray[j + 1] = currentValue;
            swapped = true;
        }
      }
      if (!swapped) {// if no number was swapped that means array is sorted now, break the loop.
          break;
      }
    }
  return gridInfoArray;
  }
  */

  compareTestsPassed(a, b) {
    if (a.testsPassed < b.testsPassed) {
      return 1;
    }
    if (a.testsPassed > b.testsPassed) {
      return -1;
    }
    return 0;
  }

  compareNewTestsPassedSinceLastMonth(a, b) {
    if (a.newTestsPassedSinceLastMonth < b.newTestsPassedSinceLastMonth) {
      return 1;
    }
    if (a.newTestsPassedSinceLastMonth > b.newTestsPassedSinceLastMonth) {
      return -1;
    }
    return 0;
  }

  compareTestsPassedPerActivChip(a, b) {
    if (a.testsPassedPerActivChip < b.testsPassedPerActivChip) {
      return 1;
    }
    if (a.testsPassedPerActivChip > b.testsPassedPerActivChip) {
      return -1;
    }
    return 0;
  }

  /*
  compareTestsPassedPerTag(a, b, setTags) {
    var result;
      for (var i = 0; i < setTags.length; i++){
        if(setTags[i] == 1){
          if(a.testsPassedPerTag[i] >= b.testsPassedPerTag[i]){
            result = 1;
          } else {
            return -1;
          }
        }
      }
      return result;
  }
  */

  toggleSelection(chip: MatChip) {
    chip.toggleSelected();
  }
} // Ende von AppComponent

// Interface für die repository Komponente welche grob die Informationen des repository zeigt
interface Project {
  gitlabInstance: string;
  gitlabProjectId: number;

  id: number;
  name: string;
  results: [];
  url: string;
}

interface GridInfo {
  project: string;

  testsPassed: number;
  testsPassedPerActivChip: number;
  newTestsPassedSinceLastMonth: number;
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
  priority: number;
  message: string; // ist Fehlermeldung
}

// Zum Speichern der Daten eines LintingResult
interface LintingResult {
  projectId: number;
  id: number;
  lintTime: string;
  checkResults: CheckResults[];
}
