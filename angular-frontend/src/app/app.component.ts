import { RepositoryDetailsComponent } from './repository-details/repository-details.component';
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
import { MatDialogModule, MatDialog } from '@angular/material/dialog';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
})
export class AppComponent {
  title = 'angular-frontend';
  //SearchBarValue = '';
  all_projects: Project[];
  serverID = 'http://localhost:6969/api/';
  options: FormGroup;
  forwardLinkWorked = true;
  errorMsgForwardLink = '';

  hideRequiredControl = new FormControl(false);
  floatLabelControl = new FormControl('auto');
  @ViewChild('parent', { read: ViewContainerRef }) container: ViewContainerRef;
  //onEnter(SearchBarValue: string) { this.SearchBarValue = SearchBarValue;
  //this.forwardLink("http://localhost:6969/api/projects",SearchBarValue);
  //}

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
  forwardLink(serverID, URL) {
    const headers = { 'Content-Type': 'text/html' };

    let HTTPOptions: Object = {
      headers: new HttpHeaders({
        'Content-Type': 'application/json',
      }),
      responseType: 'text',
    };

    this.http.post<String>(serverID, URL, HTTPOptions).subscribe(
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
      /*() => {
            console.log("The POST observable is now completed.");
            this.errorMsgForwardLink = 'Internal server error'
            this.forwardLinkWorked = false;
        }*/
    );
  }

  GetProjects(serverID) {
    this.http.get(serverID).subscribe((results) => {
      this.all_projects = JSON.parse(JSON.stringify(results)) as Project[];
      console.log(this.all_projects);

      for (let project in this.all_projects) {
        this.addComponent(
          this.all_projects[project].name,
          this.all_projects[project].id,
          this.all_projects[project].url
        );
      }
    });
    /*{ // currently it you can only send the pure URL and not as a JSON
      "data": gitID
  })*/
  }

  ngOnInit() {
    this.GetProjects('http://localhost:6969/api/projects');
  }

  addComponent(name, id, gitlabInstance) {
    var comp = this._cfr.resolveComponentFactory(RepositoryComponent);
    var expComponent = this.container.createComponent(comp);
    expComponent.instance._ref = expComponent;
    expComponent.instance.name = name;
    expComponent.instance.id = id;
    expComponent.instance.gitlabInstance = gitlabInstance;
    expComponent.instance.serverID = this.serverID;
  }
} // end of AppComponent

// Interface for the repository component which shows coarse repo infos
interface Project {
  gitlabInstance: string;
  gitlabProjectId: number;

  id: number;
  name: string;
  results: [];
  url: string;
}
