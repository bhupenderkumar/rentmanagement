import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IBuilding } from '../building.model';
import { BuildingService } from '../service/building.service';

@Component({
  templateUrl: './building-delete-dialog.component.html',
})
export class BuildingDeleteDialogComponent {
  building?: IBuilding;

  constructor(protected buildingService: BuildingService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.buildingService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
