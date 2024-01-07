import { Component } from '@angular/core';

@Component({
  selector: 'app-api-docs',
  templateUrl: './api-docs.component.html',
  styleUrl: './api-docs.component.css'
})
export class ApiDocsComponent {

  // Define your API requests data here
  authRequests = [
    {
      name: 'Register',
      method: 'POST',
      exampleRequest: '{ "firstname": "george", "lastname": "moisidis", "email": "test@gmail.com", "password": "password" }',
      exampleResponse: '...'
    },
    // Include other authentication requests
  ];

  datasetRequests = [
    {
      name: 'Upload',
      method: 'POST',
      exampleRequest: '{ "file": "iris.csv", "access_type": "PUBLIC" }',
      exampleResponse: '...'
    },
    // Include other dataset requests
  ];

  userRequests = [
    {
      name: 'Get Profile',
      method: 'GET',
      exampleRequest: '{ "userId": "123" }',
      exampleResponse: '...'
    },
    // Include other user requests
  ];

  actionRequests = [
    {
      name: 'Perform Action',
      method: 'POST',
      exampleRequest: '{ "action": "doSomething" }',
      exampleResponse: '...'
    },
    // Include other action requests
  ];

  // Define other request categories similarly

}
