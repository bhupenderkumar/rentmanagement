import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { BuildingDetailComponent } from './building-detail.component';

describe('Building Management Detail Component', () => {
  let comp: BuildingDetailComponent;
  let fixture: ComponentFixture<BuildingDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [BuildingDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ building: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(BuildingDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(BuildingDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load building on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.building).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
