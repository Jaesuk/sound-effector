CREATE TABLE user_providers (
  id               SERIAL PRIMARY KEY,
  user_id          INTEGER   NOT NULL,
  provider_id      INTEGER   NOT NULL,
  provider_user_id VARCHAR   NOT NULL,
  connected_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
--;;
-- For now, I will not allow that the user connects multiple account for a provider.
CREATE UNIQUE INDEX user_providers_uk1 ON user_providers (user_id, provider_id);
--;;
CREATE INDEX user_providers_idx1 ON user_providers (provider_id, provider_user_id, user_id);
