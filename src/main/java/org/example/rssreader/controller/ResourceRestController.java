package org.example.rssreader.controller;

import jakarta.validation.Valid;
import org.example.rssreader.dto.ResourceRequestDto;
import org.example.rssreader.dto.ResourceResponseDto;
import org.example.rssreader.model.Resource;
import org.example.rssreader.model.User;
import org.example.rssreader.service.CurrentUserService;
import org.example.rssreader.service.ResourceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/resources")
public class ResourceRestController {

    private final ResourceService resourceService;
    private final CurrentUserService currentUserService;

    public ResourceRestController(ResourceService resourceService,
                                  CurrentUserService currentUserService) {
        this.resourceService = resourceService;
        this.currentUserService = currentUserService;
    }

    @GetMapping
    public List<ResourceResponseDto> getResources(Authentication authentication) {
        User user = currentUserService.getCurrentUser(authentication);

        return resourceService.getUserResources(user.getId())
                .stream()
                .map(ResourceResponseDto::from)
                .toList();
    }

    @PostMapping
    public ResponseEntity<?> createResource(@Valid @RequestBody ResourceRequestDto request,
                                            BindingResult bindingResult,
                                            Authentication authentication) {
        if (bindingResult.hasErrors()) {
            return validationErrorResponse(bindingResult);
        }

        User user = currentUserService.getCurrentUser(authentication);

        Resource resource = resourceService.createResourceForUser(
                user.getId(),
                request.title(),
                request.link()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ResourceResponseDto.from(resource));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateResource(@PathVariable("id") long id,
                                            @Valid @RequestBody ResourceRequestDto request,
                                            BindingResult bindingResult,
                                            Authentication authentication) {
        if (bindingResult.hasErrors()) {
            return validationErrorResponse(bindingResult);
        }

        User user = currentUserService.getCurrentUser(authentication);

        return resourceService.updateUserResource(
                        user.getId(),
                        id,
                        request.title(),
                        request.link()
                )
                .map(resource -> ResponseEntity.ok(ResourceResponseDto.from(resource)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteResource(@PathVariable("id") long id,
                                               Authentication authentication) {
        User user = currentUserService.getCurrentUser(authentication);

        boolean deleted = resourceService.deleteUserResource(user.getId(), id);

        if (!deleted) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }

    private ResponseEntity<Map<String, Object>> validationErrorResponse(BindingResult bindingResult) {
        List<String> errors = bindingResult.getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();

        return ResponseEntity
                .badRequest()
                .body(Map.of("errors", errors));
    }
}