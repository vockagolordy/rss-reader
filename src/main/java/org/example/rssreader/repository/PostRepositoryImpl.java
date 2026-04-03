package org.example.rssreader.repository;

import org.example.rssreader.model.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class PostRepositoryImpl implements PostRepository {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public PostRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Post> postRowMapper = (rs, rowNum) -> {
        Post post = new Post();
        post.setId(rs.getLong("id"));
        post.setTitle(rs.getString("title"));
        post.setCategory(rs.getString("category"));
        post.setDescription(rs.getString("description"));
        post.setAuthor(rs.getString("author"));
        post.setLink(rs.getString("link"));
        post.setResourceId(rs.getLong("resource_id"));
        Timestamp publishedAt = rs.getTimestamp("published_at");
        if (publishedAt != null) {
            post.setPublishedAt(publishedAt.toLocalDateTime());
        }
        post.setAddedAt(rs.getTimestamp("added_at").toLocalDateTime());
        return post;
    };

    @Override
    public Post save(Post post) {
        if (post.getId() == 0) {
            return insert(post);
        } else {
            return update(post);
        }
    }

    @Override
    public Post insert(Post post) {
        String sql = """
            INSERT INTO posts (title, category, description, author, link, resource_id, published_at, added_at) 
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """;
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, post.getTitle());
            ps.setString(2, post.getCategory());
            ps.setString(3, post.getDescription());
            ps.setString(4, post.getAuthor());
            ps.setString(5, post.getLink());
            ps.setLong(6, post.getResourceId());
            ps.setTimestamp(7, post.getPublishedAt() != null ? Timestamp.valueOf(post.getPublishedAt()) : null);
            ps.setTimestamp(8, Timestamp.valueOf(post.getAddedAt()));
            return ps;
        }, keyHolder);

        if (!keyHolder.getKeyList().isEmpty()) {
            Map<String, Object> keys = keyHolder.getKeyList().get(0);
            if (keys.containsKey("id")) {
                post.setId(((Number) keys.get("id")).longValue());
            }
        }
        return post;
    }

    @Override
    public Post update(Post post) {
        String sql = """
            UPDATE posts SET title = ?, category = ?, description = ?, author = ?, 
            link = ?, resource_id = ?, published_at = ? WHERE id = ?
        """;
        jdbcTemplate.update(sql,
                post.getTitle(),
                post.getCategory(),
                post.getDescription(),
                post.getAuthor(),
                post.getLink(),
                post.getResourceId(),
                post.getPublishedAt() != null ? Timestamp.valueOf(post.getPublishedAt()) : null,
                post.getId());
        return post;
    }

    @Override
    public List<Post> findByResourceId(long resourceId, int limit, int offset) {
        String sql = """
            SELECT * FROM posts 
            WHERE resource_id = ? 
            ORDER BY published_at DESC, added_at DESC 
            LIMIT ? OFFSET ?
        """;
        return jdbcTemplate.query(sql, postRowMapper, resourceId, limit, offset);
    }

    @Override
    public List<Post> findByUserId(long userId, int limit, int offset) {
        String sql = """
            SELECT p.* FROM posts p 
            INNER JOIN resources r ON p.resource_id = r.id 
            INNER JOIN user_resources ur ON r.id = ur.resource_id 
            WHERE ur.user_id = ? 
            ORDER BY p.published_at DESC, p.added_at DESC 
            LIMIT ? OFFSET ?
        """;
        return jdbcTemplate.query(sql, postRowMapper, userId, limit, offset);
    }

    @Override
    public boolean existsByLink(String link) {
        String sql = "SELECT COUNT(*) FROM posts WHERE link = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, link);
        return count != null && count > 0;
    }

    @Override
    public int countByUserId(long userId) {
        String sql = """
            SELECT COUNT(*) FROM posts p 
            INNER JOIN resources r ON p.resource_id = r.id 
            INNER JOIN user_resources ur ON r.id = ur.resource_id 
            WHERE ur.user_id = ?
        """;
        return jdbcTemplate.queryForObject(sql, Integer.class, userId);
    }

    @Override
    public Optional<Post> findById(long id) {
        String sql = "SELECT * FROM posts WHERE id = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, postRowMapper, id));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

}