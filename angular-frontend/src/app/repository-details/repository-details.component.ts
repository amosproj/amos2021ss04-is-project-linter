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
              //console.log(val.results[10].checkResults);
              var last_entry = val.results.length
              this.checkResults = val.results[last_entry-1].checkResults;
            
        },
        response => {
            console.log("GET call in error", response);
        },
        () => {
            console.log("The GET observable is now completed.");
        });
    
  }
  returnEmoji(input){
    if (input.result)
      return emojiMap.correct;
    else
      return emojiMap.false;
  }
  isExistencial(input){
    if(input.severity =="HIGH"){
      console.log("isTrue")
      return true;
    }
    console.log("false")
    return false;
  }
  isNonExistencial(input){
    if(input.severity =="MEDIUM"){
      return true;
    }
    return false;
  }
  isInteresting(input){
    if(input.severity == "NOT_SPECIFIED"){
      return true;
    }
    return false;
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
