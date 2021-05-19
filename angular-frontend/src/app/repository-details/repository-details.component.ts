import { HttpClient } from '@angular/common/http';
import { Component, OnInit, Inject } from '@angular/core';
import { MatDialog,MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  selector: 'app-repository-details',
  templateUrl: './repository-details.component.html',
  styleUrls: ['./repository-details.component.css']
})

export class RepositoryDetailsComponent implements OnInit {

  lastLintTime;
  RepoName = "";
  RepoURL  = "";
  latestLintingResults:  CheckResults[];
  checksHighSeverity:  CheckResults[];
  checksMediumSeverity:CheckResults[];
  checksLowSeverity:   CheckResults[];
  categories: String[];
  LintingResultsInCategories: CheckResults[][];
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
              // fill linting category array
              var last_entry = val.lintingResults.length;
              this.latestLintingResults = val.lintingResults[last_entry-1].checkResults;
              this.fillSeverityArrays();
              this.groupLintingResultsInCategories();
              // save information
              this.RepoName = val.name;
              this.RepoURL  = val.url;
              this.lastLintTime = val.lintingResults[last_entry-1].lintTime;
        },
        response => {
            console.log("GET call in error", response);
        },
        () => {
            console.log("The GET observable is now completed.");
        });
    
  }

  fillCategoriesArray(){
    for(var i = 0; i < this.latestLintingResults.length; i++){
      var categoryAlreadyThere = false;
      // Check if categories has entries
      if(this.categories){
        // Check if categories contains the current category
        for(var j = 0; j < this.categories.length; j++){
          if(this.categories[j] == this.latestLintingResults[i].category){
            categoryAlreadyThere = true;
          }
        }
      }else{
        this.categories = new Array<String>(); 
      }
      if(!categoryAlreadyThere){
        this.categories.push(this.latestLintingResults[i].category);
      }
    }
  }

  groupLintingResultsInCategories(){
    // Determine categories
    this.fillCategoriesArray();
    // Group the linting results to their corresponding categories
    this.LintingResultsInCategories = new Array(this.categories.length);
    for(var i = 0; i < this.latestLintingResults.length; i++){
      // Get index of category in categories
      var index = this.categories.indexOf(this.latestLintingResults[i].category);
      // Check if array needs to be initialized
      if(!this.LintingResultsInCategories[index]){
        this.LintingResultsInCategories[index] = [];
      }
      // Push test into category corresponding index
      this.LintingResultsInCategories[index].push(this.latestLintingResults[i]);
    }
  }

  fillSeverityArrays(){
    for(var i = 0; i < this.latestLintingResults.length; i++){
      if(this.latestLintingResults[i].severity == "HIGH"){
        this.checksHighSeverity.push(this.latestLintingResults[i])
      }else if(this.latestLintingResults[i].severity == "MEDIUM"){
        this.checksMediumSeverity.push(this.latestLintingResults[i])
      }else if(this.latestLintingResults[i].severity == "LOW"){
        this.checksLowSeverity.push(this.latestLintingResults[i])
      }
    }
  }

  returnEmojiBasedOnSeverity(input){
    if (input.result)
      return emojiMap.correct;
    else if(input.severity == "HIGH")
      return emojiMap.false;
    else if(input.severity == "MEDIUM")
      return emojiMap.warning;
    else if(input.severity == "Low")
      return emojiMap.notImportant
    //else
    //throw error ? or display error message
  }
}
const emojiMap = {
  /*unwichtig:"〰️",
  warning: "⚠️",
  false: "❌",
  correct: "✅"*/
  notImportant:"🟡",
  warning:     "🟠",
  false:       "🔴",
  correct:     "🟢"
}

export interface DialogData {
  serverID: string;
  projectID:number;
}

interface CheckResults {
  checkName:string,
  severity:string,
  result:boolean,
  category:string,
  description:string,
  fix:string,
  message:string // is errormessage
 // errormessage:string

}
