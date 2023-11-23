import {CUSTOM_ELEMENTS_SCHEMA, NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';

import {AppComponent} from './app.component';
import {LoginComponent} from './components/auth/login/login.component';
import {RegisterComponent} from './components/auth/register/register.component';
import {HomeComponent} from './components/home/home.component';
import {AboutComponent} from './components/about/about.component';
import {HistoryComponent} from './components/history/history.component';
import {HierarchicalComponent} from './components/hierarchical/hierarchical.component';
import {ProfileComponent} from './components/profile/profile.component';
import {DatasetComponent} from './components/dataset/dataset.component';
import {NavbarComponent} from './components/navbar/navbar.component';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {MatToolbarModule} from "@angular/material/toolbar";
import {MatButtonModule} from "@angular/material/button";
import {RouterLink, RouterOutlet} from "@angular/router";
import {AppRoutingModule} from "./modules/AppRoutingModule";
import {MatCardModule} from "@angular/material/card";
import {MatIconModule} from "@angular/material/icon";
import {MatInputModule} from "@angular/material/input";
import {FormsModule} from "@angular/forms";
import {HttpClientModule} from "@angular/common/http";
import {DatasetListComponent} from './components/dataset/dataset-list/dataset-list.component';
import {
  DatasetListItemComponent
} from './components/dataset/dataset-list/dataset-list-item/dataset-list-item.component';
import {DatasetPreviewComponent} from './components/dataset/dataset-preview/dataset-preview.component';
import {UploadDialogComponent} from './components/dataset/upload-dialog/upload-dialog.component';
import {
  MatDialogActions,
  MatDialogClose,
  MatDialogContent,
  MatDialogModule,
  MatDialogTitle
} from "@angular/material/dialog";
import {MatSelectModule} from "@angular/material/select";
import {MatChipsModule} from "@angular/material/chips";
import {MatListModule} from "@angular/material/list";

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    RegisterComponent,
    HomeComponent,
    AboutComponent,
    HistoryComponent,
    HierarchicalComponent,
    ProfileComponent,
    DatasetComponent,
    NavbarComponent,
    DatasetListComponent,
    DatasetListItemComponent,
    DatasetPreviewComponent,
    UploadDialogComponent,
  ],
  imports: [
    BrowserModule,
    RouterLink,
    BrowserAnimationsModule,
    MatToolbarModule,
    MatButtonModule,
    RouterOutlet,
    AppRoutingModule,
    MatCardModule,
    MatIconModule,
    MatInputModule,
    FormsModule,
    HttpClientModule,
    MatDialogTitle,
    MatDialogContent,
    MatSelectModule,
    MatDialogActions,
    MatDialogClose,
    MatDialogModule,
    MatChipsModule,
    MatListModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule {
}
