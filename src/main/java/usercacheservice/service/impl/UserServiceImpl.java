package usercacheservice.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
        return null;
    }

    @Override
    public List<UserDto.Response> getAllUsers() {
        return List.of();
    }

    @Override
    public UserDto.Response updateUser(UUID id, UserDto.UpdateRequest updateRequest) {
        return null;
    }

    @Override
    public void deleteUser(UUID id) {

    }
}
