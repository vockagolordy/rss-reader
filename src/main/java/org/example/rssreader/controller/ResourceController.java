package org.example.rssreader.controller;

import org.example.rssreader.dto.ResourceDto;
import org.example.rssreader.model.Resource;
import org.example.rssreader.model.User;
import org.example.rssreader.service.CurrentUserService;
import org.example.rssreader.service.ResourceService;
import org.example.rssreader.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/resources")
public class ResourceController {

    private final ResourceService resourceService;
    private final UserService userService;
    private final CurrentUserService currentUserService;

    @Autowired
    public ResourceController(ResourceService resourceService,
                              UserService userService,
                              CurrentUserService currentUserService) {
        this.resourceService = resourceService;
        this.userService = userService;
        this.currentUserService = currentUserService;
    }

    @GetMapping
    public String listResources(Authentication authentication, Model model) {
        User user = currentUserService.getCurrentUser(authentication);

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
                              Authentication authentication,
                              RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "add-resource";
        }

        User user = currentUserService.getCurrentUser(authentication);
        Resource resource = resourceService.addResource(resourceDto);

        userService.addResourceToUser(user.getId(), resource.getId());

        redirectAttributes.addFlashAttribute("success", "Resource added successfully!");
        return "redirect:/resources";
    }

    @PostMapping("/{id}/delete")
    public String deleteResource(@PathVariable("id") long id,
                                 Authentication authentication,
                                 RedirectAttributes redirectAttributes) {
        User user = currentUserService.getCurrentUser(authentication);

        userService.removeResourceFromUser(user.getId(), id);

        redirectAttributes.addFlashAttribute("success", "Resource removed successfully!");
        return "redirect:/resources";
    }

    @PostMapping("/refresh")
    public String refreshResources(Authentication authentication,
                                   RedirectAttributes redirectAttributes) {
        User user = currentUserService.getCurrentUser(authentication);
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