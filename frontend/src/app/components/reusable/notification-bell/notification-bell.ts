import { Component, OnInit, OnDestroy, ElementRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatIcon } from '@angular/material/icon';
import { NotificationService } from '../../../services/notification.service';
import { Notification } from '../../../models/notification';
import { AuthService } from '../../../services/auth.service';
import { Subject, takeUntil } from 'rxjs';

@Component({
  selector: 'app-notification-bell',
  standalone: true,
  imports: [CommonModule, MatIcon],
  templateUrl: './notification-bell.html',
  styleUrls: ['./notification-bell.css']
})
export class NotificationBellComponent implements OnInit, OnDestroy {
  isOpen = false;
  notifications: Notification[] = [];
  unreadCount = 0;
  private pollInterval: any = null;
  private destroy$ = new Subject<void>();
  private previousUserId: number | null = null;

  constructor(
    private notificationService: NotificationService,
    private authService: AuthService,
    private elementRef: ElementRef
  ) {}

  ngOnInit(): void {
    this.notificationService.unreadCount.pipe(takeUntil(this.destroy$)).subscribe(count => this.unreadCount = count);
    this.authService.getCurrentUser().pipe(takeUntil(this.destroy$)).subscribe(user => {
      const currentUserId = user ? Number(user.roleId) : null;
      if (currentUserId !== this.previousUserId) {
        this.previousUserId = currentUserId;
        this.notifications = [];
        this.unreadCount = 0;
        this.refreshData();
      }
    });
    this.pollInterval = setInterval(() => this.refreshData(), 30000);
  }

  ngOnDestroy(): void {
    if (this.pollInterval) clearInterval(this.pollInterval);
    this.destroy$.next();
    this.destroy$.complete();
  }

  refreshData(): void {
    if (!this.previousUserId) return;
    this.notificationService.refreshUnreadCount();
    this.notificationService.getNotifications().subscribe({
      next: (notificationsData) => this.notifications = notificationsData,
      error: () => {}
    });
  }

  toggle(): void {
    this.isOpen = !this.isOpen;
  }

  close(): void {
    this.isOpen = false;
  }

  markAsRead(notification: Notification, event: Event): void {
    event.stopPropagation();
    if (!notification.isRead) {
      this.notificationService.markAsRead(notification.id).subscribe({
        next: () => {
          this.notifications = this.notifications.filter(n => n.id !== notification.id);
          this.unreadCount = Math.max(0, this.unreadCount - 1);
        }
      });
    }
  }

  markAllAsRead(): void {
    this.notificationService.markAllAsRead().subscribe({
      next: () => {
        this.notifications = [];
        this.unreadCount = 0;
      }
    });
  }

  formatTime(dateStr: string): string {
    if (!dateStr) return '';
    const parts = dateStr.split('T');
    if (parts.length < 2) return '';
    const dateParts = parts[0].split('-');
    const timeParts = parts[1].split(':');
    const d = parseInt(dateParts[2], 10);
    const m = parseInt(dateParts[1], 10);
    const h = parseInt(timeParts[0], 10);
    const min = timeParts[1];
    const time = `${h}:${min}`;
    const date = `${d.toString().padStart(2, '0')}-${m.toString().padStart(2, '0')}`;

    const now = new Date();
    const notifDate = new Date(now.getFullYear(), now.getMonth(), now.getDate(), h, parseInt(min, 10));
    const diffMs = now.getTime() - notifDate.getTime();
    const diffMin = Math.floor(diffMs / 60000);
    if (diffMin < 1) return 'Ahora';
    if (diffMin < 60) return `Hace ${diffMin}m, ${date}`;
    const diffHrs = Math.floor(diffMin / 60);
    if (diffHrs < 24) return `Hace ${diffHrs}h ${min}, ${date}`;
    const diffDays = Math.floor(diffHrs / 24);
    return `${time}, ${date}`;
  }

  onBackdropClick(): void {
    this.close();
  }
}
