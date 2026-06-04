CREATE TABLE IF NOT EXISTS oauth_accounts (
      id               BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
      user_id          BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
      provider         VARCHAR(50)  NOT NULL,
      provider_user_id VARCHAR(255) NOT NULL,
      UNIQUE (provider, provider_user_id)
);