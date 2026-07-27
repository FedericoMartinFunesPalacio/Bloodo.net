import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';

export type ToastType = 'success' | 'error' | 'info' | 'warning';

export interface Toast {
  id: number;
  message: string;
  type: ToastType;
  duration: number;
}

@Injectable({ providedIn: 'root' })
export class ToastService {
  private toasts$ = new BehaviorSubject<Toast[]>([]);
  private counter = 0;

  getToasts(): Observable<Toast[]> {
    return this.toasts$.asObservable();
  }

  show(message: string, type: ToastType = 'info', duration = 4000): void {
    const toast: Toast = { id: ++this.counter, message, type, duration };
    this.toasts$.next([...this.toasts$.value, toast]);

    if (duration > 0) {
      setTimeout(() => this.dismiss(toast.id), duration);
    }
  }

  success(message: string, duration = 4000): void {
    this.show(message, 'success', duration);
  }

  error(message: string, duration = 5000): void {
    this.show(message, 'error', duration);
  }

  info(message: string, duration = 4000): void {
    this.show(message, 'info', duration);
  }

  warning(message: string, duration = 4500): void {
    this.show(message, 'warning', duration);
  }

  dismiss(id: number): void {
    this.toasts$.next(this.toasts$.value.filter(t => t.id !== id));
  }

  confirm(message: string, title = 'Confirmar'): Promise<boolean> {
    return new Promise(resolve => {
      this._confirmState = { message, title, resolve };
      this._confirmVisible$.next(true);
    });
  }

  private _confirmState: { message: string; title: string; resolve: (v: boolean) => void } | null = null;
  private _confirmVisible$ = new BehaviorSubject<boolean>(false);

  get confirmVisible$(): Observable<boolean> {
    return this._confirmVisible$.asObservable();
  }

  get confirmState() {
    return this._confirmState;
  }

  resolveConfirm(result: boolean): void {
    this._confirmState?.resolve(result);
    this._confirmState = null;
    this._confirmVisible$.next(false);
  }
}
