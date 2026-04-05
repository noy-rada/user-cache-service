package usercacheservice.service.impl;

import usercacheservice.dto.UserDto;
import usercacheservice.service.UserService;

import java.util.List;
import java.util.UUID;

public class UserServiceImpl implements UserService {
    @Override
    public UserDto.Response createUser(UserDto.CreateRequest createRequest) {
        return null;
    }

    @Override
    public UserDto.Response getUserById(UUID id) {
        return null;
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
