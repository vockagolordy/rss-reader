-- users
CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       uuid VARCHAR(36) NOT NULL UNIQUE,
                       username VARCHAR(50) NOT NULL UNIQUE,
                       email VARCHAR(100) NOT NULL UNIQUE,
                       password_hash VARCHAR(255) NOT NULL,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- resources
CREATE TABLE resources (
                           id BIGSERIAL PRIMARY KEY,
                           title VARCHAR(255) NOT NULL,
                           link VARCHAR(500) NOT NULL UNIQUE,
                           created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                           updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- user_resources
CREATE TABLE user_resources (
                                user_id BIGINT NOT NULL,
                                resource_id BIGINT NOT NULL,
                                added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                PRIMARY KEY (user_id, resource_id),
                                FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                                FOREIGN KEY (resource_id) REFERENCES resources(id) ON DELETE CASCADE
);

-- posts
CREATE TABLE posts (
                       id BIGSERIAL PRIMARY KEY,
                       title VARCHAR(500),
                       category VARCHAR(100),
                       description TEXT,
                       author VARCHAR(255),
                       link VARCHAR(500) NOT NULL UNIQUE,
                       resource_id BIGINT NOT NULL,
                       published_at TIMESTAMP,
                       added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       FOREIGN KEY (resource_id) REFERENCES resources(id) ON DELETE CASCADE
);

-- индексы для оптимизации
CREATE INDEX idx_posts_resource_id ON posts(resource_id);
CREATE INDEX idx_posts_published_at ON posts(published_at);
CREATE INDEX idx_user_resources_user_id ON user_resources(user_id);