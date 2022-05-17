import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { GenerateBillService } from '../service/generate-bill.service';
import { IGenerateBill, GenerateBill } from '../generate-bill.model';
import { ITenant } from 'app/entities/tenant/tenant.model';
import { TenantService } from 'app/entities/tenant/service/tenant.service';

import { GenerateBillUpdateComponent } from './generate-bill-update.component';

describe('GenerateBill Management Update Component', () => {
  let comp: GenerateBillUpdateComponent;
  let fixture: ComponentFixture<GenerateBillUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let generateBillService: GenerateBillService;
  let tenantService: TenantService;

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
    tenantService = TestBed.inject(TenantService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Tenant query and add missing value', () => {
      const generateBill: IGenerateBill = { id: 456 };
      const tenant: ITenant = { id: 88236 };
      generateBill.tenant = tenant;

      const tenantCollection: ITenant[] = [{ id: 61812 }];
      jest.spyOn(tenantService, 'query').mockReturnValue(of(new HttpResponse({ body: tenantCollection })));
      const additionalTenants = [tenant];
      const expectedCollection: ITenant[] = [...additionalTenants, ...tenantCollection];
      jest.spyOn(tenantService, 'addTenantToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ generateBill });
      comp.ngOnInit();

      expect(tenantService.query).toHaveBeenCalled();
      expect(tenantService.addTenantToCollectionIfMissing).toHaveBeenCalledWith(tenantCollection, ...additionalTenants);
      expect(comp.tenantsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const generateBill: IGenerateBill = { id: 456 };
      const tenant: ITenant = { id: 1375 };
      generateBill.tenant = tenant;

      activatedRoute.data = of({ generateBill });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(generateBill));
      expect(comp.tenantsSharedCollection).toContain(tenant);
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

  describe('Tracking relationships identifiers', () => {
    describe('trackTenantById', () => {
      it('Should return tracked Tenant primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackTenantById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });
});
