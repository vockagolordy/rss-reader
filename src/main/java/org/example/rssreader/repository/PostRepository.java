package org.example.rssreader.repository;

import org.example.rssreader.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    boolean existsByLink(String link);

    @EntityGraph(attributePaths = "resource")
    Page<Post> findDistinctByResourceUsersId(long userId, Pageable pageable);

    @EntityGraph(attributePaths = "resource")
    Optional<Post> findWithResourceById(long id);
}