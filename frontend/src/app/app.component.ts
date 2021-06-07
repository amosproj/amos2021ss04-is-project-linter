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

import { CommonModule } from '@angular/common';
import { MatDialog } from '@angular/material/dialog';
import { SpinnerComponentComponent } from './spinner-component/spinner-component.component';

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
  init_all_projects: Project[];
  options: FormGroup;
  forwardLinkWorked = true;
  errorMsgForwardLink = '';
  hideRequiredControl = new FormControl(false);
  floatLabelControl = new FormControl('auto');
  searchCriteria = new FormControl('');
  availableSearchCriteria: string[] = ['Bestandene Tests', 'Neue bestandene Tests in den letzten 30 Tagen'];
  chipOptions: string[];
  filterInfo = "Momentan sortierts nach Tag: - und Kategorie: -";
  toggleToTrue=true;
  
  @ViewChild('parent', { read: ViewContainerRef }) container: ViewContainerRef;
 

  constructor(
    public dialog: MatDialog,
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

  async GetProjects() {
    // Holt alle Projekte vom Backend-Server (Ohne LintingResults)
    let dialogRef = this.dialog.open(SpinnerComponentComponent, {
      width: '100%',
      height: '100%',
      panelClass: 'custom-dialog-container',
   
    });
   
    await this.http.get(`${environment.baseURL}/projects`).toPromise().then((results:any) => {
      this.all_projects = JSON.parse(JSON.stringify(results)) as Project[];
      console.log(this.all_projects);

      for (let project in this.all_projects) {
        this.addComponent(
          this.all_projects[project].name,
          this.all_projects[project].id,
          this.all_projects[project].url
        );
      }

      this.prepareProjectDataForSorting();
      this.init_all_projects = this.all_projects.slice();
      dialogRef.close();
    }); // momentan kann man nur die URL senden und nicht ein JSON Objekt
  }

  ngOnInit() {
    this.GetProjects();
  }

  ngAfterViewInit(){
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

  async prepareProjectDataForSorting() {
    for (var i = 0; i < this.all_projects.length; i++) {
      //lade ergebnisse der checks aus dem backend
      
      await this.http
        .get(`${environment.baseURL}/project/${this.all_projects[i].id}/lastMonth`).toPromise().then((val: any) => {

          var checkResults: CheckResults[] =
            val.lintingResults[val.lintingResults.length - 1].checkResults;
          var checkResultsLastMonth: CheckResults[] =
            val.lintingResults[0].checkResults;
          //Zähler für erfolgreiche Checks pro Tag
          var checksPassed : number[] = new Array(this.chipOptions.length).fill(0);
          //Zähler für erfolgreiche Checks letzten Monat pro Tag
          var checksPassedLastMonth : number[] = new Array(this.chipOptions.length).fill(0);
          for (var j = 0; j < checkResults.length - 1; j++) {
            // wenn der Check erfolgreich war erhöhe die Zähler
            if (checkResults[j].result) {
              checksPassed[this.chipOptions.indexOf(checkResults[j].tag)] += 1; 
            }
            if (checkResultsLastMonth[j].result) {
              checksPassedLastMonth[this.chipOptions.indexOf(checkResults[j].tag)] += 1;
            }
          }
          var newChecksPassedLastMonth : number[] = new Array(this.chipOptions.length);
          for (var j = 0; j < this.chipOptions.length; j++){
            newChecksPassedLastMonth[j] = checksPassed[j] - checksPassedLastMonth[j];
          }
          //var info : GridInfo = {project : val.name, testsPassed: checksPassed};
          this.all_projects[i].passedTestsPerTag = checksPassed;
          this.all_projects[i].newPassedTestsPerTagLastMonth = newChecksPassedLastMonth;
          this.all_projects[i].passedTestsInFilter = 0;
          this.all_projects[i].newPassedTestsLastMonth = 0;

        });
    }
 
  }

  sortProjects(){
    //stelle die initale unsortierte Reihenfolge der Projekte wieder her
    this.all_projects = this.init_all_projects.slice();

    //aktualisiere die Filter Info
    this.filterInfo = "Momentan sortiert nach Tag: ";
    for(var i = 0; i < this.chipsControl.value.length; i++){
      this.filterInfo += this.chipsControl.value[i];
      if(i != this.chipsControl.value.length - 1){
        this.filterInfo += ", ";
      }
    } 
    this.filterInfo += " und Kategorie: " + this.searchCriteria.value; 
    
    this.removeAllProjectsFromOverview();

    //setze die notwendigen variablen fürs sortieren der projekte nach den gewählten tags 
    for(let project in this.all_projects){
      this.all_projects[project].passedTestsInFilter = 0;
      this.all_projects[project].newPassedTestsLastMonth = 0;
      for (var i = 0; i < this.chipOptions.length; i++){
        for(var j = 0; j < this.chipsControl.value.length; j++){
          /*
          if(this.chipsControl.value[j].includes("check")){
            this.chipsControl.value[j] = this.chipsControl.value[j].replace("check","");
          }
          this.chipsControl.value[j] = this.chipsControl.value[j].trim();
          */
          if(this.chipsControl.value[j] == this.chipOptions[i]){
            this.all_projects[project].passedTestsInFilter += this.all_projects[project].passedTestsPerTag[i];
            this.all_projects[project].newPassedTestsLastMonth += this.all_projects[project].newPassedTestsPerTagLastMonth[i];
          }
        }
      }
    }
    
    //wähle die sortier funktion nach eingabe
    console.log('Projekte for sortieren!', this.all_projects);
    switch (this.searchCriteria.value) {
      case 'Bestandene Tests': {
        this.all_projects.sort(this.compareTestsPassedPerActivFilter);
        break;
      }
      case 'Neue bestandene Tests in den letzten 30 Tagen': {
        this.all_projects.sort(this.compareNewTestsPassedSinceLastMonthFilter);
        break;
      }
    
    }
    console.log('Projekte nach sortieren', this.all_projects);
    
    for (let project in this.all_projects) {
      this.addComponent(
        this.all_projects[project].name,
        this.all_projects[project].id,
        this.all_projects[project].url
      );
    }
    

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

  compareNewTestsPassedSinceLastMonthFilter(a, b) {
    if (a.newPassedTestsLastMonth < b.newPassedTestsLastMonth) {
      return 1;
    }
    if (a.newPassedTestsLastMonth > b.newPassedTestsLastMonth) {
      return -1;
    }
    return 0;
  }
  compareTestsPassedPerActivFilter(a,b){
    if (a.passedTestsInFilter < b.passedTestsInFilter) {
      return 1;
    }
    if (a.passedTestsInFilter > b.passedTestsInFilter) {
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
  passedTestsInFilter: number;
  newPassedTestsLastMonth: number;
  passedTestsPerTag: number[];
  newPassedTestsPerTagLastMonth: number[];
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
