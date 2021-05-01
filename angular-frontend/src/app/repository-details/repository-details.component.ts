import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Repositories } from '../repositories';

@Component({
  selector: 'app-repository-details',
  templateUrl: './repository-details.component.html',
  styleUrls: ['./repository-details.component.css']
})
export class RepositoryDetailsComponent implements OnInit {
  
  details = { id: null, name: '',    image: '', URL: '', owner:    '', forks:    null };
  repos = Repositories;
  constructor(public route: ActivatedRoute, public router: Router) { }

  ngOnInit(): void {
    if (this.route.snapshot.paramMap.get('id') !== 'null') {
      const id = parseInt(this.route.snapshot.paramMap.get('id'), 0);
      this.details = this.repos.find(x => x.id === id);
    }
  }

}
