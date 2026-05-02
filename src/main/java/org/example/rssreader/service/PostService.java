package org.example.rssreader.service;

import org.example.rssreader.model.Post;
import org.example.rssreader.model.Resource;
import org.example.rssreader.repository.PostRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final ResourceService resourceService;

    public PostService(PostRepository postRepository, ResourceService resourceService) {
        this.postRepository = postRepository;
        this.resourceService = resourceService;
    }

    @Transactional(readOnly = true)
    public Page<Post> getUserFeedPage(long userId, int page, int size) {
        PageRequest pageRequest = PageRequest.of(
                Math.max(page, 0),
                Math.max(size, 1),
                Sort.by(
                        Sort.Order.desc("publishedAt"),
                        Sort.Order.desc("addedAt")
                )
        );

        return postRepository.findDistinctByResourceUsersId(userId, pageRequest);
    }

    @Transactional(readOnly = true)
    public List<Post> getUserFeed(long userId, int page, int size) {
        return getUserFeedPage(userId, page, size).getContent();
    }

    @Transactional(readOnly = true)
    public int getUserFeedCount(long userId) {
        return (int) getUserFeedPage(userId, 0, 1).getTotalElements();
    }

    @Transactional(readOnly = true)
    public Optional<Post> getPostById(long id) {
        return postRepository.findWithResourceById(id);
    }

    @Transactional
    public int refreshUserFeed(long userId) {
        List<Resource> userResources = resourceService.getUserResources(userId);
        int totalNewPosts = 0;

        for (Resource resource : userResources) {
            totalNewPosts += resourceService.updateResourcePosts(resource.getId());
        }

        return totalNewPosts;
    }
}