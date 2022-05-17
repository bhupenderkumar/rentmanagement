import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { GenerateBillService } from '../service/generate-bill.service';
import { IGenerateBill, GenerateBill } from '../generate-bill.model';

import { GenerateBillUpdateComponent } from './generate-bill-update.component';

describe('GenerateBill Management Update Component', () => {
  let comp: GenerateBillUpdateComponent;
  let fixture: ComponentFixture<GenerateBillUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let generateBillService: GenerateBillService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [GenerateBillUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(GenerateBillUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(GenerateBillUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    generateBillService = TestBed.inject(GenerateBillService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const generateBill: IGenerateBill = { id: 456 };

      activatedRoute.data = of({ generateBill });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(generateBill));
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<GenerateBill>>();
      const generateBill = { id: 123 };
      jest.spyOn(generateBillService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ generateBill });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: generateBill }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(generateBillService.update).toHaveBeenCalledWith(generateBill);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<GenerateBill>>();
      const generateBill = new GenerateBill();
      jest.spyOn(generateBillService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ generateBill });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: generateBill }));
      saveSubject.complete();

      // THEN
      expect(generateBillService.create).toHaveBeenCalledWith(generateBill);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<GenerateBill>>();
      const generateBill = { id: 123 };
      jest.spyOn(generateBillService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ generateBill });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(generateBillService.update).toHaveBeenCalledWith(generateBill);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
