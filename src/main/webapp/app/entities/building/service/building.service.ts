import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IBuilding, getBuildingIdentifier } from '../building.model';

export type EntityResponseType = HttpResponse<IBuilding>;
export type EntityArrayResponseType = HttpResponse<IBuilding[]>;

@Injectable({ providedIn: 'root' })
export class BuildingService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/buildings');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(building: IBuilding): Observable<EntityResponseType> {
    return this.http.post<IBuilding>(this.resourceUrl, building, { observe: 'response' });
  }

  update(building: IBuilding): Observable<EntityResponseType> {
    return this.http.put<IBuilding>(`${this.resourceUrl}/${getBuildingIdentifier(building) as number}`, building, { observe: 'response' });
  }

  partialUpdate(building: IBuilding): Observable<EntityResponseType> {
    return this.http.patch<IBuilding>(`${this.resourceUrl}/${getBuildingIdentifier(building) as number}`, building, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IBuilding>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IBuilding[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addBuildingToCollectionIfMissing(buildingCollection: IBuilding[], ...buildingsToCheck: (IBuilding | null | undefined)[]): IBuilding[] {
    const buildings: IBuilding[] = buildingsToCheck.filter(isPresent);
    if (buildings.length > 0) {
      const buildingCollectionIdentifiers = buildingCollection.map(buildingItem => getBuildingIdentifier(buildingItem)!);
      const buildingsToAdd = buildings.filter(buildingItem => {
        const buildingIdentifier = getBuildingIdentifier(buildingItem);
        if (buildingIdentifier == null || buildingCollectionIdentifiers.includes(buildingIdentifier)) {
          return false;
        }
        buildingCollectionIdentifiers.push(buildingIdentifier);
        return true;
      });
      return [...buildingsToAdd, ...buildingCollection];
    }
    return buildingCollection;
  }
}
