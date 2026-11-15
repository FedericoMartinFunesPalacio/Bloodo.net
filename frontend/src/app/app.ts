import { Component } from '@angular/core';
import { RouterOutlet, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { NavbarComponent } from './components/reusable/navbar/navbar';
import { ToastComponent } from './components/reusable/toast/toast';
import { ConfirmDialogComponent } from './components/reusable/confirm-dialog/confirm-dialog';
import { SharedFooterComponent } from './components/reusable/shared-footer/shared-footer';
import { FaqModalComponent } from './components/reusable/faq-modal/faq-modal';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, CommonModule, NavbarComponent, ToastComponent, ConfirmDialogComponent, SharedFooterComponent, FaqModalComponent],
  templateUrl: './app.html',
  styleUrls: ['./app.css']
})
export class App {
  constructor(private router: Router) {}

  isAuthPage(): boolean {
    const url = this.router.url;
    return url === '/' || url.startsWith('/auth') || url.startsWith('/register') || url.startsWith('/forgot-password');
  }

  isLandingPage(): boolean {
    return this.router.url === '/';
  }
}
