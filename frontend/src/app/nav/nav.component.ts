import { Component } from '@angular/core';

@Component({
  selector: 'app-nav',
  templateUrl: './nav.component.html',
  styleUrls: ['./nav.component.css'],
})
export class NavComponent {
  constructor() {}

  links = [
    { name: 'Projekte', path: 'projects' },
    { name: 'Statistiken', path: 'statistics' },
    { name: 'Status', path: 'status' },
  ];
}
