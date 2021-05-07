import { MatDialog } from '@angular/material/dialog';
//import { RepositoryDetailsComponent } from './../repository-details/repository-details.component';
import { Component, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AppComponent } from '../app.component';
import { RepositoryDetailsComponent } from '../repository-details/repository-details.component';
import { Repositories } from '../repositories';
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
  image: '../assets/FinalesLogo.png';
  @ViewChild(RepositoryDetailsComponent) child;
  removeObject(){
    this._ref.destroy();
  }
  repos = Repositories;
  constructor(public route: ActivatedRoute, public router: Router, public dialog: MatDialog,private http: HttpClient) { }

  ngOnInit(): void {
   
  }
  showDetailsViaDialog()
  {
    //Hier sollte man die details per id getten
    let dialogRef = this.dialog.open(RepositoryDetailsComponent, {width: '1000px', data: {serverID: this.serverID,projectID:this.id}})

  }
  
  
}
