import { Component } from '@angular/core';
import {AuthService} from "../../../services/auth/auth.service";


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

  constructor(private authService: AuthService) {
  }


  register() {
    const userData = {
      firstname: this.firstName,
      lastname: this.lastName,
      email: this.email,
      password: this.password
    };

    this.authService.register(userData)
      .subscribe(response => {
        const accessToken = response.access_token;
        this.authService.setToken(accessToken);
      });
  }
}
