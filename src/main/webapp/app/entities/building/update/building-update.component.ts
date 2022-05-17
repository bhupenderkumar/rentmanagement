import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IBuilding, Building } from '../building.model';
import { BuildingService } from '../service/building.service';
import { ILocation } from 'app/entities/location/location.model';
import { LocationService } from 'app/entities/location/service/location.service';

@Component({
  selector: 'jhi-building-update',
  templateUrl: './building-update.component.html',
})
export class BuildingUpdateComponent implements OnInit {
  isSaving = false;

  addressIdsCollection: ILocation[] = [];

  editForm = this.fb.group({
    id: [],
    buildingName: [null, [Validators.required]],
    addressId: [null, Validators.required],
  });

  constructor(
    protected buildingService: BuildingService,
    protected locationService: LocationService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ building }) => {
      this.updateForm(building);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const building = this.createFromForm();
    if (building.id !== undefined) {
      this.subscribeToSaveResponse(this.buildingService.update(building));
    } else {
      this.subscribeToSaveResponse(this.buildingService.create(building));
    }
  }

  trackLocationById(_index: number, item: ILocation): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IBuilding>>): void {
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

  protected updateForm(building: IBuilding): void {
    this.editForm.patchValue({
      id: building.id,
      buildingName: building.buildingName,
      addressId: building.addressId,
    });

    this.addressIdsCollection = this.locationService.addLocationToCollectionIfMissing(this.addressIdsCollection, building.addressId);
  }

  protected loadRelationshipsOptions(): void {
    this.locationService
      .query({ filter: 'building-is-null' })
      .pipe(map((res: HttpResponse<ILocation[]>) => res.body ?? []))
      .pipe(
        map((locations: ILocation[]) =>
          this.locationService.addLocationToCollectionIfMissing(locations, this.editForm.get('addressId')!.value)
        )
      )
      .subscribe((locations: ILocation[]) => (this.addressIdsCollection = locations));
  }

  protected createFromForm(): IBuilding {
    return {
      ...new Building(),
      id: this.editForm.get(['id'])!.value,
      buildingName: this.editForm.get(['buildingName'])!.value,
      addressId: this.editForm.get(['addressId'])!.value,
    };
  }
}
