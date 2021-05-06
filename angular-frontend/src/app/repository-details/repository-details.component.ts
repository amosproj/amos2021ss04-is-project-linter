import { Component, OnInit, Inject } from '@angular/core';
import { MatDialog,MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  selector: 'app-repository-details',
  templateUrl: './repository-details.component.html',
  styleUrls: ['./repository-details.component.css']
})

export class RepositoryDetailsComponent implements OnInit {

  serverID = "";
  
  constructor(public dialogRef: MatDialogRef<RepositoryDetailsComponent>,
  @Inject(MAT_DIALOG_DATA) public data: DialogData){} 


  ngOnInit(): void {
  }
  
  closeDialog(){
    this.dialogRef.close();
  }
}

export interface DialogData {
  serverID: string;
}
