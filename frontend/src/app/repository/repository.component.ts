import { MatDialog } from '@angular/material/dialog';
import { Component, Input, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';

import { RepositoryDetailsComponent } from '../repository-details/repository-details.component';
import { Project } from '../schemas'

@Component({
  selector: 'app-repository',
  templateUrl: './repository.component.html',
  styleUrls: ['./repository.component.css'],
})
export class RepositoryComponent implements OnInit {
  @Input() project: Project;
  @ViewChild(RepositoryDetailsComponent) child;

  constructor(
    public route: ActivatedRoute,
    public router: Router,
    public dialog: MatDialog,
    private http: HttpClient
  ) {}

  ngOnInit(): void {}

  showDetailsViaDialog() {
    //Erstellt das Dialogfenster
    let dialogRef = this.dialog.open(RepositoryDetailsComponent, {
      width: '77%',
      height: '96%',
      panelClass: 'custom-dialog-container',
      data: { project: this.project },
    });
  }

  fixOverflowingDescription() {
    return this.project.description.substring(0, 100) + '...';
  }
}
