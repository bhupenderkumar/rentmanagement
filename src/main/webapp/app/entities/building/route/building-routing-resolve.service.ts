import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IBuilding, Building } from '../building.model';
import { BuildingService } from '../service/building.service';

@Injectable({ providedIn: 'root' })
export class BuildingRoutingResolveService implements Resolve<IBuilding> {
  constructor(protected service: BuildingService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IBuilding> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((building: HttpResponse<Building>) => {
          if (building.body) {
            return of(building.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Building());
  }
}
