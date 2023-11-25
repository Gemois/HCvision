import {Component, Input, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {UserService} from "../../services/user.service";
import {AuthService} from "../../services/auth/auth.service";
import {Router} from "@angular/router";
import {CustomSnackbarService} from "../../services/custom-snackbar.service";
import {MatDialog} from "@angular/material/dialog";
import {ConfirmationDialogComponent} from "./confirmation-dialog/confirmation-dialog.component";

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.css'
})
export class ProfileComponent implements OnInit {
  @Input() userId: number;
  user: any;
  isEditing = false;
  userForm: FormGroup;

  constructor(private authService: AuthService,
              private userService: UserService,
              private fb: FormBuilder,
              private router: Router,
              private customSnackbar: CustomSnackbarService,
              private dialog: MatDialog) {

    this.userForm = this.fb.group({
      firstname: ['', Validators.required],
      lastname: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required],

    });
  }

  ngOnInit() {
    this.loadUserData();
  }

  loadUserData() {
    this.userService.getUser().subscribe((data: any) => {
      this.user = data;
      console.log(this.user);
      this.userForm.patchValue(this.user);
      console.log(this.userForm);
    });
  }

  editUser() {
    this.isEditing = true;
  }

  deleteUser(): void {
    const dialogRef = this.dialog.open(ConfirmationDialogComponent);

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.userService.deleteUser().subscribe(
          () => {
            this.authService.logout();
            this.router.navigate(['']);
          },
          (error) => {
            console.error('Error deleting user:', error);
          }
        );
      }
    });
  }

  updateUser() {
    const updatedUserData = this.userForm.value;
    this.userService.updateUser(updatedUserData).subscribe(() => {
      this.isEditing = false;
      this.loadUserData();
      console.log("User Details updated!")
      this.customSnackbar.open("User Details updated!","Close",{})
    });
  }
}
