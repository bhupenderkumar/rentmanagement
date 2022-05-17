import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IOwner, getOwnerIdentifier } from '../owner.model';

export type EntityResponseType = HttpResponse<IOwner>;
export type EntityArrayResponseType = HttpResponse<IOwner[]>;

@Injectable({ providedIn: 'root' })
export class OwnerService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/owners');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(owner: IOwner): Observable<EntityResponseType> {
    return this.http.post<IOwner>(this.resourceUrl, owner, { observe: 'response' });
  }

  update(owner: IOwner): Observable<EntityResponseType> {
    return this.http.put<IOwner>(`${this.resourceUrl}/${getOwnerIdentifier(owner) as number}`, owner, { observe: 'response' });
  }

  partialUpdate(owner: IOwner): Observable<EntityResponseType> {
    return this.http.patch<IOwner>(`${this.resourceUrl}/${getOwnerIdentifier(owner) as number}`, owner, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IOwner>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IOwner[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addOwnerToCollectionIfMissing(ownerCollection: IOwner[], ...ownersToCheck: (IOwner | null | undefined)[]): IOwner[] {
    const owners: IOwner[] = ownersToCheck.filter(isPresent);
    if (owners.length > 0) {
      const ownerCollectionIdentifiers = ownerCollection.map(ownerItem => getOwnerIdentifier(ownerItem)!);
      const ownersToAdd = owners.filter(ownerItem => {
        const ownerIdentifier = getOwnerIdentifier(ownerItem);
        if (ownerIdentifier == null || ownerCollectionIdentifiers.includes(ownerIdentifier)) {
          return false;
        }
        ownerCollectionIdentifiers.push(ownerIdentifier);
        return true;
      });
      return [...ownersToAdd, ...ownerCollection];
    }
    return ownerCollection;
  }
}
