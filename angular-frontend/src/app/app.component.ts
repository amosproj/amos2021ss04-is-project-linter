import { ComponentFactoryResolver } from '@angular/core';
import { ViewContainerRef } from '@angular/core';
import { ViewChild } from '@angular/core';
import { Component } from '@angular/core';
import {FormBuilder, FormControl, FormGroup} from '@angular/forms';
import { RepositoryDetailsComponent } from './repository-details/repository-details.component';
import { RepositoryListComponent } from './repository-list/repository-list.component';
import { HttpClient, HttpClientModule } from '@angular/common/http';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'angular-frontend';
  value = '';
 
  serverID = "localhost:8080/projects"
  options: FormGroup;
  hideRequiredControl = new FormControl(false);
  floatLabelControl = new FormControl('auto');
  @ViewChild('parent', { read: ViewContainerRef }) container: ViewContainerRef;
  onEnter(value: string) { this.value = value;
 // this.forwardLink("localhost:8080/projects",value);

}

  forwardLink(serverID,gitID){
    this.http.post<any>(serverID,
    {
        "data": gitID
    })
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
constructor(fb: FormBuilder,private _cfr: ComponentFactoryResolver,private http: HttpClient) {
  this.options = fb.group({
    hideRequired: this.hideRequiredControl,
    floatLabel: this.floatLabelControl,
  });


}

ngOnInit(){ }


  addComponent(){    
    var comp = this._cfr.resolveComponentFactory(RepositoryDetailsComponent);
    var expComponent = this.container.createComponent(comp);
    expComponent.instance._ref = expComponent;
}
}

