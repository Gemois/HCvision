import {Component} from '@angular/core';
import {AuthService} from "../../../services/auth/auth.service";
import {SnackbarService} from "../../../services/snackbar.service";
import {Router} from "@angular/router";


@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent {
  firstName: string = '';
  lastName: string = '';
  email: string = '';
  password: string = '';

  constructor(private authService: AuthService,
              private customSnackbarService: SnackbarService, private router: Router) {
  }

  register() {
    const userData = {
      firstname: this.firstName,
      lastname: this.lastName,
      email: this.email,
      password: this.password
    };

    this.authService.register(userData)
      .subscribe({
        next: () => {
          this.customSnackbarService.open('Registration complete', 'Close', {});
          console.error('User successfully registered');
          this.router.navigate(['/login']);
        },
        error: (error) => {
          this.customSnackbarService.open('Register failed', 'Close', {});
          console.error('Error registering user:', error);
        }
      });
  }
}
