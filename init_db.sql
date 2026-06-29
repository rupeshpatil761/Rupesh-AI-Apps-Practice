-- PostgreSQL initial setup script
-- This script is run once when a fresh Postgres data directory is initialized

SELECT 'Database initialized and ready for Spring JPA' AS status;

CREATE EXTENSION IF NOT EXISTS vector;

CREATE TABLE IF NOT EXISTS vector_store(
    id TEXT PRIMARY KEY,
    content TEXT,
    metadata JSONB,
    embedding VECTOR(768)  -- nomic-embed-text produces 768-dimensional vectors
);

-- Create HNSW index for fast search
CREATE INDEX IF NOT EXISTS vector_store_embedding_idx ON vector_store USING HNSW (embedding vector_cosine_ops);
