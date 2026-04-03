package org.example.rssreader.service;

import org.example.rssreader.model.Post;
import org.example.rssreader.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final ResourceService resourceService;

    @Autowired
    public PostService(PostRepository postRepository, ResourceService resourceService) {
        this.postRepository = postRepository;
        this.resourceService = resourceService;
    }

    @Transactional(readOnly = true)
    public List<Post> getUserFeed(long userId, int page, int size) {
        int offset = page * size;
        return postRepository.findByUserId(userId, size, offset);
    }

    @Transactional(readOnly = true)
    public int getUserFeedCount(long userId) {
        return postRepository.countByUserId(userId);
    }

    @Transactional(readOnly = true)
    public Optional<Post> getPostById(long id) {
        return postRepository.findById(id);
    }

    @Transactional
    public int refreshUserFeed(long userId) {
        List<org.example.rssreader.model.Resource> userResources = resourceService.getUserResources(userId);
        int totalNewPosts = 0;

        for (org.example.rssreader.model.Resource resource : userResources) {
            totalNewPosts += resourceService.updateResourcePosts(resource.getId());
        }

        return totalNewPosts;
    }
}