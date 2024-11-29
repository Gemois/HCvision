import {Component, ViewChild} from '@angular/core';
import {Router} from '@angular/router';
import {AuthService} from "../../services/auth.service";
import {BreakpointObserver, Breakpoints} from "@angular/cdk/layout";
import {MatSidenav} from "@angular/material/sidenav";

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.css'
})
export class NavbarComponent {
  isSmallScreen = false;

  constructor(private authService: AuthService, private router: Router, private breakpointObserver: BreakpointObserver) {
    this.breakpointObserver
      .observe([Breakpoints.XSmall, Breakpoints.Small])
      .subscribe((state) => (this.isSmallScreen = state.matches));
  }


  openRateForm(): void {
    window.open("https://docs.google.com/forms/d/e/1FAIpQLSfcToH9RTdWMJmAy_4xHVEqhxZblwUPYHxT_WintFm6si_lAw/viewform?usp=sf_link", '_blank');
  }

  openApiDocs(): void {
    window.open("http://localhost:8080/swagger-ui/index.html#/", '_blank');
  }

  isAuthenticated() {
    return this.authService.isAuthenticated();
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['']);
  }
}
