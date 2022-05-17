import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IGenerateBill } from '../generate-bill.model';
import { GenerateBillService } from '../service/generate-bill.service';
import { GenerateBillDeleteDialogComponent } from '../delete/generate-bill-delete-dialog.component';

@Component({
  selector: 'jhi-generate-bill',
  templateUrl: './generate-bill.component.html',
})
export class GenerateBillComponent implements OnInit {
  generateBills?: IGenerateBill[];
  isLoading = false;

  constructor(protected generateBillService: GenerateBillService, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.generateBillService.query().subscribe({
      next: (res: HttpResponse<IGenerateBill[]>) => {
        this.isLoading = false;
        this.generateBills = res.body ?? [];
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(_index: number, item: IGenerateBill): number {
    return item.id!;
  }

  delete(generateBill: IGenerateBill): void {
    const modalRef = this.modalService.open(GenerateBillDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.generateBill = generateBill;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
