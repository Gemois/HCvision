import {Component} from '@angular/core';

@Component({
  selector: 'app-api-docs',
  templateUrl: './api-docs.component.html',
  styleUrl: './api-docs.component.css'
})
export class ApiDocsComponent {

  authRequests = [
    {
      name: 'Register',
      method: 'POST',
      exampleRequest: '{ "firstname": "george", "lastname": "moisidis", "email": "test@gmail.com", "password": "password" }',
      exampleResponse: '...'
    },
  ];

  datasetRequests = [
    {
      name: 'Upload',
      method: 'POST',
      exampleRequest: '{ "file": "iris.csv", "access_type": "PUBLIC" }',
      exampleResponse: '...'
    },
  ];

  userRequests = [
    {
      name: 'Get Profile',
      method: 'GET',
      exampleRequest: '{ "userId": "123" }',
      exampleResponse: '...'
    },
  ];

  actionRequests = [
    {
      name: 'Perform Action',
      method: 'POST',
      exampleRequest: '{ "action": "doSomething" }',
      exampleResponse: '...'
    },
  ];
}
