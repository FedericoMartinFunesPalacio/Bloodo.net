import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { Subscription } from 'rxjs';
import { ToastService } from '../../services/toast.service';

@Component({
  selector: 'app-confirm-dialog',
  standalone: true,
  imports: [CommonModule, MatIconModule],
  templateUrl: './confirm-dialog.html',
  styleUrls: ['./confirm-dialog.css']
})
export class ConfirmDialogComponent implements OnInit, OnDestroy {
  visible = false;
  title = '';
  message = '';
  private sub!: Subscription;

  constructor(private toastService: ToastService) {}

  ngOnInit(): void {
    this.sub = this.toastService.confirmVisible$.subscribe(visible => {
      this.visible = visible;
      if (visible && this.toastService.confirmState) {
        this.title = this.toastService.confirmState.title;
        this.message = this.toastService.confirmState.message;
      }
    });
  }

  ngOnDestroy(): void {
    this.sub.unsubscribe();
  }

  confirm(): void {
    this.toastService.resolveConfirm(true);
  }

  cancel(): void {
    this.toastService.resolveConfirm(false);
  }

  onOverlayClick(event: MouseEvent): void {
    if ((event.target as HTMLElement).classList.contains('confirm-overlay')) {
      this.cancel();
    }
  }
}
