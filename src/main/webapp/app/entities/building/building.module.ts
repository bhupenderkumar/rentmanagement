import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { BuildingComponent } from './list/building.component';
import { BuildingDetailComponent } from './detail/building-detail.component';
import { BuildingUpdateComponent } from './update/building-update.component';
import { BuildingDeleteDialogComponent } from './delete/building-delete-dialog.component';
import { BuildingRoutingModule } from './route/building-routing.module';

@NgModule({
  imports: [SharedModule, BuildingRoutingModule],
  declarations: [BuildingComponent, BuildingDetailComponent, BuildingUpdateComponent, BuildingDeleteDialogComponent],
  entryComponents: [BuildingDeleteDialogComponent],
})
export class BuildingModule {}
