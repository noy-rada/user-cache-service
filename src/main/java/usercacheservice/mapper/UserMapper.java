package usercacheservice.mapper;

import usercacheservice.domain.User;
import usercacheservice.dto.UserDto;

public class UserMapper {

    private UserMapper() {}

    public static UserDto.Response toResponse(User user) {
        return new UserDto.Response(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
