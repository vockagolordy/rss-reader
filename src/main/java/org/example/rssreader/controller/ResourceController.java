package org.example.rssreader.controller;

import org.example.rssreader.dto.ResourceDto;
import org.example.rssreader.model.Resource;
import org.example.rssreader.service.ResourceService;
import org.example.rssreader.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    @Autowired
    public ResourceController(ResourceService resourceService,
                              UserService userService) {
        this.resourceService = resourceService;
        this.userService = userService;
    }

    @GetMapping
    public String listResources(@AuthenticationPrincipal(expression = "id") long userId,
                                Model model) {
        List<Resource> resources = resourceService.getUserResources(userId);

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
                              @AuthenticationPrincipal(expression = "id") long userId,
                              RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "add-resource";
        }

        Resource resource = resourceService.addResource(resourceDto);

        userService.addResourceToUser(userId, resource.getId());

        redirectAttributes.addFlashAttribute("success", "Resource added successfully!");
        return "redirect:/resources";
    }

    @PostMapping("/{id}/delete")
    public String deleteResource(@PathVariable("id") long id,
                                 @AuthenticationPrincipal(expression = "id") long userId,
                                 RedirectAttributes redirectAttributes) {
        userService.removeResourceFromUser(userId, id);

        redirectAttributes.addFlashAttribute("success", "Resource removed successfully!");
        return "redirect:/resources";
    }

    @PostMapping("/refresh")
    public String refreshResources(@AuthenticationPrincipal(expression = "id") long userId,
                                   RedirectAttributes redirectAttributes) {
        List<Resource> resources = resourceService.getUserResources(userId);

        int totalNewPosts = 0;

        for (Resource resource : resources) {
            totalNewPosts += resourceService.updateResourcePosts(resource.getId());
        }

        redirectAttributes.addFlashAttribute("success",
                String.format("Refreshed successfully! Found %d new posts.", totalNewPosts));

        return "redirect:/resources";
    }
}
