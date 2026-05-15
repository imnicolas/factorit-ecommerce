import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';

export type DialogType = 'info' | 'success' | 'error' | 'confirm';

export interface DialogState {
  type: DialogType;
  title: string;
  message: string;
  confirmLabel: string;
  cancelLabel?: string;
  danger?: boolean;
  resolve: (ok: boolean) => void;
}

export interface ConfirmOptions {
  title?: string;
  confirmLabel?: string;
  cancelLabel?: string;
  danger?: boolean;
}

/**
 * Servicio centralizado para mostrar diálogos.
 * Reemplaza alert() y confirm() nativos del browser.
 *
 * Uso:
 *   await this.dialog.info('Producto agregado');
 *   const ok = await this.dialog.confirm('¿Eliminar carrito?');
 */
@Injectable({ providedIn: 'root' })
export class DialogService {

  private state$ = new BehaviorSubject<DialogState | null>(null);

  get state(): Observable<DialogState | null> {
    return this.state$.asObservable();
  }

  info(message: string, title: string = 'Aviso'): Promise<void> {
    return this.open('info', title, message, { confirmLabel: 'Aceptar' }).then(() => {});
  }

  success(message: string, title: string = 'Listo'): Promise<void> {
    return this.open('success', title, message, { confirmLabel: 'Aceptar' }).then(() => {});
  }

  error(message: string, title: string = 'Error'): Promise<void> {
    return this.open('error', title, message, { confirmLabel: 'Aceptar' }).then(() => {});
  }

  confirm(message: string, opts: ConfirmOptions = {}): Promise<boolean> {
    return this.open('confirm', opts.title ?? 'Confirmar', message, {
      confirmLabel: opts.confirmLabel ?? 'Sí',
      cancelLabel: opts.cancelLabel ?? 'Cancelar',
      danger: opts.danger ?? false
    });
  }

  close(value: boolean) {
    const current = this.state$.value;
    if (current) {
      current.resolve(value);
      this.state$.next(null);
    }
  }

  private open(
    type: DialogType,
    title: string,
    message: string,
    opts: { confirmLabel: string; cancelLabel?: string; danger?: boolean }
  ): Promise<boolean> {
    return new Promise<boolean>((resolve) => {
      this.state$.next({
        type,
        title,
        message,
        confirmLabel: opts.confirmLabel,
        cancelLabel: opts.cancelLabel,
        danger: opts.danger,
        resolve
      });
    });
  }
}
