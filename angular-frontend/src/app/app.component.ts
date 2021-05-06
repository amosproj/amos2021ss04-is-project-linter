import { RepositoryDetailsComponent } from './repository-details/repository-details.component';
import { ComponentFactoryResolver } from '@angular/core';
import { ViewContainerRef } from '@angular/core';
import { ViewChild } from '@angular/core';
import { Component } from '@angular/core';
import {FormBuilder, FormControl, FormGroup} from '@angular/forms';
import { RepositoryComponent } from './repository/repository.component';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import {MatDialogModule, MatDialog} from '@angular/material/dialog'; 



@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  
  title = 'angular-frontend';
  value = '';
  all_projects:Project[];
  serverID = "http://localhost:8080/projects"
  options: FormGroup;
  hideRequiredControl = new FormControl(false);
  floatLabelControl = new FormControl('auto');
  @ViewChild('parent', { read: ViewContainerRef }) container: ViewContainerRef;
  onEnter(value: string) { this.value = value;
 // this.forwardLink("http://localhost:8080/projects",value);
  }
constructor(fb: FormBuilder,private _cfr: ComponentFactoryResolver,private http: HttpClient) {
  this.options = fb.group({
    hideRequired: this.hideRequiredControl,
    floatLabel: this.floatLabelControl,
  });


}
  forwardLink(serverID,URL){
    this.http.post<any>(serverID,URL)
    /*{ // currently it you can only send the pure URL and not as a JSON
        "data": gitID
    })*/
    .subscribe(
        (val) => {
            console.log("POST call successful value returned in body", 
                        val);
        },
        response => {
            console.log("POST call in error", response);
        },
        () => {
            console.log("The POST observable is now completed.");
        });
}

GetProject(serverID, gitID){
  this.http.get(serverID+"/"+gitID)
  /*{ // currently it you can only send the pure URL and not as a JSON
      "data": gitID
  })*/
  .subscribe(
      (val:any) => {
          console.log("GET call successful value returned in body", 
                      val);
      },
      response => {
          console.log("GET call in error", response);
      },
      () => {
          console.log("The GET observable is now completed.");
      });
  
}

GetProjects(serverID){
  this.http.get(serverID).subscribe(

    results => {
      this.all_projects = JSON.parse(JSON.stringify(results)) as Project[];
      console.log(this.all_projects);

      for(let project in this.all_projects){
       this.addComponent(this.all_projects[project].name,this.all_projects[project].id,this.all_projects[project].url);
      }
    }

  );
  /*{ // currently it you can only send the pure URL and not as a JSON
      "data": gitID
  })*/
  
    }




ngOnInit(){ 
  this.GetProjects("http://localhost:8080/projects");

}


  addComponent(name, id, gitlabInstance){    
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

  
  gitlabInstance: string,
   gitlabProjectId:number,

   id: number,
   name: string,
   results:[],
   url:string

}

