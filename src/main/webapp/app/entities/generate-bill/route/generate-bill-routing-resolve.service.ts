import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IGenerateBill, GenerateBill } from '../generate-bill.model';
import { GenerateBillService } from '../service/generate-bill.service';

@Injectable({ providedIn: 'root' })
export class GenerateBillRoutingResolveService implements Resolve<IGenerateBill> {
  constructor(protected service: GenerateBillService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IGenerateBill> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((generateBill: HttpResponse<GenerateBill>) => {
          if (generateBill.body) {
            return of(generateBill.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new GenerateBill());
  }
}
