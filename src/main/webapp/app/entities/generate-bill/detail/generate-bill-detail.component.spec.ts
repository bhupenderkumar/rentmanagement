import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { GenerateBillDetailComponent } from './generate-bill-detail.component';

describe('GenerateBill Management Detail Component', () => {
  let comp: GenerateBillDetailComponent;
  let fixture: ComponentFixture<GenerateBillDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [GenerateBillDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ generateBill: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(GenerateBillDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(GenerateBillDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load generateBill on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.generateBill).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
