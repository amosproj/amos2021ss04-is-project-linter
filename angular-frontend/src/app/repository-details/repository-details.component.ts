import { HttpClient } from '@angular/common/http';
import { Component, OnInit, Inject } from '@angular/core';
import { MatDialog,MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  selector: 'app-repository-details',
  templateUrl: './repository-details.component.html',
  styleUrls: ['./repository-details.component.css']
})

export class RepositoryDetailsComponent implements OnInit {

  checkResults:        CheckResults[];
  checksHighSeverity:  CheckResults[];
  checksMediumSeverity:CheckResults[];
  checksLowSeverity:   CheckResults[];
  constructor(public dialogRef: MatDialogRef<RepositoryDetailsComponent>,
  @Inject(MAT_DIALOG_DATA) public data: DialogData,private http: HttpClient){} 


  ngOnInit(): void {
    // initialyze arrays sorted via severity void
    this.checksHighSeverity = new Array<CheckResults>(); 
    this.checksMediumSeverity = new Array<CheckResults>(); 
    this.checksLowSeverity = new Array<CheckResults>(); 
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
              //console.log(val.results[10].checkResults);
              var last_entry = val.lintingResults.length
              this.checkResults = val.lintingResults[last_entry-1].checkResults;
              this.fillSeverityArrays();
        },
        response => {
            console.log("GET call in error", response);
        },
        () => {
            console.log("The GET observable is now completed.");
        });
    
  }

  fillSeverityArrays()
  {
    for(var i = 0; i < this.checkResults.length; i++){
      if(this.checkResults[i].severity == "HIGH"){
        this.checksHighSeverity.push(this.checkResults[i])
      }else if(this.checkResults[i].severity == "MEDIUM"){
        this.checksMediumSeverity.push(this.checkResults[i])
      }else if(this.checkResults[i].severity == "LOW"){
        this.checksLowSeverity.push(this.checkResults[i])
      }
    }
  }

  returnEmoji(input){
    if (input.result)
      return emojiMap.correct;
    else
      return emojiMap.false;
  }
}
const emojiMap = {
  false: "❌",
  correct: "✅"
}

export interface DialogData {
  serverID: string;
  projectID:number;
}

interface CheckResults {
  checkName:string,
  severity:string,
  result:boolean
 // errormessage:string

}
