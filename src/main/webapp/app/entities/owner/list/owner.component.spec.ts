import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { OwnerService } from '../service/owner.service';

import { OwnerComponent } from './owner.component';

describe('Owner Management Component', () => {
  let comp: OwnerComponent;
  let fixture: ComponentFixture<OwnerComponent>;
  let service: OwnerService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [OwnerComponent],
    })
      .overrideTemplate(OwnerComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(OwnerComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(OwnerService);

    const headers = new HttpHeaders();
    jest.spyOn(service, 'query').mockReturnValue(
      of(
        new HttpResponse({
          body: [{ id: 123 }],
          headers,
        })
      )
    );
  });

  it('Should call load all on init', () => {
    // WHEN
    comp.ngOnInit();

    // THEN
    expect(service.query).toHaveBeenCalled();
    expect(comp.owners?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });
});
