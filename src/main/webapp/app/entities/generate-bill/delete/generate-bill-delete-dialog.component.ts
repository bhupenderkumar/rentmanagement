import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IGenerateBill } from '../generate-bill.model';
import { GenerateBillService } from '../service/generate-bill.service';

@Component({
  templateUrl: './generate-bill-delete-dialog.component.html',
})
export class GenerateBillDeleteDialogComponent {
  generateBill?: IGenerateBill;

  constructor(protected generateBillService: GenerateBillService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.generateBillService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
