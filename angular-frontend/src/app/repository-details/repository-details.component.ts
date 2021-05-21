import { HttpClient } from '@angular/common/http';
import { Component, OnInit, Inject } from '@angular/core';
import { MatDialogModule,MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

import * as dayjs from 'dayjs'

@Component({
  selector: 'app-repository-details',
  templateUrl: './repository-details.component.html',
  styleUrls: ['./repository-details.component.css']
})

export class RepositoryDetailsComponent implements OnInit {

  
  emojiMap = {
    /*unwichtig:"〰️",
    warning: "⚠️",
    false: "❌",
    correct: "✅"*/
    notImportant:"🟡",
    warning:     "🟠",
    false:       "🔴",
    correct:     "🟢",
    bug: "🐛"
  }

  lastLintTime;
  RepoName = "";
  RepoURL  = "";
  checksHighSeverity:    CheckResults[];  // currently not in use
  checksMediumSeverity:  CheckResults[];  // currently not in use
  checksLowSeverity:     CheckResults[];  // currently not in use
  latestLintingResults:  CheckResults[];
  categories: String[];
  LintingResultsInCategories: CheckResults[][];
  maxColsForTiles=9
  tiles: Tile[] = [
    {text: 'Kategorien',             cols: 5, rows:6, color: 'white'},
    {text: 'Ergebnisse Aller Tests', cols: 4, rows: 2, color: 'white'},
  ];
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
              //this.fillSeverityArrays(); // currently does not need to be used
              this.groupLintingResultsInCategories();
              // save information
              this.RepoName = val.name;
              this.RepoURL  = val.url;
              this.lastLintTime = dayjs(val.lintingResults[last_entry-1].lintTime).format("DD.MM.YYYY - H:mm");
              // dynamically create missing tiles for grid list corresponding to their grid list
              this.addTilesForCategoryGraphAndFooter()
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

  addTilesForCategoryGraphAndFooter(){
    for(var i = 0; i < this.categories.length; i++){
      var t = <Tile>{color: "white", cols: 2, rows:2, text: this.categories[i]};
      this.tiles.push(t)
    }
  }

  returnEmojiBasedOnSeverity(input){
    if (input.result)
      return this.emojiMap.correct;
    else if(input.severity == "HIGH")
      return this.emojiMap.false;
    else if(input.severity == "MEDIUM")
      return this.emojiMap.warning;
    else if(input.severity == "Low")
      return this.emojiMap.notImportant
    else
      return this.emojiMap.bug
  }
  
}

declare const require: any; //used for loading svg

// For getting the project
export interface DialogData {
  serverID: string;
  projectID:number;
}

// For storing the information on the project
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

// For angular tiles
export interface Tile {
  color: string;
  cols: number;
  rows: number;
  text: string;
}
