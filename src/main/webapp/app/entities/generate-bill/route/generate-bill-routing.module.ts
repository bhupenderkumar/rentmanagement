import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { GenerateBillComponent } from '../list/generate-bill.component';
import { GenerateBillDetailComponent } from '../detail/generate-bill-detail.component';
import { GenerateBillUpdateComponent } from '../update/generate-bill-update.component';
import { GenerateBillRoutingResolveService } from './generate-bill-routing-resolve.service';

const generateBillRoute: Routes = [
  {
    path: '',
    component: GenerateBillComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: GenerateBillDetailComponent,
    resolve: {
      generateBill: GenerateBillRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: GenerateBillUpdateComponent,
    resolve: {
      generateBill: GenerateBillRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: GenerateBillUpdateComponent,
    resolve: {
      generateBill: GenerateBillRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(generateBillRoute)],
  exports: [RouterModule],
})
export class GenerateBillRoutingModule {}
