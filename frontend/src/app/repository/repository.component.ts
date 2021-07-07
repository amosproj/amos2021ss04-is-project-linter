import { MatDialog } from '@angular/material/dialog';
import { Component, Input, ViewChild } from '@angular/core';

import { RepositoryDetailsComponent } from '../repository-details/repository-details.component';
import { Project } from '../schemas';

@Component({
  selector: 'app-repository',
  templateUrl: './repository.component.html',
  styleUrls: ['./repository.component.css'],
})
export class RepositoryComponent {
  @Input() project: Project;
  @ViewChild(RepositoryDetailsComponent) child;

  constructor(public dialog: MatDialog) {}

  openDetailsDialog() {
    // Öffnet das Dialogfenster für die Repository Details
    let dialogRef = this.dialog.open(RepositoryDetailsComponent, {
      width: '85%',
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

  truncateName() {
    if (this.project.name == '') {
      return 'Kein Name Vorhanden';
    } else if (this.project.name.length <= 40) {
      return this.project.name;
    } else {
      return this.project.name.substring(0, 40) + '...';
    }
  }
}
