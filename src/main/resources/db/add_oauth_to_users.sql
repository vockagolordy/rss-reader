-- Adds OAuth2 support fields to existing local users table.

ALTER TABLE users
    ADD COLUMN IF NOT EXISTS provider VARCHAR(30) NOT NULL DEFAULT 'local',
    ADD COLUMN IF NOT EXISTS provider_id VARCHAR(255);

ALTER TABLE users
    ALTER COLUMN password_hash DROP NOT NULL;

CREATE UNIQUE INDEX IF NOT EXISTS ux_users_provider_provider_id
    ON users(provider, provider_id)
    WHERE provider_id IS NOT NULL;