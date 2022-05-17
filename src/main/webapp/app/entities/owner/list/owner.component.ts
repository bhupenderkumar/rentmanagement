import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IOwner } from '../owner.model';
import { OwnerService } from '../service/owner.service';
import { OwnerDeleteDialogComponent } from '../delete/owner-delete-dialog.component';

@Component({
  selector: 'jhi-owner',
  templateUrl: './owner.component.html',
})
export class OwnerComponent implements OnInit {
  owners?: IOwner[];
  isLoading = false;

  constructor(protected ownerService: OwnerService, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.ownerService.query().subscribe({
      next: (res: HttpResponse<IOwner[]>) => {
        this.isLoading = false;
        this.owners = res.body ?? [];
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(_index: number, item: IOwner): number {
    return item.id!;
  }

  delete(owner: IOwner): void {
    const modalRef = this.modalService.open(OwnerDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.owner = owner;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
