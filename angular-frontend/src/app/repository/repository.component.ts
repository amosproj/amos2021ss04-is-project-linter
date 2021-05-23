import { MatDialog } from '@angular/material/dialog';
import { Component, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { RepositoryDetailsComponent } from '../repository-details/repository-details.component';
import { HttpClient, HttpClientModule } from '@angular/common/http';

@Component({
  selector: 'app-repository',
  templateUrl: './repository.component.html',
  styleUrls: ['./repository.component.css']
})
export class RepositoryComponent implements OnInit {
  
  serverID = ""
  _ref:any;  
  gitlabInstance = '';
  name = '';
  id = 0; 
  @ViewChild(RepositoryDetailsComponent) child;
  removeObject(){
    this._ref.destroy();
  }
  constructor(public route: ActivatedRoute, public router: Router, public dialog: MatDialog,private http: HttpClient) { }

  ngOnInit(): void {
   
  }
  showDetailsViaDialog()
  {
    //console.log(this.id)
    //Hier sollte man die details per id getten
    let dialogRef = this.dialog.open(RepositoryDetailsComponent, {width: '77%',height:'96%', panelClass: "custom-dialog-container", data: {serverID: this.serverID,projectID:this.id}})

  }
  
  
}
