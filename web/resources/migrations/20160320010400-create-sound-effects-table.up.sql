CREATE TABLE sound_effects (
  id          SERIAL PRIMARY KEY,
  title       VARCHAR   NOT NULL,
  url         VARCHAR   NOT NULL,
  uploader_id INTEGER   NOT NULL,
  created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
--;;
CREATE INDEX sound_effects_idx1 ON sound_effects (uploader_id, id);
