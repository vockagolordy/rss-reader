package org.example.rssreader.controller;

import org.example.rssreader.model.Post;
import org.example.rssreader.model.Resource;
import org.example.rssreader.model.User;
import org.example.rssreader.service.PostService;
import org.example.rssreader.service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;
    private final ResourceService resourceService;

    @Autowired
    public PostController(PostService postService, ResourceService resourceService) {
        this.postService = postService;
        this.resourceService = resourceService;
    }

    private User getCurrentUser(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            throw new RuntimeException("User not logged in");
        }
        return user;
    }

    @GetMapping
    public String showFeed(@RequestParam(name = "page", defaultValue = "0") int page,
                           @RequestParam(name = "size", defaultValue = "10") int size,
                           HttpSession session,
                           Model model,
                           RedirectAttributes redirectAttributes) {
        User user = getCurrentUser(session);

        int newPostsCount = postService.refreshUserFeed(user.getId());
        if (newPostsCount > 0) {
            redirectAttributes.addFlashAttribute("info",
                    String.format("Found %d new posts!", newPostsCount));
        }

        List<Post> posts = postService.getUserFeed(user.getId(), page, size);
        int totalPosts = postService.getUserFeedCount(user.getId());
        int totalPages = (int) Math.ceil((double) totalPosts / size);

        model.addAttribute("posts", posts);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("size", size);
        model.addAttribute("totalPosts", totalPosts);

        return "feed";
    }

    @GetMapping("/{id}")
    public String showPost(@PathVariable("id") long id,
                           HttpSession session,
                           Model model) {
        User user = getCurrentUser(session);

        Optional<Post> postOpt = postService.getPostById(id);

        if (postOpt.isEmpty()) {
            return "error/404";
        }

        Post post = postOpt.get();

        List<Resource> userResources = resourceService.getUserResources(user.getId());
        boolean hasAccess = userResources.stream()
                .anyMatch(r -> r.getId() == post.getResourceId());

        if (!hasAccess) {
            return "error/403";
        }

        Optional<Resource> resourceOpt = resourceService.findById(post.getResourceId());
        resourceOpt.ifPresent(resource -> {
            model.addAttribute("resource", resource);
        });

        model.addAttribute("post", post);
        return "post";
    }
}