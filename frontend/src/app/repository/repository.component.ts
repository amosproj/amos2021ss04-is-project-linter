import { MatDialog } from '@angular/material/dialog';
import { Component, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { RepositoryDetailsComponent } from '../repository-details/repository-details.component';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-repository',
  templateUrl: './repository.component.html',
  styleUrls: ['./repository.component.css'],
})
export class RepositoryComponent implements OnInit {
  _ref: any;
  project: Project;
  @ViewChild(RepositoryDetailsComponent) child;
  removeObject() {
    this._ref.destroy();
  }
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
}

// Interface für die repository Komponente welche grob die Informationen des repository zeigt
interface Project {
  gitlabInstance: string;
  gitlabProjectId: number;
  id: number;
  name: string;
  description: string;
  results: [];
  url: string;
  passedTestsInFilter: number;
  newPassedTestsLastMonth: number;
  passedTestsPerTag: number[];
  newPassedTestsPerTagLastMonth: number[];

  lintingResults: LintingResult[];
}

// Zum speichern der Daten des Projekts
interface CheckResults {
  checkName: string;
  severity: string;
  result: boolean;
  category: string;
  description: string;
  tag: string;
  fix: string;
  priority: number;
  message: string; // ist Fehlermeldung
}

// Zum Speichern der Daten eines LintingResult
interface LintingResult {
  projectId: number;
  id: number;
  lintTime: string;
  checkResults: CheckResults[];
}
