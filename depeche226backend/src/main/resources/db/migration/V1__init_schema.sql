CREATE TABLE IF NOT EXISTS users (
  id BIGSERIAL PRIMARY KEY,
  pseudonyme VARCHAR(80) NOT NULL UNIQUE,
  email VARCHAR(180) NOT NULL UNIQUE,
  password_hash VARCHAR(255) NOT NULL,
  centre_interet VARCHAR(255),
  newsletter_subscribed BOOLEAN NOT NULL DEFAULT FALSE,
  enabled BOOLEAN NOT NULL DEFAULT TRUE,
  created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS user_roles (
  user_id BIGINT NOT NULL,
  role VARCHAR(32) NOT NULL,
  PRIMARY KEY (user_id, role),
  CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE INDEX IF NOT EXISTS idx_user_roles_user ON user_roles(user_id);

CREATE TABLE IF NOT EXISTS rubriques (
  id BIGSERIAL PRIMARY KEY,
  nom VARCHAR(120) NOT NULL UNIQUE,
  slug VARCHAR(120) UNIQUE,
  created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS articles (
  id BIGSERIAL PRIMARY KEY,
  titre VARCHAR(200) NOT NULL,
  contenu TEXT NOT NULL,
  statut VARCHAR(32) NOT NULL,
  video_url VARCHAR(500),
  youtube_video_id VARCHAR(32),
  date_publication TIMESTAMP WITH TIME ZONE,
  rejection_reason VARCHAR(500),
  redacteur_id BIGINT NOT NULL,
  rubrique_id BIGINT NOT NULL,
  created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_articles_redacteur FOREIGN KEY (redacteur_id) REFERENCES users(id),
  CONSTRAINT fk_articles_rubrique FOREIGN KEY (rubrique_id) REFERENCES rubriques(id)
);

CREATE INDEX IF NOT EXISTS idx_articles_status ON articles(statut);
CREATE INDEX IF NOT EXISTS idx_articles_rubrique ON articles(rubrique_id);
CREATE INDEX IF NOT EXISTS idx_articles_publication_date ON articles(date_publication);

CREATE TABLE IF NOT EXISTS comments (
  id BIGSERIAL PRIMARY KEY,
  contenu TEXT NOT NULL,
  date_creation TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
  statut VARCHAR(32) NOT NULL,
  rejection_reason VARCHAR(500),
  moderated_at TIMESTAMP WITH TIME ZONE,
  user_id BIGINT NOT NULL,
  article_id BIGINT NOT NULL,
  CONSTRAINT fk_comments_user FOREIGN KEY (user_id) REFERENCES users(id),
  CONSTRAINT fk_comments_article FOREIGN KEY (article_id) REFERENCES articles(id)
);

CREATE INDEX IF NOT EXISTS idx_comments_status ON comments(statut);
CREATE INDEX IF NOT EXISTS idx_comments_article ON comments(article_id);
CREATE INDEX IF NOT EXISTS idx_comments_user ON comments(user_id);

CREATE TABLE IF NOT EXISTS likes (
  id BIGSERIAL PRIMARY KEY,
  date_like TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
  user_id BIGINT NOT NULL,
  article_id BIGINT NOT NULL,
  CONSTRAINT fk_likes_user FOREIGN KEY (user_id) REFERENCES users(id),
  CONSTRAINT fk_likes_article FOREIGN KEY (article_id) REFERENCES articles(id),
  CONSTRAINT uk_likes_user_article UNIQUE (user_id, article_id)
);

CREATE INDEX IF NOT EXISTS idx_likes_article ON likes(article_id);
CREATE INDEX IF NOT EXISTS idx_likes_user ON likes(user_id);

CREATE TABLE IF NOT EXISTS newsletter_subscriptions (
  id BIGSERIAL PRIMARY KEY,
  pseudonyme VARCHAR(80),
  email VARCHAR(180) NOT NULL UNIQUE,
  active BOOLEAN NOT NULL DEFAULT TRUE,
  subscribed_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
  unsubscribed_at TIMESTAMP WITH TIME ZONE
);

CREATE INDEX IF NOT EXISTS idx_newsletter_active ON newsletter_subscriptions(active);

CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_pseudonyme ON users(pseudonyme);
