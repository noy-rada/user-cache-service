package usercacheservice.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Caching;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import usercacheservice.domain.User;
import usercacheservice.dto.UserDto;
import usercacheservice.exception.ConflictException;
import usercacheservice.exception.ResourceNotFoundException;
import usercacheservice.mapper.UserMapper;
import usercacheservice.repository.UserRepository;
import usercacheservice.service.UserService;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final CacheManager cacheManager;

    @Override
    @Transactional
    @Caching(put = {
            @CachePut(value = "users",     key = "#result.id()"),
            @CachePut(value = "users-by-username", key = "#result.username()")
    })
    public UserDto.Response createUser(UserDto.CreateRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new ConflictException("Username already exists: %s".formatted(request.username()));
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new ConflictException("Email already exists: %s".formatted(request.email()));
        }

        var user = User.builder()
                .username(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .build();

        var savedUser = userRepository.save(user);
        log.info("Created user with id={}", savedUser.getId());

        return UserMapper.toResponse(savedUser);
    }

    @Override
    public UserDto.Response getUserById(UUID id) {
        log.info("Cache miss - fetching user with id={} from DB", id);

        return userRepository.findById(id)
                .map(UserMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: %s".formatted(id)));
    }

    @Override
    public UserDto.Response getUserByUsername(String username) {
        log.info("Cache miss - fetching user by username={} from DB", username);

        return userRepository.findByUsername(username)
                .map(UserMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: %s".formatted(username)));
    }

    @Override
    public List<UserDto.Response> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public UserDto.Response updateUser(UUID id, UserDto.UpdateRequest updateRequest) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: %s".formatted(id)));

        var previousUsername = user.getUsername();

        if (updateRequest.username() != null && !updateRequest.username().equals(user.getUsername())) {
            if (userRepository.existsByUsernameAndIdNot(updateRequest.username(), id)) {
                throw new ConflictException("Username already exists: %s".formatted(updateRequest.username()));
            }
            user.setUsername(updateRequest.username());
        }

        if (updateRequest.email() != null && !updateRequest.email().equals(user.getEmail())) {
            if (userRepository.existsByEmailAndIdNot(updateRequest.email(), id)) {
                throw new ConflictException("Email already exists: %s".formatted(updateRequest.email()));
            }
            user.setEmail(updateRequest.email());
        }

        if (updateRequest.password() != null) {
            user.setPassword(passwordEncoder.encode(updateRequest.password()));
        }

        var updatedUser = userRepository.save(user);
        var response = UserMapper.toResponse(updatedUser);

        var usersCache = cacheManager.getCache("users");
        if (usersCache != null) {
            usersCache.put(response.id(), response);
        }

        var usersByUsernameCache = cacheManager.getCache("users-by-username");
        if (usersByUsernameCache != null) {
            if (!previousUsername.equals(response.username())) {
                usersByUsernameCache.evict(previousUsername);
            }
            usersByUsernameCache.put(response.username(), response);
        }

        log.info("Updated user with id={}", id);
        return response;
    }

    @Override
    public void deleteUser(UUID id) {

    }
}
