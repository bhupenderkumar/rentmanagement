import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { GenerateBillService } from '../service/generate-bill.service';

import { GenerateBillComponent } from './generate-bill.component';

describe('GenerateBill Management Component', () => {
  let comp: GenerateBillComponent;
  let fixture: ComponentFixture<GenerateBillComponent>;
  let service: GenerateBillService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [GenerateBillComponent],
    })
      .overrideTemplate(GenerateBillComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(GenerateBillComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(GenerateBillService);

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
    expect(comp.generateBills?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });
});
