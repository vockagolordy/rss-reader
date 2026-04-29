package org.example.rssreader.service;

import org.example.rssreader.model.Post;
import org.example.rssreader.model.Resource;
import org.example.rssreader.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    public PostService(PostRepository postRepository, ResourceService resourceService) {
        this.postRepository = postRepository;
        this.resourceService = resourceService;
    }

    @Transactional(readOnly = true)
    public List<Post> getUserFeed(long userId, int page, int size) {
        PageRequest pageRequest = PageRequest.of(
                page,
                size,
                Sort.by(
                        Sort.Order.desc("publishedAt"),
                        Sort.Order.desc("addedAt")
                )
        );

        Page<Post> posts = postRepository.findDistinctByResourceUsersId(userId, pageRequest);

        return posts.getContent();
    }

    @Transactional(readOnly = true)
    public int getUserFeedCount(long userId) {
        PageRequest pageRequest = PageRequest.of(0, 1);
        Page<Post> posts = postRepository.findDistinctByResourceUsersId(userId, pageRequest);

        return (int) posts.getTotalElements();
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