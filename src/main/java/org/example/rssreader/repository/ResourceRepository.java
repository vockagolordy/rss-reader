package org.example.rssreader.repository;


import org.example.rssreader.model.Resource;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ResourceRepository {

    Resource save(Resource resource);

    Resource insert(Resource resource);

    Resource update(Resource resource);

    Optional<Resource> findByLink(String link);

    Optional<Resource> findById(long id);

    List<Resource> findByUserId(long userId);

    List<Resource> findAll();

    void updateLastCheck(long id, LocalDateTime lastCheck);

    void deleteById(long id);
}