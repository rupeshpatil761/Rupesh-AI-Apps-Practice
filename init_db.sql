-- PostgreSQL initial setup script
-- Tables will be created automatically by Spring JPA (ddl-auto: update)
-- This script is run on container startup

-- Verify database exists (optional)
SELECT 'Database initialized and ready for Spring JPA' as status;

CREATE EXTENSION IF NOT EXISTS vector;

CREATE TABLE IF NOT EXISTS product_embeddings (
    --id BIGSERIAL PRIMARY KEY,
    id TEXT PRIMARY KEY, -- it should be TEXT (not UUID)
    --product_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    embedding vector(1536) NOT NULL
);