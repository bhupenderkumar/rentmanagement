import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, ActivatedRoute, Router, convertToParamMap } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { IGenerateBill, GenerateBill } from '../generate-bill.model';
import { GenerateBillService } from '../service/generate-bill.service';

import { GenerateBillRoutingResolveService } from './generate-bill-routing-resolve.service';

describe('GenerateBill routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let routingResolveService: GenerateBillRoutingResolveService;
  let service: GenerateBillService;
  let resultGenerateBill: IGenerateBill | undefined;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: convertToParamMap({}),
            },
          },
        },
      ],
    });
    mockRouter = TestBed.inject(Router);
    jest.spyOn(mockRouter, 'navigate').mockImplementation(() => Promise.resolve(true));
    mockActivatedRouteSnapshot = TestBed.inject(ActivatedRoute).snapshot;
    routingResolveService = TestBed.inject(GenerateBillRoutingResolveService);
    service = TestBed.inject(GenerateBillService);
    resultGenerateBill = undefined;
  });

  describe('resolve', () => {
    it('should return IGenerateBill returned by find', () => {
      // GIVEN
      service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultGenerateBill = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultGenerateBill).toEqual({ id: 123 });
    });

    it('should return new IGenerateBill if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultGenerateBill = result;
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultGenerateBill).toEqual(new GenerateBill());
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as GenerateBill })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultGenerateBill = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultGenerateBill).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
