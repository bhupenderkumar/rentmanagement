import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IRoom, Room } from '../room.model';
import { RoomService } from '../service/room.service';
import { IBuilding } from 'app/entities/building/building.model';
import { BuildingService } from 'app/entities/building/service/building.service';
import { ITenant } from 'app/entities/tenant/tenant.model';
import { TenantService } from 'app/entities/tenant/service/tenant.service';

@Component({
  selector: 'jhi-room-update',
  templateUrl: './room-update.component.html',
})
export class RoomUpdateComponent implements OnInit {
  isSaving = false;

  buildingsSharedCollection: IBuilding[] = [];
  tenantsSharedCollection: ITenant[] = [];

  editForm = this.fb.group({
    id: [],
    roomName: [null, [Validators.required]],
    floor: [],
    building: [null, Validators.required],
    tenants: [],
  });

  constructor(
    protected roomService: RoomService,
    protected buildingService: BuildingService,
    protected tenantService: TenantService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ room }) => {
      this.updateForm(room);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const room = this.createFromForm();
    if (room.id !== undefined) {
      this.subscribeToSaveResponse(this.roomService.update(room));
    } else {
      this.subscribeToSaveResponse(this.roomService.create(room));
    }
  }

  trackBuildingById(_index: number, item: IBuilding): number {
    return item.id!;
  }

  trackTenantById(_index: number, item: ITenant): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IRoom>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(room: IRoom): void {
    this.editForm.patchValue({
      id: room.id,
      roomName: room.roomName,
      floor: room.floor,
      building: room.building,
      tenants: room.tenants,
    });

    this.buildingsSharedCollection = this.buildingService.addBuildingToCollectionIfMissing(this.buildingsSharedCollection, room.building);
    this.tenantsSharedCollection = this.tenantService.addTenantToCollectionIfMissing(this.tenantsSharedCollection, room.tenants);
  }

  protected loadRelationshipsOptions(): void {
    this.buildingService
      .query()
      .pipe(map((res: HttpResponse<IBuilding[]>) => res.body ?? []))
      .pipe(
        map((buildings: IBuilding[]) =>
          this.buildingService.addBuildingToCollectionIfMissing(buildings, this.editForm.get('building')!.value)
        )
      )
      .subscribe((buildings: IBuilding[]) => (this.buildingsSharedCollection = buildings));

    this.tenantService
      .query()
      .pipe(map((res: HttpResponse<ITenant[]>) => res.body ?? []))
      .pipe(map((tenants: ITenant[]) => this.tenantService.addTenantToCollectionIfMissing(tenants, this.editForm.get('tenants')!.value)))
      .subscribe((tenants: ITenant[]) => (this.tenantsSharedCollection = tenants));
  }

  protected createFromForm(): IRoom {
    return {
      ...new Room(),
      id: this.editForm.get(['id'])!.value,
      roomName: this.editForm.get(['roomName'])!.value,
      floor: this.editForm.get(['floor'])!.value,
      building: this.editForm.get(['building'])!.value,
      tenants: this.editForm.get(['tenants'])!.value,
    };
  }
}
