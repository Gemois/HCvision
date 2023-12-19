import { Component } from '@angular/core';
import {MatDialogRef} from "@angular/material/dialog";

@Component({
  selector: 'app-reset-password-dialog',
  templateUrl: './reset-password-dialog.component.html',
  styleUrl: './reset-password-dialog.component.css'
})
export class ResetPasswordDialogComponent {
  constructor(public dialogRef: MatDialogRef<ResetPasswordDialogComponent>) { }

  login() {
    this.dialogRef.close('login');
  }
}
