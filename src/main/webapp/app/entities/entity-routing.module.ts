import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'building',
        data: { pageTitle: 'rentmanagementApp.building.home.title' },
        loadChildren: () => import('./building/building.module').then(m => m.BuildingModule),
      },
      {
        path: 'room',
        data: { pageTitle: 'rentmanagementApp.room.home.title' },
        loadChildren: () => import('./room/room.module').then(m => m.RoomModule),
      },
      {
        path: 'location',
        data: { pageTitle: 'rentmanagementApp.location.home.title' },
        loadChildren: () => import('./location/location.module').then(m => m.LocationModule),
      },
      {
        path: 'tenant',
        data: { pageTitle: 'rentmanagementApp.tenant.home.title' },
        loadChildren: () => import('./tenant/tenant.module').then(m => m.TenantModule),
      },
      {
        path: 'owner',
        data: { pageTitle: 'rentmanagementApp.owner.home.title' },
        loadChildren: () => import('./owner/owner.module').then(m => m.OwnerModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
