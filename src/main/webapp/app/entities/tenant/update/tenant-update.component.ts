import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ITenant, Tenant } from '../tenant.model';
import { TenantService } from '../service/tenant.service';
import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { IGenerateBill } from 'app/entities/generate-bill/generate-bill.model';
import { GenerateBillService } from 'app/entities/generate-bill/service/generate-bill.service';
import { ILocation } from 'app/entities/location/location.model';
import { LocationService } from 'app/entities/location/service/location.service';

@Component({
  selector: 'jhi-tenant-update',
  templateUrl: './tenant-update.component.html',
})
export class TenantUpdateComponent implements OnInit {
  isSaving = false;

  tenantsCollection: IGenerateBill[] = [];
  locationsCollection: ILocation[] = [];

  editForm = this.fb.group({
    id: [],
    tenantName: [null, [Validators.required]],
    addressProff: [null, [Validators.required]],
    addressProffContentType: [],
    numberofFamilyMembers: [],
    phoneNumber: [null, [Validators.required]],
    rentStartDate: [null, [Validators.required]],
    rentAmount: [null, [Validators.required]],
    electricityUnitRate: [null, [Validators.required]],
    startingElectricityUnit: [null, [Validators.required]],
    anyOtherDetails: [null, [Validators.maxLength(500)]],
    sendNotification: [],
    emailAddress: [],
    emergencyContactNumber: [],
    outStandingAmount: [],
    monthEndCalculation: [],
    calculateOnDate: [],
    tenant: [],
    location: [],
  });

  constructor(
    protected dataUtils: DataUtils,
    protected eventManager: EventManager,
    protected tenantService: TenantService,
    protected generateBillService: GenerateBillService,
    protected locationService: LocationService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ tenant }) => {
      this.updateForm(tenant);

      this.loadRelationshipsOptions();
    });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  setFileData(event: Event, field: string, isImage: boolean): void {
    this.dataUtils.loadFileToForm(event, this.editForm, field, isImage).subscribe({
      error: (err: FileLoadError) =>
        this.eventManager.broadcast(new EventWithContent<AlertError>('rentmanagementApp.error', { ...err, key: 'error.file.' + err.key })),
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const tenant = this.createFromForm();
    if (tenant.id !== undefined) {
      this.subscribeToSaveResponse(this.tenantService.update(tenant));
    } else {
      this.subscribeToSaveResponse(this.tenantService.create(tenant));
    }
  }

  trackGenerateBillById(_index: number, item: IGenerateBill): number {
    return item.id!;
  }

  trackLocationById(_index: number, item: ILocation): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ITenant>>): void {
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

  protected updateForm(tenant: ITenant): void {
    this.editForm.patchValue({
      id: tenant.id,
      tenantName: tenant.tenantName,
      addressProff: tenant.addressProff,
      addressProffContentType: tenant.addressProffContentType,
      numberofFamilyMembers: tenant.numberofFamilyMembers,
      phoneNumber: tenant.phoneNumber,
      rentStartDate: tenant.rentStartDate,
      rentAmount: tenant.rentAmount,
      electricityUnitRate: tenant.electricityUnitRate,
      startingElectricityUnit: tenant.startingElectricityUnit,
      anyOtherDetails: tenant.anyOtherDetails,
      sendNotification: tenant.sendNotification,
      emailAddress: tenant.emailAddress,
      emergencyContactNumber: tenant.emergencyContactNumber,
      outStandingAmount: tenant.outStandingAmount,
      monthEndCalculation: tenant.monthEndCalculation,
      calculateOnDate: tenant.calculateOnDate,
      tenant: tenant.tenant,
      location: tenant.location,
    });

    this.tenantsCollection = this.generateBillService.addGenerateBillToCollectionIfMissing(this.tenantsCollection, tenant.tenant);
    this.locationsCollection = this.locationService.addLocationToCollectionIfMissing(this.locationsCollection, tenant.location);
  }

  protected loadRelationshipsOptions(): void {
    this.generateBillService
      .query({ filter: 'tenant-is-null' })
      .pipe(map((res: HttpResponse<IGenerateBill[]>) => res.body ?? []))
      .pipe(
        map((generateBills: IGenerateBill[]) =>
          this.generateBillService.addGenerateBillToCollectionIfMissing(generateBills, this.editForm.get('tenant')!.value)
        )
      )
      .subscribe((generateBills: IGenerateBill[]) => (this.tenantsCollection = generateBills));

    this.locationService
      .query({ filter: 'tenant-is-null' })
      .pipe(map((res: HttpResponse<ILocation[]>) => res.body ?? []))
      .pipe(
        map((locations: ILocation[]) =>
          this.locationService.addLocationToCollectionIfMissing(locations, this.editForm.get('location')!.value)
        )
      )
      .subscribe((locations: ILocation[]) => (this.locationsCollection = locations));
  }

  protected createFromForm(): ITenant {
    return {
      ...new Tenant(),
      id: this.editForm.get(['id'])!.value,
      tenantName: this.editForm.get(['tenantName'])!.value,
      addressProffContentType: this.editForm.get(['addressProffContentType'])!.value,
      addressProff: this.editForm.get(['addressProff'])!.value,
      numberofFamilyMembers: this.editForm.get(['numberofFamilyMembers'])!.value,
      phoneNumber: this.editForm.get(['phoneNumber'])!.value,
      rentStartDate: this.editForm.get(['rentStartDate'])!.value,
      rentAmount: this.editForm.get(['rentAmount'])!.value,
      electricityUnitRate: this.editForm.get(['electricityUnitRate'])!.value,
      startingElectricityUnit: this.editForm.get(['startingElectricityUnit'])!.value,
      anyOtherDetails: this.editForm.get(['anyOtherDetails'])!.value,
      sendNotification: this.editForm.get(['sendNotification'])!.value,
      emailAddress: this.editForm.get(['emailAddress'])!.value,
      emergencyContactNumber: this.editForm.get(['emergencyContactNumber'])!.value,
      outStandingAmount: this.editForm.get(['outStandingAmount'])!.value,
      monthEndCalculation: this.editForm.get(['monthEndCalculation'])!.value,
      calculateOnDate: this.editForm.get(['calculateOnDate'])!.value,
      tenant: this.editForm.get(['tenant'])!.value,
      location: this.editForm.get(['location'])!.value,
    };
  }
}
