import { ChangeDetectorRef } from '@angular/core';
import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
})
export class AppComponent {
  selectedIndex = 0;

  constructor(private router: Router, private cdRef: ChangeDetectorRef) {}

  selectTab(index: number): void {
    this.selectedIndex = index;
    this.cdRef.detectChanges();
  }

  checkTabChange($event) {
    if ($event.index == 0) {
      this.router.navigate(['projects']);
    }
    if ($event.index == 1) {
      this.router.navigate(['statistics']);
    }
    if ($event.index == 2) {
      this.router.navigate(['status']);
    }
  }
}
