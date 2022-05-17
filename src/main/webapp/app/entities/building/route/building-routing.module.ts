import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { BuildingComponent } from '../list/building.component';
import { BuildingDetailComponent } from '../detail/building-detail.component';
import { BuildingUpdateComponent } from '../update/building-update.component';
import { BuildingRoutingResolveService } from './building-routing-resolve.service';

const buildingRoute: Routes = [
  {
    path: '',
    component: BuildingComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: BuildingDetailComponent,
    resolve: {
      building: BuildingRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: BuildingUpdateComponent,
    resolve: {
      building: BuildingRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: BuildingUpdateComponent,
    resolve: {
      building: BuildingRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(buildingRoute)],
  exports: [RouterModule],
})
export class BuildingRoutingModule {}
