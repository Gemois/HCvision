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
  ) { }

  ngOnInit(): void {
    // Subscribe to the paramMap observable to capture query parameters
    this.route.queryParamMap.subscribe(params => {
      // Get the token parameter from the URL
      const token = params.get('token');

      if (token) {
        // Make a request to the API using AuthService
        this.authService.confirmEmail(token).subscribe({
            // Handle the success case
            next: () => {
              console.log('Token confirmed successfully');
              // Display a success message using MatSnackBar
              this.snackBar.open('Email confirmed successfully', 'Close', {
                duration: 5000, // Adjust the duration as needed
                panelClass: 'success-snackbar' // Add a custom class for styling
              });
            },
            // Handle any errors
            error: error => {
              console.error('Error confirming token:', error);
              // You may want to handle errors appropriately, e.g., display an error message to the user
            }
          }
        );
      }
    });
  }
}
