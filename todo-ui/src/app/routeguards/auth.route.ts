import { Injectable } from '@angular/core';
import {
  ActivatedRouteSnapshot,
  Router,
  RouterStateSnapshot,
} from '@angular/router';
import { KeycloakAuthGuard, KeycloakService } from 'keycloak-angular';
import { User } from '../model/user.model';
import { KeycloakProfile } from 'keycloak-js';

@Injectable({
  providedIn: 'root',
})
export class AuthKeyClockGuard extends KeycloakAuthGuard {
  user = new User();
  public userProfile: KeycloakProfile | null = null;
  constructor(
    protected override readonly router: Router,
    protected readonly keycloak: KeycloakService
  ) {
    super(router, keycloak);
  }

  public async isAccessAllowed(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ) {
    console.log('isAccessAllowed method called'); // Log when method is called
    console.log('Route:', route); // Log route details
    console.log('State:', state); // Log state details

    if (!this.authenticated) {
      console.log('User is not authenticated. Redirecting to login.');
      await this.keycloak.login({
        redirectUri: window.location.origin + state.url,
      });
    } else {
      console.log('User is authenticated');

      this.userProfile = await this.keycloak.loadUserProfile();

      this.user.authStatus = 'AUTH';
      this.user.name = this.userProfile.firstName || "";
      this.user.email = this.userProfile.email || "";

      window.sessionStorage.setItem("userdetails", JSON.stringify(this.user));
      console.log('User details saved to session storage:', this.user);
    }

    const requiredRoles = route.data["roles"];
    console.log('Required roles for this route:', requiredRoles);

    if (!(requiredRoles instanceof Array) || requiredRoles.length === 0) {
      console.log('No specific roles required. Access granted.');
      return true;
    }

    const hasRequiredRole = requiredRoles.some((role) => this.roles.includes(role));
    console.log('User roles:', this.roles);
    console.log('User has required role:', hasRequiredRole);

    if (!hasRequiredRole) {
      console.warn('Access denied: User does not have the required role');
    }

    return hasRequiredRole;
  }
}
