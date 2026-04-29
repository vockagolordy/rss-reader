package org.example.rssreader.service;

import org.example.rssreader.dto.ResourceDto;
import org.example.rssreader.model.Post;
import org.example.rssreader.model.Resource;
import org.example.rssreader.repository.PostRepository;
import org.example.rssreader.repository.ResourceRepository;
import org.example.rssreader.util.RSSParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ResourceService {

    private final ResourceRepository resourceRepository;
    private final PostRepository postRepository;
    private final RSSParser rssParser;

    @Autowired
    public ResourceService(ResourceRepository resourceRepository,
                           PostRepository postRepository,
                           RSSParser rssParser) {
        this.resourceRepository = resourceRepository;
        this.postRepository = postRepository;
        this.rssParser = rssParser;
    }

    @Transactional
    public Resource addResource(ResourceDto resourceDto) {
        Optional<Resource> existingResource = resourceRepository.findByLink(resourceDto.getLink());

        if (existingResource.isPresent()) {
            return existingResource.get();
        }

        Resource resource = new Resource();

        resource.setTitle(resourceDto.getTitle());
        resource.setLink(resourceDto.getLink());
        resource.setCreatedAt(LocalDateTime.now());
        resource.setUpdatedAt(LocalDateTime.now());

        return resourceRepository.save(resource);
    }

    @Transactional(readOnly = true)
    public List<Resource> getUserResources(long userId) {
        return resourceRepository.findByUsersIdOrderByCreatedAtDesc(userId);
    }

    @Transactional
    public int updateResourcePosts(long resourceId) {
        Optional<Resource> resourceOpt = resourceRepository.findById(resourceId);

        if (resourceOpt.isEmpty()) {
            return 0;
        }

        Resource resource = resourceOpt.get();
        int newPostsCount = 0;

        try {
            List<Post> newPosts = rssParser.parseFeed(resource);

            for (Post post : newPosts) {
                if (!postRepository.existsByLink(post.getLink())) {
                    post.setResource(resource);
                    postRepository.save(post);
                    newPostsCount++;
                }
            }

            resource.setUpdatedAt(LocalDateTime.now());
            resourceRepository.save(resource);

        } catch (Exception e) {
            System.err.println("Error parsing RSS feed: " + resource.getLink());
            e.printStackTrace();
        }

        return newPostsCount;
    }

    @Transactional
    public int updateAllResources() {
        List<Resource> allResources = resourceRepository.findAll();
        int totalNewPosts = 0;

        for (Resource resource : allResources) {
            totalNewPosts += updateResourcePosts(resource.getId());
        }

        return totalNewPosts;
    }

    @Transactional(readOnly = true)
    public Optional<Resource> findById(long id) {
        return resourceRepository.findById(id);
    }
}