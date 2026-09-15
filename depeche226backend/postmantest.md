# Postman test - Depeche226 backend

Je liste ici les endpoints exposes par l’API, avec les chemins exacts et le type HTTP attendu. J’ai laisse le backend vide par defaut: pas de donnees seed, pas de compte admin pre-cree, tout doit etre configure et cree manuellement dans Postman.

## 1) Authentification

### POST /api/v1/auth/register

- Description: inscription d’un utilisateur.
- Body JSON attendu: nom, email, password, etc. selon le DTO `RegisterRequest` du projet.

### POST /api/v1/auth/login

- Description: connexion utilisateur et generation du JWT.
- Body JSON attendu: email + password.

### GET /api/v1/auth/me

- Description: retourne le profil du user courant.
- Authentification: Bearer JWT requis.

## 2) Utilisateurs

### GET /api/v1/users/me

- Description: profil courant.
- Authentification: Bearer JWT requis.

### PATCH /api/v1/users/me

- Description: mise a jour du profil courant.
- Authentification: Bearer JWT requis.

### PATCH /api/v1/users/me/password

- Description: changement de mot de passe.
- Authentification: Bearer JWT requis.

### DELETE /api/v1/users/{id}

- Description: suppression d’un compte.
- Authentification: Bearer JWT requis.
- Role requis: ADMINISTRATEUR.

### GET /api/v1/users

- Description: liste des utilisateurs.
- Authentification: Bearer JWT requis.
- Role requis: ADMINISTRATEUR.

### PATCH /api/v1/users/{id}/roles

- Description: mise a jour des roles d’un utilisateur.
- Authentification: Bearer JWT requis.
- Role requis: ADMINISTRATEUR.

## 3) Rubriques / categories

### GET /api/v1/categories

- Description: liste toutes les rubriques.

### POST /api/v1/categories

- Description: creation d’une rubrique.
- Authentification: Bearer JWT requis.
- Role requis: ADMINISTRATEUR.

### PUT /api/v1/categories/{id}

- Description: modification d’une rubrique.
- Authentification: Bearer JWT requis.
- Role requis: ADMINISTRATEUR.

### DELETE /api/v1/categories/{id}

- Description: suppression d’une rubrique.
- Authentification: Bearer JWT requis.
- Role requis: ADMINISTRATEUR.

## 4) Articles

### GET /api/v1/articles

- Description: liste paginee des articles.
- Parametres optionnels: page, size, sort, direction, search, rubriqueId, statut.

### GET /api/v1/articles/{id}

- Description: detail d’un article.
- Visible publiquement si l’article est publie.

### POST /api/v1/articles

- Description: creation d’un article.
- Authentification: Bearer JWT requis.
- Roles requis: REDACTEUR ou ADMINISTRATEUR.

### PUT /api/v1/articles/{id}

- Description: modification d’un article.
- Authentification: Bearer JWT requis.

### DELETE /api/v1/articles/{id}

- Description: suppression d’un article.
- Authentification: Bearer JWT requis.

### POST /api/v1/articles/{id}/submit

- Description: soumission d’un article pour validation.
- Authentification: Bearer JWT requis.

### POST /api/v1/articles/{id}/publish

- Description: publication d’un article.
- Authentification: Bearer JWT requis.
- Role requis: ADMINISTRATEUR.

### POST /api/v1/articles/{id}/reject

- Description: rejet d’un article.
- Authentification: Bearer JWT requis.
- Role requis: ADMINISTRATEUR.

### GET /api/v1/articles/{articleId}/comments

- Description: commentaires approuves d’un article.

### POST /api/v1/articles/{articleId}/comments

- Description: ajout d’un commentaire.
- Authentification: Bearer JWT requis.

### POST /api/v1/articles/{articleId}/like

- Description: toggle like sur un article.
- Authentification: Bearer JWT requis.

### DELETE /api/v1/articles/{articleId}/like

- Description: retrait du like.
- Authentification: Bearer JWT requis.

### GET /api/v1/articles/{articleId}/likes/count

- Description: nombre de likes sur un article.

## 5) Moderation des commentaires

### GET /api/v1/moderation/comments

- Description: liste des commentaires a moderer.
- Authentification: Bearer JWT requis.
- Roles requis: MODERATEUR ou ADMINISTRATEUR.

### POST /api/v1/moderation/comments/{commentId}/approve

- Description: approuver un commentaire.
- Authentification: Bearer JWT requis.
- Roles requis: MODERATEUR ou ADMINISTRATEUR.

### POST /api/v1/moderation/comments/{commentId}/reject

- Description: rejeter un commentaire.
- Authentification: Bearer JWT requis.
- Roles requis: MODERATEUR ou ADMINISTRATEUR.

## 6) Newsletter

### POST /api/v1/newsletter/subscribe

- Description: inscription a la newsletter.

### POST /api/v1/newsletter/unsubscribe

- Description: desinscription a la newsletter.

### GET /api/v1/newsletter/status

- Description: statut de la newsletter pour un email.
- Parametre: email.

## 7) Swagger / doc OpenAPI

- Swagger UI: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- OpenAPI JSON: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

## 8) Tips de test rapide dans Postman

1. Je commence par creer un user via POST /api/v1/auth/register.
2. Je me connecte avec POST /api/v1/auth/login pour recuperer le token JWT.
3. Je copie le token dans l’onglet Authorization -> Bearer Token.
4. Pour les endpoints admin, je cree un compte puis j’attribue le role ADMINISTRATEUR depuis le backend ou via la logique metier de mon setup local.
5. Une fois le token en place, je peux tester la creation d’articles, de commentaires, de likes et la moderation.

> Important: j’ai laisse le projet volontairement vide par defaut. Il ne cree ni user, ni rubrique, ni article au demarrage. Je fais tout manuellement pour garder le controle total sur mes donnees de test.
