import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { BuildingService } from '../service/building.service';

import { BuildingComponent } from './building.component';

describe('Building Management Component', () => {
  let comp: BuildingComponent;
  let fixture: ComponentFixture<BuildingComponent>;
  let service: BuildingService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [BuildingComponent],
    })
      .overrideTemplate(BuildingComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(BuildingComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(BuildingService);

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
    expect(comp.buildings?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });
});
