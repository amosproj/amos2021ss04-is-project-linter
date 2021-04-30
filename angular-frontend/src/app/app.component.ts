import { Component } from '@angular/core';
import {FormBuilder, FormControl, FormGroup} from '@angular/forms';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'angular-frontend';
  value = '';
  http: any;
  options: FormGroup;
  hideRequiredControl = new FormControl(false);
  floatLabelControl = new FormControl('auto');
  onEnter(value: string) { this.value = value;
  this.forwardLink("serverID",value);}

  forwardLink(serverID,gitID){
    this.http.post(serverID,
    {
        "url": gitID
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
constructor(fb: FormBuilder) {
  this.options = fb.group({
    hideRequired: this.hideRequiredControl,
    floatLabel: this.floatLabelControl,
  });
}
  }

