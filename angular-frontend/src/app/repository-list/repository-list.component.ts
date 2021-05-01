import { Component, OnInit } from '@angular/core';
import { Repositories } from '../repositories';

@Component({
  selector: 'app-repository-list',
  templateUrl: './repository-list.component.html',
  styleUrls: ['./repository-list.component.css']
})
export class RepositoryListComponent implements OnInit {

  repos = Repositories;
  constructor() {}

  ngOnInit(): void {
  }

}
