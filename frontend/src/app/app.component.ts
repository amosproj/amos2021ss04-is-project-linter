import { ChangeDetectorRef } from '@angular/core';
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
})
export class AppComponent implements OnInit {
  constructor(private router: Router,  private cdRef:ChangeDetectorRef ) {}
  selectedIndex = 0;
  ngOnInit() {}

  ngAfterViewInit() {}
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
