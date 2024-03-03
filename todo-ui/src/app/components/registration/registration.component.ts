import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import {environment} from "../../../environments/environment";
import {AppConstants} from "../../constants/app.constants";

@Component({
  selector: 'app-registration',
  templateUrl: './registration.component.html',
  styleUrls: ['./registration.component.css']
})
export class RegistrationComponent implements OnInit {

  userData = {
    username: '',
    email: '',
    password: ''
  };

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
  }


  register() {
    this.http.post(environment.rooturl + AppConstants.USER_AGENT_API_URL + AppConstants.LOGIN_API_URL + "/register", this.userData)
      .subscribe(
        response => {
          console.log('Registration successful:', response);
          // Optionally, perform any additional actions after successful registration
        },
        error => {
          console.error('Registration failed:', error);
          // Optionally, display an error message to the user
        }
      );
  }
}
