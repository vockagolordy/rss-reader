package org.example.rssreader.service;

import org.example.rssreader.dto.ResourceDto;
import org.example.rssreader.model.Post;
import org.example.rssreader.model.Resource;
import org.example.rssreader.repository.PostRepository;
import org.example.rssreader.repository.ResourceRepository;
import org.example.rssreader.util.RSSParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ResourceService {

    private static final Logger log = LoggerFactory.getLogger(ResourceService.class);

    private final ResourceRepository resourceRepository;
    private final PostRepository postRepository;
    private final RSSParser rssParser;
    private final UserService userService;

    public ResourceService(ResourceRepository resourceRepository,
                           PostRepository postRepository,
                           RSSParser rssParser,
                           UserService userService) {
        this.resourceRepository = resourceRepository;
        this.postRepository = postRepository;
        this.rssParser = rssParser;
        this.userService = userService;
    }

    @Transactional
    public Resource addResource(ResourceDto resourceDto) {
        return resourceRepository.findByLink(resourceDto.getLink())
                .orElseGet(() -> createResource(
                        resourceDto.getTitle(),
                        resourceDto.getLink()
                ));
    }

    @Transactional
    public Resource createResourceForUser(long userId, String title, String link) {
        Resource resource = resourceRepository.findByLink(link)
                .orElseGet(() -> createResource(title, link));

        userService.addResourceToUser(userId, resource.getId());

        updateResourcePosts(resource.getId());

        return resource;
    }

    @Transactional
    public Optional<Resource> updateUserResource(long userId, long resourceId, String title, String link) {
        Optional<Resource> resourceOpt = resourceRepository.findById(resourceId);

        if (resourceOpt.isEmpty()) {
            return Optional.empty();
        }

        Resource resource = resourceOpt.get();

        if (!userHasAccessToResource(resource, userId)) {
            return Optional.empty();
        }

        resource.setTitle(title);
        resource.setLink(link);
        resource.setUpdatedAt(LocalDateTime.now());

        Resource savedResource = resourceRepository.save(resource);

        updateResourcePosts(savedResource.getId());

        return Optional.of(savedResource);
    }

    @Transactional
    public boolean deleteUserResource(long userId, long resourceId) {
        Optional<Resource> resourceOpt = resourceRepository.findById(resourceId);

        if (resourceOpt.isEmpty()) {
            return false;
        }

        Resource resource = resourceOpt.get();

        if (!userHasAccessToResource(resource, userId)) {
            return false;
        }

        userService.removeResourceFromUser(userId, resourceId);

        return true;
    }

    @Transactional
    public int updateResourcePosts(long resourceId) {
        Optional<Resource> resourceOpt = resourceRepository.findById(resourceId);

        if (resourceOpt.isEmpty()) {
            return 0;
        }

        Resource resource = resourceOpt.get();

        try {
            List<Post> parsedPosts = rssParser.parseFeed(resource);
            int newPostsCount = saveNewPosts(resource, parsedPosts);

            resource.setUpdatedAt(LocalDateTime.now());
            resourceRepository.save(resource);

            return newPostsCount;
        } catch (Exception e) {
            log.warn("Error parsing RSS feed: {}", resource.getLink(), e);
            return 0;
        }
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

    @Transactional(readOnly = true)
    public List<Resource> getUserResources(long userId) {
        return resourceRepository.findByUsersIdOrderByCreatedAtDesc(userId);
    }

    private Resource createResource(String title, String link) {
        Resource resource = new Resource();

        resource.setTitle(title);
        resource.setLink(link);
        resource.setCreatedAt(LocalDateTime.now());
        resource.setUpdatedAt(LocalDateTime.now());

        return resourceRepository.save(resource);
    }

    private int saveNewPosts(Resource resource, List<Post> posts) {
        int newPostsCount = 0;

        for (Post post : posts) {
            if (!postRepository.existsByLink(post.getLink())) {
                post.setResource(resource);
                postRepository.save(post);
                newPostsCount++;
            }
        }

        return newPostsCount;
    }

    private boolean userHasAccessToResource(Resource resource, long userId) {
        return resource.getUsers()
                .stream()
                .anyMatch(user -> user.getId() == userId);
    }
}