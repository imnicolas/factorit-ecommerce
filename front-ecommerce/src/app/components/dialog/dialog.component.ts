import { Component, OnDestroy, OnInit } from '@angular/core';
import { Subscription } from 'rxjs';
import { DialogService, DialogState } from '../../services/dialog.service';

@Component({
  selector: 'app-dialog',
  templateUrl: './dialog.component.html',
  styleUrls: ['./dialog.component.css']
})
export class DialogComponent implements OnInit, OnDestroy {

  state: DialogState | null = null;
  private sub?: Subscription;

  constructor(private dialog: DialogService) { }

  ngOnInit(): void {
    this.sub = this.dialog.state.subscribe(s => this.state = s);
  }

  ngOnDestroy(): void {
    this.sub?.unsubscribe();
  }

  confirm() {
    this.dialog.close(true);
  }

  cancel() {
    this.dialog.close(false);
  }

  onBackdropClick() {
    // Backdrop click cancela (equivalente a cerrar con ✕)
    this.dialog.close(false);
  }
}
