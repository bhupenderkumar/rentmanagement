import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IGenerateBill, getGenerateBillIdentifier } from '../generate-bill.model';

export type EntityResponseType = HttpResponse<IGenerateBill>;
export type EntityArrayResponseType = HttpResponse<IGenerateBill[]>;

@Injectable({ providedIn: 'root' })
export class GenerateBillService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/generate-bills');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(generateBill: IGenerateBill): Observable<EntityResponseType> {
    return this.http.post<IGenerateBill>(this.resourceUrl, generateBill, { observe: 'response' });
  }

  update(generateBill: IGenerateBill): Observable<EntityResponseType> {
    return this.http.put<IGenerateBill>(`${this.resourceUrl}/${getGenerateBillIdentifier(generateBill) as number}`, generateBill, {
      observe: 'response',
    });
  }

  partialUpdate(generateBill: IGenerateBill): Observable<EntityResponseType> {
    return this.http.patch<IGenerateBill>(`${this.resourceUrl}/${getGenerateBillIdentifier(generateBill) as number}`, generateBill, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IGenerateBill>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IGenerateBill[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addGenerateBillToCollectionIfMissing(
    generateBillCollection: IGenerateBill[],
    ...generateBillsToCheck: (IGenerateBill | null | undefined)[]
  ): IGenerateBill[] {
    const generateBills: IGenerateBill[] = generateBillsToCheck.filter(isPresent);
    if (generateBills.length > 0) {
      const generateBillCollectionIdentifiers = generateBillCollection.map(
        generateBillItem => getGenerateBillIdentifier(generateBillItem)!
      );
      const generateBillsToAdd = generateBills.filter(generateBillItem => {
        const generateBillIdentifier = getGenerateBillIdentifier(generateBillItem);
        if (generateBillIdentifier == null || generateBillCollectionIdentifiers.includes(generateBillIdentifier)) {
          return false;
        }
        generateBillCollectionIdentifiers.push(generateBillIdentifier);
        return true;
      });
      return [...generateBillsToAdd, ...generateBillCollection];
    }
    return generateBillCollection;
  }
}
