import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ArticlesCard } from './articles-card';

describe('ArticlesCard', () => {
  let component: ArticlesCard;
  let fixture: ComponentFixture<ArticlesCard>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ArticlesCard],
    }).compileComponents();

    fixture = TestBed.createComponent(ArticlesCard);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
