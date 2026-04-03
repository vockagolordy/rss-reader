package org.example.rssreader.repository;

import org.example.rssreader.model.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class ResourceRepositoryImpl implements ResourceRepository {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ResourceRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Resource> resourceRowMapper = (rs, rowNum) -> {
        Resource resource = new Resource();
        resource.setId(rs.getLong("id"));
        resource.setTitle(rs.getString("title"));
        resource.setLink(rs.getString("link"));
        resource.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            resource.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        return resource;
    };

    @Override
    public Resource save(Resource resource) {
        if (resource.getId() == 0) {
            return insert(resource);
        } else {
            return update(resource);
        }
    }

    @Override
    public Resource insert(Resource resource) {
        String sql = "INSERT INTO resources (title, link, created_at, updated_at) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, resource.getTitle());
            ps.setString(2, resource.getLink());
            ps.setTimestamp(3, Timestamp.valueOf(resource.getCreatedAt()));
            ps.setTimestamp(4, Timestamp.valueOf(resource.getUpdatedAt()));
            return ps;
        }, keyHolder);

        if (!keyHolder.getKeyList().isEmpty()) {
            Map<String, Object> keys = keyHolder.getKeyList().get(0);
            if (keys.containsKey("id")) {
                resource.setId(((Number) keys.get("id")).longValue());
            }
        }
        return resource;
    }

    @Override
    public Resource update(Resource resource) {
        String sql = "UPDATE resources SET title = ?, link = ?, updated_at = ? WHERE id = ?";
        jdbcTemplate.update(sql,
                resource.getTitle(),
                resource.getLink(),
                Timestamp.valueOf(resource.getUpdatedAt()),
                resource.getId());
        return resource;
    }

    @Override
    public Optional<Resource> findByLink(String link) {
        String sql = "SELECT * FROM resources WHERE link = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, resourceRowMapper, link));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Resource> findById(long id) {
        String sql = "SELECT * FROM resources WHERE id = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, resourceRowMapper, id));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Resource> findByUserId(long userId) {
        String sql = """
            SELECT r.* FROM resources r 
            INNER JOIN user_resources ur ON r.id = ur.resource_id 
            WHERE ur.user_id = ? 
            ORDER BY r.created_at DESC
        """;
        return jdbcTemplate.query(sql, resourceRowMapper, userId);
    }

    @Override
    public List<Resource> findAll() {
        String sql = "SELECT * FROM resources ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, resourceRowMapper);
    }

    @Override
    public void updateLastCheck(long id, LocalDateTime lastCheck) {
        String sql = "UPDATE resources SET updated_at = ? WHERE id = ?";
        jdbcTemplate.update(sql, Timestamp.valueOf(lastCheck), id);
    }

    @Override
    public void deleteById(long id) {
        String deleteUserResourcesSql = "DELETE FROM user_resources WHERE resource_id = ?";
        jdbcTemplate.update(deleteUserResourcesSql, id);

        String deletePostsSql = "DELETE FROM posts WHERE resource_id = ?";
        jdbcTemplate.update(deletePostsSql, id);

        String deleteResourceSql = "DELETE FROM resources WHERE id = ?";
        jdbcTemplate.update(deleteResourceSql, id);
    }
}