import {Component} from '@angular/core';
import {AuthService} from "../../../services/auth.service";
import {MatSnackBar} from "@angular/material/snack-bar";
import {UserService} from "../../../services/user.service";
import {ResetPasswordDialogComponent} from "./reset-password-dialog/reset-password-dialog.component";
import {MatDialog} from "@angular/material/dialog";
import {Router} from "@angular/router";

@Component({
  selector: 'app-reset-password',
  templateUrl: './reset-password.component.html',
  styleUrl: './reset-password.component.css'
})
export class ResetPasswordComponent {
  showResetForm: boolean = false;
  emailForOTP: string = '';
  newPassword: string = '';
  confirmPassword: string = '';
  otpCode: string = '';

  constructor(private userService: UserService, private router: Router, private snackBar: MatSnackBar, private dialog: MatDialog) {
  }

  openResetSuccessDialog() {
    const dialogRef = this.dialog.open(ResetPasswordDialogComponent, {
      data: {}
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result === 'login') {
        this.router.navigate(["/login"]);
      }
    });
  }

  sendOTP() {
    this.userService.sendOTP(this.emailForOTP).subscribe(
      () => {
        this.snackBar.open('OTP Code sent successfully', 'Close', {duration: 3000});
        this.showResetForm = true;
      },
      (error) => {
        console.error('Error sending OTP Code:', error);
        this.snackBar.open('Error sending OTP Code', 'Close', {duration: 3000});
      }
    );
  }

  changePassword() {
    if (this.newPassword !== this.confirmPassword) {
      this.snackBar.open('Passwords do not match', 'Close', {duration: 3000});
      return;
    }

    this.userService.resetPassword(this.newPassword, this.otpCode).subscribe(
      () => {
        this.openResetSuccessDialog();
        this.snackBar.open('Password reset successfully', 'Close', {duration: 3000});
      },
      (error) => {
        console.error('Error resetting password:', error);
        this.snackBar.open('Error resetting password', 'Close', {duration: 3000});
      }
    );
  }
}
