package org.example.rssreader.repository;

import org.example.rssreader.model.Post;

import java.util.List;
import java.util.Optional;


public interface PostRepository {

    Post save(Post post);
    Post insert(Post post);
    Post update(Post post);
    List<Post> findByResourceId(long resourceId, int limit, int offset);
    List<Post> findByUserId(long userId, int limit, int offset);
    int countByUserId(long userId);
    boolean existsByLink(String link);
    Optional<Post> findById(long id);
}