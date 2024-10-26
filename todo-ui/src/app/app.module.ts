import { BrowserModule } from '@angular/platform-browser';
import { APP_INITIALIZER, NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpClientModule, HttpClientXsrfModule} from '@angular/common/http';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { HeaderComponent } from './components/header/header.component';
import { HomeComponent } from './components/home/home.component';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { SearchComponent } from "./components/search/search.component";

import { MatInputModule } from '@angular/material/input';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatSortModule } from '@angular/material/sort';
import { MatTableModule } from '@angular/material/table';

import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MatFormFieldModule } from '@angular/material/form-field';

import { KeycloakAngularModule, KeycloakService } from 'keycloak-angular';

import { TaskComponent } from "./components/tasks/task.component";
import { TaskDetailsComponent } from './components/task-details/task-details.component';
import { MatIconModule } from "@angular/material/icon";
import { MatButtonModule } from "@angular/material/button";
import { RegistrationComponent } from './components/registration/registration.component';
import { environment } from "../environments/environment";

function initializeKeycloak(keycloak: KeycloakService) {
  return () => {
    console.log('Initializing Keycloak...'); // Log before initialization

    const timeoutDuration = 100000;

    const redirectUri = `${window.location.origin}/dashboard`;

    return new Promise((resolve, reject) => {
      // Start Keycloak initialization
      const initPromise = keycloak.init({
        config: {
          url: environment.keycloak,
          realm: 'toDoListApp',
          clientId: 'todolistpublicclient',
        },
        initOptions: {
          checkLoginIframe: false,
          pkceMethod: 'S256',
          redirectUri: redirectUri,
        },
        loadUserProfileAtStartUp: false
      });

      // Set a timeout for initialization
      const timeoutId = setTimeout(() => {
        reject(new Error('Keycloak initialization timed out')); // Reject if timeout occurs
        console.log('Keycloak initialization is taking too long...');
      }, timeoutDuration);

      initPromise.then(() => {
        clearTimeout(timeoutId); // Clear the timeout if initialization succeeds
        console.log('Keycloak initialized successfully'); // Log on successful initialization
        resolve(true); // Resolve the promise
      }).catch((error) => {
        clearTimeout(timeoutId); // Clear the timeout if initialization fails
        console.error('Keycloak initialization failed:', error); // Log errors during initialization
        reject(error); // Reject the promise with the error
      });
    });
  };
}


@NgModule({
  bootstrap: [AppComponent],
  declarations: [
    AppComponent,
    HeaderComponent,
    HomeComponent,
    DashboardComponent,
    TaskComponent,
    SearchComponent,
    TaskDetailsComponent,
    RegistrationComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    MatFormFieldModule,
    MatInputModule,
    FormsModule,
    KeycloakAngularModule,
    HttpClientModule,
    MatTableModule,
    MatPaginatorModule,
    MatSortModule,
    HttpClientXsrfModule.withOptions({
      cookieName: 'XSRF-TOKEN',
      headerName: 'X-XSRF-TOKEN',
    }),
    MatIconModule,
    MatButtonModule,
  ],
  providers: [
    {
      provide: APP_INITIALIZER,
      useFactory: initializeKeycloak,
      multi: true,
      deps: [KeycloakService],
    }
  ]
})
export class AppModule { }
