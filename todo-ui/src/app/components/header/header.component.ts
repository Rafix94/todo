import { Component, OnInit } from '@angular/core';
import { User } from 'src/app/model/user.model';
import { KeycloakService } from 'keycloak-angular';
import { KeycloakProfile } from 'keycloak-js';
import { Router } from '@angular/router';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit {

  user = new User();

  public isLoggedIn = false;
  public userProfile: KeycloakProfile | null = null;

  constructor(private readonly keycloakService: KeycloakService, private router: Router) { }

  public async ngOnInit() {
    this.isLoggedIn = await this.keycloakService.isLoggedIn();

    if (this.isLoggedIn) {
      this.userProfile = await this.keycloakService.loadUserProfile();
      this.user.authStatus = 'AUTH';
      this.user.name = this.userProfile.firstName || "";
      window.sessionStorage.setItem("userdetails", JSON.stringify(this.user));
    }
  }

  public login() {
    this.keycloakService.login();
  }

  register(): void {
    this.keycloakService.register({
      redirectUri: `${window.location.origin}/dashboard`  // Specify where to redirect after registration
    }).catch(error => console.error('Error during Keycloak registration:', error));
  }

  public logout() {
    let redirectURI: string = `${window.location.origin}/home`;
    this.keycloakService.logout(redirectURI);
  }


}
