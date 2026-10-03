import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { App } from './app';

describe('App', () => {
  let http: HttpTestingController;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [App],
      providers: [provideHttpClient(), provideHttpClientTesting()],
    }).compileComponents();
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => http?.verify());

  it('should not render article data when the backend is unavailable', async () => {
    const fixture = TestBed.createComponent(App);
    fixture.detectChanges();
    http.expectOne((request) => request.url === '/api/v1/articles').flush({}, {
      status: 500,
      statusText: 'Internal Server Error',
    });
    http.expectOne('/api/v1/categories').flush({}, {
      status: 403,
      statusText: 'Forbidden',
    });
    await fixture.whenStable();
    fixture.detectChanges();

    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('#lead-title')).toBeNull();
    expect(compiled.querySelector('.story-card')).toBeNull();
    expect(compiled.textContent).not.toContain('Aïda Kaboré');
    expect(compiled.textContent).toContain('Impossible de charger les articles depuis le serveur.');
  });

  it('should render an empty list without introducing sample articles', async () => {
    const fixture = TestBed.createComponent(App);
    fixture.detectChanges();
    http.expectOne((request) => request.url === '/api/v1/articles').flush(emptyArticles);
    http.expectOne('/api/v1/categories').flush([]);
    await fixture.whenStable();
    fixture.detectChanges();
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('#lead-title')).toBeNull();
    expect(compiled.textContent).toContain('Aucun article publié.');

    const listingButton = [...compiled.querySelectorAll('.main-nav button')]
      .find((button) => button.textContent?.includes('Tous les articles'));
    listingButton?.dispatchEvent(new Event('click'));
    fixture.detectChanges();

    expect(compiled.querySelector('.listing-intro h1')?.textContent).toContain('Toutes les dépêches');
    expect(compiled.querySelector('.listing-card')).toBeNull();
  });
});

const emptyArticles = {
  content: [],
  page: 0,
  size: 30,
  totalElements: 0,
  totalPages: 0,
  last: true,
};
