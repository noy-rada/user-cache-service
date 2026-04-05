package usercacheservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.UUID;

public sealed interface UserDto permits
        UserDto.CreateRequest,
        UserDto.UpdateRequest,
        UserDto.Response {

    record CreateRequest(
            @NotBlank(message = "Username is required")
            @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
            String username,

            @NotBlank(message = "Email is required")
            @Email(message = "Email must be valid")
            String email,

            @NotBlank(message = "Password is required")
            @Size(min = 6, message = "Password must be at least 6 characters")
            String password
    ) implements UserDto { }

    record UpdateRequest(
            @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
            String username,

            @Size(message = "Email must be valid")
            String email,

            @Size(min = 6, message = "Password must be at least 6 characters")
            String password
    ) implements UserDto {}

    record Response(
            UUID id,
            String username,
            String email,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) implements UserDto {}

}