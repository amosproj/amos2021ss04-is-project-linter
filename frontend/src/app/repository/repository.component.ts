import { MatDialog } from '@angular/material/dialog';
import { Component, Input, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { RepositoryDetailsComponent } from '../repository-details/repository-details.component';
import { Project } from '../schemas';

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
    public dialog: MatDialog
  ) {}

  ngOnInit(): void {}

  showDetailsViaDialog() {
    //Erstellt das Dialogfenster
    let dialogRef = this.dialog.open(RepositoryDetailsComponent, {
      width: '80%',
      height: '95%',
      panelClass: 'custom-dialog-container',
      data: this.project.id,
    });
  }

  truncateDescription() {
    if (this.project.description == '') {
      return 'Keine Beschreibung vorhanden';
    } else if (this.project.description.length <= 100) {
      return this.project.description;
    } else {
      return this.project.description.substring(0, 100) + '...';
    }
  }
}
