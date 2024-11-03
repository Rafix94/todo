// src/app/components/registration/registration.component.ts

import { Component, OnInit } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { environment } from "../../../environments/environment";
import { AppConstants } from "../../constants/app.constants";
import { Router } from "@angular/router";
import { UserDto } from "../../model/user.dto";  // Import the UserDto interface

@Component({
  selector: 'app-registration',
  templateUrl: './registration.component.html',
  styleUrls: ['./registration.component.css']
})
export class RegistrationComponent implements OnInit {

  userData: UserDto = {
    username: '',
    email: '',
    password: '',
    firstName: '',
    lastName: ''
  };

  errorMessage: string = '';

  constructor(private http: HttpClient, private router: Router) { }

  ngOnInit(): void { }

  register() {
    this.http.post(environment.rooturl + AppConstants.USER_AGENT_API_URL + AppConstants.LOGIN_API_URL + "/register", this.userData)
      .subscribe(
        () => this.router.navigate(['/home']),
        (error: HttpErrorResponse) => {
          console.error('Registration failed:', error);
          this.errorMessage = 'Registration failed. Please try again.';
        }
      );
  }
}
