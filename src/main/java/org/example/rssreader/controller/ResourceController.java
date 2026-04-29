package org.example.rssreader.controller;

import org.example.rssreader.dto.ResourceDto;
import org.example.rssreader.model.Resource;
import org.example.rssreader.model.User;
import org.example.rssreader.service.ResourceService;
import org.example.rssreader.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/resources")
public class ResourceController {

    private final ResourceService resourceService;
    private final UserService userService;

    @Autowired
    public ResourceController(ResourceService resourceService, UserService userService) {
        this.resourceService = resourceService;
        this.userService = userService;
    }

    private User getCurrentUser(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            throw new RuntimeException("User not logged in");
        }
        return user;
    }

    @GetMapping
    public String listResources(HttpSession session, Model model) {
        User user = getCurrentUser(session);
        List<Resource> resources = resourceService.getUserResources(user.getId());
        model.addAttribute("resources", resources);
        return "resources";
    }

    @GetMapping("/add")
    public String showAddResourceForm(Model model) {
        model.addAttribute("resource", new ResourceDto());
        return "add-resource";
    }

    @PostMapping
    public String addResource(@Valid @ModelAttribute("resource") ResourceDto resourceDto,
                              BindingResult result,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "add-resource";
        }

        User user = getCurrentUser(session);

        Resource resource = resourceService.addResource(resourceDto);

        userService.addResourceToUser(user.getId(), resource.getId());

        redirectAttributes.addFlashAttribute("success", "Resource added successfully!");
        return "redirect:/resources";
    }

    @PostMapping("/{id}/delete")
    public String deleteResource(@PathVariable("id") long id,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        User user = getCurrentUser(session);

        userService.removeResourceFromUser(user.getId(), id);

        redirectAttributes.addFlashAttribute("success", "Resource removed successfully!");
        return "redirect:/resources";
    }

    @PostMapping("/refresh")
    public String refreshResources(HttpSession session, RedirectAttributes redirectAttributes) {
        User user = getCurrentUser(session);
        List<Resource> resources = resourceService.getUserResources(user.getId());

        int totalNewPosts = 0;
        for (Resource resource : resources) {
            totalNewPosts += resourceService.updateResourcePosts(resource.getId());
        }

        redirectAttributes.addFlashAttribute("success",
                String.format("Refreshed successfully! Found %d new posts.", totalNewPosts));
        return "redirect:/resources";
    }
}