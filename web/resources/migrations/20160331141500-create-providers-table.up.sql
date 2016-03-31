CREATE TABLE providers (
  id       SERIAL PRIMARY KEY,
  name     VARCHAR NOT NULL,
  site_url VARCHAR NOT NULL
);
--;;
CREATE UNIQUE INDEX providers_uk1 ON providers (name);
--;;
INSERT INTO providers (name, site_url) VALUES ('Facebook', 'https://www.facebook.com/');
