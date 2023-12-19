import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {HomeComponent} from "../components/home/home.component";
import {AboutComponent} from "../components/about/about.component";
import {DatasetComponent} from "../components/dataset/dataset.component";
import {HierarchicalComponent} from "../components/hierarchical/hierarchical.component";
import {HistoryComponent} from "../components/history/history.component";
import {ProfileComponent} from "../components/profile/profile.component";
import {LoginComponent} from "../components/auth/login/login.component";
import {RegisterComponent} from "../components/auth/register/register.component";
import {AuthGuard} from "./auth.guard";
import {ResetPasswordComponent} from "../components/auth/reset-password/reset-password.component";


const routes: Routes = [
  {path: '', component: HomeComponent},
  {path: 'about', component: AboutComponent},
  {path: 'dataset', component: DatasetComponent, canActivate: [AuthGuard]},
  {path: 'hierarchical', component: HierarchicalComponent, canActivate: [AuthGuard]},
  {path: 'history', component: HistoryComponent, canActivate: [AuthGuard]},
  {path: 'profile', component: ProfileComponent, canActivate: [AuthGuard]},
  {path: 'login', component: LoginComponent},
  {path: 'register', component: RegisterComponent},
  { path: 'reset-password', component: ResetPasswordComponent }, // Add this line

];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class RoutingModule {
}
