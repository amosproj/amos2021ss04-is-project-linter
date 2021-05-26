import { ComponentFactoryResolver } from '@angular/core';
import { ViewContainerRef } from '@angular/core';
import { ViewChild } from '@angular/core';
import { Component } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { RepositoryComponent } from './repository/repository.component';
import {
  HttpClient,
  HttpClientModule,
  HttpHeaders,
} from '@angular/common/http';
import { environment } from 'src/environments/environment';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
})
export class AppComponent {
  title = 'angular-frontend';
  projectComponents = [];
  //SearchBarValue = '';
  all_projects: Project[];
  options: FormGroup;
  forwardLinkWorked = true;
  errorMsgForwardLink = '';
  hideRequiredControl = new FormControl(false);
  floatLabelControl = new FormControl('auto');
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
