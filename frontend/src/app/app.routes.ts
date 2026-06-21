import { Routes } from '@angular/router';
import { AuthComponent } from './components/auth/auth';
import { ForgotPasswordComponent } from './components/forgot-password/forgot-password';
import { CampaignListComponent } from './components/campaign-list/campaign-list';
import { CampaignDetailComponent } from './components/campaign-detail/campaign-detail';
import { CampaignFormComponent } from './components/campaign-form/campaign-form';
import { DonorListComponent } from './components/donor-list/donor-list';
import { DonorFormComponent } from './components/donor-form/donor-form';
import { OrganizerListComponent } from './components/organizer-list/organizer-list';
import { OrganizerFormComponent } from './components/organizer-form/organizer-form';
import { MyCampaignsComponent } from './components/my-campaigns/my-campaigns';
import { MyDonationsComponent } from './components/my-donations/my-donations';
import { HomeComponent } from './pages/home/home';
import { LandingComponent } from './pages/landing/landing';
import { PrincipalDummyComponent } from './pages/principal-dummy-component/principal-dummy-component';
import { AuthGuard, RoleGuard } from './guards/auth.guard';
import { UserRole } from './models/user';

export const routes: Routes = [
  { path: '', component: LandingComponent, pathMatch: 'full' },
  { path: 'auth', component: AuthComponent },
  { path: 'forgot-password', component: ForgotPasswordComponent },
  {
    path: 'home',
    component: HomeComponent,
    canActivate: [AuthGuard]
  },
  {
    path: 'campaigns',
    component: CampaignListComponent,
    canActivate: [AuthGuard]
  },
  {
    path: 'campaigns/new',
    component: CampaignFormComponent,
    canActivate: [RoleGuard],
    data: { roles: [UserRole.ADMIN, UserRole.ORGANIZER] }
  },
  {
    path: 'campaigns/:id',
    component: CampaignDetailComponent,
    canActivate: [AuthGuard]
  },
  {
    path: 'campaigns/:id/edit',
    component: CampaignFormComponent,
    canActivate: [RoleGuard],
    data: { roles: [UserRole.ADMIN, UserRole.ORGANIZER] }
  },
  {
    path: 'my-campaigns',
    component: MyCampaignsComponent,
    canActivate: [RoleGuard],
    data: { roles: [UserRole.ADMIN, UserRole.ORGANIZER] }
  },
  {
    path: 'my-donations',
    component: MyDonationsComponent,
    canActivate: [RoleGuard],
    data: { roles: [UserRole.DONOR] }
  },
  {
    path: 'donors',
    component: DonorListComponent,
    canActivate: [AuthGuard]
  },
  {
    path: 'donors/new',
    component: DonorFormComponent
  },
  {
    path: 'donors/:id/edit',
    component: DonorFormComponent,
    canActivate: [AuthGuard]
  },
  {
    path: 'organizers',
    component: OrganizerListComponent,
    canActivate: [AuthGuard]
  },
  {
    path: 'organizers/new',
    component: OrganizerFormComponent
  },
  {
    path: 'organizers/:type/:id/edit',
    component: OrganizerFormComponent,
    canActivate: [AuthGuard]
  },
  { path: 'dummy', component: PrincipalDummyComponent, canActivate: [AuthGuard] },
  { path: '**', redirectTo: '' }
];
