package org.example.rssreader.repository;

import org.example.rssreader.model.Resource;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ResourceRepository extends JpaRepository<Resource, Long> {

    List<Resource> findByUsersIdOrderByCreatedAtDesc(long userId);

    Optional<Resource> findByLink(String link);

    boolean existsByLink(String link);
}