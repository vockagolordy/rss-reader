package org.example.rssreader.controller;

import org.example.rssreader.dto.FeedDto;
import org.example.rssreader.model.Post;
import org.example.rssreader.model.Resource;
import org.example.rssreader.service.PostService;
import org.example.rssreader.service.ResourceService;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;
    private final ResourceService resourceService;

    public PostController(PostService postService,
                          ResourceService resourceService) {
        this.postService = postService;
        this.resourceService = resourceService;
    }

    @GetMapping
    public String showFeed(@AuthenticationPrincipal(expression = "id") long userId,
                           Model model) {
        int newPostsCount = postService.refreshUserFeed(userId);

        if (newPostsCount > 0) {
            model.addAttribute("info", String.format("Found %d new posts!", newPostsCount));
        }

        return "feed";
    }

    @ResponseBody
    @GetMapping("/feed")
    public FeedDto getFeedPosts(@RequestParam(name = "page", defaultValue = "0") int page,
                                @RequestParam(name = "size", defaultValue = "10") int size,
                                @AuthenticationPrincipal(expression = "id") long userId) {
        Page<Post> postPage = postService.getUserFeedPage(userId, page, size);

        return FeedDto.from(postPage);
    }

    @GetMapping("/{id}")
    public String showPost(@PathVariable("id") long id,
                           @AuthenticationPrincipal(expression = "id") long userId,
                           Model model) {
        Optional<Post> postOpt = postService.getPostById(id);

        if (postOpt.isEmpty()) {
            return "error/404";
        }

        Post post = postOpt.get();

        List<Resource> userResources = resourceService.getUserResources(userId);
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
