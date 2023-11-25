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
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {HTTP_INTERCEPTORS, HttpClientModule} from "@angular/common/http";
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
import {MatTableModule} from "@angular/material/table";
import {MatPaginatorModule} from "@angular/material/paginator";
import {MatSortModule} from "@angular/material/sort";
import {AuthInterceptor} from "./services/auth/auth.interceptor";
import { ConfirmationDialogComponent } from './components/profile/confirmation-dialog/confirmation-dialog.component';
import {MatTabsModule} from "@angular/material/tabs";
import { OptimalComponent } from './components/hierarchical/optimal/optimal.component';
import {MatSlideToggleModule} from "@angular/material/slide-toggle";
import {MatCheckboxModule} from "@angular/material/checkbox";
import {NgChartsModule} from "ng2-charts";
import {MatProgressSpinnerModule} from "@angular/material/progress-spinner";

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
    ConfirmationDialogComponent,
    OptimalComponent,
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
    MatListModule,
    MatTableModule,
    MatPaginatorModule,
    MatSortModule,
    ReactiveFormsModule,
    MatTabsModule,
    MatSlideToggleModule,
    MatCheckboxModule,
    NgChartsModule,
    MatProgressSpinnerModule
  ],
  providers: [{
    provide: HTTP_INTERCEPTORS,
    useClass: AuthInterceptor,
    multi: true
  }],
  bootstrap: [AppComponent]
})
export class AppModule {
}
