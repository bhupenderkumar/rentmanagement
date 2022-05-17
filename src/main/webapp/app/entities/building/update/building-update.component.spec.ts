import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { BuildingService } from '../service/building.service';
import { IBuilding, Building } from '../building.model';
import { ILocation } from 'app/entities/location/location.model';
import { LocationService } from 'app/entities/location/service/location.service';

import { BuildingUpdateComponent } from './building-update.component';

describe('Building Management Update Component', () => {
  let comp: BuildingUpdateComponent;
  let fixture: ComponentFixture<BuildingUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let buildingService: BuildingService;
  let locationService: LocationService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [BuildingUpdateComponent],
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
      .overrideTemplate(BuildingUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(BuildingUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    buildingService = TestBed.inject(BuildingService);
    locationService = TestBed.inject(LocationService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call addressId query and add missing value', () => {
      const building: IBuilding = { id: 456 };
      const addressId: ILocation = { id: 93471 };
      building.addressId = addressId;

      const addressIdCollection: ILocation[] = [{ id: 70967 }];
      jest.spyOn(locationService, 'query').mockReturnValue(of(new HttpResponse({ body: addressIdCollection })));
      const expectedCollection: ILocation[] = [addressId, ...addressIdCollection];
      jest.spyOn(locationService, 'addLocationToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ building });
      comp.ngOnInit();

      expect(locationService.query).toHaveBeenCalled();
      expect(locationService.addLocationToCollectionIfMissing).toHaveBeenCalledWith(addressIdCollection, addressId);
      expect(comp.addressIdsCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const building: IBuilding = { id: 456 };
      const addressId: ILocation = { id: 9957 };
      building.addressId = addressId;

      activatedRoute.data = of({ building });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(building));
      expect(comp.addressIdsCollection).toContain(addressId);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Building>>();
      const building = { id: 123 };
      jest.spyOn(buildingService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ building });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: building }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(buildingService.update).toHaveBeenCalledWith(building);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Building>>();
      const building = new Building();
      jest.spyOn(buildingService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ building });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: building }));
      saveSubject.complete();

      // THEN
      expect(buildingService.create).toHaveBeenCalledWith(building);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Building>>();
      const building = { id: 123 };
      jest.spyOn(buildingService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ building });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(buildingService.update).toHaveBeenCalledWith(building);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackLocationById', () => {
      it('Should return tracked Location primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackLocationById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });
});
