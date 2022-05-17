import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IGenerateBill, GenerateBill } from '../generate-bill.model';
import { GenerateBillService } from '../service/generate-bill.service';
import { ITenant } from 'app/entities/tenant/tenant.model';
import { TenantService } from 'app/entities/tenant/service/tenant.service';

@Component({
  selector: 'jhi-generate-bill-update',
  templateUrl: './generate-bill-update.component.html',
})
export class GenerateBillUpdateComponent implements OnInit {
  isSaving = false;

  tenantsSharedCollection: ITenant[] = [];

  editForm = this.fb.group({
    id: [],
    amountPending: [],
    sendNotification: [],
    electricityUnit: [],
    tenant: [null, Validators.required],
  });

  constructor(
    protected generateBillService: GenerateBillService,
    protected tenantService: TenantService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ generateBill }) => {
      this.updateForm(generateBill);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const generateBill = this.createFromForm();
    if (generateBill.id !== undefined) {
      this.subscribeToSaveResponse(this.generateBillService.update(generateBill));
    } else {
      this.subscribeToSaveResponse(this.generateBillService.create(generateBill));
    }
  }

  trackTenantById(_index: number, item: ITenant): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IGenerateBill>>): void {
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

  protected updateForm(generateBill: IGenerateBill): void {
    this.editForm.patchValue({
      id: generateBill.id,
      amountPending: generateBill.amountPending,
      sendNotification: generateBill.sendNotification,
      electricityUnit: generateBill.electricityUnit,
      tenant: generateBill.tenant,
    });

    this.tenantsSharedCollection = this.tenantService.addTenantToCollectionIfMissing(this.tenantsSharedCollection, generateBill.tenant);
  }

  protected loadRelationshipsOptions(): void {
    this.tenantService
      .query()
      .pipe(map((res: HttpResponse<ITenant[]>) => res.body ?? []))
      .pipe(map((tenants: ITenant[]) => this.tenantService.addTenantToCollectionIfMissing(tenants, this.editForm.get('tenant')!.value)))
      .subscribe((tenants: ITenant[]) => (this.tenantsSharedCollection = tenants));
  }

  protected createFromForm(): IGenerateBill {
    return {
      ...new GenerateBill(),
      id: this.editForm.get(['id'])!.value,
      amountPending: this.editForm.get(['amountPending'])!.value,
      sendNotification: this.editForm.get(['sendNotification'])!.value,
      electricityUnit: this.editForm.get(['electricityUnit'])!.value,
      tenant: this.editForm.get(['tenant'])!.value,
    };
  }
}
