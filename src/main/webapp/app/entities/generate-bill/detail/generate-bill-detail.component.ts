import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IGenerateBill } from '../generate-bill.model';

@Component({
  selector: 'jhi-generate-bill-detail',
  templateUrl: './generate-bill-detail.component.html',
})
export class GenerateBillDetailComponent implements OnInit {
  generateBill: IGenerateBill | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ generateBill }) => {
      this.generateBill = generateBill;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
