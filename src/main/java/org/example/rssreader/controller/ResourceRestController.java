package org.example.rssreader.controller;

import jakarta.validation.Valid;
import org.example.rssreader.dto.ResourceRequestDto;
import org.example.rssreader.dto.ResourceResponseDto;
import org.example.rssreader.model.Resource;
import org.example.rssreader.service.ResourceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/resources")
public class ResourceRestController {

    private final ResourceService resourceService;

    public ResourceRestController(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @GetMapping
    public List<ResourceResponseDto> getResources(@AuthenticationPrincipal(expression = "id") long userId) {
        return resourceService.getUserResources(userId)
                .stream()
                .map(ResourceResponseDto::from)
                .toList();
    }

    @PostMapping
    public ResponseEntity<?> createResource(@Valid @RequestBody ResourceRequestDto request,
                                            BindingResult bindingResult,
                                            @AuthenticationPrincipal(expression = "id") long userId) {
        if (bindingResult.hasErrors()) {
            return validationErrorResponse(bindingResult);
        }

        Resource resource = resourceService.createResourceForUser(
                userId,
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
                                            @AuthenticationPrincipal(expression = "id") long userId) {
        if (bindingResult.hasErrors()) {
            return validationErrorResponse(bindingResult);
        }

        return resourceService.updateUserResource(
                        userId,
                        id,
                        request.title(),
                        request.link()
                )
                .map(resource -> ResponseEntity.ok(ResourceResponseDto.from(resource)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteResource(@PathVariable("id") long id,
                                               @AuthenticationPrincipal(expression = "id") long userId) {
        boolean deleted = resourceService.deleteUserResource(userId, id);

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
