package com.ecommerce.platform.controller;

import com.ecommerce.platform.dto.response.UserResponse;
import com.ecommerce.platform.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/sync")
    public ResponseEntity<UserResponse> syncCurrentUser() {

        return ResponseEntity.ok(
                userService.syncCurrentUser()
        );
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser() {

        return ResponseEntity.ok(
                userService.getCurrentUser()
        );
    }

    @GetMapping
    public ResponseEntity<Page<UserResponse>> getAll(
            Pageable pageable
    ) {

        return ResponseEntity.ok(
                userService.getAll(pageable)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getById(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                userService.getById(id)
        );
    }
}