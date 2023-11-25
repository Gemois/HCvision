import { Component } from '@angular/core';
import {MatDialogRef} from "@angular/material/dialog";

@Component({
  selector: 'app-confirmation-dialog',
  templateUrl: './confirmation-dialog.component.html',
  styleUrl: './confirmation-dialog.component.css'
})
export class ConfirmationDialogComponent {
  constructor(private dialogRef: MatDialogRef<ConfirmationDialogComponent>) {}

  confirm(): void {
    this.dialogRef.close(true);
  }
}
