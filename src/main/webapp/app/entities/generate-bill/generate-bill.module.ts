import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { GenerateBillComponent } from './list/generate-bill.component';
import { GenerateBillDetailComponent } from './detail/generate-bill-detail.component';
import { GenerateBillUpdateComponent } from './update/generate-bill-update.component';
import { GenerateBillDeleteDialogComponent } from './delete/generate-bill-delete-dialog.component';
import { GenerateBillRoutingModule } from './route/generate-bill-routing.module';

@NgModule({
  imports: [SharedModule, GenerateBillRoutingModule],
  declarations: [GenerateBillComponent, GenerateBillDetailComponent, GenerateBillUpdateComponent, GenerateBillDeleteDialogComponent],
  entryComponents: [GenerateBillDeleteDialogComponent],
})
export class GenerateBillModule {}
