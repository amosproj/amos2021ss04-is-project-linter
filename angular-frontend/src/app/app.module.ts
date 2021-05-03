import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import {HttpClientModule} from '@angular/common/http';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MaterialModule } from './material/material.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RepositoryListComponent } from './repository-list/repository-list.component';
import { RepositoryDetailsComponent } from './repository-details/repository-details.component';


@NgModule({
  declarations: [
    AppComponent,
    RepositoryListComponent,
    RepositoryDetailsComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    BrowserAnimationsModule,
    MaterialModule,
  
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
