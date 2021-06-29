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
  @Input() projectAndTags: {project: Project; tags: string[]};
  @ViewChild(RepositoryDetailsComponent) child;

  constructor(
    public route: ActivatedRoute,
    public router: Router,
    public dialog: MatDialog
  ) {}

  ngOnInit(): void {}

  openDetailsDialog() {
    // Öffnet das Dialogfenster für die Repository Details
    let dialogRef = this.dialog.open(RepositoryDetailsComponent, {
      width: '85%',
      height: '95%',
      panelClass: 'custom-dialog-container',
      data: {projectId: this.projectAndTags.project.id, tags: this.projectAndTags.tags},
    });
  }

  truncateDescription() {
    if (this.projectAndTags.project.description == '') {
      return 'Keine Beschreibung vorhanden';
    } else if (this.projectAndTags.project.description.length <= 100) {
      return this.projectAndTags.project.description;
    } else {
      return this.projectAndTags.project.description.substring(0, 100) + '...';
    }
  }
}
