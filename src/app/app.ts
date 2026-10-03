import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Article, ArticleDetail, CommentItem, DepecheApi, Rubrique, UserProfile } from './services/depeche-api';

type View = 'home' | 'articles' | 'article' | 'login' | 'register' | 'profile' | 'writer' | 'editor' | 'moderation' | 'admin';

type NewsItem = Article;

@Component({
  selector: 'app-root',
  imports: [FormsModule],
  templateUrl: './app.html',
  styleUrl: './app.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class App {
  private readonly api = inject(DepecheApi);
  protected readonly view = signal<View>('home');
  protected readonly articles = signal<NewsItem[]>([]);
  protected readonly selectedArticle = signal<ArticleDetail | null>(null);
  protected readonly comments = signal<CommentItem[]>([]);
  protected readonly commentsMessage = signal('');
  protected readonly isLoadingComments = signal(false);
  protected readonly pendingComments = signal<CommentItem[]>([]);
  protected readonly pendingArticles = signal<NewsItem[]>([]);
  protected readonly users = signal<UserProfile[]>([]);
  protected readonly rubriques = signal<Rubrique[]>([]);
  protected readonly currentUser = signal<UserProfile | null>(null);
  protected readonly profilePseudo = signal('');
  protected readonly profileInterests = signal('');
  protected readonly profileNewsletter = signal(false);
  protected readonly search = signal('');
  protected readonly category = signal('Toutes');
  protected readonly apiMessage = signal('');
  protected readonly categoryMessage = signal('');
  protected readonly isLoading = signal(true);
  protected readonly isLoadingArticle = signal(false);
  protected readonly articleMessage = signal('');
  protected readonly formMessage = signal('');
  protected readonly newsletterEmail = signal('');
  protected readonly newsletterMessage = signal('');
  protected readonly sessionUser = signal('');
  protected readonly busy = signal(false);
  protected readonly email = signal('');
  protected readonly password = signal('');
  protected readonly pseudonyme = signal('');
  protected readonly interests = signal('');
  protected readonly commentText = signal('');
  protected readonly articleTitle = signal('');
  protected readonly articleContent = signal('');
  protected readonly articleCategoryId = signal<number | null>(null);
  protected readonly articleVideoUrl = signal('');
  protected readonly newCategoryName = signal('');
  protected readonly dashboardMessage = signal('');
  protected readonly dashboardLoaded = signal(false);
  protected readonly categories = computed(() => ['Toutes', ...this.rubriques().map((rubrique) => rubrique.nom)]);
  protected readonly roles = ['LECTEUR', 'REDACTEUR', 'MODERATEUR', 'ADMINISTRATEUR'];
  protected readonly filteredArticles = computed(() => {
    const term = this.search().trim().toLocaleLowerCase('fr');
    const selectedCategory = this.category();
    const filtered = this.articles().filter((article) => {
      const matchesCategory = selectedCategory === 'Toutes' || article.rubriqueNom === selectedCategory;
      const matchesSearch = !term || `${article.titre} ${article.rubriqueNom} ${article.redacteurPseudo}`.toLocaleLowerCase('fr').includes(term);
      return matchesCategory && matchesSearch;
    });
    return [...filtered].sort((left, right) => Date.parse(right.datePublication ?? right.createdAt ?? '') - Date.parse(left.datePublication ?? left.createdAt ?? ''));
  });

  constructor() {
    void this.loadArticles();
    void this.loadRubriques();
  }

  protected navigate(view: View): void {
    this.view.set(view);
    this.formMessage.set('');
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  protected openArticle(article: NewsItem): void {
    this.selectedArticle.set(null);
    this.comments.set([]);
    this.commentsMessage.set('');
    this.isLoadingComments.set(false);
    this.view.set('article');
    this.formMessage.set('');
    this.articleMessage.set('');
    window.scrollTo({ top: 0, behavior: 'smooth' });
    this.isLoadingArticle.set(true);
    void this.loadArticleDetails(article);
  }

  protected excerpt(article: Article): string {
    return article.contenu.length > 180 ? `${article.contenu.slice(0, 180).trimEnd()}…` : article.contenu;
  }

  protected formattedDate(date?: string): string {
    if (!date) return '';
    return new Intl.DateTimeFormat('fr-FR', { day: 'numeric', month: 'long', year: 'numeric' }).format(new Date(date));
  }

  protected async submitAuth(): Promise<void> {
    this.busy.set(true);
    this.formMessage.set('');
    try {
      if (this.view() === 'register') {
        await this.api.register({
          pseudonyme: this.pseudonyme(),
          email: this.email(),
          password: this.password(),
          centreInteret: this.interests()
        });
        this.view.set('login');
        this.formMessage.set('Votre compte est créé. Vous pouvez maintenant vous connecter.');
      } else {
        const session = await this.api.login({ email: this.email(), password: this.password() });
        this.api.saveToken(session.token);
        const user = await this.api.getCurrentUser();
        this.currentUser.set(user);
        this.sessionUser.set(user.pseudonyme || session.pseudonyme);
        this.profilePseudo.set(user.pseudonyme);
        this.profileInterests.set(user.centreInteret ?? '');
        this.profileNewsletter.set(user.newsletterSubscribed);
        const destination = this.dashboardFor(user);
        this.navigate(destination);
        await this.loadDashboard(destination);
      }
    } catch {
      this.formMessage.set('Impossible de joindre le service. Vérifiez vos identifiants et que le backend est démarré.');
    } finally {
      this.busy.set(false);
    }
  }

  protected async toggleLike(): Promise<void> {
    const article = this.selectedArticle();
    if (!article) return;
    if (!this.api.hasToken()) {
      this.navigate('login');
      this.formMessage.set('Connectez-vous pour aimer un article.');
      return;
    }
    try {
      await this.api.toggleLike(article.id);
      const detail = await this.api.getArticle(article.id);
      this.selectedArticle.set(detail);
    } catch {
      this.formMessage.set('Le j’aime n’a pas pu être enregistré.');
    }
  }

  protected async submitComment(): Promise<void> {
    const article = this.selectedArticle();
    if (!article) return;
    const content = this.commentText().trim();
    if (!content) return;
    if (!this.api.hasToken()) {
      this.navigate('login');
      this.formMessage.set('Connectez-vous pour publier un commentaire.');
      return;
    }
    try {
      const comment = await this.api.addComment(article.id, content);
      if (comment.statut === 'APPROUVE') {
        this.comments.update((current) => [comment, ...current]);
      }
      this.commentText.set('');
      this.formMessage.set(comment.statut === 'APPROUVE' ? 'Votre commentaire est publié.' : 'Commentaire envoyé pour modération.');
    } catch {
      this.formMessage.set('Le commentaire n’a pas pu être envoyé.');
    }
  }

  protected logout(): void {
    this.api.clearToken();
    this.sessionUser.set('');
    this.currentUser.set(null);
    this.navigate('home');
  }

  protected dashboardFor(user = this.currentUser()): View {
    if (!user) return 'profile';
    if (user.roles.includes('ADMINISTRATEUR')) return 'admin';
    if (user.roles.includes('MODERATEUR')) return 'moderation';
    if (user.roles.includes('REDACTEUR')) return 'writer';
    return 'profile';
  }

  protected async openDashboard(view: View): Promise<void> {
    this.navigate(view);
    await this.loadDashboard(view);
  }

  protected async saveProfile(): Promise<void> {
    const profile = this.currentUser();
    if (!profile) return;
    this.busy.set(true);
    this.dashboardMessage.set('');
    try {
      const updated = await this.api.updateProfile({
        pseudonyme: this.profilePseudo(),
        centreInteret: this.profileInterests(),
        newsletterSubscribed: this.profileNewsletter()
      });
      this.currentUser.set(updated);
      this.sessionUser.set(updated.pseudonyme);
      this.dashboardMessage.set('Profil enregistré.');
    } catch {
      this.dashboardMessage.set('Le profil n’a pas pu être enregistré.');
    } finally {
      this.busy.set(false);
    }
  }

  protected async createArticle(submit: boolean): Promise<void> {
    const rubriqueId = this.articleCategoryId();
    if (!this.articleTitle().trim() || !this.articleContent().trim() || rubriqueId === null) return;
    this.busy.set(true);
    this.dashboardMessage.set('');
    try {
      const article = await this.api.createArticle({
        titre: this.articleTitle().trim(),
        contenu: this.articleContent().trim(),
        rubriqueId,
        videoUrl: this.articleVideoUrl().trim() || undefined
      });
      const result = submit ? await this.api.submitArticle(article.id) : article;
      this.dashboardMessage.set(submit ? 'Article envoyé pour validation.' : 'Brouillon enregistré.');
      this.pendingArticles.update((current) => [result, ...current]);
      this.articleTitle.set('');
      this.articleContent.set('');
      this.articleVideoUrl.set('');
      this.view.set('writer');
    } catch {
      this.dashboardMessage.set('L’article n’a pas pu être enregistré. Vérifiez votre rôle et la rubrique choisie.');
    } finally {
      this.busy.set(false);
    }
  }

  protected async decideComment(comment: CommentItem, decision: 'APPROUVE' | 'REJETE'): Promise<void> {
    try {
      await this.api.moderateComment(comment.id, decision);
      this.pendingComments.update((items) => items.filter((item) => item.id !== comment.id));
    } catch {
      this.dashboardMessage.set('La décision de modération n’a pas pu être enregistrée.');
    }
  }

  protected async publishArticle(article: Article): Promise<void> {
    try {
      await this.api.publishArticle(article.id);
      this.pendingArticles.update((items) => items.filter((item) => item.id !== article.id));
    } catch {
      this.dashboardMessage.set('La publication a échoué.');
    }
  }

  protected async rejectArticle(article: Article): Promise<void> {
    try {
      await this.api.rejectArticle(article.id, 'Article refusé par la rédaction.');
      this.pendingArticles.update((items) => items.filter((item) => item.id !== article.id));
    } catch {
      this.dashboardMessage.set('Le refus de cet article a échoué.');
    }
  }

  protected async changeRole(user: UserProfile, role: string): Promise<void> {
    try {
      const updated = await this.api.updateUserRole(user.id, role);
      this.users.update((items) => items.map((item) => item.id === user.id ? updated : item));
    } catch {
      this.dashboardMessage.set('Le rôle n’a pas pu être modifié.');
    }
  }

  protected async createRubrique(): Promise<void> {
    const nom = this.newCategoryName().trim();
    if (!nom) return;
    try {
      const created = await this.api.createRubrique(nom);
      this.rubriques.update((items) => [...items, created]);
      this.newCategoryName.set('');
    } catch {
      this.dashboardMessage.set('La rubrique n’a pas pu être créée.');
    }
  }

  protected async subscribe(): Promise<void> {
    this.busy.set(true);
    this.newsletterMessage.set('');
    try {
      await this.api.subscribe(this.newsletterEmail());
      this.newsletterMessage.set('Merci, votre inscription est confirmée.');
      this.newsletterEmail.set('');
    } catch {
      this.newsletterMessage.set('Cette adresse n’a pas pu être inscrite pour le moment.');
    } finally {
      this.busy.set(false);
    }
  }

  protected async shareArticle(): Promise<void> {
    try {
      await navigator.clipboard.writeText(window.location.href);
      this.formMessage.set('Lien copié dans le presse-papiers.');
    } catch {
      this.formMessage.set('Le partage n’est pas disponible dans ce navigateur.');
    }
  }

  private async loadArticles(): Promise<void> {
    this.isLoading.set(true);
    this.apiMessage.set('');
    try {
      const response = await this.api.listArticles();
      this.articles.set(response.content);
    } catch {
      this.articles.set([]);
      this.apiMessage.set('Impossible de charger les articles depuis le serveur. Réessayez lorsque l’API sera disponible.');
    } finally {
      this.isLoading.set(false);
    }
  }

  private async loadRubriques(): Promise<void> {
    this.categoryMessage.set('');
    try {
      this.rubriques.set(await this.api.listRubriques());
    } catch {
      this.rubriques.set([]);
      this.categoryMessage.set('Les rubriques ne sont pas accessibles depuis le serveur.');
    }
  }

  private async loadDashboard(view: View): Promise<void> {
    this.dashboardMessage.set('');
    this.dashboardLoaded.set(false);
    try {
      if (view === 'profile') {
        const user = this.currentUser() ?? await this.api.getCurrentUser();
        this.currentUser.set(user);
        this.profilePseudo.set(user.pseudonyme);
        this.profileInterests.set(user.centreInteret ?? '');
        this.profileNewsletter.set(user.newsletterSubscribed);
      } else if (view === 'writer') {
        this.rubriques.set(await this.api.listRubriques());
      } else if (view === 'moderation') {
        this.pendingComments.set((await this.api.listPendingComments()).content);
      } else if (view === 'admin') {
        const [submitted, users, rubriques] = await Promise.all([
          this.api.listArticles('SOUMIS', 50),
          this.api.listUsers(),
          this.api.listRubriques()
        ]);
        this.pendingArticles.set(submitted.content);
        this.users.set(users.content);
        this.rubriques.set(rubriques);
      }
      this.dashboardLoaded.set(true);
    } catch {
      this.dashboardMessage.set('Les données de cet espace ne sont pas accessibles. Vérifiez votre rôle et la connexion au backend.');
    }
  }

  private async loadArticleDetails(article: NewsItem): Promise<void> {
    try {
      const detail = await this.api.getArticle(article.id);
      this.selectedArticle.set(detail);
    } catch {
      this.articleMessage.set('Impossible de charger cet article depuis le serveur.');
      return;
    } finally {
      this.isLoadingArticle.set(false);
    }
    try {
      this.comments.set((await this.api.listComments(article.id)).content);
    } catch {
      this.comments.set([]);
      this.commentsMessage.set('Les commentaires ne sont pas accessibles depuis le serveur.');
    } finally {
      this.isLoadingComments.set(false);
    }
  }
}
