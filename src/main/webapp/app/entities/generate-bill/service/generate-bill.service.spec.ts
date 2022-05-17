import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IGenerateBill, GenerateBill } from '../generate-bill.model';

import { GenerateBillService } from './generate-bill.service';

describe('GenerateBill Service', () => {
  let service: GenerateBillService;
  let httpMock: HttpTestingController;
  let elemDefault: IGenerateBill;
  let expectedResult: IGenerateBill | IGenerateBill[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(GenerateBillService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      amountPending: 0,
      sendNotification: false,
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

    it('should create a GenerateBill', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new GenerateBill()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a GenerateBill', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          amountPending: 1,
          sendNotification: true,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a GenerateBill', () => {
      const patchObject = Object.assign(
        {
          amountPending: 1,
          sendNotification: true,
        },
        new GenerateBill()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of GenerateBill', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          amountPending: 1,
          sendNotification: true,
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

    it('should delete a GenerateBill', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addGenerateBillToCollectionIfMissing', () => {
      it('should add a GenerateBill to an empty array', () => {
        const generateBill: IGenerateBill = { id: 123 };
        expectedResult = service.addGenerateBillToCollectionIfMissing([], generateBill);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(generateBill);
      });

      it('should not add a GenerateBill to an array that contains it', () => {
        const generateBill: IGenerateBill = { id: 123 };
        const generateBillCollection: IGenerateBill[] = [
          {
            ...generateBill,
          },
          { id: 456 },
        ];
        expectedResult = service.addGenerateBillToCollectionIfMissing(generateBillCollection, generateBill);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a GenerateBill to an array that doesn't contain it", () => {
        const generateBill: IGenerateBill = { id: 123 };
        const generateBillCollection: IGenerateBill[] = [{ id: 456 }];
        expectedResult = service.addGenerateBillToCollectionIfMissing(generateBillCollection, generateBill);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(generateBill);
      });

      it('should add only unique GenerateBill to an array', () => {
        const generateBillArray: IGenerateBill[] = [{ id: 123 }, { id: 456 }, { id: 2235 }];
        const generateBillCollection: IGenerateBill[] = [{ id: 123 }];
        expectedResult = service.addGenerateBillToCollectionIfMissing(generateBillCollection, ...generateBillArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const generateBill: IGenerateBill = { id: 123 };
        const generateBill2: IGenerateBill = { id: 456 };
        expectedResult = service.addGenerateBillToCollectionIfMissing([], generateBill, generateBill2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(generateBill);
        expect(expectedResult).toContain(generateBill2);
      });

      it('should accept null and undefined values', () => {
        const generateBill: IGenerateBill = { id: 123 };
        expectedResult = service.addGenerateBillToCollectionIfMissing([], null, generateBill, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(generateBill);
      });

      it('should return initial array if no GenerateBill is added', () => {
        const generateBillCollection: IGenerateBill[] = [{ id: 123 }];
        expectedResult = service.addGenerateBillToCollectionIfMissing(generateBillCollection, undefined, null);
        expect(expectedResult).toEqual(generateBillCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
