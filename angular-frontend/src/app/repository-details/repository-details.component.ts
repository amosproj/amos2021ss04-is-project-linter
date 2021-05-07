import { HttpClient } from '@angular/common/http';
import { Component, OnInit, Inject } from '@angular/core';
import { MatDialog,MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  selector: 'app-repository-details',
  templateUrl: './repository-details.component.html',
  styleUrls: ['./repository-details.component.css']
})

export class RepositoryDetailsComponent implements OnInit {

  checkResults:[];
  constructor(public dialogRef: MatDialogRef<RepositoryDetailsComponent>,
  @Inject(MAT_DIALOG_DATA) public data: DialogData,private http: HttpClient){} 


  ngOnInit(): void {
    
    this.ShowProjectDetails(this.data.serverID,this.data.projectID);
    
  
  }
  
  closeDialog(){
    this.dialogRef.close();
  }

  ShowProjectDetails(serverID, gitID){
    this.http.get(serverID+"project/"+gitID)
    /*{ // currently it you can only send the pure URL and not as a JSON
        "data": gitID
    })*/
    .subscribe(
        (val:any) => {
            console.log("GET call successful value returned in body", 
                        val);
              console.log(val.results[10].checkResults);
              this.checkResults = val.results[10].checkResults;
            
        },
        response => {
            console.log("GET call in error", response);
        },
        () => {
            console.log("The GET observable is now completed.");
        });
    
  }

}

export interface DialogData {
  serverID: string;
  projectID:number;
}

interface CheckResults {
  checkName:string,
  severity:string
 // errormessage:string

}
