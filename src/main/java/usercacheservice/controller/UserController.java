package usercacheservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import usercacheservice.dto.UserDto;
import usercacheservice.service.UserService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "APIs for managing users with Redis write-through caching")
public class UserController {

    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new user", description = "Creates user in PostgreSQL and writes to Redis cache")
    public UserDto.Response createUser(@Valid @RequestBody UserDto.CreateRequest request) {
        return userService.createUser(request);
    }

    // get user by ID
    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", description = "Returns from Redis cache if available")
    public UserDto.Response getUserById(@PathVariable UUID id) {
        return userService.getUserById(id);
    }

    // Get user by username
    @GetMapping("/username/{username}")
    @Operation(summary = "Get user by username")
    public UserDto.Response getUserByUsername(@PathVariable String username) {
        return userService.getUserByUsername(username);
    }

    // Get all user
    @GetMapping
    @Operation(summary = "Get all users")
    public List<UserDto.Response> getAllUsers() {
        return userService.getAllUsers();
    }


}
