import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { firstValueFrom } from 'rxjs';

export interface Article {
  id: number;
  titre: string;
  contenu: string;
  statut: string;
  videoUrl?: string;
  youtubeVideoId?: string;
  datePublication?: string;
  rubriqueNom: string;
  redacteurPseudo: string;
  createdAt?: string;
  likesCount?: number;
}

export interface ArticleDetail extends Article {
  rubriqueId?: number;
  updatedAt?: string;
  likesCount: number;
}

export interface UserProfile {
  id: number;
  pseudonyme: string;
  email: string;
  centreInteret: string;
  newsletterSubscribed: boolean;
  enabled: boolean;
  roles: string[];
  createdAt: string;
}

export interface Rubrique {
  id: number;
  nom: string;
  slug: string;
  createdAt: string;
}

export interface CommentItem {
  id: number;
  contenu: string;
  statut: string;
  auteurPseudo: string;
  dateCreation: string;
  rejectionReason?: string;
}

export interface PageResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  last: boolean;
}

export interface AuthSession {
  token: string;
  type: string;
  email: string;
  pseudonyme: string;
}

@Injectable({ providedIn: 'root' })
export class DepecheApi {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = '/api/v1';
  private readonly tokenKey = 'depeche226-token';

  listArticles(statut = 'PUBLIE', size = 30): Promise<PageResponse<Article>> {
    const params = new HttpParams()
      .set('page', 0)
      .set('size', size)
      .set('sort', 'datePublication')
      .set('direction', 'desc')
      .set('statut', statut);
    return firstValueFrom(this.http.get<PageResponse<Article>>(`${this.baseUrl}/articles`, { params }));
  }

  getArticle(id: number): Promise<ArticleDetail> {
    return firstValueFrom(this.http.get<ArticleDetail>(`${this.baseUrl}/articles/${id}`));
  }

  getCurrentUser(): Promise<UserProfile> {
    return firstValueFrom(this.http.get<UserProfile>(`${this.baseUrl}/users/me`, { headers: this.authHeaders() }));
  }

  updateProfile(request: { pseudonyme: string; centreInteret: string; newsletterSubscribed: boolean }): Promise<UserProfile> {
    return firstValueFrom(this.http.patch<UserProfile>(`${this.baseUrl}/users/me`, request, { headers: this.authHeaders() }));
  }

  listComments(articleId: number): Promise<PageResponse<CommentItem>> {
    return firstValueFrom(this.http.get<PageResponse<CommentItem>>(`${this.baseUrl}/articles/${articleId}/comments`));
  }

  listPendingComments(): Promise<PageResponse<CommentItem>> {
    return firstValueFrom(this.http.get<PageResponse<CommentItem>>(`${this.baseUrl}/moderation/comments`, {
      params: new HttpParams().set('page', 0).set('size', 50),
      headers: this.authHeaders()
    }));
  }

  moderateComment(commentId: number, decision: 'APPROUVE' | 'REJETE'): Promise<CommentItem> {
    return firstValueFrom(this.http.post<CommentItem>(
      `${this.baseUrl}/moderation/comments/${commentId}/${decision === 'APPROUVE' ? 'approve' : 'reject'}`,
      { decision, rejectionReason: decision === 'REJETE' ? 'Ne respecte pas les règles de publication.' : '' },
      { headers: this.authHeaders() }
    ));
  }

  listUsers(): Promise<PageResponse<UserProfile>> {
    return firstValueFrom(this.http.get<PageResponse<UserProfile>>(`${this.baseUrl}/users`, {
      params: new HttpParams().set('page', 0).set('size', 100),
      headers: this.authHeaders()
    }));
  }

  updateUserRole(userId: number, role: string): Promise<UserProfile> {
    return firstValueFrom(this.http.patch<UserProfile>(`${this.baseUrl}/users/${userId}/roles`, [role], { headers: this.authHeaders() }));
  }

  deleteUser(userId: number): Promise<void> {
    return firstValueFrom(this.http.delete<void>(`${this.baseUrl}/users/${userId}`, { headers: this.authHeaders() }));
  }

  listRubriques(): Promise<Rubrique[]> {
    return firstValueFrom(this.http.get<Rubrique[]>(`${this.baseUrl}/categories`));
  }

  createRubrique(nom: string): Promise<Rubrique> {
    return firstValueFrom(this.http.post<Rubrique>(`${this.baseUrl}/categories`, { nom }, { headers: this.authHeaders() }));
  }

  createArticle(request: { titre: string; contenu: string; rubriqueId: number; videoUrl?: string }): Promise<ArticleDetail> {
    return firstValueFrom(this.http.post<ArticleDetail>(`${this.baseUrl}/articles`, request, { headers: this.authHeaders() }));
  }

  submitArticle(articleId: number): Promise<ArticleDetail> {
    return firstValueFrom(this.http.post<ArticleDetail>(`${this.baseUrl}/articles/${articleId}/submit`, {}, { headers: this.authHeaders() }));
  }

  publishArticle(articleId: number): Promise<ArticleDetail> {
    return firstValueFrom(this.http.post<ArticleDetail>(`${this.baseUrl}/articles/${articleId}/publish`, {}, { headers: this.authHeaders() }));
  }

  rejectArticle(articleId: number, reason: string): Promise<ArticleDetail> {
    return firstValueFrom(this.http.post<ArticleDetail>(`${this.baseUrl}/articles/${articleId}/reject`, reason, {
      headers: this.authHeaders().set('Content-Type', 'text/plain')
    }));
  }

  register(request: { pseudonyme: string; email: string; password: string; centreInteret: string }): Promise<unknown> {
    return firstValueFrom(this.http.post(`${this.baseUrl}/auth/register`, request));
  }

  login(request: { email: string; password: string }): Promise<AuthSession> {
    return firstValueFrom(this.http.post<AuthSession>(`${this.baseUrl}/auth/login`, request));
  }

  addComment(articleId: number, contenu: string): Promise<CommentItem> {
    return firstValueFrom(this.http.post<CommentItem>(
      `${this.baseUrl}/articles/${articleId}/comments`,
      { contenu },
      { headers: this.authHeaders() }
    ));
  }

  toggleLike(articleId: number): Promise<boolean> {
    return firstValueFrom(this.http.post<boolean>(
      `${this.baseUrl}/articles/${articleId}/like`,
      {},
      { headers: this.authHeaders() }
    ));
  }

  subscribe(email: string): Promise<unknown> {
    return firstValueFrom(this.http.post(`${this.baseUrl}/newsletter/subscribe`, { email }));
  }

  saveToken(token: string): void {
    localStorage.setItem(this.tokenKey, token);
  }

  getToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }

  hasToken(): boolean {
    return Boolean(this.getToken());
  }

  clearToken(): void {
    localStorage.removeItem(this.tokenKey);
  }

  private authHeaders(): HttpHeaders {
    return new HttpHeaders({ Authorization: `Bearer ${this.getToken() ?? ''}` });
  }
}