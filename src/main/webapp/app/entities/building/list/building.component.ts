import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IBuilding } from '../building.model';
import { BuildingService } from '../service/building.service';
import { BuildingDeleteDialogComponent } from '../delete/building-delete-dialog.component';

@Component({
  selector: 'jhi-building',
  templateUrl: './building.component.html',
})
export class BuildingComponent implements OnInit {
  buildings?: IBuilding[];
  isLoading = false;

  constructor(protected buildingService: BuildingService, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.buildingService.query().subscribe({
      next: (res: HttpResponse<IBuilding[]>) => {
        this.isLoading = false;
        this.buildings = res.body ?? [];
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(_index: number, item: IBuilding): number {
    return item.id!;
  }

  delete(building: IBuilding): void {
    const modalRef = this.modalService.open(BuildingDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.building = building;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
