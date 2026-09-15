# Depeche226 Backend

Backend Spring Boot 3 pour la plateforme éditoriale Depeche226.

## Stack

- Java 17
- Spring Boot 3.3.3
- Spring Security + JWT
- Spring Data JPA + PostgreSQL
- Flyway
- OpenAPI / Swagger UI
- JUnit 5 + Testcontainers

## Démarrage local sans Docker

on peut démarrer l'API directement avec H2 en mémoire. Aucune installation de PostgreSQL ou de Docker n'est nécessaire pour les tests locaux.

```bash
./mvnw spring-boot:run
```

Sous PowerShell Windows, je peux aussi utiliser :

```powershell
.\mvnw.cmd spring-boot:run
```

Les données H2 sont en mémoire et sont effacées à chaque arrêt de l'application. La configuration est centralisée dans `src/main/resources/application.properties`.

## Démarrage avec PostgreSQL et Docker

Quand je voudrai passer à PostgreSQL, je pourrai démarrer la base avec :

```powershell
docker compose up -d postgres
```

Puis je lancerai l'application avec les variables PostgreSQL adaptées, ou directement tout le projet :

```powershell
docker compose up --build
```

## API principale

- Auth : `/api/v1/auth/register`, `/api/v1/auth/login`, `/api/v1/auth/me`
- Articles : `/api/v1/articles`, `/api/v1/articles/{id}`
- Commentaires : `/api/v1/articles/{articleId}/comments`
- Newsletter : `/api/v1/newsletter/subscribe`
- Swagger UI : `/swagger-ui.html`

## Test

```bash
./mvnw test
```
