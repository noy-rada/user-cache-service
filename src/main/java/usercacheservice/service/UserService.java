package usercacheservice.service;

import usercacheservice.dto.UserDto;

import java.util.List;
import java.util.UUID;

public interface UserService {

    UserDto.Response createUser(UserDto.CreateRequest createRequest);

    UserDto.Response getUserById(UUID id);

    UserDto.Response getUserByUsername(String username);

    List<UserDto.Response> getAllUsers();

    UserDto.Response updateUser(UUID id, UserDto.UpdateRequest updateRequest);

    void deleteUser(UUID id);
}
