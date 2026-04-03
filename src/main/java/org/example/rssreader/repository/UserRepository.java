package org.example.rssreader.repository;


import org.example.rssreader.model.User;

import java.util.Optional;

public interface UserRepository {

    User save(User user);

    User insert(User user);

    User update(User user);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findById(long id);

    void addResourceToUser(long userId, long resourceId);

    void removeResourceFromUser(long userId, long resourceId);
}