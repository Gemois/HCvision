import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HierarchicalComponent } from './hierarchical.component';

describe('HierarchicalComponent', () => {
  let component: HierarchicalComponent;
  let fixture: ComponentFixture<HierarchicalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [HierarchicalComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(HierarchicalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
