import { Component } from '@angular/core';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'angular-frontend';
  value = '';
  http: any;
  onEnter(value: string) { this.value = value;}

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
  }

