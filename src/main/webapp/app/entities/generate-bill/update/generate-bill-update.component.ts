import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { IGenerateBill, GenerateBill } from '../generate-bill.model';
import { GenerateBillService } from '../service/generate-bill.service';

@Component({
  selector: 'jhi-generate-bill-update',
  templateUrl: './generate-bill-update.component.html',
})
export class GenerateBillUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    amountPending: [],
    sendNotification: [],
  });

  constructor(protected generateBillService: GenerateBillService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ generateBill }) => {
      this.updateForm(generateBill);
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
    });
  }

  protected createFromForm(): IGenerateBill {
    return {
      ...new GenerateBill(),
      id: this.editForm.get(['id'])!.value,
      amountPending: this.editForm.get(['amountPending'])!.value,
      sendNotification: this.editForm.get(['sendNotification'])!.value,
    };
  }
}
