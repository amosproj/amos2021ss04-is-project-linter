import { APP_INITIALIZER, NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule } from '@angular/common/http';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { CommonModule } from '@angular/common';
import { FlexLayoutModule } from '@angular/flex-layout';

import { AppComponent } from './app.component';
import { ApiService } from './api.service';
import { StateService } from './state.service';
import { MaterialModule } from './material/material.module';
import { AppRoutingModule } from './app-routing.module';
import { RepositoryComponent } from './repository/repository.component';
import { RepositoryDetailsComponent } from './repository-details/repository-details.component';
import { SpinnerComponentComponent } from './spinner-component/spinner-component.component';
import { FooterComponent } from './footer/footer.component';
import { HeaderComponent } from './header/header.component';
import { ProjectsTabComponent } from './projects-tab/projects-tab.component';
import { StatisticsTabComponent } from './statistics-tab/statistics-tab.component';
import { StatusTabComponent } from './status-tab/status-tab.component';
import { SearchComponent } from './search/search.component';
import { NavComponent } from './nav/nav.component';

export function init_app(state: StateService) {
  return () => state.loadConfig();
}

@NgModule({
  declarations: [
    AppComponent,
    RepositoryComponent,
    RepositoryDetailsComponent,
    SpinnerComponentComponent,
    FooterComponent,
    HeaderComponent,
    ProjectsTabComponent,
    StatisticsTabComponent,
    StatusTabComponent,
    SearchComponent,
    NavComponent,
  ],
  entryComponents: [RepositoryDetailsComponent],
  imports: [
    BrowserModule,
    CommonModule,
    AppRoutingModule,
    HttpClientModule,
    BrowserAnimationsModule,
    MaterialModule,
    FlexLayoutModule,
  ],
  providers: [
    ApiService,
    StateService,
    {
      provide: APP_INITIALIZER,
      useFactory: init_app,
      deps: [StateService],
      multi: true,
    },
  ], // Make available throughout entire app
  bootstrap: [AppComponent],
})
export class AppModule {}
