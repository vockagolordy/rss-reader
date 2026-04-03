package org.example.rssreader.repository;

import org.example.rssreader.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Map;
import java.util.Optional;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<User> userRowMapper = (rs, rowNum) -> {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setUuid(rs.getString("uuid"));
        user.setUsername(rs.getString("username"));
        user.setEmail(rs.getString("email"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return user;
    };

    @Override
    public User save(User user) {
        if (user.getId() == 0) {
            return insert(user);
        } else {
            return update(user);
        }
    }

    @Override
    public User insert(User user) {
        String sql = "INSERT INTO users (uuid, username, email, password_hash, created_at) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getUuid());
            ps.setString(2, user.getUsername());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getPasswordHash());
            ps.setTimestamp(5, Timestamp.valueOf(user.getCreatedAt()));
            return ps;
        }, keyHolder);

        if (!keyHolder.getKeyList().isEmpty()) {
            Map<String, Object> keys = keyHolder.getKeyList().get(0);
            if (keys.containsKey("id")) {
                user.setId(((Number) keys.get("id")).longValue());
            }
        }

        return user;
    }

    @Override
    public User update(User user) {
        String sql = "UPDATE users SET username = ?, email = ?, password_hash = ? WHERE id = ?";
        jdbcTemplate.update(sql, user.getUsername(), user.getEmail(), user.getPasswordHash(), user.getId());
        return user;
    }

    @Override
    public Optional<User> findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, userRowMapper, username));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, userRowMapper, email));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<User> findById(long id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, userRowMapper, id));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public void addResourceToUser(long userId, long resourceId) {
        String sql = "INSERT INTO user_resources (user_id, resource_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, userId, resourceId);
    }

    @Override
    public void removeResourceFromUser(long userId, long resourceId) {
        String sql = "DELETE FROM user_resources WHERE user_id = ? AND resource_id = ?";
        jdbcTemplate.update(sql, userId, resourceId);
    }
}