import { ComponentFactoryResolver, Input } from '@angular/core';
import { ViewContainerRef } from '@angular/core';
import { ViewChild } from '@angular/core';
import { Component } from '@angular/core';
import { ControlValueAccessor, FormBuilder, FormControl, FormGroup } from '@angular/forms';
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
export class AppComponent implements OnInit  {
  title = 'frontend';
  projectComponents = [];
  chipsControl = new FormControl('');
  chipsValue$ = this.chipsControl.valueChanges;
  //SearchBarValue = '';
  kategorie = new FormControl("");
  all_projects: Project[];
  options: FormGroup;
  forwardLinkWorked = true;
  errorMsgForwardLink = '';
  hideRequiredControl = new FormControl(false);
  floatLabelControl = new FormControl('auto');

  chipOptions: string[];

  gridInfo: GridInfo[] = new Array<GridInfo>();

  dataArray: GridInfo[] = new Array<GridInfo>();
  displayColumns : string[] = ['project' ,'testsPassed', 'testsPassedPerTag'];

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
    this.chipOptions=[];
    for (let [key, value] of Object.entries(configFile.checks)) {
      if(!this.chipOptions.includes(value.tag)){
        this.chipOptions.push(value.tag);
      }
    }
    return this.chipOptions;

  }

  createStatistik(event: Event){
    this.getProjectInfoForStatistik();
  }

  getProjectInfoForStatistik() {
    //lade projekte, vlt überflüssig
    this.GetProjects();
    //für jedes projekt
    for (var i = 0 ; i < this.all_projects.length ; i++){
      //lade ergebnisse der checks aus dem backend
      this.http.get(`${environment.baseURL}/project/${this.all_projects[i].id}`).subscribe((val: any) => {
        var checkResults : CheckResults[] = val.lintingResults[val.lintingResults.length - 1].checkResults;
        //Zähler für erfolgreiche Checks
        var checksPassed = 0;
        //Zähler für erfolgreiche Checks pro Tag
        var checksPassedPerTag:number[] = new Array(this.chipOptions.length);
        for( var  j = 0 ; j < this.chipOptions.length; j++){
          checksPassedPerTag[j] = 0;
        }
        for (var j = 0; j < checkResults.length; j++){
          // wenn der Check erfolgreich war erhöhe die Zähler
          if(checkResults[j].result){
            checksPassed = checksPassed + 1;
            checksPassedPerTag[this.chipOptions.indexOf(checkResults[j].tag)] = checksPassedPerTag[this.chipOptions.indexOf(checkResults[j].tag)] + 1;
          }
        }
        //var info : GridInfo = {project : val.name, testsPassed: checksPassed};
        var info : GridInfo = {project : val.name, testsPassed: checksPassed, testsPassedPerTag: checksPassedPerTag};
        this.gridInfo.push(info);
      });
    }
    //wähle die sortier funktion nach eingabe
    this.gridInfo.sort(this.compareOnlyTestsPassed);
    console.log('Grid Info', this.gridInfo);
    var item : GridInfo;
    console.log('hier!!!');
    for (var index in this.gridInfo){
      item = this.gridInfo[index];
      this.dataArray.push(item);
    }
    
    this.data.data = this.dataArray;
  }

  compareOnlyTestsPassed(a, b) {
    if(a.testsPassed < b.testsPassed){
      return 1;
    }
    if(a.testsPassed > b.testsPassed){
      return -1;
    }
    return 0;
  }

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
  testsPassedPerTag : number[];
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