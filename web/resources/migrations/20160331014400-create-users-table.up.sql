CREATE TABLE users (
  id        SERIAL PRIMARY KEY,
  name      VARCHAR   NOT NULL,
  email     VARCHAR   NOT NULL,
  joined_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
--;;
CREATE UNIQUE INDEX users_uk1 ON users (email);
--;;
INSERT INTO users (name, email) VALUES ('System', 'system@soundeffector.com');
