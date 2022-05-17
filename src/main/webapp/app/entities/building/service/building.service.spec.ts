import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IBuilding, Building } from '../building.model';

import { BuildingService } from './building.service';

describe('Building Service', () => {
  let service: BuildingService;
  let httpMock: HttpTestingController;
  let elemDefault: IBuilding;
  let expectedResult: IBuilding | IBuilding[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(BuildingService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      buildingName: 'AAAAAAA',
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign({}, elemDefault);

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a Building', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Building()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Building', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          buildingName: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Building', () => {
      const patchObject = Object.assign(
        {
          buildingName: 'BBBBBB',
        },
        new Building()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Building', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          buildingName: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a Building', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addBuildingToCollectionIfMissing', () => {
      it('should add a Building to an empty array', () => {
        const building: IBuilding = { id: 123 };
        expectedResult = service.addBuildingToCollectionIfMissing([], building);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(building);
      });

      it('should not add a Building to an array that contains it', () => {
        const building: IBuilding = { id: 123 };
        const buildingCollection: IBuilding[] = [
          {
            ...building,
          },
          { id: 456 },
        ];
        expectedResult = service.addBuildingToCollectionIfMissing(buildingCollection, building);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Building to an array that doesn't contain it", () => {
        const building: IBuilding = { id: 123 };
        const buildingCollection: IBuilding[] = [{ id: 456 }];
        expectedResult = service.addBuildingToCollectionIfMissing(buildingCollection, building);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(building);
      });

      it('should add only unique Building to an array', () => {
        const buildingArray: IBuilding[] = [{ id: 123 }, { id: 456 }, { id: 40106 }];
        const buildingCollection: IBuilding[] = [{ id: 123 }];
        expectedResult = service.addBuildingToCollectionIfMissing(buildingCollection, ...buildingArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const building: IBuilding = { id: 123 };
        const building2: IBuilding = { id: 456 };
        expectedResult = service.addBuildingToCollectionIfMissing([], building, building2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(building);
        expect(expectedResult).toContain(building2);
      });

      it('should accept null and undefined values', () => {
        const building: IBuilding = { id: 123 };
        expectedResult = service.addBuildingToCollectionIfMissing([], null, building, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(building);
      });

      it('should return initial array if no Building is added', () => {
        const buildingCollection: IBuilding[] = [{ id: 123 }];
        expectedResult = service.addBuildingToCollectionIfMissing(buildingCollection, undefined, null);
        expect(expectedResult).toEqual(buildingCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
