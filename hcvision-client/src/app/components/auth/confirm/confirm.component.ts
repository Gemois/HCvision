import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {AuthService} from "../../../services/auth.service";
import {MatSnackBar} from "@angular/material/snack-bar";

@Component({
  selector: 'app-confirm',
  templateUrl: './confirm.component.html',
  styleUrl: './confirm.component.css'
})
export class ConfirmComponent implements OnInit {

  constructor(
    private route: ActivatedRoute,
    private authService: AuthService,
    private router: Router,
    private snackBar: MatSnackBar // Inject MatSnackBar
  ) {
  }

  ngOnInit(): void {
    this.route.queryParamMap.subscribe(params => {
      const token = params.get('token');

      if (token) {
        this.authService.confirmEmail(token).subscribe({
            next: () => {
              console.log('Token confirmed successfully');
              this.snackBar.open('Email confirmed successfully', 'Close', {
                duration: 5000,
                panelClass: 'success-snackbar'
              });
            },
            error: error => {
              console.error('Error confirming token:', error);
            }
          }
        );
      }
    });
  }
}
