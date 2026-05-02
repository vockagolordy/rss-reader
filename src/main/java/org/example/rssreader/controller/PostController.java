package org.example.rssreader.controller;

import org.example.rssreader.dto.PostDto;
import org.example.rssreader.model.Post;
import org.example.rssreader.model.Resource;
import org.example.rssreader.model.User;
import org.example.rssreader.service.CurrentUserService;
import org.example.rssreader.service.PostService;
import org.example.rssreader.service.ResourceService;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;
    private final ResourceService resourceService;
    private final CurrentUserService currentUserService;

    public PostController(PostService postService,
                          ResourceService resourceService,
                          CurrentUserService currentUserService) {
        this.postService = postService;
        this.resourceService = resourceService;
        this.currentUserService = currentUserService;
    }

    @GetMapping
    public String showFeed(Authentication authentication,
                           Model model) {
        User user = currentUserService.getCurrentUser(authentication);

        int newPostsCount = postService.refreshUserFeed(user.getId());

        if (newPostsCount > 0) {
            model.addAttribute("info", String.format("Found %d new posts!", newPostsCount));
        }

        return "feed";
    }

    @ResponseBody
    @GetMapping("/feed")
    public Map<String, Object> getFeedPosts(@RequestParam(name = "page", defaultValue = "0") int page,
                                            @RequestParam(name = "size", defaultValue = "10") int size,
                                            Authentication authentication) {
        User user = currentUserService.getCurrentUser(authentication);

        Page<Post> postPage = postService.getUserFeedPage(user.getId(), page, size);

        List<PostDto> posts = postPage.getContent()
                .stream()
                .map(PostDto::from)
                .toList();

        return Map.of(
                "posts", posts,
                "page", postPage.getNumber(),
                "size", postPage.getSize(),
                "totalElements", postPage.getTotalElements(),
                "totalPages", postPage.getTotalPages(),
                "hasNext", postPage.hasNext()
        );
    }

    @GetMapping("/{id}")
    public String showPost(@PathVariable("id") long id,
                           Authentication authentication,
                           Model model) {
        User user = currentUserService.getCurrentUser(authentication);

        Optional<Post> postOpt = postService.getPostById(id);

        if (postOpt.isEmpty()) {
            return "error/404";
        }

        Post post = postOpt.get();

        List<Resource> userResources = resourceService.getUserResources(user.getId());
        boolean hasAccess = userResources.stream()
                .anyMatch(resource -> resource.getId() == post.getResourceId());

        if (!hasAccess) {
            return "error/403";
        }

        Optional<Resource> resourceOpt = resourceService.findById(post.getResourceId());
        resourceOpt.ifPresent(resource -> model.addAttribute("resource", resource));

        model.addAttribute("post", post);
        return "post";
    }
}