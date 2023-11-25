import { Injectable } from '@angular/core';
import {
  MatSnackBar,
  MatSnackBarConfig,
  MatSnackBarRef,
  TextOnlySnackBar
} from "@angular/material/snack-bar";

@Injectable({
  providedIn: 'root'
})
export class CustomSnackbarService {

  constructor(private snackBar: MatSnackBar) {}

  open(message: string, action: string, config?: MatSnackBarConfig): MatSnackBarRef<TextOnlySnackBar> {
    const defaultConfig: MatSnackBarConfig = {
      duration: 3000,
      horizontalPosition: 'center',
      verticalPosition: 'bottom',
      panelClass: [],
    };

    const mergedConfig = { ...defaultConfig, ...config };
    return this.snackBar.open(message, action, mergedConfig);
  }
}
