import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AppComponent } from '../app.component';
import { Repositories } from '../repositories';

@Component({
  selector: 'app-repository-details',
  templateUrl: './repository-details.component.html',
  styleUrls: ['./repository-details.component.css']
})
export class RepositoryDetailsComponent implements OnInit {
  
  _ref:any;  
  gitlabInstance = '';
  name = '';
  id = 0; 
  image: '../assets/FinalesLogo.png';
  removeObject(){
    this._ref.destroy();
  }
  repos = Repositories;
  constructor(public route: ActivatedRoute, public router: Router) { }

  ngOnInit(): void {
   
  }

}
